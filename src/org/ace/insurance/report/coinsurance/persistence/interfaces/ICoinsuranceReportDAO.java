package org.ace.insurance.report.coinsurance.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.coinsurance.CoinsuranceCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceReport;
import org.ace.insurance.report.coinsurance.CoinsuranceSummaryReport;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICoinsuranceReportDAO {
	/**
	 * To retrieve all the CoinsuranceReport(s) in the repository by the provided criteria 
	 * @param coinsuranceCriteria - the criteria of start date, end date, insurance type and so on.
	 * @return The list of CoinsuranceReports
	 * @throws DAOException - The exception thrown by
	 */
	public List<CoinsuranceReport> findCoinsurances(CoinsuranceCriteria coinsuranceCriteria)throws DAOException;
	
	/**
	 * To retrieve all the CoinsuranceSummaryReport(s) in the repository by the provided criteria 
	 * @param coinsuranceCriteria - the criteria of start date, end date, insurance type and so on.
	 * @return The list of CoinsuranceSummaryReports
	 * @throws DAOException - The exception thrown by
	 */
	public List<CoinsuranceSummaryReport> findCoinsuranceSummary(CoinsuranceCriteria coinsuranceCriteria)throws DAOException;	
	
	/**
	 * To retrieve all the co-insuranced policy No. from the database
	 * 
	 * @return the list of policy No.
	 * @throws DAOException
	 */
	public List<String> findAllPolicyNo()throws DAOException;
}
