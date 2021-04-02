package org.ace.insurance.travel.personTravel.policy;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalKeyfactorValue;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PERSONTRAVEL_POLICY_KEYFACTORVALUE)
@TableGenerator(name = "PERSONTRAVEL_POLICY_KEYFACTORVALUE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PERSONTRAVEL_POLICY_KEYFACTORVALUE_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PersonTravelPolicyKeyfactorValue.findAll", query = "SELECT t FROM PersonTravelPolicyKeyfactorValue t") })
@EntityListeners(IDInterceptor.class)
public class PersonTravelPolicyKeyfactorValue implements Serializable {

	private static final long serialVersionUID = -4729536740693829292L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PERSONTRAVEL_POLICY_KEYFACTORVALUE_GEN")
	private String id;
	private String value;

	@OneToOne
	@JoinColumn(name = "KEYFACTORID", referencedColumnName = "ID")
	private KeyFactor keyfactor;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public PersonTravelPolicyKeyfactorValue() {
	}

	public PersonTravelPolicyKeyfactorValue(PersonTravelProposalKeyfactorValue keyfactorValue) {
		this.value = keyfactorValue.getValue();
		this.keyfactor = keyfactorValue.getKeyfactor();
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the keyfactor
	 */
	public KeyFactor getKeyfactor() {
		return keyfactor;
	}

	/**
	 * @param keyfactor
	 *            the keyfactor to set
	 */
	public void setKeyfactor(KeyFactor keyfactor) {
		this.keyfactor = keyfactor;
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
		result = prime * result + ((keyfactor == null) ? 0 : keyfactor.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		PersonTravelPolicyKeyfactorValue other = (PersonTravelPolicyKeyfactorValue) obj;
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
		if (keyfactor == null) {
			if (other.keyfactor != null)
				return false;
		} else if (!keyfactor.equals(other.keyfactor))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
