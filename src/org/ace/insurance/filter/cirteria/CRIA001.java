package org.ace.insurance.filter.cirteria;

public enum CRIA001 {
	FULLNAME("FULLNAME"), FIRSTNAME("FIRSTNAME"), MIDDLENAME("MIDDLENAME"), LASTNAME("LASTNAME"), NRCNO("NRCNO"), FRCNO("FRCNO"), PASSPORTNO("PASSPORTNO");
	private String label;

	private CRIA001(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
