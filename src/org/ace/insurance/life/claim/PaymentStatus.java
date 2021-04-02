package org.ace.insurance.life.claim;

public enum PaymentStatus {
	WAITING("WAITING"), PAID("PAID");
	private String label;

	private PaymentStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
