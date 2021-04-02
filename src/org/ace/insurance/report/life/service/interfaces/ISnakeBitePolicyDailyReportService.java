package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.life.snakebite.view.SnakeBiteDailyIncomeReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyDailyReportCriteria;
import org.ace.insurance.system.common.branch.Branch;

/**
 * This interface serves as the service layer to manipulate the
 * <code>SnakeBitePolicy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/12/2
 */
public interface ISnakeBitePolicyDailyReportService {
	/**
	 * This method is used to retrieve all existing
	 * <code>SnakeBiteDailyIncomeReport</code> objects from the repository.
	 * 
	 * @param criteria
	 *            An instance of SummaryReportCriteria.
	 * @return A {@link List} of {@link SnakeBiteDailyIncomeReport} instances
	 * @throws SystemException
	 *             An Exception which occurs during the operation
	 */

	public List<SnakeBiteDailyIncomeReport> findSnakeBitePolicyDailyReportByCriteria(SnakeBitePolicyDailyReportCriteria criteria);

	/**
	 * This method generate Jasper report in PDF format.
	 * 
	 * @param reports
	 *            - the report records to be generated and printed in PDF file
	 * @param fullFilePath
	 *            - the file path where the generated PDF will be saved
	 * @param criteria
	 *            - the user filtered criteria values that need to be passed to
	 *            report generation process
	 */
	public void generateSnakeBitePolicyDailyReport(List<SnakeBiteDailyIncomeReport> reports, SnakeBitePolicyDailyReportCriteria criteria, List<Branch> branchList, String dirPath,
			String fileName) throws Exception;
}
