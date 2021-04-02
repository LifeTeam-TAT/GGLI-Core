package org.ace.insurance.web.manage.life.issue;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.ListSplitor;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.migrate.service.interfaces.IShortEndowmentExtraValueService;
import org.ace.insurance.life.policy.EndorsementLifePolicyPrint;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.PolicyInsuredPersonBeneficiariesHistory;
import org.ace.insurance.life.policyHistory.PolicyInsuredPersonHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
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
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "LifePolicyIssueActionBean")
public class LifePolicyIssueActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{ShortEndowmentExtraValueService}")
	private IShortEndowmentExtraValueService shortEndowmentExtraValueService;

	public void setShortEndowmentExtraValueService(IShortEndowmentExtraValueService shortEndowmentExtraValueService) {
		this.shortEndowmentExtraValueService = shortEndowmentExtraValueService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

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

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	@ManagedProperty(value = "#{LifeEndorsementService}")
	private ILifeEndorsementService lifeEndorsementService;

	public void setLifeEndorsementService(ILifeEndorsementService lifeEndorsementService) {
		this.lifeEndorsementService = lifeEndorsementService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private BancaassuranceProposal bancaassuranceProposal;
	private LifeEndorseInfo lifeEndorseInfo;

	private List<LifePolicyHistory> lifePolicyHistoryList;
	private Date todaydate = new Date();
	private LifePolicyHistory lifePolicyHistory;
	private List<LifePolicy> lifePolicyList;
	private List<Payment> paymentList;
	private LifePolicy lifePolicy;
	private PaymentDTO paymentDTO;
	private EndorsementLifePolicyPrint endorsementLifePolicyPrint;
	private boolean nilExcess;
	private boolean showPreview;
	private boolean disableIssueBtn;
	private boolean disableSetUPBtn;
	private String AddDispline;
	private String Reasons;
	private String Changes;
	private String keyFactor;
	private boolean isEndorsement = false;
	private boolean isPublicLife = false;
	private boolean isGroupLife = false;
	private boolean isPersonalAccident = false;
	private boolean isFarmer = false;
	private boolean isPublicTermLife = false;
	private boolean isSinglePremiumEndowmentLife = false;
	private boolean isSinglePremiumCreditLife = false;
	private boolean isShortTermSinglePremiumCreditLife = false;
	private boolean isShortEndowLife;
	private boolean isSportMan;
	private boolean isSimpleLife;

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
		lifePolicyList = new ArrayList<LifePolicy>();
		disableSetUPBtn = true;
		isEndorsement = lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT);
		lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposal.getId());
		lifePolicyList.add(lifePolicy);
		bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
		Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
		isPublicLife = KeyFactorChecker.isPublicLife(product);
		isGroupLife = KeyFactorChecker.isGroupLife(product);
		isPersonalAccident = KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product);
		isFarmer = KeyFactorChecker.isFarmer(product);
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		isSportMan = KeyFactorChecker.isSportMan(product);
		isSimpleLife = KeyFactorChecker.isSimpleLife(product);

		PolicyReferenceType policyReferenceType = isShortEndowLife ? PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY
				: isSinglePremiumCreditLife ? PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY
						: isSinglePremiumEndowmentLife ? PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY
								: isShortTermSinglePremiumCreditLife ? PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY
										: isSimpleLife ? PolicyReferenceType.SIMPLE_LIFE_POLICY : PolicyReferenceType.LIFE_POLICY;
		paymentList = paymentService.findByProposal(lifeProposal.getId(), policyReferenceType, true);
		paymentDTO = new PaymentDTO(paymentList);
		if (lifeProposal.getComplete()) {
			this.disableIssueBtn = true;
		}
		if (isEndorsement) {
			lifePolicyHistoryList = lifePolicyHistoryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
			lifePolicyHistory = EndorsementUtil.getLatestPolicyHistory(lifePolicyHistoryList);
			lifeEndorseInfo = lifeEndorsementService.findLastLifeEndorseInfoByProposalNo(lifeProposal.getLifePolicy().getPolicyNo());
		}
		
		if(isSimpleLife) {
		for (InsuredPersonKeyFactorValue vehKF : lifeProposal.getProposalInsuredPersonList().get(0).getKeyFactorValueList()) {
			if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {
				keyFactor = vehKF.getValue();
			}
			
		}
	}
	}

	public void preparePolicyIssue(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
		List<EndorsementLifePolicyPrint> list = lifePolicyService.findEndorsementPolicyPrintByLifePolicyNo(lifePolicy.getPolicyNo());
		if (list.size() > 0) {
			this.endorsementLifePolicyPrint = list.get(list.size() - 1);
		} else {
			this.endorsementLifePolicyPrint = new EndorsementLifePolicyPrint();
		}
	}

	public void changeNillExcessValue(LifePolicy lifePolicy) {
		lifePolicy.setNilExcess(nilExcess);
	}

	public int getYear() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		return year;
	}

	public int getMonth() {
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH);
		return month + 1;
	}

	public int getDay() {
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	public void issuePolicy() {
		try {
			if (isEndorsement) {
				lifeEndorsementService.issueLifeProposal(lifeProposal);
			} else {
				lifeProposalService.issueLifeProposal(lifeProposal);
			}
			lifeProposal = lifeProposalService.findLifeProposalById(lifeProposal.getId());
			this.showPreview = true;
			this.disableIssueBtn = true;
			addInfoMessage(null, MessageId.ISSUING_PROCESS_SUCCESS_PARAM, lifeProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	private final String reportName = "LifePolicyIssue";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport(LifePolicy lifePolicy) {
		String customerName = lifePolicy.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		if (isPublicLife) {
			fileName = "PublicLife_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generatePublicLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} else if (isGroupLife) {
			fileName = "GroupLife_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generateGroupLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} else if (isPersonalAccident) {
			fileName = "PersonalAccident_" + customerName + "_Issue" + ".pdf";
			if (lifePolicy.getPolicyInsuredPersonList().size() > 1) {
				DocumentBuilder.generatePAGroupPolicy(lifePolicy, paymentDTO, dirPath, fileName);
			} else {
				DocumentBuilder.generatePAPolicy(lifePolicy, paymentDTO, dirPath, fileName);
			}
		} else if (isFarmer) {
			fileName = "Farmer_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generateFarmerPolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} else if (isPublicTermLife) {
			fileName = "PublicTermLife_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generatePublicTermLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} else if (isSinglePremiumEndowmentLife) {
			fileName = "SinglePremiumEndowmentLife_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generateSinglePremiumEndowmentLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} else if (isSinglePremiumCreditLife) {
			fileName = "SinglePremiumCreditLife_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generateSinglePremiumCreditLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} else if (isShortTermSinglePremiumCreditLife) {
			fileName = "ShortTermSinglePremiumCreditLife_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generateShortTermSinglePremiumCreditLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} else if (isSimpleLife) {
			fileName = "SimpleLife_" + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generateSimpleLifePolicy(lifePolicy, paymentDTO, dirPath, fileName);
		} 
//		else if (isSimpleLife && lifePolicy.getOrganization() != null){ 
//			fileName ="SimpleLife_" + customerName + "_Issue" + ".pdf";
//			DocumentBuilder.generateSimpleLifePolicyforOrganization(lifePolicy, paymentDTO, dirPath, fileName); 
//		}
		else if (isShortEndowLife) {
			fileName = "ShortEndowLife_" + customerName + "_Issue" + ".pdf";
			if (lifeProposal.isMigrate()) {
				double extraPremium = shortEndowmentExtraValueService.findExtraAmount(lifePolicy.getId());
				ShortEndowUnderwritingDOCFactory.generateMigrateShortEndowLifePolicy(lifePolicy, dirPath, fileName, paymentList, extraPremium);
			} else
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifePolicy(lifePolicy, dirPath, fileName);
		} else {
			fileName = (isSportMan ? "SportMan_" : "SnakeBite_") + customerName + "_Issue" + ".pdf";
			DocumentBuilder.generateSportManPolicy(lifePolicy, paymentDTO, dirPath, fileName);
		}
	}

	public void increasePrintCount() {
		lifePolicyService.increaseLifePolicyPrintCount(lifePolicy.getId());
		lifePolicy.setPrintCount(lifePolicy.getPrintCount() + 1);
	}

	public Date getPresentDate() {
		return new Date();
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public List<LifePolicy> getLifePolicyList() {
		return lifePolicyList;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public boolean getNilExcess() {
		return nilExcess;
	}

	public void setNilExcess(boolean nilExcess) {
		this.nilExcess = nilExcess;
	}

	public boolean getShowPreview() {
		return showPreview;
	}

	public boolean getDisableIssueBtn() {
		return disableIssueBtn;
	}

	public LifePolicyHistory getLifePolicyHistory() {
		return lifePolicyHistory;
	}

	public Date getTodaydate() {
		return todaydate;
	}

	public void setTodaydate(Date todaydate) {
		this.todaydate = todaydate;
	}

	public String getAddDispline() {
		return AddDispline;
	}

	public void setAddDispline(String addDispline) {
		AddDispline = addDispline;
	}

	public String getReasons() {
		return Reasons;
	}

	public void setReasons(String reasons) {
		Reasons = reasons;
	}

	public String getChanges() {
		return Changes;
	}

	public void setChanges(String changes) {
		Changes = changes;
	}

	public boolean isIsEndorsement() {
		return isEndorsement;
	}

	public void setIsEndorsement(boolean isEndorsement) {
		this.isEndorsement = isEndorsement;
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public LifeEndorseInfo getLifeEndorseInfo() {
		return lifeEndorseInfo;
	}

	public boolean isChangeOthers() {
		if (lifeProposal.getLifePolicy() != null) {
			PolicyInsuredPersonHistory insuPersonHistory;
			if (lifeProposal.getLifePolicy().getInsuredPersonInfo().size() != lifePolicyHistory.getInsuredPersonInfo().size()) {
				return true;
			}
			if (lifeProposal.getLifePolicy().getPolicyStatus() == PolicyStatus.TERMINATE) {
				return true;
			}
			if (!lifeProposal.getLifePolicy().getPaymentType().getId().equalsIgnoreCase(lifePolicyHistory.getPaymentType().getId())) {
				return true;
			}
			for (PolicyInsuredPerson insuPerson : lifeProposal.getLifePolicy().getInsuredPersonInfo()) {
				if (insuPerson.getEndorsementStatus() == EndorsementStatus.NEW) {
					return true;
				}
				if (insuPerson.getEndorsementStatus() == EndorsementStatus.REPLACE) {
					return true;
				}
				if (insuPerson.getEndorsementStatus() == EndorsementStatus.EDIT) {
					insuPersonHistory = lifePolicyHistoryService.findInsuredPersonByCodeNo(insuPerson.getInsPersonCodeNo(), lifePolicyHistory.getId());
					if (!insuPerson.getFullName().equalsIgnoreCase(insuPersonHistory.getFullName())) {
						return true;
					} else if (!insuPerson.getResidentAddress().getResidentAddress().equalsIgnoreCase(insuPersonHistory.getResidentAddress().getResidentAddress())) {
						return true;
					} else if (insuPerson.getOccupation() != null && !insuPerson.getOccupation().getId().equalsIgnoreCase(insuPersonHistory.getOccupation().getId())) {
						return true;
					} else if (insuPerson.getPeriodMonth() != insuPersonHistory.getPeriodMonth()) {
						return true;
					} else if (insuPerson.getSumInsured() != insuPersonHistory.getSumInsured()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isChangeBeneficiary() {
		PolicyInsuredPersonHistory insuPersonHistory;
		PolicyInsuredPersonBeneficiariesHistory beneficiaryHistory;
		if (lifeProposal.getLifePolicy() != null && lifeProposal.getLifePolicy().getPolicyStatus() != PolicyStatus.TERMINATE) {
			for (PolicyInsuredPerson insuPerson : lifeProposal.getLifePolicy().getInsuredPersonInfo()) {
				if (insuPerson.getEndorsementStatus() == EndorsementStatus.EDIT) {
					insuPersonHistory = lifePolicyHistoryService.findInsuredPersonByCodeNo(insuPerson.getInsPersonCodeNo(), lifePolicyHistory.getId());
					if (insuPerson.getPolicyInsuredPersonBeneficiariesList().size() != insuPersonHistory.getPolicyInsuredPersonBeneficiariesList().size()) {
						return true;
					}
					for (PolicyInsuredPersonBeneficiaries beneficiary : insuPerson.getPolicyInsuredPersonBeneficiariesList()) {
						beneficiaryHistory = lifePolicyHistoryService.findBeneficiaryByCodeNo(beneficiary.getBeneficiaryNo(), insuPersonHistory.getId());
						if (beneficiaryHistory == null) {
							return true;
						}
						if (!beneficiary.getName().equals(beneficiaryHistory.getName())) {
							return true;
						} else if (beneficiary.getAge() != beneficiaryHistory.getAge()) {
							return true;
						} else if (beneficiary.getPercentage() != beneficiaryHistory.getPercentage()) {
							return true;
						} else if (beneficiary.getRelationship() == null) {
							if (beneficiaryHistory.getRelationship() != null) {
								return true;
							}
						} else if (beneficiary.getRelationship() != null) {
							if (beneficiaryHistory.getRelationship() == null) {
								return true;
							} else if (!beneficiary.getRelationship().equals(beneficiaryHistory.getRelationship())) {
								return true;
							}
						} else if (!beneficiary.getResidentAddress().equals(beneficiaryHistory.getResidentAddress())) {
							return true;
						} else if (!beneficiary.getIdType().equals(beneficiaryHistory.getIdType())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public String endorseChangeDescriptionForPublic() {
		String desc = null;
		String fontStart = "<font style=\"font-size:13px;\">";
		String fontEnd = "</font>";
		desc = "<table border=\"0\" style=\"margin-top: -3px;font-size:13px;font-family:'Arial'\" cellspacing=\"0\" cellpadding=\"5\">";
		if (isEndorsement && lifeProposal.getLifePolicy() != null) {
			int i = 0;
			for (PolicyInsuredPerson insuPerson : lifeProposal.getLifePolicy().getInsuredPersonInfo()) {
				PolicyInsuredPersonHistory insuPersonHistory = new PolicyInsuredPersonHistory();
				if (lifePolicyHistory != null) {
					insuPersonHistory = lifePolicyHistoryService.findInsuredPersonByCodeNo(insuPerson.getInsPersonCodeNo(), lifePolicyHistory.getId());
				}

				if (insuPersonHistory != null) {
					if (lifeProposal.getLifePolicy().getPolicyStatus() == PolicyStatus.UPDATE) {
						if (!insuPerson.getName().equals(insuPersonHistory.getName())) {
							i = i + 1;
							desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "Customer Name " + fontEnd + "</td>" + "<td>" + fontStart
									+ "-   " + insuPersonHistory.getFullName() + fontEnd + "</td>" + "<td width=\"30\" align=\"center\">" + fontStart + "To" + fontEnd + "</td>"
									+ "<td>" + fontStart + insuPerson.getFullName() + fontEnd + "</td>" + "</tr>";
						}
						if (!insuPerson.getResidentAddress().equals(insuPersonHistory.getResidentAddress())) {
							i = i + 1;
							desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "Customer Address " + fontEnd + "</td>" + "<td>" + fontStart
									+ "-   " + insuPersonHistory.getResidentAddress().getFullResidentAddress() + fontEnd + "</td>" + "<td width=\"30\" align=\"center\">"
									+ fontStart + "To" + fontEnd + "</td>" + "<td>" + fontStart + insuPerson.getResidentAddress().getFullResidentAddress() + fontEnd + "</td>"
									+ "</tr>";
						}
						if (insuPerson.getOccupation() == null && insuPersonHistory.getOccupation() != null) {
							i = i + 1;
							desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "Occupation " + fontEnd + "</td>" + "<td>" + fontStart
									+ "-   " + insuPersonHistory.getOccupation().getName() + fontEnd + "</td>" + "<td width=\"30\" align=\"center\">" + fontStart + "To" + fontEnd
									+ "</td>" + "<td> No Occupation </td>" + "</tr>";
						} else if (insuPerson.getOccupation() != null) {
							if (!insuPerson.getOccupation().equals(insuPersonHistory.getOccupation())) {
								i = i + 1;
								desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "Occupation " + fontEnd + "</td>" + "<td>" + fontStart
										+ "-   ";
								if (insuPersonHistory.getOccupation() == null) {
									desc += "No Occupation";
								} else {
									desc += insuPersonHistory.getOccupation().getName();
								}
								desc += fontEnd + "</td>" + "<td width=\"30\" align=\"center\">" + fontStart + "To" + fontEnd + "</td>" + "<td>" + fontStart
										+ insuPerson.getOccupation().getName() + fontEnd + "</td>" + "</tr>";
							}
						}
						if (!lifeProposal.getLifePolicy().getPaymentType().equals(lifePolicyHistory.getPaymentType())) {
							i = i + 1;
							desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "Payment Type " + fontEnd + "</td>" + "<td>" + fontStart
									+ "-   " + lifePolicyHistory.getPaymentType().getName() + fontEnd + "</td>" + "<td width=\"30\" align=\"center\">" + fontStart + "To" + fontEnd
									+ "</td>" + "<td>" + fontStart + lifeProposal.getLifePolicy().getPaymentType().getName() + fontEnd + "</td>" + "</tr>";
						}
						if (insuPerson.getSumInsured() != insuPersonHistory.getSumInsured()) {
							i = i + 1;
							desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "Sum Insured " + fontEnd + "</td>" + "<td>" + fontStart
									+ "-   " + Utils.getCurrencyFormatString(insuPersonHistory.getSumInsured()) + " Kyats" + fontEnd + "</td>"
									+ "<td width=\"30\" align=\"center\">" + fontStart + "To" + fontEnd + "</td>" + "<td>" + fontStart
									+ Utils.getCurrencyFormatString(insuPerson.getSumInsured()) + " Kyats" + fontEnd + "</td>" + "</tr>";
						}
						if (insuPerson.getPeriodYears() != insuPersonHistory.getPeriodYears()) {
							i = i + 1;
							desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "Period " + fontEnd + "</td>" + "<td>" + fontStart + "-   "
									+ insuPersonHistory.getPeriodYears() + " years" + fontEnd + "</td>" + "<td width=\"30\" align=\"center\">" + fontStart + "To" + fontEnd
									+ "</td>" + "<td>" + fontStart + insuPerson.getPeriodYears() + " years" + fontEnd + "</td>" + "</tr>";
						}
					} else {
						desc += "<tr>" + "<td colspan=\"4\">" + fontStart + " This Policy is Terminated.... " + fontEnd + "</td>" + "</tr>";
					}
				} else {
					i = i + 1;
					desc += "<tr>" + "<td>" + fontStart + i + ". " + fontEnd + "</td>" + "<td>" + fontStart + "New Insured Person" + fontEnd + "</td>" + "<td>" + fontStart + " - "
							+ insuPerson.getFullName() + " , Premium - " + fontEnd + "</td>" + "<td width=\"30\" align=\"center\">" + fontStart
							+ Utils.getCurrencyFormatString(insuPerson.getPremium()) + fontEnd + "</td>" + "<td>" + fontStart + "Ks , Period - " + insuPerson.getPeriodYears()
							+ " years" + fontEnd + "</td>" + "</tr>";
				}
			}
		}
		desc += "</table>";
		return desc;
	}

	public boolean isLicenseNoNull() {
		if (lifeProposal.getAgent() != null) {
			return true;
		} else
			return false;
	}

	public List<List<PolicyInsuredPerson>> getPolicyInsuredList() {
		if (lifePolicy != null) {
			return ListSplitor.split(lifePolicy.getInsuredPersonInfo(), 8);
		}
		return null;
	}

	public EndorsementLifePolicyPrint getEndorsementLifePolicyPrint() {
		return endorsementLifePolicyPrint;
	}

	public void setEndorsementLifePolicyPrint(EndorsementLifePolicyPrint endorsementLifePolicyPrint) {
		this.endorsementLifePolicyPrint = endorsementLifePolicyPrint;
	}

	public void generatePrintSetUpLifePolicy(LifePolicy lifePolicy) {
		if (endorsementLifePolicyPrint == null) {
			endorsementLifePolicyPrint = new EndorsementLifePolicyPrint();
		}
		LifePolicyHistory lifePolicyHistory = getLifePolicyHistory(lifePolicy.getPolicyNo());
		DocumentBuilder.generatePrintSetUpLifePolicy(endorsementLifePolicyPrint, lifeProposal, lifePolicy, lifePolicyHistory, dirPath, fileName);
	}

	private LifePolicyHistory getLifePolicyHistory(String policyNo) {
		List<LifePolicyHistory> policyList = lifePolicyHistoryService.findLifePolicyHistoryByPolicyNo(policyNo);
		return EndorsementUtil.getLatestPolicyHistory(policyList);
	}

	public void saveEndorsementPolicyPrint() {
		try {
			if (validEndorsementPolicyPrint()) {
				disableSetUPBtn = false;
				if (endorsementLifePolicyPrint.getId() == null) {
					endorsementLifePolicyPrint.setLifePolicy(lifePolicy);
					lifePolicyService.addNewEndorsementLifePolicyPrint(endorsementLifePolicyPrint);
				} else {
					lifePolicyService.updateEndorsementLifePolicyPrint(endorsementLifePolicyPrint);
				}
				PrimeFaces.current().executeScript("PF('endorsementSetUpEntryDialog').hide()");
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean isDisableSetUPBtn() {
		return disableSetUPBtn;
	}

	public void setDisableSetUPBtn(boolean disableSetUPBtn) {
		this.disableSetUPBtn = disableSetUPBtn;
	}

	private boolean validEndorsementPolicyPrint() {
		boolean valid = true;
		String formID = "endorsementSetUpEntryForm";
		if (isEmpty(endorsementLifePolicyPrint.getEndorseChange())) {
			addErrorMessage(formID + ":endorseChange", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(endorsementLifePolicyPrint.getEndorseChangeDetail())) {
			addErrorMessage(formID + ":endorseChangeDetail", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* For Template */
	@ManagedProperty(value = "#{LifePolicySummaryService}")
	private ILifePolicySummaryService lifePolicySummaryService;

	public void setLifePolicySummaryService(ILifePolicySummaryService lifePolicySummaryService) {
		this.lifePolicySummaryService = lifePolicySummaryService;
	}

	public boolean isEndorse(LifeProposal lifeProposal) {
		if (lifeProposal == null) {
			return false;
		}
		return EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy());
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		if (lifePolicyHistoryList == null) {
			if (lifeProposal.getLifePolicy() != null) {
				lifePolicyHistoryList = lifePolicyHistoryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
			}
		}
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary(String policyId) {
		LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(policyId);
		return summary;
	}
	
	public boolean getIsSimpleLife() {
		return isSimpleLife;
	}

	public String getKeyFactor() {
		return keyFactor;
	}

	public String getPageHeader() {
		return (isEndorsement ? "Life Endorsement"
				: isFarmer ? "Farmer"
						: isPublicTermLife ? "PublicTermLife"
								: isSinglePremiumEndowmentLife ? "Single Premium Endowment Life"
										: isSinglePremiumCreditLife ? "Single Premium Credit Life"
												: isShortTermSinglePremiumCreditLife ? "Short Term Single Premium Credit Life"
														: isSimpleLife ? "Simple Life"
																: isPersonalAccident ? "Personal Accident" : isShortEndowLife ? "Short Term Endowment Life" : "Life")
				+ " Policy Issue";
	}
}
