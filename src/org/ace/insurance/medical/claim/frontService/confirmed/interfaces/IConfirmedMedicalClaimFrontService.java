package org.ace.insurance.medical.claim.frontService.confirmed.interfaces;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;

public interface IConfirmedMedicalClaimFrontService {
	public Payment confirmMedicalClaimProposal(MedicalClaimProposalDTO medicalClaimProposalDTO, WorkFlowDTO workflowDTO, PaymentDTO paymentDTO);
}
