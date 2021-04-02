package org.ace.insurance.web.manage.medical.groupMicroHealth.confirmation;

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
@ManagedBean(name="GroupMicroHealthConfirmationActionBean")
public class GroupMicroHealthConfirmationActionBean extends BaseBean{
	

	@ManagedProperty(value ="#{WorkFlowService}")
	private IWorkFlowService workFlowService;
	
	
	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}
	
	private GroupMicroHealth groupMicroHealth;
	private User user;
	private User responsiblePerson;
	
	@PostConstruct
	public void init(){
		intializeInjection();
	}
	
	public void intializeInjection(){
		user = (user == null)? (User) getParam(Constants.LOGIN_USER):user;
		groupMicroHealth = (groupMicroHealth == null)? (GroupMicroHealth) getParam("groupMicroHealth"):groupMicroHealth;
	}
	
	public String confirmGroupMicroHealth(){
		WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupMicroHealth.getId(), null, WorkflowTask.PAYMENT, WorkflowReferenceType.GROUP_MICROHEALTH, user, responsiblePerson);
		outjectGroupMicroHealth();
		outjectWorkFlowDTO(workFlowDTO);
		return "groupMicroHealthReceipt";
		
	}
	
	public String editGroupMicroHealth() {
		outjectGroupMicroHealth();
		return "editGroupMicroHealth";
	}

	public String denyGroupMicroHealth() {
		String result = null;
		try {
			if (responsiblePerson == null) {
				responsiblePerson = user;
			}
			WorkflowTask workflowTask = null;
			workflowTask = WorkflowTask.PROPOSAL_REJECT;
			
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupMicroHealth.getId(), null, workflowTask, WorkflowReferenceType.GROUP_MICROHEALTH, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}
	
	
	private void outjectGroupMicroHealth() {
		putParam("groupMicroHealth", groupMicroHealth);
		
	}

	
	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO",workFlowDTO);
		
	}

	
	public void selectUser(){
		selectUser(WorkflowTask.PAYMENT,WorkFlowType.MEDICAL_INSURANCE);
	}
	public void returnUser(SelectEvent event){
		User user = (User) event.getObject();
		this.responsiblePerson = user;
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

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}
	
	

}
