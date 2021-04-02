package org.ace.insurance.system.common.bancaRefferal;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.utils.BancaStatus;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.BANCA_REFFERAL)
@TableGenerator(name = "BANCA_REFFERAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "BANCA_REFFERAL_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class BancaRefferal implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "BANCA_REFFERAL_GEN")
	private String id;

	private String name;

	private String codeNumber;

	private String licenseNo;

	@Temporal(TemporalType.DATE)
	private Date licenseExpireDate;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(TemporalType.DATE)
	private Date startdate;

	@Temporal(TemporalType.DATE)
	private Date enddate;

	@Enumerated(EnumType.STRING)
	private BancaStatus status;

	private String remark;

	private String idNo;

	@Enumerated(value = EnumType.STRING)
	private IdConditionType idConditionType;

	@Enumerated(value = EnumType.STRING)
	private IdType idType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NATIONALITYID", referencedColumnName = "ID")
	private Country country;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATECODEID", referencedColumnName = "ID")
	private StateCode stateCode;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOWNSHIPCODEID", referencedColumnName = "ID")
	private TownshipCode townshipCode;

	@Transient
	private String fullIdNo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private BancaBranch bancaBranch;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	private int version;

	private String phone;

	public BancaRefferal() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCodeNumber() {
		return codeNumber;
	}

	public void setCodeNumber(String codeNumber) {
		this.codeNumber = codeNumber;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public BancaStatus getStatus() {
		return status;
	}

	public void setStatus(BancaStatus status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public Date getLicenseExpireDate() {
		return licenseExpireDate;
	}

	public void setLicenseExpireDate(Date licenseExpireDate) {
		this.licenseExpireDate = licenseExpireDate;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public BancaBranch getBancaBranch() {
		return bancaBranch;
	}

	public void setBancaBranch(BancaBranch bancaBranch) {
		this.bancaBranch = bancaBranch;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public IdConditionType getIdConditionType() {
		return idConditionType;
	}

	public void setIdConditionType(IdConditionType idConditionType) {
		this.idConditionType = idConditionType;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public StateCode getStateCode() {
		return stateCode;
	}

	public void setStateCode(StateCode stateCode) {
		this.stateCode = stateCode;
	}

	public TownshipCode getTownshipCode() {
		return townshipCode;
	}

	public void setTownshipCode(TownshipCode townshipCode) {
		this.townshipCode = townshipCode;
	}

	public void setFullIdNo(String fullIdNo) {
		this.fullIdNo = fullIdNo;
	}

	@Access(value = AccessType.PROPERTY)
	public String getFullIdNo() {
		String result = "";
		if (!IdType.STILL_APPLYING.equals(idType)) {
			if (stateCode != null) {
				result = result + stateCode.getCodeNo() + "/";
			}
			if (townshipCode != null) {
				result = result + townshipCode.getTownshipcodeno();
			}
			if (idConditionType != null) {
				result = result + "(" + idConditionType.getLabel() + ")";
			}
			if (result.isEmpty() && idNo != null) {
				return idNo;
			} else {
				return result + idNo;
			}
		} else
			return null;
	}

	public String setFullIdNo() {
		if (idType.equals(IdType.NRCNO)) {
			fullIdNo = stateCode.getCodeNo() + "/" + townshipCode.getStateCode() + "(" + idConditionType + ")" + idNo;
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO)) {
			fullIdNo = idNo;
		}
		return fullIdNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bancaBranch == null) ? 0 : bancaBranch.hashCode());
		result = prime * result + ((codeNumber == null) ? 0 : codeNumber.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((enddate == null) ? 0 : enddate.hashCode());
		result = prime * result + ((fullIdNo == null) ? 0 : fullIdNo.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idConditionType == null) ? 0 : idConditionType.hashCode());
		result = prime * result + ((idNo == null) ? 0 : idNo.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
		result = prime * result + ((licenseExpireDate == null) ? 0 : licenseExpireDate.hashCode());
		result = prime * result + ((licenseNo == null) ? 0 : licenseNo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((startdate == null) ? 0 : startdate.hashCode());
		result = prime * result + ((stateCode == null) ? 0 : stateCode.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((townshipCode == null) ? 0 : townshipCode.hashCode());
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
		BancaRefferal other = (BancaRefferal) obj;
		if (bancaBranch == null) {
			if (other.bancaBranch != null)
				return false;
		} else if (!bancaBranch.equals(other.bancaBranch))
			return false;
		if (codeNumber == null) {
			if (other.codeNumber != null)
				return false;
		} else if (!codeNumber.equals(other.codeNumber))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (enddate == null) {
			if (other.enddate != null)
				return false;
		} else if (!enddate.equals(other.enddate))
			return false;
		if (fullIdNo == null) {
			if (other.fullIdNo != null)
				return false;
		} else if (!fullIdNo.equals(other.fullIdNo))
			return false;
		if (gender != other.gender)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idConditionType != other.idConditionType)
			return false;
		if (idNo == null) {
			if (other.idNo != null)
				return false;
		} else if (!idNo.equals(other.idNo))
			return false;
		if (idType != other.idType)
			return false;
		if (licenseExpireDate == null) {
			if (other.licenseExpireDate != null)
				return false;
		} else if (!licenseExpireDate.equals(other.licenseExpireDate))
			return false;
		if (licenseNo == null) {
			if (other.licenseNo != null)
				return false;
		} else if (!licenseNo.equals(other.licenseNo))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (startdate == null) {
			if (other.startdate != null)
				return false;
		} else if (!startdate.equals(other.startdate))
			return false;
		if (stateCode == null) {
			if (other.stateCode != null)
				return false;
		} else if (!stateCode.equals(other.stateCode))
			return false;
		if (status != other.status)
			return false;
		if (townshipCode == null) {
			if (other.townshipCode != null)
				return false;
		} else if (!townshipCode.equals(other.townshipCode))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
