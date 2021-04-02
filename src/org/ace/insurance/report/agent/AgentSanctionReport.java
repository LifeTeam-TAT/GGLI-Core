package org.ace.insurance.report.agent;

import java.util.Date;

import org.ace.insurance.payment.AgentCommission;

public class AgentSanctionReport {
	
	private String id;
	
	private String cashReceiptNo;
	
	private String policyHolder;
	
	private String policyNo;
	
	private double sumInsured;
	
	private double premium;
	
	private double reinstatementPremium;
	
	private double comissionRate;
	
	private double comission;
	
	private String agentName;
	
	private String agentCode;
	
	private String licenseNo;
	
	private Date startDate;
	
	private Date endDate;
	
	private String typeOfProduct;
	
	private String mobile;
	
	private String address;
	
	private AgentCommission agentCommission;
	
	private String sanctionNo;
	
	public AgentSanctionReport() {
		
	}

	public AgentSanctionReport(String id, String cashReceiptNo, String policyHolder,
			String policyNo, double sumInsured, double premium,
			double reinstatementPremium, double comissionRate,
			double comission, String agentName, String agentCode,
			String licenseNo, Date startDate, Date endDate,
			String typeOfProduct, String mobile, String address, AgentCommission agentCommission,
			String sanctionNo) {
		super();
		this.id = id;
		this.cashReceiptNo = cashReceiptNo;
		this.policyHolder = policyHolder;
		this.policyNo = policyNo;
		this.sumInsured = sumInsured;
		this.premium = premium;
		this.reinstatementPremium = reinstatementPremium;
		this.comissionRate = comissionRate;
		this.comission = comission;
		this.agentName = agentName;
		this.agentCode = agentCode;
		this.licenseNo = licenseNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.typeOfProduct = typeOfProduct;
		this.mobile = mobile;
		this.address = address;
		this.agentCommission = agentCommission;
		this.sanctionNo = sanctionNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCashReceiptNo() {
		return cashReceiptNo;
	}

	public String getPolicyHolder() {
		return policyHolder;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public double getReinstatementPremium() {
		return reinstatementPremium;
	}

	public double getComissionRate() {
		return comissionRate;
	}

	public double getComission() {
		return comission;
	}
	
	public String getAgentName() {
		return agentName;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getTypeOfProduct() {
		return typeOfProduct;
	}

	public void setCashReceiptNo(String cashReceiptNo) {
		this.cashReceiptNo = cashReceiptNo;
	}

	public void setPolicyHolder(String policyHolder) {
		this.policyHolder = policyHolder;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public void setReinstatementPremium(double reinstatementPremium) {
		this.reinstatementPremium = reinstatementPremium;
	}

	public void setComissionRate(float comissionRate) {
		this.comissionRate = comissionRate;
	}

	public void setComission(double comission) {
		this.comission = comission;
	}
	
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setTypeOfProduct(String typeOfProduct) {
		this.typeOfProduct = typeOfProduct;
	}

	public String getMobile() {
		return mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public AgentCommission getAgentCommission() {
		return agentCommission;
	}

	public void setAgentCommission(AgentCommission agentCommission) {
		this.agentCommission = agentCommission;
	}

	public String getSanctionNo() {
		return sanctionNo;
	}

	public void setSanctionNo(String sanctionNo) {
		this.sanctionNo = sanctionNo;
	}
	
}
