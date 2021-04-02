package org.ace.insurance.life.farmer.service.interfaces;

import java.util.List;

import org.ace.insurance.report.common.MonthlyReportCriteria;
import org.ace.insurance.report.farmer.FarmerDailyReport;
import org.ace.insurance.report.farmer.FarmerMonthlyReport;
import org.ace.insurance.report.farmer.FarmerSummaryReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;

public interface IFarmerReportService {

	public List<FarmerDailyReport> findFarmerDailyReport(LifeDailyIncomeReportCriteria criteria);

	public List<FarmerMonthlyReport> findFarmerMonthlyReport(MonthlyReportCriteria criteria);

	public List<FarmerSummaryReport> findFarmerSummaryReport(LifeDailyIncomeReportCriteria criteria);
}
