package org.ace.insurance.system.common.branch;

import java.io.Serializable;

import org.ace.insurance.system.common.entitys.Entitys;

public class BRA001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String branchCode;
	private String description;
	private String townshipName;
	private String address;
	private Entitys entitys;
	private boolean status;

	public BRA001(String id, String name, String branchCode, String description, String townshipName, String address, Entitys entitys) {
		this.id = id;
		this.name = name;
		this.branchCode = branchCode;
		this.description = description;
		this.townshipName = townshipName;
		this.address = address;
		this.entitys = entitys;

	}

	public BRA001(Branch branch) {
		this.id = branch.getId();
		this.name = branch.getName();
		this.branchCode = branch.getBranchCode();
		this.description = branch.getDescription();
		this.townshipName = branch.getTownship().getName();
		this.address = branch.getAddress();
		this.entitys = branch.getEntity();
		this.status = branch.isStatus();
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

	public String getDescription() {
		return description;
	}

	public String getTownshipName() {
		return townshipName;
	}

	public void setTownshipName(String townshipName) {
		this.townshipName = townshipName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((branchCode == null) ? 0 : branchCode.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((entitys == null) ? 0 : entitys.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((townshipName == null) ? 0 : townshipName.hashCode());
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
		BRA001 other = (BRA001) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (branchCode == null) {
			if (other.branchCode != null)
				return false;
		} else if (!branchCode.equals(other.branchCode))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (entitys == null) {
			if (other.entitys != null)
				return false;
		} else if (!entitys.equals(other.entitys))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (townshipName == null) {
			if (other.townshipName != null)
				return false;
		} else if (!townshipName.equals(other.townshipName))
			return false;
		return true;
	}

}
