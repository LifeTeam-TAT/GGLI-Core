package org.ace.insurance.accounting;

public enum SacsCode {
	RP("Reinsurance Premiums"),
	CL("Claims Payments"),
	CO("Coinsurance"),
	CR("Claim R/I Recovery"),
	FG("Fire & General Insurance"),
	GL("General Ledger"),
	BK("Company Bank Accounts"),
	CN("Client account"),
	AG("Agent or Intermediary");
	private String label;

	private SacsCode(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
