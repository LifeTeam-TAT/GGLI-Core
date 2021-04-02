package org.ace.insurance.web.manage.report.medical;

import org.ace.insurance.common.MonthType;
import org.ace.insurance.system.common.branch.Branch;

public class HealthMonthlyReportCriteria {
	private MonthType month;
	private int year;
	private Branch Branch;

	public HealthMonthlyReportCriteria() {
		super();
	}

	public HealthMonthlyReportCriteria(MonthType month, int year, Branch branch) {
		super();
		this.month = month;
		this.year = year;
		Branch = branch;
	}

	public MonthType getMonth() {
		return month;
	}

	public void setMonth(MonthType month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Branch getBranch() {
		return Branch;
	}

	public void setBranch(Branch branch) {
		Branch = branch;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Branch == null) ? 0 : Branch.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HealthMonthlyReportCriteria other = (HealthMonthlyReportCriteria) obj;
		if (Branch == null) {
			if (other.Branch != null)
				return false;
		} else if (!Branch.equals(other.Branch))
			return false;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
}