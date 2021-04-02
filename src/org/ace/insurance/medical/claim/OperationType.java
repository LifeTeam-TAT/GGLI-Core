package org.ace.insurance.medical.claim;

public enum OperationType {
	OPERATION("Operation"), ABORTION("Abortion");

	private String label;

	private OperationType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
