package org.ace.insurance.web.manage.life.claim.confirm;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.claimaccept.service.interfaces.IClaimAcceptedInfoService;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeClaimConfirmationActionBean")
public class LifeClaimConfirmationActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	@ManagedProperty(value = "#{ClaimAcceptedInfoService}")
	private IClaimAcceptedInfoService claimAcceptedInfoService;

	public void setClaimAcceptedInfoService(IClaimAcceptedInfoService claimAcceptedInfoService) {
		this.claimAcceptedInfoService = claimAcceptedInfoService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private LifeClaimProposal lifeClaimProposal;
	private ClaimAcceptedInfo claimAcceptedInfo;
	private User responsiblePerson;
	private String remark;
	private PaymentDTO paymentDTO;
	private boolean isCheque;
	private Payment payment;
	private boolean showFormFlg;
	private boolean isDisabilityClaim;
	private boolean isDeathClaim;
	private boolean isHospitalClaim;
	private DisabilityLifeClaim disabiliyClaim;
	private boolean isPrint;
	private boolean reject;

	private void initializeInjection() {

		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeClaimProposal = (lifeClaimProposal == null) ? (LifeClaimProposal) getParam("lifeClaimProposal") : lifeClaimProposal;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentDTO = new PaymentDTO();
		claimAcceptedInfo = claimAcceptedInfoService.findClaimAcceptedInfoByReferenceNo(lifeClaimProposal.getId(), ReferenceType.LIFE_CLAIM);
		if (claimAcceptedInfo != null) {
			paymentDTO.setServicesCharges(claimAcceptedInfo.getServicesCharges());
			paymentDTO.setClaimAmount(claimAcceptedInfo.getClaimAmount());
		}
		checkDisabilityClaimType();
		for (LifePolicyClaim claim : lifeClaimProposal.getClaimList()) {
			if (!claim.isApproved()) {
				reject = true;
				break;
			}
		}
	}

	private void checkDisabilityClaimType() {
		for (LifePolicyClaim claim : lifeClaimProposal.getClaimList()) {
			if (claim instanceof DisabilityLifeClaim) {
				isDisabilityClaim = true;
				disabiliyClaim = (DisabilityLifeClaim) claim;
			} else if (claim instanceof LifeDeathClaim) {
				isDeathClaim = true;
			} else {
				isHospitalClaim = true;
			}
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaimProposal");
	}

	public void confirm() {
		String formID = "confirmaitonMedicalClaimProposalForm";
		Boolean valid = true;
		if (responsiblePerson == null) {
			addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!valid) {
			showFormFlg = false;
		} else {
			showFormFlg = true;
		}
	}

	public String deny() {
		String result = null;
		try {
			if (responsiblePerson == null) {
				String formID = "confirmaitonMedicalClaimProposalForm";
				addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			} else {
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimProposal.getId(), remark, WorkflowTask.CLAIM_CONFIRMATION, WorkflowReferenceType.LIFE_CLAIM, user,
						responsiblePerson);
				lifeClaimProposalService.rejectLifeClaimPropsal(lifeClaimProposal, workFlowDTO);
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimProposal.getClaimProposalNo());
				result = "dashboard";
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void confirmClaimBeneficiary() {
		try {

			if (isCheque) {
				paymentDTO.setPaymentChannel(PaymentChannel.CHEQUE);
			} else {
				paymentDTO.setPaymentChannel(PaymentChannel.CASHED);
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimProposal.getId(), remark, WorkflowTask.CLAIM_PAYMENT, WorkflowReferenceType.LIFE_CLAIM, user, responsiblePerson);
			lifeClaimProposalService.confirmLifeClaimPropsal(lifeClaimProposal, paymentDTO, workFlowDTO);
			addInfoMessage(null, "LIFE_ClAIM_CONFIRM_SUCCESS_PARAM", lifeClaimProposal.getClaimProposalNo());
			isPrint = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void openTemplateDialog() {
		putParam("lifeClaimProposal", lifeClaimProposal);
		putParam("workFlowList", getWorkflowList());
		openLifeClaimInfoTemplate();
	}

	public List<WorkFlowHistory> getWorkflowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeClaimProposal.getId());
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		paymentDTO.setBank(bank);
	}

	public boolean isCheque() {
		return isCheque;
	}

	public void setCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}

	public void setPaymentDTO(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_PAYMENT, WorkFlowType.LIFE);
	}

	public LifeClaimProposal getLifeClaimProposal() {
		return lifeClaimProposal;
	}

	public ClaimAcceptedInfo getClaimAcceptedInfo() {
		return claimAcceptedInfo;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setLifeClaimProposal(LifeClaimProposal lifeClaimProposal) {
		this.lifeClaimProposal = lifeClaimProposal;
	}

	public void setClaimAcceptedInfo(ClaimAcceptedInfo claimAcceptedInfo) {
		this.claimAcceptedInfo = claimAcceptedInfo;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isShowFormFlg() {
		return showFormFlg;
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		if (!isCheque) {
			paymentDTO.setBank(null);
			paymentDTO.setChequeNo(null);
		}
	}

	public boolean getIsDisabilityClaim() {
		return isDisabilityClaim;
	}

	public boolean getIsDeathClaim() {
		return isDeathClaim;
	}

	public boolean getIsHospitalClaim() {
		return isHospitalClaim;
	}

	public DisabilityLifeClaim getDisabiliyClaim() {
		return disabiliyClaim;
	}

	public void setDisabiliyClaim(DisabilityLifeClaim disabiliyClaim) {
		this.disabiliyClaim = disabiliyClaim;
	}

	public boolean getIsprint() {
		return isPrint;
	}

	public void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}

	public boolean isReject() {
		return reject;
	}

	public void setReject(boolean reject) {
		this.reject = reject;
	}

}
