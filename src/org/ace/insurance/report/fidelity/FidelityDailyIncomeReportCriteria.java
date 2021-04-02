package org.ace.insurance.report.fidelity;

import java.util.Date;

import org.ace.insurance.system.common.branch.Branch;

public class FidelityDailyIncomeReportCriteria {
	private Date startDate;
	private Date endDate;
	private Branch branch;
	
	public FidelityDailyIncomeReportCriteria() {
	}

	public FidelityDailyIncomeReportCriteria(Date startDate, Date endDate,Branch branch) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.branch = branch;
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
}
