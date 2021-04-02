package org.ace.insurance.proxy;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.interfaces.IDataModel;

public class MED001 implements ISorter, IDataModel  {
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private String customerName;
	private Date submittedDate;
	private Date pendingSince;
	private int unit;

	public MED001() {

	}

	public MED001(String id, String proposalNo, String customerName, Date submittedDate, Date pendingSince, int unit) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.customerName = customerName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.unit = unit;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}

}
