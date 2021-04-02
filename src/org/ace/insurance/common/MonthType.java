package org.ace.insurance.common;

public enum MonthType {
	JAN("January"),FEB("February"),MAR("March"),APR("April"),MAY("May"),JUN("June"),
	JUL("July"),AUG("August"),SEP("September"),OCT("October"),NOV("November"),DEC("December");

	private String label;

	private MonthType (String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
