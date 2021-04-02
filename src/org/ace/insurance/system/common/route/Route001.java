package org.ace.insurance.system.common.route;

import java.io.Serializable;

public class Route001 implements Serializable {
	private static final long serialVersionUID = -5051498836613520404L;

	private String id;
	private String name;
	private String description;

	public Route001(String id, String name, String description) {
		super();
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
