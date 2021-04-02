package org.ace.insurance.cashreceipt;

import java.util.Date;

import org.ace.insurance.common.WorkflowReferenceType;

public class CashReceiptCriteria {
	private Date startDate;
	private Date endDate;
	private WorkflowReferenceType referenceType;

	public CashReceiptCriteria() {
	}

	public CashReceiptCriteria(Date startDate, Date endDate, WorkflowReferenceType referenceType) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.referenceType = referenceType;
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

	public WorkflowReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(WorkflowReferenceType referenceType) {
		this.referenceType = referenceType;
	}

}
