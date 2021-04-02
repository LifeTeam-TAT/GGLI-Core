package org.ace.insurance.report.agent;

import java.util.Date;

import org.ace.insurance.common.AgentSaleInsuranceType;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class AgentSaleCriteria {
	private AgentSaleInsuranceType insuranceType;
	private Branch branch;
	private Agent agent;
	private Date startDate;
	private Date endDate;

	private SalePoint salePoint;

	private Entitys entity;

	public AgentSaleCriteria() {

	}

	public AgentSaleCriteria(AgentSaleInsuranceType insuranceType, Branch branch, Agent agent, Date startDate, Date endDate, SalePoint salePoint, Entitys entity) {
		super();
		this.insuranceType = insuranceType;
		this.branch = branch;
		this.agent = agent;
		this.startDate = startDate;
		this.endDate = endDate;
		this.salePoint = salePoint;
		this.entity = entity;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
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

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public void setInsuranceType(AgentSaleInsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public AgentSaleInsuranceType getInsuranceType() {
		return insuranceType;
	}

}
