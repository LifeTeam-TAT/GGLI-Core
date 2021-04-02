package org.ace.ws.cargo.model.branch;

public class BranchDTO {
	private String id;
	private String name;
	private String branchCode;
	private String Address;
	private boolean isCoInsuAccess;
	private String description;
	private String township;
	private int version;

	public BranchDTO() {
	}

	public BranchDTO(String id, String name, String branchCode, String address, boolean isCoInsuAccess, String description, String township, int version) {
		super();
		this.id = id;
		this.name = name;
		this.branchCode = branchCode;
		Address = address;
		this.isCoInsuAccess = isCoInsuAccess;
		this.description = description;
		this.township = township;
		this.version = version;
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

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public boolean isCoInsuAccess() {
		return isCoInsuAccess;
	}

	public void setCoInsuAccess(boolean isCoInsuAccess) {
		this.isCoInsuAccess = isCoInsuAccess;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTownship() {
		return township;
	}

	public void setTownship(String township) {
		this.township = township;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
