package org.ace.ws.cargo.model.city;

import java.io.Serializable;

public class CityDTO implements Serializable {

	private String id;
	private String name;
	private String description;
	private String province;
	private int version;

	public CityDTO() {

	}

	public CityDTO(String id, String name, String description, String province, int version) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.province = province;
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
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

}
