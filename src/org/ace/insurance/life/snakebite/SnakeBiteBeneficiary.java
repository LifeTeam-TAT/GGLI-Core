package org.ace.insurance.life.snakebite;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.web.manage.life.snakebite.SnakeBiteBeneficiariesInfoDTO;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.SNAKEBITEBENEFICIARY)
@TableGenerator(name = "SNAKEBITEBENEFICIARY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "SNAKEBITEBENEFICIARY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "SnakeBiteBeneficiary.findAll", query = "SELECT p FROM SnakeBiteBeneficiary p "),
		@NamedQuery(name = "SnakeBiteBeneficiary.findById", query = "SELECT p FROM SnakeBiteBeneficiary p WHERE p.id = :id") })
@Access(value = AccessType.FIELD)
public class SnakeBiteBeneficiary {
	@Transient
	private String id;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RELATIONSHIPID", referencedColumnName = "ID")
	private RelationShip relationship;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SNAKEBITEPOLICYID", referencedColumnName = "ID")
	private SnakeBitePolicy snakeBitePolicy;
	private int age;
	private float percentage;
	private String initialId;
	private String idNo;

	@Embedded
	private Name name;

	@Enumerated(value = EnumType.STRING)
	private IdType idType;

	@Enumerated(value = EnumType.STRING)
	private Gender gender;

	@Embedded
	private ResidentAddress residentAddress;

	@Transient
	private String prefix;

	public SnakeBiteBeneficiary() {
		super();
	}

	public SnakeBiteBeneficiary(RelationShip relationship, SnakeBitePolicy snakeBitePolicy, int age, float percentage, String initialId, String idNo, Name name, IdType idType,
			Gender gender, ResidentAddress residentAddress, String prefix) {
		super();
		this.relationship = relationship;
		this.snakeBitePolicy = snakeBitePolicy;
		this.age = age;
		this.percentage = percentage;
		this.initialId = initialId;
		this.idNo = idNo;
		this.name = name;
		this.idType = idType;
		this.gender = gender;
		this.residentAddress = residentAddress;
		this.prefix = prefix;
	}

	public SnakeBiteBeneficiary(SnakeBiteBeneficiariesInfoDTO snakeBiteBeneficiariesInfoDTO) {
		super();
		this.relationship = snakeBiteBeneficiariesInfoDTO.getRelationship();
		this.age = snakeBiteBeneficiariesInfoDTO.getAge();
		this.percentage = snakeBiteBeneficiariesInfoDTO.getPercentage();
		this.initialId = snakeBiteBeneficiariesInfoDTO.getInitialId();
		this.idNo = snakeBiteBeneficiariesInfoDTO.getIdNo();
		this.name = snakeBiteBeneficiariesInfoDTO.getName();
		this.idType = snakeBiteBeneficiariesInfoDTO.getIdType();
		this.gender = snakeBiteBeneficiariesInfoDTO.getGender();
		this.residentAddress = snakeBiteBeneficiariesInfoDTO.getResidentAddress();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "SNAKEBITEBENEFICIARY_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public SnakeBitePolicy getSnakeBitePolicy() {
		return snakeBitePolicy;
	}

	public void setSnakeBitePolicy(SnakeBitePolicy snakeBitePolicy) {
		this.snakeBitePolicy = snakeBitePolicy;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
		return residentAddress;
	}

	public void setResidentAddress(ResidentAddress residentAddress) {
		this.residentAddress = residentAddress;
	}

	public Name getName() {
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

	public String getFullAddress() {
		String result = "";
		if (residentAddress != null) {
			if (residentAddress.getResidentAddress() != null && !residentAddress.getResidentAddress().isEmpty()) {
				result = result + residentAddress.getResidentAddress();
			}
			if (residentAddress.getTownship() != null && !residentAddress.getTownship().getFullTownShip().isEmpty()) {
				result = result + ", " + residentAddress.getTownship().getFullTownShip();
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idNo == null) ? 0 : idNo.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
		result = prime * result + ((initialId == null) ? 0 : initialId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Float.floatToIntBits(percentage);
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((residentAddress == null) ? 0 : residentAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SnakeBiteBeneficiary other = (SnakeBiteBeneficiary) obj;
		if (age != other.age)
			return false;
		if (gender != other.gender)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (residentAddress == null) {
			if (other.residentAddress != null)
				return false;
		} else if (!residentAddress.equals(other.residentAddress))
			return false;
		return true;
	}

}