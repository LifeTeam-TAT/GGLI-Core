package org.ace.insurance.report.common;

import java.util.Date;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class SalesReportCriteria {
	public Agent agent;
	public PolicyReferenceType referenceType;
	public Date startDate;
	public Date endDate;
	private Product product;

	private Branch branch;
	private SalePoint salePoint;

	private Entitys entity;

	public SalesReportCriteria() {

	}

	public SalesReportCriteria(Agent agent, PolicyReferenceType referenceType, Date startDate, Date endDate, Product product, Branch branch, SalePoint salePoint) {
		super();
		this.agent = agent;
		this.referenceType = referenceType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.product = product;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public PolicyReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(PolicyReferenceType referenceType) {
		this.referenceType = referenceType;
	}

}