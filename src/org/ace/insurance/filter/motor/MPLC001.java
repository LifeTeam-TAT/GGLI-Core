package org.ace.insurance.filter.motor;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.system.common.paymenttype.PaymentType;

public class MPLC001 implements ISorter {
	private String id;
	private String policyNo;
	private String proposalNo;
	private String saleMan;
	private String agent;
	private String customer;
	private String branch;
	private double premium;
	private double sumInsured;
	private PaymentType paymentType;
	private Date commenmanceDate;

	public MPLC001(String id, String policyNo, String proposalNo, String saleMan, String agent, String customer, String branch, double premium, double sumInsured,
			PaymentType paymentType, Date commenmanceDate) {
		this.id = id;
		this.policyNo = policyNo;
		this.proposalNo = proposalNo;
		this.saleMan = saleMan;
		this.agent = agent;
		this.customer = customer;
		this.branch = branch;
		this.premium = premium;
		this.sumInsured = sumInsured;
		this.paymentType = paymentType;
		this.commenmanceDate = commenmanceDate;
	}

	public String getId() {
		return id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String getSaleMan() {
		return saleMan;
	}

	public String getAgent() {
		return agent;
	}

	public String getCustomer() {
		return customer;
	}

	public String getBranch() {
		return branch;
	}

	public double getPremium() {
		return premium;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public Date getCommenmanceDate() {
		return commenmanceDate;
	}

	public String getRegistrationNo() {
		return policyNo;
	}
}
