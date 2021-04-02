package org.ace.insurance.report.life.view;

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
@Table(name = TableName.VWT_LIFEDAILYINCOME)
@ReadOnly
public class LifeDailyIncomeReportView {
	@Id
	private String id;
	@Column(name = "POLICYNO")
	private String policyNo;
	@Column(name = "PRODUCTID")
	private String productId;
	@Column(name = "POLICYTYPE")
	private String policyType;
	@Column(name = "CASHRECEIPTNO")
	private String cashReceiptNo;
	@Column(name = "CUSTOMERNAME")
	private String customerName;

	@Column(name = "ORGANIZATIONNAME")
	private String organizationName;

	@Temporal(TemporalType.DATE)
	@Column(name = "COMMENMANCEDATE")
	private Date commencementDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "PAYMENTDATE")
	private Date paymentDate;

	@Column(name = "AMOUNT")
	private double amount;
	@Column(name = "BRANCHID")
	private String branchId;
	@Column(name = "BRANCHNAME")
	private String branchName;
	@Column(name = "STAMPFEES")
	private double stampFee;
	@Column(name = "PAYMENTTYPENAME")
	private String paymentTypeName;

	@Column(name = "SALEPOINTID")
	private String salePointId;
	@Column(name = "SALEPOINTNAME")
	private String salePointName;
	@Column(name = "SALEPOINTCODE")
	private String salePointCode;
	private String entitysId;

	private String proposalNo;

	public LifeDailyIncomeReportView() {
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getCashReceiptNo() {
		return cashReceiptNo;
	}

	public void setCashReceiptNo(String cashReceiptNo) {
		this.cashReceiptNo = cashReceiptNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getCommencementDate() {
		return commencementDate;
	}

	public void setCommencementDate(Date commencementDate) {
		this.commencementDate = commencementDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public double getStampFee() {
		return stampFee;
	}

	public void setStampFee(double stampFee) {
		this.stampFee = stampFee;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
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

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getEntitysId() {
		return entitysId;
	}
	
	

}
