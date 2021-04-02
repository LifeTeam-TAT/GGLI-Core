package org.ace.insurance.report.coinsurance.service.interfaces;

import java.util.List;

import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.report.customer.CustomerInformationCriteria;
import org.ace.insurance.report.customer.CustomerInformationReport;

public interface ICustomerInformationReportService {
	public List<CustomerInformationReport> findCustomerInformation(CustomerInformationCriteria lifeProposalCriteria);

	public List<IPolicy> findAllActivePoliciesByCustomerId(String Id);

	/**
	 * Generates the {@link CustomerInformationReport} in JasperReport-PDF
	 * format and returns the full path of the output file.
	 * 
	 * @param reportList
	 *            the list of the {@link CustomerInformationReport} which are to
	 *            be included in the output file
	 * @param filePath
	 *            the fully qualified file path where the output report file
	 *            should be produced
	 * @param fileName
	 *            the name of the output file which contains the report
	 */
	public void generateReport(List<CustomerInformationReport> reportList, String filePath, String fileName);

	/**
	 * 
	 * @param customer
	 * @param filePath
	 * @param fileName
	 */
	public void generateReportIndividual(CustomerInformationReport customer, String filePath, String fileName);
}
