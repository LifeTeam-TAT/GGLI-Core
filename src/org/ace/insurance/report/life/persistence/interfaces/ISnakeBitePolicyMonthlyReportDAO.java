package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyMonthlyReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

/**
 * This interface serves as the DAO to manipulate the <code>Public SnakeBitePolicy Monthly Report</code> object.
 * 
 * @author Ace Plus
 * @since 1.0.0
 * @date 2013/11/29
 */
public interface ISnakeBitePolicyMonthlyReportDAO {
	/**
	 * This method is used to retrieve all existing <code>PublicSnakeBitePolicyMonthlyReport</code> objects from the database.
	 * @param criteria An instance of SummaryReportCriteria.
	 * @return A {@link List} of {@link SnakeBitePolicyMonthlyReport} instances
	 * @throws DAOException An exception occurs during the DB operation
	 */
	//public List<SnakeBitePolicyMonthlyReport> findPublicSnakeBitePolicyMonthlyReport(SummaryReportCriteria criteria)throws DAOException;

	public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReport(
			SnakeBitePolicyMonthlyReportCriteria criteria);

/*	public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReport(
			SummaryReportCriteria criteria);*/

	
}
