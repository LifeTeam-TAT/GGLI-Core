package org.ace.insurance.common.utils;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "bancaStatus")
@XmlEnum
public enum BancaStatus {
	INACTIVE("Inactive"), ACTIVE("Active");

	private String label;

	private BancaStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
