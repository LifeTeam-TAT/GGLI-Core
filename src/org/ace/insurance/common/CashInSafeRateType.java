package org.ace.insurance.common;

public enum CashInSafeRateType {
	GOVERNMENTANDBANK ("Government and Bank"),OTHERBUSINESS("Other Business");

	private String label;

	private CashInSafeRateType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
