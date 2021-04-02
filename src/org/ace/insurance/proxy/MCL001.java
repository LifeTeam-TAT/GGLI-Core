package org.ace.insurance.proxy;

import java.util.Date;

import org.ace.insurance.common.ISorter;

public class MCL001 implements ISorter {
	private static final long serialVersionUID = 1L;

	private String id;
	private String claimReferenceNo;
	private String policyNo;
	private String customerName;
	private String personCasualtyName;
	private Date submittedDate;
	private Date pendingSince;
	private String cur;
	private double claimAmount;
	private double approvedValue;
	private String partName;

	public MCL001() {

	}

	public MCL001(String id, String claimReferenceNo, String policyNo, String customerName, String personCasualtyName, Date submittedDate, Date pendingSince, double claimAmout,
			double approvedValue, String cur) {
		this.id = id;
		this.policyNo = policyNo;
		this.customerName = customerName;
		this.personCasualtyName = personCasualtyName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.claimAmount = claimAmout;
		this.claimReferenceNo = claimReferenceNo;
		this.approvedValue = approvedValue;
		this.cur = cur;
	}

	public MCL001(String id, String claimReferenceNo, String policyNo, String customerName, String personCasualtyName, Date submittedDate, Date pendingSince, double claimAmout,
			double approvedValue, String cur, String partName) {
		this.id = id;
		this.policyNo = policyNo;
		this.customerName = customerName;
		this.personCasualtyName = personCasualtyName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.claimAmount = claimAmout;
		this.claimReferenceNo = claimReferenceNo;
		this.approvedValue = approvedValue;
		this.cur = cur;
		this.partName = partName;

	}

	public String getCur() {
		return cur;
	}

	public void setCur(String cur) {
		this.cur = cur;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPersonCasualtyName() {
		return personCasualtyName;
	}

	public void setPersonCasualtyName(String personCasualtyName) {
		this.personCasualtyName = personCasualtyName;
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

	public double getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(double claimAmount) {
		this.claimAmount = claimAmount;
	}

	public String getClaimReferenceNo() {
		return claimReferenceNo;
	}

	public void setClaimReferenceNo(String claimReferenceNo) {
		this.claimReferenceNo = claimReferenceNo;
	}

	public double getApprovedValue() {
		return approvedValue;
	}

	public void setApprovedValue(double approvedValue) {
		this.approvedValue = approvedValue;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	@Override
	public String getRegistrationNo() {
		return claimReferenceNo;
	}

}
