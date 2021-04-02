package org.ace.insurance.web.manage.agent.payment;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.staff.Staff;

public class StaffCommissionDTO implements ISorter {
	private static final long serialVersionUID = 1L;

	private String tempId;
	private String referenceNo;
	private PolicyReferenceType referenceType;
	private Staff staff;
	private double withHoldingTax;
	private double commission;
	private Date commissionStartDate;
	private Date paymentDate;
	private Boolean isPaid;
	private Date fromDate;
	private Date toDate;
	private int policyCount;
	private String invoiceNo;
	private String chequeNo;
	private PaymentChannel paymentChannel;
	private Bank bank;

	public StaffCommissionDTO() {
	}

	public StaffCommissionDTO(StaffAgentCommission ac) {
		this.referenceNo = ac.getReferenceNo();
		this.referenceType = ac.getReferenceType();
		this.staff = ac.getStaff();
		this.commission = ac.getCommission();
		this.withHoldingTax = ac.getWithHoldingTax();
		this.commissionStartDate = ac.getCommissionStartDate();
		this.paymentDate = ac.getPaymentDate();
		this.isPaid = ac.getIsPaid();
	}

	public StaffCommissionDTO(Staff staff, Double commission, double withHoldingTax, Date fromDate, Date toDate, String invoiceNo, PaymentChannel paymentChannel) {
		this.staff = staff;
		this.commission = commission;
		this.withHoldingTax = withHoldingTax;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.invoiceNo = invoiceNo;
		this.paymentChannel = paymentChannel;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public PolicyReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(PolicyReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public Date getCommissionStartDate() {
		return commissionStartDate;
	}

	public void setCommissionStartDate(Date commissionStartDate) {
		this.commissionStartDate = commissionStartDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getPolicyCount() {
		return policyCount;
	}

	public void setPolicyCount(int policyCount) {
		this.policyCount = policyCount;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public PaymentChannel getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(PaymentChannel paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public double getWithHoldingTax() {
		return withHoldingTax;
	}

	public void setWithHoldingTax(double withHoldingTax) {
		this.withHoldingTax = withHoldingTax;
	}

	@Override
	public String getRegistrationNo() {
		return this.staff.getStaff_no();
	}

}
