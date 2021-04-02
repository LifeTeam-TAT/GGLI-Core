package org.ace.insurance.web.manage.life.claim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.PaymentStatus;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.user.User;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeDisabilityPaymentActionBean")
public class LifeDisabilityPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	private User user;
	private boolean isAccountBank;
	private boolean isCheque;
	private boolean isBank;
	private boolean isTransfer;
	private PaymentChannel channelValue;
	private LifeDisabilityPaymentCriteria criteria;
	private DisabilityLifeClaim disabilityClaim;
	private PaymentDTO payment;
	private User responsiblePerson;
	private String remark;
	private List<DisabilityLifeClaim> disabilityLifeClaimList;
	private boolean showDisabilityPanel;

	@PostConstruct
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		criteria = new LifeDisabilityPaymentCriteria();
		disabilityClaim = new DisabilityLifeClaim();
		payment = new PaymentDTO();
		disabilityLifeClaimList = new ArrayList<DisabilityLifeClaim>();
	}

	public void search() {
		disabilityLifeClaimList = lifeClaimProposalService.findDisabilityLifeClaimByLifeClaimProposalNo(criteria);
		if (disabilityLifeClaimList.size() > 0) {
			showDisabilityPanel = true;
			disabilityClaim = disabilityLifeClaimList.get(0);
		}

	}

	public String confirm() {
		WorkFlowDTO workFlowDTO = new WorkFlowDTO(disabilityClaim.getId(), remark, WorkflowTask.CLAIM_PAYMENT, WorkflowReferenceType.LIFE_CLAIM, user, responsiblePerson);
		if (disabilityClaim.getPaymentStatus().equals(PaymentStatus.PAID)) {
			disabilityClaim.setPaidterm(disabilityClaim.getPaidterm() + 1);
			lifeClaimProposalService.confirmLifeDisabilityClaim(disabilityClaim, payment, workFlowDTO);
		}

		return "dashboard";

	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		payment.setAccountBank(null);
		payment.setBank(null);
		payment.setChequeNo(null);
		payment.setPoNo(null);
	}

	public LifeDisabilityPaymentCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(LifeDisabilityPaymentCriteria criteria) {
		this.criteria = criteria;
	}

	public List<DisabilityLifeClaim> getDisabilityLifeClaimList() {
		return disabilityLifeClaimList;
	}

	public PaymentStatus[] getPaymentStatus() {
		return PaymentStatus.values();
	}

	public void changePaymentStatus(AjaxBehaviorEvent event) {
		if (disabilityClaim.getPaymentStatus().equals(PaymentStatus.PAID)) {
			disabilityClaim.setPaidterm(disabilityClaim.getPaidterm() + 1);
		}
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		this.disabilityClaim.setPaymentType(paymentType);
		int month = paymentType.getMonth();
		double termdisabilityAmt = disabilityClaim.getDisabilityAmount() / (12 / month);
		this.disabilityClaim.setTermDisabilityAmount(termdisabilityAmt);
		this.disabilityClaim.setPaidterm(1);
	}

	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		payment.setAccountBank(accountBank);
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		payment.setBank(bank);
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_PAYMENT, WorkFlowType.LIFE);
	}

	public DisabilityLifeClaim getDisabilityClaim() {
		return disabilityClaim;
	}

	public void setDisabilityClaim(DisabilityLifeClaim disabilityClaim) {
		this.disabilityClaim = disabilityClaim;
	}

	public boolean isAccountBank() {
		return isAccountBank;
	}

	public void setAccountBank(boolean isAccountBank) {
		this.isAccountBank = isAccountBank;
	}

	public boolean isCheque() {
		return isCheque;
	}

	public void setCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public boolean isBank() {
		return isBank;
	}

	public void setBank(boolean isBank) {
		this.isBank = isBank;
	}

	public boolean isTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public PaymentChannel getChannelValue() {
		return channelValue;
	}

	public void setChannelValue(PaymentChannel channelValue) {
		if (channelValue.equals(PaymentChannel.CHEQUE)) {
			isAccountBank = true;
			isCheque = true;
			isBank = true;
			isTransfer = false;
		} else if (channelValue.equals(PaymentChannel.TRANSFER)) {
			isAccountBank = true;
			isCheque = false;
			isBank = true;
			isTransfer = true;
		} else {
			isAccountBank = false;
			isCheque = false;
			isBank = false;
			isTransfer = false;
		}
		this.channelValue = channelValue;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
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

	public boolean isShowDisabilityPanel() {
		return showDisabilityPanel;
	}

}
