package org.ace.insurance.web.manage.medical;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;

@RequestScoped
@ManagedBean(name = "MedicalProposalTemplateBean")
public class MedicalProposalTemplateBean {
	private MedicalPolicy medicalPolicy;

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	public MedicalPolicy getMedicalPolicyList(String medicalProposalId) {
		if (medicalPolicy == null) {
			medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(medicalProposalId);
		}
		return medicalPolicy;
	}

}
