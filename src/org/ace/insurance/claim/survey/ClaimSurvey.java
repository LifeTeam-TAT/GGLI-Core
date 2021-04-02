package org.ace.insurance.claim.survey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.interfaces.IEntity;
import org.ace.java.component.FormatID;

/**
 * Entity defining the abstract nature and behavior of <code>Claim Survey</code>
 * object.
 * 
 * @author Ace Plus
 * @since 1.0.0
 * @date 2013/06/27
 */
@Entity
@Table(name = "CLAIMSURVEY")
// TODO: to replace "CLAIMSURVEY" with TableName.CLAIMSURVEY after updating
// TableName with Claim Survey db table name
@TableGenerator(name = "CLAIMSURVEY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "CLAIMSURVEY_GEN", allocationSize = 10)
@Access(value = AccessType.FIELD)
public class ClaimSurvey implements Serializable, IEntity {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;

	private String prefix;

	@Column(name = "SURVEYTEAM")
	private String surveyTeam;

	@Column(name = "SURVEYPLACE")
	private String surveyPlace;

	@Column(name = "SURVEYDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date surveyDate;

	@Column(name = "SPECIALCASE")
	private String specialCase;

	@Column(name = "SUPPORTBYCOMPANY")
	private boolean supportByCompany;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
	private List<Attachment> attachmentList;

	@Version
	private int version;

	// ------------------------------ Constructors
	// ------------------------------

	public ClaimSurvey() {
		this(null, null, null, null);
	}

	public ClaimSurvey(String surveyTeam, String surveyPlace, Date surveyDate, String specialCase) {
		this(surveyTeam, surveyPlace, surveyDate, specialCase, null);
	}

	public ClaimSurvey(String surveyTeam, String surveyPlace, Date surveyDate, String specialCase, List<Attachment> attachmentList) {
		this.id = null;
		this.prefix = null;
		this.surveyTeam = surveyTeam;
		this.surveyPlace = surveyPlace;
		this.surveyDate = surveyDate;
		this.specialCase = specialCase;
		this.attachmentList = attachmentList;
		this.version = 0;
	}

	// ------------------------------ Accessors and Mutators
	// ------------------------------
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "CLAIMSURVEY_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		// String prefix = "ISMOT042";
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public boolean isSupportByCompany() {
		return supportByCompany;
	}

	public void setSupportByCompany(boolean supportByCompany) {
		this.supportByCompany = supportByCompany;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSurveyTeam() {
		return surveyTeam;
	}

	public void setSurveyTeam(String surveyTeam) {
		this.surveyTeam = surveyTeam;
	}

	public String getSurveyPlace() {
		return surveyPlace;
	}

	public void setSurveyPlace(String surveyPlace) {
		this.surveyPlace = surveyPlace;
	}

	public Date getSurveyDate() {
		return surveyDate;
	}

	public void setSurveyDate(Date surveyDate) {
		this.surveyDate = surveyDate;
	}

	public String getSpecialCase() {
		return specialCase;
	}

	public void setSpecialCase(String specialCase) {
		this.specialCase = specialCase;
	}

	public List<Attachment> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<Attachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<Attachment> attachmentList) {
		this.attachmentList = null;
		appendAttachments(attachmentList);
	}

	// ------------------------------ Overriden and Utility Methods
	// ------------------------------

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void addAttachment(String name, String filePath) {
		addAttachment(new Attachment(name, filePath));
	}

	public void addAttachment(Attachment attachment) {
		if (attachmentList == null) {
			attachmentList = new ArrayList<Attachment>();
		}
		attachmentList.add(attachment);
	}

	public void removeAttachment(Attachment attachment) {
		if (attachmentList != null) {
			attachmentList.remove(attachment);
		}
	}

	public void appendAttachments(List<Attachment> attachments) {
		if (attachments != null) {
			for (Attachment attachment : attachments) {
				addAttachment(attachment);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((specialCase == null) ? 0 : specialCase.hashCode());
		result = prime * result + ((surveyDate == null) ? 0 : surveyDate.hashCode());
		result = prime * result + ((surveyPlace == null) ? 0 : surveyPlace.hashCode());
		result = prime * result + ((surveyTeam == null) ? 0 : surveyTeam.hashCode());
		result = prime * result + version;
		result = prime * result + (supportByCompany ? 1231 : 1237);
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
		ClaimSurvey other = (ClaimSurvey) obj;
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
		if (specialCase == null) {
			if (other.specialCase != null)
				return false;
		} else if (!specialCase.equals(other.specialCase))
			return false;
		if (surveyDate == null) {
			if (other.surveyDate != null)
				return false;
		} else if (!surveyDate.equals(other.surveyDate))
			return false;
		if (surveyPlace == null) {
			if (other.surveyPlace != null)
				return false;
		} else if (!surveyPlace.equals(other.surveyPlace))
			return false;
		if (surveyTeam == null) {
			if (other.surveyTeam != null)
				return false;
		} else if (!surveyTeam.equals(other.surveyTeam))
			return false;
		if (version != other.version)
			return false;
		if (supportByCompany != other.supportByCompany)
			return false;
		return true;
	}

}
