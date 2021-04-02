package org.ace.insurance.web.manage.life.snakebite.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "AddNewSnakeBitePolicyPaymentActionBean")
public class AddNewSnakeBitePolicyPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SnakeBitePolicyService}")
	private ISnakeBitePolicyService snakeBitePolicyService;

	public void setSnakeBitePolicyService(ISnakeBitePolicyService snakeBitePolicyService) {
		this.snakeBitePolicyService = snakeBitePolicyService;
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

	private User user;
	private SnakeBitePolicyDTO snakeBitePolicyDTO;

	private Payment payment;
	private PaymentDTO paymentDTO;
	private List<Payment> paymentList;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		snakeBitePolicyDTO = (SnakeBitePolicyDTO) getParam("snakeBitePolicyDTO");
		paymentList = paymentService.findPaymentByReceiptNo(snakeBitePolicyDTO.getReceiptNo());
		paymentDTO = new PaymentDTO(paymentList);
	}

	public String confirm() {
		String result = null;
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			List<SnakeBitePolicy> snakeBitePolicyList = snakeBitePolicyService.findSnakeBitePolicyByReceiptNo(snakeBitePolicyDTO.getReceiptNo());
			paymentList = paymentService.findPaymentByReceiptNo(snakeBitePolicyDTO.getReceiptNo());
			for (Payment p : paymentList) {
				payment = p;
			}
			snakeBitePolicyService.paymentSnakeBitePolicy(snakeBitePolicyList, payment);
			addInfoMessage(null, MessageId.PAYMENT_PROCESS_SUCCESS, snakeBitePolicyDTO.getReceiptNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	@PreDestroy
	public void destroy() {
		removeParam("snakeBitePolicyDTO");
	}

	public SnakeBitePolicyDTO getSnakeBitePolicyDTO() {
		return snakeBitePolicyDTO;
	}

	public void setSnakeBitePolicyDTO(SnakeBitePolicyDTO snakeBitePolicyDTO) {
		this.snakeBitePolicyDTO = snakeBitePolicyDTO;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Payment getPayment() {
		return payment;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}
	
}
