package org.ace.insurance.life.proposalhistory;

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
import org.ace.insurance.common.ProposalHistoryEntryType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.mobile.enums.ProposalStatus;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.LIFEPROPOSAL_HISTORY)
@TableGenerator(name = "LIFEPROPOSAL_HISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPROPOSAL_HISTORY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "LifeProposalHistory.findAll", query = "SELECT m FROM LifeProposalHistory m "),
		@NamedQuery(name = "LifeProposalHistory.findByDate", query = "SELECT m FROM LifeProposalHistory m WHERE m.submittedDate BETWEEN :startDate AND :endDate"),
		@NamedQuery(name = "LifeProposalHistory.updateCompleteStatus", query = "UPDATE LifeProposalHistory m SET m.complete = :complete WHERE m.id = :id") })
@Access(value = AccessType.FIELD)
public class LifeProposalHistory implements Serializable, IDataModel {
	private static final long serialVersionUID = 7564214263861012292L;

	private boolean complete;

	@Transient
	private String id;
	@Transient
	private String prefix;

	private String proposalNo;
	private String portalId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;

	@Enumerated(EnumType.STRING)
	private ProposalHistoryEntryType entryType;

	@Enumerated(EnumType.STRING)
	private WorkflowTask workflowTask;

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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIFEPROPOSALID", referencedColumnName = "ID")
	private LifeProposal lifeProposal;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lifeProposalHistory", orphanRemoval = true)
	private List<LifeProposalInsuredPersonHistory> lifeProposalInsuredPersonHistoryList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lifeProposalHistory", orphanRemoval = true)
	private List<LifeProposalAttachmentHistory> lifeProposalAttachmentHistoryList;

	@Enumerated(EnumType.STRING)
	private ProposalType proposalType;

	@Enumerated(EnumType.STRING)
	private ProposalStatus proposalStatus;

	@Enumerated(value = EnumType.STRING)
	private ClassificationOfHealth customerClsOfHealth;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
	private List<Attachment> customerMedicalCheckUpAttachmentList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "LIFEPROPOSALID", referencedColumnName = "ID")
	private List<SurveyQuestionAnswer> customerSurveyQuestionAnswerList;

	@Version
	private int version;

	public LifeProposalHistory() {
	}

	public LifeProposalHistory(LifeProposal lifeProposal) {
		this.agent = lifeProposal.getAgent();
		this.branch = lifeProposal.getBranch();
		this.customer = lifeProposal.getCustomer();
		this.saleMan = lifeProposal.getSaleMan();
		this.paymentType = lifeProposal.getPaymentType();
		this.organization = lifeProposal.getOrganization();
		this.lifeProposal = lifeProposal;
		this.submittedDate = lifeProposal.getSubmittedDate();
		this.proposalNo = lifeProposal.getProposalNo();
		this.proposalType = lifeProposal.getProposalType();
		this.proposalStatus = lifeProposal.getProposalStatus();
		this.customerClsOfHealth = lifeProposal.getCustomerClsOfHealth();

		for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			addLifeProposalInsuredPersonHistory(new LifeProposalInsuredPersonHistory(insuredPerson));
		}
		for (LifeProposalAttachment attachment : lifeProposal.getAttachmentList()) {
			addLifeProposalAttachmentHistory(new LifeProposalAttachmentHistory(attachment));
		}
		for (Attachment attachment : lifeProposal.getCustomerMedicalCheckUpAttachmentList()) {
			getCustomerMedicalCheckUpAttachmentList().add(new Attachment(attachment));
		}
		for (SurveyQuestionAnswer surveyQuestionAnswer : lifeProposal.getCustomerSurveyQuestionAnswerList()) {
			getCustomerSurveyQuestionAnswerList().add(new SurveyQuestionAnswer(surveyQuestionAnswer));
		}
	}

	/******************************************************
	 * getter / setter
	 **********************************************************/

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPROPOSAL_HISTORY_GEN")
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

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public ProposalHistoryEntryType getEntryType() {
		return entryType;
	}

	public WorkflowTask getWorkflowTask() {
		return workflowTask;
	}

	public void setWorkflowTask(WorkflowTask workflowTask) {
		this.workflowTask = workflowTask;
	}

	public void setEntryType(ProposalHistoryEntryType entryType) {
		this.entryType = entryType;
	}

	public String getPortalId() {
		return portalId;
	}

	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}

	public void setLifeProposalInsuredPersonHistoryList(List<LifeProposalInsuredPersonHistory> lifeProposalInsuredPersonHistoryList) {
		this.lifeProposalInsuredPersonHistoryList = lifeProposalInsuredPersonHistoryList;
	}

	public List<LifeProposalInsuredPersonHistory> getLifeProposalInsuredPersonHistoryList() {
		if (this.lifeProposalInsuredPersonHistoryList == null) {
			this.lifeProposalInsuredPersonHistoryList = new ArrayList<LifeProposalInsuredPersonHistory>();
		}
		return this.lifeProposalInsuredPersonHistoryList;
	}

	public List<LifeProposalAttachmentHistory> getLifeProposalAttachmentHistoryList() {
		if (lifeProposalAttachmentHistoryList == null) {
			lifeProposalAttachmentHistoryList = new ArrayList<LifeProposalAttachmentHistory>();
		}
		return lifeProposalAttachmentHistoryList;
	}

	public void setLifeProposalAttachmentHistoryList(List<LifeProposalAttachmentHistory> lifeProposalAttachmentHistoryList) {
		this.lifeProposalAttachmentHistoryList = lifeProposalAttachmentHistoryList;
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
		if (customerSurveyQuestionAnswerList == null) {
			customerSurveyQuestionAnswerList = new ArrayList<SurveyQuestionAnswer>();
		}
		return customerSurveyQuestionAnswerList;
	}

	public void setCustomerSurveyQuestionAnswerList(List<SurveyQuestionAnswer> customerSurveyQuestionAnswerList) {
		this.customerSurveyQuestionAnswerList = customerSurveyQuestionAnswerList;
	}

	public void addLifeProposalAttachmentHistory(LifeProposalAttachmentHistory lifeProposalAttachmentHistory) {
		lifeProposalAttachmentHistory.setLifeProposalHistory(this);
		getLifeProposalAttachmentHistoryList().add(lifeProposalAttachmentHistory);
	}

	public void addLifeProposalInsuredPersonHistory(LifeProposalInsuredPersonHistory lifeProposalInsuredPersonHistory) {
		lifeProposalInsuredPersonHistory.setLifeProposalHistory(this);
		getLifeProposalInsuredPersonHistoryList().add(lifeProposalInsuredPersonHistory);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + ((entryType == null) ? 0 : entryType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((portalId == null) ? 0 : portalId.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		result = prime * result + version;
		result = prime * result + ((workflowTask == null) ? 0 : workflowTask.hashCode());
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
		LifeProposalHistory other = (LifeProposalHistory) obj;
		if (complete != other.complete)
			return false;
		if (entryType != other.entryType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (submittedDate == null) {
			if (other.submittedDate != null)
				return false;
		} else if (!submittedDate.equals(other.submittedDate))
			return false;
		if (version != other.version)
			return false;
		if (workflowTask != other.workflowTask)
			return false;
		return true;
	}

}