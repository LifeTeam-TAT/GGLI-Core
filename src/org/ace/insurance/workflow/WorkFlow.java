package org.ace.insurance.workflow;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
@Table(name = TableName.WORKFLOW)
@TableGenerator(name = "WORKFLOW_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "WORKFLOW_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "WorkFlow.findAll", query = "SELECT a FROM WorkFlow a "),
		@NamedQuery(name = "WorkFlow.findByReferenceNo", query = "SELECT a FROM WorkFlow a WHERE a.referenceNo = :referenceNo"),
		@NamedQuery(name = "WorkFlow.findByUser", query = "SELECT a FROM WorkFlow a WHERE a.responsiblePerson.usercode = :usercode"),
		@NamedQuery(name = "WorkFlow.findByRefType", query = "SELECT a FROM WorkFlow a WHERE a.referenceType = :referenceType"),
		@NamedQuery(name = "WorkFlow.findById", query = "SELECT a FROM WorkFlow a WHERE a.id = :id") })
@Access(value = AccessType.FIELD)
public class WorkFlow implements Serializable {

	private static final long serialVersionUID = 1L;
	@Transient
	private String id;
	@Transient
	private String prefix;
	private String referenceNo;
	private String remark;

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

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	private boolean deleteFlag;

	@Version
	private int version;

	public WorkFlow() {
	}

	public WorkFlow(WorkFlowDTO workFlowDTO) {
		this.referenceNo = workFlowDTO.getReferenceNo();
		this.referenceType = workFlowDTO.getReferenceType();
		this.createdUser = workFlowDTO.getCreatedUser();
		this.responsiblePerson = workFlowDTO.getResponsiblePerson();
		this.remark = workFlowDTO.getRemark();
		this.workflowTask = workFlowDTO.getWorkflowTask();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "WORKFLOW_GEN")
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

	public String getRemark() {
		return this.remark;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((createdUser == null) ? 0 : createdUser.hashCode());
		result = prime * result + (deleteFlag ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((referenceType == null) ? 0 : referenceType.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((responsiblePerson == null) ? 0 : responsiblePerson.hashCode());
		result = prime * result + version;
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
		WorkFlow other = (WorkFlow) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (createdUser == null) {
			if (other.createdUser != null)
				return false;
		} else if (!createdUser.equals(other.createdUser))
			return false;
		if (deleteFlag != other.deleteFlag)
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
		if (responsiblePerson == null) {
			if (other.responsiblePerson != null)
				return false;
		} else if (!responsiblePerson.equals(other.responsiblePerson))
			return false;
		if (version != other.version)
			return false;
		if (workflowTask != other.workflowTask)
			return false;
		return true;
	}

}