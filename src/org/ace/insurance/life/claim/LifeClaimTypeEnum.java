package org.ace.insurance.life.claim;

public enum LifeClaimTypeEnum {
	HOSPITAL_DISCHARGE("HOSPITALIZED_CLAIM"), DEATH_CLAIM("DEATH_CLAIM"), DISIBILITY_CLAIM("DISIBILITY_CLAIM");

	private String label;

	private LifeClaimTypeEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
