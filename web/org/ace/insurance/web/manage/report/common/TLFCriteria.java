package org.ace.insurance.web.manage.report.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class TLFCriteria {
	private SalePoint salePoint;
	private Date startDate;
	private Date endDate;
	private PaymentChannel paymentChannel;
	private List<String> productIdList;
	private Branch branch;
	private Entitys entity;

	public TLFCriteria() {

	}

	public TLFCriteria(SalePoint salePoint, Date startDate, Date endDate, PaymentChannel paymentChannel, Branch branch,Entitys entity) {
		super();
		this.salePoint = salePoint;
		this.startDate = startDate;
		this.endDate = endDate;
		this.paymentChannel = paymentChannel;
		this.branch = branch;
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

	public PaymentChannel getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(PaymentChannel paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public List<String> getProductIdList() {
		if (productIdList == null) {
			return new ArrayList<>();
		} else {
			return productIdList;
		}
	}

	public void setProductIdList(List<String> productIdList) {
		this.productIdList = productIdList;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

}
