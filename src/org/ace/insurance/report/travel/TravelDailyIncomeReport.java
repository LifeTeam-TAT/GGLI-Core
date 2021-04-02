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
@Table(name = TableName.TRAVELDAILYINCOMEREPORT)
@ReadOnly
public class TravelDailyIncomeReport implements ISorter {

	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String proposalId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;
	private String expressId;
	private String expressName;
	private String registrationNo;
	private String occurrenceId;
	private String occurrenceDesc;
	private int noOfPassenger;
	private double premium;
	private double commission;
	private double netPremium;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date toDate;

	private String salePointId;

	private String salePointName;

	private String branchId;

	private String branchName;

	private String entityId;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public String getId() {
		return id;
	}

	public String getProposalId() {
		return proposalId;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public String getExpressId() {
		return expressId;
	}

	public String getExpressName() {
		return expressName;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public String getOccurrenceId() {
		return occurrenceId;
	}

	public String getOccurrenceDesc() {
		return occurrenceDesc;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public double getPremium() {
		return premium;
	}

	public double getCommission() {
		return commission;
	}

	public double getNetPremium() {
		return netPremium;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

}
