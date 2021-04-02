package org.ace.insurance.report.actuarialReserve;

import java.math.BigDecimal;
import java.util.Date;

import org.ace.insurance.common.Gender;

public class ActuarialReserveReport implements Comparable<ActuarialReserveReport> {
	private Date issueDate;
	private int issueAge;
	private Gender gender;
	private int policyTerm;
	private int premiumMode;
	private Date activePolicyEndDate;
	private BigDecimal grossPremium;
	private String policyNo;
	private BigDecimal sumInured;

	public ActuarialReserveReport(String policyNo, Date issueDate, int issueAge, Gender gender, int policyTerm, int premiumMode, Date activePolicyEndDate, BigDecimal grossPremium,
			BigDecimal suminsured) {
		super();
		this.issueDate = issueDate;
		this.issueAge = issueAge;
		this.gender = gender;
		this.policyTerm = policyTerm;
		this.premiumMode = premiumMode;
		this.activePolicyEndDate = activePolicyEndDate;
		this.grossPremium = grossPremium;
		this.policyNo = policyNo;
		this.sumInured = suminsured;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public int getIssueAge() {
		return issueAge;
	}

	public Gender getGender() {
		return gender;
	}

	public int getPolicyTerm() {
		return policyTerm;
	}

	public int getPremiumMode() {
		return premiumMode;
	}

	public Date getActivePolicyEndDate() {
		return activePolicyEndDate;
	}

	public BigDecimal getGrossPremium() {
		return grossPremium;
	}

	public BigDecimal getSumInured() {
		return sumInured;
	}

	public void setSumInured(BigDecimal sumInured) {
		this.sumInured = sumInured;
	}

	@Override
	public int compareTo(ActuarialReserveReport object) {
		return this.issueDate.compareTo(object.getIssueDate());
	}

}
