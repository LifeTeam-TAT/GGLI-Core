package org.ace.insurance.report.fidelity.service.interfaces;

import java.util.List;

import org.ace.insurance.report.fidelity.FidelityDailyIncomeReport;
import org.ace.insurance.report.fidelity.FidelityDailyIncomeReportCriteria;
import org.ace.insurance.system.common.branch.Branch;

public interface IFidelityDailyIncomeReportService {
	public List<FidelityDailyIncomeReport> findFidelityDailyIncomeReport(FidelityDailyIncomeReportCriteria criteria);
	public void generateFidelityDailyIncomeReport(String fullTemplateFilePath, FidelityDailyIncomeReportCriteria criteria, List<Branch> branchList, String dirPath, String fileName);
	public void generateFidelityDailyIncomeReport(List<FidelityDailyIncomeReport> fidelityDailyIncomeReportList, String fullReportFilePath);

}
