package org.ace.insurance.web.manage.medical.proposal.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalAttachment;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonKeyFactorValue;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.medical.claim.factory.MedicalPolicyFactory;
import org.ace.insurance.web.manage.medical.proposal.MedProAttDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuDTO;
import org.ace.insurance.web.manage.medical.proposal.OrganizationDTO;

public class MedicalProposalFactory {

	public static MedicalProposal getMedicalProposal(MedProDTO proposalDTO) {

		MedicalProposal proposal = new MedicalProposal();
		if (proposalDTO.isExistsEntity()) {
			proposal.setVersion(proposalDTO.getVersion());
			proposal.setId(proposalDTO.getId());
		}
		proposal.setGroupMicroHealthID(proposalDTO.getGroupMicroHealthID());
		proposal.setProposalNo(proposalDTO.getProposalNo());
		proposal.setProposalType(proposalDTO.getProposalType());
		proposal.setSubmittedDate(proposalDTO.getSubmittedDate());
		proposal.setBranch(proposalDTO.getBranch());
		proposal.setPaymentType(proposalDTO.getPaymentType());
		proposal.setAgent(proposalDTO.getAgent());
		proposal.setSaleMan(proposalDTO.getSaleMan());
		proposal.setCustomerType(proposalDTO.getCustomerType());
		proposal.setSaleChannelType(proposalDTO.getSaleChannelType());
		proposal.setStaffPlan(proposalDTO.isStaffPlan());
		proposal.setEips(proposalDTO.getEips());
		if (null != proposalDTO.getSalePoint()) {
			proposal.setSalePoint(proposalDTO.getSalePoint());
		}

		if (proposalDTO.getMedicalPolicyDTO() != null) {
			proposal.setOldMedicalPolicy(MedicalPolicyFactory.createMedicalPolicy(proposalDTO.getMedicalPolicyDTO()));
		}

		// populate InsuredPerson Data
		for (MedProInsuDTO insuredPersonDTO : proposalDTO.getMedProInsuDTOList()) {
			MedicalProposalInsuredPerson insuredPerson = MedicalProposalInsuredPersonFactory.getProposalInsuredPerson(insuredPersonDTO);
			insuredPerson.getKeyFactorValueList().clear();
			for (MedicalProposalInsuredPersonKeyFactorValue inskf : insuredPersonDTO.getKeyFactorValueList()) {
				KeyFactor kf = inskf.getKeyFactor();
				if (KeyFactorChecker.isGender(kf)) {
					inskf.setValue(insuredPerson.getCustomer().getGender() + "");
				} else if (KeyFactorChecker.isMedicalAge(kf)) {
					inskf.setValue(org.ace.insurance.common.Utils.getAgeForNextYear(insuredPerson.getCustomer().getDateOfBirth()) + "");
				} else {
					inskf.setValue(proposal.getPaymentType().getId() + "");
				}
				insuredPerson.addMedicalKeyFactorValue(inskf);
			}
			proposal.addMedicalProposalInsuredPerson(insuredPerson);
		}
		// populate Customer and Referral Data
		if (proposalDTO.getCustomer() != null) {
			proposal.setCustomer(CustomerFactory.getCustomer(proposalDTO.getCustomer()));
		}
		if (proposalDTO.getOrganization() != null) {
			proposal.setOrganization(new Organization(proposalDTO.getOrganization()));
		}
		if (proposalDTO.getReferral() != null) {
			proposal.setReferral(CustomerFactory.getCustomer(proposalDTO.getReferral()));
		}
		// populate proposal attached
		for (MedProAttDTO attachmentDTO : proposalDTO.getAttachmentList()) {
			MedicalProposalAttachment attachment = MedicalProposalAttachmentFactory.getMedicalProposalAttachment(attachmentDTO);
			proposal.addAttachment(attachment);
		}
		if (proposalDTO.getCommonCreateAndUpateMarks() != null) {
			proposal.setCommonCreateAndUpateMarks(proposalDTO.getCommonCreateAndUpateMarks());
		}
		return proposal;
	}

	public static MedProDTO getMedProDTO(MedicalProposal proposal) {
		MedProDTO proposalDTO = new MedProDTO();
		if (proposal.getId() != null) {
			proposalDTO.setExistsEntity(true);
			proposalDTO.setVersion(proposal.getVersion());
			proposalDTO.setId(proposal.getId());
		}
		proposalDTO.setGroupMicroHealthID(proposal.getGroupMicroHealthID());
		proposalDTO.setProposalNo(proposal.getProposalNo());
		proposalDTO.setProposalType(proposal.getProposalType());
		proposalDTO.setSubmittedDate(proposal.getSubmittedDate());
		proposalDTO.setBranch(proposal.getBranch());
		proposalDTO.setPaymentType(proposal.getPaymentType());
		proposalDTO.setAgent(proposal.getAgent());
		proposalDTO.setSaleMan(proposal.getSaleMan());
		proposalDTO.setCustomerType(proposal.getCustomerType());
		proposalDTO.setSaleChannelType(proposal.getSaleChannelType());
		proposalDTO.setStaffPlan(proposal.isStaffPlan());
		proposalDTO.setEips(proposal.getEips());
		
		if (null != proposal.getSalePoint()) {
			proposalDTO.setSalePoint(proposal.getSalePoint());
		}
		if (proposal.getOldMedicalPolicy() != null) {
			proposalDTO.setMedicalPolicyDTO(MedicalPolicyFactory.createMedicalPolicyDTO(proposal.getOldMedicalPolicy()));
		}
		// populate InsuredPerson Data
		List<MedProInsuDTO> medProInsuDTOList = new ArrayList<MedProInsuDTO>();
		for (MedicalProposalInsuredPerson insuredPerson : proposal.getMedicalProposalInsuredPersonList()) {
			MedProInsuDTO insuredPersonDTO = MedicalProposalInsuredPersonFactory.getMedProInsuDTO(insuredPerson);
			insuredPersonDTO.getKeyFactorValueList().clear();
			for (MedicalProposalInsuredPersonKeyFactorValue inskf : insuredPerson.getKeyFactorValueList()) {
				KeyFactor kf = inskf.getKeyFactor();
				if (KeyFactorChecker.isPaymentType(kf)) {
					inskf.setValue(proposalDTO.getPaymentType() + "");
				} else if (KeyFactorChecker.isMedicalAge(kf)) {
					inskf.setValue(org.ace.insurance.common.Utils.getAgeForNextYear(insuredPersonDTO.getCustomer().getDateOfBirth()) + "");
				} else if (KeyFactorChecker.isGender(kf)) {
					inskf.setValue(insuredPersonDTO.getCustomer().getGender() + "");
				}
				insuredPersonDTO.addMedicalKeyFactorValue(inskf);
			}
			medProInsuDTOList.add(insuredPersonDTO);
			proposalDTO.addInsuredPerson(insuredPersonDTO);
		}
		// proposalDTO.setMedProInsuDTOList(medProInsuDTOList);

		// populate Customer and Referral Data
		if (proposal.getCustomer() != null) {
			proposalDTO.setCustomer(CustomerFactory.getCustomerDTO(proposal.getCustomer()));
		}
		if (proposal.getOrganization() != null) {
			proposalDTO.setOrganization(new OrganizationDTO(proposal.getOrganization()));
		}
		if (proposal.getReferral() != null) {
			proposalDTO.setReferral(CustomerFactory.getCustomerDTO(proposal.getReferral()));
		}

		// populate proposal attached
		for (MedicalProposalAttachment attachment : proposal.getAttachmentList()) {
			MedProAttDTO attachmentDTO = MedicalProposalAttachmentFactory.getMedicalProposalAttachmentDTO(attachment);
			proposalDTO.addAttachment(attachmentDTO);
		}

		if (proposal.getCommonCreateAndUpateMarks() != null) {
			proposalDTO.setCommonCreateAndUpateMarks(proposal.getCommonCreateAndUpateMarks());
		}
		return proposalDTO;
	}
}
