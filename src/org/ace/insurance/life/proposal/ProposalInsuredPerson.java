package org.ace.insurance.life.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonAddon;
import org.ace.insurance.life.policy.PolicyInsuredPersonAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.PolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.gradeinfo.GradeInfo;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.riskyOccupation.RiskyOccupation;
import org.ace.insurance.system.common.school.School;
import org.ace.insurance.system.common.typesOfSport.TypesOfSport;
import org.ace.insurance.web.common.PeriodType;
import org.ace.insurance.web.common.SurveyAnswerOne;
import org.ace.insurance.web.common.SurveyAnswerTwo;
import org.ace.insurance.web.manage.life.proposal.BeneficiariesInfoDTO;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonAddOnDTO;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.java.component.FormatID;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PROPOSALINSUREDPERSON)
@TableGenerator(name = "PRLINSURPERSON_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PRLINSURPERSON_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "ProposalInsuredPerson.findAll", query = "SELECT s FROM ProposalInsuredPerson s "),
		@NamedQuery(name = "ProposalInsuredPerson.findById", query = "SELECT s FROM ProposalInsuredPerson s WHERE s.id = :id") })
@Access(value = AccessType.FIELD)
@EntityListeners(IDInterceptor.class)
public class ProposalInsuredPerson implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean approved;
	private boolean needMedicalCheckup;
	private int paymentTerm;
	@Column(name = "PERIODOFMONTH")
	private int periodMonth;
	@Column(name = "PERIODOFYEAR")
	private int periodYear;
	@Column(name = "PERIODOFWEEK")
	private int periodWeek;
	@Column(name = "AGE")
	private int age;
	@Enumerated(value = EnumType.STRING)
	private PeriodType periodType;
	private double proposedSumInsured;
	private double proposedPremium;
	private double approvedSumInsured;
	private double approvedPremium;
	private double basicTermPremium;
	private double addOnTermPremium;
	private double endorsementNetBasicPremium;;
	private double endorsementNetAddonPremium;
	private double interest;
	@Transient
	private String id;
	private String prefix;
	private String rejectReason;
	@Column(name = "INPERSONCODENO")
	private String insPersonCodeNo;
	@Column(name = "INPERSONGROUPCODENO")
	private String inPersonGroupCodeNo;
	private String initialId;
	private String idNo;
	private String fatherName;
	private String parentName;
	private String parentIdNo;
	private SurveyAnswerOne surveyquestionOne;
	private SurveyAnswerTwo surveyquestionTwo;
	@Enumerated(value = EnumType.STRING)
	private IdType parentIdType;
	private Date parentDOB;
	private int weight;
	private int height;
	private double bmi;

	@Transient
	private Boolean isPaidPremiumForPaidup;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;

	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@Enumerated(EnumType.STRING)
	private EndorsementStatus endorsementStatus;

	@Enumerated(value = EnumType.STRING)
	private ClassificationOfHealth clsOfHealth;

	@Enumerated(value = EnumType.STRING)
	private Gender gender;

	@Enumerated(value = EnumType.STRING)
	private IdType idType;
	private int unit;
	@Embedded
	private ResidentAddress residentAddress;

	@Embedded
	private Name name;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TYPESOFSPORTID", referencedColumnName = "ID")
	private TypesOfSport typesOfSport;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCCUPATIONID", referencedColumnName = "ID")
	private Occupation occupation;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RISKYOCCUPATIONID", referencedColumnName = "ID")
	private RiskyOccupation riskyOccupation;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPROPOSALID", referencedColumnName = "ID")
	private LifeProposal lifeProposal;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RELATIONSHIPID", referencedColumnName = "ID")
	private RelationShip relationship;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCHOOLID", referencedColumnName = "ID")
	private School school;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GRATEINFOID", referencedColumnName = "ID")
	private GradeInfo gradeInfo;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "proposalInsuredPerson", orphanRemoval = true)
	private List<InsuredPersonAttachment> attachmentList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "INSUREDPERSONID", referencedColumnName = "ID")
	private List<InsuranceHistoryRecord> insuranceHistoryRecord;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
	private List<Attachment> birthCertificateAttachment;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "proposalInsuredPerson", orphanRemoval = true)
	private List<InsuredPersonAddon> insuredPersonAddOnList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "proposalInsuredPerson", orphanRemoval = true)
	private List<InsuredPersonKeyFactorValue> keyFactorValueList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "proposalInsuredPerson", orphanRemoval = true)
	private List<InsuredPersonBeneficiaries> insuredPersonBeneficiariesList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lifeproposalInsuredPerson", orphanRemoval = true)
	private List<SurveyQuestionAnswer> surveyQuestionAnswerList;

	@Enumerated(value = EnumType.STRING)
	private SumInsuredType sumInsuredType;

	@Version
	private int version;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;
	
//	@Embedded
//	private ContentInfo contentInfo;

	public ProposalInsuredPerson() {

	}

	public ProposalInsuredPerson(InsuredPersonInfoDTO dto) {
		this.approved = dto.isApprove();
		this.needMedicalCheckup = dto.isNeedMedicalCheckup();
		this.paymentTerm = dto.getPaymentTerm();
		this.periodMonth = dto.getPeriodOfMonths();
		this.age = dto.getAgeForNextYear();
		this.proposedPremium = dto.getPremium();
		this.proposedSumInsured = dto.getSumInsuredInfo();
		this.approvedPremium = dto.getApprovedPremium();
		this.approvedSumInsured = dto.getApprovedSumInsured();
		this.basicTermPremium = dto.getBasicTermPremium();
		this.addOnTermPremium = dto.getAddOnTermPremium();
		this.endorsementNetAddonPremium = dto.getEndorsementAddonPremium();
		this.endorsementNetBasicPremium = dto.getEndorsementBasicPremium();
		this.interest = dto.getInterest();
		this.rejectReason = dto.getRejectReason();
		this.insPersonCodeNo = dto.getInsPersonCodeNo();
		this.inPersonGroupCodeNo = dto.getInPersonGroupCodeNo();
		this.initialId = dto.getInitialId();
		this.idNo = dto.getFullIdNo();
		this.fatherName = dto.getFatherName();
		this.parentName = dto.getParentName();
		this.parentDOB = dto.getDateOfBirth();
		this.parentIdType = dto.getIdType();
		this.parentIdNo = dto.getIdNo();
		this.surveyquestionOne = dto.getSurveyquestionOne();
		this.surveyquestionTwo = dto.getSurveyquestionTwo();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.dateOfBirth = dto.getDateOfBirth();
		this.endorsementStatus = dto.getEndorsementStatus();
		this.clsOfHealth = dto.getClassificationOfHealth();
		this.gender = dto.getGender();
		this.idType = dto.getIdType();
		this.residentAddress = dto.getResidentAddress();
		this.name = dto.getName();
		this.product = dto.getProduct();
		this.typesOfSport = dto.getTypesOfSport();
		this.occupation = dto.getOccupation();
		this.riskyOccupation = dto.getRiskyOccupation();
		this.unit = dto.getUnit();
		this.weight = dto.getWeight();
		this.height = dto.getHeight();
		this.bmi = dto.getBmi();
		this.relationship = dto.getRelationShip();
		this.school = dto.getSchool();
		this.gradeInfo = dto.getGradeInfo();
		this.sumInsuredType = dto.getSumInsuredType();
//		this.contentInfo = dto.getContentInfo();
		// override
		this.customer = dto.getCustomer();
		for (InsuredPersonAttachment attach : dto.getPerAttachmentList()) {
			addAttachment(attach);
		}
		for (InsuranceHistoryRecord record : dto.getInsuranceHistoryRecord()) {
			addInsuranceHistoryRecord(record);
		}
		for (Attachment attach : dto.getBirthCertificateAttachments()) {
			addBirthCertificateAttachment(attach);
		}
		for (InsuredPersonKeyFactorValue kfv : dto.getKeyFactorValueList()) {
			addLifeKeyFactorValue(new InsuredPersonKeyFactorValue(kfv.getValue(), kfv.getKeyFactor()));
		}
		for (BeneficiariesInfoDTO beneficiary : dto.getBeneficiariesInfoDTOList()) {
			addBeneficiaries(new InsuredPersonBeneficiaries(beneficiary));
		}
		if (dto.getInsuredPersonAddOnDTOList() != null) {
			for (InsuredPersonAddOnDTO addOn : dto.getInsuredPersonAddOnDTOList()) {
				addInsuredPersonAddon(new InsuredPersonAddon(addOn));
			}
		}

		if (dto.isExistsEntity()) {
			this.id = dto.getTempId();
			this.version = dto.getVersion();
		}
	}

	public ProposalInsuredPerson(InsuredPersonInfoDTO insuredPersonInfoDTO, LifeProposal lifeProposal) {
		this.id = insuredPersonInfoDTO.getId();
		this.lifeProposal = lifeProposal;
		this.dateOfBirth = insuredPersonInfoDTO.getDateOfBirth();
		this.proposedSumInsured = insuredPersonInfoDTO.getSumInsuredInfo();
		this.product = insuredPersonInfoDTO.getProduct();
		this.customer = insuredPersonInfoDTO.getCustomer();
		this.periodMonth = insuredPersonInfoDTO.getPeriodOfMonths();
		this.startDate = insuredPersonInfoDTO.getStartDate();
		this.endDate = insuredPersonInfoDTO.getEndDate();
		this.proposedPremium = insuredPersonInfoDTO.getPremium();
		this.initialId = insuredPersonInfoDTO.getInitialId();
		this.idNo = insuredPersonInfoDTO.getFullIdNo();
		this.idType = insuredPersonInfoDTO.getIdType();
		this.name = insuredPersonInfoDTO.getName();
		this.residentAddress = insuredPersonInfoDTO.getResidentAddress();
		this.gender = insuredPersonInfoDTO.getGender();
		this.occupation = insuredPersonInfoDTO.getOccupation();
		this.riskyOccupation = insuredPersonInfoDTO.getRiskyOccupation();
		this.fatherName = insuredPersonInfoDTO.getFatherName();
		this.weight = insuredPersonInfoDTO.getWeight();
		this.height = insuredPersonInfoDTO.getHeight();
		this.bmi = insuredPersonInfoDTO.getBmi();
		this.endorsementNetBasicPremium = insuredPersonInfoDTO.getEndorsementNetBasicPremium();
		this.endorsementNetAddonPremium = insuredPersonInfoDTO.getEndorsementNetBasicPremium();
		this.interest = insuredPersonInfoDTO.getInterest();
		this.endorsementStatus = insuredPersonInfoDTO.getEndorsementStatus();
		this.insPersonCodeNo = insuredPersonInfoDTO.getInsPersonCodeNo();
		this.isPaidPremiumForPaidup = insuredPersonInfoDTO.getIsPaidPremiumForPaidup();
		this.age = insuredPersonInfoDTO.getAgeForNextYear();
		this.inPersonGroupCodeNo = insuredPersonInfoDTO.getInPersonGroupCodeNo();
		this.parentName = insuredPersonInfoDTO.getParentName();
		this.parentIdNo = insuredPersonInfoDTO.getParentFullIdNo();
		this.parentIdType = insuredPersonInfoDTO.getParentIdType();
		this.parentDOB = insuredPersonInfoDTO.getParentDOB();
		this.school = insuredPersonInfoDTO.getSchool();
		this.gradeInfo = insuredPersonInfoDTO.getGradeInfo();
		this.relationship = insuredPersonInfoDTO.getRelationShip();
		this.version = insuredPersonInfoDTO.getVersion();
		this.approved = insuredPersonInfoDTO.isApprove();
		this.sumInsuredType = insuredPersonInfoDTO.getSumInsuredType();
		this.periodType = insuredPersonInfoDTO.getPeriodType();
		this.surveyquestionOne = insuredPersonInfoDTO.getSurveyquestionOne();
		this.surveyquestionTwo = insuredPersonInfoDTO.getSurveyquestionTwo();
//		this.contentInfo = insuredPersonInfoDTO.getContentInfo();
		for (InsuredPersonAttachment attach : insuredPersonInfoDTO.getPerAttachmentList()) {
			addAttachment(attach);
		}
		for (InsuranceHistoryRecord record : insuredPersonInfoDTO.getInsuranceHistoryRecord()) {
			addInsuranceHistoryRecord(record);
		}
		for (Attachment attach : insuredPersonInfoDTO.getBirthCertificateAttachments()) {
			addBirthCertificateAttachment(attach);
		}
		for (InsuredPersonKeyFactorValue kfv : insuredPersonInfoDTO.getKeyFactorValueList()) {
			addLifeKeyFactorValue(new InsuredPersonKeyFactorValue(kfv.getValue(), kfv.getKeyFactor()));
		}
		for (BeneficiariesInfoDTO beneficiary : insuredPersonInfoDTO.getBeneficiariesInfoDTOList()) {
			addBeneficiaries(new InsuredPersonBeneficiaries(beneficiary));
		}
		if (insuredPersonInfoDTO.getInsuredPersonAddOnDTOList() != null) {
			for (InsuredPersonAddOnDTO addOn : insuredPersonInfoDTO.getInsuredPersonAddOnDTOList()) {
				addInsuredPersonAddon(new InsuredPersonAddon(addOn));
			}
		}
	}

	public ProposalInsuredPerson(Date dateOfBirth, double proposedSumInsured, Product product, LifeProposal lifeproposal, int periodMonth, Date startDate, Date endDate,
			double proposedPremium, double endorsementNetBasicPremium, double endorsementNetAddonPremium, double interest, EndorsementStatus status, String insPersonCodeNo,
			String inPersonGroupCodeNo) {
		this.dateOfBirth = dateOfBirth;
		this.proposedSumInsured = proposedSumInsured;
		this.product = product;
		this.lifeProposal = lifeproposal;
		this.periodMonth = periodMonth;
		this.startDate = startDate;
		this.endDate = endDate;
		this.proposedPremium = proposedPremium;
		this.endorsementStatus = status;
		this.endorsementNetBasicPremium = endorsementNetBasicPremium;
		this.endorsementNetAddonPremium = endorsementNetAddonPremium;
		this.insPersonCodeNo = insPersonCodeNo;
		this.interest = interest;
		this.inPersonGroupCodeNo = inPersonGroupCodeNo;

	}

	public ProposalInsuredPerson(PolicyInsuredPerson policyInsuredPerson) {
		this.clsOfHealth = policyInsuredPerson.getClsOfHealth();
		this.dateOfBirth = policyInsuredPerson.getDateOfBirth();
		this.product = policyInsuredPerson.getProduct();
		this.periodMonth = policyInsuredPerson.getPeriodMonth();
		this.startDate = policyInsuredPerson.getStartDate();
		this.endDate = policyInsuredPerson.getEndDate();
		this.proposedSumInsured = policyInsuredPerson.getSumInsured();
		this.proposedPremium = policyInsuredPerson.getPremium();
		this.paymentTerm = policyInsuredPerson.getPaymentTerm();
		this.approvedSumInsured = policyInsuredPerson.getSumInsured();
		this.weight = policyInsuredPerson.getWeight();
		this.height = policyInsuredPerson.getHeight();
		this.approvedPremium = policyInsuredPerson.getPremium();
		this.insPersonCodeNo = policyInsuredPerson.getInsPersonCodeNo();
		this.initialId = policyInsuredPerson.getInitialId();
		this.idNo = policyInsuredPerson.getIdNo();
		this.idType = policyInsuredPerson.getIdType();
		this.name = policyInsuredPerson.getName();
		this.residentAddress = policyInsuredPerson.getResidentAddress();
		this.gender = policyInsuredPerson.getGender();
		this.occupation = policyInsuredPerson.getOccupation();
		this.riskyOccupation = policyInsuredPerson.getRiskyOccupation();
		this.fatherName = policyInsuredPerson.getFatherName();
		this.customer = policyInsuredPerson.getCustomer();
		this.age = policyInsuredPerson.getAge();
		this.inPersonGroupCodeNo = policyInsuredPerson.getInPersonGroupCodeNo();
		this.typesOfSport = policyInsuredPerson.getTypesOfSport();
		this.endorsementStatus = policyInsuredPerson.getEndorsementStatus();
		this.basicTermPremium = policyInsuredPerson.getBasicTermPremium();
		this.addOnTermPremium = policyInsuredPerson.getAddOnTermPremium();
		this.endorsementNetBasicPremium = policyInsuredPerson.getEndorsementNetBasicPremium();
		this.endorsementNetAddonPremium = policyInsuredPerson.getEndorsementNetAddonPremium();
		this.interest = policyInsuredPerson.getInterest();
		this.unit = policyInsuredPerson.getUnit();
		this.relationship = policyInsuredPerson.getRelationShip();
		this.school = policyInsuredPerson.getSchool();
		this.gradeInfo = policyInsuredPerson.getGradeInfo();
		this.sumInsuredType = policyInsuredPerson.getSumInsuredType();
//		this.contentInfo = policyInsuredPerson.getContentInfo();
		for (PolicyInsuredPersonKeyFactorValue lifeKFValue : policyInsuredPerson.getPolicyInsuredPersonkeyFactorValueList()) {
			addLifeKeyFactorValue(new InsuredPersonKeyFactorValue(lifeKFValue));
		}
		for (PolicyInsuredPersonBeneficiaries insuredPersonBeneficiaries : policyInsuredPerson.getPolicyInsuredPersonBeneficiariesList()) {
			addBeneficiaries(new InsuredPersonBeneficiaries(insuredPersonBeneficiaries));
		}
		for (PolicyInsuredPersonAddon addon : policyInsuredPerson.getPolicyInsuredPersonAddOnList()) {
			addInsuredPersonAddon(new InsuredPersonAddon(addon));
		}
		for (PolicyInsuredPersonAttachment attachment : policyInsuredPerson.getAttachmentList()) {
			addAttachment(new InsuredPersonAttachment(attachment));
		}
		for (Attachment attach : policyInsuredPerson.getBirthCertificateAttachment()) {
			addBirthCertificateAttachment(attach);
		}
	}

	public ProposalInsuredPerson(Customer customer) {
		this.name = customer.getName();
		this.initialId = customer.getInitialId();
		this.fatherName = customer.getFatherName();
		this.idNo = customer.getFullIdNo();
		this.idType = customer.getIdType();
		this.dateOfBirth = customer.getDateOfBirth();
		this.residentAddress = customer.getResidentAddress();
		this.gender = customer.getGender();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PRLINSURPERSON_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public double getProposedSumInsured() {
		return proposedSumInsured;
	}

	public void setProposedSumInsured(double proposedSumInsured) {
		this.proposedSumInsured = proposedSumInsured;
	}

	public double getProposedPremium() {
		return proposedPremium;
	}

	public void setProposedPremium(double proposedPremium) {
		this.proposedPremium = proposedPremium;
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

	public int getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
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

	public int getPeriodMonth() {
		return periodMonth;
	}

	public void setPeriodMonth(int periodMonth) {
		this.periodMonth = periodMonth;
	}

	public int getPeriodYear() {
		return periodYear;
	}

	public void setPeriodYear(int periodYear) {
		this.periodYear = periodYear;
	}

	public int getPeriodWeek() {
		return periodWeek;
	}

	public void setPeriodWeek(int periodWeek) {
		this.periodWeek = periodWeek;
	}

	public int getPeriodYears() {
		return periodMonth / 12;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

	/* for student life */
	public int getPremiumTerm() {
		return getPeriodYears() - 3;
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

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		if (approved) {
			needMedicalCheckup = false;
			rejectReason = null;
			approvedSumInsured = proposedSumInsured;
		} else {
			this.approvedSumInsured = 0.0;
		}
		this.approved = approved;
	}

	public boolean isNeedMedicalCheckup() {
		return needMedicalCheckup;
	}

	public void setNeedMedicalCheckup(boolean needMedicalCheckup) {
		this.needMedicalCheckup = needMedicalCheckup;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public List<InsuredPersonAttachment> getAttachmentList() {
		if (this.attachmentList == null) {
			this.attachmentList = new ArrayList<InsuredPersonAttachment>();
		}
		return this.attachmentList;
	}

	public void setAttachmentList(List<InsuredPersonAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<Attachment> getBirthCertificateAttachment() {
		if (this.birthCertificateAttachment == null) {
			this.birthCertificateAttachment = new ArrayList<Attachment>();
		}
		return birthCertificateAttachment;
	}

	public void setBirthCertificateAttachment(List<Attachment> birthCertificateAttachment) {
		this.birthCertificateAttachment = birthCertificateAttachment;
	}

	public List<InsuranceHistoryRecord> getInsuranceHistoryRecord() {
		if (insuranceHistoryRecord == null) {
			insuranceHistoryRecord = new ArrayList<>();
		}
		return insuranceHistoryRecord;
	}

	public void setInsuranceHistoryRecord(List<InsuranceHistoryRecord> insuranceHistoryRecord) {
		this.insuranceHistoryRecord = insuranceHistoryRecord;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public RelationShip getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public GradeInfo getGradeInfo() {
		return gradeInfo;
	}

	public void setGradeInfo(GradeInfo gradeInfo) {
		this.gradeInfo = gradeInfo;
	}

	public List<InsuredPersonAddon> getInsuredPersonAddOnList() {
		if (insuredPersonAddOnList == null) {
			insuredPersonAddOnList = new ArrayList<InsuredPersonAddon>();
		}
		return insuredPersonAddOnList;
	}

	public void setInsuredPersonAddOnList(List<InsuredPersonAddon> insuredPersonAddOnList) {
		this.insuredPersonAddOnList = insuredPersonAddOnList;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getAddOnPremium() {
		double premium = 0.0;
		if (insuredPersonAddOnList != null) {
			for (InsuredPersonAddon iao : insuredPersonAddOnList) {
				premium = Utils.getTwoDecimalPoint(premium + iao.getProposedPremium());
			}
		}
		return premium;
	}

	public double getAddOnSumInsured() {
		double sumInsured = 0.0;
		if (insuredPersonAddOnList != null) {
			for (InsuredPersonAddon iao : insuredPersonAddOnList) {
				sumInsured = sumInsured + iao.getProposedSumInsured();
			}
		}
		return sumInsured;
	}

	public double getTotalPermium() {
		return Utils.getTwoDecimalPoint(proposedPremium + getAddOnPremium());
	}

	public List<InsuredPersonBeneficiaries> getInsuredPersonBeneficiariesList() {
		if (this.insuredPersonBeneficiariesList == null) {
			this.insuredPersonBeneficiariesList = new ArrayList<InsuredPersonBeneficiaries>();
		}
		return this.insuredPersonBeneficiariesList;
	}

	public void setInsuredPersonBeneficiariesList(List<InsuredPersonBeneficiaries> insuredPersonBeneficiariesList) {
		this.insuredPersonBeneficiariesList = insuredPersonBeneficiariesList;
	}

	public void setProposalVehicleList(List<InsuredPersonBeneficiaries> insuredPersonBeneficiariesList) {
		this.insuredPersonBeneficiariesList = insuredPersonBeneficiariesList;
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

	public void addLifeKeyFactorValue(InsuredPersonKeyFactorValue insuredPersonKeyFactorValue) {
		insuredPersonKeyFactorValue.setProposalInsuredPerson(this);
		getKeyFactorValueList().add(insuredPersonKeyFactorValue);
	}

	public void addInsuredPersonAddon(InsuredPersonAddon insuredPersonAddon) {
		insuredPersonAddon.setProposalInsuredPerson(this);
		getInsuredPersonAddOnList().add(insuredPersonAddon);
	}

	public void addSurveyQuestionAnswer(SurveyQuestionAnswer surveyQuestion) {
		surveyQuestion.setLifeproposalInsuredPerson(this);
		getSurveyQuestionAnswerList().add(surveyQuestion);
	}

	public void addAttachment(InsuredPersonAttachment attachment) {
		attachment.setProposalInsuredPerson(this);
		getAttachmentList().add(attachment);
	}

	public void addBirthCertificateAttachment(Attachment attachment) {
		getBirthCertificateAttachment().add(attachment);
	}

	public void addInsuranceHistoryRecord(InsuranceHistoryRecord record) {
		getInsuranceHistoryRecord().add(record);
	}

	public void addBeneficiaries(InsuredPersonBeneficiaries insuredPersonBeneficiaries) {
		insuredPersonBeneficiaries.setProposalInsuredPerson(this);
		getInsuredPersonBeneficiariesList().add(insuredPersonBeneficiaries);
	}

	public ClassificationOfHealth getClsOfHealth() {
		return clsOfHealth;
	}

	public void setClsOfHealth(ClassificationOfHealth clsOfHealth) {
		this.clsOfHealth = clsOfHealth;
	}

	public boolean isValidAttachment() {
		if (attachmentList == null || attachmentList.isEmpty()) {
			return false;
		}
		return true;
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

	public ResidentAddress getResidentAddress() {
		return residentAddress;
	}

	public void setResidentAddress(ResidentAddress residentAddress) {
		this.residentAddress = residentAddress;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
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
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentIdNo() {
		return parentIdNo;
	}

	public void setParentIdNo(String parentIdNo) {
		this.parentIdNo = parentIdNo;
	}

	public IdType getParentIdType() {
		return parentIdType;
	}

	public void setParentIdType(IdType parentIdType) {
		this.parentIdType = parentIdType;
	}

	public Date getParentDOB() {
		return parentDOB;
	}

	public void setParentDOB(Date parentDOB) {
		this.parentDOB = parentDOB;
	}

	public EndorsementStatus getEndorsementStatus() {
		return endorsementStatus;
	}

	public void setEndorsementStatus(EndorsementStatus endorsementStatus) {
		this.endorsementStatus = endorsementStatus;
	}

	public double getEndorsementNetBasicPremium() {
		return endorsementNetBasicPremium;
	}

	public void setEndorsementNetBasicPremium(double endorsementNetBasicPremium) {
		this.endorsementNetBasicPremium = endorsementNetBasicPremium;
	}

	public double getEndorsementNetAddonPremium() {
		return endorsementNetAddonPremium;
	}

	public void setEndorsementNetAddonPremium(double endorsementNetAddonPremium) {
		this.endorsementNetAddonPremium = endorsementNetAddonPremium;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public String getInsPersonCodeNo() {
		return insPersonCodeNo;
	}

	public void setInsPersonCodeNo(String insPersonCodeNo) {
		this.insPersonCodeNo = insPersonCodeNo;
	}

	public Boolean getIsPaidPremiumForPaidup() {
		return isPaidPremiumForPaidup;
	}

	public void setIsPaidPremiumForPaidup(Boolean isPaidPremiumForPaidup) {
		this.isPaidPremiumForPaidup = isPaidPremiumForPaidup;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getBmi() {
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}

	public RiskyOccupation getRiskyOccupation() {
		return riskyOccupation;
	}

	public void setRiskyOccupation(RiskyOccupation riskyOccupation) {
		this.riskyOccupation = riskyOccupation;
	}

	public String getInPersonGroupCodeNo() {
		return inPersonGroupCodeNo;
	}

	public void setInPersonGroupCodeNo(String inPersonGroupCodeNo) {
		this.inPersonGroupCodeNo = inPersonGroupCodeNo;
	}

	public TypesOfSport getTypesOfSport() {
		return typesOfSport;
	}

	public void setTypesOfSport(TypesOfSport typesOfSport) {
		this.typesOfSport = typesOfSport;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}
	
//	public ContentInfo getContentInfo() {
//		return contentInfo;
//	}
//
//	public void setContentInfo(ContentInfo contentInfo) {
//		this.contentInfo = contentInfo;
//	}
	
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

	public String getPeriod() {
		if (periodMonth > 0) {
			if (periodMonth / 12 < 1) {
				return periodMonth + " - Months";
			} else {
				return periodMonth / 12 + " - Year";
			}
		} else if (periodYear > 0) {
			return periodYear + " - Year";
		} else {
			return periodWeek + " - Week";
		}

	}

	public List<Date> getTimeSlotList() {
		List<Date> result = new ArrayList<Date>();
		result.add(startDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int months = lifeProposal.getPaymentType().getMonth();
		if (months > 0) {
			int a = 12 / months;
			for (int i = 1; i < a; i++) {
				cal.add(Calendar.MONTH, months);
				result.add(cal.getTime());
			}
		}
		return result;
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
		return result.replaceAll("\\s+", " ");
	}

	public String getFullAddress() {
		String result = "";
		if (residentAddress != null) {
			if (residentAddress.getResidentAddress() != null && !residentAddress.getResidentAddress().isEmpty()) {
				result = result + residentAddress.getResidentAddress();
			}
			if (residentAddress.getTownship() != null && !residentAddress.getTownship().getFullTownShip().isEmpty()) {
				result = result + ", " + residentAddress.getTownship().getFullTownShip();
			}
		}
		return result;
	}

	public double getPremiumForOneThousandKyat() {
		return ((proposedPremium * product.getBaseSumInsured()) / proposedSumInsured);
	}

	public String getSchoolName() {
		if (school != null)
			return school.getName();
		return "-";
	}

	public String getSchoolAddress() {
		if (school != null && school.getAddress() != null)
			return school.getFullAddress();
		return "-";
	}

	public String getGradeInfoName() {
		if (gradeInfo != null)
			return gradeInfo.getName();
		return "-";
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public SumInsuredType getSumInsuredType() {
		return sumInsuredType;
	}

	public void setSumInsuredType(SumInsuredType sumInsuredType) {
		this.sumInsuredType = sumInsuredType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(addOnTermPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + age;
		result = prime * result + (approved ? 1231 : 1237);
		temp = Double.doubleToLongBits(approvedPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(approvedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((attachmentList == null) ? 0 : attachmentList.hashCode());
		temp = Double.doubleToLongBits(basicTermPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((birthCertificateAttachment == null) ? 0 : birthCertificateAttachment.hashCode());
		result = prime * result + ((clsOfHealth == null) ? 0 : clsOfHealth.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		temp = Double.doubleToLongBits(endorsementNetAddonPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(endorsementNetBasicPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((endorsementStatus == null) ? 0 : endorsementStatus.hashCode());
		result = prime * result + ((fatherName == null) ? 0 : fatherName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((gradeInfo == null) ? 0 : gradeInfo.hashCode());
		result = prime * result + height;
		result = prime * result + (int)bmi;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idNo == null) ? 0 : idNo.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
		result = prime * result + ((inPersonGroupCodeNo == null) ? 0 : inPersonGroupCodeNo.hashCode());
		result = prime * result + ((initialId == null) ? 0 : initialId.hashCode());
		result = prime * result + ((insPersonCodeNo == null) ? 0 : insPersonCodeNo.hashCode());
		result = prime * result + ((insuranceHistoryRecord == null) ? 0 : insuranceHistoryRecord.hashCode());
		result = prime * result + ((insuredPersonAddOnList == null) ? 0 : insuredPersonAddOnList.hashCode());
		result = prime * result + ((insuredPersonBeneficiariesList == null) ? 0 : insuredPersonBeneficiariesList.hashCode());
		temp = Double.doubleToLongBits(interest);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((isPaidPremiumForPaidup == null) ? 0 : isPaidPremiumForPaidup.hashCode());
		result = prime * result + ((keyFactorValueList == null) ? 0 : keyFactorValueList.hashCode());
		// result = prime * result + ((lifeProposal == null) ? 0 :
		// lifeProposal.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (needMedicalCheckup ? 1231 : 1237);
		result = prime * result + ((occupation == null) ? 0 : occupation.hashCode());
		result = prime * result + ((parentDOB == null) ? 0 : parentDOB.hashCode());
		result = prime * result + ((parentIdNo == null) ? 0 : parentIdNo.hashCode());
		result = prime * result + ((parentIdType == null) ? 0 : parentIdType.hashCode());
		result = prime * result + ((parentName == null) ? 0 : parentName.hashCode());
		result = prime * result + paymentTerm;
		result = prime * result + periodMonth;
		result = prime * result + periodWeek;
		result = prime * result + periodYear;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		temp = Double.doubleToLongBits(proposedPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(proposedSumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((rejectReason == null) ? 0 : rejectReason.hashCode());
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
		result = prime * result + ((residentAddress == null) ? 0 : residentAddress.hashCode());
		result = prime * result + ((riskyOccupation == null) ? 0 : riskyOccupation.hashCode());
		result = prime * result + ((school == null) ? 0 : school.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((sumInsuredType == null) ? 0 : sumInsuredType.hashCode());
		result = prime * result + ((surveyQuestionAnswerList == null) ? 0 : surveyQuestionAnswerList.hashCode());
		result = prime * result + ((typesOfSport == null) ? 0 : typesOfSport.hashCode());
		result = prime * result + unit;
		result = prime * result + version;
		result = prime * result + weight;
//		result = prime * result + ((contentInfo == null) ? 0 : contentInfo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProposalInsuredPerson other = (ProposalInsuredPerson) obj;
		if (Double.doubleToLongBits(addOnTermPremium) != Double.doubleToLongBits(other.addOnTermPremium))
			return false;
		if (age != other.age)
			return false;
		if (approved != other.approved)
			return false;
		if (Double.doubleToLongBits(approvedPremium) != Double.doubleToLongBits(other.approvedPremium))
			return false;
		if (Double.doubleToLongBits(approvedSumInsured) != Double.doubleToLongBits(other.approvedSumInsured))
			return false;
		if (attachmentList == null) {
			if (other.attachmentList != null)
				return false;
		} else if (!attachmentList.equals(other.attachmentList))
			return false;
		if (Double.doubleToLongBits(basicTermPremium) != Double.doubleToLongBits(other.basicTermPremium))
			return false;
		if (birthCertificateAttachment == null) {
			if (other.birthCertificateAttachment != null)
				return false;
		} else if (!birthCertificateAttachment.equals(other.birthCertificateAttachment))
			return false;
		if (clsOfHealth != other.clsOfHealth)
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (Double.doubleToLongBits(endorsementNetAddonPremium) != Double.doubleToLongBits(other.endorsementNetAddonPremium))
			return false;
		if (Double.doubleToLongBits(endorsementNetBasicPremium) != Double.doubleToLongBits(other.endorsementNetBasicPremium))
			return false;
		if (endorsementStatus != other.endorsementStatus)
			return false;
		if (fatherName == null) {
			if (other.fatherName != null)
				return false;
		} else if (!fatherName.equals(other.fatherName))
			return false;
		if (gender != other.gender)
			return false;
		if (gradeInfo == null) {
			if (other.gradeInfo != null)
				return false;
		} else if (!gradeInfo.equals(other.gradeInfo))
			return false;
		if (height != other.height)
			return false;
		if (bmi != other.bmi)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idNo == null) {
			if (other.idNo != null)
				return false;
		} else if (!idNo.equals(other.idNo))
			return false;
		if (idType != other.idType)
			return false;
		if (inPersonGroupCodeNo == null) {
			if (other.inPersonGroupCodeNo != null)
				return false;
		} else if (!inPersonGroupCodeNo.equals(other.inPersonGroupCodeNo))
			return false;
		if (initialId == null) {
			if (other.initialId != null)
				return false;
		} else if (!initialId.equals(other.initialId))
			return false;
		if (insPersonCodeNo == null) {
			if (other.insPersonCodeNo != null)
				return false;
		} else if (!insPersonCodeNo.equals(other.insPersonCodeNo))
			return false;
		if (insuranceHistoryRecord == null) {
			if (other.insuranceHistoryRecord != null)
				return false;
		} else if (!insuranceHistoryRecord.equals(other.insuranceHistoryRecord))
			return false;
		if (insuredPersonAddOnList == null) {
			if (other.insuredPersonAddOnList != null)
				return false;
		} else if (!insuredPersonAddOnList.equals(other.insuredPersonAddOnList))
			return false;
		if (insuredPersonBeneficiariesList == null) {
			if (other.insuredPersonBeneficiariesList != null)
				return false;
		} else if (!insuredPersonBeneficiariesList.equals(other.insuredPersonBeneficiariesList))
			return false;
		if (Double.doubleToLongBits(interest) != Double.doubleToLongBits(other.interest))
			return false;
		if (isPaidPremiumForPaidup == null) {
			if (other.isPaidPremiumForPaidup != null)
				return false;
		} else if (!isPaidPremiumForPaidup.equals(other.isPaidPremiumForPaidup))
			return false;
		if (keyFactorValueList == null) {
			if (other.keyFactorValueList != null)
				return false;
		} else if (!keyFactorValueList.equals(other.keyFactorValueList))
			return false;
		// if (lifeProposal == null) {
		// if (other.lifeProposal != null)
		// return false;
		// } else if (!lifeProposal.equals(other.lifeProposal))
		// return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (needMedicalCheckup != other.needMedicalCheckup)
			return false;
		if (occupation == null) {
			if (other.occupation != null)
				return false;
		} else if (!occupation.equals(other.occupation))
			return false;
		if (parentDOB == null) {
			if (other.parentDOB != null)
				return false;
		} else if (!parentDOB.equals(other.parentDOB))
			return false;
		if (parentIdNo == null) {
			if (other.parentIdNo != null)
				return false;
		} else if (!parentIdNo.equals(other.parentIdNo))
			return false;
		if (parentIdType != other.parentIdType)
			return false;
		if (parentName == null) {
			if (other.parentName != null)
				return false;
		} else if (!parentName.equals(other.parentName))
			return false;
		if (paymentTerm != other.paymentTerm)
			return false;
		if (periodMonth != other.periodMonth)
			return false;
		if (periodWeek != other.periodWeek)
			return false;
		if (periodYear != other.periodYear)
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (Double.doubleToLongBits(proposedPremium) != Double.doubleToLongBits(other.proposedPremium))
			return false;
		if (Double.doubleToLongBits(proposedSumInsured) != Double.doubleToLongBits(other.proposedSumInsured))
			return false;
		if (rejectReason == null) {
			if (other.rejectReason != null)
				return false;
		} else if (!rejectReason.equals(other.rejectReason))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		if (residentAddress == null) {
			if (other.residentAddress != null)
				return false;
		} else if (!residentAddress.equals(other.residentAddress))
			return false;
		if (riskyOccupation == null) {
			if (other.riskyOccupation != null)
				return false;
		} else if (!riskyOccupation.equals(other.riskyOccupation))
			return false;
		if (school == null) {
			if (other.school != null)
				return false;
		} else if (!school.equals(other.school))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (sumInsuredType != other.sumInsuredType)
			return false;
		if (surveyQuestionAnswerList == null) {
			if (other.surveyQuestionAnswerList != null)
				return false;
		} else if (!surveyQuestionAnswerList.equals(other.surveyQuestionAnswerList))
			return false;
		if (typesOfSport == null) {
			if (other.typesOfSport != null)
				return false;
		} else if (!typesOfSport.equals(other.typesOfSport))
			return false;
		if (unit != other.unit)
			return false;
		if (version != other.version)
			return false;
		if (weight != other.weight)
			return false;
//		if (contentInfo == null) {
//			if (other.contentInfo != null)
//				return false;
//		} else if (!contentInfo.equals(other.contentInfo))
//			return false;
		return true;
	}

}
