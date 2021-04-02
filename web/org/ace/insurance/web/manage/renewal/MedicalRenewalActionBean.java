package org.ace.insurance.web.manage.renewal;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.MedicalPolicyCriteriaItems;
import org.ace.insurance.medical.claim.MedicalPolicyCriteria;
import org.ace.insurance.medical.policy.MPL001;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "MedicalRenewalActionBean")
public class MedicalRenewalActionBean extends BaseBean {
	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService policyService;

	public void setPolicyService(IMedicalPolicyService policyService) {
		this.policyService = policyService;
	}

	private MedicalPolicyCriteria criteria;
	private List<MPL001> policyList;
	private MPL001 policyDTO;

	@PostConstruct
	public void init() {
		criteria = new MedicalPolicyCriteria();
	}

	public void search() {
		policyList = policyService.findMedicalPolicyByCriteria(criteria);
	}

	public void reset() {
		criteria.setCriteriaValue(null);
		criteria.setMedicalPolicyCriteriaItems(null);
	}

	public void prepareToRenewPolicy(MPL001 policyDTO) {
		this.policyDTO = policyDTO;
	}

	public String renewPolicy() {
		MedicalPolicy medicalPolicy = policyService.findMedicalPolicyById(this.policyDTO.getId());
		putParam("medicalPolicy", medicalPolicy);
		return "renewalMedicalProposal";
	}

	public MedicalPolicyCriteriaItems[] getMedicalPolicyCriteriaList() {
		return MedicalPolicyCriteriaItems.values();
	}

	public MedicalPolicyCriteria getCriteria() {
		return criteria;
	}

	public List<MPL001> getPolicyList() {
		return policyList;
	}

	public MPL001 getPolicyDTO() {
		return policyDTO;
	}

}
