package org.ace.insurance.web.manage.life.groupfarmer.confirm;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "AddNewGroupFarmerConfirmActionBean")
@ViewScoped
public class AddNewGroupFarmerConfirmActionBean extends BaseBean {

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}
	
	
	private User user;
	private GroupFarmerProposal groupFarmerProposal;
	private String remark;
	private User responsiblePerson;

	@PostConstruct()
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		groupFarmerProposal = (groupFarmerProposal == null) ? (GroupFarmerProposal) getParam("groupFarmerProposal") : groupFarmerProposal;
		
	}
	
	public double getTotalPremium() {
		double totalPremium = 0.0;
		return totalPremium;
	}

	public String confirmGroupFarmerProposal() {
		if (responsiblePerson == null) {
			addErrorMessage("groupFarmerConfirmationForm:responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			return null;
		}
		WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupFarmerProposal.getId(), remark, WorkflowTask.PAYMENT, WorkflowReferenceType.GROUPFARMER_PROPOSAL, user, responsiblePerson);
		outjectGroupFarmerProposal(groupFarmerProposal);
		outjectWorkFlowDTO(workFlowDTO);
		return "confirmGroupFarmerProposalPrint";
	}
	
	
	public String denyGroupFarmerProposal() {
		String result = null;
		try {
			if (responsiblePerson == null) {
				responsiblePerson = user;
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupFarmerProposal.getId(), remark, WorkflowTask.PROPOSAL_REJECT, WorkflowReferenceType.TRAVEL_PROPOSAL, user, responsiblePerson);
			groupFarmerProposalService.rejectGroupFarmerProposal(groupFarmerProposal, workFlowDTO);
			outjectGroupFarmerProposal(groupFarmerProposal);
			outjectWorkFlowDTO(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, groupFarmerProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	
	public String editGroupFarmerProposal() {
		outjectGroupFarmerProposal(groupFarmerProposal);
		return "editGroupFarmerProposal";
	}

	private void outjectGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		putParam("groupFarmerProposal", groupFarmerProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void selectUser() {
		WorkflowTask workflowTask = WorkflowTask.PAYMENT;
		selectUser(workflowTask, WorkFlowType.FARMER);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

}
