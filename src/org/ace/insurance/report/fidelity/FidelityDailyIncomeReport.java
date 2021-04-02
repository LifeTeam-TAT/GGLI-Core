package org.ace.insurance.report.fidelity;

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
@Table(name = TableName.FIDELITYDAILYINCOMEREPORT)
@ReadOnly
public class FidelityDailyIncomeReport implements ISorter {

	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String proposalNo;
	private String policyNo;
	private String customerId;
	private String customerName;
	private String organizationId;
	private String organizationName;
	private String paymentId;
	private String receiptNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date receiptDate;
	private double premium;
	private double discountPercentage;
	private double stampFees;
	private double serviceCharges;
	private double basicPremium;
	private double addOnPremium;
	private double totalPremium;
	private double amount;
	private String branchId;
	private String branchName;

	public String getId() {
		return id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public double getPremium() {
		return premium;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public double getStampFees() {
		return stampFees;
	}

	public double getServiceCharges() {
		return serviceCharges;
	}

	public double getBasicPremium() {
		return basicPremium;
	}

	public double getAddOnPremium() {
		return addOnPremium;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public double getAmount() {
		return amount;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	@Override
	public String getRegistrationNo() {
		return id;
	}
}
