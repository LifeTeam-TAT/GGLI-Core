package org.ace.insurance.report.coinsurance;

public class CoinsuranceSummaryReport {
	private String coinsuranceCompanyName;
	private double inAmount;
	private double outAmount;
	private double difference;

	public CoinsuranceSummaryReport() {
	}

	public CoinsuranceSummaryReport(String coinsuranceCompanyName, double inAmount, double outAmount) {
		this.coinsuranceCompanyName = coinsuranceCompanyName;
		this.inAmount = inAmount;
		this.outAmount = outAmount;
		this.difference = inAmount - outAmount;
	}

	public String getCoinsuranceCompanyName() {
		return coinsuranceCompanyName;
	}

	public double getInAmount() {
		return inAmount;
	}

	public void setCoinsuranceCompanyName(String coinsuranceCompanyName) {
		this.coinsuranceCompanyName = coinsuranceCompanyName;
	}

	public void setInAmount(double inAmount) {
		this.inAmount = inAmount;
	}

	public void setOutAmount(double outAmount) {
		this.outAmount = outAmount;
	}

	public void setDifference(double difference) {
		this.difference = difference;
	}

	public double getOutAmount() {
		return outAmount;
	}

	public double getDifference() {
		return difference;
	}

}
