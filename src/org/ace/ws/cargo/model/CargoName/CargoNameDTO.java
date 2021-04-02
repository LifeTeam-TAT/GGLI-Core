package org.ace.ws.cargo.model.CargoName;

import java.io.Serializable;

public class CargoNameDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String description;
	private int version;

	public CargoNameDTO() {

	}

	public CargoNameDTO(String id, String name, String description, int version) {
		this.id = id;
		this.name = name;
		this.description = description;
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

}
