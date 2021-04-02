package org.ace.insurance.common;

public enum OrganizationCriteriaItems {
	NAME("Name"), ORGANIZATION_CODE("Organization Code"), REGISTRATION_NO("Registration No");
	private String label;

	private OrganizationCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
