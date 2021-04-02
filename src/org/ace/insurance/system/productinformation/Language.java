package org.ace.insurance.system.productinformation;

public enum Language {

	ENGLISH("English"), MYANMAR("Myanmar");

	private String label;

	private Language(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}