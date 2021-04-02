package org.ace.insurance.system.appversion;

public enum MobileType {
	ANDROID("Android"), IOS("IOS");

	private String label;

	private MobileType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
