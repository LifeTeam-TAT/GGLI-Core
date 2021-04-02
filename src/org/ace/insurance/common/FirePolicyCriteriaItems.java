package org.ace.insurance.common;

public enum FirePolicyCriteriaItems {
	POLICY_NO("Policy No"), CUSTOMER_NAME("Customer Name"), ORGANIZATION_NAME("Organization Name"), BANK_CUSTOMER("Bank Customer Name");
	private String label;

	private FirePolicyCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
