package org.ace.insurance.common;

public enum CargoClaimStatus {
	APPROVED("APPROVED"), REJECT("REJECT");

	private String label;

	private CargoClaimStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
