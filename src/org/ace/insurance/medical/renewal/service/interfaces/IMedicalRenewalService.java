package org.ace.insurance.medical.renewal.service.interfaces;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;

public interface IMedicalRenewalService {
	public MedicalProposal addNewMedicalProposal(MedProDTO medicalProposalDTO, WorkFlowDTO workFlowDTO);
}
