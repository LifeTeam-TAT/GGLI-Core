package org.ace.insurance.report.farmer;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@IdClass(FarmerSummaryReportViewId.class)
@Table(name = TableName.FARMER_SUMMARY)
@ReadOnly
public class FarmerSummaryReportView {
	@Id
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	private int numberOfPolicy;
	private double sumInsured;
	private double premium;
	private double commission;
	private String remark;
	@Id
	private String branchId;
	@Column(name = "BRANCH_NAME")
	private String branchName;
	@Column(name = "SALEPOINT_ID")
	private String salePointId;
	@Column(name = "SALEPOINT_NAME")
	private String salePointName;
	@Column(name = "ENTITYS_ID")
	private String entityId;

	public String getEntityId() {
		return entityId;
	}

	public FarmerSummaryReportView() {

	}

	public Date getDate() {
		return date;
	}

	public int getNumberOfPolicy() {
		return numberOfPolicy;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public double getCommission() {
		return commission;
	}

	public String getRemark() {
		return remark;
	}

	public String getBranchName() {
		return branchName;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
		long temp;
		temp = Double.doubleToLongBits(commission);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + numberOfPolicy;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((salePointId == null) ? 0 : salePointId.hashCode());
		result = prime * result + ((salePointName == null) ? 0 : salePointName.hashCode());
		temp = Double.doubleToLongBits(sumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FarmerSummaryReportView other = (FarmerSummaryReportView) obj;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (branchName == null) {
			if (other.branchName != null)
				return false;
		} else if (!branchName.equals(other.branchName))
			return false;
		if (Double.doubleToLongBits(commission) != Double.doubleToLongBits(other.commission))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (numberOfPolicy != other.numberOfPolicy)
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (salePointId == null) {
			if (other.salePointId != null)
				return false;
		} else if (!salePointId.equals(other.salePointId))
			return false;
		if (salePointName == null) {
			if (other.salePointName != null)
				return false;
		} else if (!salePointName.equals(other.salePointName))
			return false;
		if (Double.doubleToLongBits(sumInsured) != Double.doubleToLongBits(other.sumInsured))
			return false;
		return true;
	}

}
