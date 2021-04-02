package org.ace.insurance.web.manage.user;

import java.util.Set;

import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;

public class AuthorityDTO {
	private String tempId;
	private WorkFlowType workFlowType;
	private Set<WorkflowTask> permissionList;

	public AuthorityDTO(WorkFlowType workFlowType, Set<WorkflowTask> permissionList) {
		this.workFlowType = workFlowType;
		this.permissionList = permissionList;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public WorkFlowType getWorkFlowType() {
		return workFlowType;
	}

	public void setWorkFlowType(WorkFlowType workFlowType) {
		this.workFlowType = workFlowType;
	}

	public Set<WorkflowTask> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(Set<WorkflowTask> permissionList) {
		this.permissionList = permissionList;
	}
}
