package org.ace.insurance.common;

public enum ResourceItemCriteriaItems {
	NAME("NAME"), PAGE_URL("PAGE_URL");
	private String label;

	private ResourceItemCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
