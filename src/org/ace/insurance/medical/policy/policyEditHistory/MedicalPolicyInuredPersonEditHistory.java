package org.ace.insurance.medical.policy.policyEditHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAttachment;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonGuardian;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.medical.proposal.MedicalPersonHistoryRecord;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAttachment;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonKeyFactorValue;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name=TableName.MEDPOLICY_INSUREDPERSON_EDITHISTORY)
@EntityListeners(IDInterceptor.class)
@TableGenerator(name = "MEDICALPOLICY_INSUREDPERSON_EDITHISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "MEDICALPOLICY_INSUREDPERSON_EDITHISTORY_GEN", allocationSize = 10)
public class MedicalPolicyInuredPersonEditHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEDICALPOLICY_INSUREDPERSON_EDITHISTORY_GEN")
	private String id;
	@Column(name = "PERIODOFMONTH")
	private int periodMonth;
	@Column(name = "AGE")
	private int age;
	private int paymentTerm;
	private double premium;
	private double addOnPremium;
	private double operationAmount;
	private double disabilityAmount;
	private boolean actived;
	@Column(name = "HOSPITALDAYCOUNT")
	private int hosp_day_count;
	private boolean death;
	private int unit;
	private int basicPlusUnit;
	private double basicPlusPremium;
	private double totalNcbPremium;
	private double totalDiscountPremium;
	@Column(name = "BASICTERMPREMIUM")
	private double basicTermPremium;
	@Column(name = "ADDONTERMPREMIUM")
	private double addonTermPremium;
	@Column(name = "INPERSONCODENO")
	private String insPersonCodeNo;
	@Column(name = "INPERSONGROUPCODENO")
	private String inPersonGroupCodeNo;

	@Column(name = "BASICPLUSTERMPREMIUM")
	private double basicPlusTermPremium;

	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;

	@Enumerated(EnumType.STRING)
	private ClaimStatus claimStatus;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RELATIONSHIPID", referencedColumnName = "ID")
	private RelationShip relationship;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEDICALPOLICYID", referencedColumnName = "ID")
	private MedicalPolicy medicalPolicy;*/

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "policyInsuredPerson", orphanRemoval = true)
	private List<MedicalPolicyInsuredPersonBeneficiariesEditHistory> policyInsuredPersonBeneficiariesList;
	
	@Transient
	private String prefix;

	@Version
	private int version;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;
	
	public MedicalPolicyInuredPersonEditHistory(){
		
	}
	
	public MedicalPolicyInuredPersonEditHistory(MedicalPolicyInsuredPerson insuredPerson) {
		this.dateOfBirth = insuredPerson.getCustomer().getDateOfBirth();
		this.customer = insuredPerson.getCustomer();
		this.periodMonth = insuredPerson.getPeriodMonth();
		this.startDate = insuredPerson.getStartDate();
		this.endDate = insuredPerson.getEndDate();
		this.product = insuredPerson.getProduct();
		this.premium = insuredPerson.getPremium();
		this.addOnPremium = insuredPerson.getAddOnPremium();
		this.relationship = insuredPerson.getRelationship();
		this.insPersonCodeNo = insuredPerson.getInsPersonCodeNo();
		this.inPersonGroupCodeNo = insuredPerson.getInPersonGroupCodeNo();
		this.basicPlusUnit = insuredPerson.getBasicPlusUnit();
		this.basicPlusPremium = insuredPerson.getBasicPlusPremium();
		this.totalNcbPremium = insuredPerson.getTotalNcbPremium();
		this.age = Utils.getAgeForNextYear(insuredPerson.getCustomer().getDateOfBirth());
		this.unit = insuredPerson.getUnit();
		this.basicTermPremium = insuredPerson.getBasicTermPremium();
		this.addonTermPremium = insuredPerson.getAddOnTermPremium();

		for (MedicalPolicyInsuredPersonBeneficiaries insuredPersonBeneficiaries : insuredPerson.getPolicyInsuredPersonBeneficiariesList()) {
			addInsuredPersonBeneficiaries( new MedicalPolicyInsuredPersonBeneficiariesEditHistory(insuredPersonBeneficiaries));
		}

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

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getAddOnPremium() {
		return addOnPremium;
	}

	public void setAddOnPremium(double addOnPremium) {
		this.addOnPremium = addOnPremium;
	}

	public double getOperationAmount() {
		return operationAmount;
	}

	public void setOperationAmount(double operationAmount) {
		this.operationAmount = operationAmount;
	}

	public double getDisabilityAmount() {
		return disabilityAmount;
	}

	public void setDisabilityAmount(double disabilityAmount) {
		this.disabilityAmount = disabilityAmount;
	}

	public boolean isActived() {
		return actived;
	}

	public void setActived(boolean actived) {
		this.actived = actived;
	}

	public int getHosp_day_count() {
		return hosp_day_count;
	}

	public void setHosp_day_count(int hosp_day_count) {
		this.hosp_day_count = hosp_day_count;
	}

	public boolean isDeath() {
		return death;
	}

	public void setDeath(boolean death) {
		this.death = death;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
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

	public double getTotalNcbPremium() {
		return totalNcbPremium;
	}

	public void setTotalNcbPremium(double totalNcbPremium) {
		this.totalNcbPremium = totalNcbPremium;
	}

	public double getTotalDiscountPremium() {
		return totalDiscountPremium;
	}

	public void setTotalDiscountPremium(double totalDiscountPremium) {
		this.totalDiscountPremium = totalDiscountPremium;
	}

	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	public double getAddonTermPremium() {
		return addonTermPremium;
	}

	public void setAddonTermPremium(double addonTermPremium) {
		this.addonTermPremium = addonTermPremium;
	}

	public String getInsPersonCodeNo() {
		return insPersonCodeNo;
	}

	public void setInsPersonCodeNo(String insPersonCodeNo) {
		this.insPersonCodeNo = insPersonCodeNo;
	}

	public String getInPersonGroupCodeNo() {
		return inPersonGroupCodeNo;
	}

	public void setInPersonGroupCodeNo(String inPersonGroupCodeNo) {
		this.inPersonGroupCodeNo = inPersonGroupCodeNo;
	}

	public double getBasicPlusTermPremium() {
		return basicPlusTermPremium;
	}

	public void setBasicPlusTermPremium(double basicPlusTermPremium) {
		this.basicPlusTermPremium = basicPlusTermPremium;
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

	public ClaimStatus getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(ClaimStatus claimStatus) {
		this.claimStatus = claimStatus;
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

	public void setProduct(Product product) {
		this.product = product;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<MedicalPolicyInsuredPersonBeneficiariesEditHistory> getPolicyInsuredPersonBeneficiariesList() {
		if (this.policyInsuredPersonBeneficiariesList == null) {
			this.policyInsuredPersonBeneficiariesList = new ArrayList<MedicalPolicyInsuredPersonBeneficiariesEditHistory>();
		}
		return this.policyInsuredPersonBeneficiariesList;
	}

	public void setPolicyInsuredPersonBeneficiariesList(
			List<MedicalPolicyInsuredPersonBeneficiariesEditHistory> policyInsuredPersonBeneficiariesList) {
		this.policyInsuredPersonBeneficiariesList = policyInsuredPersonBeneficiariesList;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}
	

	public void addInsuredPersonBeneficiaries(MedicalPolicyInsuredPersonBeneficiariesEditHistory policyInsuredPersonBeneficiaries) {
		getPolicyInsuredPersonBeneficiariesList().add(policyInsuredPersonBeneficiaries);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	

}
