package org.ace.ws.model.system;

import java.util.Date;

// Hospital
public class HOS001 {
	private String id;
	private String name;
	private String address;
	private String phone;
	private String website;
	private String email;
	private int noOfBed;
	private String township;
	private int version;
	private String createdUserId;
	private Date createdDate;
	private String updatedUserId;
	private Date updatedDate;

	public HOS001() {
	}

	public HOS001(String id, String name, String address, String phone, String website, String email, int noOfBed, String township, int version, String createdUserId,
			Date createdDate, String updatedUserId, Date updatedDate) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.website = website;
		this.email = email;
		this.noOfBed = noOfBed;
		this.township = township;
		this.version = version;
		this.createdUserId = createdUserId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedDate = updatedDate;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getNoOfBed() {
		return noOfBed;
	}

	public void setNoOfBed(int noOfBed) {
		this.noOfBed = noOfBed;
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

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(String updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}
