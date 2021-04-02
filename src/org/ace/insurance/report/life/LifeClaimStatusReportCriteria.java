package org.ace.insurance.report.life;

import java.util.Date;

import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class LifeClaimStatusReportCriteria {
	private Date startDate;
	private Date endDate;
	private Branch branch;
	private PolicyStatus policyStatus;

	private SalePoint salePoint;
	private Entitys entity;
	private String salePointId;

	public LifeClaimStatusReportCriteria() {
	}

	public LifeClaimStatusReportCriteria(Date startDate, Date endDate, Branch branch, PolicyStatus policyStatus, SalePoint salePoint) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.branch = branch;
		this.policyStatus = policyStatus;
		this.salePoint = salePoint;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
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

	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
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
