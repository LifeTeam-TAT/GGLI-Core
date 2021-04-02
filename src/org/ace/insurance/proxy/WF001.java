package org.ace.insurance.proxy;

import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;

public class WF001 {
	private WorkflowTask workflowTask;
	private WorkflowReferenceType referenceType;

	public WF001(WorkflowTask workflowTask, WorkflowReferenceType referenceType) {
		this.workflowTask = workflowTask;
		this.referenceType = referenceType;
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

	@Override
	public String toString() {

		return workflowTask + " - " + referenceType;
	}
}
