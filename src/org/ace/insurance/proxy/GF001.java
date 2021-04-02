package org.ace.insurance.proxy;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.system.common.organization.Organization;

public class GF001 implements ISorter , IDataModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private Date submittedDate;
	private double sumInsured;
	private Date pendingSince;
	private double premium;
	private String customerName;
	private Organization organization;
	private int totalInsuredPerson;

	public GF001() {
	}

	public GF001(String id, String proposalNo,String customerName, Date submittedDate,int totalInsuredPerson ,double totalSI, double premium, Date pendingSince) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.submittedDate = submittedDate;
		this.sumInsured = totalSI;
		this.pendingSince = pendingSince;
		this.premium = premium;
		this.customerName=customerName;
		this.totalInsuredPerson=totalInsuredPerson;
	}

	@Override
	public String getRegistrationNo() {
		// TODO Auto-generated method stub
		return proposalNo;
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

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public Date getPendingSince() {
		return pendingSince;
	}

	public void setPendingSince(Date pendingSince) {
		this.pendingSince = pendingSince;
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public int getTotalInsuredPerson() {
		return totalInsuredPerson;
	}

	public void setTotalInsuredPerson(int totalInsuredPerson) {
		this.totalInsuredPerson = totalInsuredPerson;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	
	
	

}
