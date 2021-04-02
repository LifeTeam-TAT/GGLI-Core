package org.ace.insurance.web.manage.enquires;

import java.util.Date;

import org.ace.insurance.common.ProposalType;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;

public class CargoClaimEnquiryCriteria {
	private Agent agent;
	private Customer customer;
	private Organization organization;
	private Bank bankCustomer;
	private SaleMan saleMan;
	private Branch branch;
	private String claimNo;
	private ProposalType proposalType;
	private String claimReferenceNo;
	private Customer referral;
	private Date leavingDate;

	public CargoClaimEnquiryCriteria() {

	}

	public CargoClaimEnquiryCriteria(Agent agent, Customer customer, Organization organization, Bank bankCustomer, SaleMan saleMan, Branch branch, String claimNo,
			ProposalType proposalType, String claimReferenceNo, Customer referral, Date leavingDate) {
		this.agent = agent;
		this.customer = customer;
		this.organization = organization;
		this.bankCustomer = bankCustomer;
		this.saleMan = saleMan;
		this.branch = branch;
		this.claimNo = claimNo;
		this.proposalType = proposalType;
		this.claimReferenceNo = claimReferenceNo;
		this.referral = referral;
		this.leavingDate = leavingDate;
	}

	public String getSalePersonName() {
		if (agent != null) {
			return agent.getFullName();
		} else if (saleMan != null) {
			return saleMan.getFullName();
		} else if (referral != null) {
			return referral.getFullName();
		}
		return null;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Bank getBankCustomer() {
		return bankCustomer;
	}

	public void setBankCustomer(Bank bankCustomer) {
		this.bankCustomer = bankCustomer;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getClaimNo() {
		return claimNo;
	}

	public void setClaimNo(String claimNo) {
		this.claimNo = claimNo;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
	}

	public String getClaimReferenceNo() {
		return claimReferenceNo;
	}

	public void setClaimReferenceNo(String claimReferenceNo) {
		this.claimReferenceNo = claimReferenceNo;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public Date getLeavingDate() {
		return leavingDate;
	}

	public void setLeavingDate(Date leavingDate) {
		this.leavingDate = leavingDate;
	}

}
