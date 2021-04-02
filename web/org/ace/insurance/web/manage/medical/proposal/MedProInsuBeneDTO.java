package org.ace.insurance.web.manage.medical.proposal;

import java.util.Date;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.web.common.CommonDTO;

public class MedProInsuBeneDTO extends CommonDTO {
	private String tempId;
	private int age;
	private float percentage;
	private String beneficiaryNo;
	private String initialId;
	private String fatherName;
	private String idNo;
	private Gender gender;
	private IdType idType;
	private Date dateOfBirth;
	private ResidentAddress residentAddress;
	private ContentInfo contentInfo;
	private Name name;
	private RelationShip relationship;
	private MedicalProposalInsuredPerson proposalInsuredPerson;
	private StateCode stateCode;
	private TownshipCode townshipCode;
	private IdConditionType idConditionType;
	private String fullIdNo;

	public MedProInsuBeneDTO() {
		tempId = System.nanoTime() + "";
	}

	public MedProInsuBeneDTO(MedicalProposalInsuredPersonBeneficiaries medProInsuBeneDTO) {
		this.age = medProInsuBeneDTO.getAge();
		this.percentage = medProInsuBeneDTO.getPercentage();
		this.beneficiaryNo = medProInsuBeneDTO.getBeneficiaryNo();
		this.initialId = medProInsuBeneDTO.getInitialId();
		this.fatherName = medProInsuBeneDTO.getFatherName();
		this.idNo = medProInsuBeneDTO.getIdNo();
		this.gender = medProInsuBeneDTO.getGender();
		this.idType = medProInsuBeneDTO.getIdType();
		this.residentAddress = medProInsuBeneDTO.getResidentAddress();
		this.name = medProInsuBeneDTO.getName();
		this.relationship = medProInsuBeneDTO.getRelationship();
		this.stateCode = medProInsuBeneDTO.getStateCode();
		this.townshipCode = medProInsuBeneDTO.getTownshipCode();
		this.idConditionType = medProInsuBeneDTO.getIdConditionType();
		this.fullIdNo = medProInsuBeneDTO.getFullIdNo();
		this.dateOfBirth = medProInsuBeneDTO.getDateOfBirth();

	}

	public MedicalProposalInsuredPerson getProposalInsuredPerson() {
		return proposalInsuredPerson;
	}

	public void setProposalInsuredPerson(MedicalProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
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

	public String getFullIdNo() {
		String result = "";
		if (stateCode != null) {
			result = result + stateCode.getCodeNo() + "/";
		}
		if (townshipCode != null) {
			result = result + townshipCode.getTownshipcodeno();
		}
		if (idConditionType != null) {
			result = result + "(" + idConditionType.getLabel() + ")";
		}
		return result + idNo;
	}

	public void setFullIdNo(String fullIdNo) {
		this.fullIdNo = fullIdNo;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
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

	public String getBeneficiaryNo() {
		return beneficiaryNo;
	}

	public void setBeneficiaryNo(String beneficiaryNo) {
		this.beneficiaryNo = beneficiaryNo;
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

	public ContentInfo getContentInfo() {
		if (contentInfo == null) {
			contentInfo = new ContentInfo();
		}
		return contentInfo;
	}

	public void setContentInfo(ContentInfo contentInfo) {
		this.contentInfo = contentInfo;
	}

	public Name getName() {
		if (name == null) {
			name = new Name();
		}

		return name;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
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

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public RelationShip getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + age;
		result = prime * result + ((beneficiaryNo == null) ? 0 : beneficiaryNo.hashCode());
		result = prime * result + ((contentInfo == null) ? 0 : contentInfo.hashCode());
		result = prime * result + ((fatherName == null) ? 0 : fatherName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((idNo == null) ? 0 : idNo.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
		result = prime * result + ((initialId == null) ? 0 : initialId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Float.floatToIntBits(percentage);
		result = prime * result + ((proposalInsuredPerson == null) ? 0 : proposalInsuredPerson.hashCode());
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
		result = prime * result + ((residentAddress == null) ? 0 : residentAddress.hashCode());
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MedProInsuBeneDTO other = (MedProInsuBeneDTO) obj;
		if (age != other.age)
			return false;
		if (beneficiaryNo == null) {
			if (other.beneficiaryNo != null)
				return false;
		} else if (!beneficiaryNo.equals(other.beneficiaryNo))
			return false;
		if (contentInfo == null) {
			if (other.contentInfo != null)
				return false;
		} else if (!contentInfo.equals(other.contentInfo))
			return false;
		if (fatherName == null) {
			if (other.fatherName != null)
				return false;
		} else if (!fatherName.equals(other.fatherName))
			return false;
		if (gender != other.gender)
			return false;
		if (idNo == null) {
			if (other.idNo != null)
				return false;
		} else if (!idNo.equals(other.idNo))
			return false;
		if (idType != other.idType)
			return false;
		if (initialId == null) {
			if (other.initialId != null)
				return false;
		} else if (!initialId.equals(other.initialId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Float.floatToIntBits(percentage) != Float.floatToIntBits(other.percentage))
			return false;
		if (proposalInsuredPerson == null) {
			if (other.proposalInsuredPerson != null)
				return false;
		} else if (!proposalInsuredPerson.equals(other.proposalInsuredPerson))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		if (residentAddress == null) {
			if (other.residentAddress != null)
				return false;
		} else if (!residentAddress.equals(other.residentAddress))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		return true;
	}
}