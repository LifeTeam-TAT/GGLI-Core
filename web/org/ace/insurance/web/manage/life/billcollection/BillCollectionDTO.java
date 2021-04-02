package org.ace.insurance.web.manage.life.billcollection;

import java.util.Calendar;
import java.util.Date;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.paymenttype.PaymentType;

public class BillCollectionDTO {
	private String policyId;
	private String policyNo;
	private String insuredName;
	private String idNo;
	private Date startDate;
	private Date endDate;
	private Date paidUpDate;
	private PaymentType paymentType;
	private int paymentTimes;
	private int lastPaymentTerm;
	private double basicTermPremium;
	private double loanInterest;
	private double renewalInterest;
	private double serviceCharges;
	private double refund;
	private Agent agent;
	private double rate;
	private double sumInsured;
	private String customerAddress;
	private double receiptPremium;
	private double premium;
	private String remark;
	private int keyCount;
	private double paidUpAmount;
	private double realPaidUpAmount;
	private double requiredAmount;
	private double receivedPremium;
	private double addOnPremium;
	private double ncbPremium;
	private double discountPercent;
	private PolicyReferenceType referenceType;
	private double extraAmount;

	public BillCollectionDTO() {
	}

	public BillCollectionDTO(String policyId, String policyNo, String insuredName, String idNo, Date startDate, Date endDate, PaymentType paymentType, int paymentTimes,
			int lastPaymentTerm, double basicTermPremium, double addOnPremium, double discountPercent, PolicyReferenceType referenceType, double ncbPremium, double loanInterest,
			double renewalInterest, double serviceCharges, double refund) {
		this.policyId = policyId;
		this.policyNo = policyNo;
		this.insuredName = insuredName;
		this.idNo = idNo;
		this.startDate = startDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, paymentType.getMonth() * paymentTimes);
		// GGI Request
		// if (Utils.isLastDayOfMonth(startDate)) {
		// calendar.add(Calendar.MONTH, 1);
		// calendar.set(Calendar.DAY_OF_MONTH, 1);
		// calendar.add(Calendar.DATE, -1);
		// }
		this.endDate = calendar.getTime();
		this.paymentType = paymentType;
		this.paymentTimes = paymentTimes;
		this.lastPaymentTerm = lastPaymentTerm;
		this.basicTermPremium = basicTermPremium;
		this.addOnPremium = addOnPremium;
		this.discountPercent = discountPercent;
		this.referenceType = referenceType;
		this.ncbPremium = ncbPremium;
		this.loanInterest = loanInterest;
		this.renewalInterest = renewalInterest;
		this.serviceCharges = serviceCharges;
		this.refund = refund;
	}

	public BillCollectionDTO(String policyId, String policyNo, String insuredName, String idNo, Date startDate, Date endDate, PaymentType paymentType, int paymentTimes,
			int lastPaymentTerm, double basicTermPremium, double addOnPremium, double discountPercent, PolicyReferenceType referenceType, double ncbPremium, double loanInterest,
			double renewalInterest, double serviceCharges, double refund, double extraAmount) {
		this.policyId = policyId;
		this.policyNo = policyNo;
		this.insuredName = insuredName;
		this.idNo = idNo;
		this.startDate = startDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, paymentType.getMonth() * paymentTimes);
		this.endDate = calendar.getTime();
		this.paymentType = paymentType;
		this.paymentTimes = paymentTimes;
		this.lastPaymentTerm = lastPaymentTerm;
		this.basicTermPremium = basicTermPremium;
		this.addOnPremium = addOnPremium;
		this.discountPercent = discountPercent;
		this.referenceType = referenceType;
		this.ncbPremium = ncbPremium;
		this.loanInterest = loanInterest;
		this.renewalInterest = renewalInterest;
		this.serviceCharges = serviceCharges;
		this.refund = refund;
		this.extraAmount = extraAmount;
	}

	public BillCollectionDTO(String policyId, String policyNo, String insuredName, String idNo, Date startDate, Date endDate, PaymentType paymentType, int paymentTimes,
			int lastPaymentTerm, double basicTermPremium, double loanInterest, double renewalInterest, double serviceCharges, double paidUpAmount, double realPaidUpAmount,
			double receivedPremium, Date paidUpDate) {
		this.policyId = policyId;
		this.policyNo = policyNo;
		this.insuredName = insuredName;
		this.idNo = idNo;
		this.startDate = endDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.MONTH, paymentType.getMonth() * paymentTimes);
		this.endDate = calendar.getTime();
		this.paymentType = paymentType;
		this.paymentTimes = paymentTimes;
		this.lastPaymentTerm = lastPaymentTerm;
		this.basicTermPremium = basicTermPremium;
		this.loanInterest = loanInterest;
		this.renewalInterest = renewalInterest;
		this.serviceCharges = serviceCharges;
		this.paidUpAmount = paidUpAmount;
		this.realPaidUpAmount = realPaidUpAmount;
		this.receivedPremium = receivedPremium;
		this.paidUpDate = paidUpDate;
	}

	public BillCollectionDTO(String policyNo, String customerName, String customerAddress, Date startDate, Date endDate, double sumInsured, double receivedPremium, double premium,
			String remark, double rate) {
		this.policyNo = policyNo;
		this.insuredName = customerName;
		this.customerAddress = customerAddress;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sumInsured = sumInsured;
		this.premium = premium;
		this.receiptPremium = receivedPremium;
		this.rate = rate;
		this.remark = remark;
	}

	// TODO FIX PSH
	// public BillCollectionDTO(DeclarationPolicyCollection collection, int
	// keyCount) {
	// this.policyNo = collection.getPolicyNo();
	// this.insuredName = collection.getFirePolicy().getCustomerName();
	// this.customerAddress = collection.getFirePolicy().getCustomerAddress();
	// this.startDate = collection.getDateOfCollection();
	// this.sumInsured = collection.getSumInsured();
	// this.premium = collection.getTotalPremium();
	// this.receiptPremium = collection.getReceivedPremium();
	// this.rate = collection.getRate();
	// this.remark = collection.getRemark();
	// this.keyCount = keyCount;
	// }

	public double getPaidUpAmount() {
		return paidUpAmount;
	}

	public void setPaidUpAmount(double paidUpAmount) {
		this.paidUpAmount = paidUpAmount;
	}

	public double getRealPaidUpAmount() {
		return realPaidUpAmount;
	}

	public Date getPaidUpDate() {
		return paidUpDate;
	}

	public void setPaidUpDate(Date paidUpDate) {
		this.paidUpDate = paidUpDate;
	}

	public void setRealPaidUpAmount(double realPaidUpAmount) {
		this.realPaidUpAmount = realPaidUpAmount;
	}

	public double getRequiredAmount() {
		return requiredAmount;
	}

	public void setRequiredAmount(double requiredAmount) {
		this.requiredAmount = requiredAmount;
	}

	public double getReceivedPremium() {
		return receivedPremium;
	}

	public void setReceivedPremium(double receivedPremium) {
		this.receivedPremium = receivedPremium;
	}

	public double getAddOnPremium() {
		return addOnPremium;
	}

	public void setAddOnPremium(double addOnPremium) {
		this.addOnPremium = addOnPremium;
	}

	public double getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(double discountPercent) {
		this.discountPercent = discountPercent;
	}

	public double getNcbPremium() {
		return ncbPremium;
	}

	public void setNcbPremium(double ncbPremium) {
		this.ncbPremium = ncbPremium;
	}

	public int getKeyCount() {
		return keyCount;
	}

	public void setKeyCount(int keyCount) {
		this.keyCount = keyCount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public double getReceiptPremium() {
		return receiptPremium;
	}

	public void setReceiptPremium(double receiptPremium) {
		this.receiptPremium = receiptPremium;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public int getPaymentTimes() {
		return paymentTimes;
	}

	public void setPaymentTimes(int paymentTimes) {
		this.paymentTimes = paymentTimes;
	}

	public int getLastPaymentTerm() {
		return lastPaymentTerm;
	}

	public void setLastPaymentTerm(int lastPaymentTerm) {
		this.lastPaymentTerm = lastPaymentTerm;
	}

	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
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

	public double getServiceCharges() {
		return serviceCharges;
	}

	public void setServiceCharges(double serviceCharges) {
		this.serviceCharges = serviceCharges;
	}

	public double getRefund() {
		return refund;
	}

	public void setRefund(double refund) {
		this.refund = refund;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public PolicyReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(PolicyReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public double getTotalAmount() {
		double totalTermPremium = (getTermPremium() - ncbPremium - getDiscountPremium()) * paymentTimes;
		double interestAndServiceCharges = serviceCharges + loanInterest + renewalInterest;
		return ((totalTermPremium + interestAndServiceCharges) - refund) - extraAmount;
		// return ((getTermPremium() - ncbPremium - getDiscountPremium()) *
		// paymentTimes) + (serviceCharges + loanInterest + renewalInterest) -
		// refund - extraAmount;
	}

	public double getTermPremium() {
		return basicTermPremium + addOnPremium;
	}

	public double getDiscountPremium() {
		return getTermPremium() * discountPercent / 100;
	}

	public double getTotalPaidUpAmount() {
		return realPaidUpAmount - serviceCharges;
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

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	/**
	 * @return the extraAmount
	 */
	public double getExtraAmount() {
		return extraAmount;
	}

	/**
	 * @param extraAmount
	 *            the extraAmount to set
	 */
	public void setExtraAmount(double extraAmount) {
		this.extraAmount = extraAmount;
	}

}
