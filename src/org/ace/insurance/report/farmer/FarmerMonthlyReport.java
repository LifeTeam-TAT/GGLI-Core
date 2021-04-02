package org.ace.insurance.report.farmer;

import org.ace.insurance.common.ISorter;

public class FarmerMonthlyReport implements ISorter {
	private static final long serialVersionUID = 288430480510991981L;

	private String id;
	private String policyNo;
	private String insuredPersonName;
	private String address;
	private double sumInsured;
	private double premium;
	private double commission;
	private String cashReceiptNoAndPaymentDate;
	private String agentNameAndCode;
	private String proposalNo;
	private String salepoint;
	private String branch;
	private String entity;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getSalepoint() {
		return salepoint;
	}

	public void setSalepoint(String salepoint) {
		this.salepoint = salepoint;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public FarmerMonthlyReport(FarmerMonthlyView view) {
		this.id = view.getId();
		this.policyNo = view.getPolicyNo();
		this.insuredPersonName = view.getInsuredPersonName();
		this.address = view.getAddress();
		this.sumInsured = view.getSumInsured();
		this.premium = view.getPremium();
		this.commission = view.getCommission();
		this.cashReceiptNoAndPaymentDate = view.getCashReceiptNoAndPaymentDate();
		this.agentNameAndCode = view.getAgentNameAndCode();
		this.proposalNo = view.getProposalNo();
		this.salepoint = view.getSalePointName();
		this.branch = view.getBranchName();
		this.entity = view.getEntityId();
	}

	public String getId() {
		return id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public String getAddress() {
		return address;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public double getCommission() {
		return commission;
	}

	public String getCashReceiptNoAndPaymentDate() {
		return cashReceiptNoAndPaymentDate;
	}

	public String getAgentNameAndCode() {
		return agentNameAndCode;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

}
