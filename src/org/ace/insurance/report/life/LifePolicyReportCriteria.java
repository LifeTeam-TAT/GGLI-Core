package org.ace.insurance.report.life;

import java.util.Date;

import org.ace.insurance.common.ProposalType;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class LifePolicyReportCriteria {
	private Date startDate;
	private Date endDate;
	private Agent agent;
	private Customer customer;
	private Branch branch;
	private Organization organization;
	private ProposalType proposaltype;
	private Product product;
	private SalePoint salePoint;
	private Entitys entity;
	
	public LifePolicyReportCriteria() {

	}

	public LifePolicyReportCriteria(Date startDate, Date endDate, Agent agent, Customer customer, Branch branch, Organization organization, ProposalType proposaltype, Product product,SalePoint salePoint,Entitys entity) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.agent = agent;
		this.customer = customer;
		this.branch = branch;
		this.organization = organization;
		this.proposaltype = proposaltype;
		this.product = product;
		this.salePoint=salePoint;
		this.entity=entity;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public ProposalType getProposaltype() {
		return proposaltype;
	}

	public void setProposaltype(ProposalType proposaltype) {
		this.proposaltype = proposaltype;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

}
