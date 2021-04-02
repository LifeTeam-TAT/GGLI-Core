package org.ace.insurance.product;

import java.io.Serializable;

import org.ace.insurance.common.ProductGroupType;

public class PROGRP001 implements Serializable {
	private static final long serialVersionUID = 403570860946827514L;
	private String id;
	private String name;
	private ProductGroupType groupType;

	public PROGRP001(String id, String name, ProductGroupType groupType) {
		this.id = id;
		this.name = name;
		this.groupType = groupType;
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

	public ProductGroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(ProductGroupType groupType) {
		this.groupType = groupType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

}
