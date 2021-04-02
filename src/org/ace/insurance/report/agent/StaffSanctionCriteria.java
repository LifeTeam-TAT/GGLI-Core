package org.ace.insurance.report.agent;

import java.util.Date;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.staff.Staff;

public class StaffSanctionCriteria {
	public InsuranceType insuranceType;
	private Date startDate;
	private Date endDate;
	private Branch branch;
	private Staff staff;
	private String sanctionNo;
	private Currency currency;
	private String receiptNo;
	private String bpmsReceiptNo;
	private String policyNo;

	public StaffSanctionCriteria() {

	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public String getSanctionNo() {
		return sanctionNo;
	}

	public void setSanctionNo(String sanctionNo) {
		this.sanctionNo = sanctionNo;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getBpmsReceiptNo() {
		return bpmsReceiptNo;
	}

	public void setBpmsReceiptNo(String bpmsReceiptNo) {
		this.bpmsReceiptNo = bpmsReceiptNo;
	}
}
