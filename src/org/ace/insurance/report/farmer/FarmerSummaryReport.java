package org.ace.insurance.report.farmer;

import java.util.Date;

public class FarmerSummaryReport {
	private Date date;
	private int numberOfPolicy;
	private double sumInsured;
	private double premium;
	private double commission;
	private String remark;
	private String salepoint;
	private String branch;

	public String getSalepoint() {
		return salepoint;
	}

	public void setSalepoint(String salepoint) {
		this.salepoint = salepoint;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public FarmerSummaryReport() {

	}

	public FarmerSummaryReport(FarmerSummaryReportView view) {
		date = view.getDate();
		numberOfPolicy = view.getNumberOfPolicy();
		sumInsured = view.getSumInsured();
		premium = view.getPremium();
		commission = view.getCommission();
		remark = view.getRemark();
		salepoint = view.getSalePointName();
		branch = view.getBranchName();
	}

	public Date getDate() {
		return date;
	}

	public int getNumberOfPolicy() {
		return numberOfPolicy;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public double getCommission() {
		return commission;
	}

	public String getRemark() {
		return remark;
	}

}
