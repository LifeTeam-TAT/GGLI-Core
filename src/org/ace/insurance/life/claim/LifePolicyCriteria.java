package org.ace.insurance.life.claim;

import org.ace.insurance.common.LifePolicyCriteriaItems;

public class LifePolicyCriteria {
	public String criteriaValue;
	public LifePolicyCriteriaItems lifePolicyCriteriaItems;

	public LifePolicyCriteria() {
		super();
	}
	
	public LifePolicyCriteria(String criteriaValue, LifePolicyCriteriaItems lifePolicyCriteriaItems) {
		super();
		this.criteriaValue = criteriaValue;
		this.lifePolicyCriteriaItems = lifePolicyCriteriaItems;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public LifePolicyCriteriaItems getLifePolicyCriteriaItems() {
		return lifePolicyCriteriaItems;
	}

	public void setLifePolicyCriteriaItems(LifePolicyCriteriaItems lifePolicyCriteriaItems) {
		this.lifePolicyCriteriaItems = lifePolicyCriteriaItems;
	}
}
