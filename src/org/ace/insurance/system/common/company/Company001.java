/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.company;

import java.io.Serializable;

public class Company001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;
	private String address;
	private String townshipName;

	public Company001(Company company) {
		this.id = company.getId();
		this.name = company.getName();
		this.description = company.getDescription();
		this.address = company.getAddress().getPermanentAddress();
		this.townshipName = company.getAddress().getTownship().getName();
	}

	public Company001(String id, String name, String description, String township, String address) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.address = address;
		this.townshipName = township;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTownshipName() {
		return townshipName;
	}

	public void setTownshipName(String townshipName) {
		this.townshipName = townshipName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}