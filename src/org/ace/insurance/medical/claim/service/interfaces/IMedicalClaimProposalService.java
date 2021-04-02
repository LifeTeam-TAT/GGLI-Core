package org.ace.insurance.medical.claim.service.interfaces;

import java.util.List;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.claim.MC001;
import org.ace.insurance.medical.claim.MedicalClaimProposal;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.claim.MedicalClaimMonthlyReport;
import org.ace.insurance.report.common.MonthlyReportCriteria;
import org.ace.insurance.web.manage.enquires.ClaimEnquiryCriteria;
import org.ace.insurance.web.manage.medical.claim.DeathClaimDTO;
import org.ace.insurance.web.manage.medical.claim.HospitalizedClaimDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.web.manage.medical.claim.MedicationClaimDTO;
import org.ace.insurance.web.manage.medical.claim.OperationClaimDTO;

public interface IMedicalClaimProposalService {
	public MedicalClaimProposal findMedicalClaimProposalById(String id);

	public HospitalizedClaimDTO findHospitalizedClaimById(String id);

	public MedicationClaimDTO findMedicationClaimById(String id);

	public OperationClaimDTO findOperationClaimById(String id);

	public DeathClaimDTO findDeathClaimById(String id);

	public void updateMedicalClaimProposal(MedicalClaimProposal medicalClaimProposal, WorkFlowDTO w);

	public List<MC001> findAllMedicalClaimProposal(ClaimEnquiryCriteria enquiryCriteria);

	public List<MC001> findAllMedicalClaimProposalByPolicyId(String id);

	public List<MedicalClaimMonthlyReport> findMedicalClaimMonthlyReport(MonthlyReportCriteria criteria);

	public void informMedicalClaimProposal(WorkFlowDTO workflowDTO, ClaimAcceptedInfo claimAcceptedInfo);

	public void paymentMedicalClaimProposal(Payment payment, MedicalClaimProposalDTO medicalClaimProposalDTO);

}
