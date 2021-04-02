package org.ace.insurance.life.policyEditHistory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.ace.insurance.common.PolicyHistoryEntryType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.TableName;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.LIFEPOLICYEDITHISTORY)
@TableGenerator(name = "LIFEPOLICY_EDITHISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPOLICY_EDITHISTORY_GEN", allocationSize = 10)
@NamedQueries(value = {
		@NamedQuery(name = "LifePolicyEditHistory.findAll", query = "SELECT l FROM LifePolicyEditHistory l "),
		@NamedQuery(name = "LifePolicyEditHistory.findAllActiveLifePolicy", query = "SELECT l FROM LifePolicyEditHistory l WHERE l.policyNo IS NOT NULL"),
		@NamedQuery(name = "LifePolicyEditHistory.findByProposalId", query = "SELECT l FROM LifePolicyEditHistory l WHERE l.lifeProposal.id = :lifeProposalId"),
		@NamedQuery(name = "LifePolicyEditHistory.findById", query = "SELECT l FROM LifePolicyEditHistory l WHERE l.id = :id"),
		// @NamedQuery(name = "LifePolicyEditHistory.findForCoinsurance", query
		// =
		// "SELECT l FROM MotorPolicy l WHERE l.commenmanceDate IS NOT NULL AND l.isCoinsuranceApplied = FALSE AND l.totalSumInsured > l.productGroup.maxSumInsured"),
		@NamedQuery(name = "LifePolicyEditHistory.increasePrintCount", query = "UPDATE LifePolicyEditHistory l SET l.printCount = l.printCount + 1 WHERE l.id = :id"),
		@NamedQuery(name = "LifePolicyEditHistory.findByPolicyNo", query = "SELECT l FROM LifePolicyEditHistory l where l.policyNo = :policyNo "),
		@NamedQuery(name = "LifePolicyEditHistory.updateCommenmanceDate", query = "UPDATE LifePolicyEditHistory l SET l.commenmanceDate = :commenceDate WHERE l.id = :id") })
@Access(value = AccessType.FIELD)
public class LifePolicyEditHistory implements Serializable {
	private static final long serialVersionUID = -4188881327890797521L;
	@Transient
	private String id;
	@Transient
	private String prefix;
	@Transient
	private boolean nilExcess;
	private boolean isCoinsuranceApplied;
	private boolean activeEndorsement;
	private int lastPaymentTerm;
	private int printCount;
	@Column(name = "ENTRY_COUNT")
	private int entryCount;
	private double totalDiscountAmount;
	private double standardExcess;
	private String policyNo;
	private String policyReferenceNo;
	private String endorsementNo;

	@Enumerated(EnumType.STRING)
	private PolicyStatus policyStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "ENTRY_TYPE")
	private PolicyHistoryEntryType entryType;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endorsementConfirmDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date commenmanceDate;

	@Column(name = "RENEWALCONFIRMDATE")
	@Temporal(TemporalType.DATE)
	private Date renewalConfirmDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVEDPOLICYSTARTDATE")
	private Date activedPolicyStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVEDPOLICYENDDATE")
	private Date activedPolicyEndDate;

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
	@JoinColumn(name = "APPROVERID", referencedColumnName = "ID")
	private User approvedBy;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALEMANID", referencedColumnName = "ID")
	private SaleMan saleMan;

	@OneToOne
	@JoinColumn(name = "PROPOSALID")
	private LifeProposal lifeProposal;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lifePolicy", orphanRemoval = true)
	private List<PolicyInsuredPersonEditHistory> policyInsuredPersonList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lifePolicy", orphanRemoval = true)
	private List<LifePolicyAttachmentEditHistory> attachmentList;

	@Version
	private int version;

	public LifePolicyEditHistory() {
	}

	public LifePolicyEditHistory(LifePolicy lifePolicy) {
		this.customer = lifePolicy.getCustomer();
		this.organization = lifePolicy.getOrganization();
		this.saleMan = lifePolicy.getSaleMan();
		this.branch = lifePolicy.getBranch();
		this.paymentType = lifePolicy.getPaymentType();
		this.agent = lifePolicy.getAgent();
		this.lifeProposal = lifePolicy.getLifeProposal();
		this.policyNo = lifePolicy.getPolicyNo();
		this.commenmanceDate = lifePolicy.getCommenmanceDate();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPOLICY_EDITHISTORY_GEN")
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

	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Date getCommenmanceDate() {
		return commenmanceDate;
	}

	public void setCommenmanceDate(Date commenmanceDate) {
		this.commenmanceDate = commenmanceDate;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public double getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(double totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public List<PolicyInsuredPersonEditHistory> getInsuredPersonInfo() {
		return policyInsuredPersonList;
	}

	public void setVehicleInfo(List<PolicyInsuredPersonEditHistory> insuredPersonInfo) {
		this.policyInsuredPersonList = insuredPersonInfo;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public List<LifePolicyAttachmentEditHistory> getAttachmentList() {
		if (this.attachmentList == null) {
			this.attachmentList = new ArrayList<LifePolicyAttachmentEditHistory>();
		}
		return this.attachmentList;
	}

	public void setAttachmentList(List<LifePolicyAttachmentEditHistory> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public boolean isNilExcess() {
		return nilExcess;
	}

	public void setNilExcess(boolean nilExcess) {
		this.nilExcess = nilExcess;
	}

	public double getStandardExcess() {
		return standardExcess;
	}

	public void setStandardExcess(double standardExcess) {
		this.standardExcess = standardExcess;
	}

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public List<PolicyInsuredPersonEditHistory> getPolicyInsuredPersonList() {
		if (this.policyInsuredPersonList == null) {
			this.policyInsuredPersonList = new ArrayList<PolicyInsuredPersonEditHistory>();
		}
		return policyInsuredPersonList;
	}

	public void setPolicyInsuredPersonList(List<PolicyInsuredPersonEditHistory> policyInsuredPersonList) {
		this.policyInsuredPersonList = policyInsuredPersonList;
	}

	public void addPolicyInsuredPersonInfo(PolicyInsuredPersonEditHistory policyInsuredPersonInfo) {
		if (policyInsuredPersonList == null) {
			policyInsuredPersonList = new ArrayList<PolicyInsuredPersonEditHistory>();
		}
		policyInsuredPersonInfo.setLifePolicy(this);
		policyInsuredPersonList.add(policyInsuredPersonInfo);
	}

	public void addLifePolicyAttachment(LifePolicyAttachmentEditHistory attachment) {
		if (attachmentList == null) {
			attachmentList = new ArrayList<LifePolicyAttachmentEditHistory>();
		}
		attachment.setLifePolicy(this);
		attachmentList.add(attachment);
	}

	/* System Auto Calculate Process */
	public double getPremium() {
		double premium = 0.0;
		for (PolicyInsuredPersonEditHistory pi : policyInsuredPersonList) {
			premium = premium + pi.getPremium();
		}
		return premium;
	}

	public double getAddOnPremium() {
		double premium = 0.0;
		for (PolicyInsuredPersonEditHistory pi : policyInsuredPersonList) {
			premium = premium + pi.getAddOnPremium();
		}
		return premium;
	}

	public double getSumInsured() {
		double sumInsured = 0.0;
		for (PolicyInsuredPersonEditHistory pi : policyInsuredPersonList) {
			sumInsured = sumInsured + pi.getSumInsured();
		}
		return sumInsured;
	}

	public double getAddOnSumInsured() {
		double sumInsured = 0.0;
		for (PolicyInsuredPersonEditHistory pi : policyInsuredPersonList) {
			sumInsured = sumInsured + pi.getAddOnSumInsure();
		}
		return sumInsured;
	}

	public double getTotalPremium() {
		return getPremium() + getAddOnPremium();
	}

	public double getTotalSumInsured() {
		return getSumInsured() + getAddOnSumInsured();
	}

	public double getTotalTermPremium() {
		return getTotalBasicTermPremium() + getTotalAddOnTermPremium();
	}

	public double getTotalBasicTermPremium() {
		double termPermium = 0.0;
		for (PolicyInsuredPersonEditHistory pi : policyInsuredPersonList) {
			termPermium = termPermium + pi.getBasicTermPremium();
		}
		return termPermium;
	}

	public double getTotalAddOnTermPremium() {
		double termPermium = 0.0;
		for (PolicyInsuredPersonEditHistory pi : policyInsuredPersonList) {
			termPermium = termPermium + pi.getAddOnTermPremium();
		}
		return termPermium;
	}

	public String getCustomerName() {
		if (customer != null) {
			return customer.getFullName();
		}
		if (organization != null) {
			return organization.getOwnerName();
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isCoinsuranceApplied() {
		return isCoinsuranceApplied;
	}

	public void setCoinsuranceApplied(boolean isCoinsuranceApplied) {
		this.isCoinsuranceApplied = isCoinsuranceApplied;
	}

	public ProductGroup getProductGroup() {
		if (this.policyInsuredPersonList != null && !this.policyInsuredPersonList.isEmpty()) {
			return this.policyInsuredPersonList.get(0).getProduct().getProductGroup();
		}
		return null;
	}

	public boolean isActiveEndorsement() {
		return activeEndorsement;
	}

	public void setActiveEndorsement(boolean activeEndorsement) {
		this.activeEndorsement = activeEndorsement;
	}

	public Date getEndorsementConfirmDate() {
		return endorsementConfirmDate;
	}

	public void setEndorsementConfirmDate(Date endorsementConfirmDate) {
		this.endorsementConfirmDate = endorsementConfirmDate;
	}

	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
	}

	public String getPolicyReferenceNo() {
		return policyReferenceNo;
	}

	public void setPolicyReferenceNo(String policyReferenceNo) {
		this.policyReferenceNo = policyReferenceNo;
	}

	public int getEntryCount() {
		return entryCount;
	}

	public void setEntryCount(int entryCount) {
		this.entryCount = entryCount;
	}

	public PolicyHistoryEntryType getEntryType() {
		return entryType;
	}

	public void setEntryType(PolicyHistoryEntryType entryType) {
		this.entryType = entryType;
	}

	public Date getRenewalConfirmDate() {
		return renewalConfirmDate;
	}

	public void setRenewalConfirmDate(Date renewalConfirmDate) {
		this.renewalConfirmDate = renewalConfirmDate;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public int getLastPaymentTerm() {
		return lastPaymentTerm;
	}

	public void setLastPaymentTerm(int lastPaymentTerm) {
		this.lastPaymentTerm = lastPaymentTerm;
	}

	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}

	public void setActivedPolicyStartDate(Date activedPolicyStartDate) {
		this.activedPolicyStartDate = activedPolicyStartDate;
	}

	public Date getActivedPolicyEndDate() {
		return activedPolicyEndDate;
	}

	public void setActivedPolicyEndDate(Date activedPolicyEndDate) {
		this.activedPolicyEndDate = activedPolicyEndDate;
	}

	public String getEndorsementNo() {
		return endorsementNo;
	}

	public void setEndorsementNo(String endorsementNo) {
		this.endorsementNo = endorsementNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (activeEndorsement ? 1231 : 1237);
		result = prime * result + ((activedPolicyEndDate == null) ? 0 : activedPolicyEndDate.hashCode());
		result = prime * result + ((activedPolicyStartDate == null) ? 0 : activedPolicyStartDate.hashCode());
		result = prime * result + ((approvedBy == null) ? 0 : approvedBy.hashCode());
		result = prime * result + ((commenmanceDate == null) ? 0 : commenmanceDate.hashCode());
		result = prime * result + ((endorsementConfirmDate == null) ? 0 : endorsementConfirmDate.hashCode());
		result = prime * result + ((endorsementNo == null) ? 0 : endorsementNo.hashCode());
		result = prime * result + entryCount;
		result = prime * result + ((entryType == null) ? 0 : entryType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isCoinsuranceApplied ? 1231 : 1237);
		result = prime * result + lastPaymentTerm;
		result = prime * result + (nilExcess ? 1231 : 1237);
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((policyReferenceNo == null) ? 0 : policyReferenceNo.hashCode());
		result = prime * result + ((policyStatus == null) ? 0 : policyStatus.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + printCount;
		result = prime * result + ((renewalConfirmDate == null) ? 0 : renewalConfirmDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(standardExcess);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(totalDiscountAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		LifePolicyEditHistory other = (LifePolicyEditHistory) obj;
		if (activeEndorsement != other.activeEndorsement)
			return false;
		if (activedPolicyEndDate == null) {
			if (other.activedPolicyEndDate != null)
				return false;
		} else if (!activedPolicyEndDate.equals(other.activedPolicyEndDate))
			return false;
		if (activedPolicyStartDate == null) {
			if (other.activedPolicyStartDate != null)
				return false;
		} else if (!activedPolicyStartDate.equals(other.activedPolicyStartDate))
			return false;
		if (approvedBy == null) {
			if (other.approvedBy != null)
				return false;
		} else if (!approvedBy.equals(other.approvedBy))
			return false;
		if (commenmanceDate == null) {
			if (other.commenmanceDate != null)
				return false;
		} else if (!commenmanceDate.equals(other.commenmanceDate))
			return false;
		if (endorsementConfirmDate == null) {
			if (other.endorsementConfirmDate != null)
				return false;
		} else if (!endorsementConfirmDate.equals(other.endorsementConfirmDate))
			return false;
		if (endorsementNo == null) {
			if (other.endorsementNo != null)
				return false;
		} else if (!endorsementNo.equals(other.endorsementNo))
			return false;
		if (entryCount != other.entryCount)
			return false;
		if (entryType != other.entryType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isCoinsuranceApplied != other.isCoinsuranceApplied)
			return false;
		if (lastPaymentTerm != other.lastPaymentTerm)
			return false;
		if (nilExcess != other.nilExcess)
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (policyReferenceNo == null) {
			if (other.policyReferenceNo != null)
				return false;
		} else if (!policyReferenceNo.equals(other.policyReferenceNo))
			return false;
		if (policyStatus != other.policyStatus)
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (printCount != other.printCount)
			return false;
		if (renewalConfirmDate == null) {
			if (other.renewalConfirmDate != null)
				return false;
		} else if (!renewalConfirmDate.equals(other.renewalConfirmDate))
			return false;
		if (Double.doubleToLongBits(standardExcess) != Double.doubleToLongBits(other.standardExcess))
			return false;
		if (Double.doubleToLongBits(totalDiscountAmount) != Double.doubleToLongBits(other.totalDiscountAmount))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
