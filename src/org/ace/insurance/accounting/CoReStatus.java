package org.ace.insurance.accounting;

public enum CoReStatus {
	N("Normal"), CO("CO"), RE("RP"), ALL("ALL");
	private String label;

	private CoReStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
