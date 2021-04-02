package org.ace.insurance.system.common.bankBranch;

import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.township.Township;

public class BBRANCH001 {

	private String id;
	private String name;
	private String branchCode;
	private String address;
	private Bank bank;
	private Township township;

	public BBRANCH001(String id, String branchCode, String name, Bank bank, Township township, String address) {
		this.id = id;
		this.branchCode = branchCode;
		this.name = name;
		this.bank = bank;
		this.township = township;
		this.address = address;

	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public String getAddress() {
		return address;
	}

	public Bank getBank() {
		return bank;
	}

	public Township getTownship() {
		return township;
	}

}
