package org.ace.insurance.system.common.province;

import java.io.Serializable;

public class PROV001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;

	public PROV001(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public PROV001(Province province) {
		this.id = province.getId();
		this.name = province.getName();
		this.description = province.getDescription();
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
