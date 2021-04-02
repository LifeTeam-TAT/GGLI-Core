package org.ace.insurance.report.medical.view;

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
@Table(name = TableName.HEALTHDAILYREPORT_VIEW)
@ReadOnly
public class HealthDailyReportView {
	@Id
	private String id;

	@Temporal(TemporalType.DATE)
	private Date paymentDate;

	private String proposalNo;
	private String policyNo;
	private String insuredName;
	private String beneficiaryName;
	private int basicUnit;
	private int additionalUnit;
	private int option1Unit;
	private int option2Unit;
	private double premium;
	private String premiumMode;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	private String agentName;
	private String branchId;
	private String branch;
	@Column(name = "SALEPOINTID")
	private String salePointId;
	@Column(name = "SALEPOINTNAME")
	private String salePointName;
	@Column(name = "SALEPOINTCODE")
	private String salePointCode;
	private String entityId;

	public HealthDailyReportView() {

	}

	public String getId() {
		return id;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public int getBasicUnit() {
		return basicUnit;
	}

	public int getAdditionalUnit() {
		return additionalUnit;
	}

	public int getOption1Unit() {
		return option1Unit;
	}

	public int getOption2Unit() {
		return option2Unit;
	}

	public double getPremium() {
		return premium;
	}

	public String getPremiumMode() {
		return premiumMode;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getBranch() {
		return branch;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getSalePointCode() {
		return salePointCode;
	}

	public void setSalePointCode(String salePointCode) {
		this.salePointCode = salePointCode;
	}

	public String getEntityId() {
		return entityId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + additionalUnit;
		result = prime * result + ((agentName == null) ? 0 : agentName.hashCode());
		result = prime * result + basicUnit;
		result = prime * result + ((beneficiaryName == null) ? 0 : beneficiaryName.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((insuredName == null) ? 0 : insuredName.hashCode());
		result = prime * result + option1Unit;
		result = prime * result + option2Unit;
		result = prime * result + ((paymentDate == null) ? 0 : paymentDate.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		long temp;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((premiumMode == null) ? 0 : premiumMode.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		HealthDailyReportView other = (HealthDailyReportView) obj;
		if (additionalUnit != other.additionalUnit)
			return false;
		if (agentName == null) {
			if (other.agentName != null)
				return false;
		} else if (!agentName.equals(other.agentName))
			return false;
		if (basicUnit != other.basicUnit)
			return false;
		if (beneficiaryName == null) {
			if (other.beneficiaryName != null)
				return false;
		} else if (!beneficiaryName.equals(other.beneficiaryName))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (insuredName == null) {
			if (other.insuredName != null)
				return false;
		} else if (!insuredName.equals(other.insuredName))
			return false;
		if (option1Unit != other.option1Unit)
			return false;
		if (option2Unit != other.option2Unit)
			return false;
		if (paymentDate == null) {
			if (other.paymentDate != null)
				return false;
		} else if (!paymentDate.equals(other.paymentDate))
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (premiumMode == null) {
			if (other.premiumMode != null)
				return false;
		} else if (!premiumMode.equals(other.premiumMode))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
