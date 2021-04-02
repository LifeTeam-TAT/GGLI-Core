package org.ace.insurance.report.agent;

import java.util.Date;

import org.ace.insurance.common.AgentStatus;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class AgentCommissionDetailCriteria {
	public Agent agent;
	private String sanctionNo;
	public InsuranceType insuranceType;
	private Date startDate;
	private Date endDate;
	private AgentStatus agentStatus;
	private Currency currency;
	private Entitys entity;
	private Branch branch;
	private SalePoint salePoint;

	public AgentCommissionDetailCriteria() {
	}

	public AgentCommissionDetailCriteria(Agent agent, String sanctionNo, InsuranceType insuranceType, Date startDate, Date endDate, AgentStatus agentStatus, Currency currency,
			Branch branch, SalePoint salePoint) {
		super();
		this.agent = agent;
		this.sanctionNo = sanctionNo;
		this.insuranceType = insuranceType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.agentStatus = agentStatus;
		this.currency = currency;
		this.branch = branch;
		this.salePoint = salePoint;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

	public Branch getBranch() {
		return branch;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
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

	public AgentStatus getAgentStatus() {
		return agentStatus;
	}

	public void setAgentStatus(AgentStatus agentStatus) {
		this.agentStatus = agentStatus;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getSanctionNo() {
		return sanctionNo;
	}

	public void setSanctionNo(String sanctionNo) {
		this.sanctionNo = sanctionNo;
	}

}