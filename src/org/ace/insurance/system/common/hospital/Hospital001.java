/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.hospital;

import java.io.Serializable;

public class Hospital001 implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String phone;
	private String address;
	private String township;
	private String description;

	public Hospital001(String id, String name, String phone, String address, String township, String description) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.township = township;
		this.description = description;
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

	public String getPhone() {
		return phone;
	}

	public String getAddress() {
		return address;
	}

	public String getTownship() {
		return township;
	}

}