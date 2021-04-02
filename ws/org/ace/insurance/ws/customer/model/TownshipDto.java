package org.ace.insurance.ws.customer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.system.common.township.Township;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Township")
public class TownshipDto implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "id", required = false)
	private String id;
//	@XmlElement(name = "prefix", required = false)
//	private String prefix;
	@XmlElement(name = "name", required = false)
	private String name;
	@XmlElement(name = "description", required = false)
	private String description;
	@XmlElement(name = "province", required = false)
	private ProvinceDto province;
//	private int version;

	public TownshipDto() {
	}
	public TownshipDto(Township township) {
		this.id = township.getId();
		this.name = township.getName();
		this.description = township.getDescription();
//		this.prefix = township.getPrefix();
		this.province = new ProvinceDto(township.getProvince());
	}
	
	public TownshipDto(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProvinceDto getProvince() {
		return province;
	}

	public void setProvince(ProvinceDto province) {
		this.province = province;
	}

//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}
	
	public String getFullTownShip() {
		String fullAddress = name ;
		if(province != null && !province.getFullProvience().isEmpty()) {
			fullAddress = name + ", " + province.getFullProvience();
		}
		return fullAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
//	public String getPrefix() {
//		return prefix;
//	}
//
//
//	public void setPrefix(String prefix) {
//		this.prefix = prefix;
//	}
}
