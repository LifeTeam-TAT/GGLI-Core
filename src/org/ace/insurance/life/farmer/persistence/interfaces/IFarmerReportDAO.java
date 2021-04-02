package org.ace.insurance.life.farmer.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.common.MonthlyReportCriteria;
import org.ace.insurance.report.farmer.FarmerDailyReport;
import org.ace.insurance.report.farmer.FarmerMonthlyReport;
import org.ace.insurance.report.farmer.FarmerSummaryReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IFarmerReportDAO {
	public List<FarmerDailyReport> findDailyReport(LifeDailyIncomeReportCriteria criteria) throws DAOException;
	
	public List<FarmerMonthlyReport> findMonthlyReport(MonthlyReportCriteria criteria) throws DAOException;
	
	public List<FarmerSummaryReport> findSummaryReport(LifeDailyIncomeReportCriteria criteria) throws DAOException;
}
