package org.ace.insurance.web.manage.life.claim.inform;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.PaymentStatus;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "InformLifeClaimActionBean")
public class InformLifeClaimActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
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
		claimAcceptedInfo = new ClaimAcceptedInfo();
		claimAcceptedInfo.setReferenceNo(lifeClaimProposal.getId());
		claimAcceptedInfo.setClaimAmount(lifeClaimProposal.getTotalClaimAmont());
		claimAcceptedInfo.setInformDate(new Date());
		claimAcceptedInfo.setReferenceType(ReferenceType.LIFE_CLAIM);
		checkDisabilityClaimType();
		checkApprove();
	}

	private void checkApprove() {
		for (LifePolicyClaim claim : lifeClaimProposal.getClaimList()) {
			if (!claim.isApproved()) {
				reject = true;
				break;
			}
		}
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaimProposal");
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

	private void setDisabilityClaim() {
		for (LifePolicyClaim claim : lifeClaimProposal.getClaimList()) {
			if (claim instanceof DisabilityLifeClaim) {
				claim = disabiliyClaim;
			}
		}
	}

	public void changePaymentStatus(AjaxBehaviorEvent event) {
		if (disabiliyClaim.getPaymentStatus().equals(PaymentStatus.WAITING)) {
			double totalClaimAmt = claimAcceptedInfo.getClaimAmount() - lifeClaimProposal.getDisabilityClaimAmount();
			claimAcceptedInfo.setClaimAmount(totalClaimAmt);
		}

	}

	public void rejectLifeClaim() {

		try {
			if (isDisabilityClaim) {
				setDisabilityClaim();
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimProposal.getId(), remark, WorkflowTask.CLAIM_CONFIRMATION, WorkflowReferenceType.LIFE_CLAIM, user,
					responsiblePerson);
			lifeClaimProposalService.informLifeClaim(null, lifeClaimProposal, workFlowDTO);
			isPrint = true;
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_INFORM_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimProposal.getClaimProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	public void informLifeClaim() {
		try {
			if (isDisabilityClaim) {
				setDisabilityClaim();
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimProposal.getId(), remark, WorkflowTask.CLAIM_CONFIRMATION, WorkflowReferenceType.LIFE_CLAIM, user,
					responsiblePerson);
			lifeClaimProposalService.informLifeClaim(claimAcceptedInfo, lifeClaimProposal, workFlowDTO);
			isPrint = true;
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_INFORM_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimProposal.getClaimProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	public PaymentStatus[] getPaymentStatus() {
		return PaymentStatus.values();
	}

	public void openTemplateDialog() {
		putParam("lifeClaimProposal", lifeClaimProposal);
		putParam("workFlowList", getWorkflowList());
		openLifeClaimInfoTemplate();
	}

	public List<WorkFlowHistory> getWorkflowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeClaimProposal.getId());
	}

	public LifeClaimProposal getLifeClaimProposal() {
		return lifeClaimProposal;
	}

	public ClaimAcceptedInfo getClaimAcceptedInfo() {
		return claimAcceptedInfo;
	}

	public void setLifeClaimProposal(LifeClaimProposal lifeClaimProposal) {
		this.lifeClaimProposal = lifeClaimProposal;
	}

	public void setClaimAcceptedInfo(ClaimAcceptedInfo claimAcceptedInfo) {
		this.claimAcceptedInfo = claimAcceptedInfo;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_SURVEY, WorkFlowType.LIFE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		this.disabiliyClaim.setPaymentType(paymentType);

		double termdisabilityAmt = 0.00;
		int month = paymentType.getMonth();
		if (month == 0) {
			termdisabilityAmt = disabiliyClaim.getDisabilityAmount();
			disabiliyClaim.setPaymentterm(1);
		} else {
			int term = 12 / month;
			termdisabilityAmt = disabiliyClaim.getDisabilityAmount() / term;
			disabiliyClaim.setPaymentterm(term);
		}
		disabiliyClaim.setPaidterm(1);
		disabiliyClaim.setTermDisabilityAmount(termdisabilityAmt);
		claimAcceptedInfo.setClaimAmount(lifeClaimProposal.getHospitalClaimAmount() + termdisabilityAmt);
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public boolean getIsPrint() {
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
