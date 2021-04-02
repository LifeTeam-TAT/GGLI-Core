package org.ace.insurance.web.manage.life.studentLife.confirm;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
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
@ManagedBean(name = "AddNewStudentLifeConfirmationActionBean")
public class AddNewStudentLifeConfirmationActionBean extends BaseBean implements Serializable {
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
	private WorkFlowDTO workFlowDTO;
	private boolean approvedProposal = true;
	private boolean isEndorse;
	private boolean isFarmer;
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
		approvedProposal = true;
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (!pv.isApproved()) {
				approvedProposal = false;
				break;
			}
		}
		if (recalculatePremium(CONFIRMATION)) {
			lifeProposalService.calculatePremium(lifeProposal);
		}
	}

	public boolean isApprovedProposal() {
		return approvedProposal;
	}

	public boolean getIsEndorse() {
		return isEndorse;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public String confirmLifeProposal() {
		WorkflowTask workflowTask = lifeProposal.isSkipPaymentTLF() ? WorkflowTask.ISSUING : WorkflowTask.PAYMENT;
		WorkflowReferenceType referenceType = WorkflowReferenceType.STUDENT_LIFE_PROPOSAL;
		workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, responsiblePerson);
		outjectLifeProposal(lifeProposal);
		outjectWorkFlowDTO(workFlowDTO);
		return "printConfirmStudentLifeProposal";
	}

	public String editLifeProposal() {
		BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposal.getId());
		putParam("bancaassuranceProposal", bancaassuranceProposal);
		putParam("insuranceType", InsuranceType.STUDENT_LIFE);
		putParam("lifeProposal", lifeProposal);
		putParam("editLifeProposal", true);
		return "addNewStudentLifeProposal";
	}

	public String denyLifeProposal() {
		String result = null;
		try {
			WorkflowTask workflowTask = WorkflowTask.PROPOSAL_REJECT;
			WorkflowReferenceType referenceType = WorkflowReferenceType.STUDENT_LIFE_PROPOSAL;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, workflowTask, referenceType, user, user);
			lifeProposalService.rejectLifeProposal(lifeProposal, workFlowDTO);
			PrimeFaces.current().executeScript("PF('denyDialog').hide();");
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void selectUser() {
		WorkflowTask workflowTask = WorkflowTask.PAYMENT;
		WorkFlowType workFlowType = WorkFlowType.STUDENT_LIFE;
		selectUser(workflowTask, workFlowType);
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
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
		if (lifePolicyList == null) {
			lifePolicyList = lifePolicyService.findPolicyByProposalId(lifeProposal.getId());
		}
		return lifePolicyList;
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList() {
		if (lifePolicyHistoryList == null) {
			if (lifeProposal.getLifePolicy() != null) {
				lifePolicyHistoryList = lifePolicyHistoryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getPolicyNo());
			}
		}
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary() {
		return lifePolicySummaryService.findLifePolicyByPolicyNo(lifeProposal.getLifePolicy().getId());
	}

	public boolean isFarmer() {
		return isFarmer;
	}

	public String getPageHeader() {
		return "Student Life Proposal Confirmation";
	}

	public void openTemplateDialog() {
		putParam("lifeProposal", lifeProposal);
		putParam("workFlowList", getWorkFlowList());
		openStudentLifeInfoTemplate();
	}

}
