package org.ace.insurance.common;

public enum AgentCriteriaItems {
	FULLNAME("Full Name"), AGENT_CODE("Agent Code"), LISCENSENO("Liscense No"), NRCNO("NRC"), FRCNO("FRC NO"), PASSPORTNO("Passport No");
	private String label;

	private AgentCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
