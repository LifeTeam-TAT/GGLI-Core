package org.ace.insurance.medical.policy.policyEditHistory;

import java.util.Date;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name=TableName.MEDPOLICY_INSUREDPERSONBENEFICIARIES_EDITHISTORY)
@EntityListeners(IDInterceptor.class)
@TableGenerator(name = "MEDICALPOLICY_BENEFICIARES_EDITHISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "MEDICALPOLICY_BENEFICIARES_EDITHISTORY_GEN", allocationSize = 10)
public class MedicalPolicyInsuredPersonBeneficiariesEditHistory {
	
	@Version
	private int version;
	private int age;
	private float percentage;
	private String beneficiaryNo;
	private String initialId;
	private String fatherName;
	private String idNo;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEDICALPOLICY_BENEFICIARES_EDITHISTORY_GEN")
	private String id;

	@Enumerated(value = EnumType.STRING)
	private Gender gender;

	@Enumerated(value = EnumType.STRING)
	private IdType idType;

	@Enumerated(EnumType.STRING)
	private ClaimStatus claimStatus;

	@Embedded
	private ResidentAddress residentAddress;

	@Embedded
	private Name name;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RELATIONSHIPID", referencedColumnName = "ID")
	private RelationShip relationship;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POLICYINSUREDPERSONID", referencedColumnName = "ID")
	private MedicalPolicyInuredPersonEditHistory policyInsuredPerson;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deathDate;
	private String deathReason;
	private boolean death;
	@Transient
	private String fullIdNo;

	@Enumerated(value = EnumType.STRING)
	private IdConditionType idConditionType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATECODEID", referencedColumnName = "ID")
	private StateCode stateCode;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOWNSHIPCODEID", referencedColumnName = "ID")
	private TownshipCode townshipCode;

	@Embedded
	private ContentInfo contentInfo;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;
	
	 public MedicalPolicyInsuredPersonBeneficiariesEditHistory(){
		
	}
	
	public MedicalPolicyInsuredPersonBeneficiariesEditHistory(MedicalPolicyInsuredPersonBeneficiaries insuredPersonBeneficiaries) {
		this.beneficiaryNo = insuredPersonBeneficiaries.getBeneficiaryNo();
		this.age = insuredPersonBeneficiaries.getAge();
		this.percentage = insuredPersonBeneficiaries.getPercentage();
		this.relationship = insuredPersonBeneficiaries.getRelationship();
		this.initialId = insuredPersonBeneficiaries.getInitialId();
		this.fatherName = insuredPersonBeneficiaries.getFatherName();
		this.idNo = insuredPersonBeneficiaries.getIdNo();
		this.gender = insuredPersonBeneficiaries.getGender();
		this.idType = insuredPersonBeneficiaries.getIdType();
		this.residentAddress = insuredPersonBeneficiaries.getResidentAddress();
		this.name = insuredPersonBeneficiaries.getName();
		this.relationship = insuredPersonBeneficiaries.getRelationship();
		this.contentInfo = insuredPersonBeneficiaries.getContentInfo();
		this.stateCode = insuredPersonBeneficiaries.getStateCode();
		this.townshipCode = insuredPersonBeneficiaries.getTownshipCode();
		this.idConditionType = insuredPersonBeneficiaries.getIdConditionType();
		this.fullIdNo = insuredPersonBeneficiaries.getFullIdNo();
	}
	
	

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public float getPercentage() {
		return percentage;
	}

	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}

	public String getBeneficiaryNo() {
		return beneficiaryNo;
	}

	public void setBeneficiaryNo(String beneficiaryNo) {
		this.beneficiaryNo = beneficiaryNo;
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public ClaimStatus getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(ClaimStatus claimStatus) {
		this.claimStatus = claimStatus;
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

	public RelationShip getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
	}

	

	public MedicalPolicyInuredPersonEditHistory getPolicyInsuredPerson() {
		return policyInsuredPerson;
	}

	public void setPolicyInsuredPerson(MedicalPolicyInuredPersonEditHistory policyInsuredPerson) {
		this.policyInsuredPerson = policyInsuredPerson;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public String getDeathReason() {
		return deathReason;
	}

	public void setDeathReason(String deathReason) {
		this.deathReason = deathReason;
	}

	public boolean isDeath() {
		return death;
	}

	public void setDeath(boolean death) {
		this.death = death;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public void setFullIdNo(String fullIdNo) {
		this.fullIdNo = fullIdNo;
	}

	public IdConditionType getIdConditionType() {
		return idConditionType;
	}

	public void setIdConditionType(IdConditionType idConditionType) {
		this.idConditionType = idConditionType;
	}

	public StateCode getStateCode() {
		return stateCode;
	}

	public void setStateCode(StateCode stateCode) {
		this.stateCode = stateCode;
	}

	public TownshipCode getTownshipCode() {
		return townshipCode;
	}

	public void setTownshipCode(TownshipCode townshipCode) {
		this.townshipCode = townshipCode;
	}

	public ContentInfo getContentInfo() {
		return contentInfo;
	}

	public void setContentInfo(ContentInfo contentInfo) {
		this.contentInfo = contentInfo;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}


}
