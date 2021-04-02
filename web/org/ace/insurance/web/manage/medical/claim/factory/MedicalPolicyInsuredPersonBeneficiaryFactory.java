package org.ace.insurance.web.manage.medical.claim.factory;

import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonBeneficiaryDTO;

public class MedicalPolicyInsuredPersonBeneficiaryFactory {

	public static MedicalPolicyInsuredPersonBeneficiaries createMedicalPolicyInsuredPersonBeneficiary(MedicalPolicyInsuredPersonBeneficiaryDTO benificiaryDTO) {
		MedicalPolicyInsuredPersonBeneficiaries medicalPolicyInsuredPersonBeneficiary = new MedicalPolicyInsuredPersonBeneficiaries();
		if (benificiaryDTO.isExistsEntity()) {
			medicalPolicyInsuredPersonBeneficiary.setId(benificiaryDTO.getId());
			medicalPolicyInsuredPersonBeneficiary.setVersion(benificiaryDTO.getVersion());
		}

		medicalPolicyInsuredPersonBeneficiary.setAge(benificiaryDTO.getAge());
		medicalPolicyInsuredPersonBeneficiary.setPercentage(benificiaryDTO.getPercentage());
		medicalPolicyInsuredPersonBeneficiary.setBeneficiaryNo(benificiaryDTO.getBeneficiaryNo());
		medicalPolicyInsuredPersonBeneficiary.setInitialId(benificiaryDTO.getInitialId());
		medicalPolicyInsuredPersonBeneficiary.setFatherName(benificiaryDTO.getFatherName());
		medicalPolicyInsuredPersonBeneficiary.setIdNo(benificiaryDTO.getIdNo());
		medicalPolicyInsuredPersonBeneficiary.setGender(benificiaryDTO.getGender());
		medicalPolicyInsuredPersonBeneficiary.setIdType(benificiaryDTO.getIdType());
		medicalPolicyInsuredPersonBeneficiary.setClaimStatus(benificiaryDTO.getClaimStatus());
		medicalPolicyInsuredPersonBeneficiary.setResidentAddress(benificiaryDTO.getResidentAddress());
		medicalPolicyInsuredPersonBeneficiary.setName(benificiaryDTO.getName());
		medicalPolicyInsuredPersonBeneficiary.setRelationship(benificiaryDTO.getRelationship());
		medicalPolicyInsuredPersonBeneficiary.setDeathDate(benificiaryDTO.getDeathDate());
		medicalPolicyInsuredPersonBeneficiary.setDeathReason(benificiaryDTO.getDeathReason());
		medicalPolicyInsuredPersonBeneficiary.setDeath(benificiaryDTO.isDeath());
		medicalPolicyInsuredPersonBeneficiary.setStateCode(benificiaryDTO.getStateCode());
		medicalPolicyInsuredPersonBeneficiary.setContentInfo(benificiaryDTO.getContentInfo());
		medicalPolicyInsuredPersonBeneficiary.setTownshipCode(benificiaryDTO.getTownshipCode());
		medicalPolicyInsuredPersonBeneficiary.setIdConditionType(benificiaryDTO.getIdConditionType());

		if (benificiaryDTO.getCommonCreateAndUpateMarks() != null) {
			medicalPolicyInsuredPersonBeneficiary.setCommonCreateAndUpateMarks(benificiaryDTO.getCommonCreateAndUpateMarks());
		}
		return medicalPolicyInsuredPersonBeneficiary;
	}

	public static MedicalPolicyInsuredPersonBeneficiaryDTO createMedicalPolicyInsuredPersonBeneficiaryDTO(
			MedicalPolicyInsuredPersonBeneficiaries medicalPolicyInsuredPersonBeneficiary) {
		MedicalPolicyInsuredPersonBeneficiaryDTO benificiaryDTO = new MedicalPolicyInsuredPersonBeneficiaryDTO();

		if (medicalPolicyInsuredPersonBeneficiary.getId() != null && (!medicalPolicyInsuredPersonBeneficiary.getId().isEmpty())) {
			benificiaryDTO.setId(medicalPolicyInsuredPersonBeneficiary.getId());
			benificiaryDTO.setVersion(medicalPolicyInsuredPersonBeneficiary.getVersion());
			benificiaryDTO.setExistsEntity(true);
		}

		benificiaryDTO.setAge(medicalPolicyInsuredPersonBeneficiary.getAge());
		benificiaryDTO.setPercentage(medicalPolicyInsuredPersonBeneficiary.getPercentage());
		benificiaryDTO.setBeneficiaryNo(medicalPolicyInsuredPersonBeneficiary.getBeneficiaryNo());
		benificiaryDTO.setInitialId(medicalPolicyInsuredPersonBeneficiary.getInitialId());
		benificiaryDTO.setFatherName(medicalPolicyInsuredPersonBeneficiary.getFatherName());
		benificiaryDTO.setIdNo(medicalPolicyInsuredPersonBeneficiary.getIdNo());
		benificiaryDTO.setGender(medicalPolicyInsuredPersonBeneficiary.getGender());
		benificiaryDTO.setIdType(medicalPolicyInsuredPersonBeneficiary.getIdType());
		benificiaryDTO.setClaimStatus(medicalPolicyInsuredPersonBeneficiary.getClaimStatus());
		benificiaryDTO.setResidentAddress(medicalPolicyInsuredPersonBeneficiary.getResidentAddress());
		benificiaryDTO.setName(medicalPolicyInsuredPersonBeneficiary.getName());
		benificiaryDTO.setRelationship(medicalPolicyInsuredPersonBeneficiary.getRelationship());
		benificiaryDTO.setDeathDate(medicalPolicyInsuredPersonBeneficiary.getDeathDate());
		benificiaryDTO.setDeathReason(medicalPolicyInsuredPersonBeneficiary.getDeathReason());
		benificiaryDTO.setDeath(medicalPolicyInsuredPersonBeneficiary.isDeath());
		benificiaryDTO.setStateCode(medicalPolicyInsuredPersonBeneficiary.getStateCode());
		benificiaryDTO.setTownshipCode(medicalPolicyInsuredPersonBeneficiary.getTownshipCode());
		benificiaryDTO.setContentInfo(medicalPolicyInsuredPersonBeneficiary.getContentInfo());
		benificiaryDTO.setIdConditionType(medicalPolicyInsuredPersonBeneficiary.getIdConditionType());
		if (medicalPolicyInsuredPersonBeneficiary.getCommonCreateAndUpateMarks() != null) {
			benificiaryDTO.setCommonCreateAndUpateMarks(medicalPolicyInsuredPersonBeneficiary.getCommonCreateAndUpateMarks());
		}
		return benificiaryDTO;
	}

}
