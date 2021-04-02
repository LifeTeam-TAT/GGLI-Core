package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.life.snakebite.view.SnakeBiteDailyIncomeReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyDailyReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

/**
 * This interface serves as the DAO to manipulate the
 * <code>Public SnakeBitePolicy Monthly Report</code> object.
 * 
 * @author Ace Plus
 * @since 1.0.0
 * @date 2013/11/29
 */
public interface ISnakeBitePolicyDailyReportDAO {
	/**
	 * This method is used to retrieve all existing
	 * <code>SnakeBiteDailyIncomeReport</code> objects from the database.
	 * 
	 * @param criteria
	 *            An instance of SummaryReportCriteria.
	 * @return A {@link List} of {@link SnakeBiteDailyIncomeReport} instances
	 * @throws DAOException
	 *             An exception occurs during the DB operation
	 */

	public List<SnakeBiteDailyIncomeReport> findSnakeBitePolicyDailyReport(SnakeBitePolicyDailyReportCriteria criteria);

}
