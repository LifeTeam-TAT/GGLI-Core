package org.ace.insurance.medical.claim;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "claimType")
@XmlEnum
public enum ClaimType {
	HOSPITALIZED_CLAIM("Hospitalized"), DEATH_CLAIM("Death"), MEDICATION_CLAIM("Medication"), OPERATION_CLAIM("Operation");

	private String label;

	private ClaimType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
