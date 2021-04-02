package org.ace.insurance.web.manage.medical.groupMicroHealth.policy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class GroupMicroHealthDTO {
	private String proposalNo;
	private String id;
	private Date startDate;
	private int numberOfInsuredPerson;
	private int numberOfUnit;
	private double totalPremium;
	private Agent agent;
	private SaleMan saleMan;
	private Customer referral;
	private SalePoint salePoint;
	private boolean paymentComplete;
	private boolean processComplete;
	private List<MedicalProposal> medicalProposalList;
	private boolean showIssueButton;
	private int version;
	private Branch branch;

	public GroupMicroHealthDTO(String proposalNo, String id, Date startDate, int numberOfInsuredPerson, int numberOfUnit, double totalPremium, Agent agent, SaleMan saleMan,
			Customer referral, SalePoint salePoint, boolean paymentComplete) {
		super();
		this.proposalNo = proposalNo;
		this.id = id;
		this.startDate = startDate;
		this.numberOfInsuredPerson = numberOfInsuredPerson;
		this.numberOfUnit = numberOfUnit;
		this.totalPremium = totalPremium;
		this.agent = agent;
		this.saleMan = saleMan;
		this.referral = referral;
		this.salePoint = salePoint;
		this.paymentComplete = paymentComplete;
	}

	public GroupMicroHealthDTO(String id, Date startDate, int numberOfInsuredPerson, int numberOfUnit, double totalPremium, Agent agent, SaleMan saleMan, Customer referral,
			SalePoint salePoint, boolean paymentComplete, boolean processComplete) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.numberOfInsuredPerson = numberOfInsuredPerson;
		this.numberOfUnit = numberOfUnit;
		this.totalPremium = totalPremium;
		this.agent = agent;
		this.saleMan = saleMan;
		this.referral = referral;
		this.salePoint = salePoint;
		this.paymentComplete = paymentComplete;
		this.processComplete = processComplete;
	}

	public GroupMicroHealthDTO(GroupMicroHealth groupMicroHealth) {
		this.id = groupMicroHealth.getId();
		this.proposalNo = groupMicroHealth.getProposalNo();
		this.startDate = groupMicroHealth.getStartDate();
		this.numberOfInsuredPerson = groupMicroHealth.getNumberOfInsuredPerson();
		this.numberOfUnit = groupMicroHealth.getNumberOfUnit();
		this.totalPremium = groupMicroHealth.getTotalPremium();
		this.agent = groupMicroHealth.getAgent();
		this.saleMan = groupMicroHealth.getSaleMan();
		this.referral = groupMicroHealth.getReferral();
		this.salePoint = groupMicroHealth.getSalePoint();
		this.paymentComplete = groupMicroHealth.isPaymentComplete();
		this.processComplete = groupMicroHealth.isProcessComplete();
		this.version = groupMicroHealth.getVersion();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getNumberOfInsuredPerson() {
		return numberOfInsuredPerson;
	}

	public void setNumberOfInsuredPerson(int numberOfInsuredPerson) {
		this.numberOfInsuredPerson = numberOfInsuredPerson;
	}

	public int getNumberOfUnit() {
		return numberOfUnit;
	}

	public void setNumberOfUnit(int numberOfUnit) {
		this.numberOfUnit = numberOfUnit;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
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

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public List<MedicalProposal> getMedicalProposalList() {
		if (medicalProposalList == null) {
			return new ArrayList<>();
		}
		return medicalProposalList;
	}

	public void setMedicalProposalList(List<MedicalProposal> medicalProposalList) {
		this.medicalProposalList = medicalProposalList;
	}

	public boolean isShowIssueButton() {
		return showIssueButton;
	}

	public void setShowIssueButton(boolean showIssueButton) {
		this.showIssueButton = showIssueButton;
	}

	public boolean isProcessComplete() {
		return processComplete;
	}

	public void setProcessComplete(boolean processComplete) {
		this.processComplete = processComplete;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public Branch getBranch() {
		return branch;
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

}
