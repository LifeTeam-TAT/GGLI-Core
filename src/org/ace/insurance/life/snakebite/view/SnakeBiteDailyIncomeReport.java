package org.ace.insurance.life.snakebite.view;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.SNAKEBITEDAILYINCOMEREPORT)
@ReadOnly
public class SnakeBiteDailyIncomeReport implements ISorter {

	private static final long serialVersionUID = 1L;
	@Id
	private String policyId;
	private String snakeBitePolicyNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	private String customerId;
	private String customerName;
	private String customerNrc;
	private String customerAddress;
	private String beneficialId;
	private String beneficialName;
	private String beneficialNrc;
	private String beneficialaddress;
	private double premium;
	private double commission;
	private double netpremium;
	private String branchId;
	@Column(name = "BRANCH_NAME")
	private String branchName;
	@Column(name = "SALEPOINT_ID")
	private String salePointId;
	@Column(name = "SALEPOINT_NAME")
	private String salePointName;
	@Column(name = "ENTITYS_ID")
	private String entityId;

	public String getPolicyId() {
		return policyId;
	}

	public String getSnakeBitePolicyNo() {
		return snakeBitePolicyNo;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerNrc() {
		return customerNrc;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public String getBeneficialId() {
		return beneficialId;
	}

	public String getBeneficialName() {
		return beneficialName;
	}

	public String getBeneficialNrc() {
		return beneficialNrc;
	}

	public String getBeneficialaddress() {
		return beneficialaddress;
	}

	public double getPremium() {
		return premium;
	}

	public double getCommission() {
		return commission;
	}

	public double getNetpremium() {
		return netpremium;
	}

	@Override
	public String getRegistrationNo() {
		return policyId;
	}

	public String getBranchId() {
		return branchId;
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

	public String getEntityId() {
		return entityId;
	}

}
