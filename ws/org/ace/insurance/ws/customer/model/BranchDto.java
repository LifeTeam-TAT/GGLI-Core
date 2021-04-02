package org.ace.insurance.ws.customer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.system.common.branch.Branch;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Branch")
public class BranchDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "id", required = true)
	private String id;
//	@XmlElement(name = "prefix", required = true)
//	private String prefix;
	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "branchCode", required = true)
	private String branchCode;
	@XmlElement(name = "Address", required = true)
	private String Address;
	@XmlElement(name = "description", required = true)
	private String description;
	@XmlElement(name = "township", required = true)
	private TownshipDto township;
	//private int version;

	public BranchDto() {
	}
	
	public BranchDto(Branch branch) {
		this.id = branch.getId();
		this.name = branch.getName();
		this.description = branch.getDescription();
		this.Address = branch.getAddress();
		this.branchCode = branch.getBranchCode();
		//this.prefix = branch.getPrefix();
		this.township = new TownshipDto(branch.getTownship());
				
	}
	
	public BranchDto(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public String getPrefix() {
//		return prefix;
//	}
//
//	public void setPrefix(String prefix) {
//		this.prefix = prefix;
//	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}

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

	public TownshipDto getTownship() {
		return township;
	}

	public void setTownship(TownshipDto township) {
		this.township = township;
	}
}
