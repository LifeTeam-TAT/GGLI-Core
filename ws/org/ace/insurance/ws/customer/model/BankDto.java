package org.ace.insurance.ws.customer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.system.common.bank.Bank;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Bank")
public class BankDto implements Serializable {
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
	@XmlElement(name = "acode", required = false)
	private String acode;
//	private int version;

	public BankDto() {
	}
	
	public BankDto(Bank bank) {
		this.id = bank.getId();
		this.name = bank.getName();
		this.description = bank.getDescription();
		//this.prefix = bank.getPrefix();
		this.acode = bank.getAcode();		
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

	
	public String getAcode() {
		return this.acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

	
//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}
//
//	public String getPrefix() {
//		return prefix;
//	}
//
//	public void setPrefix(String prefix) {
//		this.prefix = prefix;
//	}
}
