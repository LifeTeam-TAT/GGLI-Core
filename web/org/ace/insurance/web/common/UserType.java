package org.ace.insurance.web.common;

public enum UserType {
	AGENT("Agent"), SALEMAN("Saleman"), REFERRAL("Referral"), WALKIN("Walkin"), CHANNEL("Channel");

	private String label;

	private UserType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
