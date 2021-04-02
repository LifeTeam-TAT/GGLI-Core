package org.ace.insurance.common;

import org.ace.insurance.user.User;

public class WorkFlowDTO {
	private String referenceNo;
	private String remark;
	private WorkflowTask workflowTask;
	private WorkflowReferenceType referenceType;
	private User createdUser;
	private User responsiblePerson;

	public WorkFlowDTO() {
	}

	public WorkFlowDTO(String referenceNo, String remark, WorkflowTask workflowTask, WorkflowReferenceType referenceType, User createdUser, User responsiblePerson) {
		this.referenceNo = referenceNo;
		this.remark = remark;
		this.workflowTask = workflowTask;
		this.referenceType = referenceType;
		this.createdUser = createdUser;
		this.responsiblePerson = responsiblePerson;
	}

	public WorkFlowDTO(String remark, WorkflowTask workflowTask, WorkflowReferenceType referenceType, User createdUser, User responsiblePerson) {
		this.remark = remark;
		this.workflowTask = workflowTask;
		this.referenceType = referenceType;
		this.createdUser = createdUser;
		this.responsiblePerson = responsiblePerson;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getRemark() {
		return remark;
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

	public WorkflowReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(WorkflowReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public User getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(User createdUser) {
		this.createdUser = createdUser;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

}
