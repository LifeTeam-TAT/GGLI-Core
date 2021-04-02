package org.ace.insurance.coinsurance;

import java.util.Date;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;

public class OutCoinsuranceCriteria {
	public String coinsuranceCompany;
	public Enum<InsuranceType> insuranceType;
	private Agent agent;
	private Customer customer;
	private Organization organization;
	private SaleMan saleMan;
	private Branch branch;
	private String policyNo;
	private Date startDate;
	private Date endDate;

	public OutCoinsuranceCriteria() {

	}

	public OutCoinsuranceCriteria(String coinsuranceCompany, Enum<InsuranceType> insuranceType) {
		super();
		this.coinsuranceCompany = coinsuranceCompany;
		this.insuranceType = insuranceType;
	}

	public String getCoinsuranceCompany() {
		return coinsuranceCompany;
	}

	public void setCoinsuranceCompany(String coinsuranceCompany) {
		this.coinsuranceCompany = coinsuranceCompany;
	}

	public Enum<InsuranceType> getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(Enum<InsuranceType> insuranceType) {
		this.insuranceType = insuranceType;
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

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
