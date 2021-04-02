package org.ace.insurance.filter.bankCustomer;

import org.ace.insurance.system.common.bank.Bank;

public class BANKCUSTOMER001 {
	private String id;
	private String name;
	private String description;
	private String acode;

	public BANKCUSTOMER001(String id, String name, String description, String code) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.acode = code;
	}

	public BANKCUSTOMER001(Bank bank) {
		this.id = bank.getId();
		this.acode = bank.getAcode();
		this.description = bank.getDescription();
		this.name = bank.getName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAcode() {
		return acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

}
