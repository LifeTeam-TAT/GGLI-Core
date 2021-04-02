package org.ace.insurance.web.manage.life.claim.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "PaymentLifeClaimActionBean")
public class PaymentLifeClaimActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private LifeClaimProposal lifeClaimProposal;
	private Payment payment;

	private User user;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeClaimProposal = (lifeClaimProposal == null) ? (LifeClaimProposal) getParam("lifeClaimProposal") : lifeClaimProposal;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		payment = paymentService.findClaimProposal(lifeClaimProposal.getId(), PolicyReferenceType.LIFE_CLAIM, false);
	}

	public String paymentLifeClaimProposal() {
		String result = "dashboard";
		try {
			List<Payment> paymentList = new ArrayList<Payment>();
			paymentList.add(payment);
			lifeClaimProposalService.paymentLifeClaimProposal(lifeClaimProposal, paymentList);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_PAYMENT_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimProposal.getClaimProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaimProposal");
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

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

}
