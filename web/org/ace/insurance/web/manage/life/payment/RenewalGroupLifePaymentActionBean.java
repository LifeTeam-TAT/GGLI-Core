package org.ace.insurance.web.manage.life.payment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifePolicyService;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.deno.service.interfaces.IDenoService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "RenewalGroupLifePaymentActionBean")
public class RenewalGroupLifePaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
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

	@ManagedProperty(value = "#{RenewalGroupLifePolicyService}")
	private IRenewalGroupLifePolicyService renewalGroupLifePolicyService;

	public void setRenewalGroupLifePolicyService(IRenewalGroupLifePolicyService renewalGroupLifePolicyService) {
		this.renewalGroupLifePolicyService = renewalGroupLifePolicyService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private PaymentDTO paymentDTO;
	private List<Payment> paymentList;
	private boolean cashPayment = true;
	private String remark;
	private User responsiblePerson;
	private List<LifePolicy> lifePolicyList;
	private List<WorkFlowHistory> workflowHistoryList;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
		workflowHistoryList = workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.LIFE_POLICY, false);
		paymentDTO = new PaymentDTO(paymentList);
		lifePolicyList = renewalGroupLifePolicyService.findLifePolicyByProposalId(lifeProposal.getId());
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void selectUser() {
		selectUser(WorkflowTask.RENEWAL_ISSUING, WorkFlowType.LIFE);
	}

	public boolean isCashPayment() {
		return cashPayment;
	}

	public EnumSet<PaymentChannel> getPaymentChannels() {
		return EnumSet.of(PaymentChannel.CASHED, PaymentChannel.CHEQUE);
	}

	public void changePaymentChannel(AjaxBehaviorEvent e) {
		PaymentChannel paymentChannel = (PaymentChannel) ((SelectOneMenu) e.getSource()).getValue();
		if (paymentChannel.equals(PaymentChannel.CHEQUE)) {
			cashPayment = false;
		} else {
			cashPayment = true;
		}
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public void setPayment(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workflowHistoryList;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String paymentLifeProposal() {
		String result = null;
		try {
			String formID = "lifePaymentForm";
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
				return null;
			}
			WorkflowTask workflowTask = null;
			workflowTask = WorkflowTask.RENEWAL_ISSUING;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, WorkflowReferenceType.LIFE_PROPOSAL, user, responsiblePerson);
			for (LifePolicy lifePolicy : lifePolicyList) {
				if (!lifePolicy.isEndorsementApplied()) {
					Date validDate = paymentDateStatus(lifePolicy);
					renewalGroupLifePolicyService.updatePaymentDate(lifePolicy.getId(), new Date(), validDate);
				}
			}
			renewalGroupLifeProposalService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, user.getBranch(), RequestStatus.FINISHED.name());
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private Date paymentDateStatus(LifePolicy lifePolicy) {
		int addMonth = 0;
		if (lifePolicy.getPaymentType().getName().equals("ANNUAL")) {
			addMonth = 12;
		} else if (lifePolicy.getPaymentType().getName().equals("SEMI-ANNUAL")) {
			addMonth = 6;
		} else if (lifePolicy.getPaymentType().getName().equals("QUARTER")) {
			addMonth = 3;
		} else if (lifePolicy.getPaymentType().getName().equals("LUMPSUM")) {
			addMonth = lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears() * 12;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, addMonth);
		Date paymentValidDate = cal.getTime();
		return paymentValidDate;
	}

	public List<LifePolicy> getLifePolicyList() {
		return lifePolicyList;
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	/* For Template */
	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

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

	public boolean isEndorse(LifeProposal lifeProposal) {
		if (lifeProposal == null) {
			return false;
		}
		return EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy());
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary(String policyId) {
		LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(policyId);
		return summary;
	}
}
