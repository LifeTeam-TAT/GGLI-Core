package org.ace.insurance.life.policyEditHistory;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
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
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.life.policy.PolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.LIFEPOLICYINSUREDPERSONKEYFACTOREDITHISTORY)
@TableGenerator(name = "LPOLINSUREDPERSONKEYFACTOR_EDITHISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LPOLINSUREDPERSONKEYFACTOR_EDITHISTORY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PolicyInsuredPersonKeyFactorValueEditHistory.findAll", query = "SELECT v FROM PolicyInsuredPersonKeyFactorValueEditHistory v "),
		@NamedQuery(name = "PolicyInsuredPersonKeyFactorValueEditHistory.findById", query = "SELECT v FROM PolicyInsuredPersonKeyFactorValueEditHistory v WHERE v.id = :id") })
@Access(value = AccessType.FIELD)
public class PolicyInsuredPersonKeyFactorValueEditHistory {
	@Transient
	private String id;
	@Transient
	private String prefix;

	private String value;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KEYFACTORID", referencedColumnName = "ID")
	private KeyFactor keyFactor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPOLICYINSUREDPERSONID", referencedColumnName = "ID")
	private PolicyInsuredPersonEditHistory policyInsuredPerson;

	@Version
	private int version;

	public PolicyInsuredPersonKeyFactorValueEditHistory() {
	}

	public PolicyInsuredPersonKeyFactorValueEditHistory(KeyFactor keyFactor) {
		this.keyFactor = keyFactor;
	}

	public PolicyInsuredPersonKeyFactorValueEditHistory(InsuredPersonKeyFactorValue keyFactorValue) {
		this.value = keyFactorValue.getValue();
		this.keyFactor = keyFactorValue.getKeyFactor();
	}

	public PolicyInsuredPersonKeyFactorValueEditHistory(PolicyInsuredPersonKeyFactorValue keyFactorValue) {
		this.value = keyFactorValue.getValue();
		this.keyFactor = keyFactorValue.getKeyFactor();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LPOLINSUREDPERSONKEYFACTOR_EDITHISTORY_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public KeyFactor getKeyFactor() {
		return this.keyFactor;
	}

	public void setKeyFactor(KeyFactor keyFactor) {
		this.keyFactor = keyFactor;
	}

	public PolicyInsuredPersonEditHistory getPolicyInsuredPerson() {
		return policyInsuredPerson;
	}

	public void setPolicyInsuredPerson(PolicyInsuredPersonEditHistory policyInsuredPerson) {
		this.policyInsuredPerson = policyInsuredPerson;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public PolicyInsuredPersonEditHistory getPolicyInsuredPersonInfo() {
		return policyInsuredPerson;
	}

	public void setPolicyInsuredPersonInfo(PolicyInsuredPersonEditHistory policyInsuredPerson) {
		this.policyInsuredPerson = policyInsuredPerson;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + version;
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
		PolicyInsuredPersonKeyFactorValueEditHistory other = (PolicyInsuredPersonKeyFactorValueEditHistory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
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
