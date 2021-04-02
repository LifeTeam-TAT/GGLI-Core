package org.ace.insurance.report.shortEndowLife;

import java.util.Date;

import org.ace.insurance.common.ISorter;

public class NewShortTermEndowmentLifePolicyReport implements ISorter {

	private static final long serialVersionUID = 1L;
	private String id;
	private String insuredPersonName;
	private int age;
	private String policyNo;
	private String address;
	private double sumInsured;
	private String paymentMode;
	private int policyTerm;
	private double basicTermPremium;
	private Date policyStartDate;
	private Date lastPaymentDate;
	private String agentId;
	private String agentName;
	private String branchId;
	private String branchName;
	private String salePointId;
	private String salePointName;
	private String entitys;

	public NewShortTermEndowmentLifePolicyReport(NewShortTermEndowmentLifePolicyView obj) {

		this.id = obj.getPolicyId();
		this.insuredPersonName = obj.getInsuredPersonName();
		this.age = obj.getAge();
		this.policyNo = obj.getPolicyNo();
		this.address = obj.getAddress();
		this.sumInsured = obj.getSumInsured();
		this.paymentMode = obj.getPaymentMode();
		this.policyTerm = obj.getPolicyTerm();
		this.basicTermPremium = obj.getBasicTermPremium();
		this.policyStartDate = obj.getPolicyStartDate();
		this.lastPaymentDate = obj.getLastPaymentDate();
		this.agentId = obj.getAgentId();
		this.agentName = obj.getAgentName();
		this.branchId = obj.getBranchId();
		this.branchName = obj.getBranchName();
		this.salePointId = obj.getSalePointId();
		this.salePointName = obj.getSalePointName();
		this.entitys = obj.getEntitysId();
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public void setInsuredPersonName(String insuredPersonName) {
		this.insuredPersonName = insuredPersonName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public int getPolicyTerm() {
		return policyTerm;
	}

	public void setPolicyTerm(int policyTerm) {
		this.policyTerm = policyTerm;
	}

	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	public Date getPolicyStartDate() {
		return policyStartDate;
	}

	public void setPolicyStartDate(Date policyStartDate) {
		this.policyStartDate = policyStartDate;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getEntitys() {
		return entitys;
	}

	public void setEntitys(String entitys) {
		this.entitys = entitys;
	}

}
