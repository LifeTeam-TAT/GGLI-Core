package org.ace.insurance.common;

public enum FarmerPolicyType {
	
	FARMER("Farmer"),GROUP_FARMER("Group Farmer");
	
	private String label;

	private FarmerPolicyType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
