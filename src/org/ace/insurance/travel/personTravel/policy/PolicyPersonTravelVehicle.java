package org.ace.insurance.travel.personTravel.policy;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UsageOfVehicle;
import org.ace.insurance.travel.personTravel.proposal.ProposalPersonTravelVehicle;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.POLICYPERSONTRAVEL_VEHICLE)
@TableGenerator(name = "POLICYPERSONTRAVEL_VEHICLE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "POLICYPERSONTRAVEL_VEHICLE_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PolicyPersonTravelVehicle.findAll", query = "SELECT v FROM PolicyPersonTravelVehicle v"),
		@NamedQuery(name = "PolicyPersonTravelVehicle.findById", query = "SELECT v FROM PolicyPersonTravelVehicle v WHERE v.id=:id") })
@EntityListeners(IDInterceptor.class)
public class PolicyPersonTravelVehicle implements Serializable {

	private static final long serialVersionUID = 5055317670845134545L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "POLICYPERSONTRAVEL_VEHICLE_GEN")
	private String id;
	@Enumerated(EnumType.STRING)
	private UsageOfVehicle usageOfVehicle;
	private String registrationNo;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public PolicyPersonTravelVehicle() {
	}

	public PolicyPersonTravelVehicle(ProposalPersonTravelVehicle proposalVehicle) {
		this.usageOfVehicle = proposalVehicle.getUsageOfVehicle();
		this.registrationNo = proposalVehicle.getRegistrationNo();
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
	 * @return the usageOfVehicle
	 */
	public UsageOfVehicle getUsageOfVehicle() {
		return usageOfVehicle;
	}

	/**
	 * @param usageOfVehicle
	 *            the usageOfVehicle to set
	 */
	public void setUsageOfVehicle(UsageOfVehicle usageOfVehicle) {
		this.usageOfVehicle = usageOfVehicle;
	}

	/**
	 * @return the registrationNo
	 */
	public String getRegistrationNo() {
		return registrationNo;
	}

	/**
	 * @param registrationNo
	 *            the registrationNo to set
	 */
	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
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
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((registrationNo == null) ? 0 : registrationNo.hashCode());
		result = prime * result + ((usageOfVehicle == null) ? 0 : usageOfVehicle.hashCode());
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
		PolicyPersonTravelVehicle other = (PolicyPersonTravelVehicle) obj;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (registrationNo == null) {
			if (other.registrationNo != null)
				return false;
		} else if (!registrationNo.equals(other.registrationNo))
			return false;
		if (usageOfVehicle != other.usageOfVehicle)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
