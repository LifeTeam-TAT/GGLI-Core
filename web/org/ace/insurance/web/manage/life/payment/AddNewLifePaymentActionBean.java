package org.ace.insurance.web.manage.life.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.deno.service.interfaces.IDenoService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewLifePaymentActionBean")
public class AddNewLifePaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{DenoService}")
	private IDenoService denoService;

	public void setDenoService(IDenoService denoService) {
		this.denoService = denoService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{BankService}")
	private IBankService bankService;

	public void setBankService(IBankService bankService) {
		this.bankService = bankService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
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
	private PaymentDTO paymentDTO;
	private List<Payment> paymentList;
	private boolean isEndorse;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isPublicTermLife;
	private boolean isShortEndowLife;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isSinglePremiumCreditLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isSimpleLife;
	private String remark;
	private String keyFactor;
	private User responsiblePerson;
	private List<LifePolicy> lifePolicyList;
	private LifeEndorseInfo lifeEndorseInfo;

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
		bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		isEndorse = lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT);
		isPersonalAccident = KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product);
		isFarmer = KeyFactorChecker.isFarmer(product);
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(product);
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		PolicyReferenceType referenceType = isPersonalAccident ? PolicyReferenceType.PA_POLICY
				: isFarmer ? PolicyReferenceType.FARMER_POLICY
						: isPublicTermLife ? PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY
								: isSinglePremiumEndowmentLife ? PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY
										: isSinglePremiumCreditLife ? PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY
												: isShortTermSinglePremiumCreditLife ? PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY
														: isSimpleLife ? PolicyReferenceType.SIMPLE_LIFE_POLICY
																: isShortEndowLife ? PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY : PolicyReferenceType.LIFE_POLICY;
		paymentList = paymentService.findByProposal(lifeProposal.getId(), referenceType, false);
		paymentDTO = new PaymentDTO(paymentList);
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposal.getId());
		lifePolicyList.add(lifePolicy);
		if (isEndorse && !isShortEndowLife) {
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

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void selectUser() {
		WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_ISSUING : WorkflowTask.ISSUING;
		WorkFlowType workFlowType = isPublicTermLife ? WorkFlowType.PUBLIC_TERM_LIFE
				: isSinglePremiumEndowmentLife ? WorkFlowType.SINGLE_PREMIUM_ENDOWMENT_LIFE
						: isSinglePremiumCreditLife ? WorkFlowType.SINGLE_PREMIUM_CREDIT_LIFE
								: isShortTermSinglePremiumCreditLife ? WorkFlowType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE
										: isSimpleLife ? WorkFlowType.SIMPLE_LIFE
												: isPersonalAccident ? WorkFlowType.PERSONAL_ACCIDENT
														: isFarmer ? WorkFlowType.FARMER : isShortEndowLife ? WorkFlowType.SHORT_ENDOWMENT : WorkFlowType.LIFE;
		selectUser(workflowTask, workFlowType);
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public LifeEndorseInfo getLifeEndorseInfo() {
		return lifeEndorseInfo;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public boolean getIsSimpleLife() {
		return isSimpleLife;
	}
	
	public String getKeyFactor() {
		return keyFactor;
	}

	public String paymentLifeProposal() {
		String result = null;
		try {
			WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_ISSUING : WorkflowTask.ISSUING;
			WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
					: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
							: isSinglePremiumEndowmentLife ? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
									: isSinglePremiumCreditLife ? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
											: isShortTermSinglePremiumCreditLife ? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
													: isSimpleLife ? WorkflowReferenceType.SIMPLE_LIFE_PROPOSAL
															: isShortEndowLife ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
			if (isEndorse) {
				lifeEndorsementService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, user.getBranch(), RequestStatus.FINISHED.name());
			} else if (lifeProposal.isMigrate()) {
				lifeProposalService.paymentMigrateShortTermProposal(lifeProposal, workFlowDTO, paymentList, user.getBranch(), RequestStatus.FINISHED.name());
			} else {
				lifeProposalService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, user.getBranch(), RequestStatus.FINISHED.name());
			}
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public List<LifePolicy> getLifePolicyList() {
		return lifePolicyList;
	}

	public boolean getIsEndorse() {
		return isEndorse;
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	/* Life Proposal Template */

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	@ManagedProperty(value = "#{LifePolicySummaryService}")
	private ILifePolicySummaryService lifePolicySummaryService;

	public void setLifePolicySummaryService(ILifePolicySummaryService lifePolicySummaryService) {
		this.lifePolicySummaryService = lifePolicySummaryService;
	}

	private List<LifePolicyHistory> lifePolicyHistoryList;

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		if (lifePolicyHistoryList == null) {
			if (lifeProposal.getLifePolicy() != null) {
				lifePolicyHistoryList = lifePolicyHistoryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
			}
		}
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary() {
		LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getId());
		return summary;
	}

	public String getPageHeader() {
		return (isEndorse ? "Life Endorsement"
				: isFarmer ? "Farmer"
						: isPublicTermLife ? "PublicTermLife"
								: isSinglePremiumEndowmentLife ? "Single Premium Endowment Life"
										: isSinglePremiumCreditLife ? "Single Premium Credit Life"
												: isShortTermSinglePremiumCreditLife ? "Short Term Single Premium Credit Life"
														: isSimpleLife ? "Simple Life"
																: isShortEndowLife ? "Short Term Endowment Life" : isPersonalAccident ? "Personal Accident" : "Life")
				+ " Proposal Payment";
	}
}
