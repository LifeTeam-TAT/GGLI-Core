package org.ace.insurance.report.life.service;

import java.util.Date;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

/**
 * This class serves as the Criteria to manipulate the
 * <code>SnakeBite Policy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/Nov/29
 */

public class SnakeBitePolicyDailyReportCriteria {
	private Date startDate;
	private Date endDate;
	private Branch branch;
	private SalePoint salePoint;
	private Entitys entity;
	private String salePointId;

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public SnakeBitePolicyDailyReportCriteria() {

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

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

}
