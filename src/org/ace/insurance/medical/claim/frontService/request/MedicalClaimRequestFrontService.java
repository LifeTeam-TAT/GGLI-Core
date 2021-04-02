package org.ace.insurance.medical.claim.frontService.request;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.medical.claim.ClaimInitialReport;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.claim.DeathClaim;
import org.ace.insurance.medical.claim.HospitalizedClaim;
import org.ace.insurance.medical.claim.MedicalClaim;
import org.ace.insurance.medical.claim.MedicalClaimBeneficiary;
import org.ace.insurance.medical.claim.MedicalClaimProposal;
import org.ace.insurance.medical.claim.MedicationClaim;
import org.ace.insurance.medical.claim.OperationClaim;
import org.ace.insurance.medical.claim.frontService.request.interfaces.IMedicalClaimRequestFrontService;
import org.ace.insurance.medical.claim.persistence.interfaces.IMedicalClaimInitialRepDAO;
import org.ace.insurance.medical.claim.persistence.interfaces.IMedicalClaimProposalDAO;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.policy.persistence.interfaces.IMedicalPolicyDAO;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.web.manage.medical.claim.factory.MedicalClaimProposalFactory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "MedicalClaimRequestFrontService")
public class MedicalClaimRequestFrontService extends BaseService implements IMedicalClaimRequestFrontService {

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "MedicalClaimProposalDAO")
	private IMedicalClaimProposalDAO medicalClaimProposalDAO;

	@Resource(name = "MedicalPolicyDAO")
	private IMedicalPolicyDAO medicalPolicyDAO;

	@Resource(name = "MedicalClaimInitialRepDAO")
	private IMedicalClaimInitialRepDAO medicalClaimInitialRepDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalClaimProposal addNewMedicalClaim(MedicalClaimProposal medicalClaimProposal, ClaimInitialReport medicalClaimInitialReport,
			List<MedicalPolicyInsuredPersonBeneficiaries> polInsuPersonBeneficiaryList, WorkFlowDTO workflowDTO) {
		try {
			// TODO FIXME PSH
			// Question for old medical claim no
			medicalClaimProposal.setClaimRequestId(customIDGenerator.getNextId(SystemConstants.MEDICALCLAIMPROPOSAL_ID_NO, null, false));
			medicalPolicyDAO.updatePolicyInsuredPerson(medicalClaimProposal.getPolicyInsuredPerson());
			calculateClaimAmount(medicalClaimProposal);
			MedicalPolicyInsuredPerson policyInsuredPerson = medicalClaimProposal.getPolicyInsuredPerson();
			policyInsuredPerson.setPolicyInsuredPersonBeneficiariesList(new ArrayList<MedicalPolicyInsuredPersonBeneficiaries>());
			for (MedicalPolicyInsuredPersonBeneficiaries mBeneficiaries : polInsuPersonBeneficiaryList) {
				policyInsuredPerson.addInsuredPersonBeneficiaries(mBeneficiaries);
			}
			medicalClaimProposal = medicalClaimProposalDAO.insert(medicalClaimProposal);
			workflowDTO.setReferenceNo(medicalClaimProposal.getId());
			workFlowDTOService.addNewWorkFlow(workflowDTO);

			medicalClaimInitialReport.setClaimStatus(ClaimStatus.CLAIM_APPLIED);
			medicalClaimInitialRepDAO.update(medicalClaimInitialReport);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a Death Claim", e);
		}
		return medicalClaimProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalClaimProposalDTO updatMedicalClaim(MedicalClaimProposalDTO medicalClaimProposalDTO, WorkFlowDTO workflowDTO) {
		MedicalClaimProposal medicalClaimProposal;
		try {
			medicalClaimProposal = medicalClaimProposalDAO.update(MedicalClaimProposalFactory.createMedicalClaimProposal(medicalClaimProposalDTO));
			List<MedicalPolicyInsuredPersonBeneficiaries> polInsuPersonBeneficiaryList = medicalClaimProposal.getPolicyInsuredPerson().getPolicyInsuredPersonBeneficiariesList();
			for (MedicalPolicyInsuredPersonBeneficiaries mBeneficiaries : polInsuPersonBeneficiaryList) {
				medicalPolicyDAO.updatePolicyInsuBeneByBeneficiaryNo(mBeneficiaries);
			}
			workflowDTO.setReferenceNo(medicalClaimProposal.getId());
			workFlowDTOService.updateWorkFlow(workflowDTO, workflowDTO.getWorkflowTask());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Death Claim", e);
		}
		return medicalClaimProposalDTO;
	}

	public void calculateClaimAmount(MedicalClaimProposal medicalClaimProposal) {

		double claimAmount = 0.0;

		double total_hospClaimAmount = 0.0;
		double total_deathClaimAmount = 0.0;
		double total_medicationClaimAmount = 0.0;
		double total_operationClaimAmount = 0.0;

		MedicalPolicyInsuredPerson claimPerson = medicalClaimProposal.getPolicyInsuredPerson();
		int unit = claimPerson.getUnit();
		int totalHospitalizedDays = 0;
		HospitalizedClaim hospitalizedClaim = null;
		DeathClaim deathClaim = null;
		MedicationClaim medicationClaim = null;
		OperationClaim operationClaim = null;

		for (MedicalClaim medicalClaim : medicalClaimProposal.getMedicalClaimList()) {
			if (medicalClaim instanceof HospitalizedClaim) {
				hospitalizedClaim = (HospitalizedClaim) medicalClaim;
			}
			if (medicalClaim instanceof DeathClaim) {
				deathClaim = (DeathClaim) medicalClaim;
			}
			if (medicalClaim instanceof MedicationClaim) {
				medicationClaim = (MedicationClaim) medicalClaim;
			}
			if (medicalClaim instanceof OperationClaim) {
				operationClaim = (OperationClaim) medicalClaim;
			}
		}
		if (medicalClaimProposal.isDeath()) {
			claimAmount += deathClaim.getDeathClaimAmount();
		}
		if (medicalClaimProposal.isHospitalized()) {
			totalHospitalizedDays = Utils.daysBetween(hospitalizedClaim.getHospitalizedStartDate(), hospitalizedClaim.getHospitalizedEndDate(), false, true);
			claimAmount += hospitalizedClaim.getHospitalizedAmount();
		}
		if (medicalClaimProposal.isMedication()) {
			claimAmount += medicationClaim.getMedicationFee();
		}
		if (medicalClaimProposal.isOperation()) {
			claimAmount += operationClaim.getOperationFee() + operationClaim.getAbortionAmount();
		}
		if (medicalClaimProposal.getMedicalClaimBeneficiariesList() != null && medicalClaimProposal.getMedicalClaimBeneficiariesList().size() != 0) {
			for (MedicalClaimBeneficiary beneficiary : medicalClaimProposal.getMedicalClaimBeneficiariesList()) {
				if (beneficiary.getMedicalPolicyInsuredPersonBeneficiaries() != null) {
					double claimAmountByPer = claimAmount * beneficiary.getMedicalPolicyInsuredPersonBeneficiaries().getPercentage() / 100;
					double hospitalizedAmountByPer = total_hospClaimAmount * beneficiary.getMedicalPolicyInsuredPersonBeneficiaries().getPercentage() / 100;
					double operationAmountByPer = total_operationClaimAmount * beneficiary.getMedicalPolicyInsuredPersonBeneficiaries().getPercentage() / 100;
					double medicationAmountByPer = total_medicationClaimAmount * beneficiary.getMedicalPolicyInsuredPersonBeneficiaries().getPercentage() / 100;
					double deathClaimAmountByPer = total_deathClaimAmount * beneficiary.getMedicalPolicyInsuredPersonBeneficiaries().getPercentage() / 100;
					beneficiary.setClaimAmount(claimAmountByPer);
					beneficiary.setHospClaimAmount(hospitalizedAmountByPer);
					beneficiary.setOperClaimAmount(operationAmountByPer);
					beneficiary.setMediClaimAmount(medicationAmountByPer);
					beneficiary.setDeathClaimAmount(deathClaimAmountByPer);
					beneficiary.setUnit(unit);
					beneficiary.setNoOfHospDays(totalHospitalizedDays);
				} else if (beneficiary.getMedicalPolicyInsuredPerson() != null) {
					beneficiary.setClaimAmount(claimAmount);
					beneficiary.setHospClaimAmount(total_hospClaimAmount);
					beneficiary.setOperClaimAmount(total_operationClaimAmount);
					beneficiary.setMediClaimAmount(total_medicationClaimAmount);
					beneficiary.setDeathClaimAmount(total_deathClaimAmount);
					beneficiary.setUnit(unit);
					beneficiary.setNoOfHospDays(totalHospitalizedDays);
				} else {
					double claimAmountByPer = claimAmount * beneficiary.getPercentage() / 100;
					double hospitalizedAmountByPer = total_hospClaimAmount * beneficiary.getPercentage() / 100;
					double operationAmountByPer = total_operationClaimAmount * beneficiary.getPercentage() / 100;
					double medicationAmountByPer = total_medicationClaimAmount * beneficiary.getPercentage() / 100;
					double deathClaimAmountByPer = total_deathClaimAmount * beneficiary.getPercentage() / 100;
					beneficiary.setClaimAmount(claimAmountByPer);
					beneficiary.setHospClaimAmount(hospitalizedAmountByPer);
					beneficiary.setOperClaimAmount(operationAmountByPer);
					beneficiary.setMediClaimAmount(medicationAmountByPer);
					beneficiary.setDeathClaimAmount(deathClaimAmountByPer);
					beneficiary.setUnit(unit);
					beneficiary.setNoOfHospDays(totalHospitalizedDays);
				}

			}
		}
	}
}
