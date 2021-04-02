package org.ace.insurance.common;

public enum PolicyChannel {

	CORE("Core"),

	PORTAL("Portal"),

	MOBILE("Mobile");

	private String label;

	private PolicyChannel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
