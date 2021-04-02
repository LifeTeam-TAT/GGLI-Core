package org.ace.insurance.system.common.agent.history;

/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.FamilyInfoHistory;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.ViberContent;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.AGENTHISTORY)
@TableGenerator(name = "AGENTHISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "AGENTHISTORY_GEN", allocationSize = 10)
@Access(value = AccessType.FIELD)
public class AgentHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;
	@Transient
	private String prefix;
	private String codeNo;
	private String liscenseNo;
	private String initialId;
	private String idNo;
	private String fatherName;
	private String birthMark;
	private String accountNo;
	private String remark;
	private String training;
	private String outstandingEvent;
	@Embedded
	private Name name;

	@Enumerated(value = EnumType.STRING)
	private Gender gender;

	@Enumerated(value = EnumType.STRING)
	private MaritalStatus maritalStatus;

	@Temporal(TemporalType.DATE)
	private Date appointedDate;

	@ElementCollection
	@CollectionTable(name = "AGENT_FAMILY_HISTORY_LINK", joinColumns = @JoinColumn(name = "AGENTHISTORYID", referencedColumnName = "ID"))
	private List<FamilyInfoHistory> familyInfohistory;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QUALIFICATIONID", referencedColumnName = "ID")
	private Qualification qualification;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BANKBRANCHID", referencedColumnName = "ID")
	private BankBranch bankBranch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALEPOINTID", referencedColumnName = "ID")
	private SalePoint salePoint;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RELIGIONID", referencedColumnName = "ID")
	private Religion religion;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NATIONALITYID", referencedColumnName = "ID")
	private Country country;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;

	@Embedded
	private PermanentAddress permanentAddress;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "agenthistory", orphanRemoval = true)
	private AgentAttachmentHistory attachmenthistory;

	@Embedded
	private ResidentAddress residentAddress;

	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@Enumerated(value = EnumType.STRING)
	private IdType idType;

	@Column(name = "GROUPTYPE")
	@Enumerated(value = EnumType.STRING)
	private ProductGroupType groupType;

	@AttributeOverrides({ @AttributeOverride(name = "phone", column = @Column(name = "VoicePhoneNo")), @AttributeOverride(name = "mobile", column = @Column(name = "SMSPhoneNo")) })
	@Embedded
	private ContentInfo contentInfo;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "viberPhone", column = @Column(name = "ViberPhoneNo")) })
	private ViberContent viberContent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@Version
	private int version;

	@Transient
	private StateCode stateCode;

	@Transient
	private TownshipCode townshipCode;

	@Transient
	private IdConditionType idConditionType;

	@Transient
	private String fullIdNo;

	public AgentHistory() {
	}

	public AgentHistory(Agent agent) {
		super();
		this.prefix = agent.getPrefix();
		this.codeNo = agent.getCodeNo();
		this.liscenseNo = agent.getLiscenseNo();
		this.initialId = agent.getInitialId();
		this.idNo = agent.getIdNo();
		this.fatherName = agent.getFatherName();
		this.birthMark = agent.getBirthMark();
		this.accountNo = agent.getAccountNo();
		this.remark = agent.getRemark();
		this.training = agent.getTraining();
		this.outstandingEvent = agent.getOutstandingEvent();
		this.name = agent.getName();
		this.gender = agent.getGender();
		this.maritalStatus = agent.getMaritalStatus();
		this.appointedDate = agent.getAppointedDate();
		if (agent.getFamilyInfo().size() > 0) {
			for (FamilyInfo info : agent.getFamilyInfo()) {
				familyInfohistory.add(new FamilyInfoHistory(info));
			}
		}
		this.qualification = agent.getQualification();
		this.bankBranch = agent.getBankBranch();
		this.salePoint = agent.getSalePoint();
		this.religion = agent.getReligion();
		this.country = agent.getCountry();
		this.organization = agent.getOrganization();
		this.permanentAddress = agent.getPermanentAddress();
		if (agent.getAttachment() != null) {
			this.attachmenthistory = new AgentAttachmentHistory(agent.getAttachment());

		}
		this.residentAddress = agent.getResidentAddress();
		this.dateOfBirth = agent.getAppointedDate();
		this.idType = agent.getIdType();
		this.groupType = agent.getGroupType();
		this.contentInfo = agent.getContentInfo();
		this.viberContent = agent.getViberContent();
		this.branch = agent.getBranch();
		this.version = agent.getVersion();
		this.stateCode = agent.getStateCode();
		this.townshipCode = agent.getTownshipCode();
		this.idConditionType = agent.getIdConditionType();
		this.fullIdNo = agent.getFullIdNo();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "AGENTHISTORY_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public String getCodeNo() {
		return this.codeNo;
	}

	public void setCodeNo(String codeNo) {
		this.codeNo = codeNo;
	}

	public String getLiscenseNo() {
		return this.liscenseNo;
	}

	public void setLiscenseNo(String liscenseNo) {
		this.liscenseNo = liscenseNo;
	}

	public ContentInfo getContentInfo() {
		if (this.contentInfo == null) {
			this.contentInfo = new ContentInfo();
		}
		return this.contentInfo;
	}

	public void setContentInfo(ContentInfo contentInfo) {
		this.contentInfo = contentInfo;
	}

	public Branch getBranch() {
		return this.branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getFullName() {
		return initialId + name.getFullName();
	}

	public String getFullAddress() {
		String result = "";
		if (residentAddress.getResidentAddress() != null && !residentAddress.getResidentAddress().isEmpty()) {
			result = result + residentAddress.getResidentAddress();
		}
		if (residentAddress.getTownship() != null && !residentAddress.getTownship().getFullTownShip().isEmpty()) {
			result = result + ", " + residentAddress.getTownship().getFullTownShip();
		}
		return result;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getBirthMark() {
		return birthMark;
	}

	public void setBirthMark(String birthMark) {
		this.birthMark = birthMark;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTraining() {
		return training;
	}

	public void setTraining(String training) {
		this.training = training;
	}

	public String getOutstandingEvent() {
		return outstandingEvent;
	}

	public void setOutstandingEvent(String outstandingEvent) {
		this.outstandingEvent = outstandingEvent;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Date getAppointedDate() {
		return appointedDate;
	}

	public void setAppointedDate(Date appointedDate) {
		this.appointedDate = appointedDate;
	}

	public List<FamilyInfoHistory> getFamilyInfohistory() {
		return familyInfohistory;
	}

	public AgentAttachmentHistory getAttachmenthistory() {
		return attachmenthistory;
	}

	public void setFamilyInfohistory(List<FamilyInfoHistory> familyInfohistory) {
		this.familyInfohistory = familyInfohistory;
	}

	public void setAttachmenthistory(AgentAttachmentHistory attachmenthistory) {
		this.attachmenthistory = attachmenthistory;
	}

	public Qualification getQualification() {
		return qualification;
	}

	public void setQualification(Qualification qualification) {
		this.qualification = qualification;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public Religion getReligion() {
		return religion;
	}

	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public ProductGroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(ProductGroupType groupType) {
		this.groupType = groupType;
	}

	public PermanentAddress getPermanentAddress() {
		if (this.permanentAddress == null) {
			this.permanentAddress = new PermanentAddress();
		}
		return permanentAddress;
	}

	public void setPermanentAddress(PermanentAddress permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public ResidentAddress getResidentAddress() {
		if (this.residentAddress == null) {
			this.residentAddress = new ResidentAddress();
		}
		return this.residentAddress;
	}

	public void setResidentAddress(ResidentAddress residentAddress) {
		this.residentAddress = residentAddress;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
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

	public IdConditionType getIdConditionType() {
		return idConditionType;
	}

	public void setIdConditionType(IdConditionType idConditionType) {
		this.idConditionType = idConditionType;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public void setFullIdNo(String fullIdNo) {
		this.fullIdNo = fullIdNo;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public ViberContent getViberContent() {
		if (viberContent == null) {
			this.viberContent = new ViberContent();
		}
		return this.viberContent;
	}

	public void setViberContent(ViberContent viberContent) {
		this.viberContent = viberContent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNo == null) ? 0 : accountNo.hashCode());
		result = prime * result + ((appointedDate == null) ? 0 : appointedDate.hashCode());
		result = prime * result + ((attachmenthistory == null) ? 0 : attachmenthistory.hashCode());
		result = prime * result + ((bankBranch == null) ? 0 : bankBranch.hashCode());
		result = prime * result + ((birthMark == null) ? 0 : birthMark.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((codeNo == null) ? 0 : codeNo.hashCode());
		result = prime * result + ((contentInfo == null) ? 0 : contentInfo.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + ((familyInfohistory == null) ? 0 : familyInfohistory.hashCode());
		result = prime * result + ((fatherName == null) ? 0 : fatherName.hashCode());
		result = prime * result + ((fullIdNo == null) ? 0 : fullIdNo.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((groupType == null) ? 0 : groupType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idConditionType == null) ? 0 : idConditionType.hashCode());
		result = prime * result + ((idNo == null) ? 0 : idNo.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
		result = prime * result + ((initialId == null) ? 0 : initialId.hashCode());
		result = prime * result + ((liscenseNo == null) ? 0 : liscenseNo.hashCode());
		result = prime * result + ((maritalStatus == null) ? 0 : maritalStatus.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((outstandingEvent == null) ? 0 : outstandingEvent.hashCode());
		result = prime * result + ((permanentAddress == null) ? 0 : permanentAddress.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((qualification == null) ? 0 : qualification.hashCode());
		result = prime * result + ((religion == null) ? 0 : religion.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((residentAddress == null) ? 0 : residentAddress.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((stateCode == null) ? 0 : stateCode.hashCode());
		result = prime * result + ((townshipCode == null) ? 0 : townshipCode.hashCode());
		result = prime * result + ((training == null) ? 0 : training.hashCode());
		result = prime * result + version;
		result = prime * result + ((viberContent == null) ? 0 : viberContent.hashCode());
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
		AgentHistory other = (AgentHistory) obj;
		if (accountNo == null) {
			if (other.accountNo != null)
				return false;
		} else if (!accountNo.equals(other.accountNo))
			return false;
		if (appointedDate == null) {
			if (other.appointedDate != null)
				return false;
		} else if (!appointedDate.equals(other.appointedDate))
			return false;
		if (attachmenthistory == null) {
			if (other.attachmenthistory != null)
				return false;
		} else if (!attachmenthistory.equals(other.attachmenthistory))
			return false;
		if (bankBranch == null) {
			if (other.bankBranch != null)
				return false;
		} else if (!bankBranch.equals(other.bankBranch))
			return false;
		if (birthMark == null) {
			if (other.birthMark != null)
				return false;
		} else if (!birthMark.equals(other.birthMark))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (codeNo == null) {
			if (other.codeNo != null)
				return false;
		} else if (!codeNo.equals(other.codeNo))
			return false;
		if (contentInfo == null) {
			if (other.contentInfo != null)
				return false;
		} else if (!contentInfo.equals(other.contentInfo))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (familyInfohistory == null) {
			if (other.familyInfohistory != null)
				return false;
		} else if (!familyInfohistory.equals(other.familyInfohistory))
			return false;
		if (fatherName == null) {
			if (other.fatherName != null)
				return false;
		} else if (!fatherName.equals(other.fatherName))
			return false;
		if (fullIdNo == null) {
			if (other.fullIdNo != null)
				return false;
		} else if (!fullIdNo.equals(other.fullIdNo))
			return false;
		if (gender != other.gender)
			return false;
		if (groupType != other.groupType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idConditionType != other.idConditionType)
			return false;
		if (idNo == null) {
			if (other.idNo != null)
				return false;
		} else if (!idNo.equals(other.idNo))
			return false;
		if (idType != other.idType)
			return false;
		if (initialId == null) {
			if (other.initialId != null)
				return false;
		} else if (!initialId.equals(other.initialId))
			return false;
		if (liscenseNo == null) {
			if (other.liscenseNo != null)
				return false;
		} else if (!liscenseNo.equals(other.liscenseNo))
			return false;
		if (maritalStatus != other.maritalStatus)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (outstandingEvent == null) {
			if (other.outstandingEvent != null)
				return false;
		} else if (!outstandingEvent.equals(other.outstandingEvent))
			return false;
		if (permanentAddress == null) {
			if (other.permanentAddress != null)
				return false;
		} else if (!permanentAddress.equals(other.permanentAddress))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (qualification == null) {
			if (other.qualification != null)
				return false;
		} else if (!qualification.equals(other.qualification))
			return false;
		if (religion == null) {
			if (other.religion != null)
				return false;
		} else if (!religion.equals(other.religion))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (residentAddress == null) {
			if (other.residentAddress != null)
				return false;
		} else if (!residentAddress.equals(other.residentAddress))
			return false;
		if (salePoint == null) {
			if (other.salePoint != null)
				return false;
		} else if (!salePoint.equals(other.salePoint))
			return false;
		if (stateCode == null) {
			if (other.stateCode != null)
				return false;
		} else if (!stateCode.equals(other.stateCode))
			return false;
		if (townshipCode == null) {
			if (other.townshipCode != null)
				return false;
		} else if (!townshipCode.equals(other.townshipCode))
			return false;
		if (training == null) {
			if (other.training != null)
				return false;
		} else if (!training.equals(other.training))
			return false;
		if (version != other.version)
			return false;
		if (viberContent == null) {
			if (other.viberContent != null)
				return false;
		} else if (!viberContent.equals(other.viberContent))
			return false;
		return true;
	}

}