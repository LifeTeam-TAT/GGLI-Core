package org.ace.insurance.mobile.enums;

public enum ResponseStatus {
	SUCCESS("Success"),

	FAIL("Fail"),

	INVALIDUSER("Invalid User"),

	WRONG_PASSWORD("Wrong Password"),

	INACTIVE("Inactive"),

	EXISTING_USER("Already User"),

	INTERNAL_SERVER_ERROR("000"),

	REQUEST_NOT_FOUND("001"),

	INVALID_REQUEST_PARAM("Invalid Request Param"),

	UNAUTHORIZED_REQUEST("Unauthorized Request");

	private String label;

	private ResponseStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
