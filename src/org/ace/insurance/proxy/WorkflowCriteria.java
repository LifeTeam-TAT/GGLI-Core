package org.ace.insurance.proxy;

import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.User;

public class WorkflowCriteria {
	private WorkflowReferenceType referenceType;
	private WorkflowTask workflowTask;
	private ProposalType proposalType;
	private User responsiblePerson;

	public WorkflowCriteria(WorkflowReferenceType referenceType, WorkflowTask workflowTask, ProposalType proposalType, User responsiblePerson) {
		this.referenceType = referenceType;
		this.workflowTask = workflowTask;
		this.proposalType = proposalType;
		this.responsiblePerson = responsiblePerson;
	}

	public WorkflowCriteria(WorkflowReferenceType referenceType, WorkflowTask workflowTask, User responsiblePerson) {
		this.referenceType = referenceType;
		this.workflowTask = workflowTask;
		this.responsiblePerson = responsiblePerson;
	}

	public WorkflowReferenceType getReferenceType() {
		return referenceType;
	}

	public WorkflowTask getWorkflowTask() {
		return workflowTask;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

}
