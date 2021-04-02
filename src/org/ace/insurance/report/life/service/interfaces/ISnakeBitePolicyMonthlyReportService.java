package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyMonthlyReportCriteria;
import org.ace.insurance.system.common.branch.Branch;


/**
 * This interface serves as the service layer to manipulate the <code>SnakeBitePolicy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/12/2
 */
public interface ISnakeBitePolicyMonthlyReportService {
	/**
	 * This method is used to retrieve all existing <code>SnakeBitePolicy Monthly Report</code> objects from the repository.
	 * @param criteria An instance of SummaryReportCriteria.
	 * @return A {@link List} of {@link SnakeBitePolicyMonthlyReport} instances
	 * @throws SystemException An Exception which occurs during the operation
	 */
	/*public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReportByCriteria(SummaryReportCriteria criteria);
	
	*//**
	 * This method generate Jasper report in PDF format.
	 * @param reports - the report records to be generated and printed in PDF file
	 * @param fullFilePath - the file path where the generated PDF will be saved
	 * @param criteria - the user filtered criteria values that need to be passed to report generation process
	 *//*
	public void generateSnakeBitePolicyMonthlyReport(List<SnakeBitePolicyMonthlyReport> reports, String fullFilePath, SummaryReportCriteria criteria);*/

	public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReportByCriteria(SnakeBitePolicyMonthlyReportCriteria criteria);
	
	public void generateSnakeBitePolicyMonthlyReport(List<SnakeBitePolicyMonthlyReport> reports, SnakeBitePolicyMonthlyReportCriteria criteria, 
				List<Branch> branchList, String dirPath, String fileName) throws Exception;
}
