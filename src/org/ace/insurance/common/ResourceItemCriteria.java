package org.ace.insurance.common;

public class ResourceItemCriteria {
	public String criteriaValue;
	public ResourceItemCriteriaItems resourceItemCriteria;

	public ResourceItemCriteria() {

	}

	public ResourceItemCriteria(String criteriaValue, ResourceItemCriteriaItems resourceItemCriteria) {
		this.criteriaValue = criteriaValue;
		this.resourceItemCriteria = resourceItemCriteria;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public ResourceItemCriteriaItems getResourceItemCriteria() {
		return resourceItemCriteria;
	}

	public void setResourceItemCriteria(ResourceItemCriteriaItems resourceItemCriteria) {
		this.resourceItemCriteria = resourceItemCriteria;
	}

}
