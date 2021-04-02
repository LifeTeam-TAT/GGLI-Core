package org.ace.insurance.web.manage.medical.payment;

import java.io.Serializable;
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

import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.frontService.paymentService.interfaces.IPaymentMedicalProposalFrontService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.user.User;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.event.SelectEvent;

/**
 * 
 * Medical Payment ActionBean
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2015/01/15
 */
@ViewScoped
@ManagedBean(name = "MedicalPaymentActionBean")
public class MedicalPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

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

	@ManagedProperty(value = "#{PaymentMedicalProposalFrontService}")
	private IPaymentMedicalProposalFrontService paymentMedicalProposalFrontService;

	public void setPaymentMedicalProposalFrontService(IPaymentMedicalProposalFrontService paymentMedicalProposalFrontService) {
		this.paymentMedicalProposalFrontService = paymentMedicalProposalFrontService;
	}

	private User user;
	private PaymentDTO paymentDTO;
	private boolean cashPayment = true;
	private String remark;
	private List<Payment> paymentList;
	private User responsiblePerson;
	private List<WorkFlowHistory> workflowHistoryList;
	private MedicalProposal medicalProposal;
	private WorkflowReferenceType referenceType;
	private HealthType healthType;
	private PolicyReferenceType policyReferenceType;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalProposal = (medicalProposal == null) ? (MedicalProposal) getParam("medicalProposal") : medicalProposal;
		workflowHistoryList = workFlowService.findWorkFlowHistoryByRefNo(medicalProposal.getId());
		healthType = (HealthType) getParam("WORKFLOWHEALTHTYPE");
		referenceType = null;
		switch (healthType) {
			case HEALTH:
				referenceType = WorkflowReferenceType.HEALTH_PROPOSAL;
				policyReferenceType = PolicyReferenceType.HEALTH_POLICY;
				break;
			case CRITICALILLNESS:
				referenceType = WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL;
				policyReferenceType = PolicyReferenceType.CRITICAL_ILLNESS_POLICY;
				break;
			case MICROHEALTH:
				referenceType = WorkflowReferenceType.MICRO_HEALTH_PROPOSAL;
				policyReferenceType = PolicyReferenceType.MICRO_HEALTH_POLICY;
				break;
			default:
				break;
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("medicalProposal");
		removeParam("WORKFLOWHEALTHTYPE");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentList = paymentService.findByProposal(medicalProposal.getId(), policyReferenceType, false);
		paymentDTO = new PaymentDTO(paymentList);
	}

	public String paymentMedicalProposal() {
		String result = null;
		try {
			String formID = "medicalPaymentForm";
			if (responsiblePerson == null) {
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
				return null;
			}

			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, WorkflowTask.ISSUING, referenceType, user, responsiblePerson);
			paymentMedicalProposalFrontService.paymentMedicalProposal(medicalProposal, workFlowDTO, paymentList, user.getBranch(), RequestStatus.FINISHED.name());
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalProposal.getProposalNo());
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

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public MedProDTO getMedicalProposalDTO() {
		return MedicalProposalFactory.getMedProDTO(medicalProposal);
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

}
