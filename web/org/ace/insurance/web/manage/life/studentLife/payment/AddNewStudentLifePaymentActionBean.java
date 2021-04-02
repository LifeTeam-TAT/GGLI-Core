package org.ace.insurance.web.manage.life.studentLife.payment;

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
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.deno.service.interfaces.IDenoService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewStudentLifePaymentActionBean")
public class AddNewStudentLifePaymentActionBean extends BaseBean implements Serializable {
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

	private User user;
	private LifeProposal lifeProposal;
	private PaymentDTO paymentDTO;
	private List<Payment> paymentList;
	private String remark;
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
		paymentList = paymentService.findByProposal(lifeProposal.getId(), PolicyReferenceType.STUDENT_LIFE_POLICY, false);
		paymentDTO = new PaymentDTO(paymentList);
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposal.getId());
		lifePolicyList.add(lifePolicy);
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void selectUser() {
		WorkflowTask workflowTask = WorkflowTask.ISSUING;
		WorkFlowType workFlowType = WorkFlowType.STUDENT_LIFE;
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

	public String paymentLifeProposal() {
		String result = null;
		try {
			WorkflowTask workflowTask = WorkflowTask.ISSUING;
			WorkflowReferenceType referenceType = WorkflowReferenceType.STUDENT_LIFE_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
			lifeProposalService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, user.getBranch(), RequestStatus.FINISHED.name());
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

	public void openTemplateDialog() {
		putParam("lifeProposal", lifeProposal);
		putParam("workFlowList", getWorkFlowList());
		openStudentLifeInfoTemplate();
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getPageHeader() {
		return "Student Life Proposal Payment";
	}
}
