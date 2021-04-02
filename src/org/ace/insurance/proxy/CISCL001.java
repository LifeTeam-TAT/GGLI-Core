package org.ace.insurance.proxy;

import java.util.Date;

import org.ace.insurance.common.ISorter;

public class CISCL001 implements ISorter {
	private String id;
	private String claimNo;
	private String policyNo;
	private String name;
	private Date pendingSince;
	private double totalClaimAmount;

	public CISCL001() {

	}

	public CISCL001(String id, String claimNo, String policyNo, String name, Date pendingSince, double totalClaimAmount) {
		super();
		this.id = id;
		this.claimNo = claimNo;
		this.policyNo = policyNo;
		this.name = name;
		this.pendingSince = pendingSince;
		this.totalClaimAmount = totalClaimAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClaimNo() {
		return claimNo;
	}

	public void setClaimNo(String claimNo) {
		this.claimNo = claimNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPendingSince() {
		return pendingSince;
	}

	public void setPendingSince(Date pendingSince) {
		this.pendingSince = pendingSince;
	}

	public double getTotalClaimAmount() {
		return totalClaimAmount;
	}

	public void setTotalClaimAmount(double totalClaimAmount) {
		this.totalClaimAmount = totalClaimAmount;
	}

	@Override
	public String getRegistrationNo() {
		// TODO Auto-generated method stub
		return claimNo;
	}

}
