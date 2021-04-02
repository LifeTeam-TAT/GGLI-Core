package org.ace.insurance.travel.personTravel.policy;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.travel.personTravel.proposal.ProposalTravellerBeneficiary;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.POLICYTRAVELLER_BENEFICIARY)
@TableGenerator(name = "POLICYTRAVELLER_BENEFICIARY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "POLICYTRAVELLER_BENEFICIARY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PolicyTravellerBeneficiary.findAll", query = "SELECT p FROM PolicyTravellerBeneficiary p") })
@EntityListeners(IDInterceptor.class)
public class PolicyTravellerBeneficiary implements Serializable {

	private static final long serialVersionUID = 6831779771472683175L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "POLICYTRAVELLER_BENEFICIARY_GEN")
	private String id;
	private double percentage;
	private String name;
	private String fatherName;
	private String nrcNo;
	private String email;
	private String phone;
	private String address;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOWNSHIPID", referencedColumnName = "ID")
	private Township township;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RELATIONSHIPID", referencedColumnName = "ID")
	private RelationShip relationship;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public PolicyTravellerBeneficiary() {
	}

	public PolicyTravellerBeneficiary(ProposalTravellerBeneficiary beneficiary) {
		this.percentage = beneficiary.getPercentage();
		this.name = beneficiary.getName();
		this.fatherName = beneficiary.getFatherName();
		this.nrcNo = beneficiary.getNrcNo();
		this.email = beneficiary.getEmail();
		this.phone = beneficiary.getPhone();
		this.address = beneficiary.getAddress();
		this.township = beneficiary.getTownship();
		this.relationship = beneficiary.getRelationship();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the percentage
	 */
	public double getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage
	 *            the percentage to set
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fatherName
	 */
	public String getFatherName() {
		return fatherName;
	}

	/**
	 * @param fatherName
	 *            the fatherName to set
	 */
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	/**
	 * @return the nrcNo
	 */
	public String getNrcNo() {
		return nrcNo;
	}

	/**
	 * @param nrcNo
	 *            the nrcNo to set
	 */
	public void setNrcNo(String nrcNo) {
		this.nrcNo = nrcNo;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the township
	 */
	public Township getTownship() {
		return township;
	}

	/**
	 * @param township
	 *            the township to set
	 */
	public void setTownship(Township township) {
		this.township = township;
	}

	/**
	 * @return the relationship
	 */
	public RelationShip getRelationship() {
		return relationship;
	}

	/**
	 * @param relationship
	 *            the relationship to set
	 */
	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
	}

	/**
	 * @return the commonCreateAndUpateMarks
	 */
	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	/**
	 * @param commonCreateAndUpateMarks
	 *            the commonCreateAndUpateMarks to set
	 */
	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fatherName == null) ? 0 : fatherName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nrcNo == null) ? 0 : nrcNo.hashCode());
		long temp;
		temp = Double.doubleToLongBits(percentage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
		result = prime * result + ((township == null) ? 0 : township.hashCode());
		result = prime * result + version;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyTravellerBeneficiary other = (PolicyTravellerBeneficiary) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fatherName == null) {
			if (other.fatherName != null)
				return false;
		} else if (!fatherName.equals(other.fatherName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nrcNo == null) {
			if (other.nrcNo != null)
				return false;
		} else if (!nrcNo.equals(other.nrcNo))
			return false;
		if (Double.doubleToLongBits(percentage) != Double.doubleToLongBits(other.percentage))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		if (township == null) {
			if (other.township != null)
				return false;
		} else if (!township.equals(other.township))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
