package org.ace.insurance.web.manage.life.confirm;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.RespPersonCriteria;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "RenewalGroupLifeConfirmActionBean")
public class RenewalGroupLifeConfirmActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private WorkFlowDTO workFlowDTO;
	private boolean approvedProposal = true;
	private String remark;
	private User responsiblePerson;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
		workFlowDTO = (workFlowDTO == null) ? (WorkFlowDTO) getParam("workFlowDTO") : workFlowDTO;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		String productId = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getId();
		if (lifeProposal.getProposalInsuredPersonList().size() >= 1 && productId.equals(getGroupLifeId())) {
			int approvedCount = 0;
			for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
				if (pv.isApproved()) {
					approvedCount++;
				}
			}
			if (approvedCount < 5) {
				approvedProposal = false;
			}
		} else {
			approvedProposal = lifeProposal.getProposalInsuredPersonList().get(0).isApproved();
		}
		if (!EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy())) {
			if (recalculatePremium(CONFIRMATION)) {
				renewalGroupLifeProposalService.calculatePremium(lifeProposal);
			}
		}
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public String confirmLifeProposal() {
		if (responsiblePerson == null) {
			addErrorMessage("lifeConfirmaitonForm:responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			return null;
		}
		WorkflowTask workflowTask = WorkflowTask.RENEWAL_PAYMENT;
		workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, WorkflowReferenceType.LIFE_PROPOSAL, user, responsiblePerson);
		outjectLifeProposal(lifeProposal);
		outjectWorkFlowDTO(workFlowDTO);
		return "renewalGroupLifeConfirmReceipt";
	}

	public String editLifeProposal() {
		return "editLifeProposal";
	}

	public String getGroupLifeId() {
		return ProductIDConfig.getGroupLifeId();
	}

	public String denyLifeProposal() {
		String result = null;
		try {
			/*
			 * String formID = "lifeConfirmaitonForm"; if (responsiblePerson ==
			 * null) { addErrorMessage(formID + ":responsiblePerson",
			 * UIInput.REQUIRED_MESSAGE_ID); return null; }
			 */
			if (responsiblePerson == null) {
				responsiblePerson = user;
			}
			WorkflowTask workflowTask = null;
			workflowTask = WorkflowTask.RENEWAL_PROPOSAL_REJECT;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, WorkflowReferenceType.LIFE_PROPOSAL, user, responsiblePerson);
			renewalGroupLifeProposalService.rejectLifeProposal(lifeProposal, workFlowDTO);
			outjectLifeProposal(lifeProposal);
			outjectWorkFlowDTO(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private boolean inputCheck(RespPersonCriteria respPersonCriteria) {
		boolean result = true;
		if (respPersonCriteria.getRespPersonCriteria() == null) {
			addErrorMessage("selectUserForm:respPersonCriteria", MessageId.REQUIRED_ADDON);
			result = false;
		}
		return result;
	}

	public void selectUser() {
		selectUser(WorkflowTask.RENEWAL_PAYMENT, WorkFlowType.LIFE);
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	private void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	private void outjectWorkFlowDTO(WorkFlowDTO workFlowDTO) {
		putParam("workFlowDTO", workFlowDTO);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	/* For Template */
	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	@ManagedProperty(value = "#{LifePolicySummaryService}")
	private ILifePolicySummaryService lifePolicySummaryService;

	public void setLifePolicySummaryService(ILifePolicySummaryService lifePolicySummaryService) {
		this.lifePolicySummaryService = lifePolicySummaryService;
	}

	private List<LifePolicy> lifePolicyList;
	private List<LifePolicyHistory> lifePolicyHistoryList;

	public List<LifePolicy> getLifePolicyList() {
		return lifePolicyList;
	}

	public boolean isEndorse(LifeProposal lifeProposal) {
		if (lifeProposal == null) {
			return false;
		}
		return EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy());
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary(String policyId) {
		LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(policyId);
		return summary;
	}
}
