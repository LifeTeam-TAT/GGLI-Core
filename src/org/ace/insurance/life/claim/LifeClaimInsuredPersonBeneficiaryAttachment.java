package org.ace.insurance.life.claim;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.interfaces.IEntity;
import org.ace.java.component.FormatID;

/**
 * @author T&D Infomation System Ltd
 * @since 1.0.0
 * @date 2013/07/16
 */

@Entity
@Table(name = TableName.LIFECLAIM_INSUREDPERSON_BENEFICIARY_ATTACHMENT)
@TableGenerator(name = "LIFECLAIMINSUREDPERSONBENEFICIARYATTACHMENT_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFECLAIMINSUREDPERSONBENEFICIARYATTACHMENT_GEN", allocationSize = 10)
@NamedQueries(value = {
		@NamedQuery(name = "ClaimInsuredPersonBeneficiaryAttachment.findAll", query = "SELECT m FROM LifeClaimInsuredPersonBeneficiaryAttachment m "),
		@NamedQuery(name = "ClaimInsuredPersonBeneficiaryAttachment.findById", query = "SELECT m FROM LifeClaimInsuredPersonBeneficiaryAttachment m WHERE m.id = :id") })
@Access(value = AccessType.FIELD)
public class LifeClaimInsuredPersonBeneficiaryAttachment implements
		Serializable, IEntity {

	@Transient
	private String id;
	@Transient
	private String prefix;
	private String name;
	private String filePath;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLAIMINSUREDPERSONBENEFICIARYID", referencedColumnName = "ID")
	private LifeClaimInsuredPersonBeneficiary claimInsuredPersonBeneficiary;

	@Version
	private int version;

	public LifeClaimInsuredPersonBeneficiaryAttachment() {
	}

	public LifeClaimInsuredPersonBeneficiaryAttachment(String name,
			String filePath) {
		this.name = name;
		this.filePath = filePath;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFECLAIMINSUREDPERSONBENEFICIARYATTACHMENT_GEN")
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public LifeClaimInsuredPersonBeneficiary getClaimInsuredPersonBeneficiary() {
		return claimInsuredPersonBeneficiary;
	}

	public void setClaimInsuredPersonBeneficiary(
			LifeClaimInsuredPersonBeneficiary claimInsuredPersonBeneficiary) {
		this.claimInsuredPersonBeneficiary = claimInsuredPersonBeneficiary;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
		LifeClaimInsuredPersonBeneficiaryAttachment other = (LifeClaimInsuredPersonBeneficiaryAttachment) obj;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
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
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
}
