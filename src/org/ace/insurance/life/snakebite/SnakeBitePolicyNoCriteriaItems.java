package org.ace.insurance.life.snakebite;

public enum SnakeBitePolicyNoCriteriaItems {
	BOOK_NO("Book No."), POLICY_NO("Policy No.");
	private String label;

	private SnakeBitePolicyNoCriteriaItems(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
