package org.ace.insurance.web.manage.report.common;

public class AccountManualReceiptDTO {
	private String acName;
	private double amount;
	private String receivedDate;
	private String branchName;
	private String salePointName;

	public AccountManualReceiptDTO(String acName, double amount, String receivedDate, String salePointName, String branchName) {
		super();
		this.acName = acName;
		this.amount = amount;
		this.receivedDate = receivedDate;
		this.branchName = branchName;
		this.salePointName = salePointName;
	}

	public String getAcName() {
		return acName;
	}

	public void setAcName(String acName) {
		this.acName = acName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

}
