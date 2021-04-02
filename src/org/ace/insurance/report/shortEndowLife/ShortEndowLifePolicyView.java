package org.ace.insurance.report.shortEndowLife;

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
@Table(name = TableName.VWT_SHORTENDOWLIFEPOLICYVIEW)
@ReadOnly
public class ShortEndowLifePolicyView {
	@Id
	private String policyId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date commenmanceDate;

	private String policyNo;

	@Column(name = "INSUREDPERSON_NAME")
	private String insuredPersonName;

	private double sumInsured;

	@Temporal(TemporalType.TIMESTAMP)
	private Date policyStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date policyEndDate;

	private String assignee;

	private double yearlyPremium;

	private String modeOfPremium;

	private double termPremium;
	
	private String agentId;
	
	@Column(name = "AGENTNAME")
	private String agentName;
	
	private String remark;
	
	@Column(name = "BRANCH_ID")
	private String branchId;
	
	@Column(name = "BRANCH_NAME")
	private String branchName;
	
	private String customerId;
	private String organizationId;
	
	@Column(name = "SALEPOINT_ID")
	private String salePointId;

	@Column(name = "SALEPOINT_NAME")
	private String salePointName;
	
	private String proposalNo;
	
	private String entitysId;

	public ShortEndowLifePolicyView() {

	}

	/**
	 * @return the policyId
	 */
	public String getPolicyId() {
		return policyId;
	}

	/**
	 * @param policyId
	 *            the policyId to set
	 */
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	/**
	 * @return the commenmanceDate
	 */
	public Date getCommenmanceDate() {
		return commenmanceDate;
	}

	/**
	 * @param commenmanceDate
	 *            the commenmanceDate to set
	 */
	public void setCommenmanceDate(Date commenmanceDate) {
		this.commenmanceDate = commenmanceDate;
	}

	/**
	 * @return the policyNo
	 */
	public String getPolicyNo() {
		return policyNo;
	}

	/**
	 * @param policyNo
	 *            the policyNo to set
	 */
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	/**
	 * @return the insuredPersonName
	 */
	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	/**
	 * @param insuredPersonName
	 *            the insuredPersonName to set
	 */
	public void setInsuredPersonName(String insuredPersonName) {
		this.insuredPersonName = insuredPersonName;
	}

	/**
	 * @return the sumInsured
	 */
	public double getSumInsured() {
		return sumInsured;
	}

	/**
	 * @param sumInsured
	 *            the sumInsured to set
	 */
	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	/**
	 * @return the policyStartDate
	 */
	public Date getPolicyStartDate() {
		return policyStartDate;
	}

	/**
	 * @param policyStartDate
	 *            the policyStartDate to set
	 */
	public void setPolicyStartDate(Date policyStartDate) {
		this.policyStartDate = policyStartDate;
	}

	/**
	 * @return the policyEndDate
	 */
	public Date getPolicyEndDate() {
		return policyEndDate;
	}

	/**
	 * @param policyEndDate
	 *            the policyEndDate to set
	 */
	public void setPolicyEndDate(Date policyEndDate) {
		this.policyEndDate = policyEndDate;
	}

	/**
	 * @return the assignee
	 */
	public String getAssignee() {
		return assignee;
	}

	/**
	 * @param assignee
	 *            the assignee to set
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	/**
	 * @return the yearlyPremium
	 */
	public double getYearlyPremium() {
		return yearlyPremium;
	}

	/**
	 * @param yearlyPremium
	 *            the yearlyPremium to set
	 */
	public void setYearlyPremium(double yearlyPremium) {
		this.yearlyPremium = yearlyPremium;
	}

	/**
	 * @return the modeOfPremium
	 */
	public String getModeOfPremium() {
		return modeOfPremium;
	}

	/**
	 * @param modeOfPremium
	 *            the modeOfPremium to set
	 */
	public void setModeOfPremium(String modeOfPremium) {
		this.modeOfPremium = modeOfPremium;
	}

	/**
	 * @return the termPremium
	 */
	public double getTermPremium() {
		return termPremium;
	}

	/**
	 * @param termPremium
	 *            the termPremium to set
	 */
	public void setTermPremium(double termPremium) {
		this.termPremium = termPremium;
	}

	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * @param agentName
	 *            the agentName to set
	 */
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId
	 *            the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * @return the branchId
	 */
	public String getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId
	 *            the branchId to set
	 */
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId
	 *            the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the organizationId
	 */
	public String getOrganizationId() {
		return organizationId;
	}

	/**
	 * @param organizationId
	 *            the organizationId to set
	 */
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getEntitysId() {
		return entitysId;
	}

	public void setEntitysId(String entitysId) {
		this.entitysId = entitysId;
	}

}