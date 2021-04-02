package org.ace.insurance.life.policyLog;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.java.component.FormatID;
@Entity
@Table(name=TableName.LIFE_POLICY_CLAIM_LOG)
@TableGenerator(name = "LIFEPOLICYCLAIMLOG_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPOLICYCLAIMLOG_GEN", allocationSize = 10)
@Access(value = AccessType.FIELD)
public class LifePolicyClaimLog {
	@Transient
	private String id;
	@Transient
	private String prefix;
	@Column(name="CLAIMPROPOSALDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date claimProposalDate;
	@Column(name="CLAIMCONFIRMDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date claimConfirmDate;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFECLAIMID", referencedColumnName = "ID")
	private LifeClaim lifeClaim;
	@ManyToOne(cascade = CascadeType.ALL , fetch = FetchType.LAZY )
	@JoinColumn(name = "LIFEPOLICYTIMELINELOGID" , referencedColumnName ="ID" )
	private LifePolicyTimeLineLog lifePolicyTimeLineLog;
	@Version
	private int version;
	
	public LifePolicyClaimLog() {
		
	}
	
	public LifePolicyClaimLog( Date claimProposalDate, Date claimConfirmDate,
			LifeClaim lifeClaim, LifePolicyTimeLineLog lifePolicyTimeLineLog) {
		this.claimProposalDate = claimProposalDate;
		this.claimConfirmDate = claimConfirmDate;
		this.lifeClaim = lifeClaim;
		this.lifePolicyTimeLineLog = lifePolicyTimeLineLog;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPOLICYCLAIMLOG_GEN")
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

	public Date getClaimProposalDate() {
		return claimProposalDate;
	}

	public void setClaimProposalDate(Date claimProposalDate) {
		this.claimProposalDate = claimProposalDate;
	}

	public Date getClaimConfirmDate() {
		return claimConfirmDate;
	}

	public void setClaimConfirmDate(Date claimConfirmDate) {
		this.claimConfirmDate = claimConfirmDate;
	}

	public LifeClaim getLifeClaim() {
		return lifeClaim;
	}

	public void setLifeClaim(LifeClaim lifeClaim) {
		this.lifeClaim = lifeClaim;
	}

	public LifePolicyTimeLineLog getLifePolicyTimeLineLog() {
		return lifePolicyTimeLineLog;
	}

	public void setLifePolicyTimeLineLog(LifePolicyTimeLineLog lifePolicyTimeLineLog) {
		this.lifePolicyTimeLineLog = lifePolicyTimeLineLog;
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
		result = prime * result + ((claimConfirmDate == null) ? 0 : claimConfirmDate.hashCode());
		result = prime * result + ((claimProposalDate == null) ? 0 : claimProposalDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		LifePolicyClaimLog other = (LifePolicyClaimLog) obj;
		if (claimConfirmDate == null) {
			if (other.claimConfirmDate != null)
				return false;
		} else if (!claimConfirmDate.equals(other.claimConfirmDate))
			return false;
		if (claimProposalDate == null) {
			if (other.claimProposalDate != null)
				return false;
		} else if (!claimProposalDate.equals(other.claimProposalDate))
			return false;
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
		if (version != other.version)
			return false;
		return true;
	}

}
