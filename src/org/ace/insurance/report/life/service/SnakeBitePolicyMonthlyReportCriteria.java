package org.ace.insurance.report.life.service;

import java.util.Date;

/**
 * This class serves as the Criteria to manipulate the <code>SnakeBite Policy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/Nov/29
 */

public class SnakeBitePolicyMonthlyReportCriteria {
	private Date startDate;
	private Date endDate;
	
	public SnakeBitePolicyMonthlyReportCriteria(){
		
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


}
