package org.ace.insurance.report.coinsurance.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.report.JRGenerateUtility;
import org.ace.insurance.report.coinsurance.CoinsuranceCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceReport;
import org.ace.insurance.report.coinsurance.CoinsuranceSummaryReport;
import org.ace.insurance.report.coinsurance.persistence.interfaces.ICoinsuranceReportDAO;
import org.ace.insurance.report.coinsurance.service.interfaces.ICoinsuranceReportService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "CoinsuranceReportService")
public class CoinsuranceReportService implements ICoinsuranceReportService {

	@Resource(name = "CoinsuranceReportDAO")
	private ICoinsuranceReportDAO coinsuranceReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<CoinsuranceReport> findCoinsuranceReports(CoinsuranceCriteria coinsuranceCriteria) {
		List<CoinsuranceReport> result = null;
		try {
			result = coinsuranceReportDAO.findCoinsurances(coinsuranceCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find CoinsuranceReports by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<CoinsuranceSummaryReport> findCoinsuranceSummaryReports(CoinsuranceCriteria coinsuranceCriteria) {
		List<CoinsuranceSummaryReport> result = null;
		try {
			result = coinsuranceReportDAO.findCoinsuranceSummary(coinsuranceCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find CoinsuranceSummaryReports by criteria.", e);
		}
		return result;
	}

	@Override
	public List<String> findAllPolicyNo() {
		List<String> policies = null;
		try {
			policies = coinsuranceReportDAO.findAllPolicyNo();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Coinsured Policy No..", e);
		}
		return policies;
	}

	@Override
	public void generateSummaryReport(List<CoinsuranceSummaryReport> reportList, String filePath, String fileName) {
		final String templatePath = "/report-template/coinsurance/";
		final String templateName = "coinsuranceSummaryReport.jrxml";
		String templateFullPath = templatePath + templateName;
		String outputFilePdf = filePath + fileName;

		// Create the DataSource instance with the given list which
		// in turn to be filled up in the report
		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(reportList);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("TableDataSource", ds);
		new JRGenerateUtility().generateReport(templateFullPath, outputFilePdf, paramMap);
	}

	@Override
	public void generateReport(List<CoinsuranceReport> reportList, String filePath, String fileName) {
		final String templatePath = "/report-template/coinsurance/";
		final String templateName = "coinsuranceReport.jrxml";
		String templateFullPath = templatePath + templateName;
		String outputFilePdf = filePath + fileName;

		double totalSI = calculateTotalSumInsured(reportList);
		double totalPremium = calculateTotalPremium(reportList);
		double totalReceivedSI = calculateTotalReceivedSumInsured(reportList);

		// Create the DataSource instance with the given list which
		// in turn to be filled up in the report
		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(reportList);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("TableDataSource", ds);
		paramMap.put("totalSI", totalSI);
		paramMap.put("totalReceivedSI", totalReceivedSI);
		paramMap.put("totalPremium", totalPremium);
		new JRGenerateUtility().generateReport(templateFullPath, outputFilePdf, paramMap);
	}

	private double calculateTotalSumInsured(List<CoinsuranceReport> reportList) {
		double totalSI = 0.0;

		if (reportList != null) {
			for (CoinsuranceReport report : reportList) {
				totalSI = totalSI + report.getSumInsured();
			}
		}
		return totalSI;
	}

	private double calculateTotalReceivedSumInsured(List<CoinsuranceReport> reportList) {
		double totalReceivedSI = 0.0;

		if (reportList != null) {
			for (CoinsuranceReport report : reportList) {
				totalReceivedSI = totalReceivedSI + report.getReceivedSumInsured();
			}
		}
		return totalReceivedSI;
	}

	private double calculateTotalPremium(List<CoinsuranceReport> reportList) {
		double totalPremium = 0.0;

		if (reportList != null) {
			for (CoinsuranceReport report : reportList) {
				totalPremium = totalPremium + report.getPremium();
			}
		}
		return totalPremium;
	}
}
