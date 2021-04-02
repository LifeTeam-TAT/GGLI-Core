/***************************************************************************************
 * @author <<>>
 * @Date 
 * @Version 1.0
 * @Purpose <<>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.life.proposal;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.LIFESURVEY)
@TableGenerator(name = "LIFESURVEY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFESURVEY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "LifeSurvey.findAll", query = "SELECT s FROM LifeSurvey s "),
		@NamedQuery(name = "LifeSurvey.findById", query = "SELECT s FROM LifeSurvey s WHERE s.id = :id") })
@Access(value = AccessType.FIELD)
public class LifeSurvey implements Serializable {
	@Transient
	private String id;
	@Transient
	private String prefix;

	@Column(name = "SURVEYDATE")
	@Temporal(TemporalType.DATE)
	private Date date;

	// TO DELETE
	@Column(name = "PLACEOFMEDICALCHECKUP")
	private String placeOfMedicalCheckUp;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HOSPITALID", referencedColumnName = "ID")
	private Hospital hospital;

	@Column(name = "MEDICALOFFICERNAME")
	private String medicalOfficerName;

	@Column(name = "RANKANDQUALIFICATION")
	private String rankAndQualification;

	@Column(name = "BOARDORNOT")
	private boolean boardOrNot;

	@Column(name = "ADDRESS")
	private String address;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOWNSHIPID", referencedColumnName = "ID")
	private Township township;

	@Column(name = "REMARK")
	private String remark;

	@OneToOne
	@JoinColumn(name = "LIFEPROPOSALID", referencedColumnName = "ID")
	private LifeProposal lifeProposal;

	@Version
	private int version;

	public LifeSurvey() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFESURVEY_GEN")
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public String getPlaceOfMedicalCheckUp() {
		return placeOfMedicalCheckUp;
	}

	public void setPlaceOfMedicalCheckUp(String placeOfMedicalCheckUp) {
		this.placeOfMedicalCheckUp = placeOfMedicalCheckUp;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public String getMedicalOfficerName() {
		return medicalOfficerName;
	}

	public void setMedicalOfficerName(String medicalOfficerName) {
		this.medicalOfficerName = medicalOfficerName;
	}

	public String getRankAndQualification() {
		return rankAndQualification;
	}

	public void setRankAndQualification(String rankAndQualification) {
		this.rankAndQualification = rankAndQualification;
	}

	public boolean isBoardOrNot() {
		return boardOrNot;
	}

	public void setBoardOrNot(boolean boardOrNot) {
		this.boardOrNot = boardOrNot;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Township getTownship() {
		return township;
	}

	public void setTownship(Township township) {
		this.township = township;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + (boardOrNot ? 1231 : 1237);
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((medicalOfficerName == null) ? 0 : medicalOfficerName.hashCode());
		result = prime * result + ((placeOfMedicalCheckUp == null) ? 0 : placeOfMedicalCheckUp.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((rankAndQualification == null) ? 0 : rankAndQualification.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
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
		LifeSurvey other = (LifeSurvey) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (boardOrNot != other.boardOrNot)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (medicalOfficerName == null) {
			if (other.medicalOfficerName != null)
				return false;
		} else if (!medicalOfficerName.equals(other.medicalOfficerName))
			return false;
		if (placeOfMedicalCheckUp == null) {
			if (other.placeOfMedicalCheckUp != null)
				return false;
		} else if (!placeOfMedicalCheckUp.equals(other.placeOfMedicalCheckUp))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (rankAndQualification == null) {
			if (other.rankAndQualification != null)
				return false;
		} else if (!rankAndQualification.equals(other.rankAndQualification))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
