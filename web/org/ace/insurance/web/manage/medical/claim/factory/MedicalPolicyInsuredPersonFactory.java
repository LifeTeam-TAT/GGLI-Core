package org.ace.insurance.web.manage.medical.claim.factory;

import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAttachment;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonBeneficiaryDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.GuardianFactory;

public class MedicalPolicyInsuredPersonFactory {

	public static MedicalPolicyInsuredPerson createMedicalPolicyInsuredPerson(MedicalPolicyInsuredPersonDTO insuredPersonDTO) {
		MedicalPolicyInsuredPerson insuredPerson = new MedicalPolicyInsuredPerson();

		if (insuredPersonDTO.isExitsEntity()) {
			insuredPerson.setId(insuredPersonDTO.getId());
			insuredPerson.setVersion(insuredPersonDTO.getVersion());
		}
		insuredPerson.setDeath(insuredPersonDTO.isDeath());
		insuredPerson.setPeriodMonth(insuredPersonDTO.getPeriodMonth());
		insuredPerson.setAge(insuredPersonDTO.getAge());
		insuredPerson.setPaymentTerm(insuredPersonDTO.getPaymentTerm());
		insuredPerson.setPremium(insuredPersonDTO.getPremium());
		insuredPerson.setDateOfBirth(insuredPersonDTO.getDateOfBirth());
		insuredPerson.setStartDate(insuredPersonDTO.getStartDate());
		insuredPerson.setEndDate(insuredPersonDTO.getEndDate());
		insuredPerson.setUnit(insuredPersonDTO.getUnit());
		insuredPerson.setBasicPlusUnit(insuredPersonDTO.getBasicPlusUnit());
		insuredPerson.setBasicPlusPremium(insuredPersonDTO.getBasicPlusPremium());
		insuredPerson.setTotalDiscountPremium(insuredPersonDTO.getTotalDiscountPremium());
		insuredPerson.setClaimStatus(insuredPersonDTO.getClaimStatus());
		insuredPerson.setProduct(insuredPersonDTO.getProduct());
		insuredPerson.setCustomer(insuredPersonDTO.getCustomer());
		insuredPerson.setRelationship(insuredPersonDTO.getRelationship());
		insuredPerson.setBasicTermPremium(insuredPersonDTO.getBasicTermPremium());
		insuredPerson.setAddonTermPremium(insuredPersonDTO.getAddOnTermPremium());
		insuredPerson.setAddOnPremium(insuredPersonDTO.getAddOnPremium());
		// insuredPerson.setMedicalPolicy(MedicalPolicyFactory.createMedicalPolicy(insuredPersonDTO.getMedicalPolicyDTO()));

		if (insuredPersonDTO.getGuardian() != null) {
			insuredPerson.setGuardian(GuardianFactory.getPolicyGuardian(insuredPersonDTO.getGuardian()));
		}

		if (insuredPersonDTO.getAttachmentList() != null && insuredPersonDTO.getAttachmentList().size() != 0) {
			for (MedicalPolicyInsuredPersonAttachment mpa : insuredPersonDTO.getAttachmentList()) {
				insuredPerson.addAttachment(mpa);
			}
		}

		if (insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList() != null && insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList().size() != 0) {
			for (MedicalPolicyInsuredPersonBeneficiaryDTO insuredPersonBeneficiaryDTO : insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList()) {
				insuredPerson.addInsuredPersonBeneficiaries(MedicalPolicyInsuredPersonBeneficiaryFactory.createMedicalPolicyInsuredPersonBeneficiary(insuredPersonBeneficiaryDTO));
			}
		}

		for (MedicalPolicyInsuredPersonAddOn addOnDTO : insuredPersonDTO.getAddOnList()) {
			insuredPerson.addInsuredPersonAddOn(addOnDTO);
		}
		if (insuredPersonDTO.getCommonCreateAndUpateMarks() != null) {
			insuredPerson.setCommonCreateAndUpateMarks(insuredPersonDTO.getCommonCreateAndUpateMarks());
		}
		return insuredPerson;
	}

	public static MedicalPolicyInsuredPersonDTO createMedicalPolicyInsuredPersonDTO(MedicalPolicyInsuredPerson insuredPerson) {
		MedicalPolicyInsuredPersonDTO insuredPersonDTO = new MedicalPolicyInsuredPersonDTO();

		if (insuredPerson.getId() != null && (!insuredPerson.getId().isEmpty())) {
			insuredPersonDTO.setId(insuredPerson.getId());
			insuredPersonDTO.setVersion(insuredPerson.getVersion());
			insuredPersonDTO.setExitsEntity(true);
		}
		insuredPersonDTO.setDeath(insuredPerson.isDeath());
		insuredPersonDTO.setPeriodMonth(insuredPerson.getPeriodMonth());
		insuredPersonDTO.setAge(insuredPerson.getAge());
		insuredPersonDTO.setPaymentTerm(insuredPerson.getPaymentTerm());
		insuredPersonDTO.setPremium(insuredPerson.getPremium());
		insuredPersonDTO.setDateOfBirth(insuredPerson.getDateOfBirth());
		insuredPersonDTO.setStartDate(insuredPerson.getStartDate());
		insuredPersonDTO.setEndDate(insuredPerson.getEndDate());
		insuredPersonDTO.setUnit(insuredPerson.getUnit());
		insuredPersonDTO.setBasicPlusUnit(insuredPerson.getBasicPlusUnit());
		insuredPersonDTO.setBasicPlusPremium(insuredPerson.getBasicPlusPremium());
		insuredPersonDTO.setTotalDiscountPremium(insuredPerson.getTotalDiscountPremium());
		insuredPersonDTO.setClaimStatus(insuredPerson.getClaimStatus());
		insuredPersonDTO.setProduct(insuredPerson.getProduct());
		insuredPersonDTO.setCustomer(insuredPerson.getCustomer());
		insuredPersonDTO.setRelationship(insuredPerson.getRelationship());
		insuredPersonDTO.setBasicTermPremium(insuredPerson.getBasicTermPremium());
		insuredPersonDTO.setAddOnPremium(insuredPerson.getAddOnPremium());
		insuredPersonDTO.setAddOnTermPremium(insuredPerson.getAddOnTermPremium());
		insuredPersonDTO.setTotalHosDays(insuredPerson.getHosp_day_count());
		// insuredPersonDTO.setMedicalPolicyDTO(MedicalPolicyFactory.createMedicalPolicyDTO(insuredPerson.getMedicalPolicy()));
		if (insuredPerson.getGuardian() != null) {
			insuredPersonDTO.setGuardian(GuardianFactory.getPolicyGuardianDTO(insuredPerson.getGuardian()));
		}

		if (insuredPerson.getAttachmentList() != null && insuredPerson.getAttachmentList().size() != 0) {
			for (MedicalPolicyInsuredPersonAttachment mpa : insuredPerson.getAttachmentList()) {
				insuredPersonDTO.addMedicalPolicyInsuredPersonAttachment(mpa);
			}
		}

		for (MedicalPolicyInsuredPersonAddOn addOnDTO : insuredPerson.getPolicyInsuredPersonAddOnList()) {
			insuredPersonDTO.addAddOn(addOnDTO);
		}

		if (insuredPerson.getPolicyInsuredPersonBeneficiariesList() != null && insuredPerson.getPolicyInsuredPersonBeneficiariesList().size() != 0) {
			for (MedicalPolicyInsuredPersonBeneficiaries insuredPersonBeneficiary : insuredPerson.getPolicyInsuredPersonBeneficiariesList()) {
				insuredPersonDTO.addMedicalPolicyInsuredPersonBeneficiaryDTO(
						MedicalPolicyInsuredPersonBeneficiaryFactory.createMedicalPolicyInsuredPersonBeneficiaryDTO(insuredPersonBeneficiary));
			}
		}
		if (insuredPerson.getCommonCreateAndUpateMarks() != null) {
			insuredPersonDTO.setCommonCreateAndUpateMarks(insuredPerson.getCommonCreateAndUpateMarks());
		}
		return insuredPersonDTO;
	}
}
