package org.ace.insurance.report.coinsurance;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.COINSURANCEREPORT)
@ReadOnly
public class CoinsuranceReport implements ISorter {

	private static final long serialVersionUID = 1L;
	@Id
	public String id;
	public String policyNo;
	@Temporal(TemporalType.TIMESTAMP)
	public Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date endDate;
	public String coinsuranceCompanyId;
	public String coinsuranceCompanyName;
	public String coinsuranceType;
	public String insuranceType;
	public double receivedSumInsured;
	public double sumInsured;
	public double premium;

	public String getId() {
		return id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getCoinsuranceCompanyId() {
		return coinsuranceCompanyId;
	}

	public String getCoinsuranceCompanyName() {
		return coinsuranceCompanyName;
	}

	public String getCoinsuranceType() {
		return coinsuranceType;
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public double getReceivedSumInsured() {
		return receivedSumInsured;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	@Override
	public String getRegistrationNo() {
		return id;
	}

}
