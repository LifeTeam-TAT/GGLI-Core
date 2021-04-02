package org.ace.insurance.web.manage.life.inform;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.document.life.ShortEndowUnderwritingDOCFactory;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeProposalInformActionBean")
public class LifeProposalInformActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}
	
	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}


	private User user;
	private LifeProposal lifeProposal;
	private BancaassuranceProposal bancaassuranceProposal;
	private boolean approvedProposal;
	private boolean isAllowedPrint;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isPublicTermLife;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isSinglePremiumCreditLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isSimpleLife;
	private boolean isShortEndowLife;
	private boolean isSnakeBite;
	private boolean isPublicLife;
	private boolean isGroupLife;
	private boolean isSportMan;
	private String remark;
	private String keyFactor;
	private User responsiblePerson;
	private AcceptedInfo acceptedInfo;
	// for reject letter
	private final String reportName = "acceptanceLetter";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName = null;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		if (recalculatePremium(CONFIRMATION)) {
			lifeProposalService.calculatePremium(lifeProposal);
		}
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
		isPersonalAccident = (KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product));
		isFarmer = KeyFactorChecker.isFarmer(product);
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		isSnakeBite = KeyFactorChecker.isSnakeBite(product);
		isPublicLife = KeyFactorChecker.isPublicLife(product);
		isGroupLife = KeyFactorChecker.isGroupLife(product);
		isSportMan = KeyFactorChecker.isSportMan(product);
		acceptedInfo = new AcceptedInfo();
		acceptedInfo.setReferenceNo(lifeProposal.getId());
		if (isPersonalAccident) {
			acceptedInfo.setReferenceType(ReferenceType.PA_PROPOSAL);
		} else if (isFarmer) {
			acceptedInfo.setReferenceType(ReferenceType.FARMER_PROPOSAL);
		} else if (isPublicTermLife) {
			acceptedInfo.setReferenceType(ReferenceType.PUBLIC_TERM_LIFE_PROPOSAL);

		} else if (isShortEndowLife) {
			acceptedInfo.setReferenceType(ReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL);
		} else if (isSinglePremiumEndowmentLife) {
			acceptedInfo.setReferenceType(ReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL);
		} else if (isSinglePremiumCreditLife) {
			acceptedInfo.setReferenceType(ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL);
		} else if (isShortTermSinglePremiumCreditLife) {
			acceptedInfo.setReferenceType(ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL);
		} else if (isSimpleLife) {
			acceptedInfo.setReferenceType(ReferenceType.SIMPLE_LIFE_PROPOSAL);
		} else {
			acceptedInfo.setReferenceType(ReferenceType.LIFE_PROPOSAL);
		}

		acceptedInfo.setBasicPremium(lifeProposal.getApprovedPremium());
		acceptedInfo.setAddOnPremium(lifeProposal.getApprovedAddOnPremium());
		acceptedInfo.setPaymentType(lifeProposal.getPaymentType());
		approvedProposal = true;
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (!pv.isApproved()) {
				approvedProposal = false;
				break;
			}
		}
		if(isSimpleLife) {
		for (InsuredPersonKeyFactorValue vehKF : lifeProposal.getProposalInsuredPersonList().get(0).getKeyFactorValueList()) {
			if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {
				keyFactor = vehKF.getValue();
			}
			
		}
	}
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void selectUser() {
		WorkflowTask workflowTask = WorkflowTask.CONFIRMATION;
		WorkFlowType workFlowType = isPersonalAccident ? WorkFlowType.PERSONAL_ACCIDENT
				: isFarmer ? WorkFlowType.FARMER
						: isPublicTermLife ? WorkFlowType.PUBLIC_TERM_LIFE
								: isSinglePremiumEndowmentLife ? WorkFlowType.SINGLE_PREMIUM_ENDOWMENT_LIFE
										: isSinglePremiumCreditLife ? WorkFlowType.SINGLE_PREMIUM_CREDIT_LIFE
												: isShortTermSinglePremiumCreditLife ? WorkFlowType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE
														: isSimpleLife ? WorkFlowType.SIMPLE_LIFE
														: isShortEndowLife ? WorkFlowType.SHORT_ENDOWMENT : WorkFlowType.LIFE;
		selectUser(workflowTask, workFlowType);
	}

	public AcceptedInfo getAcceptedInfo() {
		return acceptedInfo;
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}
	
	public String getKeyFactor() {
		return keyFactor;
	}

	public boolean getIsAllowedPrint() {
		return isAllowedPrint;
	}

	public void informApprovedLifeProposal() {
		try {
			WorkflowTask workflowTask = WorkflowTask.CONFIRMATION;
			WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
					: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
							: isPublicTermLife ? WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL
									: isSinglePremiumEndowmentLife ? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
											: isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
													: isShortTermSinglePremiumCreditLife ? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
															: isSimpleLife ? WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL
															: isShortEndowLife ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
			lifeProposalService.informProposal(lifeProposal, workFlowDTO, acceptedInfo, RequestStatus.APPROVED.name());
			addInfoMessage(null, MessageId.INFORM_PROCESS_SUCCESS, lifeProposal.getProposalNo());
			isAllowedPrint = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void informRejectLifeProposal() {
		try {
			WorkflowTask workflowTask = WorkflowTask.CONFIRMATION;

			WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
					: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
							: isPublicTermLife ? WorkflowReferenceType.PUBLIC_TERM_LIFE_PROPOSAL
									: isSinglePremiumEndowmentLife ? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
											: isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
													: isShortTermSinglePremiumCreditLife ? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
															: isSimpleLife ? WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL
															: isShortEndowLife ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;

			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);

			lifeProposalService.informProposal(lifeProposal, workFlowDTO, null, RequestStatus.REJECTED.name());

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.INFORM_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());

			isAllowedPrint = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public Date getCurrentDate() {
		return new Date();
	}

	public boolean isVisibleEndorseNetPremium() {
		if (lifeProposal.getLifePolicy() != null && lifeProposal.getEndorsementNetPremium() > 0) {
			return true;
		}
		return false;
	}

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		String customerName = lifeProposal.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		if (approvedProposal) {
			if (isPersonalAccident) {
				fileName = "PersonalAccident_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generatePersonalAccidentAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isFarmer) {
				fileName = "Farmer_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateFarmerAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isPublicTermLife) {
				fileName = "PublicTermLife_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generatePublicTermLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isSinglePremiumEndowmentLife) {
				fileName = "SinglePremiumEndowmentLife" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateSinglePremiumEndowmentLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isSinglePremiumCreditLife) {
				fileName = "SinglePremiumCreditLife" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateSinglePremiumCreditLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isShortTermSinglePremiumCreditLife) { 
				fileName = "ShortTermSinglePremiumCreditLif" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateShortTermSinglePremiumCreditLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			}  else if (isSimpleLife) { 
				fileName = "SimpleLife" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateSimpleLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isShortEndowLife) {
				fileName = "ShortEndowLife_" + customerName + "_Inform" + ".pdf";
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isPublicLife || isSnakeBite || isGroupLife || isSportMan) {
				fileName = (isPublicLife ? "PublicLife_" : isSnakeBite ? "SnakeBite_" : isGroupLife ? "GroupLife_" : "SportMan_") + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			}
		} else {
			if (isPersonalAccident) {
				fileName = "PersonalAccident_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generatePersonalAccidentRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isFarmer) {
				fileName = "Farmer_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateFarmerRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);

			} else if (isPublicTermLife) {
				fileName = "PublicTermLife_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generatePublicTermLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);

			} else if (isSinglePremiumEndowmentLife) {
				fileName = "SinglePremiumEndowmentLife_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateSinglePremiumEndowmentLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);

			} else if (isSinglePremiumCreditLife) {
				fileName = "SinglePremiumCreditLife_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateSinglePremiumCreditLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);

			} else if (isShortEndowLife) {
				fileName = "ShortEndowLife_" + customerName + "_Inform" + ".pdf";
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			} else if (isPublicLife || isSnakeBite || isGroupLife || isSportMan) {
				fileName = isPublicLife ? "PublicLife_" : isSnakeBite ? "SnakeBite_" : isGroupLife ? "GroupLife_" : "SportMan_" + customerName + "_Inform" + ".pdf";
				DocumentBuilder.generateLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
			}
		}
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double netAmount() {
		double netAmount = 0.0;

		double discountPercent = acceptedInfo.getDiscountPercent();

		double totalPremium = acceptedInfo.getTotalPremium();
		double discountAmount = (totalPremium / 100) * discountPercent;

		double serviceCharges = acceptedInfo.getServicesCharges();

		netAmount = (totalPremium - discountAmount) + serviceCharges;

		return netAmount;
	}

	public boolean getIsShortEndowLife() {
		return isShortEndowLife;
	}
	
	public boolean getIsSimpleLife() {
		return isSimpleLife;
	}
	
	public String getPageHeader() {
		return (isFarmer ? "Farmer"
				: isPublicTermLife ? "PublicTermLife"
						: isSinglePremiumEndowmentLife ? "Single Premium Endowment Life"
								: isSinglePremiumCreditLife ? "Single Premium Credit Life"
										: isShortEndowLife ? "Short Term Endowment Life" : isPersonalAccident ? "Personal Accident" : "Life")
				+ " Proposal Inform";
	}
}
