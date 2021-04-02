package org.ace.ws.cargo.model.addOn;

import java.io.Serializable;

public class AddOnDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String description;
	private String addOnType;
	private double maxAmount;
	private double value;
	private int version;

	public AddOnDTO() {

	}

	public AddOnDTO(String id, String name, String description, String addOnType, double maxAmount, double value, int version) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.addOnType = addOnType;
		this.maxAmount = maxAmount;
		this.value = value;
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

	public String getAddOnType() {
		return addOnType;
	}

	public void setAddOnType(String addOnType) {
		this.addOnType = addOnType;
	}

	public double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
