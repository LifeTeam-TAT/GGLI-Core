package org.ace.insurance.web.manage.life.groupfarmer.payment;

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

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "AddNewGroupFarmerPaymentActionBean")
@ViewScoped
public class AddNewGroupFarmerPaymentActionBean extends BaseBean {
	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	private User user;
	private PaymentDTO paymentDTO;
	private boolean cashPayment = true;
	private String remark;
	private List<Payment> paymentList;
	private User responsiblePerson;
	private List<WorkFlowHistory> workflowHistoryList;
	private GroupFarmerProposal groupFarmerProposal;
	private WorkflowReferenceType referenceType;

	private PolicyReferenceType policyReferenceType;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		groupFarmerProposal = (groupFarmerProposal == null) ? (GroupFarmerProposal) getParam("groupFarmerProposal") : groupFarmerProposal;
		workflowHistoryList = workFlowService.findWorkFlowHistoryByRefNo(groupFarmerProposal.getId());
		referenceType = WorkflowReferenceType.GROUPFARMER_PROPOSAL;
		policyReferenceType = PolicyReferenceType.GROUP_FARMER_PROPOSAL;

	}

	@PreDestroy
	public void destroy() {
		removeParam("groupFarmerProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentList = paymentService.findByProposal(groupFarmerProposal.getId(), policyReferenceType, false);
		paymentDTO = new PaymentDTO(paymentList);
	}

	public String paymentMedicalProposal() {
		String result = null;
		try {
			groupFarmerProposalService.paymentGroupFarmerProposal(groupFarmerProposal,paymentList, user.getBranch(), RequestStatus.FINISHED.name());
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, groupFarmerProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
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

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workflowHistoryList;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void selectUser() {
		selectUser(WorkflowTask.ISSUING, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}

	public void setPaymentDTO(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public List<WorkFlowHistory> getWorkflowHistoryList() {
		return workflowHistoryList;
	}

	public void setWorkflowHistoryList(List<WorkFlowHistory> workflowHistoryList) {
		this.workflowHistoryList = workflowHistoryList;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
	}

	public WorkflowReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(WorkflowReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public PolicyReferenceType getPolicyReferenceType() {
		return policyReferenceType;
	}

	public void setPolicyReferenceType(PolicyReferenceType policyReferenceType) {
		this.policyReferenceType = policyReferenceType;
	}

	public IWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public IPaymentService getPaymentService() {
		return paymentService;
	}

	public void setCashPayment(boolean cashPayment) {
		this.cashPayment = cashPayment;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

}
