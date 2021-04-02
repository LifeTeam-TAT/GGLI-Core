package org.ace.insurance.ws.customer.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.system.common.industry.Industry;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Industry")
public class IndustryDto implements Serializable {
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
//	private int version;

	public IndustryDto() {
	}
	
	public IndustryDto(Industry industry) {
		this.id = industry.getId();
		this.name = industry.getName();
		this.description = industry.getDescription();
	//	this.prefix = industry.getPrefix();
	}
	
	public IndustryDto(String id) {
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

	public void setName(String month) {
		this.name = month;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}
//
//	public String getPrefix() {
//		return prefix;
//	}
//
//	public void setPrefix(String prefix) {
//		this.prefix = prefix;
//	}
}
