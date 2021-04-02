package org.ace.insurance.system.common.organization;

import java.io.Serializable;

public class ORG001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String ownerName;
	private String regNo;
	private String address;
	private String phone;
	private String mobile;
	private String email;

	public ORG001(String id, String name, String ownerName, String regNo, String address, String township, String phone, String mobile, String email) {
		this.id = id;
		this.name = name;
		this.ownerName = ownerName;
		this.regNo = regNo;
		this.address = address + ", " + township;
		this.phone = phone;
		this.mobile = mobile;
		this.email = email;
	}

	public ORG001(Organization organization) {
		this.id = organization.getId();
		this.name = organization.getName();
		this.ownerName = organization.getOwnerName();
		this.regNo = organization.getRegNo();
		this.address = organization.getFullAddress();
		this.phone = organization.getContentInfo().getPhone();
		this.mobile = organization.getContentInfo().getMobile();
		this.email = organization.getContentInfo().getEmail();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getRegNo() {
		return regNo;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	public String getMobile() {
		return mobile;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((regNo == null) ? 0 : regNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ORG001 other = (ORG001) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		} else if (!mobile.equals(other.mobile))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ownerName == null) {
			if (other.ownerName != null)
				return false;
		} else if (!ownerName.equals(other.ownerName))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (regNo == null) {
			if (other.regNo != null)
				return false;
		} else if (!regNo.equals(other.regNo))
			return false;
		return true;
	}

}
