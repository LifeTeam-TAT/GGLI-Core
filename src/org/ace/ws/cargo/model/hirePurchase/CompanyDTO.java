package org.ace.ws.cargo.model.hirePurchase;

import java.io.Serializable;

public class CompanyDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;
	private String address;
	private String mobile;
	private String email;
	private String fax;
	private int version;

	public CompanyDTO() {

	}

	public CompanyDTO(String id, String name, String description, String address, String mobile, String email, String fax, int version) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.address = address;
		this.mobile = mobile;
		this.version = version;
		this.email = email;
		this.fax = fax;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

}
