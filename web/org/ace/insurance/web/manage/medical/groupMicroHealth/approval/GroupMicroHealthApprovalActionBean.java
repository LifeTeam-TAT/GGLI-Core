package org.ace.insurance.web.manage.medical.groupMicroHealth.approval;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "GroupMicroHealthApprovalActionBean")
public class GroupMicroHealthApprovalActionBean extends BaseBean {

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private GroupMicroHealth groupMicroHealth;
	private boolean approved;
	private User user;
	private User responsiblePerson;

	@PostConstruct
	public void init() {
		initializeInjection();
		double authority = user.getAuthority();
		if (authority >= groupMicroHealth.getTotalPremium()) {
			approved = true;
		}
	}

	public void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		groupMicroHealth = (groupMicroHealth == null) ? (GroupMicroHealth) getParam("groupMicroHealth") : groupMicroHealth;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.MEDICAL_INSURANCE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public String addNewGroupMicroHealthApproval() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupMicroHealth.getId(), null, WorkflowTask.CONFIRMATION, WorkflowReferenceType.GROUP_MICROHEALTH, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, groupMicroHealth.getProposalNo());
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.APPROVAL_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String redriectMedicalApproval() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupMicroHealth.getId(), null, WorkflowTask.APPROVAL, WorkflowReferenceType.GROUP_MICROHEALTH, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REDIRECT_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String getSalePersonName() {
		if (groupMicroHealth.getAgent() != null) {
			return groupMicroHealth.getAgent().getFullName();
		} else if (groupMicroHealth.getSaleMan() != null) {
			return groupMicroHealth.getSaleMan().getFullName();
		} else if (groupMicroHealth.getReferral() != null) {
			return groupMicroHealth.getReferral().getFullName();
		}
		return null;
	}

	public GroupMicroHealth getGroupMicroHealth() {
		return groupMicroHealth;
	}

	public void setGroupMicroHealth(GroupMicroHealth groupMicroHealth) {
		this.groupMicroHealth = groupMicroHealth;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

}
