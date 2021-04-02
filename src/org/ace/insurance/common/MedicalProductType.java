package org.ace.insurance.common;

public enum MedicalProductType {
	
	HEALTH("Health"),MICRO_HEALTH("Micro Health"),CRITICAL_ILLNESS("Critical Illness");
	
	private String label;

	private MedicalProductType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
