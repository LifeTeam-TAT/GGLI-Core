package org.ace.insurance.common;

public class OrganizationCriteria {
	public String criteriaValue;
	public OrganizationCriteriaItems organizationCriteria;

	public OrganizationCriteria() {

	}

	public OrganizationCriteria(String criteriaValue, OrganizationCriteriaItems organizationCriteria) {
		this.criteriaValue = criteriaValue;
		this.organizationCriteria = organizationCriteria;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public OrganizationCriteriaItems getOrganizationCriteria() {
		return organizationCriteria;
	}

	public void setOrganizationCriteria(OrganizationCriteriaItems organizationCriteria) {
		this.organizationCriteria = organizationCriteria;
	}
}
