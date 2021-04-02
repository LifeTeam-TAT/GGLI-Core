package org.ace.insurance.medical.proposal.frontService.surveyService;

import javax.annotation.Resource;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.medical.proposal.frontService.surveyService.interfaces.IAddNewMedicalSurveyFrontService;
import org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "AddNewMedicalSurveyFrontService")
public class AddNewMedicalSurveyFrontService extends BaseService implements IAddNewMedicalSurveyFrontService {

	@Resource(name = "MedicalProposalDAO")
	private IMedicalProposalDAO medicalProposalDAO;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewSurvey(MedicalSurvey medicalSurvey, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			medicalProposalDAO.insertSurvey(medicalSurvey);
			/*
			 * medicalProposalDAO.updateInsuPersonMedicalStatus(medicalSurvey.
			 * getMedicalProposal().getMedicalProposalInsuredPerson());
			 */
			medicalProposalDAO.update(medicalSurvey.getMedicalProposal());
			// medicalProposalDAO.addAttachment(medicalSurvey.getMedicalProposal());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Survey", e);
		}
	}

}
