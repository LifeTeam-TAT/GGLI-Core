package org.ace.insurance.filter.life;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PolicyStatus;

public class LFP001 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String policyNo;
	private String customer;
	private Date startDate;
	private Date endDate;
	private PolicyStatus policyStatus;

	public LFP001(String id, String policyNo, String salutation, Name customer, String organization, Date startDate, Date endDate, PolicyStatus policyStatus) {
		super();
		this.id = id;
		this.policyNo = policyNo;
		this.customer = salutation != null ? salutation + " " + customer.getFullName() : organization;
		this.startDate = startDate;
		this.endDate = endDate;
		this.policyStatus = policyStatus;
	}

	public String getId() {
		return id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getCustomer() {
		return customer;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

}
