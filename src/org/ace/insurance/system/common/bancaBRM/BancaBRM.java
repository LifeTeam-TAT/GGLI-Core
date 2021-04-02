package org.ace.insurance.system.common.bancaBRM;

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
import org.ace.insurance.system.common.brmbranchinfo.BrmBranchInfo;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.BANCA_BRM)
@TableGenerator(name = "BANCA_BRM_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "BANCA_BRM_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class BancaBRM implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "BANCA_BRM_GEN")
	private String id;
	private String name;
	private String staffId;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@Temporal(TemporalType.DATE)
	private Date dob;
	private String phone;
	private String phoneViber;

	@Temporal(TemporalType.DATE)
	private Date gginlJoindate;

	@Temporal(TemporalType.DATE)
	private Date bancaJoindate;

	@Temporal(TemporalType.DATE)
	private Date bancaEnddate;

	private String cadre;
	private String bancaDesignation;

	@Enumerated(EnumType.STRING)
	private BancaStatus status;

	private String remark;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "BANCA_BRM_BRMBRANCHINFO", joinColumns = { @JoinColumn(name = "BANCA_BRM_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "BRMBRANCHINFOID", referencedColumnName = "ID") })
	private List<BrmBranchInfo> brmBranchInfoList;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	private int version;

	public BancaBRM() {

	}

	public BancaBRM(String id, String name, String staffId, Gender gender, Date dob, String phone, String phoneViber, Date gginlJoindate, Date bancaJoindate, Date bancaEnddate,
			String cadre, String bancaDesignation, BancaStatus status, String remark, List<BrmBranchInfo> brmBranchInfoList, CommonCreateAndUpateMarks commonCreateAndUpateMarks,
			int version) {
		this.id = id;
		this.name = name;
		this.staffId = staffId;
		this.gender = gender;
		this.dob = dob;
		this.phone = phone;
		this.phoneViber = phoneViber;
		this.gginlJoindate = gginlJoindate;
		this.bancaJoindate = bancaJoindate;
		this.bancaEnddate = bancaEnddate;
		this.cadre = cadre;
		this.bancaDesignation = bancaDesignation;
		this.status = status;
		this.remark = remark;
		// this.brmBranchInfoList = brmBranchInfoList;
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
		this.version = version;
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

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
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

	public BancaStatus getStatus() {
		return status;
	}

	public void setStatus(BancaStatus status) {
		this.status = status;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoneViber() {
		return phoneViber;
	}

	public void setPhoneViber(String phoneViber) {
		this.phoneViber = phoneViber;
	}

	public Date getGginlJoindate() {
		return gginlJoindate;
	}

	public void setGginlJoindate(Date gginlJoindate) {
		this.gginlJoindate = gginlJoindate;
	}

	public Date getBancaJoindate() {
		return bancaJoindate;
	}

	public void setBancaJoindate(Date bancaJoindate) {
		this.bancaJoindate = bancaJoindate;
	}

	public Date getBancaEnddate() {
		return bancaEnddate;
	}

	public void setBancaEnddate(Date bancaEnddate) {
		this.bancaEnddate = bancaEnddate;
	}

	public String getCadre() {
		return cadre;
	}

	public void setCadre(String cadre) {
		this.cadre = cadre;
	}

	public String getBancaDesignation() {
		return bancaDesignation;
	}

	public void setBancaDesignation(String bancaDesignation) {
		this.bancaDesignation = bancaDesignation;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<BrmBranchInfo> getBrmBranchInfoList() {
		if (brmBranchInfoList == null) {
			brmBranchInfoList = new ArrayList<BrmBranchInfo>();
		}
		return brmBranchInfoList;
	}

	public void setBrmBranchInfoList(List<BrmBranchInfo> brmBranchInfoList) {
		this.brmBranchInfoList = brmBranchInfoList;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bancaDesignation == null) ? 0 : bancaDesignation.hashCode());
		result = prime * result + ((bancaEnddate == null) ? 0 : bancaEnddate.hashCode());
		result = prime * result + ((bancaJoindate == null) ? 0 : bancaJoindate.hashCode());
		// result = prime * result + ((brmBranchInfoList == null) ? 0 :
		// brmBranchInfoList.hashCode());
		result = prime * result + ((cadre == null) ? 0 : cadre.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((gginlJoindate == null) ? 0 : gginlJoindate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((phoneViber == null) ? 0 : phoneViber.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((staffId == null) ? 0 : staffId.hashCode());
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
		BancaBRM other = (BancaBRM) obj;
		if (bancaDesignation == null) {
			if (other.bancaDesignation != null)
				return false;
		} else if (!bancaDesignation.equals(other.bancaDesignation))
			return false;
		if (bancaEnddate == null) {
			if (other.bancaEnddate != null)
				return false;
		} else if (!bancaEnddate.equals(other.bancaEnddate))
			return false;
		if (bancaJoindate == null) {
			if (other.bancaJoindate != null)
				return false;
		} else if (!bancaJoindate.equals(other.bancaJoindate))
			return false;
		/*
		 * if (brmBranchInfoList == null) { if (other.brmBranchInfoList != null)
		 * return false; } else if
		 * (!brmBranchInfoList.equals(other.brmBranchInfoList)) return false;
		 */
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
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (gginlJoindate == null) {
			if (other.gginlJoindate != null)
				return false;
		} else if (!gginlJoindate.equals(other.gginlJoindate))
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
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (phoneViber == null) {
			if (other.phoneViber != null)
				return false;
		} else if (!phoneViber.equals(other.phoneViber))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (staffId == null) {
			if (other.staffId != null)
				return false;
		} else if (!staffId.equals(other.staffId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
