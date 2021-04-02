package org.ace.insurance.web.manage.medical.claim.factory;

import org.ace.insurance.medical.claim.MedicalClaim;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimDTO;

public class MedicalClaimFactory {
	public static MedicalClaim createMedicalClaim(MedicalClaimDTO medicalClaimDTO) {
		MedicalClaim medicalClaim = new MedicalClaim();
		if (medicalClaimDTO.isExistsEntity()) {
			medicalClaim.setId(medicalClaimDTO.getId());
			medicalClaim.setVersion(medicalClaimDTO.getVersion());
		}
		medicalClaim.setApproved(medicalClaimDTO.isApproved());
		if (medicalClaimDTO.getMedicalClaimProposal() != null) {
			medicalClaim.setMedicalClaimProposal(MedicalClaimProposalFactory.createMedicalClaimProposal(medicalClaimDTO.getMedicalClaimProposal()));
		}
		medicalClaim.setRejectReason(medicalClaimDTO.getRejectReason());
		if (medicalClaimDTO.getCommonCreateAndUpateMarks() != null) {
			medicalClaim.setCommonCreateAndUpateMarks(medicalClaimDTO.getCommonCreateAndUpateMarks());
		}
		return medicalClaim;
	}

	public static MedicalClaimDTO createMedicalClaimDTO(MedicalClaim medicalClaim) {
		MedicalClaimDTO medicalClaimDTO = new MedicalClaimDTO();
		if (medicalClaim.getId() != null) {
			medicalClaimDTO.setId(medicalClaim.getId());
			medicalClaimDTO.setVersion(medicalClaim.getVersion());
			medicalClaimDTO.setExistsEntity(true);
		}
		medicalClaimDTO.setApproved(medicalClaim.isApproved());
		if (medicalClaim.getMedicalClaimProposal() != null) {
			medicalClaimDTO.setMedicalClaimProposal(MedicalClaimProposalFactory.createMedicalClaimProposalDTO(medicalClaim.getMedicalClaimProposal()));
		}
		medicalClaimDTO.setRejectReason(medicalClaim.getRejectReason());
		if (medicalClaim.getCommonCreateAndUpateMarks() != null) {
			medicalClaimDTO.setCommonCreateAndUpateMarks(medicalClaim.getCommonCreateAndUpateMarks());
		}
		return medicalClaimDTO;
	}
}
