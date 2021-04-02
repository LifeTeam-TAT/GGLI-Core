package org.ace.insurance.common;

public enum PolicyCriteriaItems {
	POLICYNO("POLICYNO"), CUSTOMERNAME("CUSTOMERNAME"), ORGANIZATIONNAME("ORGANIZATIONNAME"), BANKCUSTOMER("BANKCUSTOMER");
	private String label;

	private PolicyCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
