package org.ace.insurance.common;

public class FirePolicyCriteria {
	private String customerName;
	private String organizationName;
	private String policyNo;
	private String bankCustomer;
	public String criteriaValue;
	public FirePolicyCriteriaItems firePolicyCriteriaItems;

	public FirePolicyCriteria() {
		super();
	}

	public FirePolicyCriteria(String customerName, String organizationName, String policyNo, String bankCustomer) {
		super();
		this.customerName = customerName;
		this.organizationName = organizationName;
		this.policyNo = policyNo;
		this.bankCustomer = bankCustomer;
	}

	public String getBankCustomer() {
		return bankCustomer;
	}

	public void setBankCustomer(String bankCustomer) {
		this.bankCustomer = bankCustomer;
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

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public FirePolicyCriteriaItems getFirePolicyCriteriaItems() {
		return firePolicyCriteriaItems;
	}

	public void setFirePolicyCriteriaItems(FirePolicyCriteriaItems firePolicyCriteriaItems) {
		this.firePolicyCriteriaItems = firePolicyCriteriaItems;
	}

}
