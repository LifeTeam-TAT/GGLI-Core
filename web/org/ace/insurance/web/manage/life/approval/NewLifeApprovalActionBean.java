package org.ace.insurance.web.manage.life.approval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "NewLifeApprovalActionBean")
public class NewLifeApprovalActionBean extends BaseBean implements Serializable {
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

	@ManagedProperty(value = "#{LifeEndorsementService}")
	private ILifeEndorsementService lifeEndorsementService;

	public void setLifeEndorsementService(ILifeEndorsementService lifeEndorsementService) {
		this.lifeEndorsementService = lifeEndorsementService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private BancaassuranceProposal bancaassuranceProposal;
	private ProposalInsuredPerson proposalInsuredPerson;
	private String remark;
	private User responsiblePerson;
	private boolean approved;
	private boolean isEndorse;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isShortEndowLife;
	private LifeEndorseInfo lifeEndorseInfo;
	private boolean isAllApproved;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		// TODO FIXME PSH for better Performance use proposalid from param
		String lifeProposalId = (String) getParam("lifeProposalId");
		if (null != lifeProposalId && !lifeProposalId.trim().isEmpty()) {
			lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalId);
		}

		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
		bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		double userAuthority = user.getAuthority();
		double sumInsured = lifeProposal.getSumInsured();
		if (userAuthority >= sumInsured) {
			approved = true;
		}
		isEndorse = lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT);
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		isPersonalAccident = (KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product));
		isFarmer = KeyFactorChecker.isFarmer(product);
		isShortEndowLife = KeyFactorChecker.isShortTermEndowment(product.getId());
		if (isEndorse) {
			lifeEndorseInfo = lifeEndorsementService.findLastLifeEndorseInfoByProposalNo(lifeProposal.getLifePolicy().getPolicyNo());
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void selectUser() {
		WorkflowTask workflowTask = !approved ? WorkflowTask.APPROVAL : isEndorse ? WorkflowTask.ENDORSEMENT_CONFIRMATION : WorkflowTask.INFORM;
		WorkFlowType workFlowType = isPersonalAccident ? WorkFlowType.PERSONAL_ACCIDENT
				: isFarmer ? WorkFlowType.FARMER : isShortEndowLife ? WorkFlowType.SHORT_ENDOWMENT : WorkFlowType.LIFE;
		selectUser(workflowTask, workFlowType);
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public ProposalInsuredPerson getProposalInsuredperson() {
		return proposalInsuredPerson;
	}

	public void setProposalInsuredperson(ProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
	}

	public void prepareApproveInsuredperson(ProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
	}

	public LifeEndorseInfo getLifeEndorseInfo() {
		return lifeEndorseInfo;
	}

	public void changeAllApproved() {
		for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			insuredPerson.setApproved(isAllApproved);
		}
	}

	public String addNewLifeApproval() {
		String result = null;
		try {
			WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_CONFIRMATION : WorkflowTask.INFORM;
			WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
					: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
							: isShortEndowLife ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
			if (isEndorse) {
				lifeEndorsementService.approveLifeProposal(lifeProposal, workFlowDTO);
			} else {
				lifeProposalService.approveLifeProposal(lifeProposal, workFlowDTO);
			}
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.APPROVAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String redriectLifeApproval() {
		String result = null;
		try {
			WorkflowTask workflowTask = isEndorse ? WorkflowTask.ENDORSEMENT_APPROVAL : WorkflowTask.APPROVAL;
			WorkflowReferenceType referenceType = isPersonalAccident ? WorkflowReferenceType.PA_PROPOSAL
					: isFarmer ? WorkflowReferenceType.FARMER_PROPOSAL
							: isShortEndowLife ? WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL : WorkflowReferenceType.LIFE_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REDIRECT_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean getIsEndorse() {
		return isEndorse;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	/* Life Proposal Template */
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

	public List<LifePolicy> getLifePolicyList() {
		lifePolicyList = new ArrayList<LifePolicy>();
		if (lifeProposal.getLifePolicy() != null) {
			lifePolicyList.add(lifeProposal.getLifePolicy());
		}
		return lifePolicyList;
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		if (lifeProposal.getLifePolicy() != null) {
			return lifePolicyHistoryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
		}
		return new ArrayList<>();

	}

	public LifePolicySummary getLifePolicySummary(String policyId) {
		LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(policyId);
		return summary;
	}

	public String getPageHeader() {
		return (isEndorse ? "Life Endorsement" : isFarmer ? "Farmer" : isShortEndowLife ? "Short Term Endowment Life" : isPersonalAccident ? "Personal Accident" : "Life")
				+ " Proposal Approval";
	}

	public boolean isAllApproved() {
		return isAllApproved;
	}

	public void setAllApproved(boolean isAllApproved) {
		this.isAllApproved = isAllApproved;
	}
}
