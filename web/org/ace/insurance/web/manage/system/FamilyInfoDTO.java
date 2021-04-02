package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.Date;

import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FamilyInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String tempId;
	private StateCode stateCode;
	private TownshipCode townshipCode;
	private IdConditionType idConditionType;
	private String fullIdNo;
	private Name name;
	private String initialId;
	private String idNo;
	private IdType idType;
	private Date dateOfBirth;
	private RelationShip relationShip;
	private Industry industry;
	private Occupation occupation;

	public FamilyInfoDTO() {
		tempId = System.nanoTime() + "";
	}

	public FamilyInfoDTO(FamilyInfo familyInfo) {
		tempId = System.nanoTime() + "";
		this.initialId = familyInfo.getInitialId();
		this.fullIdNo = familyInfo.getIdNo();
		this.idType = familyInfo.getIdType();
		this.dateOfBirth = familyInfo.getDateOfBirth();
		this.name = familyInfo.getName();
		this.idType = familyInfo.getIdType();
		this.relationShip = familyInfo.getRelationShip();
		this.industry = familyInfo.getIndustry();
		this.occupation = familyInfo.getOccupation();

	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public Name getName() {
		if (this.name == null) {
			this.name = new Name();
		}
		return this.name;
	}

	public void setName(Name name) {
		this.name = name;
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

	public void setFullIdNo(String fullIdNo) {
		this.fullIdNo = fullIdNo;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public void setFullIdNo() {
		if (idType.equals(IdType.NRCNO)) {
			fullIdNo = stateCode.getCodeNo() + "/" + townshipCode.getTownshipcodeno() + "(" + idConditionType.getLabel() + ")" + idNo;
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO)) {
			fullIdNo = idNo;
		}
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Occupation getOccupation() {
		return this.occupation;
	}

	public void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}

	public Industry getIndustry() {
		return this.industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	public RelationShip getRelationShip() {
		return this.relationShip;
	}

	public void setRelationShip(RelationShip relationShip) {
		this.relationShip = relationShip;
	}

	public String getFullName() {
		return name.getFullName();
	}

	public StateCode getStateCode() {
		return stateCode;
	}

	public void setStateCode(StateCode stateCode) {
		this.stateCode = stateCode;
	}

	public TownshipCode getTownshipCode() {
		return townshipCode;
	}

	public void setTownshipCode(TownshipCode townshipCode) {
		this.townshipCode = townshipCode;
	}

	public IdConditionType getIdConditionType() {
		return idConditionType;
	}

	public void setIdConditionType(IdConditionType idConditionType) {
		this.idConditionType = idConditionType;
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
