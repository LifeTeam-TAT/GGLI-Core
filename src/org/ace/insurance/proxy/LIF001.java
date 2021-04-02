package org.ace.insurance.proxy;

import java.io.Serializable;
import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.interfaces.IDataModel;

public class LIF001 implements Serializable, ISorter, IDataModel {
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private String customerName;
	private Date submittedDate;
	private Date pendingSince;
	private double sumInsured;

	public LIF001() {
	}

	public LIF001(String id, String proposalNo, String intialId, Name cusName, String orgName, Date submittedDate, Date pendingSince, double sumInsured) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.customerName = cusName != null ? intialId + " " + cusName.getFullName() : orgName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.sumInsured = sumInsured;
	
	}

	public String getId() {
		return id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public Date getPendingSince() {
		return pendingSince;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}
}
