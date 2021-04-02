package org.ace.insurance.system.productinformation;

public enum OrderControlType {
	ACENDING("Acending"),DECENDING("Decending");
	private String label;

	private OrderControlType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
