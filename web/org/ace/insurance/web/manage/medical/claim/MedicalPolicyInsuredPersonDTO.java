package org.ace.insurance.web.manage.medical.claim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAttachment;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.web.common.CommonDTO;
import org.ace.insurance.web.manage.medical.proposal.CustomerDTO;
import org.ace.insurance.web.manage.medical.proposal.PolicyGuardianDTO;

public class MedicalPolicyInsuredPersonDTO extends CommonDTO {

	private boolean exitsEntity;
	private boolean death;
	private String id;
	private int periodMonth;
	private int age;
	private int paymentTerm;
	private int totalHosDays;
	private double basicTermPremium;
	private double addOnTermPremium;
	private double premium;
	private double totalDiscountPremium;
	private Date dateOfBirth;
	private Date startDate;
	private Date endDate;
	private int unit;
	private int basicPlusUnit;
	private double basicPlusPremium;
	private ClaimStatus claimStatus;
	private Product product;
	private Customer customer;
	private PolicyGuardianDTO guardian;
	private MedicalPolicyDTO medicalPolicyDTO;
	private List<MedicalPolicyInsuredPersonAttachment> attachmentList;
	private List<MedicalPolicyInsuredPersonAddOn> policyInsuredPersonAddOnList;
	private List<MedicalPolicyInsuredPersonKeyFactorValue> policyInsuredPersonkeyFactorValueList;
	private List<MedicalPolicyInsuredPersonBeneficiaryDTO> policyInsuredPersonBeneficiariesDtoList;
	private List<MedicalPolicyInsuredPersonAddOn> addOnList;
	private int version;
	private String tempId;
	private boolean sameInsuredPerson;
	private int periodYear;
	private RelationShip relationship;
	private CustomerDTO customerDTO;
	private Organization organization;
	private double addOnPremium;

	public MedicalPolicyInsuredPersonDTO() {
		tempId = System.nanoTime() + "";
		customerDTO = new CustomerDTO();
	}

	public MedicalPolicyInsuredPersonDTO(MedicalPolicyInsuredPersonDTO medicalPolicyInsuredPersonDTO) {
		this.id = medicalPolicyInsuredPersonDTO.getId();
		this.periodMonth = medicalPolicyInsuredPersonDTO.getPeriodMonth();
		this.age = medicalPolicyInsuredPersonDTO.getAge();
		this.paymentTerm = medicalPolicyInsuredPersonDTO.getPaymentTerm();
		this.basicTermPremium = medicalPolicyInsuredPersonDTO.getBasicTermPremium();
		this.addOnTermPremium = medicalPolicyInsuredPersonDTO.getAddOnTermPremium();
		this.premium = medicalPolicyInsuredPersonDTO.getPremium();
		this.dateOfBirth = medicalPolicyInsuredPersonDTO.getDateOfBirth();
		this.startDate = medicalPolicyInsuredPersonDTO.getStartDate();
		this.endDate = medicalPolicyInsuredPersonDTO.getEndDate();
		this.unit = medicalPolicyInsuredPersonDTO.getUnit();
		this.basicPlusUnit = medicalPolicyInsuredPersonDTO.getBasicPlusUnit();
		this.claimStatus = medicalPolicyInsuredPersonDTO.getClaimStatus();
		this.product = medicalPolicyInsuredPersonDTO.getProduct();
		this.customer = medicalPolicyInsuredPersonDTO.getCustomer();
		this.attachmentList = medicalPolicyInsuredPersonDTO.getAttachmentList();
		this.policyInsuredPersonAddOnList = medicalPolicyInsuredPersonDTO.getPolicyInsuredPersonAddOnList();
		//TODO FIXME PSH Two AddonList
		this.policyInsuredPersonAddOnList = medicalPolicyInsuredPersonDTO.getAddOnList();
		this.policyInsuredPersonkeyFactorValueList = medicalPolicyInsuredPersonDTO.getPolicyInsuredPersonkeyFactorValueList();
		this.policyInsuredPersonBeneficiariesDtoList = medicalPolicyInsuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList();
		this.version = medicalPolicyInsuredPersonDTO.getVersion();
		this.guardian = medicalPolicyInsuredPersonDTO.getGuardian();
		this.exitsEntity = medicalPolicyInsuredPersonDTO.isExitsEntity();
		this.death = medicalPolicyInsuredPersonDTO.isDeath();
		this.tempId = medicalPolicyInsuredPersonDTO.getTempId();
		this.periodYear = medicalPolicyInsuredPersonDTO.getPeriodYear();
		this.relationship = medicalPolicyInsuredPersonDTO.getRelationship();
		this.organization = medicalPolicyInsuredPersonDTO.getOrganization();
		this.addOnPremium = medicalPolicyInsuredPersonDTO.getAddOnPremium();
	}

	public int getTotalHosDays() {
		return totalHosDays;
	}

	public void setTotalHosDays(int totalHosDays) {
		this.totalHosDays = totalHosDays;
	}

	public MedicalPolicyDTO getMedicalPolicyDTO() {
		return medicalPolicyDTO;
	}

	public void setMedicalPolicyDTO(MedicalPolicyDTO medicalPolicyDTO) {
		this.medicalPolicyDTO = medicalPolicyDTO;
	}

	public int getBasicPlusUnit() {
		return basicPlusUnit;
	}

	public void setBasicPlusUnit(int basicPlusUnit) {
		this.basicPlusUnit = basicPlusUnit;
	}

	public Double getBasicPlusPremium() {
		return basicPlusPremium;
	}

	public void setBasicPlusPremium(Double basicPlusPremium) {
		this.basicPlusPremium = basicPlusPremium;
	}

	public PolicyGuardianDTO getGuardian() {
		return guardian;
	}

	public void setGuardian(PolicyGuardianDTO guardian) {
		this.guardian = guardian;
	}

	public boolean isDeath() {
		return death;
	}

	public void setDeath(boolean death) {
		this.death = death;
	}

	public boolean isExitsEntity() {
		return exitsEntity;
	}

	public void setExitsEntity(boolean exitsEntity) {
		this.exitsEntity = exitsEntity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPeriodMonth() {
		return periodMonth;
	}

	public void setPeriodMonth(int periodMonth) {
		this.periodMonth = periodMonth;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
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

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
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

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public ClaimStatus getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(ClaimStatus claimStatus) {
		this.claimStatus = claimStatus;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustomerDTO getCustomerDTO() {
		return customerDTO;
	}

	public void setCustomerDTO(CustomerDTO customerDTO) {
		this.customerDTO = customerDTO;
	}

	public List<MedicalPolicyInsuredPersonAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<MedicalPolicyInsuredPersonAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<MedicalPolicyInsuredPersonAddOn> getPolicyInsuredPersonAddOnList() {
		return policyInsuredPersonAddOnList;
	}

	public void setPolicyInsuredPersonAddOnList(List<MedicalPolicyInsuredPersonAddOn> policyInsuredPersonAddOnList) {
		this.policyInsuredPersonAddOnList = policyInsuredPersonAddOnList;
	}

	public List<MedicalPolicyInsuredPersonKeyFactorValue> getPolicyInsuredPersonkeyFactorValueList() {
		return policyInsuredPersonkeyFactorValueList;
	}

	public void setPolicyInsuredPersonkeyFactorValueList(List<MedicalPolicyInsuredPersonKeyFactorValue> policyInsuredPersonkeyFactorValueList) {
		this.policyInsuredPersonkeyFactorValueList = policyInsuredPersonkeyFactorValueList;
	}

	public List<MedicalPolicyInsuredPersonBeneficiaryDTO> getPolicyInsuredPersonBeneficiariesDtoList() {
		return policyInsuredPersonBeneficiariesDtoList;
	}

	public void setPolicyInsuredPersonBeneficiariesDtoList(List<MedicalPolicyInsuredPersonBeneficiaryDTO> policyInsuredPersonBeneficiariesDtoList) {
		this.policyInsuredPersonBeneficiariesDtoList = policyInsuredPersonBeneficiariesDtoList;
	}

	public List<MedicalPolicyInsuredPersonAddOn> getAddOnList() {
		if (addOnList == null) {
			addOnList = new ArrayList<MedicalPolicyInsuredPersonAddOn>();
		}
		return addOnList;
	}

	public void setAddOnList(List<MedicalPolicyInsuredPersonAddOn> addOnList) {
		this.addOnList = addOnList;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void addMedicalPolicyInsuredPersonAttachment(MedicalPolicyInsuredPersonAttachment medicalPolicyInsuredPersonAttachment) {
		if (this.attachmentList == null) {
			this.attachmentList = new ArrayList<MedicalPolicyInsuredPersonAttachment>();
		}
		this.attachmentList.add(medicalPolicyInsuredPersonAttachment);
	}

	public void addAddOn(MedicalPolicyInsuredPersonAddOn addOn) {
		if (this.addOnList == null) {
			this.addOnList = new ArrayList<MedicalPolicyInsuredPersonAddOn>();
		}
		this.addOnList.add(addOn);
	}

	public void addMedicalPolicyInsuredPersonBeneficiaryDTO(MedicalPolicyInsuredPersonBeneficiaryDTO medicalPolicyInsuredPersonBeneficiaryDTO) {
		if (policyInsuredPersonBeneficiariesDtoList == null) {
			this.policyInsuredPersonBeneficiariesDtoList = new ArrayList<MedicalPolicyInsuredPersonBeneficiaryDTO>();
		}
		this.policyInsuredPersonBeneficiariesDtoList.add(medicalPolicyInsuredPersonBeneficiaryDTO);
	}

	public RelationShip getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
	}

	public String getFullName() {
		if (this.customer != null) {
			return this.customer.getFullName();
		} else {
			return "";
		}

	}

	public String getIdNo() {
		if (this.customer != null) {
			return this.customer.getIdNo();
		} else {
			return "";
		}

	}

	public ResidentAddress getResidentAddress() {
		if (this.customer != null) {
			return this.customer.getResidentAddress();
		} else {
			return null;
		}

	}

	public int getPeriodYears() {
		return periodMonth / 12;
	}

	public double getTotalDiscountPremium() {
		return totalDiscountPremium;
	}

	public void setTotalDiscountPremium(double totalDiscountPremium) {
		this.totalDiscountPremium = totalDiscountPremium;
	}

	public int getPeriodYear() {
		return periodYear;
	}

	public void setPeriodYear(int periodYear) {
		this.periodYear = periodYear;
	}

	public double getAddOnPremium() {
		return addOnPremium;
	}

	public void setAddOnPremium(double addOnPremium) {
		this.addOnPremium = addOnPremium;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public boolean isSameInsuredPerson() {
		return sameInsuredPerson;
	}

	public void setSameInsuredPerson(boolean sameInsuredPerson) {
		this.sameInsuredPerson = sameInsuredPerson;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setBasicPlusPremium(double basicPlusPremium) {
		this.basicPlusPremium = basicPlusPremium;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(addOnPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(addOnTermPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + age;
		temp = Double.doubleToLongBits(basicPlusPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + basicPlusUnit;
		temp = Double.doubleToLongBits(basicTermPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((claimStatus == null) ? 0 : claimStatus.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((customerDTO == null) ? 0 : customerDTO.hashCode());
		result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + (death ? 1231 : 1237);
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + (exitsEntity ? 1231 : 1237);
		result = prime * result + ((guardian == null) ? 0 : guardian.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((medicalPolicyDTO == null) ? 0 : medicalPolicyDTO.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + paymentTerm;
		result = prime * result + periodMonth;
		result = prime * result + periodYear;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
		result = prime * result + (sameInsuredPerson ? 1231 : 1237);
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
		temp = Double.doubleToLongBits(totalDiscountPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + totalHosDays;
		result = prime * result + unit;
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MedicalPolicyInsuredPersonDTO other = (MedicalPolicyInsuredPersonDTO) obj;
		if (Double.doubleToLongBits(addOnPremium) != Double.doubleToLongBits(other.addOnPremium))
			return false;
		if (Double.doubleToLongBits(addOnTermPremium) != Double.doubleToLongBits(other.addOnTermPremium))
			return false;
		if (age != other.age)
			return false;
		if (Double.doubleToLongBits(basicPlusPremium) != Double.doubleToLongBits(other.basicPlusPremium))
			return false;
		if (basicPlusUnit != other.basicPlusUnit)
			return false;
		if (Double.doubleToLongBits(basicTermPremium) != Double.doubleToLongBits(other.basicTermPremium))
			return false;
		if (claimStatus != other.claimStatus)
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (customerDTO == null) {
			if (other.customerDTO != null)
				return false;
		} else if (!customerDTO.equals(other.customerDTO))
			return false;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (death != other.death)
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (exitsEntity != other.exitsEntity)
			return false;
		if (guardian == null) {
			if (other.guardian != null)
				return false;
		} else if (!guardian.equals(other.guardian))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (medicalPolicyDTO == null) {
			if (other.medicalPolicyDTO != null)
				return false;
		} else if (!medicalPolicyDTO.equals(other.medicalPolicyDTO))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (paymentTerm != other.paymentTerm)
			return false;
		if (periodMonth != other.periodMonth)
			return false;
		if (periodYear != other.periodYear)
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		if (sameInsuredPerson != other.sameInsuredPerson)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		if (Double.doubleToLongBits(totalDiscountPremium) != Double.doubleToLongBits(other.totalDiscountPremium))
			return false;
		if (totalHosDays != other.totalHosDays)
			return false;
		if (unit != other.unit)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
