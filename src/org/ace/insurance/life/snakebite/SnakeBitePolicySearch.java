package org.ace.insurance.life.snakebite;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;

public class SnakeBitePolicySearch {
	private String bookNo;
	private String snakeBitePolicyNo;
	private Customer customer;
	private Branch branch;

	public SnakeBitePolicySearch() {
	}

	public SnakeBitePolicySearch(Object object) {
		Object[] objArray = (Object[]) object;
		if (objArray[0] instanceof String) {
			bookNo = (String) objArray[0];
		}
		if (objArray[1] instanceof String) {
			snakeBitePolicyNo = (String) objArray[1];
		}
		if (objArray[2] instanceof Customer) {
			customer = (Customer) objArray[2];
		}
		if (objArray[3] instanceof Branch) {
			branch = (Branch) objArray[3];
		}
	}

	public String getBookNo() {
		return bookNo;
	}

	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}

	public String getSnakeBitePolicyNo() {
		return snakeBitePolicyNo;
	}

	public void setSnakeBitePolicyNo(String snakeBitePolicyNo) {
		this.snakeBitePolicyNo = snakeBitePolicyNo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

}
