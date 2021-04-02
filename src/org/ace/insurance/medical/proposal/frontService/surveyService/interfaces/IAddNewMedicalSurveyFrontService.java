package org.ace.insurance.medical.proposal.frontService.surveyService.interfaces;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalSurvey;

public interface IAddNewMedicalSurveyFrontService {
	
	public void addNewSurvey(MedicalSurvey medicalSurvey, WorkFlowDTO workFlowDTO);

}
