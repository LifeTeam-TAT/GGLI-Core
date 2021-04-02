package org.ace.insurance.life.snakebite;

public class SnakeBitePolicyNoCriteria {
	private String bookNo;
	private String policyNo;
	public String criteriaValue;
	public SnakeBitePolicyNoCriteriaItems snakeBitePolicyNoCriteriaItems;

	public SnakeBitePolicyNoCriteria() {
		super();
	}

	public SnakeBitePolicyNoCriteria(String bookNo, String policyNo) {
		super();
		this.bookNo = bookNo;
		this.policyNo = policyNo;
	}

	public String getBookNo() {
		return bookNo;
	}

	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public SnakeBitePolicyNoCriteriaItems getSnakeBitePolicyNoCriteriaItems() {
		return snakeBitePolicyNoCriteriaItems;
	}

	public void setSnakeBitePolicyNoCriteriaItems(SnakeBitePolicyNoCriteriaItems snakeBitePolicyNoCriteriaItems) {
		this.snakeBitePolicyNoCriteriaItems = snakeBitePolicyNoCriteriaItems;
	}
}
