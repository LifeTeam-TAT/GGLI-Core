package org.ace.insurance.life.proposal;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.system.common.paymenttype.PaymentType;

public class LPL002 implements ISorter {
	private String id;
	private String policyNo;
	private String proposalNo;
	private String saleMan;
	private String agent;
	private String customer;
	private String branch;
	private double premium;
	private double sumInsured;
	private String paymentType;
	private Date commenmanceDate;
	private double basicTermPremium;
	private int paymentTimes;
	
	public LPL002(String id, String policyNo, String proposalNo,
			String saleMan, String agent, String customer, String branch,
			double premium, double sumInsured,PaymentType paymenttype,
			Date commenmanceDate,double basicTermPremium,int periodMonth ) {
		this.id = id;
		this.policyNo = policyNo;
		this.proposalNo = proposalNo;
		this.saleMan = saleMan;
		this.agent = agent;
		this.customer = customer;
		this.branch = branch;
		this.premium = premium;
		this.sumInsured = sumInsured;
		this.paymentType = paymenttype.getName();
		this.commenmanceDate = commenmanceDate;
		this.basicTermPremium=basicTermPremium;
		int monthTimes = paymenttype.getMonth();
		if (monthTimes > 0) {
			this.paymentTimes= periodMonth / monthTimes;
		} else {
			this.paymentTimes= 1;
		}
		
	}
	
	public LPL002() {
		
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
	
	public String getProposalNo() {
		return proposalNo;
	}
	
	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}
	
	public String getSaleMan() {
		return saleMan;
	}
	
	public void setSaleMan(String saleMan) {
		this.saleMan = saleMan;
	}
	public String getAgent() {
		return agent;
	}
	
	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getBranch() {
		return branch;
	}
	
	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	public double getPremium() {
		return premium;
	}
	
	public void setPremium(double premium) {
		this.premium = premium;
	}
	public double getSumInsured() {
		return sumInsured;
	}
	
	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}
	public String getPaymentType() {
		return paymentType;
	}
	
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	public Date getCommenmanceDate() {
		return commenmanceDate;
	}
	
	public void setCommenmanceDate(Date commenmanceDate) {
		this.commenmanceDate = commenmanceDate;
	}
	

	public int getPaymentTimes() {
		return paymentTimes;
	}

	public void setPaymentTimes(int paymentTimes) {
		this.paymentTimes = paymentTimes;
	}

	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}
	
	
}
