package org.ace.insurance.common;

public enum SalutationType {
	MG("Mg"), MA("Ma"), MR("Mr"), MS("Ms");

	private String label;

	private SalutationType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
