
package org.ace.insurance.web.manage.life.singlepremiumcreditlife.approve;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewSinglePremiumCreditLifeApprovalActionBean")
public class AddNewSinglePremiumCreditLifeApprovalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private boolean approved;
	private User user;
	private LifeProposal lifeProposal;
	private double approvedSumInsured;
	private boolean needMedicalCheckup;
	private String rejectReason;
	private String remark;
	private User responsiblePerson;

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		// TODO FIXME PSH for better Performance use proposalid from param
		String lifeProposalId = (String) getParam("lifeProposalId");
		if (null != lifeProposalId && !lifeProposalId.trim().isEmpty()) {
			lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		}
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		double userAuthority = user.getAuthority();
		double sumInsured = lifeProposal.getSumInsured();
		if (userAuthority >= sumInsured) {
			approved = true;
		}
	}

	public void openTemplateDialog() {
		putParam("lifeProposal", lifeProposal);
		putParam("workFlowList", getWorkFlowList());
		openNewLifeProposalInfoTemplate();
	}

	public void selectUser() {
		WorkflowTask workflowTask = !approved ? WorkflowTask.APPROVAL : WorkflowTask.INFORM;
		selectUser(workflowTask, WorkFlowType.SINGLE_PREMIUM_CREDIT_LIFE);
	}

	public String redriectLifeApproval() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.APPROVAL, WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, user,
					responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REDIRECT_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void prepareApproveLifeProposal() {
		if (responsiblePerson != null) {
			PrimeFaces.current().executeScript("PF('approveConfirmDialog').show()");
		}
	}

	public String approveLifeProposal() {
		String result = null;
		try {
			for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
				insuredPerson.setApproved(true);
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.INFORM, WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, user,
					responsiblePerson);
			lifeProposalService.approveLifeProposal(lifeProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.APPROVAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void prepareRejectLifeProposal() {
		PrimeFaces.current().executeScript("PF('lifeRejectDialog').show()");
	}

	public String rejectLifeProposal() {
		String result = null;
		try {
			for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
				insuredPerson.setApproved(false);
				insuredPerson.setApprovedSumInsured(approvedSumInsured);
				insuredPerson.setNeedMedicalCheckup(needMedicalCheckup);
				insuredPerson.setRejectReason(rejectReason);
			}
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.INFORM, WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL, user,
					responsiblePerson);
			lifeProposalService.approveLifeProposal(lifeProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REJECT_PROPOSAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
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

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public double getApprovedSumInsured() {
		return approvedSumInsured;
	}

	public void setApprovedSumInsured(double approvedSumInsured) {
		this.approvedSumInsured = approvedSumInsured;
	}

	public boolean isNeedMedicalCheckup() {
		return needMedicalCheckup;
	}

	public void setNeedMedicalCheckup(boolean needMedicalCheckup) {
		this.needMedicalCheckup = needMedicalCheckup;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
}
