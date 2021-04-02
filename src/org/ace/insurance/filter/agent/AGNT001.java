package org.ace.insurance.filter.agent;

public class AGNT001 {
	private String id;
	private String agentCode;
	private String licenseNo;
	private String name;
	private String idNo;
	private String address;
	private String branch;

	public AGNT001() {

	}

	public AGNT001(String id, String agentCode, String licenseNo, String name, String idNo, String address, String branch) {
		this.id = id;
		this.agentCode = agentCode;
		this.licenseNo = licenseNo;
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

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
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
