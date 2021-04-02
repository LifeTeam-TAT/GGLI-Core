package org.ace.insurance.report.fidelity.service.interfaces;

/**
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/5
 */
import java.util.List;

import org.ace.insurance.report.fidelity.FidelityMonthlyReport;
import org.ace.insurance.report.fidelity.FidelityMonthlyReportCriteria;


public interface IFidelityMonthlyReportService {

	public List<FidelityMonthlyReport> findFidelityMonthlyReport(FidelityMonthlyReportCriteria fidelityMonthlyCriteria);

}
