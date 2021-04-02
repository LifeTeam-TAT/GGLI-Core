package org.ace.insurance.report.farmer;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.FARMER_MONTHLY)
@ReadOnly
public class FarmerMonthlyView {

	@Id
	private String id;
	private String policyNo;
	private String insuredPersonName;
	private String address;
	private double sumInsured;
	private double premium;
	private double commission;
	private String cashReceiptNoAndPaymentDate;
	private String agentNameAndCode;
	@Temporal(TemporalType.TIMESTAMP)
	private Date activedPolicyStartDate;
	private String branchId;
	@Column(name = "BRANCH_NAME")
	private String branchName;
	private String proposalNo;
	@Column(name = "SALEPOINT_ID")
	private String salePointId;

	@Column(name = "SALEPOINT_NAME")
	private String salePointName;
	@Column(name = "ENTITYS_ID")
	private String entityId;

	public String getEntityId() {
		return entityId;
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

	public FarmerMonthlyView() {

	}

	public String getId() {
		return id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public String getAddress() {
		return address;
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

	public String getCashReceiptNoAndPaymentDate() {
		return cashReceiptNoAndPaymentDate;
	}

	public String getAgentNameAndCode() {
		return agentNameAndCode;
	}

	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activedPolicyStartDate == null) ? 0 : activedPolicyStartDate.hashCode());
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((agentNameAndCode == null) ? 0 : agentNameAndCode.hashCode());
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
		result = prime * result + ((cashReceiptNoAndPaymentDate == null) ? 0 : cashReceiptNoAndPaymentDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(commission);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((insuredPersonName == null) ? 0 : insuredPersonName.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
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
		FarmerMonthlyView other = (FarmerMonthlyView) obj;
		if (activedPolicyStartDate == null) {
			if (other.activedPolicyStartDate != null)
				return false;
		} else if (!activedPolicyStartDate.equals(other.activedPolicyStartDate))
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (agentNameAndCode == null) {
			if (other.agentNameAndCode != null)
				return false;
		} else if (!agentNameAndCode.equals(other.agentNameAndCode))
			return false;
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
		if (cashReceiptNoAndPaymentDate == null) {
			if (other.cashReceiptNoAndPaymentDate != null)
				return false;
		} else if (!cashReceiptNoAndPaymentDate.equals(other.cashReceiptNoAndPaymentDate))
			return false;
		if (Double.doubleToLongBits(commission) != Double.doubleToLongBits(other.commission))
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (insuredPersonName == null) {
			if (other.insuredPersonName != null)
				return false;
		} else if (!insuredPersonName.equals(other.insuredPersonName))
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
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
