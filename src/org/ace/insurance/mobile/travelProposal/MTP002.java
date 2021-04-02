package org.ace.insurance.mobile.travelProposal;

import java.util.Date;

public class MTP002 {

	private String mobileUserId;
	private String mobileUserName;
	private String policyNo;
	private double premium;
	private Date paymentDate;
	private String travelProposalId;

	public MTP002() {
	}

	public MTP002(String mobileUserId, String mobileUserName, String policyNo, double premium, Date paymentDate, String travelProposalId) {
		super();
		this.mobileUserId = mobileUserId;
		this.mobileUserName = mobileUserName;
		this.policyNo = policyNo;
		this.premium = premium;
		this.paymentDate = paymentDate;
		this.travelProposalId = travelProposalId;
	}

	public String getMobileUserId() {
		return mobileUserId;
	}

	public void setMobileUserId(String mobileUserId) {
		this.mobileUserId = mobileUserId;
	}

	public String getMobileUserName() {
		return mobileUserName;
	}

	public void setMobileUserName(String mobileUserName) {
		this.mobileUserName = mobileUserName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getTravelProposalId() {
		return travelProposalId;
	}

	public void setTravelProposalId(String travelProposalId) {
		this.travelProposalId = travelProposalId;
	}

}
