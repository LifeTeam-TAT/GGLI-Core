package org.ace.insurance.medical.renewal.service;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.renewal.service.interfaces.IMedicalRenewalService;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;

@Service(value = "MedicalRenewalService")
public class MedicalRenewalService extends BaseService implements IMedicalRenewalService {

	@Override
	public MedicalProposal addNewMedicalProposal(MedProDTO medicalProposalDTO, WorkFlowDTO workFlowDTO) {
		// TODO Auto-generated method stub
		return null;
	}

}
