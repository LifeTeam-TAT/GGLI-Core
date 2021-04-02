package org.ace.insurance.web.manage.endorsement.life.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.life.policy.LPC001;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;

@ViewScoped
@ManagedBean(name = "LifePolicyEndorsementActionBean")
public class LifePolicyEndorsementActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private PolicyCriteria policyCriteria;
	private List<LPC001> lifePolicyList;

	private boolean isEndorse;
	private String message;
	private LifePolicy policy;
	private Date policyStartDate;
	private Date policyEndDate;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@PostConstruct
	public void init() {
		lifePolicyList = new ArrayList<LPC001>();
		policyCriteria = new PolicyCriteria();
		policy = new LifePolicy();
		isEndorse = true;
	}

	public void search() {
		if (inputCheck(policyCriteria)) {
			lifePolicyList = lifePolicyService.findByPolicyCriteria(policyCriteria, 30);
		}
	}

	public void reset() {
		policyCriteria.setCriteriaValue(null);
		policyCriteria.setPolicyCriteria(null);
		lifePolicyList.clear();
	}

	private boolean inputCheck(PolicyCriteria policyCriteria) {
		boolean result = true;
		String formId = "LifePolicyEndorsementForm";
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

	public List<LPC001> getLifePolicyList() {
		return lifePolicyList;
	}

	public void handlePolicyEndorse(LPC001 lifePolicy) {
		this.policy = lifePolicyService.findLifePolicyById(lifePolicy.getId());
		PolicyInsuredPerson insuredperson = policy.getPolicyInsuredPersonList().get(0);
		policyStartDate = insuredperson.getStartDate();
		policyEndDate = insuredperson.getEndDate();
		if (new Date().before(policyEndDate)) {
			isEndorse = true;
		} else {
			isEndorse = false;
		}
		PrimeFaces.current().executeScript("PF('informationDialog').show()");

	}

	public String editLifePolicy() {
		putParam("lifePolicy", policy);
		putParam("InsuranceType", InsuranceType.LIFE);
		return "lifeProposal";
	}

	public PolicyCriteria getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteria policyCriteria) {
		this.policyCriteria = policyCriteria;
	}

	public boolean isEndorse() {
		return isEndorse;
	}

	public String getMessage() {
		return message;
	}

	public void setEndorse(boolean isEndorse) {
		this.isEndorse = isEndorse;
	}

	public LifePolicy getPolicy() {
		return policy;
	}

	public void setPolicy(LifePolicy policy) {
		this.policy = policy;
	}

	public Date getPolicyStartDate() {
		return policyStartDate;
	}

	public Date getPolicyEndDate() {
		return policyEndDate;
	}

}
