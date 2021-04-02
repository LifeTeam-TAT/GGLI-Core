package org.ace.insurance.web.manage.medical.claim.request;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.common.ValidationUtil;
import org.ace.insurance.web.common.Validator;

@ViewScoped
@ManagedBean(name = "DeathBeneficiaryValidator")
public class DeathBeneficiaryValidator implements Validator<MedicalPolicyInsuredPersonBeneficiaries> {
	public ValidationResult validate(MedicalPolicyInsuredPersonBeneficiaries medicalPolicyInsuredPersonBeneficiaries) {
		ValidationResult result = new ValidationResult();
		String formID = "beneficiaryDeathDialogForm";

		if (medicalPolicyInsuredPersonBeneficiaries.getDeathDate() == null) {
			result.addErrorMessage(formID + ":benDeathDate", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (ValidationUtil.isStringEmpty(medicalPolicyInsuredPersonBeneficiaries.getDeathReason())) {
			result.addErrorMessage(formID + ":benDeathReason", UIInput.REQUIRED_MESSAGE_ID);
		}

		return result;
	}
}
