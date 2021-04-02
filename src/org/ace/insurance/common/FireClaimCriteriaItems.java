package org.ace.insurance.common;

public enum FireClaimCriteriaItems {
	POLICYNO("Policy No"), NRCNO("Nrc No"), CUSTOMERNAME("Customer Name"), ORGANIZATIONNAME("Organization Name"), REGISTRATIONCODE("Registration Code");
	private String label;

	private FireClaimCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
