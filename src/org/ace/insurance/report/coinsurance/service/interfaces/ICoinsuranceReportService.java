package org.ace.insurance.report.coinsurance.service.interfaces;

import java.util.List;

import org.ace.insurance.report.coinsurance.CoinsuranceCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceReport;
import org.ace.insurance.report.coinsurance.CoinsuranceSummaryReport;

public interface ICoinsuranceReportService {
	/**
	 * To retrieve all the CoinsuranceReport(s) in the system by the provided criteria 
	 * @param coinsuranceCriteria - the criteria of start date, end date, insurance type and so on.
	 * @return The list of CoinsuranceReports
	 */

	public List<CoinsuranceReport> findCoinsuranceReports(CoinsuranceCriteria coinsuranceCriteria);
	
	/**
	 * To retrieve all the CoinsuranceSummaryReport list in the system by the provided criteria 
	 * 
	 * @param coinsuranceCriteria - the criteria of start date and end date.
	 * @return The list of CoinsuranceSummaryReport
	 */
	public List<CoinsuranceSummaryReport> findCoinsuranceSummaryReports(CoinsuranceCriteria coinsuranceCriteria);
	
	/**
	 * To retrieve all the co-insuranced policy No. from the system
	 * 
	 * @return the list of policy No.
	 */
	public List<String> findAllPolicyNo();

	/**
	 * Generates the {@link CoinsuranceReport} in JasperReport-PDF format and 
	 * returns the full path of the output file.
	 * 
	 * @param reportList the list of the {@link CoinsuranceReport} which are to be included in the output file
	 * @param filePath the fully qualified file path where the output report file should be produced
	 * @param fileName the name of the output file which contains the report
	 */
	public void generateReport(List<CoinsuranceReport> reportList, String filePath, String fileName);

	/**
	 * Generates the {@link CoinsuranceSummaryReport} in JasperReport-PDF format and 
	 * returns the full path of the output file.
	 * 
	 * @param reportList the list of the {@link CoinsuranceSummaryReport} which are to be included in the output file
	 * @param filePath the fully qualified file path where the output report file should be produced
	 * @param fileName the name of the output file which contains the report
	 */
	public void generateSummaryReport(List<CoinsuranceSummaryReport> reportList, String filePath, String fileName);
}
