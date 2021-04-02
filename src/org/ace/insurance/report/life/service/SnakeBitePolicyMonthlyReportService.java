package org.ace.insurance.report.life.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.persistence.interfaces.ISnakeBitePolicyMonthlyReportDAO;
import org.ace.insurance.report.life.service.interfaces.ISnakeBitePolicyMonthlyReportService;
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

/**
 * This class serves as the Service to manipulate the
 * <code>SnakeBite Policy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/Nov/29
 */

@Service(value = "SnakeBitePolicyMonthlyReportService")
public class SnakeBitePolicyMonthlyReportService implements ISnakeBitePolicyMonthlyReportService {

	@Resource(name = "SnakeBitePolicyMonthlyReportDAO")
	private ISnakeBitePolicyMonthlyReportDAO snakeBitePolicyMonthlyReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReportByCriteria(SnakeBitePolicyMonthlyReportCriteria criteria) {
		List<SnakeBitePolicyMonthlyReport> result = new ArrayList<SnakeBitePolicyMonthlyReport>();
		try {
			result = snakeBitePolicyMonthlyReportDAO.findSnakeBitePolicyMonthlyReport(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find SnakeBitePolicyMonthlyReport by criteria.", e);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void generateSnakeBitePolicyMonthlyReport(List<SnakeBitePolicyMonthlyReport> reports, SnakeBitePolicyMonthlyReportCriteria criteria, List<Branch> branchList,
			String dirPath, String fileName) {
		List jasperPrintList = new ArrayList();
		try {
			Map paramMap = new HashMap();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(reports));
			InputStream policyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/snakeBite/snakeBiteMontlyReport.jrxml");
			JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
			JasperPrint policyJP = JasperFillManager.fillReport(policyJR, paramMap, new JREmptyDataSource());
			jasperPrintList.add(policyJP);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			FileHandler.forceMakeDirectory(dirPath);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(dirPath + fileName));
			exporter.exportReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
