package org.ace.insurance.web.manage.medical.proposal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.medical.proposal.MedicalPersonHistoryRecord;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonKeyFactorValue;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.web.common.CommonDTO;
import org.ace.insurance.web.manage.medical.survey.SurveyQuestionAnswerDTO;

public class MedProInsuDTO extends CommonDTO {
	private boolean isPaidPremiumForPaidup;
	private boolean approved;
	private boolean needMedicalCheckup;
	private int paymentTerm;
	private int age;
	private double proposedPremium;
	private double approvedPremium;
	private double basicTermPremium;
	private double addOnPremium;
	private double totalNcbPremium;
	private String rejectReason;
	private int periodYear;
	private Date startDate;
	private Date endDate;
	private Date dateOfBirth;
	private int unit;
	private RelationShip relationship;
	private Product product;
	private CustomerDTO customer;
	private MedProGuardianDTO guardianDTO;
	private List<MedProInsuAttDTO> attachmentList;
	private List<MedProInsuAddOnDTO> insuredPersonAddOnList;
	private List<MedicalProposalInsuredPersonKeyFactorValue> keyFactorValueList;
	private List<MedProInsuBeneDTO> insuredPersonBeneficiariesList;
	private List<SurveyQuestionAnswerDTO> surveyQuestionAnsweDTOList;
	private List<MedicalPersonHistoryRecord> historyRecordList;
	private Map<String, MedProInsuAddOnDTO> insuredPersonAddOnDTOMap = new HashMap<String, MedProInsuAddOnDTO>();
	private boolean finishedApproved;
	private boolean sameInsuredPerson;
	private Name name;
	private String initialId;
	private String fatherName;
	private Occupation occupation;
	private ResidentAddress residentAddress;
	private PaymentType paymentType;
	private Gender gender;
	private String tempId;
	private int basicPlusUnit;
	private double basicPlusPremium;
	private double addOnTermPremium;
	private double basicPlusTermPremium;
	private MedProDTO medProDTO;

	public MedProInsuDTO() {
		tempId = System.nanoTime() + "";
		customer = new CustomerDTO();
	}

	public MedProInsuDTO(MedProInsuDTO medProInsuDTO) {
		this.isPaidPremiumForPaidup = medProInsuDTO.isPaidPremiumForPaidup();
		this.approved = medProInsuDTO.isApproved();
		this.needMedicalCheckup = medProInsuDTO.isNeedMedicalCheckup();
		this.paymentTerm = medProInsuDTO.getPaymentTerm();
		this.age = medProInsuDTO.getAge();
		this.proposedPremium = medProInsuDTO.getProposedPremium();
		this.approvedPremium = medProInsuDTO.getApprovedPremium();
		this.basicTermPremium = medProInsuDTO.getBasicTermPremium();
		this.addOnPremium = medProInsuDTO.getAddOnPremium();
		this.totalNcbPremium = medProInsuDTO.getTotalNcbPremium();
		this.rejectReason = medProInsuDTO.getRejectReason();
		this.periodYear = medProInsuDTO.getPeriodYear();
		this.startDate = medProInsuDTO.getStartDate();
		this.endDate = medProInsuDTO.getEndDate();
		this.dateOfBirth = medProInsuDTO.getDateOfBirth();
		this.unit = medProInsuDTO.getUnit();
		this.relationship = medProInsuDTO.getRelationship();
		this.product = medProInsuDTO.getProduct();
		this.customer = new CustomerDTO(medProInsuDTO.getCustomer());
		this.guardianDTO = medProInsuDTO.getGuardianDTO();
		this.attachmentList = medProInsuDTO.getAttachmentList();
		this.insuredPersonAddOnList = medProInsuDTO.getInsuredPersonAddOnList();
		this.keyFactorValueList = medProInsuDTO.getKeyFactorValueList();
		this.insuredPersonBeneficiariesList = medProInsuDTO.getInsuredPersonBeneficiariesList();
		this.surveyQuestionAnsweDTOList = medProInsuDTO.getSurveyQuestionAnsweDTOList();
		this.historyRecordList = medProInsuDTO.getHistoryRecordList();
		this.finishedApproved = medProInsuDTO.isFinishedApproved();
		this.sameInsuredPerson = medProInsuDTO.isSameInsuredPerson();
		this.name = medProInsuDTO.getName();
		this.initialId = medProInsuDTO.getInitialId();
		this.fatherName = medProInsuDTO.getFatherName();
		this.occupation = medProInsuDTO.getOccupation();
		this.residentAddress = medProInsuDTO.getResidentAddress();
		this.paymentType = medProInsuDTO.getPaymentType();
		this.gender = medProInsuDTO.getGender();
		this.tempId = medProInsuDTO.getTempId();
		this.basicPlusUnit = medProInsuDTO.getBasicPlusUnit();
		this.basicPlusPremium = medProInsuDTO.getBasicPlusPremium();
		this.addOnTermPremium = medProInsuDTO.getAddOnTermPremium();
		this.basicPlusTermPremium = medProInsuDTO.getBasicPlusTermPremium();
	}

	public MedProInsuDTO(CustomerDTO customer, MedProGuardianDTO guardianDTO) {
		this.customer = customer;
		this.guardianDTO = guardianDTO;
	}

	public MedProDTO getMedProDTO() {
		return medProDTO;
	}

	public void setMedProDTO(MedProDTO medProDTO) {
		this.medProDTO = medProDTO;
	}

	public boolean isSameInsuredPerson() {
		return sameInsuredPerson;
	}

	public void setSameInsuredPerson(boolean sameInsuredPerson) {
		this.sameInsuredPerson = sameInsuredPerson;
	}

	public boolean isPaidPremiumForPaidup() {
		return isPaidPremiumForPaidup;
	}

	public void setPaidPremiumForPaidup(boolean isPaidPremiumForPaidup) {
		this.isPaidPremiumForPaidup = isPaidPremiumForPaidup;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isNeedMedicalCheckup() {
		return needMedicalCheckup;
	}

	public void setNeedMedicalCheckup(boolean needMedicalCheckup) {
		this.needMedicalCheckup = needMedicalCheckup;
	}

	public int getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getProposedPremium() {
		return proposedPremium;
	}

	public void setProposedPremium(double proposedPremium) {
		this.proposedPremium = proposedPremium;
	}

	public double getApprovedPremium() {
		return approvedPremium;
	}

	public void setApprovedPremium(double approvedPremium) {
		this.approvedPremium = approvedPremium;
	}

	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	public double getAddOnPremium() {
		return addOnPremium;
	}

	public void setAddOnPremium(double addOnPremium) {
		this.addOnPremium = addOnPremium;
	}

	public double getTotalNcbPremium() {
		return totalNcbPremium;
	}

	public void setTotalNcbPremium(double totalNcbPremium) {
		this.totalNcbPremium = totalNcbPremium;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
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

	public int getPeriodYear() {
		return periodYear;
	}

	public void setPeriodYear(int periodYear) {
		this.periodYear = periodYear;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public RelationShip getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
	}

	public Product getProduct() {
		return product;
	}

	public MedProGuardianDTO getGuardianDTO() {
		return guardianDTO;
	}

	public void setGuardianDTO(MedProGuardianDTO guardianDTO) {
		this.guardianDTO = guardianDTO;
	}

	public void setProduct(Product product) {
		this.product = product;
		loadKeyFactor(product);

	}

	public double getTotalPremium() {
		proposedPremium = proposedPremium + getAddOnPremium();
		return proposedPremium;
	}

	// public double getAddOnPremium() {
	// double premium = 0.0;
	// if (insuredPersonAddOnList != null) {
	// for (MedProInsuAddOnDTO iao : insuredPersonAddOnList) {
	// premium = premium + iao.getProposedPremium();
	// }
	// }
	// return premium;
	// }

	public double getAddOnUnit() {
		double premium = 0.0;
		if (insuredPersonAddOnList != null) {
			for (MedProInsuAddOnDTO iao : insuredPersonAddOnList) {
				premium = premium + iao.getUnit();
			}
		}
		return premium;
	}

	public List<SurveyQuestionAnswerDTO> getSurveyQuestionAnsweDTOList() {
		if (surveyQuestionAnsweDTOList == null) {
			surveyQuestionAnsweDTOList = new ArrayList<SurveyQuestionAnswerDTO>();
		}
		return surveyQuestionAnsweDTOList;
	}

	public void setSurveyQuestionAnsweDTOList(List<SurveyQuestionAnswerDTO> surveyQuestionAnsweDTOList) {
		this.surveyQuestionAnsweDTOList = surveyQuestionAnsweDTOList;
	}

	public List<MedicalPersonHistoryRecord> getHistoryRecordList() {
		if (historyRecordList == null) {
			historyRecordList = new ArrayList<MedicalPersonHistoryRecord>();
		}
		return historyRecordList;
	}

	public void setHistoryRecordList(List<MedicalPersonHistoryRecord> historyRecordList) {
		this.historyRecordList = historyRecordList;
	}

	private void loadKeyFactor(Product product) {
		keyFactorValueList = new ArrayList<MedicalProposalInsuredPersonKeyFactorValue>();
		for (KeyFactor kf : product.getKeyFactorList()) {
			MedicalProposalInsuredPersonKeyFactorValue insKf = new MedicalProposalInsuredPersonKeyFactorValue(kf);
			insKf.setKeyFactor(kf);
			keyFactorValueList.add(insKf);
		}
	}

	public CustomerDTO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}

	public List<MedProInsuAddOnDTO> getInsuredPersonAddOnList() {
		if (insuredPersonAddOnList == null) {
			insuredPersonAddOnList = new ArrayList<MedProInsuAddOnDTO>();
		}
		return insuredPersonAddOnList;
	}

	public void setInsuredPersonAddOnList(List<MedProInsuAddOnDTO> insuredPersonAddOnList) {
		this.insuredPersonAddOnList = insuredPersonAddOnList;
	}

	public List<MedProInsuAttDTO> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<MedProInsuAttDTO>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<MedProInsuAttDTO> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<MedicalProposalInsuredPersonKeyFactorValue> getKeyFactorValueList(MedicalProposalInsuredPerson proposalInsuredPerson) {
		return keyFactorValueList;
	}

	public List<MedicalProposalInsuredPersonKeyFactorValue> getKeyFactorValueList() {
		if (keyFactorValueList == null) {
			keyFactorValueList = new ArrayList<MedicalProposalInsuredPersonKeyFactorValue>();
		}
		return keyFactorValueList;
	}

	public void setKeyFactorValueList(List<MedicalProposalInsuredPersonKeyFactorValue> keyFactorValueList) {
		this.keyFactorValueList = keyFactorValueList;
	}

	public List<MedProInsuBeneDTO> getInsuredPersonBeneficiariesList() {
		if (insuredPersonBeneficiariesList == null) {
			insuredPersonBeneficiariesList = new ArrayList<MedProInsuBeneDTO>();
		}
		return insuredPersonBeneficiariesList;
	}

	public void setInsuredPersonBeneficiariesList(List<MedProInsuBeneDTO> insuredPersonBeneficiariesList) {
		this.insuredPersonBeneficiariesList = insuredPersonBeneficiariesList;
	}

	public List<MedProInsuAddOnDTO> getInsuredPersonAddOnDTOList() {
		if (insuredPersonAddOnList == null) {
			insuredPersonAddOnList = new ArrayList<MedProInsuAddOnDTO>();
		}
		return insuredPersonAddOnList;
	}

	public Map<String, MedProInsuAddOnDTO> getInsuredPersonAddOnDTOMap() {
		if (insuredPersonAddOnDTOMap == null) {
			insuredPersonAddOnDTOMap = new HashMap<String, MedProInsuAddOnDTO>();
		}
		return insuredPersonAddOnDTOMap;
	}

	public void addInsuredPersonAddon(MedProInsuAddOnDTO insuredPersonAddonDTO) {
		getInsuredPersonAddOnList().add(insuredPersonAddonDTO);
	}

	public void addHistoryRecord(MedicalPersonHistoryRecord record) {
		getHistoryRecordList().add(record);
	}

	public void addSurveyQuestion(SurveyQuestionAnswerDTO surveyQuestion) {
		getSurveyQuestionAnsweDTOList().add(surveyQuestion);
	}

	public void addBeneficiaries(MedProInsuBeneDTO medProInsuBeneDTO) {
		getInsuredPersonBeneficiariesList().add(medProInsuBeneDTO);
	}

	public void addMedicalKeyFactorValue(MedicalProposalInsuredPersonKeyFactorValue mKeyFactorValueDTO) {
		getKeyFactorValueList().add(mKeyFactorValueDTO);
	}

	public void addInsuredPersonAttachment(MedProInsuAttDTO attach) {
		getAttachmentList().add(attach);
	}

	public boolean isFinishedApproved() {
		return finishedApproved;
	}

	public void setFinishedApproved(boolean finishedApproved) {
		this.finishedApproved = finishedApproved;
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
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

	public String getFullAddress() {
		String result = "";
		if (residentAddress.getResidentAddress() != null) {
			if (residentAddress.getResidentAddress() != null && !residentAddress.getResidentAddress().isEmpty()) {
				result = residentAddress.getResidentAddress();
			}
			if (residentAddress.getTownship() != null && !residentAddress.getTownship().getName().isEmpty()) {
				result = result + " " + residentAddress.getTownship().getName();
			}
		}
		return result;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public Occupation getOccupation() {
		return occupation;
	}

	public void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}

	public ResidentAddress getResidentAddress() {
		return residentAddress;
	}

	public void setResidentAddress(ResidentAddress residentAddress) {
		this.residentAddress = residentAddress;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public int getBasicPlusUnit() {
		return basicPlusUnit;
	}

	public void setBasicPlusUnit(int basicPlusUnit) {
		this.basicPlusUnit = basicPlusUnit;
	}

	public double getBasicPlusPremium() {
		return basicPlusPremium;
	}

	public void setBasicPlusPremium(double basicPlusPremium) {
		this.basicPlusPremium = basicPlusPremium;
	}

	public int getTotalAddOnUnit() {
		int total = 0;
		for (MedProInsuAddOnDTO dto : getInsuredPersonAddOnDTOList()) {
			total += dto.getUnit();
		}
		return total;
	}

	public int getTotalUnit() {
		int totUnit = basicPlusUnit + unit;
		return totUnit;
	}

	public double getAddOnTermPremium() {
		return addOnTermPremium;
	}

	public void setAddOnTermPremium(double addOnTermPremium) {
		this.addOnTermPremium = addOnTermPremium;
	}

	public double getBasicPlusTermPremium() {
		return basicPlusTermPremium;
	}

	public void setBasicPlusTermPremium(double basicPlusTermPremium) {
		this.basicPlusTermPremium = basicPlusTermPremium;
	}

}
