package org.ace.insurance.life.policy;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.PolicyStatus;

public class LPC001 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String policyNo;
	private String saleMan;
	private String agent;
	private String customer;
	private String branch;
	private double premium;
	private double sumInsured;
	private String paymentType;
	private boolean isGroupLife;
	private PolicyStatus policyStatus;

	public LPC001(String id, String policyNo) {
		super();
		this.id = id;
		this.policyNo = policyNo;
	}

	public LPC001(String id) {
		super();
		this.id = id;
	}

	public LPC001(String id, String policyNo, String saleMan, String agent, String customer, String branch, double premium, double sumInsured, String paymentType,
			boolean isGroupLife) {
		super();
		this.id = id;
		this.policyNo = policyNo;
		this.saleMan = saleMan;
		this.agent = agent;
		this.customer = customer;
		this.branch = branch;
		this.premium = premium;
		this.sumInsured = sumInsured;
		this.paymentType = paymentType;
		this.isGroupLife = isGroupLife;
	}

	public LPC001() {

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

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public boolean isGroupLife() {
		return isGroupLife;
	}

	public void setGroupLife(boolean isGroupLife) {
		this.isGroupLife = isGroupLife;
	}

	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

}
