package org.ace.ws.cargo.model.typeOfBody;

import java.io.Serializable;

public class TypeOfBodyDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String descriptionp;
	private int version;

	public TypeOfBodyDTO() {

	}

	public TypeOfBodyDTO(String id, String name, String descriptionp, int version) {
		this.id = id;
		this.name = name;
		this.descriptionp = descriptionp;
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

	public String getDescriptionp() {
		return descriptionp;
	}

	public void setDescriptionp(String descriptionp) {
		this.descriptionp = descriptionp;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
