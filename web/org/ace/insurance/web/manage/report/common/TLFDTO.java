package org.ace.insurance.web.manage.report.common;

import java.util.Date;

import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.salepoint.SalePoint;

public class TLFDTO {
	
	private String tlfNo;
	private String acode;
	private String transaction;
	private String narration;
	private SalePoint salePoint;
	private PaymentChannel paymentChannel;
	private double homeAmount;
	private double localAmount;
	private Date startDate;
	
	public TLFDTO(){
		
	}
	

	public TLFDTO(String tlfNo, String acode, double homeAmount, double localAmount, String transaction,
			String narration, SalePoint salePoint, PaymentChannel paymentChannel, Date startDate) {
		super();
		this.tlfNo = tlfNo;
		this.acode = acode;
		this.homeAmount = homeAmount;
		this.localAmount = localAmount;
		this.transaction = transaction;
		this.narration = narration;
		this.salePoint = salePoint;
		this.paymentChannel = paymentChannel;
		this.startDate = startDate;
	}



	public String getTlfNo() {
		return tlfNo;
	}

	public void setTlfNo(String tlfNo) {
		this.tlfNo = tlfNo;
	}

	public String getAcode() {
		return acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

	

	public double getHomeAmount() {
		return homeAmount;
	}


	public void setHomeAmount(double homeAmount) {
		this.homeAmount = homeAmount;
	}


	public double getLocalAmount() {
		return localAmount;
	}


	public void setLocalAmount(double localAmount) {
		this.localAmount = localAmount;
	}


	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public SalePoint getSalePoint() {
		return salePoint;
	}

	public void setSalePoint(SalePoint salePoint) {
		this.salePoint = salePoint;
	}

	public PaymentChannel getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(PaymentChannel paymentChannel) {
		this.paymentChannel = paymentChannel;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}
	
	

}
