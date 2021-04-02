package org.ace.insurance.report.fidelity.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.report.JRGenerateUtility;
import org.ace.insurance.report.fidelity.FidelityDailyIncomeReport;
import org.ace.insurance.report.fidelity.FidelityDailyIncomeReportCriteria;
import org.ace.insurance.report.fidelity.persistence.interfaces.IFidelityDailyIncomeReportDAO;
import org.ace.insurance.report.fidelity.service.interfaces.IFidelityDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Service(value = "FidelityDailyIncomeReportService")
public class FidelityDailyIncomeReportService implements IFidelityDailyIncomeReportService {
	@Resource(name = "FidelityDailyIncomeReportDAO")
	private IFidelityDailyIncomeReportDAO fidelityDailyIncomeReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FidelityDailyIncomeReport> findFidelityDailyIncomeReport(FidelityDailyIncomeReportCriteria criteria) {
		List<FidelityDailyIncomeReport> result = new ArrayList<FidelityDailyIncomeReport>();
		try {
			result = fidelityDailyIncomeReportDAO.find(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find findFidelityDailyIncomeReport by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void generateFidelityDailyIncomeReport(List<FidelityDailyIncomeReport> fidelityDailyIncomeReportList, String fullReportFilePath) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("TableDataSource", new JRBeanCollectionDataSource(fidelityDailyIncomeReportList));
		String fullTemplateFilePath = "report-template/fire/fidelityDailyIncomeReport.jrxml";
		new JRGenerateUtility().generateReport(fullTemplateFilePath, fullReportFilePath, params);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void generateFidelityDailyIncomeReport(String fullTemplateFilePath, FidelityDailyIncomeReportCriteria criteria, List<Branch> branchList, String dirPath,
			String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> params = new HashMap<String, Object>();

			if (criteria.getBranch() == null) {
				for (Branch branch : branchList) {
					FidelityDailyIncomeReportCriteria fidelityDailyIncomeCriteria = new FidelityDailyIncomeReportCriteria();
					fidelityDailyIncomeCriteria.setStartDate(criteria.getStartDate());
					fidelityDailyIncomeCriteria.setEndDate(criteria.getEndDate());
					fidelityDailyIncomeCriteria.setBranch(branch);
					List<FidelityDailyIncomeReport> reports = findFidelityDailyIncomeReport(fidelityDailyIncomeCriteria);
					List<FidelityDailyIncomeReport> temp = new ArrayList<FidelityDailyIncomeReport>();

					if (reports != null && !reports.isEmpty()) {

						for (FidelityDailyIncomeReport report : reports) {
							temp.add(report);
						}
						params.put("TableDataSource", new JRBeanCollectionDataSource(temp));
						params.put("branch", branch.getName());
						InputStream policyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullTemplateFilePath);
						JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
						JasperPrint policyJP = JasperFillManager.fillReport(policyJR, params, new JREmptyDataSource());
						jasperPrintList.add(policyJP);
						JRExporter exporter = new JRPdfExporter();
						exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
						FileHandler.forceMakeDirectory(dirPath);
						exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(dirPath + fileName));
						exporter.exportReport();
					}
				}
			} else {
				List<FidelityDailyIncomeReport> reports = findFidelityDailyIncomeReport(criteria);

				params.put("TableDataSource", new JRBeanCollectionDataSource(reports));
				params.put("branch", criteria.getBranch().getName());
				InputStream policyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullTemplateFilePath);
				JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
				JasperPrint policyJP = JasperFillManager.fillReport(policyJR, params, new JREmptyDataSource());
				jasperPrintList.add(policyJP);
				JRExporter exporter = new JRPdfExporter();
				exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
				FileHandler.forceMakeDirectory(dirPath);
				exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(dirPath + fileName));
				exporter.exportReport();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
