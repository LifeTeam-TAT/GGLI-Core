package org.ace.insurance.system.common.country;

import java.io.Serializable;

public class COUNTRY001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;

	public COUNTRY001(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public COUNTRY001(Country country) {
		this.id = country.getId();
		this.name = country.getName();
		this.description = country.getDescription();
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
