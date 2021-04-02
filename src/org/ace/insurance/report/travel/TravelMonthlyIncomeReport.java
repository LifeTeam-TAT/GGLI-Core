package org.ace.insurance.report.travel;

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
@Table(name = TableName.TRAVELMONTHLYINCOMEREPORT)
@ReadOnly
public class TravelMonthlyIncomeReport implements ISorter {

	private static final long serialVersionUID = 1L;

	@Id
	private String travelProposalId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;
	private int totalUnit;
	private double totalPremium;
	private double totalCommission;
	private double totalNetPremium;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	private String branchId;
	private String branchName;
	private String expressName;
	private String salePointName;
	private String entityId;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getTravelProposalId() {
		return travelProposalId;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public int getTotalUnit() {
		return totalUnit;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public double getTotalCommission() {
		return totalCommission;
	}

	public double getTotalNetPremium() {
		return totalNetPremium;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public String getExpressName() {
		return expressName;
	}

	public String getSalePointName() {
		return salePointName;
	}

	@Override
	public String getRegistrationNo() {
		return travelProposalId;
	}

}
