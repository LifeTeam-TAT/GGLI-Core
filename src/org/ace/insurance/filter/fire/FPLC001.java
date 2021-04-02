package org.ace.insurance.filter.fire;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.system.common.paymenttype.PaymentType;

public class FPLC001 implements ISorter {
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
	private Date submittedDate;

	public FPLC001(String id, String policyNo, String proposalNo, String saleMan, String agent, String customer, String branch, double premium, double sumInsured,
			PaymentType paymentType, Date submittedDate) {

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
		this.submittedDate = submittedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(String saleMan) {
		this.saleMan = saleMan;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getRegistrationNo() {

		return policyNo;
	}
}
