package org.ace.insurance.web.common;

public enum SurveyAnswerTwo {
	YES("Yes"), NO("No");

	private String label;

	private SurveyAnswerTwo(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
