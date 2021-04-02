package org.ace.insurance.medical.claim;

import java.util.Date;

public class MedicalClaimCriteria {
	private String reporterName;
	private String policyNo;
	private String claimInitialReportNo;
	private Date startDate;
	private Date endDate;
	
	public MedicalClaimCriteria() {
	}

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getClaimInitialReportNo() {
		return claimInitialReportNo;
	}

	public void setClaimInitialReportNo(String claimInitialReportNo) {
		this.claimInitialReportNo = claimInitialReportNo;
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
	
	
}
