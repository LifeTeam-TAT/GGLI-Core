package org.ace.insurance.web.manage.travel;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.deno.service.interfaces.IDenoService;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.component.selectonemenu.SelectOneMenu;

@ViewScoped
@ManagedBean(name = "AddNewTravelPaymentActionBean")
public class AddNewTravelPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

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

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	private User user;
	private TravelProposal travelProposal;
	private PaymentDTO paymentDTO;
	private boolean cashPayment = true;
	private String remark;
	private List<Payment> paymentList;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (travelProposal == null) ? (TravelProposal) getParam("travelProposal") : travelProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("travelproposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentList = paymentService.findByProposal(travelProposal.getId(), PolicyReferenceType.TRAVEL_PROPOSAL, false);
		paymentDTO = new PaymentDTO(paymentList);
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

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String paymentTravelProposal() {
		String result = null;
		try {
			travelProposalService.paymentTravelProposal(travelProposal, paymentList, user.getBranch());
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public void setPayment(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

}
