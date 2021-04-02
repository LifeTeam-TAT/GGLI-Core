package org.ace.insurance.report.life;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.report.life.view.LifeDailyIncomeReportView;

public class LifeDailyIncomeReport implements ISorter {
	private String customerName;
	private String policyNo;
	private String policyType;
	private String cashReceiptNo;
	private double stampFee;
	private Date commencementDate;
	private Date paymentDate;
	private String paymentTypeName;
	private String branchName;
	private double subTotal;
	private double amount;
	public String salePointName;
	public String salePointCode;
	public String proposalNo;

	public LifeDailyIncomeReport() {
	}

	public LifeDailyIncomeReport(LifeDailyIncomeReportView view) {
		if (view.getCustomerName() == null) {
			this.customerName = view.getOrganizationName();
		} else {
			this.customerName = view.getCustomerName();
		}
		this.policyNo = view.getPolicyNo();
		this.policyType = view.getPolicyType();
		this.cashReceiptNo = view.getCashReceiptNo();
		this.stampFee = view.getStampFee();
		this.commencementDate = view.getCommencementDate();
		this.paymentTypeName = view.getPaymentTypeName();
		this.branchName = view.getBranchName();
		this.amount = view.getAmount();
		this.paymentDate = view.getPaymentDate();
		this.salePointName = view.getSalePointName();
		this.salePointCode = view.getSalePointCode();
		this.proposalNo = view.getProposalNo();
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getCashReceiptNo() {
		return cashReceiptNo;
	}

	public void setCashReceiptNo(String cashReceiptNo) {
		this.cashReceiptNo = cashReceiptNo;
	}

	public double getStampFee() {
		return stampFee;
	}

	public void setStampFee(double stampFee) {
		this.stampFee = stampFee;
	}

	public Date getCommencementDate() {
		return commencementDate;
	}

	public void setCommencementDate(Date commencementDate) {
		this.commencementDate = commencementDate;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getSalePointCode() {
		return salePointCode;
	}

	public void setSalePointCode(String salePointCode) {
		this.salePointCode = salePointCode;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

}
