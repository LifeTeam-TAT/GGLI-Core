package org.ace.insurance.workflow;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.User;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.WORKFLOW_HIST)
@TableGenerator(name = "WORKFLOW_HIST_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "WORKFLOW_HIST_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "WorkFlowHistory.findAll", query = "SELECT a FROM WorkFlowHistory a "),
		@NamedQuery(name = "WorkFlowHistory.findById", query = "SELECT a FROM WorkFlowHistory a WHERE a.id = :id") })
@Access(value = AccessType.FIELD)
public class WorkFlowHistory implements Serializable {
	@Transient
	private String id;
	@Transient
	private String prefix;
	private String referenceNo;
	private String remark;

	@Column(name = "WORKFLOWDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date workflowDate;

	@Enumerated(value = EnumType.STRING)
	private WorkflowTask workflowTask;

	@Enumerated(EnumType.STRING)
	private WorkflowReferenceType referenceType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REQUESTORID", referencedColumnName = "ID")
	private User createdUser;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESPONSIBLEUSERID", referencedColumnName = "ID")
	private User responsiblePerson;

	private boolean deleteFlag;

	@Version
	private int version;

	public WorkFlowHistory() {
	}

	public WorkFlowHistory(WorkFlowDTO workFlowDTO) {
		this.referenceNo = workFlowDTO.getReferenceNo();
		this.remark = workFlowDTO.getRemark();
		this.workflowTask = workFlowDTO.getWorkflowTask();
		this.referenceType = workFlowDTO.getReferenceType();
		this.createdUser = workFlowDTO.getCreatedUser();
		this.responsiblePerson = workFlowDTO.getResponsiblePerson();
		workflowDate = new Date();
	}

	public WorkFlowHistory(String referenceNo, String remark, WorkflowTask workflowTask, WorkflowReferenceType referenceType, User createdUser, User responsibleUser,
			Date todayDate) {
		this.referenceNo = referenceNo;
		this.remark = remark;
		this.workflowTask = workflowTask;
		this.referenceType = referenceType;
		this.createdUser = createdUser;
		this.responsiblePerson = responsibleUser;
		workflowDate = todayDate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "WORKFLOW_HIST_GEN")
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

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public WorkflowTask getWorkflowTask() {
		return workflowTask;
	}

	public void setWorkflowTask(WorkflowTask workflowTask) {
		this.workflowTask = workflowTask;
	}

	public User getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(User createdUser) {
		this.createdUser = createdUser;
	}

	public User getResponsiblePerson() {
		return this.responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public WorkflowReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(WorkflowReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public Date getWorkflowDate() {
		return workflowDate;
	}

	public void setWorkflowDate(Date workflowDate) {
		this.workflowDate = workflowDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((referenceType == null) ? 0 : referenceType.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + version;
		result = prime * result + ((workflowDate == null) ? 0 : workflowDate.hashCode());
		result = prime * result + ((workflowTask == null) ? 0 : workflowTask.hashCode());
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
		WorkFlowHistory other = (WorkFlowHistory) obj;
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
		if (referenceNo == null) {
			if (other.referenceNo != null)
				return false;
		} else if (!referenceNo.equals(other.referenceNo))
			return false;
		if (referenceType != other.referenceType)
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (version != other.version)
			return false;
		if (workflowDate == null) {
			if (other.workflowDate != null)
				return false;
		} else if (!workflowDate.equals(other.workflowDate))
			return false;
		if (workflowTask != other.workflowTask)
			return false;
		return true;
	}
}