package org.ace.insurance.web.manage.life.billcollection;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.interfaces.IDataModel;

public class PolicyNotificationDTO implements ISorter, IDataModel {
	private static final long serialVersionUID = 1L;
	private String policyNo;
	private String insuredPersonName;
	private String idNo;
	private String paymentType;
	private int paymentTerm;
	private double termPremium;
	private double loanInterest;
	private double renewalInterest;
	private double refund;
	private double totalAmout;
	private Date activedPolicyStartDate;
	private Date activedPolicyEndDate;
	private String salePointName;

	public PolicyNotificationDTO() {
	}

	public PolicyNotificationDTO(String policyNo, String insuredPersonName, String idNo, String paymentType, int paymentTerm, double basicTermPremium, Date activedPolicyStartDate,
			Date activedPolicyEndDate, String salePointName) {
		this.policyNo = policyNo;
		this.insuredPersonName = insuredPersonName;
		this.idNo = idNo;
		this.paymentType = paymentType;
		this.paymentTerm = paymentTerm;
		this.termPremium = basicTermPremium;
		this.totalAmout = basicTermPremium;
		this.activedPolicyStartDate = activedPolicyStartDate;
		this.activedPolicyEndDate = activedPolicyEndDate;
		this.salePointName = salePointName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public void setInsuredPersonName(String insuredPersonName) {
		this.insuredPersonName = insuredPersonName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public double getTermPremium() {
		return termPremium;
	}

	public void setTermPremium(double basicTermPremium) {
		this.termPremium = basicTermPremium;
	}

	public double getLoanInterest() {
		return loanInterest;
	}

	public void setLoanInterest(double loanInterest) {
		this.loanInterest = loanInterest;
	}

	public double getRenewalInterest() {
		return renewalInterest;
	}

	public void setRenewalInterest(double renewalInterest) {
		this.renewalInterest = renewalInterest;
	}

	public double getRefund() {
		return refund;
	}

	public void setRefund(double refund) {
		this.refund = refund;
	}

	public double getTotalAmout() {
		return totalAmout;
	}

	public void setTotalAmout(double totalAmout) {
		this.totalAmout = totalAmout;
	}

	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}

	public void setActivedPolicyStartDate(Date activedPolicyStartDate) {
		this.activedPolicyStartDate = activedPolicyStartDate;
	}

	public Date getActivedPolicyEndDate() {
		return activedPolicyEndDate;
	}

	public void setActivedPolicyEndDate(Date activedPolicyEndDate) {
		this.activedPolicyEndDate = activedPolicyEndDate;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

	@Override
	public String getId() {
		return policyNo;
	}

}
