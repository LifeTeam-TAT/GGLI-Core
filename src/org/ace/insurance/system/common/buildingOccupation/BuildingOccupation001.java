package org.ace.insurance.system.common.buildingOccupation;

import java.io.Serializable;

public class BuildingOccupation001 implements Serializable {

	private String id;
	private String name;
	private String description;

	public BuildingOccupation001(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}
}