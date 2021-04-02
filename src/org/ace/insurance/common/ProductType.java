package org.ace.insurance.common;

public enum ProductType {

	PUBLIC_LIFE("PUBLIC_LIFE"), GROUP_LIFE("GROUP_LIFE"), SNAKE_BITE("SNAKE_BITE"), SPORT_MAN("SPORT_MAN"), FARMER("FARMER"), SHORT_TERM_ENDOWMNENT(
			"SHORT_TERM_ENDOWMNENT"), MICRO_HEALTH_INSURANCE(
					"MICRO_HEALTH_INSURANCE"), HEALTH_INSURANCE("HEALTH_INSURANCE"), CRITICALILLNESS("CRITICALILLNESS"), TRAVEL("TRAVEL"), PERSONTRAVEL("PERSONTRAVEL");
	private String label;

	private ProductType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
