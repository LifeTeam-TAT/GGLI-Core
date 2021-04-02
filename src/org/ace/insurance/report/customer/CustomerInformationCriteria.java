package org.ace.insurance.report.customer;

import java.util.Date;

import org.ace.insurance.common.CustomerCriteriaItems;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class CustomerInformationCriteria {
	private Date startDate;
	private Date endDate;
	private String customer;
	private Integer activePolicy;
	private Integer endActivePolicy;
	private CustomerCriteriaItems searchType;
	private Branch customerBrach;
	private SalePoint customersalepoint;
	private Entitys entity;
	
	
	public CustomerInformationCriteria(){

	}
	
	
	public CustomerInformationCriteria(Date startDate, Date endDate, String customer, String branch, 
			CustomerCriteriaItems searchType, int activePolicy, int endActivePolicy, Branch customerBrach,SalePoint customersalepoint,Entitys entity) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.customer = customer;
		this.activePolicy = activePolicy;
		this.endActivePolicy = endActivePolicy;
		this.searchType = searchType;
		this.customerBrach = customerBrach;
		this.customersalepoint = customersalepoint;
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

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Integer getActivePolicy() {
		if(activePolicy == null) {
			activePolicy = new Integer(0);
		}
		return activePolicy;
	}

	public void setActivePolicy(Integer activePolicy) {
		this.activePolicy = activePolicy;
	}

	public Integer getEndActivePolicy() {
		if(endActivePolicy == null) {
			endActivePolicy = new Integer(0);
		}
		return endActivePolicy;
	}

	public void setEndActivePolicy(Integer endActivePolicy) {
		this.endActivePolicy = endActivePolicy;
	}

	public CustomerCriteriaItems getSearchType() {
		return searchType;
	}

	public void setSearchType(CustomerCriteriaItems searchType) {
		this.searchType = searchType;
	}

	public Branch getCustomerBrach() {
		return customerBrach;
	}

	public void setCustomerBrach(Branch customerBrach) {
		this.customerBrach = customerBrach;
	}

	public SalePoint getCustomersalepoint() {
		return customersalepoint;
	}

	public void setCustomersalepoint(SalePoint customersalepoint) {
		this.customersalepoint = customersalepoint;
	}


	public Entitys getEntity() {
		return entity;
	}


	public void setEntity(Entitys entity) {
		this.entity = entity;
	}	
}
