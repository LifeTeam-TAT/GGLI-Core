package org.ace.insurance.web.common;

public class ValidationUtil {

	private ValidationUtil() {
	}

	public static boolean isStringEmpty(String value) {
		return value == null || value.isEmpty();
	}

}
