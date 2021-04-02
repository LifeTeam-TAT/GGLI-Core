package org.ace.insurance.web.manage.life.claim.approve;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifeHospitalizedClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ApproveLifeClaimActionBean")
public class ApproveLifeClaimActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private LifeClaimProposal lifeClaimProposal;
	private boolean death;
	private boolean disbility;
	private boolean hospital;
	private List<LifeDeathClaim> lifeDeathClaims;
	private List<LifeHospitalizedClaim> lifeHospitalizedClaims;
	private List<DisabilityLifeClaim> disabilityLifeClaims;
	private List<WorkFlowHistory> workflowList;
	private User responsiblePerson;
	private String remark;
	private LifePolicyClaim lifePolicyClaim;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeClaimProposal = (lifeClaimProposal == null) ? (LifeClaimProposal) getParam("lifeClaimProposal") : lifeClaimProposal;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		loadClaimList();
	}

	private void loadClaimList() {
		lifeDeathClaims = new ArrayList<LifeDeathClaim>();
		lifeHospitalizedClaims = new ArrayList<LifeHospitalizedClaim>();
		disabilityLifeClaims = new ArrayList<DisabilityLifeClaim>();
		for (LifePolicyClaim claim : lifeClaimProposal.getClaimList()) {
			if (claim instanceof LifeDeathClaim) {
				LifeDeathClaim deathClaim = (LifeDeathClaim) claim;
				death = true;
				lifeDeathClaims.add(deathClaim);
			} else if (claim instanceof LifeHospitalizedClaim) {
				hospital = true;
				LifeHospitalizedClaim hospitalClaim = (LifeHospitalizedClaim) claim;
				lifeHospitalizedClaims.add(hospitalClaim);
			} else {
				disbility = true;
				DisabilityLifeClaim disabilityClaim = (DisabilityLifeClaim) claim;
				disabilityLifeClaims.add(disabilityClaim);
			}
		}
	}

	private void removeClaimList() {
		List<LifePolicyClaim> claimList = new ArrayList<LifePolicyClaim>();
		for (LifePolicyClaim lifePolicyClaim : claimList) {
			claimList.add(lifePolicyClaim);
		}
		lifeClaimProposal.getClaimList().removeAll(claimList);
	}

	public String approveLifeClaim() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimProposal.getId(), remark, WorkflowTask.CLAIM_INFORM, WorkflowReferenceType.LIFE_CLAIM, user, responsiblePerson);
			removeClaimList();
			for (LifeDeathClaim lifeDeathClaim : lifeDeathClaims) {
				lifeClaimProposal.addClaim(lifeDeathClaim);
			}
			for (LifeHospitalizedClaim lifehospClaim : lifeHospitalizedClaims) {
				lifeClaimProposal.addClaim(lifehospClaim);
			}
			for (DisabilityLifeClaim disabilityClaim : disabilityLifeClaims) {
				lifeClaimProposal.addClaim(disabilityClaim);
			}
			lifeClaimProposalService.approveLifeClaim(lifeClaimProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_APPROVAL_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimProposal.getClaimProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaimProposal");
	}

	public void openTemplateDialog() {
		putParam("lifeClaimProposal", lifeClaimProposal);
		putParam("workFlowList", getWorkflowList());
		openLifeClaimInfoTemplate();
	}

	public void loadWorkflow() {
		workflowList = workFlowService.findWorkFlowHistoryByRefNo(lifeClaimProposal.getId());
	}

	public ILifeClaimProposalService getLifeClaimProposalService() {
		return lifeClaimProposalService;
	}

	public User getUser() {
		return user;
	}

	public LifeClaimProposal getLifeClaimProposal() {
		return lifeClaimProposal;
	}

	public boolean isDeath() {
		return death;
	}

	public boolean isDisbility() {
		return disbility;
	}

	public boolean isHospital() {
		return hospital;
	}

	public List<LifeDeathClaim> getLifeDeathClaims() {
		return lifeDeathClaims;
	}

	public List<LifeHospitalizedClaim> getLifeHospitalizedClaims() {
		return lifeHospitalizedClaims;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setLifeClaimProposal(LifeClaimProposal lifeClaimProposal) {
		this.lifeClaimProposal = lifeClaimProposal;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void selectUser() {
		selectUser(WorkflowTask.CLAIM_SURVEY, WorkFlowType.LIFE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<DisabilityLifeClaim> getDisabilityLifeClaims() {
		return disabilityLifeClaims;
	}

	public List<WorkFlowHistory> getWorkflowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeClaimProposal.getId());
	}

	public LifePolicyClaim getLifePolicyClaim() {
		return lifePolicyClaim;
	}

	public void setLifePolicyClaim(LifePolicyClaim lifePolicyClaim) {
		this.lifePolicyClaim = lifePolicyClaim;
	}

}
