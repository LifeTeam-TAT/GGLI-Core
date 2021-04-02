package org.ace.insurance.common;

public enum WorkFlowType {
	LIFE("Life"),

	AGENT_COMMISSION("Agent Commission"),

	COINSURANCE("Co-insurance"),

	FEDILITY("Fidelity"),

	SNAKE_BITE_POLICY("Snake Bite Policy"),

	MEDICAL_INSURANCE("Medical Insurance"),

	TRAVEL("Travel Insurance"),

	LIFESURRENDER("Life Surrender"),

	LIFE_PAIDUP("Life PaidUp"),

	SHORTTERM_LIFE_PAIDUP("ShortTerm Endowment Life PaidUp"),

	PERSONAL_ACCIDENT("Personal Accident"),

	PERSON_TRAVEL("Person Travel"),

	FARMER("Farmer"),

	PUBLIC_TERM_LIFE("PublicTermLife"),

	SHORT_ENDOWMENT("Short Term Endowment Life"),

	STUDENT_LIFE("Student Life"),

	SINGLE_PREMIUM_ENDOWMENT_LIFE("Single Premium Endowment Life"),

	SINGLE_PREMIUM_CREDIT_LIFE("Single Preminum Credit Life"),

	SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE("Short Term Single Preminum Credit Life"),

	SIMPLE_LIFE("Simple Life");

	private String label;

	private WorkFlowType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
