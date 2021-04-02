package org.ace.insurance.life.claim;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.LIFEPOLICYCLAIM)
@TableGenerator(name = "LIFEPOLICYCLAIM_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPOLICYCLAIM_GEN", allocationSize = 10)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CLAIMTYPE", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(IDInterceptor.class)
public class LifePolicyClaim implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPOLICYCLAIM_GEN")
	private String id;
	@Column(name = "APPROVE")
	private boolean isApproved;

	@Column(name = "REJECTREASON")
	private String rejectReason;

	@Column(name = "CLAIMTYPE", insertable = false, updatable = false)
	private String claimType;

	@Version
	private int version;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	public LifePolicyClaim() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((claimType == null) ? 0 : claimType.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isApproved ? 1231 : 1237);
		result = prime * result + ((rejectReason == null) ? 0 : rejectReason.hashCode());
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
		LifePolicyClaim other = (LifePolicyClaim) obj;
		if (claimType == null) {
			if (other.claimType != null)
				return false;
		} else if (!claimType.equals(other.claimType))
			return false;
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
		if (isApproved != other.isApproved)
			return false;
		if (rejectReason == null) {
			if (other.rejectReason != null)
				return false;
		} else if (!rejectReason.equals(other.rejectReason))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
