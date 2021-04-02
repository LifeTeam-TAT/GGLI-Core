package org.ace.insurance.report.upr;

import java.util.Date;

public class UPRReport implements Comparable<UPRReport> {

	private String productName;
	private String policyNo;
	private String paymentType;

	private Date policyEndDate;
	private Date premiumDueDate;
	private Date policyInceptionDate;
	private double premium;

	public UPRReport(String productName, String policyNo, Date policyInceptionDate, Date policyEndDate, double premium, String paymentType, Date premiumDueDate) {
		super();
		this.productName = productName;
		this.policyNo = policyNo;
		this.paymentType = paymentType;
		this.policyEndDate = policyEndDate;
		this.premiumDueDate = premiumDueDate;
		this.policyInceptionDate = policyInceptionDate;
		this.premium = premium;
	}

	public String getProductName() {
		return productName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public Date getPolicyEndDate() {
		return policyEndDate;
	}

	public Date getPremiumDueDate() {
		return premiumDueDate;
	}

	public Date getPolicyInceptionDate() {
		return policyInceptionDate;
	}

	public double getPremium() {
		return premium;
	}

	@Override
	public int compareTo(UPRReport o) {
		return this.productName.compareTo(o.getProductName());
	}

}
