package org.ace.ws.cargo.model.port;

import java.io.Serializable;

public class PortDTO implements Serializable {
	private String id;
	private String name;
	private String Description;
	private String permanentAddress;
	private String mobile;
	private String status;
	private int version;

	public PortDTO() {
	}

	public PortDTO(String id, String name, String description, String permanentAddress, String mobile, String status, int version) {
		super();
		this.id = id;
		this.name = name;
		this.Description = description;
		this.permanentAddress = permanentAddress;
		this.mobile = mobile;
		this.status = status;
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

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
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

}
