package org.ace.insurance.web.manage.medical.claim.payment;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.medical.claim.service.interfaces.IMedicalClaimProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "MedicalClaimPaymentActionBean")
public class MedicalClaimPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Payment payment;
	private User user;
	private MedicalClaimProposalDTO medicalClaimProposalDTO;

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

	@ManagedProperty(value = "#{MedicalClaimProposalService}")
	private IMedicalClaimProposalService medicalClaimProposalService;

	public void setMedicalClaimProposalService(IMedicalClaimProposalService medicalClaimProposalService) {
		this.medicalClaimProposalService = medicalClaimProposalService;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		payment = paymentService.findClaimProposal(medicalClaimProposalDTO.getId(), PolicyReferenceType.MEDICAL_CLAIM, false);

	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		medicalClaimProposalDTO = (medicalClaimProposalDTO == null) ? (MedicalClaimProposalDTO) getParam("medicalClaimProposal") : medicalClaimProposalDTO;
	}

	/********************************************
	 * Action Controller
	 ********************************************/

	public String claimPaymentConfirm() {
		String result = null;
		try {
			payment.setComplete(true);
			payment.setPaymentDate(new Date());
			medicalClaimProposalService.paymentMedicalClaimProposal(payment, medicalClaimProposalDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalClaimProposalDTO.getClaimRequestId());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public MedicalClaimProposalDTO getMedicalClaimProposalDTO() {
		return medicalClaimProposalDTO;
	}

	public Payment getPayment() {
		return payment;
	}

	public List<WorkFlowHistory> getWorkflowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(medicalClaimProposalDTO.getClaimRequestId());
	}

}
