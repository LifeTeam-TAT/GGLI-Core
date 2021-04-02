package org.ace.insurance.report.test.snakeBite;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.Utils;
import org.ace.insurance.life.snakebite.view.SnakeBiteDailyIncomeReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyDailyReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ISnakeBitePolicyDailyReportService;
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

public class SnakeBitePolicyDailyReportTest {
	private static Logger logger = Logger.getLogger(SnakeBitePolicyDailyReportTest.class);
	private static ISnakeBitePolicyDailyReportService snakeBitePolicyDailyReportService;

	@BeforeClass
	public static void init() {

		logger.info("SnakeBitePolicyDailyReportTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		snakeBitePolicyDailyReportService = (ISnakeBitePolicyDailyReportService) factory.getBean("SnakeBitePolicyDailyReportService");
		logger.info("SnakeBitePolicyDailyReportTest instance has been loaded.");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(SnakeBitePolicyDailyReportTest.class.getName());
	}

	@Test
	public void findSnakeBitePolicyDailyReportTest() {
		try {
			SnakeBitePolicyDailyReportCriteria criteria = new SnakeBitePolicyDailyReportCriteria();
			criteria.setStartDate(Utils.getDate("1-9-2013"));
			criteria.setEndDate(Utils.getDate("31-3-2014"));
			List<SnakeBiteDailyIncomeReport> snakeBiteReportList = snakeBitePolicyDailyReportService.findSnakeBitePolicyDailyReportByCriteria(criteria);
			Map paramMap = new HashMap();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(snakeBiteReportList));
			InputStream inputStream = new FileInputStream("report-template/snakeBite/snakeBiteDailyReport.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/snakeBitePolicyDailyReport.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * private double
	 * getTotalReportSumInsured(List<SnakeBitePolicyMonthlyReport>
	 * snakeBitePolicyList){ double total = 0.0;
	 * System.out.println(snakeBitePolicyList.size() +
	 * "This is snake bite pl list"); for(SnakeBitePolicyMonthlyReport
	 * snakeBitePolicyMonthlyReport :snakeBitePolicyList) { total +=
	 * snakeBitePolicyMonthlyReport.getSumInsure(); } return total;
	 * 
	 * }
	 * 
	 * private double getTotalReportPremiun(List<SnakeBitePolicyMonthlyReport>
	 * snakeBitePolicyList){ double total = 0.0;
	 * for(SnakeBitePolicyMonthlyReport snakeBitePolicyReport
	 * :snakeBitePolicyList) { total += snakeBitePolicyReport.getPremium(); }
	 * return total;
	 * 
	 * }
	 */
}
