package org.ace.insurance.workflow;

import java.io.Serializable;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.User;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.TASKMESSAGE)
@TableGenerator(name = "TASKMESSAGE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "TASKMESSAGE_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "TaskMessage.deleteByUser", query = "DELETE FROM TaskMessage t WHERE t.responsiblePerson.usercode = :usercode"),
		@NamedQuery(name = "TaskMessage.findRequestCount", query = "SELECT COUNT(t.id) FROM TaskMessage t WHERE t.responsiblePerson.usercode = :usercode") })
@Access(value = AccessType.FIELD)
public class TaskMessage implements Serializable {
	@Transient
	private String id;
	private String referenceNo;
	@Enumerated(EnumType.STRING)
	private WorkflowReferenceType referenceType;
	@Enumerated(value = EnumType.STRING)
	private WorkflowTask workflowTask;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESPONSIBLEUSERID", referencedColumnName = "ID")
	private User responsiblePerson;

	@Version
	private int version;

	public TaskMessage() {
	}

	public TaskMessage(WorkFlow workflow) {
		this.referenceNo = workflow.getReferenceNo();
		this.referenceType = workflow.getReferenceType();
		this.workflowTask = workflow.getWorkflowTask();
		this.responsiblePerson = workflow.getResponsiblePerson();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TASKMESSAGE_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, "ISSYS039", 10);
		}
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public WorkflowReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(WorkflowReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public WorkflowTask getWorkflowTask() {
		return workflowTask;
	}

	public void setWorkflowTask(WorkflowTask workflowTask) {
		this.workflowTask = workflowTask;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((referenceType == null) ? 0 : referenceType.hashCode());
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
		TaskMessage other = (TaskMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (referenceNo == null) {
			if (other.referenceNo != null)
				return false;
		} else if (!referenceNo.equals(other.referenceNo))
			return false;
		if (referenceType != other.referenceType)
			return false;
		if (version != other.version)
			return false;
		if (workflowTask != other.workflowTask)
			return false;
		return true;
	}
}
