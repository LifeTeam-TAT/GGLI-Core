package org.ace.insurance.mobile.enums;

public enum PaymentGateway {
	OK$("OK$"),

	TWOC2P("TwoC2P");

	private String label;

	private PaymentGateway(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
