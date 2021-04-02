package org.ace.insurance.report.shortEndowLife;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_SHORTTERMENDOWMENTPOLICYREPORTVIEW)
@ReadOnly
public class NewShortTermEndowmentLifePolicyView implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private String policyId;
	private String insuredPersonName;
	private int age;
	private String policyNo;
	private String address;
	private double sumInsured;
	private String paymentMode;
	private int policyTerm;
	private double basicTermPremium;
	@Temporal(TemporalType.TIMESTAMP)
	private Date policyStartDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastPaymentDate;
	private String agentId;
	private String agentName;
	private String proposalNo;
	
	@Column(name = "BRANCH_ID")
	private String branchId;
	
	@Column(name = "BRANCH_NAME")
	private String branchName;
	
	@Column(name = "SALEPOINT_ID")
	private String salePointId;

	@Column(name = "SALEPOINT_NAME")
	private String salePointName;
	
	private String entitysId;

	public NewShortTermEndowmentLifePolicyView() {

	}

	public NewShortTermEndowmentLifePolicyView(NewShortTermEndowmentLifePolicyView obj) {
		this.policyId = obj. getPolicyId();
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
		this.proposalNo = obj.getProposalNo();
		this.entitysId = obj. getEntitysId();
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
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

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getEntitysId() {
		return entitysId;
	}

	
}
