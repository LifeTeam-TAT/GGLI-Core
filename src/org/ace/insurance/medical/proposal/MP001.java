package org.ace.insurance.medical.proposal;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.saleman.SaleMan;

public class MP001 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private SaleMan saleMan;
	private Agent agent;
	private Customer referral;
	private String customer;
	private String branch;
	private int unit;
	private double totalPremium;
	private Date submittedDate;
	private String policyNo;
	private String receivedNo;
	private String salePerson;

	public MP001() {
	}

	public MP001(String id, String proposalNo, String salePerson, String customer, String branch, Long unit, Double totalPremium, Date submittedDate, String policyNo,
			String receivedNo) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.customer = customer;
		this.branch = branch;
		this.unit = unit != null ? unit.intValue() : 0;
		this.totalPremium = totalPremium != null ? totalPremium.doubleValue() : 0;
		this.submittedDate = submittedDate;
		this.policyNo = policyNo;
		this.receivedNo = receivedNo;
		this.salePerson = salePerson;
	}

	public String getReceivedNo() {
		return receivedNo;
	}

	public void setReceivedNo(String receivedNo) {
		this.receivedNo = receivedNo;
	}

	public String getSalePerson() {

		return salePerson;
	}

	public void setSalePerson(String salePerson) {
		this.salePerson = salePerson;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
	}

	public String getId() {
		return id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public Agent getAgent() {
		return agent;
	}

	public String getCustomer() {
		return customer;
	}

	public String getBranch() {
		return branch;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}

}
