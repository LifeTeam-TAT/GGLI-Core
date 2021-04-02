package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.LifeDailyIncomeReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;

public interface ILifeDailyIncomeReportService {
	public List<LifeDailyIncomeReport> findLifeDailyIncome(LifeDailyIncomeReportCriteria lifeDailyIncomeCriteria, List<String> productIdList);

	public void generateLifeDailyIncomeReport(List<LifeDailyIncomeReport> reportList, String dirPath, String fileName, String branch);
}
