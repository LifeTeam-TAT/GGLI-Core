package org.ace.insurance.report.coinsurance;

import java.util.Date;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;

public class CoinsuranceEnquiryCriteria {
	private String policyNo;
	private Date startDate;
	private Date endDate;
	private InsuranceType insuranceType;
	private CoinsuranceType coinsuranceType;
	
	
	public CoinsuranceEnquiryCriteria(){
		
	}

	public CoinsuranceEnquiryCriteria(String policyNo, Date startDate, Date endDate,
			InsuranceType insuranceType, CoinsuranceType coinsuranceType) {
		super();
		this.policyNo = policyNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.insuranceType = insuranceType;
		this.coinsuranceType = coinsuranceType;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
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
}
	