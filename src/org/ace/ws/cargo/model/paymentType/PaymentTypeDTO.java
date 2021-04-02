package org.ace.ws.cargo.model.paymentType;

import java.io.Serializable;

public class PaymentTypeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private int month;
	private String description;
	private int version;

	public PaymentTypeDTO() {

	}

	public PaymentTypeDTO(String id, String name, int month, String description, int version) {
		this.id = id;
		this.name = name;
		this.month = month;
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

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
