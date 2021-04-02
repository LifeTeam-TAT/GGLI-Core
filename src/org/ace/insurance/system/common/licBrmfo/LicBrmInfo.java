package org.ace.insurance.system.common.licBrmfo;

import java.io.Serializable;
import java.util.Date;

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
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.utils.BancaStatus;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.BANCA_LIC_BRM)
@TableGenerator(name = "LIC_BRM_INFO_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIC_BRM_INFO_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class LicBrmInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIC_BRM_INFO_GEN")
	private String id;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;

	@Enumerated(EnumType.STRING)
	private BancaStatus status;

	private String remark;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BANCABRMID", referencedColumnName = "ID")
	private BancaBRM bancaBrm;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Transient
	private String tempId;

	private int version;

	@Transient
	private boolean disable;

	public LicBrmInfo() {
		tempId = System.nanoTime() + "";
	}

	public LicBrmInfo(LicBrmInfo licBrmInfo) {
		this.id = licBrmInfo.getId();
		this.startDate = licBrmInfo.getStartDate();
		this.endDate = licBrmInfo.getEndDate();
		this.status = licBrmInfo.getStatus();
		this.remark = licBrmInfo.getRemark();
		this.bancaBrm = licBrmInfo.getBancaBrm();
		this.commonCreateAndUpateMarks = licBrmInfo.getCommonCreateAndUpateMarks();
		this.tempId = licBrmInfo.getTempId();
		this.version = licBrmInfo.getVersion();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public BancaBRM getBancaBrm() {
		return bancaBrm;
	}

	public void setBancaBrm(BancaBRM bancaBrm) {
		this.bancaBrm = bancaBrm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bancaBrm == null) ? 0 : bancaBrm.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		LicBrmInfo other = (LicBrmInfo) obj;
		if (bancaBrm == null) {
			if (other.bancaBrm != null)
				return false;
		} else if (!bancaBrm.equals(other.bancaBrm))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status != other.status)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

}
