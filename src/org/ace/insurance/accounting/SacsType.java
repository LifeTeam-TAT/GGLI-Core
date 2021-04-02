package org.ace.insurance.accounting;

public enum SacsType {
	D("Discount"),
	BO("Outstanding Losses"),
	A("Intermediary or GL Account"),
	R1("Stamp Duty on RI Premium"),
	C1("Commission 1 (Agt Comm/RI Com)"),
	EC("PAXTSK"),
	CR("Claim R/I Recovery"),
	PC("Payment - Agent Commissions"),
	PP("Policy Premium Payment"),
	P("Gross Premium"),
	TF("Outstanding Losses Transfer"),
	S("Contract Suspense"),
	NP("Net Premium To Debtors Control"),
	PY("Claims Payment");
	private String label;

	private SacsType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
