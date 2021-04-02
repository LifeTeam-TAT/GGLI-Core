package org.ace.insurance.web.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "saleChannelType")
@XmlEnum
public enum SaleChannelType {

	AGENT("Agent"), SALEMAN("Saleman"), REFERRAL("Referral"), BANCASSURANCE("Bancassurance");

	private String label;

	private SaleChannelType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
