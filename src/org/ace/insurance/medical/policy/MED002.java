package org.ace.insurance.medical.policy;

import java.util.Date;

import org.ace.insurance.common.Name;
import org.ace.insurance.common.Utils;

public class MED002 {

	private String id;
	private String policyNo;
	private String proposalNo;
	private String agent;
	private String saleMan;
	private String customer;
	private String organization;
	private String branch;
	private Date policyStartDate;
	private Date policyEndDate;

	public MED002(String id, String policyNo, String proposalNo, String agentInitialId, Name agentName, String saleManInitialId, Name saleManName, String customerInitialId,
			Name customerName, String organizationName, String branchName, Date policyStartDate, Date policyEndDate) {
		super();
		this.id = id;
		this.policyNo = policyNo;
		this.proposalNo = proposalNo;
		this.agent = agentName  != null ? Utils.getCompleteName(agentInitialId, agentName) : Utils.getCompleteName(saleManInitialId, saleManName) ;
	//	this.saleMan = Utils.getCompleteName(saleManInitialId, saleManName);
		this.customer = customerName != null ?  Utils.getCompleteName(customerInitialId, customerName) : organizationName ;
		//this.organization = organizationName;
		this.branch = branchName;
		this.policyStartDate = policyStartDate;
		this.policyEndDate = policyEndDate;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the proposalNo
	 */
	public String getProposalNo() {
		return proposalNo;
	}

	/**
	 * @param proposalNo
	 *            the proposalNo to set
	 */
	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	/**
	 * @return the agent
	 */
	public String getAgent() {
		return agent;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(String agent) {
		this.agent = agent;
	}

	/**
	 * @return the saleMan
	 */
	public String getSaleMan() {
		return saleMan;
	}

	/**
	 * @param saleMan
	 *            the saleMan to set
	 */
	public void setSaleMan(String saleMan) {
		this.saleMan = saleMan;
	}

	/**
	 * @return the customer
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * @param customer
	 *            the customer to set
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * @param branch
	 *            the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
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
}
