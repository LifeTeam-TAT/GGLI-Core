package org.ace.insurance.accounting;

public enum BatchTranCode {
	T204("Cash not Banked"),
	T206("Payment"),
	T209("Authorise Payment Request"),
	T2A3("Receipt Cancellation"),
	T405("New Business Issue"),
	T409("Endorsement Issue"),
	T413("Renewal Issue"),
	T421("Approve Claim Registration"),
	T422("Approve Claim Modification"),
	T454("Cancellation issue");
	private String label;

	private BatchTranCode(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
