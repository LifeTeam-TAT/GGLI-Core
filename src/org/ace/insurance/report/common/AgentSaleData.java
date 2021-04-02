package org.ace.insurance.report.common;

public class AgentSaleData {
	private String insuranceType;
	private long noOfpolicy;
	private double premium;

	private String branchName;
	private String salePointName;

	public AgentSaleData() {
	}

	public AgentSaleData(String insuranceType, long noOfPolicy, double premium) {
		this.insuranceType = insuranceType;
		this.noOfpolicy = noOfPolicy;
		this.premium = premium;
	}

	public AgentSaleData(String insuranceType, String salePointName, String branchName, long noOfpolicy, double premium) {
		super();
		this.insuranceType = insuranceType;
		this.noOfpolicy = noOfpolicy;
		this.premium = premium;
		this.branchName = branchName;
		this.salePointName = salePointName;
	}

	public String getBranchName() {
		return branchName;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public long getNoOfpolicy() {
		return noOfpolicy;
	}

	public void setNoOfpolicy(long noOfpolicy) {
		this.noOfpolicy = noOfpolicy;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

}
