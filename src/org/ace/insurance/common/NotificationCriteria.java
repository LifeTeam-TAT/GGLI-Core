package org.ace.insurance.common;

import java.io.Serializable;
import java.util.Date;

import org.ace.insurance.system.common.salepoint.SalePoint;

public class NotificationCriteria implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReferenceType referenceType;
	private String reportType;
	private int year;
	private Date startDate;
	private Date endDate;
	private int month;
	private SalePoint salePoint;

	public NotificationCriteria() {
	}

	public NotificationCriteria(String reportType, int year, Date startDate, Date endDate, int month, SalePoint salePoint) {
		this.reportType = reportType;
		this.year = year;
		this.startDate = startDate;
		this.endDate = endDate;
		this.month = month;
		this.salePoint = salePoint;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

}
