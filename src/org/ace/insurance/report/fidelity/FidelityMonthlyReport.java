package org.ace.insurance.report.fidelity;

/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/5
 * 
 * @Updated By PPA
 * @Updated date 2016/06/10
 */
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
@Table(name = TableName.FIDELITYMONTHLYREPORT)
@ReadOnly
public class FidelityMonthlyReport implements ISorter {

	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String customerId;
	private String customerName;
	public String customerAddress;
	private String organizationId;
	private String organizationName;
	public String organizationAddress;
	public double suminsured;
	private boolean isofficial;
	private String paymentId;
	public double rate;
	public double premium;
	private double discountPercentage;
	private double stampFees;
	private double serviceCharges;
	private double basicPremium;
	private double addOnPremium;
	private double totalPremium;
	private double amount;
	public String receiptNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	private String branchId;
	private String branchName;
	private String remark;

	public String getId() {
		return id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public String getOrganizationAddress() {
		return organizationAddress;
	}

	public double getSuminsured() {
		return suminsured;
	}

	public boolean isIsofficial() {
		return isofficial;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public double getRate() {
		return rate;
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

	public String getReceiptNo() {
		return receiptNo;
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

	public String getRemark() {
		return remark;
	}

	@Override
	public String getRegistrationNo() {
		return id;
	}

}
