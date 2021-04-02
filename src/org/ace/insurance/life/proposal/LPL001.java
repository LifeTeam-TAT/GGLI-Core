package org.ace.insurance.life.proposal;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.Name;

public class LPL001 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private String salePersonName;
	private String customerName;
	private String branch;
	private double premium;
	private double sumInsured;
	private String paymentType;
	private Date submittedDate;

	public LPL001(String id, String proposalNo, String saleManSalutation, Name salemanName, String agentSalutation, Name agentName, String referalSalutation, Name referalName,
			String customerSalutation, Name customerName, String organization, String branch, double premium, double sumInsured, String paymentType, Date submittedDate) {
		this.id = id;
		this.proposalNo = proposalNo;
		if (agentSalutation != null)
			this.salePersonName = agentSalutation + " " + agentName.getFullName();
		else if (salemanName != null)
			this.salePersonName = saleManSalutation + " " + salemanName.getFullName();
		else if (referalName != null)
			this.salePersonName = referalSalutation + " " + referalName.getFullName();
		if (customerSalutation != null)
			this.customerName = customerSalutation + " " + customerName.getFullName();
		else
			this.customerName = organization;
		this.branch = branch;
		this.premium = premium;
		this.sumInsured = sumInsured;
		this.paymentType = paymentType;
		this.submittedDate = submittedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getSalePersonName() {
		return salePersonName;
	}

	public void setSalePersonName(String salePersonName) {
		this.salePersonName = salePersonName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}

}
