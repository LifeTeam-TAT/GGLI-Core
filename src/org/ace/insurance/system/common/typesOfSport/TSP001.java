package org.ace.insurance.system.common.typesOfSport;

import java.io.Serializable;

public class TSP001 implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String description;

	public TSP001(String id, String name, String description) {

		this.id = id;
		this.name = name;
		this.description = description;
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

}
