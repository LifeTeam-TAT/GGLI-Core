package org.ace.insurance.report.fidelity;

/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/5
 */
import org.ace.insurance.system.common.branch.Branch;

public class FidelityMonthlyReportCriteria {

	private int month;
	private int year;
	private Branch branch;
	private String branchId;
	
	
	public Branch getBranch() {
		return branch;
	}
	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
    public FidelityMonthlyReportCriteria() {
	
	}
	
	public FidelityMonthlyReportCriteria(int month, int year, Branch branch){
		this.setMonth(month);
		this.setYear(year);
		this.branch = branch;
		
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	
}
