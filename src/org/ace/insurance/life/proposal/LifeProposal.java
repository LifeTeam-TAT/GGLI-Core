package org.ace.insurance.life.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.common.interfaces.IProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.LifePolicyAttachment;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.mobile.enums.ProposalStatus;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.auditLog.AuditLog;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.UserType;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.LIFEPROPOSAL)
@TableGenerator(name = "LIFEPROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPROPOSAL_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "LifeProposal.findAll", query = "SELECT m FROM LifeProposal m "),
		@NamedQuery(name = "LifeProposal.findByDate", query = "SELECT m FROM LifeProposal m WHERE m.submittedDate BETWEEN :startDate AND :endDate"),
		@NamedQuery(name = "LifeProposal.updateCompleteStatus", query = "UPDATE LifeProposal m SET m.complete = :complete WHERE m.id = :id") })
@Access(value = AccessType.FIELD)
public class LifeProposal implements Serializable, IDataModel, IProposal {
	private static final long serialVersionUID = 7564214263861012292L;

	private boolean complete;

	@Transient
	private String id;

	@Transient
	private String prefix;

	private String proposalNo;

	private String portalId;

	private boolean isSkipPaymentTLF;

	private boolean isMigrate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;

	@Enumerated(EnumType.STRING)
	private ProposalType proposalType;

	@Enumerated(EnumType.STRING)
	private ProposalStatus proposalStatus;

	@Enumerated(value = EnumType.STRING)
	private ClassificationOfHealth customerClsOfHealth;

	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFERRALID", referencedColumnName = "ID")
	private Customer referral;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALEMANID", referencedColumnName = "ID")
	private SaleMan saleMan;

	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "CHANNELID", referencedColumnName = "ID")
	// private Channel channel;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPOLICYID", referencedColumnName = "ID")
	private LifePolicy lifePolicy;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lifeProposal", orphanRemoval = true)
	private List<ProposalInsuredPerson> proposalInsuredPersonList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lifeProposal", orphanRemoval = true)
	private List<LifeProposalAttachment> attachmentList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
	private List<Attachment> customerMedicalCheckUpAttachmentList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "LIFEPROPOSALID", referencedColumnName = "ID")
	private List<SurveyQuestionAnswer> customerSurveyQuestionAnswerList;

	@OneToOne
	@JoinColumn(name = "SALEPOINTID")
	private SalePoint salePoint;

	@OneToOne
	@JoinColumn(name = "GROUPFARMERPROPOSALID", referencedColumnName = "id")
	private GroupFarmerProposal groupFarmerProposal;

	private String bpmsProposalNo;

	@OneToOne
	@JoinColumn(name = "EIPS_ID", referencedColumnName = "id")
	private Eips eips;
	
	@OneToOne
	@JoinColumn(name = "AUDITLOGID", referencedColumnName = "id")
	private AuditLog auditLog;

	private boolean isStaffPlan;

	public String getBpmsProposalNo() {
		return bpmsProposalNo;
	}

	public void setBpmsProposalNo(String bpmsProposalNo) {
		this.bpmsProposalNo = bpmsProposalNo;
	}

	@Transient
	private String tempId;

	@Version
	private int version;

	public LifeProposal() {
		tempId = System.nanoTime() + "";
	}

	public LifeProposal(LifePolicy lifePolicy) {
		this.agent = lifePolicy.getAgent();
		this.branch = lifePolicy.getBranch();
		this.customer = lifePolicy.getCustomer();
		this.saleMan = lifePolicy.getSaleMan();
		this.paymentType = lifePolicy.getPaymentType();
		this.organization = lifePolicy.getOrganization();
		this.lifePolicy = lifePolicy;
		this.salePoint = lifePolicy.getSalePoint();
		this.submittedDate = lifePolicy.getCommenmanceDate();
		for (LifePolicyAttachment attachment : lifePolicy.getAttachmentList()) {
			addAttachment(new LifeProposalAttachment(attachment));
		}
		for (Attachment medicalAttach : lifePolicy.getCustomerMedicalCheckUpAttachmentList()) {
			addCustomerMedicalChkUpAttachment(medicalAttach);
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPROPOSAL_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		} else {
			this.id = null;
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public boolean getComplete() {
		return this.complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<ProposalInsuredPerson> getProposalInsuredPersonList() {
		if (this.proposalInsuredPersonList == null) {
			this.proposalInsuredPersonList = new ArrayList<ProposalInsuredPerson>();
		}
		return this.proposalInsuredPersonList;
	}

	public void setInsuredPersonList(List<ProposalInsuredPerson> proposalInsuredPersonList) {
		this.proposalInsuredPersonList = proposalInsuredPersonList;
	}

	public List<LifeProposalAttachment> getAttachmentList() {
		if (this.attachmentList == null) {
			this.attachmentList = new ArrayList<LifeProposalAttachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<LifeProposalAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public ClassificationOfHealth getCustomerClsOfHealth() {
		return customerClsOfHealth;
	}

	public void setCustomerClsOfHealth(ClassificationOfHealth customerClsOfHealth) {
		this.customerClsOfHealth = customerClsOfHealth;
	}

	public List<Attachment> getCustomerMedicalCheckUpAttachmentList() {
		if (customerMedicalCheckUpAttachmentList == null) {
			customerMedicalCheckUpAttachmentList = new ArrayList<Attachment>();
		}
		return customerMedicalCheckUpAttachmentList;
	}

	public void setCustomerMedicalCheckUpAttachmentList(List<Attachment> customerMedicalCheckUpAttachmentList) {
		this.customerMedicalCheckUpAttachmentList = customerMedicalCheckUpAttachmentList;
	}

	public List<SurveyQuestionAnswer> getCustomerSurveyQuestionAnswerList() {
		return customerSurveyQuestionAnswerList;
	}

	public void setCustomerSurveyQuestionAnswerList(List<SurveyQuestionAnswer> customerSurveyQuestionAnswerList) {
		this.customerSurveyQuestionAnswerList = customerSurveyQuestionAnswerList;
	}

	public void addCustomerSurveyQuestionAnswerList(SurveyQuestionAnswer surveyQuestion) {
		getCustomerSurveyQuestionAnswerList().add(surveyQuestion);
	}

	public void addAttachment(LifeProposalAttachment attachment) {
		if (attachmentList == null) {
			attachmentList = new ArrayList<LifeProposalAttachment>();
		}
		attachment.setLifeProposal(this);
		attachmentList.add(attachment);
	}

	public void addCustomerMedicalChkUpAttachment(Attachment attachment) {
		customerMedicalCheckUpAttachmentList.add(attachment);
	}

	public void addInsuredPerson(ProposalInsuredPerson proposalInsuredPerson) {
		if (this.proposalInsuredPersonList == null) {
			proposalInsuredPersonList = new ArrayList<ProposalInsuredPerson>();
		}
		proposalInsuredPerson.setLifeProposal(this);
		this.proposalInsuredPersonList.add(proposalInsuredPerson);
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public void setProposalInsuredPersonList(List<ProposalInsuredPerson> proposalInsuredPersonList) {
		this.proposalInsuredPersonList = proposalInsuredPersonList;
	}

	/* System Auto Calculate Process */
	public double getPremium() {
		double premium = 0.0;
		for (ProposalInsuredPerson pv : proposalInsuredPersonList) {
			if (pv.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				premium = Utils.getTwoDecimalPoint(premium + pv.getProposedPremium());
			}
		}
		return premium;
	}

	public double getApprovedPremium() {
		double premium = 0.0;
		for (ProposalInsuredPerson pv : proposalInsuredPersonList) {
			if (pv.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				if (pv.isApproved()) {
					premium = Utils.getTwoDecimalPoint(premium + pv.getProposedPremium());
				}
			}
		}
		return premium;
	}

	public double getAddOnPremium() {
		double premium = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				premium = Utils.getTwoDecimalPoint(premium + pi.getAddOnPremium());
			}
		}
		return premium;
	}

	public double getApprovedAddOnPremium() {
		double premium = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				if (pi.isApproved()) {
					premium = Utils.getTwoDecimalPoint(premium + pi.getAddOnPremium());
				}
			}
		}
		return premium;
	}

	public double getTotalBasicTermPremium() {
		double termPermium = 0.0;
		for (ProposalInsuredPerson pv : proposalInsuredPersonList) {
			if (pv.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				termPermium = Utils.getTwoDecimalPoint(termPermium + pv.getBasicTermPremium());
			}
		}
		return termPermium;
	}

	public double getTotalAddOnTermPremium() {
		double termPermium = 0.0;
		for (ProposalInsuredPerson pv : proposalInsuredPersonList) {
			if (pv.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				termPermium = Utils.getTwoDecimalPoint(termPermium + pv.getAddOnTermPremium());
			}
		}
		return termPermium;
	}

	public double getStampFees() {
		double stampFees = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			stampFees = stampFees + pi.getProposedSumInsured() * 0.003;
		}
		return stampFees;
	}

	public double getSumInsured() {
		double sumInsured = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				sumInsured = sumInsured + pi.getProposedSumInsured();
			}
		}
		return sumInsured;
	}

	public double getApprovedSumInsured() {
		double sumInsured = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				if (pi.isApproved()) {
					sumInsured = sumInsured + pi.getProposedSumInsured();
				}
			}
		}
		return sumInsured;
	}

	public double getTotalAmount() {
		return Utils.getTwoDecimalPoint(getTotalBasicTermPremium() + getTotalAddOnTermPremium() + getStampFees());
	}

	public double getAddOnSumInsured() {
		double sumInsured = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				sumInsured = sumInsured + pi.getAddOnSumInsured();
			}
		}
		return sumInsured;
	}

	public double getApprovedAddOnSumInsured() {
		double sumInsured = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				if (pi.isApproved()) {
					sumInsured = sumInsured + pi.getAddOnSumInsured();
				}
			}
		}
		return sumInsured;
	}

	public double getTotalPremium() {
		return Utils.getTwoDecimalPoint(getPremium() + getAddOnPremium());
	}

	public double getApprovedTotalPremium() {
		return Utils.getTwoDecimalPoint(getApprovedPremium() + getApprovedAddOnPremium());
	}

	public double getTotalSumInsured() {
		return getSumInsured() + getAddOnSumInsured();
	}

	public double getApprovedTotalSumInsured() {
		return getApprovedSumInsured() + getApprovedAddOnSumInsured();
	}

	public double getTotalTermPremium() {
		return Utils.getTwoDecimalPoint(getTotalBasicTermPremium() + getTotalAddOnTermPremium());
	}

	public double getPATotoalPremium() {
		double result = 0.0;
		if (!proposalInsuredPersonList.isEmpty()) {
			result = proposalInsuredPersonList.get(0).getProposedPremium() + getPAAddonPremium();
		}
		return result;
	}

	public double getPAAddonPremium() {
		double result = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (!pi.getInsuredPersonAddOnList().isEmpty()) {
				result = pi.getInsuredPersonAddOnList().get(0).getProposedPremium();
			}
		}
		return result;
	}

	public String getCustomerName() {
		if (customer != null) {
			return customer.getFullName();
		}
		if (organization != null) {
			return organization.getName();
		}
		return null;
	}

	public String getPhoneNo() {
		if (customer != null) {
			return customer.getContentInfo().getPhone();
		}
		if (organization != null) {
			return organization.getContentInfo().getPhone();
		}
		return null;
	}

	public String getCustomerId() {
		if (customer != null) {
			return customer.getId();
		}
		if (organization != null) {
			return organization.getId();
		}
		return null;
	}

	public String getCustomerAddress() {
		if (customer != null) {
			return customer.getFullAddress();
		}
		if (organization != null) {
			return organization.getFullAddress();
		}
		return null;
	}

	public String getSalePersonName() {
		if (agent != null) {
			return agent.getFullName();
		} else if (saleMan != null) {
			return saleMan.getFullName();
		} else if (referral != null) {
			return referral.getFullName();
		}
		return null;
	}

	public void setSalePersonNameNull() {
		if (agent != null) {
			agent = null;
		} else if (saleMan != null) {
			saleMan = null;
		} else if (referral != null) {
			referral = null;
		}
	}

	public double getEndorsementNetPremium() {
		double amount = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.isApproved()) {
				amount = Utils.getTwoDecimalPoint(amount + pi.getEndorsementNetBasicPremium());
			}
		}
		return amount;
	}

	public double getEndorsementAddOnPremium() {
		double amount = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.isApproved()) {
				amount = Utils.getTwoDecimalPoint(amount + pi.getEndorsementNetAddonPremium());
			}
		}
		return amount;
	}

	public double getTotalEndorsementNetPremium() {
		return Utils.getTwoDecimalPoint(getEndorsementNetPremium() + getEndorsementAddOnPremium());
	}

	public double getTotalInterest() {
		double interest = 0.0;
		for (ProposalInsuredPerson pi : proposalInsuredPersonList) {
			if (pi.isApproved()) {
				interest = interest + pi.getInterest();
			}
		}
		return interest;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
	}

	public ProposalStatus getProposalStatus() {
		return proposalStatus;
	}

	public void setProposalStatus(ProposalStatus proposalStatus) {
		this.proposalStatus = proposalStatus;
	}

	public String getPortalId() {
		return portalId;
	}

	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}

	public boolean isSkipPaymentTLF() {
		return isSkipPaymentTLF;
	}

	public void setSkipPaymentTLF(boolean isSkipPaymentTLF) {
		this.isSkipPaymentTLF = isSkipPaymentTLF;
	}

	public boolean isMigrate() {
		return isMigrate;
	}

	public void setMigrate(boolean isMigrate) {
		this.isMigrate = isMigrate;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
	}

	public Eips getEips() {
		return eips;
	}

	public void setEips(Eips eips) {
		this.eips = eips;
	}

	public AuditLog getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

	public boolean isStaffPlan() {
		return isStaffPlan;
	}

	public void setStaffPlan(boolean isStaffPlan) {
		this.isStaffPlan = isStaffPlan;
	}

	@Override
	public String getUserType() {
		if (saleMan != null) {
			return UserType.SALEMAN.toString();
		} else if (agent != null) {
			return UserType.AGENT.toString();
		} else {
			return UserType.REFERRAL.toString();
		}
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((attachmentList == null) ? 0 : attachmentList.hashCode());
		result = prime * result + ((bpmsProposalNo == null) ? 0 : bpmsProposalNo.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((customerClsOfHealth == null) ? 0 : customerClsOfHealth.hashCode());
		result = prime * result + ((customerMedicalCheckUpAttachmentList == null) ? 0
				: customerMedicalCheckUpAttachmentList.hashCode());
		result = prime * result
				+ ((customerSurveyQuestionAnswerList == null) ? 0 : customerSurveyQuestionAnswerList.hashCode());
		result = prime * result + ((eips == null) ? 0 : eips.hashCode());
		result = prime * result + ((auditLog == null) ? 0 : auditLog.hashCode());
		result = prime * result + ((groupFarmerProposal == null) ? 0 : groupFarmerProposal.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isMigrate ? 1231 : 1237);
		result = prime * result + (isSkipPaymentTLF ? 1231 : 1237);
		result = prime * result + (isStaffPlan ? 1231 : 1237);
		result = prime * result + ((lifePolicy == null) ? 0 : lifePolicy.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((portalId == null) ? 0 : portalId.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((proposalInsuredPersonList == null) ? 0 : proposalInsuredPersonList.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((proposalStatus == null) ? 0 : proposalStatus.hashCode());
		result = prime * result + ((proposalType == null) ? 0 : proposalType.hashCode());
		result = prime * result + ((referral == null) ? 0 : referral.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		result = prime * result + ((saleMan == null) ? 0 : saleMan.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
		result = prime * result + version;
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
		LifeProposal other = (LifeProposal) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (attachmentList == null) {
			if (other.attachmentList != null)
				return false;
		} else if (!attachmentList.equals(other.attachmentList))
			return false;
		if (bpmsProposalNo == null) {
			if (other.bpmsProposalNo != null)
				return false;
		} else if (!bpmsProposalNo.equals(other.bpmsProposalNo))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (complete != other.complete)
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (customerClsOfHealth != other.customerClsOfHealth)
			return false;
		if (customerMedicalCheckUpAttachmentList == null) {
			if (other.customerMedicalCheckUpAttachmentList != null)
				return false;
		} else if (!customerMedicalCheckUpAttachmentList.equals(other.customerMedicalCheckUpAttachmentList))
			return false;
		if (customerSurveyQuestionAnswerList == null) {
			if (other.customerSurveyQuestionAnswerList != null)
				return false;
		} else if (!customerSurveyQuestionAnswerList.equals(other.customerSurveyQuestionAnswerList))
			return false;
		if (eips == null) {
			if (other.eips != null)
				return false;
		} else if (!eips.equals(other.eips))
			return false;
		if (auditLog == null) {
			if (other.auditLog != null)
				return false;
		} else if (!auditLog.equals(other.auditLog))
			return false;
		if (groupFarmerProposal == null) {
			if (other.groupFarmerProposal != null)
				return false;
		} else if (!groupFarmerProposal.equals(other.groupFarmerProposal))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isMigrate != other.isMigrate)
			return false;
		if (isSkipPaymentTLF != other.isSkipPaymentTLF)
			return false;
		if (isStaffPlan != other.isStaffPlan)
			return false;
		if (lifePolicy == null) {
			if (other.lifePolicy != null)
				return false;
		} else if (!lifePolicy.equals(other.lifePolicy))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (portalId == null) {
			if (other.portalId != null)
				return false;
		} else if (!portalId.equals(other.portalId))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (proposalInsuredPersonList == null) {
			if (other.proposalInsuredPersonList != null)
				return false;
		} else if (!proposalInsuredPersonList.equals(other.proposalInsuredPersonList))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (proposalStatus != other.proposalStatus)
			return false;
		if (proposalType != other.proposalType)
			return false;
		if (referral == null) {
			if (other.referral != null)
				return false;
		} else if (!referral.equals(other.referral))
			return false;
		if (saleChannelType != other.saleChannelType)
			return false;
		if (saleMan == null) {
			if (other.saleMan != null)
				return false;
		} else if (!saleMan.equals(other.saleMan))
			return false;
		if (salePoint == null) {
			if (other.salePoint != null)
				return false;
		} else if (!salePoint.equals(other.salePoint))
			return false;
		if (submittedDate == null) {
			if (other.submittedDate != null)
				return false;
		} else if (!submittedDate.equals(other.submittedDate))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
