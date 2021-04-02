package org.ace.insurance.system.common.bancaLIC;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.utils.BancaStatus;
import org.ace.insurance.system.common.licBrmfo.LicBrmInfo;
import org.ace.insurance.system.common.licbranchinfo.LicBranchInfo;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.BANCA_LIC)
@TableGenerator(name = "BANCA_LIC_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "BANCA_LIC_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class BancaLIC implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "BANCA_LIC_GEN")
	private String id;
	private String name;
	private String staff_Id;
	private String licenseNo;

	@Temporal(TemporalType.DATE)
	private Date licenseExpireDate;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(TemporalType.DATE)
	private Date dob;

	private String phone;
	private String phone_viber;

	@Temporal(TemporalType.DATE)
	private Date gginl_joindate;

	@Temporal(TemporalType.DATE)
	private Date banca_joindate;

	@Temporal(TemporalType.DATE)
	private Date banca_enddate;

	private String cadre;
	private String banca_designation;

	@Enumerated(EnumType.STRING)
	private BancaStatus status;

	private String remark;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "BANCA_LIC_LICBRMINFO", joinColumns = { @JoinColumn(name = "BANCALICID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "LICBRMINFOID", referencedColumnName = "ID") })
	private List<LicBrmInfo> licBrmInfoList;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "BANCA_LIC_LICBRANCHINFO", joinColumns = { @JoinColumn(name = "BANCALICID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "LICBRANCHINFOID", referencedColumnName = "ID") })
	private List<LicBranchInfo> licBranchInfoList;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	private int version;

	public BancaLIC() {

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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone_viber() {
		return phone_viber;
	}

	public void setPhone_viber(String phone_viber) {
		this.phone_viber = phone_viber;
	}

	public Date getGginl_joindate() {
		return gginl_joindate;
	}

	public void setGginl_joindate(Date gginl_joindate) {
		this.gginl_joindate = gginl_joindate;
	}

	public Date getBanca_joindate() {
		return banca_joindate;
	}

	public void setBanca_joindate(Date banca_joindate) {
		this.banca_joindate = banca_joindate;
	}

	public Date getBanca_enddate() {
		return banca_enddate;
	}

	public void setBanca_enddate(Date banca_enddate) {
		this.banca_enddate = banca_enddate;
	}

	public String getCadre() {
		return cadre;
	}

	public void setCadre(String cadre) {
		this.cadre = cadre;
	}

	public String getBanca_designation() {
		return banca_designation;
	}

	public void setBanca_designation(String banca_designation) {
		this.banca_designation = banca_designation;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getStaff_Id() {
		return staff_Id;
	}

	public void setStaff_Id(String staff_Id) {
		this.staff_Id = staff_Id;
	}

	public BancaStatus getStatus() {
		return status;
	}

	public void setStatus(BancaStatus status) {
		this.status = status;
	}

	public List<LicBrmInfo> getLicBrmInfoList() {
		if (licBrmInfoList == null) {
			licBrmInfoList = new ArrayList<LicBrmInfo>();
		}
		return licBrmInfoList;
	}

	public void setLicBrmInfoList(List<LicBrmInfo> licBrmInfoList) {
		this.licBrmInfoList = licBrmInfoList;
	}

	public List<LicBranchInfo> getLicBranchInfoList() {
		if (licBranchInfoList == null) {
			licBranchInfoList = new ArrayList<LicBranchInfo>();
		}
		return licBranchInfoList;
	}

	public void setLicBranchInfoList(List<LicBranchInfo> licBranchInfoList) {
		this.licBranchInfoList = licBranchInfoList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((banca_designation == null) ? 0 : banca_designation.hashCode());
		result = prime * result + ((banca_enddate == null) ? 0 : banca_enddate.hashCode());
		result = prime * result + ((banca_joindate == null) ? 0 : banca_joindate.hashCode());
		result = prime * result + ((cadre == null) ? 0 : cadre.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((gginl_joindate == null) ? 0 : gginl_joindate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((licBranchInfoList == null) ? 0 : licBranchInfoList.hashCode());
		result = prime * result + ((licBrmInfoList == null) ? 0 : licBrmInfoList.hashCode());
		result = prime * result + ((licenseExpireDate == null) ? 0 : licenseExpireDate.hashCode());
		result = prime * result + ((licenseNo == null) ? 0 : licenseNo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((phone_viber == null) ? 0 : phone_viber.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((staff_Id == null) ? 0 : staff_Id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		BancaLIC other = (BancaLIC) obj;
		if (banca_designation == null) {
			if (other.banca_designation != null)
				return false;
		} else if (!banca_designation.equals(other.banca_designation))
			return false;
		if (banca_enddate == null) {
			if (other.banca_enddate != null)
				return false;
		} else if (!banca_enddate.equals(other.banca_enddate))
			return false;
		if (banca_joindate == null) {
			if (other.banca_joindate != null)
				return false;
		} else if (!banca_joindate.equals(other.banca_joindate))
			return false;
		if (cadre == null) {
			if (other.cadre != null)
				return false;
		} else if (!cadre.equals(other.cadre))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (gender != other.gender)
			return false;
		if (gginl_joindate == null) {
			if (other.gginl_joindate != null)
				return false;
		} else if (!gginl_joindate.equals(other.gginl_joindate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (licBranchInfoList == null) {
			if (other.licBranchInfoList != null)
				return false;
		} else if (!licBranchInfoList.equals(other.licBranchInfoList))
			return false;
		if (licBrmInfoList == null) {
			if (other.licBrmInfoList != null)
				return false;
		} else if (!licBrmInfoList.equals(other.licBrmInfoList))
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
		if (phone_viber == null) {
			if (other.phone_viber != null)
				return false;
		} else if (!phone_viber.equals(other.phone_viber))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (staff_Id == null) {
			if (other.staff_Id != null)
				return false;
		} else if (!staff_Id.equals(other.staff_Id))
			return false;
		if (status != other.status)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
