package org.ace.insurance.life;

import java.util.Date;

public class LifeEndowmentPolicySearch {

	private String policyNo;
	private Date Start;
	private Date end;
	private String paymentType;
	private String paidTerm;
	private double paidPremium;

	public LifeEndowmentPolicySearch() {
	}
	
	public LifeEndowmentPolicySearch(String policyNo, Date start, Date end, String paymentType, int paidTerm,
			double paidPremium) {
		super();
		this.policyNo = policyNo;
		Start = start;
		this.end = end;
		this.paymentType = paymentType;
		this.paidTerm = String.valueOf(paidTerm);
		this.paidPremium = paidPremium;
	}



	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Date getStart() {
		return Start;
	}

	public void setStart(Date start) {
		Start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaidTerm() {
		return paidTerm;
	}

	public void setPaidTerm(String paidTerm) {
		this.paidTerm = paidTerm;
	}

	public double getPaidPremium() {
		return paidPremium;
	}

	public void setPaidPremium(double paidPremium) {
		this.paidPremium = paidPremium;
	}


}
