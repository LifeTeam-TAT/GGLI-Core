/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "typesOfCharges")
@XmlEnum
public enum TypesOfCharges {
	FLATRATE("FlatRate"), PERCENTAGE("Percentage");

	private String label;

	private TypesOfCharges(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}