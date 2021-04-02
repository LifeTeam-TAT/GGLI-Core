package org.ace.insurance.medical.claim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.icd10.ICD10;

@Entity
@Table(name = TableName.DEATHCLAIM)
@DiscriminatorValue(value = MedicalClaimRole.DEATH_CLAIM)
@NamedQueries(value = { @NamedQuery(name = "DeathClaim.findAll", query = "SELECT d FROM DeathClaim d "),
		@NamedQuery(name = "DeathClaim.findById", query = "SELECT d FROM DeathClaim d WHERE d.id = :id") })
public class DeathClaim extends MedicalClaim implements Serializable {

	private static final long serialVersionUID = -21285200232975612L;

	@Column(name = "DEATH_PLACE")
	private String deathPlace;

	@Column(name = "OTHER_PLACE")
	private String otherPlace;

	@Column(name = "DEATH_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deathDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
	private List<Attachment> attachmentList;

	@Column(name = "DEATH_CLAIM_AMT")
	private double deathClaimAmount;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEATHREASONID", referencedColumnName = "ID")
	private ICD10 deathReason;

	private String reason;

	public DeathClaim() {
		super();
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public double getDeathClaimAmount() {
		return deathClaimAmount;
	}

	public void setDeathClaimAmount(double deathClaimAmount) {
		this.deathClaimAmount = deathClaimAmount;
	}

	public String getDeathPlace() {
		return deathPlace;
	}

	public void setDeathPlace(String deathPlace) {
		this.deathPlace = deathPlace;
	}

	public String getOtherPlace() {
		return otherPlace;
	}

	public void setOtherPlace(String otherPlace) {
		this.otherPlace = otherPlace;
	}

	public ICD10 getDeathReason() {
		return deathReason;
	}

	public void setDeathReason(ICD10 deathReason) {
		this.deathReason = deathReason;
	}

	public void addAttachment(Attachment attachment) {
		if (attachmentList == null) {
			attachmentList = new ArrayList<Attachment>();
		}
		attachmentList.add(attachment);
	}

	public List<Attachment> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<Attachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<Attachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(deathClaimAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((deathDate == null) ? 0 : deathDate.hashCode());
		result = prime * result + ((deathPlace == null) ? 0 : deathPlace.hashCode());
		result = prime * result + ((otherPlace == null) ? 0 : otherPlace.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
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
		DeathClaim other = (DeathClaim) obj;
		if (Double.doubleToLongBits(deathClaimAmount) != Double.doubleToLongBits(other.deathClaimAmount))
			return false;
		if (deathDate == null) {
			if (other.deathDate != null)
				return false;
		} else if (!deathDate.equals(other.deathDate))
			return false;
		if (deathPlace == null) {
			if (other.deathPlace != null)
				return false;
		} else if (!deathPlace.equals(other.deathPlace))
			return false;
		if (otherPlace == null) {
			if (other.otherPlace != null)
				return false;
		} else if (!otherPlace.equals(other.otherPlace))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		return true;
	}

}
