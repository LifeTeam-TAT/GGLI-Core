package org.ace.insurance.common;

public enum FireClaimStatus {
	APPROVED("APPROVED"),REJECT("REJECT");

	private String label;

	private FireClaimStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
