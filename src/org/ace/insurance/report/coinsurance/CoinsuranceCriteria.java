package org.ace.insurance.report.coinsurance;

import java.util.Date;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;

public class CoinsuranceCriteria {
	private Date startDate;
	private Date endDate;
	private String policyNo;
	private String coinsuranceCompanyId;
	private CoinsuranceType coinsuranceType;
	private InsuranceType insuranceType;

	public CoinsuranceCriteria() {
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

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCoinsuranceCompanyId() {
		return coinsuranceCompanyId;
	}

	public void setCoinsuranceCompanyId(String coinsuranceCompanyId) {
		this.coinsuranceCompanyId = coinsuranceCompanyId;
	}

	public CoinsuranceType getCoinsuranceType() {
		return coinsuranceType;
	}

	public void setCoinsuranceType(CoinsuranceType coinsuranceType) {
		this.coinsuranceType = coinsuranceType;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

}
