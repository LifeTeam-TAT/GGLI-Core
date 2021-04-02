package org.ace.insurance.travel.personTravel.proposal;

import java.util.Date;

import org.ace.insurance.common.ISorter;

public class PTPL001 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private String salePersonName;
	private String customerName;
	private String branch;
	private double premium;
	private int totalUnit;
	private int noOfPassenger;
	private String paymentType;
	private Date submittedDate;

	public PTPL001() {
		super();
	}

	public PTPL001(String id, String proposalNo, String salePersonName, String customerName, double premium) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.salePersonName = salePersonName;
		this.customerName = customerName;
		this.premium = premium;
	}

	public PTPL001(String id, String proposalNo, String salePersonName, String customerName, String branch, double premium, int totalUnit, int noOfPassenger, String paymentType,
			Date submittedDate) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.salePersonName = salePersonName;
		this.customerName = customerName;
		this.branch = branch;
		this.premium = premium;
		this.totalUnit = totalUnit;
		this.noOfPassenger = noOfPassenger;
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

	public double getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(int totalUnit) {
		this.totalUnit = totalUnit;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
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
