package org.ace.insurance.system.common.account;

import java.io.Serializable;

public class CompositeKey implements Serializable {

	private static final long serialVersionUID = 1L;

	private String aCODE;

	private String cUR;

	private String branchID;

	public String getaCODE() {
		return aCODE;
	}

	public void setaCODE(String aCODE) {
		this.aCODE = aCODE;
	}

	public String getcUR() {
		return cUR;
	}

	public void setcUR(String cUR) {
		this.cUR = cUR;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

}
