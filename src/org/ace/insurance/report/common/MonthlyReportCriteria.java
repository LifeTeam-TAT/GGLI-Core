package org.ace.insurance.report.common;

import org.ace.insurance.common.MedicalProductType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class MonthlyReportCriteria {
	private int year;
	private int month;
	private Branch branch;
	private ProposalType proposalType;
	private Product product;
	private MedicalProductType medicalProductType;

	private SalePoint salePoint;

	private Entitys entity;

	public MonthlyReportCriteria() {

	}

	public MonthlyReportCriteria(int year, int month, Branch branch, ProposalType proposalType, Product product, MedicalProductType medicalProductType, SalePoint salePoint,Entitys entity) {
		super();
		this.year = year;
		this.month = month;
		this.branch = branch;
		this.proposalType = proposalType;
		this.product = product;
		this.medicalProductType = medicalProductType;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public MedicalProductType getMedicalProductType() {
		return medicalProductType;
	}

	public void setMedicalProductType(MedicalProductType medicalProductType) {
		this.medicalProductType = medicalProductType;
	}

}
