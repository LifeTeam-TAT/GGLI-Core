package org.ace.insurance.web.manage.report.common;

import java.util.Date;

import org.ace.insurance.system.common.PaymentChannel;

public class AccountAndLifeDeptCancelReportDTO {
	private String tlfNo;
	private String productGroupName;
	private String salePointName;
	private PaymentChannel paymentChannel;
	private Date accountReceiptDate;
	private boolean accountStatus;
	private double amount;
	private Date lifeDeptPaymentDate;
	private boolean lifeDeptStatus;
	private double accountPremium;
	private boolean paymentComplete;
	// TODO FIXME PSH for remove
	private boolean paymentCompleteProcess;
	private String branchName;
	private String entity;

	public AccountAndLifeDeptCancelReportDTO(String tlfNo, String productGroupName, String salePointName, PaymentChannel paymentChannel, Date accountReceiptDate,
			boolean accountStatus, double amount, Date lifeDeptPaymentDate, boolean lifeDeptStatus, double accountPremium, boolean paymentComplete, String branchName,String entityId) {
		super();
		this.tlfNo = tlfNo;
		this.productGroupName = productGroupName;
		this.salePointName = salePointName;
		this.paymentChannel = paymentChannel;
		this.accountReceiptDate = accountReceiptDate;
		this.accountStatus = accountStatus;
		this.amount = amount;
		this.lifeDeptPaymentDate = lifeDeptPaymentDate;
		this.lifeDeptStatus = lifeDeptStatus;
		this.accountPremium = accountPremium;
		this.paymentComplete = paymentComplete;
		this.branchName = branchName;
		this.entity = entityId;
	}

	public String getTlfNo() {
		return tlfNo;
	}

	public void setTlfNo(String tlfNo) {
		this.tlfNo = tlfNo;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public PaymentChannel getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(PaymentChannel paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public Date getAccountReceiptDate() {
		return accountReceiptDate;
	}

	public void setAccountReceiptDate(Date accountReceiptDate) {
		this.accountReceiptDate = accountReceiptDate;
	}

	public boolean isAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(boolean accountStatus) {
		this.accountStatus = accountStatus;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getLifeDeptPaymentDate() {
		return lifeDeptPaymentDate;
	}

	public void setLifeDeptPaymentDate(Date lifeDeptPaymentDate) {
		this.lifeDeptPaymentDate = lifeDeptPaymentDate;
	}

	public boolean isLifeDeptStatus() {
		return lifeDeptStatus;
	}

	public void setLifeDeptStatus(boolean lifeDeptStatus) {
		this.lifeDeptStatus = lifeDeptStatus;
	}

	public double getAccountPremium() {
		return accountPremium;
	}

	public void setAccountPremium(double accountPremium) {
		this.accountPremium = accountPremium;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public boolean isPaymentCompleteProcess() {
		this.paymentCompleteProcess = (paymentComplete && !lifeDeptStatus);
		return paymentCompleteProcess;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public void setPaymentCompleteProcess(boolean paymentCompleteProcess) {
		this.paymentCompleteProcess = paymentCompleteProcess;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	
}
