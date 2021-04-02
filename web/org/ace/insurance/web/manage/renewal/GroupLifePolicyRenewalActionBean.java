package org.ace.insurance.web.manage.renewal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.life.policy.LPC001;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.lang3.StringUtils;

@ViewScoped
@ManagedBean(name = "GroupLifePolicyRenewalActionBean")
public class GroupLifePolicyRenewalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private PolicyCriteria policyCriteria;
	private List<LPC001> lifePolicyList;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@PostConstruct
	public void init() {
		lifePolicyList = new ArrayList<LPC001>();
		policyCriteria = new PolicyCriteria();
	}

	public void search() {
		if (inputCheck(policyCriteria)) {
			lifePolicyList = lifePolicyService.findRenewalGroupLifePolicyByPolicyCriteria(policyCriteria, 30);
		}
	}

	public void reset() {
		policyCriteria.setCriteriaValue(null);
		policyCriteria.setPolicyCriteria(null);
		if (lifePolicyList != null) {
			lifePolicyList.clear();
		}

	}

	private boolean inputCheck(PolicyCriteria policyCriteria) {
		boolean result = true;
		String formId = "LifePolicyRenewalForm";
		if ((policyCriteria.getPolicyCriteria() == null)) {
			addErrorMessage(formId + ":selectPolicyCriteria", MessageId.PLEASE_SELECT_POLICY_CRITERIA);
			result = false;
		}
		if (StringUtils.isBlank(policyCriteria.getCriteriaValue())) {
			addErrorMessage(formId + ":policyCriteria", MessageId.CRITERIA_VALUE_REQUIRED);
			result = false;
		}
		return result;
	}

	public String renewalLifePolicy(LPC001 lifePolicy) {
		LifePolicy policy = lifePolicyService.findLifePolicyById(lifePolicy.getId());
		putParam("lifePolicy", policy);
		return "renewalGroupLifeProposal";
	}

	public List<LPC001> getLifePolicyList() {
		return lifePolicyList;
	}

	public PolicyCriteria getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteria policyCriteria) {
		this.policyCriteria = policyCriteria;
	}
}
