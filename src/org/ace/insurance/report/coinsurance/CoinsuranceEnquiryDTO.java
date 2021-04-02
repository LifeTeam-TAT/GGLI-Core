package org.ace.insurance.report.coinsurance;

import java.util.Date;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;

public class CoinsuranceEnquiryDTO {
	private String policyNo;
	private String customerName;
	private String agentName;
	private Date startDate;
	private InsuranceType insuranceType;
	private CoinsuranceType coinsuranceType;
	private double sumInsured;
	private double premium;
	private String coId;
	private CoinsuranceCompany coinsuranceCompany;

	public CoinsuranceEnquiryDTO() {

	}

	public CoinsuranceEnquiryDTO(String coId, String policyNo, String customerName, String agentName, Date startDate, InsuranceType insuranceType, CoinsuranceType coinsuranceType,
			double sumInsured, double premium, CoinsuranceCompany coinsuranceCompany) {
		this.coId = coId;
		this.policyNo = policyNo;
		this.customerName = customerName;
		this.agentName = agentName;
		this.startDate = startDate;
		this.insuranceType = insuranceType;
		this.coinsuranceType = coinsuranceType;
		this.sumInsured = sumInsured;
		this.premium = premium;
		this.coinsuranceCompany = coinsuranceCompany;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	public String getCoId() {
		return coId;
	}

	public void setCoId(String coId) {
		this.coId = coId;
	}

	public CoinsuranceCompany getCoinsuranceCompany() {
		return coinsuranceCompany;
	}

	public void setCoinsuranceCompany(CoinsuranceCompany coinsuranceCompany) {
		this.coinsuranceCompany = coinsuranceCompany;
	}

}
