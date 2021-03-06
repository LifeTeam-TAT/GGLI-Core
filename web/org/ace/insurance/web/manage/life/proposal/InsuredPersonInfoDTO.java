package org.ace.insurance.web.manage.life.proposal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.insurance.life.claim.ClaimStatus;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonAddon;
import org.ace.insurance.life.policy.PolicyInsuredPersonAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.PolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuranceHistoryRecord;
import org.ace.insurance.life.proposal.InsuredPersonAddon;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.gradeinfo.GradeInfo;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.riskyOccupation.RiskyOccupation;
import org.ace.insurance.system.common.school.School;
import org.ace.insurance.system.common.typesOfSport.TypesOfSport;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PeriodType;
import org.ace.insurance.web.common.SurveyAnswerOne;
import org.ace.insurance.web.common.SurveyAnswerTwo;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class InsuredPersonInfoDTO {
	private boolean isSameCustomer;
	private boolean existsEntity;
	private boolean approve;
	private boolean needMedicalCheckup;
	private Boolean isPaidPremiumForPaidup;
	private int periodMonth;
	private int paymentTerm;
	private int periodOfYears;
	private PeriodType periodType;
	private int age;
	private double premium;
	private double approvedSumInsured;
	private double approvedPremium;
	private double interest;
	private double basicTermPremium;
	private double addOnTermPremium;
	private double endorsementAddonPremium;
	private double endorsementBasicPremium;
	private int height;
	private int feets;
	private int inches;
	private int pounds;
	private int weight;
	private double bmi;
	private Double sumInsuredInfo;
	private String tempId;
	private String fatherName;
	private String parentName;
	private Date parentDOB;
	private IdType parentIdType;
	private String insPersonCodeNo;
	private String inPersonGroupCodeNo;
	private String initialId;
	private String rejectReason;
	private String fullIdNo;
	private String idNo;
	private String provinceCode;
	private String townshipCode;
	private String idConditionType;
	private SurveyAnswerOne surveyquestionOne;
	private SurveyAnswerTwo surveyquestionTwo;

	private String parentFullIdNo;
	private String parentIdNo;
	private String parentProvinceCode;
	private String parentTownshipCode;
	private String parentIdConditionType;

	private Date startDate;
	private Date endDate;
	private Date dateOfBirth;
	private Product product;
	private Occupation occupation;
	private RiskyOccupation riskyOccupation;
	private Customer customer;
	private Name name;
	private String id;
	private Gender gender;
	private IdType idType;
	private SumInsuredType sumInsuredType;
	private TypesOfSport typesOfSport;
	private ResidentAddress residentAddress;
	private RelationShip relationShip;
	private School school;
	private GradeInfo gradeInfo;
	private ClassificationOfHealth classificationOfHealth;
	private EndorsementStatus endorsementStatus;
	private ClaimStatus claimStatus;
	
	// for customer excel upload
	private ContentInfo contentInfo;
	
	private List<InsuredPersonAttachment> perAttachmentList;
	private List<InsuredPersonKeyFactorValue> keyFactorValueList;
	private List<BeneficiariesInfoDTO> beneficiariesInfoDTOList;
	private Map<String, InsuredPersonAddOnDTO> insuredPersonAddOnDTOMap = new HashMap<String, InsuredPersonAddOnDTO>();
	private List<InsuredPersonAddOnDTO> insuredPersonAddOnDTOList;
	private List<Attachment> birthCertificateAttachments;
	private List<InsuranceHistoryRecord> insuranceHistoryRecord;
	private List<SurveyQuestionAnswer> surveyQuestionAnswerList;

	private List<PolicyInsuredPersonAttachment> policyPerAttachmentList;
	private List<Attachment> policyAttachmentList;
	private List<PolicyInsuredPersonKeyFactorValue> policyKeyFactorValueList;
	private int unit;

	private int periodOfWeek;

	public int getPeriodOfWeek() {
		return periodOfWeek;
	}

	public void setPeriodOfWeek(int periodOfWeek) {
		this.periodOfWeek = periodOfWeek;
	}

	private int version;

	public boolean isApprove() {
		return approve;
	}

	public void setApprove(boolean approve) {
		this.approve = approve;
	}

	public InsuredPersonInfoDTO() {
		tempId = System.nanoTime() + "";
	}

	public InsuredPersonInfoDTO(ProposalInsuredPerson proposal) {
		if (proposal.getId() == null) {
			this.tempId = System.nanoTime() + "";
		} else {
			existsEntity = true;
			this.approve = proposal.isApproved();
			this.tempId = proposal.getId();
			this.version = proposal.getVersion();
		}

		if (this.isPaidPremiumForPaidup == null) {
			this.isPaidPremiumForPaidup = false;
		}

		this.needMedicalCheckup = proposal.isNeedMedicalCheckup();
		this.periodType = proposal.getPeriodType();
		this.paymentTerm = proposal.getPaymentTerm();
		this.periodMonth = proposal.getPeriodMonth();
		if (KeyFactorChecker.isGroupLife(proposal.getProduct()) || KeyFactorChecker.isPublicLife(proposal.getProduct())
				|| KeyFactorChecker.isSinglePremiumCreditLife(proposal.getProduct()) || KeyFactorChecker.isShortTermSinglePremiumCreditLife(proposal.getProduct())
				|| KeyFactorChecker.isSinglePremiumEndowmentLife(proposal.getProduct()) || KeyFactorChecker.isShortTermEndowment(proposal.getProduct().getId())
				|| KeyFactorChecker.isStudentLife(proposal.getProduct().getId())) {
			this.periodOfYears = proposal.getPeriodMonth() / 12;
		} else {
			this.periodOfYears = proposal.getPeriodMonth();
		}

		if (KeyFactorChecker.isSimpleLife(proposal.getProduct())) {
			this.periodOfWeek = proposal.getPeriodWeek();
			this.periodOfYears = proposal.getPeriodYear();
		}
		this.age = proposal.getAge();
		this.weight = proposal.getWeight();
		this.height = proposal.getHeight();
		this.feets = proposal.getHeight() / 12;
		this.inches = proposal.getHeight() % 12;
		this.bmi = proposal.getBmi();
		this.surveyquestionOne = proposal.getSurveyquestionOne();
		this.surveyquestionTwo = proposal.getSurveyquestionTwo();
		this.sumInsuredInfo = proposal.getProposedSumInsured();
		this.premium = proposal.getProposedPremium();
		this.approvedSumInsured = proposal.getApprovedSumInsured();
		this.approvedPremium = proposal.getApprovedPremium();
		this.basicTermPremium = proposal.getBasicTermPremium();
		this.addOnTermPremium = proposal.getAddOnTermPremium();
		this.endorsementBasicPremium = proposal.getEndorsementNetBasicPremium();
		this.endorsementAddonPremium = proposal.getEndorsementNetAddonPremium();
		this.interest = proposal.getInterest();
		this.approve = proposal.isApproved();
		this.rejectReason = proposal.getRejectReason();
		this.insPersonCodeNo = proposal.getInsPersonCodeNo();
		this.inPersonGroupCodeNo = proposal.getInPersonGroupCodeNo();
		this.initialId = proposal.getInitialId();
		this.idNo = proposal.getIdNo();
		this.fullIdNo = proposal.getIdNo();
		this.parentDOB = proposal.getParentDOB();
		this.parentName = proposal.getParentName();
		this.parentFullIdNo = proposal.getParentIdNo();
		this.parentIdNo = proposal.getParentIdNo();
		this.fatherName = proposal.getFatherName();
		this.startDate = proposal.getStartDate();
		this.endDate = proposal.getEndDate();
		this.dateOfBirth = proposal.getDateOfBirth();
		this.endorsementStatus = proposal.getEndorsementStatus();
		this.classificationOfHealth = proposal.getClsOfHealth();
		this.gender = proposal.getGender();
		this.idType = proposal.getIdType();
		this.parentIdType = proposal.getParentIdType();
		this.residentAddress = proposal.getResidentAddress();
		this.relationShip = proposal.getRelationship();
		this.school = proposal.getSchool();
		this.gradeInfo = proposal.getGradeInfo();
		this.name = proposal.getName();
		this.id = proposal.getId();
		this.product = proposal.getProduct();
		this.typesOfSport = proposal.getTypesOfSport();
		this.occupation = proposal.getOccupation();
		this.riskyOccupation = proposal.getRiskyOccupation();
		this.customer = proposal.getCustomer();
		this.unit = proposal.getUnit();
		this.sumInsuredType = proposal.getSumInsuredType();
//		this.contentInfo = proposal.getContentInfo();

		for (InsuredPersonAttachment attach : proposal.getAttachmentList()) {
			addInsuredPersonAttachment(attach);
		}
		for (Attachment attach : proposal.getBirthCertificateAttachment()) {
			addBirthCertificateAttachment(attach);
		}
		for (InsuredPersonKeyFactorValue kfv : proposal.getKeyFactorValueList()) {
			addInsuredPersonKeyFactorValue(kfv);
		}
		for (InsuredPersonBeneficiaries beneficiary : proposal.getInsuredPersonBeneficiariesList()) {
			addBeneficiariesInfoDTO(new BeneficiariesInfoDTO(beneficiary));
		}
		for (InsuredPersonAddon addOn : proposal.getInsuredPersonAddOnList()) {
			insuredPersonAddOnDTOMap.put(addOn.getId(), new InsuredPersonAddOnDTO(addOn));
		}
		for (InsuranceHistoryRecord record : proposal.getInsuranceHistoryRecord()) {
			getInsuranceHistoryRecord().add(record);
		}
		for (SurveyQuestionAnswer answer : proposal.getSurveyQuestionAnswerList()) {
			if (surveyQuestionAnswerList == null) {
				surveyQuestionAnswerList = new ArrayList<SurveyQuestionAnswer>();
			}
			surveyQuestionAnswerList.add(answer);
		}
	}

	public InsuredPersonInfoDTO(PolicyInsuredPerson pi) {
		if (pi.getId() == null) {
			this.tempId = System.nanoTime() + "";
		} else {
			existsEntity = true;
			this.tempId = pi.getId();
			this.version = pi.getVersion();
		}
		this.periodMonth = pi.getPeriodMonth();
		this.periodType = pi.getPeriodType();
		if (KeyFactorChecker.isGroupLife(pi.getProduct()) || KeyFactorChecker.isPublicLife(pi.getProduct()) || KeyFactorChecker.isSinglePremiumEndowmentLife(pi.getProduct())
				|| KeyFactorChecker.isSinglePremiumCreditLife(pi.getProduct()) || KeyFactorChecker.isShortTermSinglePremiumCreditLife(pi.getProduct())
				|| KeyFactorChecker.isShortTermEndowment(pi.getProduct().getId())) {
			this.periodOfYears = pi.getPeriodMonth() / 12;
		} else {
			this.periodOfYears = pi.getPeriodMonth();
		}
		this.paymentTerm = pi.getPaymentTerm();
		this.age = pi.getAge();
		this.weight = pi.getWeight();
		this.height = pi.getHeight();
		this.feets = pi.getHeight() / 12;
		this.inches = pi.getHeight() % 12;
		this.bmi = pi.getBmi();
		this.surveyquestionOne = pi.getSurveyquestionOne();
		this.surveyquestionTwo = pi.getSurveyquestionTwo();
		this.sumInsuredInfo = pi.getSumInsured();
		this.premium = pi.getPremium();
		this.basicTermPremium = pi.getBasicTermPremium();
		this.addOnTermPremium = pi.getAddOnTermPremium();
		this.endorsementBasicPremium = pi.getEndorsementNetBasicPremium();
		this.endorsementAddonPremium = pi.getEndorsementNetAddonPremium();
		this.interest = pi.getInterest();
		this.insPersonCodeNo = pi.getInsPersonCodeNo();
		this.inPersonGroupCodeNo = pi.getInPersonGroupCodeNo();
		this.initialId = pi.getInitialId();
		this.idNo = pi.getIdNo();
		this.fullIdNo = pi.getIdNo();
		this.fatherName = pi.getFatherName();
		this.dateOfBirth = pi.getDateOfBirth();
		this.startDate = pi.getStartDate();
		this.endDate = pi.getEndDate();
		this.gender = pi.getGender();
		this.idType = pi.getIdType();
		this.endorsementStatus = pi.getEndorsementStatus();
		this.claimStatus = pi.getClaimStatus();
		this.classificationOfHealth = pi.getClsOfHealth();
		this.residentAddress = pi.getResidentAddress();
		this.name = pi.getName();
		this.product = pi.getProduct();
		this.occupation = pi.getOccupation();
		this.riskyOccupation = pi.getRiskyOccupation();
		this.customer = pi.getCustomer();
		this.typesOfSport = pi.getTypesOfSport();
		this.relationShip = pi.getRelationShip();
		this.school = pi.getSchool();
		this.gradeInfo = pi.getGradeInfo();
		this.unit = pi.getUnit();
		this.sumInsuredType = pi.getSumInsuredType();
//		this.contentInfo = pi.getContentInfo();

		for (PolicyInsuredPersonAttachment attach : pi.getAttachmentList()) {
			addPolicyInsuredPersonAttachment(attach);
		}
		for (Attachment attach : pi.getBirthCertificateAttachment()) {
			addPolicyBirthCertificateAttachment(attach);
		}
		for (PolicyInsuredPersonKeyFactorValue kfv : pi.getPolicyInsuredPersonkeyFactorValueList()) {
			addPolicyInsuredPersonKeyFactorValue(kfv);
		}
		for (PolicyInsuredPersonBeneficiaries beneficiary : pi.getPolicyInsuredPersonBeneficiariesList()) {
			addBeneficiariesInfoDTO(new BeneficiariesInfoDTO(beneficiary));
		}
		for (PolicyInsuredPersonAddon addOn : pi.getPolicyInsuredPersonAddOnList()) {
			insuredPersonAddOnDTOMap.put(addOn.getId(), new InsuredPersonAddOnDTO(addOn));
		}

	}

	public InsuredPersonInfoDTO(InsuredPersonInfoDTO insuredPersonInfoDTO) {

		if (insuredPersonInfoDTO.getId() == null && tempId == null) {
			this.tempId = System.nanoTime() + "";
		} else {
			existsEntity = true;
			this.tempId = insuredPersonInfoDTO.getId();
			this.version = insuredPersonInfoDTO.getVersion();
		}

		if (this.isPaidPremiumForPaidup == null) {
			this.isPaidPremiumForPaidup = false;
		}
		if (tempId != null) {
			this.tempId = insuredPersonInfoDTO.getTempId();
		}
		this.needMedicalCheckup = insuredPersonInfoDTO.isNeedMedicalCheckup();
		this.paymentTerm = insuredPersonInfoDTO.getPaymentTerm();
		this.periodMonth = insuredPersonInfoDTO.getPeriodMonth();
		this.periodType = insuredPersonInfoDTO.getPeriodType();
		if (KeyFactorChecker.isGroupLife(insuredPersonInfoDTO.getProduct()) || KeyFactorChecker.isPublicLife(insuredPersonInfoDTO.getProduct())
				|| KeyFactorChecker.isSinglePremiumEndowmentLife(insuredPersonInfoDTO.getProduct()) || KeyFactorChecker.isSinglePremiumCreditLife(insuredPersonInfoDTO.getProduct())
				|| KeyFactorChecker.isShortTermSinglePremiumCreditLife(insuredPersonInfoDTO.getProduct())
				|| KeyFactorChecker.isShortTermEndowment(insuredPersonInfoDTO.getProduct().getId())) {
			this.periodOfYears = insuredPersonInfoDTO.getPeriodMonth() / 12;
		} else {
			this.periodOfYears = insuredPersonInfoDTO.getPeriodMonth();
		}
		this.age = insuredPersonInfoDTO.getAge();
		this.approvedSumInsured = insuredPersonInfoDTO.getApprovedSumInsured();
		this.approvedPremium = insuredPersonInfoDTO.getApprovedPremium();
		this.approve = insuredPersonInfoDTO.isApprove();
		this.basicTermPremium = insuredPersonInfoDTO.getBasicTermPremium();
		this.addOnTermPremium = insuredPersonInfoDTO.getAddOnTermPremium();
		this.endorsementBasicPremium = insuredPersonInfoDTO.getEndorsementNetBasicPremium();
		this.endorsementAddonPremium = insuredPersonInfoDTO.getEndorsementNetAddonPremium();
		this.interest = insuredPersonInfoDTO.getInterest();
		this.weight = insuredPersonInfoDTO.getWeight();
		this.height = insuredPersonInfoDTO.getHeight();
		this.feets = insuredPersonInfoDTO.getHeight() / 12;
		this.inches = insuredPersonInfoDTO.getHeight() % 12;
		this.bmi = insuredPersonInfoDTO.getBmi();
		this.surveyquestionOne = insuredPersonInfoDTO.getSurveyquestionOne();
		this.surveyquestionTwo = insuredPersonInfoDTO.getSurveyquestionTwo();
		this.sumInsuredInfo = insuredPersonInfoDTO.getSumInsuredInfo();
		this.rejectReason = insuredPersonInfoDTO.getRejectReason();
		this.insPersonCodeNo = insuredPersonInfoDTO.getInsPersonCodeNo();
		this.inPersonGroupCodeNo = insuredPersonInfoDTO.getInPersonGroupCodeNo();
		this.initialId = insuredPersonInfoDTO.getInitialId();
		this.idNo = insuredPersonInfoDTO.getIdNo();
		this.fullIdNo = insuredPersonInfoDTO.getFullIdNo();
		this.provinceCode = insuredPersonInfoDTO.getProvinceCode();
		this.townshipCode = insuredPersonInfoDTO.getTownshipCode();
		this.parentName = insuredPersonInfoDTO.getParentName();
		this.parentDOB = insuredPersonInfoDTO.getParentDOB();
		this.parentIdNo = insuredPersonInfoDTO.getParentIdNo();
		this.parentFullIdNo = insuredPersonInfoDTO.getParentFullIdNo();
		this.parentIdType = insuredPersonInfoDTO.getParentIdType();
		this.fatherName = insuredPersonInfoDTO.getFatherName();
		this.startDate = insuredPersonInfoDTO.getStartDate();
		this.endDate = insuredPersonInfoDTO.getEndDate();
		this.dateOfBirth = insuredPersonInfoDTO.getDateOfBirth();
		this.endorsementStatus = insuredPersonInfoDTO.getEndorsementStatus();
		this.gender = insuredPersonInfoDTO.getGender();
		this.idType = insuredPersonInfoDTO.getIdType();
		this.residentAddress = insuredPersonInfoDTO.getResidentAddress();
		this.name = insuredPersonInfoDTO.getName();
		this.id = insuredPersonInfoDTO.getId();
		this.product = insuredPersonInfoDTO.getProduct();
		this.typesOfSport = insuredPersonInfoDTO.getTypesOfSport();
		this.occupation = insuredPersonInfoDTO.getOccupation();
		this.riskyOccupation = insuredPersonInfoDTO.getRiskyOccupation();
		this.customer = insuredPersonInfoDTO.getCustomer();
		this.unit = insuredPersonInfoDTO.getUnit();
		this.relationShip = insuredPersonInfoDTO.getRelationShip();
		this.school = insuredPersonInfoDTO.getSchool();
		this.gradeInfo = insuredPersonInfoDTO.getGradeInfo();
		this.classificationOfHealth = insuredPersonInfoDTO.getClassificationOfHealth();
		this.sumInsuredType = insuredPersonInfoDTO.getSumInsuredType();
		this.contentInfo = insuredPersonInfoDTO.getContentInfo();

		for (InsuredPersonKeyFactorValue kfv : insuredPersonInfoDTO.getKeyFactorValueList()) {
			addInsuredPersonKeyFactorValue(kfv);
		}
		for (BeneficiariesInfoDTO beneficiary : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
			addBeneficiariesInfoDTO(new BeneficiariesInfoDTO(beneficiary));
		}
		for (InsuredPersonAttachment attach : insuredPersonInfoDTO.getPerAttachmentList()) {
			addInsuredPersonAttachment(attach);
		}
		for (Attachment attach : insuredPersonInfoDTO.getBirthCertificateAttachments()) {
			addBirthCertificateAttachment(attach);
		}
		for (InsuranceHistoryRecord record : insuredPersonInfoDTO.getInsuranceHistoryRecord()) {
			addInsuranceHistoryRecord(record);
		}
		for (InsuranceHistoryRecord record : insuredPersonInfoDTO.getInsuranceHistoryRecord()) {
			getInsuranceHistoryRecord().add(record);
		}
		for (SurveyQuestionAnswer answer : insuredPersonInfoDTO.getSurveyQuestionAnswerList()) {
			if (surveyQuestionAnswerList == null) {
				surveyQuestionAnswerList = new ArrayList<SurveyQuestionAnswer>();
			}
			surveyQuestionAnswerList.add(answer);
		}

	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public Double getSumInsuredInfo() {
		if (sumInsuredInfo == null) {
			sumInsuredInfo = new Double(0.0);
		}
		return sumInsuredInfo;
	}

	public void setSumInsuredInfo(Double sumInsuredInfo) {
		this.sumInsuredInfo = sumInsuredInfo;
	}

	public Number getSumInsuredInfoNum() {
		if (sumInsuredInfo == null) {
			sumInsuredInfo = new Double(0.0);
		}
		return sumInsuredInfo;
	}

	public void setSumInsuredInfoNum(Number sumInsuredInfo) {
		if (sumInsuredInfo != null) {
			this.sumInsuredInfo = sumInsuredInfo.doubleValue();
		}
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public int getPeriodOfYears() {
		return Math.abs(periodOfYears);
	}

	public void setPeriodOfYears(int periodOfYears) {
		this.periodOfYears = periodOfYears;
	}

	public int getPeriodOfMonths() {
		if (KeyFactorChecker.isPublicLife(product) || KeyFactorChecker.isGroupLife(product) || KeyFactorChecker.isShortTermEndowment(product.getId())
				|| KeyFactorChecker.isSinglePremiumCreditLife(product) || KeyFactorChecker.isShortTermSinglePremiumCreditLife(product)
				|| KeyFactorChecker.isSinglePremiumEndowmentLife(product) || KeyFactorChecker.isStudentLife(product.getId())) {
			return periodOfYears * 12;
		} else {
			return periodOfYears;
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
		loadKeyFactor(product);
	}

	private void loadKeyFactor(Product product) {
		keyFactorValueList = new ArrayList<InsuredPersonKeyFactorValue>();
		if (product.getKeyFactorList() != null) {
			for (KeyFactor kf : product.getKeyFactorList()) {
				InsuredPersonKeyFactorValue insKf = new InsuredPersonKeyFactorValue(kf);
				insKf.setKeyFactor(kf);
				keyFactorValueList.add(insKf);
			}
		}
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getInPersonGroupCodeNo() {
		return inPersonGroupCodeNo;
	}

	public void setInPersonGroupCodeNo(String inPersonGroupCodeNo) {
		this.inPersonGroupCodeNo = inPersonGroupCodeNo;
	}

	public List<InsuredPersonAddOnDTO> getInsuredPersonAddOnDTOList() {
		return insuredPersonAddOnDTOList;
	}

	public List<InsuredPersonBeneficiaries> getBeneficiariesInfoList(ProposalInsuredPerson proposalInsuredPerson) {
		List<InsuredPersonBeneficiaries> result = null;
		if (beneficiariesInfoDTOList != null && !beneficiariesInfoDTOList.isEmpty()) {
			result = new ArrayList<InsuredPersonBeneficiaries>();
			for (BeneficiariesInfoDTO beneficiairesInfoDTO : beneficiariesInfoDTOList) {
				InsuredPersonBeneficiaries insPesBenf = new InsuredPersonBeneficiaries(beneficiairesInfoDTO);
				insPesBenf.setProposalInsuredPerson(proposalInsuredPerson);
				result.add(insPesBenf);
			}
		}
		return result;

	}

	public List<PolicyInsuredPersonBeneficiaries> getBeneficiariesInfoList(PolicyInsuredPerson policyInsuredPerson) {
		List<PolicyInsuredPersonBeneficiaries> result = null;
		if (beneficiariesInfoDTOList != null && !beneficiariesInfoDTOList.isEmpty()) {
			result = new ArrayList<PolicyInsuredPersonBeneficiaries>();
			for (BeneficiariesInfoDTO beneficiairesInfoDTO : beneficiariesInfoDTOList) {
				PolicyInsuredPersonBeneficiaries insPesBenf = new PolicyInsuredPersonBeneficiaries(beneficiairesInfoDTO);
				insPesBenf.setPolicyInsuredPerson(policyInsuredPerson);
				result.add(insPesBenf);
			}
		}
		return result;

	}

	public List<InsuredPersonAddon> getInsuredPersonAddOnList(ProposalInsuredPerson proposalInsuredPerson) {
		List<InsuredPersonAddon> result = new ArrayList<InsuredPersonAddon>();
		if (insuredPersonAddOnDTOList != null && !insuredPersonAddOnDTOList.isEmpty()) {
			for (InsuredPersonAddOnDTO insuredPersonAddonDTO : insuredPersonAddOnDTOList) {
				InsuredPersonAddon insAddOn = new InsuredPersonAddon(insuredPersonAddonDTO.getAddOn(), insuredPersonAddonDTO.getAddOnSumInsured());
				insAddOn.setProposalInsuredPerson(proposalInsuredPerson);
				result.add(insAddOn);
			}
		}
		return result;
	}

	public List<PolicyInsuredPersonAddon> getInsuredPersonAddOnList(PolicyInsuredPerson policyInsuredPerson) {
		List<PolicyInsuredPersonAddon> result = new ArrayList<PolicyInsuredPersonAddon>();
		if (insuredPersonAddOnDTOMap.values() != null) {
			for (InsuredPersonAddOnDTO insuredPersonAddonDTO : insuredPersonAddOnDTOMap.values()) {
				PolicyInsuredPersonAddon insAddOn = new PolicyInsuredPersonAddon(insuredPersonAddonDTO.getAddOn(), insuredPersonAddonDTO.getAddOnSumInsured());
				insAddOn.setPolicyInsuredPerson(policyInsuredPerson);
				result.add(insAddOn);
			}
		}
		return result;
	}

	public List<InsuredPersonKeyFactorValue> getKeyFactorValueList(ProposalInsuredPerson proposalInsuredPerson) {
		for (InsuredPersonKeyFactorValue inskf : keyFactorValueList) {
			inskf.setProposalInsuredPerson(proposalInsuredPerson);
		}
		return keyFactorValueList;
	}

	public List<PolicyInsuredPersonKeyFactorValue> getKeyFactorValueList(PolicyInsuredPerson proposalInsuredPerson) {
		for (PolicyInsuredPersonKeyFactorValue inskf : policyKeyFactorValueList) {
			inskf.setPolicyInsuredPerson(proposalInsuredPerson);
		}
		return policyKeyFactorValueList;
	}

	public int getAgeForNextYear() {
		Calendar cal_1 = Calendar.getInstance();
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(dateOfBirth);
		cal_2.set(Calendar.YEAR, currentYear);

		if (new Date().after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dateOfBirth);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dateOfBirth);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

	public boolean isExistsEntity() {
		return existsEntity;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getEndorsementNetAddonPremium() {
		return endorsementAddonPremium;
	}

	public void setEndorsementNetAddonPremium(double endorsementAddonPremium) {
		this.endorsementAddonPremium = endorsementAddonPremium;
	}

	public double getEndorsementNetBasicPremium() {
		return endorsementBasicPremium;
	}

	public void setEndorsementNetBasicPremium(double endorsementBasicPremium) {
		this.endorsementBasicPremium = endorsementBasicPremium;
	}

	public String getInsPersonCodeNo() {
		return insPersonCodeNo;
	}

	public void setInsPersonCodeNo(String insPersonCodeNo) {
		this.insPersonCodeNo = insPersonCodeNo;
	}

	public EndorsementStatus getEndorsementStatus() {
		return endorsementStatus;
	}

	public void setEndorsementStatus(EndorsementStatus endorsementStatus) {
		this.endorsementStatus = endorsementStatus;
	}

	@Override
	public boolean equals(Object object) {
		if (tempId.equals(((InsuredPersonInfoDTO) object).getTempId())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getTownshipCode() {
		return townshipCode;
	}

	public void setTownshipCode(String townshipCode) {
		this.townshipCode = townshipCode;
	}

	public String getParentProvinceCode() {
		return parentProvinceCode;
	}

	public void setParentProvinceCode(String parentProvinceCode) {
		this.parentProvinceCode = parentProvinceCode;
	}

	public String getParentTownshipCode() {
		return parentTownshipCode;
	}

	public void setParentTownshipCode(String parentTownshipCode) {
		this.parentTownshipCode = parentTownshipCode;
	}

	public String getIdConditionType() {
		return idConditionType;
	}

	public void setIdConditionType(String idConditionType) {
		this.idConditionType = idConditionType;
	}

	public String getParentIdConditionType() {
		return parentIdConditionType;
	}

	public void setParentIdConditionType(String parentIdConditionType) {
		this.parentIdConditionType = parentIdConditionType;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public void setFullIdNo(String fullIdNo) {
		this.fullIdNo = fullIdNo;
	}

	public String getParentFullIdNo() {
		return parentFullIdNo;
	}

	public void setParentFullIdNo(String parentFullIdNo) {
		this.parentFullIdNo = parentFullIdNo;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public SumInsuredType getSumInsuredType() {
		return sumInsuredType;
	}

	public void setSumInsuredType(SumInsuredType sumInsuredType) {
		this.sumInsuredType = sumInsuredType;
	}

	public ResidentAddress getResidentAddress() {
		if (residentAddress == null) {
			residentAddress = new ResidentAddress();
		}
		return residentAddress;
	}

	public void setResidentAddress(ResidentAddress residentAddress) {
		this.residentAddress = residentAddress;
	}

	public String getFullAddress() {
		String result = "";
		if (residentAddress.getResidentAddress() != null) {
			if (residentAddress.getResidentAddress() != null && !residentAddress.getResidentAddress().isEmpty()) {
				result = residentAddress.getResidentAddress();
			}
			if (residentAddress.getTownship() != null) {
				result = result + " " + residentAddress.getTownship().getName();
			}
		}
		return result;
	}

	public Name getName() {
		if (name == null) {
			name = new Name();
		}
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getFullName() {
		String result = "";
		if (name != null) {
			if (initialId != null && !initialId.isEmpty()) {
				result = initialId;
			}
			if (name.getFirstName() != null && !name.getFirstName().isEmpty()) {
				result = result + " " + name.getFirstName();
			}
			if (name.getMiddleName() != null && !name.getMiddleName().isEmpty()) {
				result = result + " " + name.getMiddleName();
			}
			if (name.getLastName() != null && !name.getLastName().isEmpty()) {
				result = result + " " + name.getLastName();
			}
		}
		return result;
	}

	public Occupation getOccupation() {
		return occupation;
	}

	public void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getParentName() {
		if (parentName == null) {
			parentName = "";
		}
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Date getParentDOB() {
		return parentDOB;
	}

	public void setParentDOB(Date parentDOB) {
		this.parentDOB = parentDOB;
	}

	public IdType getParentIdType() {
		return parentIdType;
	}

	public void setParentIdType(IdType parentIdType) {
		this.parentIdType = parentIdType;
	}

	public String getParentIdNo() {
		return parentIdNo;
	}

	public void setParentIdNo(String parentIdNo) {
		this.parentIdNo = parentIdNo;
	}

	public ClassificationOfHealth getClassificationOfHealth() {
		return classificationOfHealth;
	}

	public void setClassificationOfHealth(ClassificationOfHealth classificationOfHealth) {
		this.classificationOfHealth = classificationOfHealth;
	}

	public Boolean getIsPaidPremiumForPaidup() {
		return isPaidPremiumForPaidup;
	}

	public void setIsPaidPremiumForPaidup(Boolean isPaidPremiumForPaidup) {
		this.isPaidPremiumForPaidup = isPaidPremiumForPaidup;
	}

	public TypesOfSport getTypesOfSport() {
		return typesOfSport;
	}

	public void setTypesOfSport(TypesOfSport typesOfSport) {
		this.typesOfSport = typesOfSport;
	}

	public int getPeriodMonth() {
		return periodMonth;
	}

	public void setPeriodMonth(int periodMonth) {
		this.periodMonth = periodMonth;
	}

	public int getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	public double getAddOnTermPremium() {
		return addOnTermPremium;
	}

	public void setAddOnTermPremium(double addOnTermPremium) {
		this.addOnTermPremium = addOnTermPremium;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getApprovedSumInsured() {
		return approvedSumInsured;
	}

	public void setApprovedSumInsured(double approvedSumInsured) {
		this.approvedSumInsured = approvedSumInsured;
	}

	public double getApprovedPremium() {
		return approvedPremium;
	}

	public void setApprovedPremium(double approvedPremium) {
		this.approvedPremium = approvedPremium;
	}

	public double getEndorsementAddonPremium() {
		return endorsementAddonPremium;
	}

	public void setEndorsementAddonPremium(double endorsementAddonPremium) {
		this.endorsementAddonPremium = endorsementAddonPremium;
	}

	public double getEndorsementBasicPremium() {
		return endorsementBasicPremium;
	}

	public void setEndorsementBasicPremium(double endorsementBasicPremium) {
		this.endorsementBasicPremium = endorsementBasicPremium;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public ClaimStatus getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(ClaimStatus claimStatus) {
		this.claimStatus = claimStatus;
	}

	public boolean isNeedMedicalCheckup() {
		return needMedicalCheckup;
	}

	public void setNeedMedicalCheckup(boolean needMedicalCheckup) {
		this.needMedicalCheckup = needMedicalCheckup;
	}

	public void addInsuredPersonAddOn(InsuredPersonAddOnDTO insuPersonAddOnDTO) {
		boolean flag = true;
		for (InsuredPersonAddOnDTO fa : insuredPersonAddOnDTOMap.values()) {
			if (insuPersonAddOnDTO.getAddOn().getId().equals(fa.getAddOn().getId())) {
				flag = false;
				break;
			}
		}
		if (flag) {
			insuredPersonAddOnDTOMap.put(insuPersonAddOnDTO.getTempId(), insuPersonAddOnDTO);
		}
	}

	public void removeInsuredPersonAddOn(InsuredPersonAddOnDTO insuPersonAddOnDTO) {
		insuredPersonAddOnDTOList.remove(insuPersonAddOnDTO);
		// insuredPersonAddOnDTOMap.remove(insuPersonAddOnDTO.getTempId());
	}

	public void removeInsuranceHistoryRecord(InsuranceHistoryRecord record) {
		insuranceHistoryRecord.remove(record);
	}

	public List<InsuredPersonAttachment> getPerAttachmentList() {
		if (perAttachmentList == null) {
			perAttachmentList = new ArrayList<InsuredPersonAttachment>();
		}
		return perAttachmentList;
	}

	public List<SurveyQuestionAnswer> getSurveyQuestionAnswerList() {
		if (surveyQuestionAnswerList == null) {
			surveyQuestionAnswerList = new ArrayList<SurveyQuestionAnswer>();
		}
		return surveyQuestionAnswerList;
	}

	public void setSurveyQuestionAnswerList(List<SurveyQuestionAnswer> surveyQuestionAnswerList) {
		this.surveyQuestionAnswerList = surveyQuestionAnswerList;
	}

	public void setPerAttachmentList(List<InsuredPersonAttachment> perAttachmentList) {
		this.perAttachmentList = perAttachmentList;
	}

	public List<Attachment> getBirthCertificateAttachments() {
		if (birthCertificateAttachments == null) {
			birthCertificateAttachments = new ArrayList<Attachment>();
		}
		return birthCertificateAttachments;
	}

	public void setBirthCertificateAttachments(List<Attachment> birthCertificateAttachments) {
		this.birthCertificateAttachments = birthCertificateAttachments;
	}

	public List<InsuranceHistoryRecord> getInsuranceHistoryRecord() {
		if (insuranceHistoryRecord == null) {
			insuranceHistoryRecord = new ArrayList<InsuranceHistoryRecord>();
		}
		return insuranceHistoryRecord;
	}

	public void setInsuranceHistoryRecord(List<InsuranceHistoryRecord> insuranceHistoryRecord) {
		this.insuranceHistoryRecord = insuranceHistoryRecord;
	}

	public void addInsuredPersonAttachment(InsuredPersonAttachment attach) {
		getPerAttachmentList().add(attach);
	}

	public void addBirthCertificateAttachment(Attachment attach) {
		if (birthCertificateAttachments == null) {
			birthCertificateAttachments = new ArrayList<Attachment>();
		}
		birthCertificateAttachments.add(attach);
	}

	public void addInsuranceHistoryRecord(InsuranceHistoryRecord record) {
		if (insuranceHistoryRecord == null) {
			insuranceHistoryRecord = new ArrayList<InsuranceHistoryRecord>();
		}
		insuranceHistoryRecord.add(record);
	}

	public List<InsuredPersonKeyFactorValue> getKeyFactorValueList() {
		if (keyFactorValueList == null) {
			keyFactorValueList = new ArrayList<InsuredPersonKeyFactorValue>();
		}
		return keyFactorValueList;
	}

	public void setKeyFactorValueList(List<InsuredPersonKeyFactorValue> keyFactorValueList) {
		this.keyFactorValueList = keyFactorValueList;
	}

	public void addInsuredPersonKeyFactorValue(InsuredPersonKeyFactorValue kfv) {
		getKeyFactorValueList().add(kfv);
	}

	public List<BeneficiariesInfoDTO> getBeneficiariesInfoDTOList() {
		if (beneficiariesInfoDTOList == null) {
			beneficiariesInfoDTOList = new ArrayList<BeneficiariesInfoDTO>();
		}
		return beneficiariesInfoDTOList;
	}

	public void setBeneficiariesInfoDTOList(List<BeneficiariesInfoDTO> beneficiariesInfoDTO1List) {
		this.beneficiariesInfoDTOList = beneficiariesInfoDTO1List;
	}

	public void addBeneficiariesInfoDTO(BeneficiariesInfoDTO dto) {
		getBeneficiariesInfoDTOList().add(dto);
	}

	public Map<String, InsuredPersonAddOnDTO> getInsuredPersonAddOnDTOMap() {
		if (insuredPersonAddOnDTOMap == null) {
			insuredPersonAddOnDTOMap = new HashMap<String, InsuredPersonAddOnDTO>();
		}
		return insuredPersonAddOnDTOMap;
	}

	public List<PolicyInsuredPersonAttachment> getPrePolicyAttachmentList() {
		if (policyPerAttachmentList == null) {
			policyPerAttachmentList = new ArrayList<PolicyInsuredPersonAttachment>();
		}
		return policyPerAttachmentList;
	}

	public void setPrePolicyAttachmentList(List<PolicyInsuredPersonAttachment> perAttachmentList) {
		this.policyPerAttachmentList = perAttachmentList;
	}

	public List<Attachment> getPolicyAttachmentList() {
		return policyAttachmentList;
	}

	public void setPolicyAttachmentList(List<Attachment> policyAttachmentList) {
		this.policyAttachmentList = policyAttachmentList;
	}

	public void addPolicyInsuredPersonAttachment(PolicyInsuredPersonAttachment attach) {
		getPrePolicyAttachmentList().add(attach);
	}

	public void addPolicyBirthCertificateAttachment(Attachment attach) {
		getPolicyAttachmentList().add(attach);
	}

	public List<PolicyInsuredPersonKeyFactorValue> getPolicyKeyFactorValueList() {
		if (policyKeyFactorValueList == null) {
			policyKeyFactorValueList = new ArrayList<PolicyInsuredPersonKeyFactorValue>();
		}
		return policyKeyFactorValueList;
	}

	public void setPolicyKeyFactorValueList(List<PolicyInsuredPersonKeyFactorValue> policyKeyFactorValueList) {
		this.policyKeyFactorValueList = policyKeyFactorValueList;
	}

	public void addPolicyInsuredPersonKeyFactorValue(PolicyInsuredPersonKeyFactorValue kfv) {
		getPolicyKeyFactorValueList().add(kfv);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public RelationShip getRelationShip() {
		return relationShip;
	}

	public void setRelationShip(RelationShip relationShip) {
		this.relationShip = relationShip;
	}

	public School getSchool() {
		return school;
	}

	public String getSchoolName() {
		if (school != null && school.getId() != null) {
			return school.getName();
		}
		return "-";
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public GradeInfo getGradeInfo() {
		return gradeInfo;
	}

	public String getGradeInfoName() {
		if (gradeInfo != null) {
			return gradeInfo.getName();
		}
		return "-";
	}

	public void setGradeInfo(GradeInfo gradeInfo) {
		this.gradeInfo = gradeInfo;
	}

	public void setInsuredPersonAddOnDTOList(List<InsuredPersonAddOnDTO> insuredPersonAddOnDTOList) {
		this.insuredPersonAddOnDTOList = insuredPersonAddOnDTOList;
	}

	public void loadFullIdNo() {
		if (idType.equals(IdType.NRCNO) && fullIdNo != null) {
			StringTokenizer token = new StringTokenizer(fullIdNo, "/()");
			provinceCode = token.nextToken();
			townshipCode = token.nextToken();
			idConditionType = token.nextToken();
			idNo = token.nextToken();
			fullIdNo = provinceCode == null ? "" : fullIdNo;
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO)) {
			idNo = fullIdNo == null ? "" : fullIdNo;
		}
	}

	public String setFullIdNo() {
		if (idType.equals(IdType.NRCNO)) {
			fullIdNo = provinceCode + "/" + townshipCode + "(" + idConditionType + ")" + idNo;
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO)) {
			fullIdNo = idNo;
		}
		return fullIdNo;
	}

	public void loadParentFullIdNo() {
		if (parentIdType.equals(IdType.NRCNO) && parentFullIdNo != null && !parentFullIdNo.isEmpty()) {
			StringTokenizer token = new StringTokenizer(parentFullIdNo, "/()");
			parentProvinceCode = token.nextToken();
			parentTownshipCode = token.nextToken();
			parentIdConditionType = token.nextToken();
			parentIdNo = token.nextToken();
			parentFullIdNo = parentProvinceCode.equals("null") ? "" : parentFullIdNo;
		} else if (parentIdType.equals(IdType.FRCNO) || parentIdType.equals(IdType.PASSPORTNO)) {
			parentIdNo = parentFullIdNo == null ? "" : parentFullIdNo;
		}
	}

	public String setParentFullIdNo() {
		if (parentIdType.equals(IdType.NRCNO)) {
			parentFullIdNo = parentProvinceCode + "/" + parentTownshipCode + "(" + parentIdConditionType + ")" + parentIdNo;
		} else if (parentIdType.equals(IdType.FRCNO) || parentIdType.equals(IdType.PASSPORTNO)) {
			parentFullIdNo = parentIdNo;
		}
		return parentFullIdNo;
	}

	public String getPeriod() {
		if (periodMonth > 0) {
			if (periodMonth / 12 < 1) {
				return periodMonth + " - Months";
			} else {
				return periodMonth / 12 + " - Year";
			}
		} else if (periodOfYears > 0) {
			return periodOfYears + " - Year";
		} else {
			return periodOfWeek + " - Week";
		}
	}

	/* for student life */
	public int getPremiumTerm() {
		return periodOfYears - 3;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFeets() {
		return feets;
	}

	public void setFeets(int feets) {
		this.feets = feets;
	}

	public int getInches() {
		return inches;
	}

	public void setInches(int inches) {
		this.inches = inches;
	}

	public int getPounds() {
		return pounds;
	}

	public void setPounds(int pounds) {
		int calPound;
		if (pounds > 0) {
			if (weight == pounds) {
				pounds = weight - pounds;
			} else if (weight > pounds) {
				calPound = pounds * 20 / 100;
				calPound = pounds + calPound;
				if (calPound >= weight) {
					pounds = 0;
				} else {
					pounds = weight - calPound;
				}
			} else {
				calPound = pounds * 15 / 100;
				calPound = pounds - calPound;
				if (calPound <= weight) {
					pounds = 0;
				} else {
					pounds = weight - calPound;
				}
				pounds = calPound - weight;
			}
		}
		pounds = Math.abs(pounds);
		pounds = (int) Math.ceil(pounds);
		this.pounds = pounds;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public RiskyOccupation getRiskyOccupation() {
		return riskyOccupation;
	}

	public void setRiskyOccupation(RiskyOccupation riskyOccupation) {
		this.riskyOccupation = riskyOccupation;
	}

	public boolean isSameCustomer() {
		return isSameCustomer;
	}

	public void setSameCustomer(boolean isSameCustomer) {
		this.isSameCustomer = isSameCustomer;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

	public double getBmi() {
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}

	public SurveyAnswerOne getSurveyquestionOne() {
		return surveyquestionOne;
	}

	public void setSurveyquestionOne(SurveyAnswerOne surveyquestionOne) {
		this.surveyquestionOne = surveyquestionOne;
	}

	public SurveyAnswerTwo getSurveyquestionTwo() {
		return surveyquestionTwo;
	}

	public void setSurveyquestionTwo(SurveyAnswerTwo surveyquestionTwo) {
		this.surveyquestionTwo = surveyquestionTwo;
	}
	
	public ContentInfo getContentInfo() {
		if (contentInfo == null) {
			contentInfo = new ContentInfo();
		}
		return contentInfo;
	}

	public void setContentInfo(ContentInfo contentInfo) {
		this.contentInfo = contentInfo;
	}
	
	public String getPeriodValue() {
		
		switch (periodType) {
		case YEAR:
			return String.valueOf(periodOfYears).concat(" - YEAR");
		
			
		case MONTH:
			
			return  String.valueOf(periodMonth).concat(" - MONTH");
			
		case WEEK:
			
			return  String.valueOf(periodOfWeek).concat(" - WEEK");

		default:
			
			return  "0";
			
		}
		
		
		 
		
	}
}
