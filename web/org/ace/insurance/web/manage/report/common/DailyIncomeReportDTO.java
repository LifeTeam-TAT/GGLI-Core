package org.ace.insurance.web.manage.report.common;

import java.util.Date;

import org.ace.insurance.system.common.PaymentChannel;

public class DailyIncomeReportDTO {
	private String productName;
	private String policyNo;
	private String receiptNo;
	private Date receiveDate;
	private double amount;
	private PaymentChannel paymentChannel;
	private String salePointName;
	private String branchName;

	public DailyIncomeReportDTO(String productName, String policyNo, String receiptNo, Date receiveDate, double amount, PaymentChannel paymentChannel, String salePointName,
			String branchName) {
		super();
		this.productName = productName;
		this.policyNo = policyNo;
		this.receiptNo = receiptNo;
		this.receiveDate = receiveDate;
		this.amount = amount;
		this.paymentChannel = paymentChannel;
		this.salePointName = salePointName;
		this.branchName = branchName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public PaymentChannel getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(PaymentChannel paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

}
