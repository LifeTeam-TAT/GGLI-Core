package org.ace.insurance.report.common;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class StudentLifeCriteria {
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	private int year;
	private int startMonthType;
	private int endMonthType;
	private SalePoint salePoint;
	private String paymentType;
	private String channel;
	private boolean isDaily;
	private Branch branch;
	private Entitys entity;

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getStartMonthType() {
		return startMonthType;
	}

	public void setStartMonthType(int startMonthType) {
		this.startMonthType = startMonthType;
	}

	public int getEndMonthType() {
		return endMonthType;
	}

	public void setEndMonthType(int endMonthType) {
		this.endMonthType = endMonthType;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public boolean isDaily() {
		return isDaily;
	}

	public void setDaily(boolean isDaily) {
		this.isDaily = isDaily;
	}

}
