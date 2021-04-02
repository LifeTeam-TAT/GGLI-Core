package org.ace.insurance.report.life;

import java.util.Date;

import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class ShortTermEndowmentLifeReportCriteria {

	private Date startDate;
	private Date endDate;
	private String agentName;
	private String agentId;
	private String branchName;
	private String branchId;
	private String insurePersonName;
	private String policyNo;
	private Branch branch;
	private SalePoint salePoint;
	private Entitys entity;
	private Agent agent;

	public ShortTermEndowmentLifeReportCriteria(){
		
	}
	
	public ShortTermEndowmentLifeReportCriteria(Date startDate, Date endDate, String agentName, String agentId,
			String branchName, String branchId, String insurePersonName, String policyNo, Branch branch,
			SalePoint salePoint, Entitys entity) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.agentName = agentName;
		this.agentId = agentId;
		this.branchName = branchName;
		this.branchId = branchId;
		this.insurePersonName = insurePersonName;
		this.policyNo = policyNo;
		this.branch = branch;
		this.salePoint = salePoint;
		this.entity = entity;
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

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getInsurePersonName() {
		return insurePersonName;
	}

	public void setInsurePersonName(String insurePersonName) {
		this.insurePersonName = insurePersonName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

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

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
}
