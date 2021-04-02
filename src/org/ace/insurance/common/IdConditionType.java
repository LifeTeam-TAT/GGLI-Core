package org.ace.insurance.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "IdConditionType")
@XmlEnum
public enum IdConditionType {
	N("N"), P("P"), E("E"), A("A"), D("D");

	private String label;

	private IdConditionType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
