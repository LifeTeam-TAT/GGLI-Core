package org.ace.insurance.common;

public enum LifePolicyCriteriaItems {
	POLICY_NO("Policy No"), CUSTOMER_NAME("Customer"), ORGANIZATION_NAME("Organization");
	private String label;

	private LifePolicyCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
