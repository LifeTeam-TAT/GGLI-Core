package org.ace.insurance.web.manage.medical.proposal.validation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.CommonSettingConfig;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.common.ValidationUtil;
import org.ace.insurance.web.common.Validator;
import org.ace.insurance.web.manage.medical.proposal.CustomerDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProGuardianDTO;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "GuardianPersonValidator")
public class GuardianPersonValidator implements Validator<MedProGuardianDTO> {

	@Override
	public ValidationResult validate(MedProGuardianDTO dto) {
		CustomerDTO customer = dto.getCustomer();
		ValidationResult result = new ValidationResult();
		String formID = "guradianInfoEntryForm";

		if (ValidationUtil.isStringEmpty(customer.getName().getFirstName())) {
			result.addErrorMessage(formID + ":guarName", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (!IdType.STILL_APPLYING.equals(customer.getIdType())) {
			if (IdType.NRCNO.equals(customer.getIdType())) {
				if (customer.getIdNo() == null || ValidationUtil.isStringEmpty(customer.getIdNo())) {
					result.addErrorMessage(formID + ":guarIdNo", UIInput.REQUIRED_MESSAGE_ID);
				} else if ((customer.getStateCode() == null && customer.getTownshipCode() == null) || (customer.getStateCode() == null && customer.getTownshipCode() != null)
						|| (customer.getStateCode() != null && customer.getTownshipCode() == null)) {
					result.addErrorMessage(formID + ":guarIdNo", MessageId.NRC_STATE_AND_TOWNSHIP_ERROR);
				} else if (customer.getIdNo().length() != 6 && customer.getStateCode() != null && customer.getTownshipCode() != null) {
					result.addErrorMessage(formID + ":guarIdNo", MessageId.NRC_FORMAT_INCORRECT);
				}
			} else {
				customer.setIdConditionType(null);
				customer.setStateCode(null);
				customer.setTownshipCode(null);
				if (ValidationUtil.isStringEmpty(customer.getIdNo())) {
					result.addErrorMessage(formID + ":guarIdNo", UIInput.REQUIRED_MESSAGE_ID);
				}
			}

		} else {
			customer.setIdConditionType(null);
			customer.setStateCode(null);
			customer.setTownshipCode(null);
			customer.setIdNo(null);
		}
		if (customer.getDateOfBirth() == null || ValidationUtil.isStringEmpty(customer.getDateOfBirth().toString())) {
			result.addErrorMessage(formID + ":guarDateOfBirth", UIInput.REQUIRED_MESSAGE_ID);

		} else {
			if (Utils.getAgeForNextYear(customer.getDateOfBirth()) < CommonSettingConfig.getMinimumGuardianAge()) {
				result.addErrorMessage(formID + ":guarDateOfBirth", MessageId.MEDICAL_INSURED_PERSON_MINAGE, CommonSettingConfig.getMinimunMedicalAge());
			}
			// if (Utils.getAgeForNextYear(customer.getDateOfBirth()) >
			// CommonSettingConfig.getMaximunMedicalAge()) {
			// result.addErrorMessage(formID + ":guarDateOfBirth",
			// MessageId.MEDICAL_INSURED_PERSON_MAXAGE,
			// CommonSettingConfig.getMaximunMedicalAge());
			// }
		}

		if (ValidationUtil.isStringEmpty(customer.getPlaceOfBirth())) {
			result.addErrorMessage(formID + ":guarPersonPlaceOfBirth", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (ValidationUtil.isStringEmpty(customer.getResidentAddress().getResidentAddress())) {
			result.addErrorMessage(formID + ":guarResident", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getResidentAddress().getTownship() == null || ValidationUtil.isStringEmpty(customer.getResidentAddress().getTownship().getName())) {
			result.addErrorMessage(formID + ":guarTownShip", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getGender() == null) {
			result.addErrorMessage(formID + ":guarGender", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (customer.getHeight() < 1) {
			result.addErrorMessage(formID + ":guardianPersonHeight", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (customer.getWeight() < 1) {
			result.addErrorMessage(formID + ":guardianPersonWeight", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getOccupation() == null || (customer.getOccupation() != null && ValidationUtil.isStringEmpty(customer.getOccupation().getName()))) {
			result.addErrorMessage(formID + ":guarOccupation", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getCountry() == null || ValidationUtil.isStringEmpty(customer.getCountry().getName())) {
			result.addErrorMessage(formID + ":guardianPersonNationality", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (dto.getRelationship() == null) {
			result.addErrorMessage(formID + ":guarRelationShip", UIInput.REQUIRED_MESSAGE_ID);
		} else if (dto.getRelationship().getName().equalsIgnoreCase("---------")) {
			result.addErrorMessage(formID + ":guarRelationShip", UIInput.REQUIRED_MESSAGE_ID);
		}

		return result;
	}

}
