package org.ace.insurance.common;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PolicyCriteria {
	private String criteriaValue;
	private PolicyCriteriaItems policyCriteria;
	private String product;
	private Date fromDate;
	private Date toDate;
	private ReferenceType referenceType;
	private String salePointId;

	public PolicyCriteria() {

	}

	public PolicyCriteria(String criteriaValue, PolicyCriteriaItems policyCriteria, ReferenceType referenceType) {
		this.criteriaValue = criteriaValue;
		this.policyCriteria = policyCriteria;
		this.referenceType = referenceType;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public PolicyCriteriaItems getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteriaItems policyCriteria) {
		this.policyCriteria = policyCriteria;
	}

	public List<PolicyCriteriaItems> getPolicyCriteriaItemList() {
		return Arrays.asList(PolicyCriteriaItems.POLICYNO, PolicyCriteriaItems.CUSTOMERNAME, PolicyCriteriaItems.ORGANIZATIONNAME);
	}

	// for skip student life
	public List<PolicyCriteriaItems> getStudentLifePolicyCriteriaItemList() {
		return Arrays.asList(PolicyCriteriaItems.POLICYNO);
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

}
