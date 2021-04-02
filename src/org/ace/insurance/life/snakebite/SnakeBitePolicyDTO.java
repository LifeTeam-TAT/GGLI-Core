package org.ace.insurance.life.snakebite;

import java.io.Serializable;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SnakeBitePolicyDTO implements Serializable, ISorter {
	private static final long serialVersionUID = -138773794432771024L;
	private String receiptNo;
	private String bookNo;
	private double totalPremium;
	private double totalSumInsured;
	private String agentName;
	private String branchName;
	private String productName;
	private SaleMan saleMan;
	private Customer referral;
	private Product product;
	private Branch branch;
	private Agent agent;
	private String salePoint;

	public SnakeBitePolicyDTO() {
		super();
	}

	public SnakeBitePolicyDTO(String referenceNo, String bookNo, Double premium, Double sumInsured, String branchName, String agentName, String productName,String salePoint) {
		this.receiptNo = referenceNo;
		this.totalPremium = premium;
		this.totalSumInsured = sumInsured;
		this.bookNo = bookNo;
		this.branchName = branchName;
		this.agentName = agentName;
		this.productName = productName;
		this.salePoint=salePoint;
	}

	public SnakeBitePolicyDTO(String referenceNo, String bookNo, Double premium, Double sumInsured, Branch branch, Agent agent, Customer customer, SaleMan saleMan,
			Product product) {
		this.receiptNo = referenceNo;
		this.totalPremium = premium;
		this.totalSumInsured = sumInsured;
		this.bookNo = bookNo;
		this.branch = branch;
		this.agent = agent;
		this.referral = customer;
		this.saleMan = saleMan;
		this.product = product;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getBookNo() {
		return bookNo;
	}

	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
	}

	public double getTotalSumInsured() {
		return totalSumInsured;
	}

	public void setTotalSumInsured(double totalSumInsured) {
		this.totalSumInsured = totalSumInsured;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Double getAgentCommission() {
		double result = 0.0;
		if (product != null && this.getAgent() != null) {
			result = this.totalPremium * this.product.getFirstCommission() / 100;
		}
		return result;
	}

	public Double getNetPremium() {
		double result = 0.0;
		if (this.getAgent() != null) {
			result = this.totalPremium - getAgentCommission();
		} else if (this.getSaleMan() != null || this.getReferral() != null) {
			result = this.totalPremium;
		}
		return result;
	}

	public Customer getReferral() {
		return referral;
	}

	public void setReferral(Customer referral) {
		this.referral = referral;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	


	public String getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(String salePoint) {
		this.salePoint = salePoint;
	}

	@Override
	public String getRegistrationNo() {
		return receiptNo;
	}

	@Override
	public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(this, object);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
