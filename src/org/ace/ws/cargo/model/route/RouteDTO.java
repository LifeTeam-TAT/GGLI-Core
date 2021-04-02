package org.ace.ws.cargo.model.route;

import java.io.Serializable;

public class RouteDTO implements Serializable {
	private String id;
	private String name;
	private String description;
	private String status;
	private int version;

	public RouteDTO() {
	}

	public RouteDTO(String id, String name, String description, String status, int version) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = status;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
