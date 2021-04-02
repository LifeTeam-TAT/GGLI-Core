package org.ace.insurance.web.manage.reverseTLF;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.ReverseTLFService;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;

@ManagedBean(name = "ManageReverseTLFActionBean")
@ViewScoped
public class ManageReverseTLFActionBean extends BaseBean {

	@ManagedProperty(value = "#{ReverseTLFService}")
	private ReverseTLFService reverseTLFService;

	public void setReverseTLFService(ReverseTLFService reverseTLFService) {
		this.reverseTLFService = reverseTLFService;
	}

	private List<Payment> paymentList;
	private TLFVoucherCriteria criteria;

	@PostConstruct
	public void init() {
		this.paymentList = new ArrayList<>();
		criteria = new TLFVoucherCriteria();
	}

	public void searchPayment() {
		this.paymentList = reverseTLFService.findPaymentByReceiptNo(criteria);
	}

	public void reset() {
		criteria = new TLFVoucherCriteria();
		this.paymentList = new ArrayList<>();
	}

	public void createTLFAndPaymentReverse(Payment payment) {
		try {
			reverseTLFService.reverseTLFAndPayment(payment.getReceiptNo());
			criteria = new TLFVoucherCriteria();
			this.paymentList = new ArrayList<>();
			addInfoMessage("Reverse process for " + payment.getReceiptNo() + " is successfully finished");
		} catch (SystemException se) {
			addErrorMessage("Fail to reverse TLF and Payment");
		}
	}

	public TLFVoucherCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TLFVoucherCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

}
