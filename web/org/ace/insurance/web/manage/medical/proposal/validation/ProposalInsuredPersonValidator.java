package org.ace.insurance.web.manage.medical.proposal.validation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.CommonSettingConfig;
import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.web.common.Validator;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.common.ValidationUtil;
import org.ace.insurance.web.manage.medical.proposal.CustomerDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProGuardianDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuBeneDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuDTO;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ProposalInsuredPersonValidator")
public class ProposalInsuredPersonValidator implements Validator<MedProInsuDTO> {
	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{GuardianPersonValidator}")
	private Validator<MedProGuardianDTO> guardianValidator;

	public void setGuardianValidator(Validator<MedProGuardianDTO> guardianValidator) {
		this.guardianValidator = guardianValidator;
	}

	@Override
	public ValidationResult validate(MedProInsuDTO dto) {
		CustomerDTO customer = dto.getCustomer();
		ValidationResult result = new ValidationResult();
		String formID = "medicalProposalEntryForm";
		if (ValidationUtil.isStringEmpty(customer.getName().getFirstName())) {
			result.addErrorMessage(formID + ":name", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (!IdType.STILL_APPLYING.equals(customer.getIdType())) {
			if (IdType.NRCNO.equals(customer.getIdType())) {
				if (customer.getIdNo() == null || ValidationUtil.isStringEmpty(customer.getIdNo())) {
					result.addErrorMessage(formID + ":idNo", UIInput.REQUIRED_MESSAGE_ID);
				} else if ((customer.getStateCode() == null && customer.getTownshipCode() == null) || (customer.getStateCode() == null && customer.getTownshipCode() != null)
						|| (customer.getStateCode() != null && customer.getTownshipCode() == null)) {
					result.addErrorMessage(formID + ":idNo", MessageId.NRC_STATE_AND_TOWNSHIP_ERROR);
				} else if (customer.getIdNo().length() != 6 && customer.getStateCode() != null && customer.getTownshipCode() != null) {
					result.addErrorMessage(formID + ":idNo", MessageId.NRC_FORMAT_INCORRECT);
				}
			} else {
				customer.setIdConditionType(null);
				customer.setStateCode(null);
				customer.setTownshipCode(null);
				if (ValidationUtil.isStringEmpty(customer.getIdNo())) {
					result.addErrorMessage(formID + ":idNo", UIInput.REQUIRED_MESSAGE_ID);
				}
			}

		} else {
			customer.setIdConditionType(null);
			customer.setStateCode(null);
			customer.setTownshipCode(null);
			customer.setIdNo(null);
		}
		if (customer.getDateOfBirth() == null || ValidationUtil.isStringEmpty(customer.getDateOfBirth().toString())) {
			result.addErrorMessage(formID + ":dateOfBirth", UIInput.REQUIRED_MESSAGE_ID);
		} else if (HealthType.CRITICALILLNESS.equals(customer.getHealthType())) {
			if (Utils.getAgeForNextYear(customer.getDateOfBirth()) > CommonSettingConfig.getMaximunCriticalIllnessAge()) {
				result.addErrorMessage(formID + ":dateOfBirth", MessageId.MEDICAL_INSURED_PERSON_MAXAGE, CommonSettingConfig.getMaximunCriticalIllnessAge());
			}
			if (Utils.getAgeForNextYear(customer.getDateOfBirth()) < CommonSettingConfig.getMinimunMedicalAge()) {
				result.addErrorMessage(formID + ":dateOfBirth", MessageId.MEDICAL_INSURED_PERSON_MINAGE, CommonSettingConfig.getMinimumCriticalIllnessAge());
			}
		} else {
			if (Utils.getAgeForNextYear(customer.getDateOfBirth()) < CommonSettingConfig.getMinimunMedicalAge()) {
				result.addErrorMessage(formID + ":dateOfBirth", MessageId.MEDICAL_INSURED_PERSON_MINAGE, CommonSettingConfig.getMinimunMedicalAge());
			}
			if (Utils.getAgeForNextYear(customer.getDateOfBirth()) > CommonSettingConfig.getMaximunMedicalAge()) {
				result.addErrorMessage(formID + ":dateOfBirth", MessageId.MEDICAL_INSURED_PERSON_MAXAGE, CommonSettingConfig.getMaximunMedicalAge());
			}
		}

		if (ValidationUtil.isStringEmpty(customer.getPlaceOfBirth())) {
			result.addErrorMessage(formID + ":insuredPersonPlaceOfBirth", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (ValidationUtil.isStringEmpty(customer.getResidentAddress().getResidentAddress())) {
			result.addErrorMessage(formID + ":resident", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getResidentAddress().getTownship() == null || ValidationUtil.isStringEmpty(customer.getResidentAddress().getTownship().getName())) {
			result.addErrorMessage(formID + ":townShip", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getGender() == null) {
			result.addErrorMessage(formID + ":gender", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (customer.getHeight() < 1) {
			result.addErrorMessage(formID + ":insuredPersonHeight", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (customer.getWeight() < 1) {
			result.addErrorMessage(formID + ":insuredPersonWeight", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getOccupation() == null || (customer.getOccupation() != null && ValidationUtil.isStringEmpty(customer.getOccupation().getName()))) {
			result.addErrorMessage(formID + ":occupation", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (customer.getCountry() == null || ValidationUtil.isStringEmpty(customer.getCountry().getName())) {
			result.addErrorMessage(formID + ":insuredPersonNationality", UIInput.REQUIRED_MESSAGE_ID);
		}
		if (dto.getMedProDTO().getCustomerType().equals(CustomerType.INDIVIDUALCUSTOMER)) {
			if (dto.getRelationship() == null) {
				result.addErrorMessage(formID + ":beneficiaryRelationShip", UIInput.REQUIRED_MESSAGE_ID);
			} else if (dto.getRelationship().getName().equalsIgnoreCase("---------")) {
				result.addErrorMessage(formID + ":beneficiaryRelationShip", UIInput.REQUIRED_MESSAGE_ID);
			}
		}

		if (dto.getProduct() == null) {
			result.addErrorMessage(formID + ":product", UIInput.REQUIRED_MESSAGE_ID);
		}

		if (dto.getInsuredPersonBeneficiariesList() == null || dto.getInsuredPersonBeneficiariesList().size() < 1) {
			result.addErrorMessage(formID + ":beneficiaryPersonTable", MessageId.REQUIRE_BENEFICIARY);
		} else {
			double percentage = 0.0;
			for (MedProInsuBeneDTO beneficiary : dto.getInsuredPersonBeneficiariesList()) {
				percentage += beneficiary.getPercentage();
			}
			if (percentage != 100) {
				result.addErrorMessage(formID + ":beneficiaryPersonTable", MessageId.BENEFICIARY_PERCENTAGE_MUST_BE_100);
			}
		}
		if (customer != null && customer.getDateOfBirth() != null && !ValidationUtil.isStringEmpty(customer.getDateOfBirth().toString())) {
			if (Utils.getAgeForNextYear(customer.getDateOfBirth()) < 18) {
				if (dto.getGuardianDTO() == null) {
					result.addErrorMessage(formID + ":guradianPersonTable", MessageId.REQUIRE_GUARDIAN);
				} else {
					ValidationResult guarResult = guardianValidator.validate(dto.getGuardianDTO());
					if (!guarResult.isVerified()) {
						result.addErrorMessage(formID + ":guradianPersonTable", MessageId.REQUIRE_GUARDIAN);
					}
				}
			}
		}
		if (!HealthType.MICROHEALTH.equals(dto.getCustomer().getHealthType())) {
			if (dto.getUnit() < 1) {
				result.addErrorMessage(formID + ":Unit", UIInput.REQUIRED_MESSAGE_ID);
			}
			if (dto.getUnit() > 10) {
				result.addErrorMessage(formID + ":Unit", MessageId.MAX_UNIT, 10);
			}
		}
		return result;
	}
}
