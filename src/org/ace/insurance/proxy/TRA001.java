package org.ace.insurance.proxy;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IDataModel;

public class TRA001 implements ISorter, IDataModel {
	private static final long serialVersionUID = 1L;

	private String id;
	private String proposalNo;
	private String customerName;
	private Date submittedDate;
	private Date pendingSince;
	private int totalUnit;
	private double totalPremium;

	public TRA001() {
	}

	public TRA001(String id, String proposalNo, String initialId, Name cutomerName, String organizationName, Date submittedDate, int totalUnit, double totalPremium,
			Date pendingSince) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.customerName = initialId != null && !initialId.isEmpty() && cutomerName != null ? Utils.getCompleteName(initialId, cutomerName) : organizationName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.totalUnit = totalUnit;
		this.totalPremium = totalPremium;
	}

	public TRA001(String id, String proposalNo, String customerName, Date submittedDate, Date pendingSince, int totalUnit, double totalPremium) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.customerName = customerName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.totalUnit = totalUnit;
		this.totalPremium = totalPremium;
	}

	public TRA001(String id, String proposalNo, Date submittedDate, Date pendingSince, double totalPremium) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.totalPremium = totalPremium;
	}

	public TRA001(String id, String proposalNo, Date submittedDate, Date pendingSince) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
	}

	public String getId() {
		return id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public Date getPendingSince() {
		return pendingSince;
	}

	/**
	 * @return the totalUnit
	 */
	public int getTotalUnit() {
		return totalUnit;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
	}

	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public void setPendingSince(Date pendingSince) {
		this.pendingSince = pendingSince;
	}

	public void setTotalUnit(int totalUnit) {
		this.totalUnit = totalUnit;
	}

}
