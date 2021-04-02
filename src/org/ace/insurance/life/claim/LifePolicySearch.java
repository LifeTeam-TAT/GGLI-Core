package org.ace.insurance.life.claim;

import org.ace.insurance.common.Name;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;

public class LifePolicySearch {
	private String policyNo;
	private Customer customer;
	private Organization organization;
	private Branch branch;
	private String customerName;
	private String organizationName;
	private String branchName;

	public LifePolicySearch() {

	}

	public LifePolicySearch(String policyNo, String initialId, Name name, String organization, String branch) {
		this.policyNo = policyNo;
		this.customerName = initialId != null ? initialId + " " + name.getFullName() : organization;
		this.organizationName = organization;
		this.branchName = branch;

	}

	public LifePolicySearch(Object object) {
		Object[] objArray = (Object[]) object;
		if (objArray[0] instanceof String) {
			policyNo = (String) objArray[0];
		}
		if (objArray[1] instanceof Customer) {
			customer = (Customer) objArray[1];
		}
		if (objArray[2] instanceof Organization) {
			organization = (Organization) objArray[2];
		}
		if (objArray[3] instanceof Branch) {
			branch = (Branch) objArray[3];
		}
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
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

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

}
