package org.ace.insurance.groupfarmer.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.interfaces.IInsuredItem;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.GROUPFARMERPROPOSAL)
@TableGenerator(name = "GROUPFARMERPROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "GROUPFARMERPROPOSAL_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class GroupFarmerProposal implements IPolicy, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GROUPFARMERPROPOSAL_GEN")
	private String id;

	@Transient
	private String prefix;

	private String proposalNo;

	@Temporal(TemporalType.DATE)
	private Date submittedDate;

	@Enumerated(EnumType.STRING)
	private ProposalType proposalType;

	@Temporal(TemporalType.DATE)
	private Date endDate;

	private int noOfInsuredPerson;

	private double totalSI;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SALEMANID", referencedColumnName = "ID")
	private SaleMan saleMan;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REFERRALID", referencedColumnName = "ID")
	private Customer referral;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "groupFarmerProposal", orphanRemoval = true)
	private List<GroupFarmerProposalAttachment> attachmentList;

	private double premium;

	private boolean isPaymentComplete;

	private boolean isProcessComplete;

	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;

	@Transient
	private boolean isShowIssue;

	@OneToOne
	@JoinColumn(name = "SALEPOINTID")
	private SalePoint salePoint;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;
	
	@OneToOne
	@JoinColumn(name = "EIPS_ID", referencedColumnName = "id")
	private Eips eips;

	private boolean isStaffPlan;

	@Version
	private int version;

	public GroupFarmerProposal() {

	}

	public GroupFarmerProposal(GroupFarmerProposalDTO groupFarmerProposal) {
		this.id = groupFarmerProposal.getId();
		this.proposalNo = groupFarmerProposal.getProposalNo();
		this.submittedDate = groupFarmerProposal.getSubmittedDate();
		this.endDate = groupFarmerProposal.getEndDate();
		this.noOfInsuredPerson = groupFarmerProposal.getNoOfInsuredPerson();
		this.branch = groupFarmerProposal.getBranch();
		this.salePoint = groupFarmerProposal.getSalePoint();
		this.totalSI = groupFarmerProposal.getTotalSI();
		this.saleMan = groupFarmerProposal.getSaleMan();
		this.agent = groupFarmerProposal.getAgent();
		this.referral = groupFarmerProposal.getReferral();
		this.salePoint = groupFarmerProposal.getSalePoint();
		this.premium = groupFarmerProposal.getPremium();
		this.paymentType = groupFarmerProposal.getPaymentType();
		this.proposalType = groupFarmerProposal.getProposalType();
		this.organization = groupFarmerProposal.getOrganization();
		this.version = groupFarmerProposal.getVersion();
		this.attachmentList = groupFarmerProposal.getAttachMentList();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getNoOfInsuredPerson() {
		return noOfInsuredPerson;
	}

	public void setNoOfInsuredPerson(int noOfInsuredPerson) {
		this.noOfInsuredPerson = noOfInsuredPerson;
	}

	public double getTotalSI() {
		return totalSI;
	}

	public void setTotalSI(double totalSI) {
		this.totalSI = totalSI;
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

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public List<GroupFarmerProposalAttachment> getAttachmentList() {
		if (this.attachmentList == null) {
			this.attachmentList = new ArrayList<GroupFarmerProposalAttachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<GroupFarmerProposalAttachment> attachmentList) {
		if (attachmentList != null) {
			for (GroupFarmerProposalAttachment attachment : attachmentList) {
				addAttachment(attachment);
			}
		}
		this.attachmentList = attachmentList;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
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

	public boolean isPaymentComplete() {
		return isPaymentComplete;
	}

	public void setPaymentComplete(boolean isPaymentComplete) {
		this.isPaymentComplete = isPaymentComplete;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public boolean isProcessComplete() {
		return isProcessComplete;
	}

	public void setProcessComplete(boolean isProcessComplete) {
		this.isProcessComplete = isProcessComplete;
	}

	public void addAttachment(GroupFarmerProposalAttachment attachment) {
		if (attachmentList == null) {
			attachmentList = new ArrayList<GroupFarmerProposalAttachment>();
		}
		attachment.setGroupFarmerProposal(this);
		attachmentList.add(attachment);
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public boolean isShowIssue() {
		return isShowIssue;
	}

	public void setShowIssue(boolean isShowIssue) {
		this.isShowIssue = isShowIssue;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((attachmentList == null) ? 0 : attachmentList.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPaymentComplete ? 1231 : 1237);
		result = prime * result + (isProcessComplete ? 1231 : 1237);
		result = prime * result + (isShowIssue ? 1231 : 1237);
		result = prime * result + noOfInsuredPerson;
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		long temp;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((proposalType == null) ? 0 : proposalType.hashCode());
		result = prime * result + ((referral == null) ? 0 : referral.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		result = prime * result + ((saleMan == null) ? 0 : saleMan.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		temp = Double.doubleToLongBits(totalSI);
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
		GroupFarmerProposal other = (GroupFarmerProposal) obj;
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
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPaymentComplete != other.isPaymentComplete)
			return false;
		if (isProcessComplete != other.isProcessComplete)
			return false;
		if (isShowIssue != other.isShowIssue)
			return false;
		if (noOfInsuredPerson != other.noOfInsuredPerson)
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
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
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
		if (Double.doubleToLongBits(totalSI) != Double.doubleToLongBits(other.totalSI))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public String getPolicyNo() {
		// TODO Auto-generated method stub
		return proposalNo;
	}

	@Override
	public double getStandardExcess() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalDiscountAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAddOnPremium() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalPremium() {
		// TODO Auto-generated method stub
		return premium;
	}

	@Override
	public double getTotalTermPremium() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalSumInsured() {
		// TODO Auto-generated method stub
		return totalSI;
	}

	@Override
	public boolean isCoinsuranceApplied() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getCommenmanceDate() {
		// TODO Auto-generated method stub
		return submittedDate;
	}

	@Override
	public double getAgentCommission() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRenewalAgentCommission() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Customer getCustomer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCustomerName() {
		// TODO Auto-generated method stub
		return organization.getName();
	}

	@Override
	public String getCustomerAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductGroup getProductGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getApprovedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IInsuredItem> getInsuredItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InsuranceType getInsuranceType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Eips getEips() {
		return eips;
	}

	public void setEips(Eips eips) {
		this.eips = eips;
	}

	public boolean isStaffPlan() {
		return isStaffPlan;
	}

	public void setStaffPlan(boolean isStaffPlan) {
		this.isStaffPlan = isStaffPlan;
	}

}
