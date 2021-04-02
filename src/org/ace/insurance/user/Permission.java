package org.ace.insurance.user;

public enum Permission {
	UNDERWRITING("Underwriting"),
	SURVEY("Survey"),
	APPROVAL("Approval"),
	CONFIRMATION("Confirmation"),
	PAYMNET("Payment"),
	ISSUING("Issuing");

	private String label;

	private Permission(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
