package org.ace.insurance.coinsurance;

import java.util.Date;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;

public class CoinsuranceDTO {
	private String tempid;
	private String policyNo;
	private int policyTerm;
	private Date startDate;
	private double sumInsured;
	private double premium;
	private InsuranceType insuranceType;
	private CoinsuranceType coinsuranceType;
	private CoinsuranceCompany coinsuranceCompany;
	private int coInsuPercentage;
	private String invoiceNo;
	private Date invoiceDate;
	private Date paymentDate;
	private Date fromDate;
	private Date toDate;
	private String name;
	private double sumInsuredMI;
	private double sumInsuredOwn;
	private double premiumOwn;
	private double premiumMI;
	private double agentCommission;
	private double underwritingExpense;
	private String trip;
	private Date portDueDate;
	private double netPremium;
	private String customerName;
	private String customerNrc;
	private String remark;

	public CoinsuranceDTO() {

	}

	public CoinsuranceDTO(String invoiceNo, Date invoiceDate, CoinsuranceCompany company, Date fromDate, Date toDate, Double premium, Double suminsured) {
		this.invoiceNo = invoiceNo;
		this.invoiceDate = invoiceDate;
		this.coinsuranceCompany = company;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.premium = premium;
		this.sumInsured = suminsured;
	}

	public CoinsuranceDTO(String name, String policyNo, String trip, String CustomerName, String Nrc, Date portDueDate, double sumInsured, double sumInsuredOwn,
			double sumInsuedMI, double premium, double premiumOwn, double premiumMI, double agentComm, double underExpense, double netPremium, String remark) {
		this.name = name;
		this.policyNo = policyNo;
		this.trip = trip;
		this.customerName = CustomerName;
		this.portDueDate = portDueDate;
		this.customerNrc = Nrc;
		this.sumInsured = sumInsured;
		this.sumInsuredOwn = sumInsuredOwn;
		this.sumInsuredMI = sumInsuedMI;
		this.premium = premium;
		this.premiumMI = premiumMI;
		this.premiumOwn = premiumOwn;
		this.agentCommission = agentComm;
		this.underwritingExpense = underExpense;
		this.netPremium = netPremium;
		this.remark = remark;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerNrc() {
		return customerNrc;
	}

	public void setCustomerNrc(String customerNrc) {
		this.customerNrc = customerNrc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSumInsuredMI() {
		return sumInsuredMI;
	}

	public void setSumInsuredMI(double sumInsuredMI) {
		this.sumInsuredMI = sumInsuredMI;
	}

	public double getSumInsuredOwn() {
		return sumInsuredOwn;
	}

	public void setSumInsuredOwn(double sumInsuredOwn) {
		this.sumInsuredOwn = sumInsuredOwn;
	}

	public double getPremiumOwn() {
		return premiumOwn;
	}

	public void setPremiumOwn(double premiumOwn) {
		this.premiumOwn = premiumOwn;
	}

	public double getPremiumMI() {
		return premiumMI;
	}

	public void setPremiumMI(double premiumMI) {
		this.premiumMI = premiumMI;
	}

	public double getAgentCommission() {
		return agentCommission;
	}

	public void setAgentCommission(double agentCommission) {
		this.agentCommission = agentCommission;
	}

	public double getUnderwritingExpense() {
		return underwritingExpense;
	}

	public void setUnderwritingExpense(double underwritingExpense) {
		this.underwritingExpense = underwritingExpense;
	}

	public String getTrip() {
		return trip;
	}

	public void setTrip(String trip) {
		this.trip = trip;
	}

	public Date getPortDueDate() {
		return portDueDate;
	}

	public void setPortDueDate(Date portDueDate) {
		this.portDueDate = portDueDate;
	}

	public double getNetPremium() {
		return netPremium;
	}

	public void setNetPremium(double netPremium) {
		this.netPremium = netPremium;
	}

	public String getTempid() {
		return tempid;
	}

	public void setTempid(String tempid) {
		this.tempid = tempid;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public int getPolicyTerm() {
		return policyTerm;
	}

	public void setPolicyTerm(int policyTerm) {
		this.policyTerm = policyTerm;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public CoinsuranceType getCoinsuranceType() {
		return coinsuranceType;
	}

	public void setCoinsuranceType(CoinsuranceType coinsuranceType) {
		this.coinsuranceType = coinsuranceType;
	}

	public CoinsuranceCompany getCoinsuranceCompany() {
		return coinsuranceCompany;
	}

	public void setCoinsuranceCompany(CoinsuranceCompany coinsuranceCompany) {
		this.coinsuranceCompany = coinsuranceCompany;
	}

	public int getCoInsuPercentage() {
		return coInsuPercentage;
	}

	public void setCoInsuPercentage(int coInsuPercentage) {
		this.coInsuPercentage = coInsuPercentage;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
