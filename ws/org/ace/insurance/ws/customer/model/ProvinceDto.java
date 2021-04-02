package org.ace.insurance.ws.customer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.system.common.province.Province;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Province")
public class ProvinceDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "id", required = false)
	private String id;
//	@XmlElement(name = "prefix", required = false)
//	private String prefix;
	@XmlElement(name = "name", required = false)
	private String name;
	@XmlElement(name = "description", required = false)
	private String description;
	@XmlElement(name = "country", required = false)
	private CountryDto country;
//	private int version;

	public ProvinceDto() {
	}
	
	public ProvinceDto(Province province) {
		this.id = province.getId();
		this.name = province.getName();
		this.description = province.getDescription();
//		this.prefix = province.getPrefix();
		this.country = new CountryDto(province.getCountry());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public String getPrefix() {
//		return prefix;
//	}
//
//	public void setPrefix(String prefix) {
//		this.prefix = prefix;
//	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CountryDto getCountry() {
		return country;
	}

	public void setCountry(CountryDto country) {
		this.country = country;
	}

//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}

	public String getFullProvience() {
		return name + "," + country.getName();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
