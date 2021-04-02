package org.ace.insurance.medical.claim.frontService.request.interfaces;

import java.util.List;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.claim.ClaimInitialReport;
import org.ace.insurance.medical.claim.MedicalClaimProposal;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;

public interface IMedicalClaimRequestFrontService {
	public MedicalClaimProposal addNewMedicalClaim(MedicalClaimProposal medicalClaimProposal, ClaimInitialReport medicalClaimInitialReport,
			List<MedicalPolicyInsuredPersonBeneficiaries> polInsuPersonBeneficiaryList, WorkFlowDTO workflowDTO);

	public MedicalClaimProposalDTO updatMedicalClaim(MedicalClaimProposalDTO medicalClaimProposalDTO, WorkFlowDTO workflowDTO);

}
