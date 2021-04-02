package org.ace.insurance.web.manage.life.claim;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifeHospitalizedClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "LifeClaimInfoTemplateActionBean")
public class LifeClaimInfoTemplateActionBean extends BaseBean {
	private boolean death;
	private boolean disbility;
	private boolean hospital;
	private LifeClaimProposal lifeClaimProposal;
	private List<WorkFlowHistory> workflowList;
	private List<LifeDeathClaim> lifeDeathClaims;
	private List<LifeHospitalizedClaim> lifeHospitalizedClaims;
	private List<DisabilityLifeClaim> disabilityLifeClaims;

	@PostConstruct
	public void init() {
		initializeInjection();
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

	@SuppressWarnings("unchecked")
	private void initializeInjection() {
		lifeClaimProposal = (lifeClaimProposal == null) ? (LifeClaimProposal) getParam("lifeClaimProposal") : lifeClaimProposal;
		workflowList = (List<WorkFlowHistory>) getParam("workFlowList");
	}

	public LifeClaimProposal getLifeClaimProposal() {
		return lifeClaimProposal;
	}

	public List<WorkFlowHistory> getWorkflowList() {
		return workflowList;
	}

	public List<LifeDeathClaim> getLifeDeathClaims() {
		return lifeDeathClaims;
	}

	public List<LifeHospitalizedClaim> getLifeHospitalizedClaims() {
		return lifeHospitalizedClaims;
	}

	public List<DisabilityLifeClaim> getDisabilityLifeClaims() {
		return disabilityLifeClaims;
	}

	public boolean isDeath() {
		return death;
	}

	public void setDeath(boolean death) {
		this.death = death;
	}

	public boolean isDisbility() {
		return disbility;
	}

	public void setDisbility(boolean disbility) {
		this.disbility = disbility;
	}

	public boolean isHospital() {
		return hospital;
	}

	public void setHospital(boolean hospital) {
		this.hospital = hospital;
	}

}
