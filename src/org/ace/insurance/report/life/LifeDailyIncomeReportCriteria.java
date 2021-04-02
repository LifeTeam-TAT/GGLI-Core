package org.ace.insurance.report.life;

import java.util.Date;

import org.ace.insurance.common.FarmerPolicyType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class LifeDailyIncomeReportCriteria {
	private Date startDate;
	private Date endDate;
	private Branch branch;
	private ProposalType proposalType;
	private SalePoint salePoint;
	private FarmerPolicyType farmerType;
	private Entitys entity;
	private String salePointId;

	public LifeDailyIncomeReportCriteria() {
	}

	public LifeDailyIncomeReportCriteria(Date startDate, Date endDate, Branch branch, ProposalType proposalType) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.branch = branch;
		this.proposalType = proposalType;
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

	public ProposalType getProposalType() {
		return proposalType;
	}

	public void setProposalType(ProposalType proposalType) {
		this.proposalType = proposalType;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public FarmerPolicyType getFarmerType() {
		return farmerType;
	}

	public void setFarmerType(FarmerPolicyType farmerType) {
		this.farmerType = farmerType;
	}

}
