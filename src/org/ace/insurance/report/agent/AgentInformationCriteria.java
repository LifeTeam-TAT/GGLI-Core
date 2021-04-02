package org.ace.insurance.report.agent;

import java.util.Date;

import org.ace.insurance.common.AgentCriteriaItems;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class AgentInformationCriteria {
	private Date startDate;
	private Date endDate;
	private String agent;
	private ProductGroupType groupType;
	private Organization organization;
	private AgentCriteriaItems searchType;

	private Branch branch;
	private SalePoint salePoint;

	private Entitys entity;

	public AgentInformationCriteria() {

	}

	public AgentInformationCriteria(Date startDate, Date endDate, String agent, ProductGroupType groupType, Organization organization, AgentCriteriaItems searchType, Branch branch,
			SalePoint salePoint) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.agent = agent;
		this.groupType = groupType;
		this.organization = organization;
		this.searchType = searchType;
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

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public ProductGroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(ProductGroupType groupType) {
		this.groupType = groupType;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public AgentCriteriaItems getSearchType() {
		return searchType;
	}

	public void setSearchType(AgentCriteriaItems searchType) {
		this.searchType = searchType;
	}

}
