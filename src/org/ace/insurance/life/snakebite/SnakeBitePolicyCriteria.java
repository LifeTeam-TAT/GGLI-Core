package org.ace.insurance.life.snakebite;


public class SnakeBitePolicyCriteria {
	private String bookNo;
	private String agent;
	private String saleMan;
	private String refferal;
	
	public SnakeBitePolicyCriteria() {
		super();	
	}
	
	public SnakeBitePolicyCriteria(String bookNo, String agent, String saleMan, String referral ) {
		super();
		this.bookNo = bookNo;
		this.agent = agent;
		this.saleMan = saleMan;
		this.refferal = referral;
	}

	public String getBookNo() {
		return bookNo;
	}

	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(String saleMan) {
		this.saleMan = saleMan;
	}

	public String getRefferal() {
		return refferal;
	}

	public void setRefferal(String refferal) {
		this.refferal = refferal;
	}
}