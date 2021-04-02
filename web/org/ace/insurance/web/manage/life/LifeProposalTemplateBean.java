package org.ace.insurance.web.manage.life;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.ace.insurance.common.EndorsementUtil;
import org.ace.insurance.life.lifePolicySummary.LifePolicySummary;
import org.ace.insurance.life.lifePolicySummary.Service.Interfaces.ILifePolicySummaryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.web.common.Utils;

@RequestScoped
@ManagedBean(name = "LifeProposalTemplateBean")
public class LifeProposalTemplateBean {

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

	public List<LifePolicy> getLifePolicyList(String lifeProposalId) {
		if (lifePolicyList == null && !Utils.isEmpty(lifeProposalId)) {
			lifePolicyList = lifePolicyService.findPolicyByProposalId(lifeProposalId);
		}
		return lifePolicyList;
	}

	public List<LifePolicy> getBeforeEndorsementLifePolicyList(String lifePolicyId) {
		if (lifePolicyList == null) {
			lifePolicyList = lifePolicyService.findLifePolicyByPolicyId(lifePolicyId);
		}
		return lifePolicyList;
	}

	public boolean isEndorse(LifeProposal lifeProposal) {
		if (lifeProposal == null) {
			return false;
		}
		return EndorsementUtil.isEndorsementProposal(lifeProposal.getLifePolicy());
	}

	public List<LifePolicyHistory> getLifePolicyHistoryList(String policyNo) {
		if (lifePolicyHistoryList == null) {
			lifePolicyHistoryList = lifePolicyHistoryService.findLifePolicyByPolicyNo(policyNo);
		}
		return lifePolicyHistoryList;
	}

	public LifePolicySummary getLifePolicySummary(String policyId) {
		LifePolicySummary summary = lifePolicySummaryService.findLifePolicyByPolicyNo(policyId);
		return summary;
	}
}
