package org.ace.insurance.filter.customer;

import java.io.Serializable;

import org.ace.insurance.common.Name;

public class CUST001 implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String fullIdNo;
	private String address;

	public CUST001(String id, String salutation, Name name, String fullIdNo, String address, String township, String province, String country) {
		this.id = id;
		this.name = salutation + " " + name.getFullName();
		this.fullIdNo = fullIdNo;
		if (address != null && !address.equals("null")) {
			this.address = address + ", " + township + ", " + province + ", " + country;
		} else {
			this.address = "";
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

}
