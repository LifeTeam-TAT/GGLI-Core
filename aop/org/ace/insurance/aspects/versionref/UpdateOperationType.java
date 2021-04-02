package org.ace.insurance.aspects.versionref;

public enum UpdateOperationType {
	
	ADDNEW("AddNew"), UPDATE("Update"), DELETE("Delete");
	
	private String label;

	private UpdateOperationType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
