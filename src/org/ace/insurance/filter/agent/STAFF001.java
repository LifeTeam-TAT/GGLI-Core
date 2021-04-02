package org.ace.insurance.filter.agent;

public class STAFF001 {
	private String id;
	private String name;
	private String idNo;
	private String address;
	private String branch;

	public STAFF001() {

	}

	public STAFF001(String id,String name, String idNo, String address, String branch) {
		this.id = id;
		this.name = name;
		this.idNo = idNo;
		this.address = address;
		this.branch = branch;
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

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}
}
