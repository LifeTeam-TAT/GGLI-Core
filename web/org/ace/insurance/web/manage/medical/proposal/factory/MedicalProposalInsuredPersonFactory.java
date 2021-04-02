package org.ace.insurance.web.manage.medical.proposal.factory;

import org.ace.insurance.medical.proposal.MedicalPersonHistoryRecord;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAttachment;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonKeyFactorValue;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuAddOnDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuAttDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuBeneDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuDTO;

public class MedicalProposalInsuredPersonFactory {
	public static MedicalProposalInsuredPerson getProposalInsuredPerson(MedProInsuDTO insuredPersonDTO) {
		MedicalProposalInsuredPerson insuredPerson = new MedicalProposalInsuredPerson();
		if (insuredPersonDTO.isExistsEntity()) {
			insuredPerson.setId(insuredPersonDTO.getId());
			insuredPerson.setVersion(insuredPersonDTO.getVersion());
		}
		insuredPerson.setIsPaidPremiumForPaidup(insuredPersonDTO.isPaidPremiumForPaidup());
		insuredPerson.setApproved(insuredPersonDTO.isApproved());
		insuredPerson.setNeedMedicalCheckup(insuredPersonDTO.isNeedMedicalCheckup());
		insuredPerson.setAge(insuredPersonDTO.getAge());
		insuredPerson.setPaymentTerm(insuredPersonDTO.getPaymentTerm());
		insuredPerson.setPeriodMonth(insuredPersonDTO.getPeriodYear() * 12);
		insuredPerson.setProposedPremium(insuredPersonDTO.getProposedPremium());
		insuredPerson.setProposedAddOnPremium(insuredPersonDTO.getAddOnPremium());
		insuredPerson.setApprovedPremium(insuredPersonDTO.getApprovedPremium());
		insuredPerson.setTotalNcbPremium(insuredPersonDTO.getTotalNcbPremium());
		insuredPerson.setRejectReason(insuredPersonDTO.getRejectReason());
		insuredPerson.setStartDate(insuredPersonDTO.getStartDate());
		insuredPerson.setEndDate(insuredPersonDTO.getEndDate());
		insuredPerson.setUnit(insuredPersonDTO.getUnit());
		insuredPerson.setBasicPlusUnit(insuredPersonDTO.getBasicPlusUnit());
		insuredPerson.setBasicPlusPremium(insuredPersonDTO.getBasicPlusPremium());
		insuredPerson.setRelationship(insuredPersonDTO.getRelationship());
		insuredPerson.setProduct(insuredPersonDTO.getProduct());
		insuredPerson.setSameCustomer(insuredPersonDTO.isSameInsuredPerson());
		insuredPerson.setFinishedApproved(insuredPersonDTO.isFinishedApproved());

		insuredPerson.setAddOnTermPremium(insuredPersonDTO.getAddOnTermPremium());
		insuredPerson.setBasicTermPremium(insuredPersonDTO.getBasicTermPremium());

		if (insuredPersonDTO.getCustomer() != null) {
			insuredPerson.setCustomer(CustomerFactory.getCustomer(insuredPersonDTO.getCustomer()));
		}
		if (insuredPersonDTO.getGuardianDTO() != null) {
			insuredPerson.setGuardian(GuardianFactory.getGuardian(insuredPersonDTO.getGuardianDTO()));
		}

		/**
		 * populate InsuredPersonBeneficiary data
		 */
		for (MedProInsuBeneDTO beneficiaryDto : insuredPersonDTO.getInsuredPersonBeneficiariesList()) {
			MedicalProposalInsuredPersonBeneficiaries beneficiary = MedicalProposalInsuredPersonBeneFactory.getProposalInsuredPersonBeneficiaries(beneficiaryDto);
			insuredPerson.addBeneficiaries(beneficiary);
		}

		/**
		 * populate InsuredPersonAddOn data
		 */
		for (MedProInsuAddOnDTO addOnDtO : insuredPersonDTO.getInsuredPersonAddOnDTOList()) {
			MedicalProposalInsuredPersonAddOn addOn = MedProInsuAddOnFactory.getMedicalProposalInsuredPersonAddOn(addOnDtO);
			insuredPerson.addInsuredPersonAddon(addOn);
		}

		for (MedicalPersonHistoryRecord historyRecord : insuredPersonDTO.getHistoryRecordList()) {
			insuredPerson.addHistoryRecord(historyRecord);
		}

		/**
		 * populate InsuredPersonAttachment data
		 */
		for (MedProInsuAttDTO attachmentDTO : insuredPersonDTO.getAttachmentList()) {
			MedicalProposalInsuredPersonAttachment attachment = MedicalProposalInsuPersonAttachmentFactory.getMedicalProposalInsuredPersonAttachment(attachmentDTO);
			insuredPerson.addAttachment(attachment);
		}

		// for (SurveyQuestionAnswerDTO questionDTO :
		// insuredPersonDTO.getSurveyQuestionAnsweDTOList()) {
		// SurveyQuestionAnswer question =
		// SurveyQuestionAnswerDTOFactroy.getSurveyQuestionAnswer(questionDTO);
		// insuredPerson.addSurveyQuestion(question);
		// }

		if (insuredPersonDTO.getCommonCreateAndUpateMarks() != null) {
			insuredPerson.setCommonCreateAndUpateMarks(insuredPersonDTO.getCommonCreateAndUpateMarks());
		}

		return insuredPerson;
	}

	public static MedProInsuDTO getMedProInsuDTO(MedicalProposalInsuredPerson insuredPerson) {
		MedProInsuDTO insuredPersonDTO = new MedProInsuDTO();
		if (insuredPerson.getId() != null) {
			insuredPersonDTO.setExistsEntity(true);
			insuredPersonDTO.setId(insuredPerson.getId());
			insuredPersonDTO.setVersion(insuredPerson.getVersion());
		}
		insuredPersonDTO.setPaidPremiumForPaidup(insuredPerson.getIsPaidPremiumForPaidup());
		insuredPersonDTO.setApproved(insuredPerson.isApproved());
		insuredPersonDTO.setNeedMedicalCheckup(insuredPerson.isNeedMedicalCheckup());
		insuredPersonDTO.setPaymentTerm(insuredPerson.getPaymentTerm());
		insuredPersonDTO.setAge(insuredPerson.getAge());
		insuredPersonDTO.setPeriodYear(insuredPerson.getPeriodMonth() / 12);
		insuredPersonDTO.setProposedPremium(insuredPerson.getProposedPremium());
		insuredPersonDTO.setAddOnPremium(insuredPerson.getProposedAddOnPremium());
		insuredPersonDTO.setTotalNcbPremium(insuredPerson.getTotalNcbPremium());
		insuredPersonDTO.setApprovedPremium(insuredPerson.getApprovedPremium());
		insuredPersonDTO.setRejectReason(insuredPerson.getRejectReason());
		insuredPersonDTO.setStartDate(insuredPerson.getStartDate());
		insuredPersonDTO.setEndDate(insuredPerson.getEndDate());
		insuredPersonDTO.setUnit(insuredPerson.getUnit());
		insuredPersonDTO.setBasicPlusUnit(insuredPerson.getBasicPlusUnit());
		insuredPersonDTO.setBasicPlusPremium(insuredPerson.getBasicPlusPremium());
		insuredPersonDTO.setRelationship(insuredPerson.getRelationship());
		insuredPersonDTO.setProduct(insuredPerson.getProduct());
		insuredPersonDTO.setSameInsuredPerson(insuredPerson.isSameCustomer());
		insuredPersonDTO.setFinishedApproved(insuredPerson.isFinishedApproved());

		insuredPersonDTO.setAddOnTermPremium(insuredPerson.getAddOnTermPremium());
		insuredPersonDTO.setBasicTermPremium(insuredPerson.getBasicTermPremium());

		if (insuredPerson.getCustomer() != null) {
			insuredPersonDTO.setCustomer(CustomerFactory.getCustomerDTO(insuredPerson.getCustomer()));
		}
		if (insuredPerson.getGuardian() != null) {
			insuredPersonDTO.setGuardianDTO(GuardianFactory.getGuardianDTO(insuredPerson.getGuardian()));
		}
		/**
		 * populate InsuredPersonBeneficiary data
		 */
		for (MedicalProposalInsuredPersonBeneficiaries beneficiary : insuredPerson.getInsuredPersonBeneficiariesList()) {
			MedProInsuBeneDTO beneficiaryDTO = MedicalProposalInsuredPersonBeneFactory.getMedProInsuBeneDTO(beneficiary);
			insuredPersonDTO.addBeneficiaries(beneficiaryDTO);
		}

		/**
		 * populate InsuredPersonAddOn data
		 */
		for (MedicalProposalInsuredPersonAddOn addOn : insuredPerson.getInsuredPersonAddOnList()) {
			MedProInsuAddOnDTO addOnDTO = MedProInsuAddOnFactory.getMedProInsuAddOnDTO(addOn);
			insuredPersonDTO.addInsuredPersonAddon(addOnDTO);
		}

		/**
		 * populate InsuredPersonKeyFactorValue data
		 */
		for (MedicalProposalInsuredPersonKeyFactorValue inskf : insuredPerson.getKeyFactorValueList()) {
			KeyFactor kf = inskf.getKeyFactor();
			if (KeyFactorChecker.isMedicalAge(kf)) {
				inskf.setValue(insuredPerson.getAge() + "");
			}
			insuredPersonDTO.addMedicalKeyFactorValue(inskf);
		}

		/**
		 * populate InsuredPersonAttachment data
		 */

		for (MedicalPersonHistoryRecord record : insuredPerson.getMedicalPersonHistoryRecordList()) {
			insuredPersonDTO.addHistoryRecord(record);
		}

		for (MedicalProposalInsuredPersonAttachment attachment : insuredPerson.getAttachmentList()) {
			MedProInsuAttDTO attachmentDTO = MedicalProposalInsuPersonAttachmentFactory.getMedicalProposalInsuredPersonAttachmentDTO(attachment);
			insuredPersonDTO.addInsuredPersonAttachment(attachmentDTO);
		}

		// for (SurveyQuestionAnswer question :
		// insuredPerson.getSurveyQuestionAnswerList()) {
		// SurveyQuestionAnswerDTO questionDTO =
		// SurveyQuestionAnswerDTOFactroy.getSurveyQuestionAnswerDTO(question);
		// insuredPersonDTO.addSurveyQuestion(questionDTO);
		// }

		if (insuredPerson.getCommonCreateAndUpateMarks() != null) {
			insuredPersonDTO.setCommonCreateAndUpateMarks(insuredPerson.getCommonCreateAndUpateMarks());
		}
		return insuredPersonDTO;
	}

}
