package org.ace.insurance.ws.customer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.system.common.country.COUNTRY001;
import org.ace.insurance.system.common.country.Country;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Country")
public class CountryDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "id", required = true)
	private String id;
	// @XmlElement(name = "prefix", required = true)
	// private String prefix;
	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "description", required = true)
	private String description;
	// private int version;

	public CountryDto() {
	}

	public CountryDto(COUNTRY001 country) {
		this.id = country.getId();
		this.name = country.getName();
		this.description = country.getDescription();
	}

	public CountryDto(Country country) {
		this.id = country.getId();
		this.name = country.getName();
		this.description = country.getDescription();
	}

	public CountryDto(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// public String getPrefix() {
	// return prefix;
	// }
	//
	// public void setPrefix(String prefix) {
	// this.prefix = prefix;
	// }

	public String getName() {
		return this.name;
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

	// public int getVersion() {
	// return version;
	// }
	//
	// public void setVersion(int version) {
	// this.version = version;
	// }
}
