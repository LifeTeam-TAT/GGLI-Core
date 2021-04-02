package org.ace.insurance.web.manage.life.snakebite;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.snakebite.SnakeBiteBeneficiary;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SnakeBiteBeneficiariesInfoDTO {
	private String tempId;
	private int age;
	private float percentage;
	private RelationShip relationship;
	private String initialId;
	private Gender gender;
	private IdType idType;
	private ResidentAddress residentAddress;
	private Name name;
	private String idNo;
	private int version;
	private String prefix;

	public SnakeBiteBeneficiariesInfoDTO() {
		tempId = System.nanoTime() + "";
	}

	public SnakeBiteBeneficiariesInfoDTO(InsuredPersonBeneficiaries insuredPersonBeneficiaries) {
		tempId = System.nanoTime() + "";
		this.percentage = insuredPersonBeneficiaries.getPercentage();
		this.age = insuredPersonBeneficiaries.getAge();
		this.relationship = insuredPersonBeneficiaries.getRelationship();
		this.initialId = insuredPersonBeneficiaries.getInitialId();
		this.name = insuredPersonBeneficiaries.getName();
		this.gender = insuredPersonBeneficiaries.getGender();
		this.idType = insuredPersonBeneficiaries.getIdType();
		this.residentAddress = insuredPersonBeneficiaries.getResidentAddress();
		this.idNo = insuredPersonBeneficiaries.getIdNo();
	}

	public SnakeBiteBeneficiariesInfoDTO(SnakeBiteBeneficiary snakeBiteBeneficiary) {
		this.tempId = System.nanoTime() + "";
		this.percentage = snakeBiteBeneficiary.getPercentage();
		this.age = snakeBiteBeneficiary.getAge();
		this.relationship = snakeBiteBeneficiary.getRelationship();
		this.initialId = snakeBiteBeneficiary.getInitialId();
		this.name = snakeBiteBeneficiary.getName();
		this.gender = snakeBiteBeneficiary.getGender();
		this.idType = snakeBiteBeneficiary.getIdType();
		this.residentAddress = snakeBiteBeneficiary.getResidentAddress();
		this.idNo = snakeBiteBeneficiary.getIdNo();
		this.prefix = snakeBiteBeneficiary.getPrefix();
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public float getPercentage() {
		return percentage;
	}

	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}

	public RelationShip getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public boolean isValidBeneficiaries() {
		/*
		 * if(customer == null) { return false; }
		 */
		if (percentage <= 0) {
			return false;
		}
		if (age < 0) {
			return false;
		}
		return true;
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public ResidentAddress getResidentAddress() {
		if (residentAddress == null) {
			residentAddress = new ResidentAddress();
		}
		return residentAddress;
	}

	public void setResidentAddress(ResidentAddress residentAddress) {
		this.residentAddress = residentAddress;
	}

	public Name getName() {
		if (name == null) {
			name = new Name();
		}
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getFullName() {
		String result = "";
		if (name != null) {
			if (initialId != null && !initialId.isEmpty()) {
				result = initialId;
			}
			if (name.getFirstName() != null && !name.getFirstName().isEmpty()) {
				result = result + " " + name.getFirstName();
			}
			if (name.getMiddleName() != null && !name.getMiddleName().isEmpty()) {
				result = result + " " + name.getMiddleName();
			}
			if (name.getLastName() != null && !name.getLastName().isEmpty()) {
				result = result + " " + name.getLastName();
			}
		}
		return result;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
