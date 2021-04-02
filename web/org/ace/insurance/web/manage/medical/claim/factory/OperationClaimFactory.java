package org.ace.insurance.web.manage.medical.claim.factory;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.medical.claim.OperationClaim;
import org.ace.insurance.medical.claim.OperationClaimICD10;
import org.ace.insurance.web.manage.life.proposal.AttachmentDTO;
import org.ace.insurance.web.manage.medical.claim.OperationClaimDTO;

public class OperationClaimFactory {
	public static OperationClaim createOperationClaim(OperationClaimDTO operationClaimDTO) {
		OperationClaim operationClaim = new OperationClaim();
		if (operationClaimDTO.isExistEntity()) {

			operationClaim.setVersion(operationClaimDTO.getVersion());
		}
		operationClaim.setId(operationClaimDTO.getId());
		operationClaim.setClaimRole(operationClaimDTO.getClaimRole());
		operationClaim.setApproved(operationClaimDTO.isApproved());
		operationClaim.setRejectReason(operationClaimDTO.getRejectReason());
		operationClaim.setOperation(operationClaimDTO.getOperation());
		operationClaim.setOperationDate(operationClaimDTO.getOperationDate());
		operationClaim.setOperationFee(operationClaimDTO.getOperationFee());
		operationClaim.setOperationReason(operationClaimDTO.getOperationReason());
		operationClaim.setOperationRemark(operationClaimDTO.getOperationRemark());
		operationClaim.setOperationType(operationClaimDTO.getOperationType());
		for (AttachmentDTO oca : operationClaimDTO.getAttachmentList()) {
			operationClaim.addAttachment(new Attachment(oca));
		}
		if (operationClaimDTO.getCommonCreateAndUpateMarks() != null) {
			operationClaim.setCommonCreateAndUpateMarks(operationClaimDTO.getCommonCreateAndUpateMarks());
		}

		return operationClaim;
	}

	public static OperationClaimDTO createOperationClaimDTO(OperationClaim operationClaim) {
		OperationClaimDTO operationClaimDTO = new OperationClaimDTO();
		if (operationClaim.getId() != null) {
			operationClaimDTO.setId(operationClaim.getId());
			operationClaimDTO.setVersion(operationClaim.getVersion());
			operationClaimDTO.setExistEntity(true);
		}
		operationClaimDTO.setClaimRole(operationClaim.getClaimRole());
		operationClaimDTO.setApproved(operationClaim.isApproved());
		operationClaimDTO.setRejectReason(operationClaim.getRejectReason());
		operationClaimDTO.setOperation(operationClaim.getOperation());
		operationClaimDTO.setOperationDate(operationClaim.getOperationDate());
		operationClaimDTO.setOperationFee(operationClaim.getOperationFee());
		operationClaimDTO.setAbortionAmt(operationClaim.getAbortionAmount());
		operationClaimDTO.setOperationReason(operationClaim.getOperationReason());
		operationClaimDTO.setOperationRemark(operationClaim.getOperationRemark());
		operationClaimDTO.setOperationType(operationClaim.getOperationType());
		if (operationClaim.getOperationReason() != null) {
			OperationClaimICD10 claimICD10 = new OperationClaimICD10();
			claimICD10.setIcd10(operationClaim.getOperationReason());
			operationClaimDTO.addOperationClaimICD10(claimICD10);
		}
		for (Attachment oca : operationClaim.getAttachmentList()) {
			operationClaimDTO.addAttachment(new AttachmentDTO(oca));
		}
		if (operationClaim.getCommonCreateAndUpateMarks() != null) {
			operationClaimDTO.setCommonCreateAndUpateMarks(operationClaim.getCommonCreateAndUpateMarks());
		}
		return operationClaimDTO;
	}
}
