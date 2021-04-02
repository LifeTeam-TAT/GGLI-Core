/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.ws.customer.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.common.IdType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FamilyInfo")
public class FamilyInfoDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "initialId", required = true)
	private String initialId;

	@XmlElement(name = "idNo", required = false)
	private String idNo;

	@XmlElement(name = "dateOfBirth", required = false)
	private Date dateOfBirth;

	@XmlElement(name = "refCustomerId", required = false)
	private String refCustomerId;

	@XmlElement(name = "name", required = true)
	private NameDto name;

	@XmlElement(name = "idType", required = false)
	private IdType idType;

	@XmlElement(name = "relationShipId", required = false)
	private String relationShipId;

	@XmlElement(name = "industryId", required = false)
	private String industryId;

	@XmlElement(name = "occupationId", required = false)
	private String occupationId;

	public FamilyInfoDto() {
	}

	public FamilyInfoDto(String initialId, String idNo, IdType idType, Date dateOfBirth, String refCustomerId, NameDto name, String relationShipId, String industryId,
			String occupationId) {
		this.initialId = initialId;
		this.idNo = idNo;
		this.idType = idType;
		this.dateOfBirth = dateOfBirth;
		this.refCustomerId = refCustomerId;
		this.name = name;
		this.idType = idType;
		this.relationShipId = relationShipId;
		this.industryId = industryId;
		this.occupationId = occupationId;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getRefCustomerId() {
		return refCustomerId;
	}

	public void setRefCustomerId(String refCustomerId) {
		this.refCustomerId = refCustomerId;
	}

	public String getRelationShipId() {
		return relationShipId;
	}

	public void setRelationShipId(String relationShipId) {
		this.relationShipId = relationShipId;
	}

	public String getIndustryId() {
		return industryId;
	}

	public void setIndustryId(String industryId) {
		this.industryId = industryId;
	}

	public String getOccupationId() {
		return occupationId;
	}

	public void setOccupationId(String occupationId) {
		this.occupationId = occupationId;
	}

	public NameDto getName() {
		return name;
	}

	public void setName(NameDto name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(this, object);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}