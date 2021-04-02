package org.ace.insurance.ws.customer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.system.common.bankBranch.BankBranch;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BankBranch")
public class BankBranchDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "id", required = true)
	private String id;
//	@XmlElement(name = "prefix", required = true)
//	private String prefix;
	@XmlElement(name = "name", required = false)
	private String name;
	@XmlElement(name = "description", required = false)
	private String description;
	@XmlElement(name = "branchCode", required = false)
	private String branchCode;
	@XmlElement(name = "address", required = false)
	private String address;
	@XmlElement(name = "bank", required = false)
	private BankDto bank;
	@XmlElement(name = "township", required = false)
	private TownshipDto township;
//	private int version;

	public BankBranchDto() {
	}
	
	public BankBranchDto(BankBranch branch) {
		this.id = branch.getId();
		this.name = branch.getName();
		this.description = branch.getDescription();
		this.bank = new BankDto(branch.getBank());
		this.branchCode = branch.getBranchCode();
		//this.prefix = branch.getPrefix();
		this.address = branch.getAddress();
		this.township = new TownshipDto(branch.getTownship());
	}
	
	public BankBranchDto(String id) {
		this.id = id;		
	}
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BankDto getBank() {
		return bank;
	}

	public void setBank(BankDto bank) {
		this.bank = bank;
	}

	public TownshipDto getTownship() {
		return township;
	}

	public void setTownship(TownshipDto township) {
		this.township = township;
	}

//	public String getPrefix() {
//		return prefix;
//	}
//
//	public void setPrefix(String prefix) {
//		this.prefix = prefix;
//	}
}
