package org.ace.insurance.system.common.licbranchinfo;

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
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.LIC_BRANCH_INFO)
@TableGenerator(name = "LIC_BRANCH_INFO_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIC_BRANCH_INFO_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class LicBranchInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIC_BRANCH_INFO_GEN")
	private String id;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;

	@Enumerated(EnumType.STRING)
	private BancaStatus status;

	private String remark;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BACABRANCHID", referencedColumnName = "ID")
	private BancaBranch bancaBranch;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Transient
	private String tempId;

	private int version;

	@Transient
	private boolean disable;

	public LicBranchInfo() {
		tempId = System.nanoTime() + "";
	}

	public LicBranchInfo(LicBranchInfo licBranchInfo) {
		this.id = licBranchInfo.getId();
		this.startDate = licBranchInfo.getStartDate();
		this.endDate = licBranchInfo.getEndDate();
		this.status = licBranchInfo.getStatus();
		this.remark = licBranchInfo.getRemark();
		this.bancaBranch = licBranchInfo.getBancaBranch();
		this.commonCreateAndUpateMarks = licBranchInfo.getCommonCreateAndUpateMarks();
		this.tempId = licBranchInfo.getTempId();
		this.version = licBranchInfo.getVersion();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public BancaBranch getBancaBranch() {
		return bancaBranch;
	}

	public void setBancaBranch(BancaBranch bancaBranch) {
		this.bancaBranch = bancaBranch;
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
