package org.ace.insurance.system.common.city;

import org.ace.insurance.system.common.province.Province;

public class CITY001 {

	private String id;
	private String name;
	private String description;
	private Province province;

	public CITY001(String id, String name, String description, Province province) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.province = province;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

}
