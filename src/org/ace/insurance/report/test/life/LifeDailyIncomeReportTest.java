package org.ace.insurance.report.test.life;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.report.life.LifeDailyIncomeReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ILifeDailyIncomeReportService;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class LifeDailyIncomeReportTest {
	private static Logger logger = Logger.getLogger(LifeDailyIncomeReport.class);
	private static ILifeDailyIncomeReportService lifeDailyIncomeReportService;

	@BeforeClass
	public static void init() {
		logger.info("LifeDailyIncomeReportTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		lifeDailyIncomeReportService = (ILifeDailyIncomeReportService) factory.getBean("LifeDailyIncomeReportService");
		logger.info("LifeDailyIncomeReportTest instance has been loaded.");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(LifeDailyIncomeReportTest.class.getName());
	}

	// @Test
	public void test() {
		try {
			LifeDailyIncomeReportCriteria criteria = new LifeDailyIncomeReportCriteria();
			// criteria.setStartDate(Utils.getDate("01-05-2013"));
			// criteria.setEndDate(Utils.getDate("15-08-2013"));
			// List<LifeDailyIncomeReport> LifeDailyIncomeList =
			// lifeDailyIncomeReportService.findLifeDailyIncome(criteria);
			Map paramMap = new HashMap();
			// paramMap.put("totalAmount", getTotalAmount(LifeDailyIncomeList));
			// paramMap.put("LifeDailyIncomeList", new
			// JRBeanCollectionDataSource(LifeDailyIncomeList));
			InputStream inputStream = new FileInputStream("report-template/life/lifeDailyIncomeReport.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/lifeDailyIncomeReport.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void prepareTest() {
		try {
			List<LifeDailyIncomeReport> LifeDailyIncomeList = new ArrayList<LifeDailyIncomeReport>();
			Map paramMap = new HashMap();
			paramMap.put("grandTotal", 1000.0);
			paramMap.put("lastIndex", true);
			paramMap.put("branch", "branch");
			paramMap.put("totalAmount", getTotalAmount(LifeDailyIncomeList));
			paramMap.put("LifeDailyIncomeList", new JRBeanCollectionDataSource(LifeDailyIncomeList));

			InputStream inputStream = new FileInputStream("report-template/life/lifeDailyIncomeReport.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/lifeDailyIncomeReport.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double getTotalAmount(List<LifeDailyIncomeReport> reportList) {
		double totalAmount = 0.0;
		for (LifeDailyIncomeReport l : reportList) {
			totalAmount += l.getAmount();
		}
		return totalAmount;
	}
}
