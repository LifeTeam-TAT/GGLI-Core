package org.ace.insurance.web.manage.medical.claim.validation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.web.common.Validator;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "MedicalClaimSurveyValidator")
public class MedicalClaimSurveyValidator implements Validator<MedicalClaimProposalDTO> {
	@Override
	public ValidationResult validate(MedicalClaimProposalDTO mClaimProposalDTO) {
		ValidationResult result = new ValidationResult();
		String formID = "medicalClaimSurveyEntryForm";

		if (mClaimProposalDTO.getMedicalClaimSurvey().getSurveyDate() != null
				&& mClaimProposalDTO.getMedicalClaimSurvey().getSurveyDate().before(mClaimProposalDTO.getSubmittedDate())) {
			result.addErrorMessage(formID + ":surveyDate", MessageId.INVALID_SURVEY_DATE);
		}

		return result;
	}
}
