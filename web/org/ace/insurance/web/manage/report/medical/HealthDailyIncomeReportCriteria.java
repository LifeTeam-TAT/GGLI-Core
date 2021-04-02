package org.ace.insurance.web.manage.report.medical;

import java.util.Date;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class HealthDailyIncomeReportCriteria {
	private Date stratDate;
	private Date endDate;
	private Branch branch;
	private SalePoint salePoint;
	private Entitys entity;

	public HealthDailyIncomeReportCriteria(Date stratDate, Date endDate, Branch branch, SalePoint salePoint, Entitys entity) {
		super();
		this.stratDate = stratDate;
		this.endDate = endDate;
		this.branch = branch;
		this.salePoint = salePoint;
		this.entity = entity;
	}

	public HealthDailyIncomeReportCriteria() {
		super();
	}

	public Date getStartDate() {
		return stratDate;
	}

	public void setStartDate(Date strarDate) {
		this.stratDate = strarDate;
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

	public Date getStratDate() {
		return stratDate;
	}

	public void setStratDate(Date stratDate) {
		this.stratDate = stratDate;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((salePoint == null) ? 0 : salePoint.hashCode());
		result = prime * result + ((stratDate == null) ? 0 : stratDate.hashCode());
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
		HealthDailyIncomeReportCriteria other = (HealthDailyIncomeReportCriteria) obj;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (salePoint == null) {
			if (other.salePoint != null)
				return false;
		} else if (!salePoint.equals(other.salePoint))
			return false;
		if (stratDate == null) {
			if (other.stratDate != null)
				return false;
		} else if (!stratDate.equals(other.stratDate))
			return false;
		return true;
	}

}
