package org.ace.insurance.web.manage.medical.proposal.factory;

import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuAddOnDTO;

public class MedProInsuAddOnFactory {

	public static MedicalProposalInsuredPersonAddOn getMedicalProposalInsuredPersonAddOn(MedProInsuAddOnDTO dto) {
		MedicalProposalInsuredPersonAddOn insuredPersonAddOn = new MedicalProposalInsuredPersonAddOn();
		if (dto.isExistsEntity()) {
			insuredPersonAddOn.setId(dto.getId());
			insuredPersonAddOn.setVersion(dto.getVersion());
		}
		insuredPersonAddOn.setProposedPremium(dto.getProposedPremium());
		insuredPersonAddOn.setApprovedPremium(dto.getApprovedPremium());
		insuredPersonAddOn.setAddOn(dto.getAddOn());
		insuredPersonAddOn.setUnit(dto.getUnit());

		if (dto.getCommonCreateAndUpateMarks() != null) {
			insuredPersonAddOn.setCommonCreateAndUpateMarks(dto.getCommonCreateAndUpateMarks());
		}

		return insuredPersonAddOn;
	}

	public static MedProInsuAddOnDTO getMedProInsuAddOnDTO(MedicalProposalInsuredPersonAddOn insuredPersonAddOn) {
		MedProInsuAddOnDTO dto = new MedProInsuAddOnDTO();
		if (insuredPersonAddOn.getId() != null) {
			dto.setExistsEntity(true);
			dto.setId(insuredPersonAddOn.getId());
			dto.setVersion(insuredPersonAddOn.getVersion());
		}
		dto.setProposedPremium(insuredPersonAddOn.getProposedPremium());
		dto.setApprovedPremium(insuredPersonAddOn.getApprovedPremium());
		dto.setAddOn(insuredPersonAddOn.getAddOn());
		dto.setUnit(insuredPersonAddOn.getUnit());

		if (insuredPersonAddOn.getCommonCreateAndUpateMarks() != null) {
			dto.setCommonCreateAndUpateMarks(insuredPersonAddOn.getCommonCreateAndUpateMarks());
		}
		return dto;
	}
}
