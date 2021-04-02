package org.ace.insurance.report.test.snakeBite;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.snakebite.SnakeBiteBeneficiary;
import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.service.SnakeBitePolicyMonthlyReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ISnakeBitePolicyMonthlyReportService;
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

public class SnakeBitePolicyMonthlyReportTest {
	private static Logger logger = Logger.getLogger(SnakeBitePolicyMonthlyReportTest.class);
	private static ISnakeBitePolicyMonthlyReportService snakeBitePolicyMonthlyReportService;

	@BeforeClass
	public static void init() {

		logger.info("SnakeBitePolicyMonthlyReportTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		snakeBitePolicyMonthlyReportService = (ISnakeBitePolicyMonthlyReportService) factory.getBean("SnakeBitePolicyMonthlyReportService");
		logger.info("SnakeBitePolicyMonthlyReportTest instance has been loaded.");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(SnakeBitePolicyMonthlyReportTest.class.getName());
	}

	// @Test
	public void findSnakeBitePolicyMonthlyReportTest() {
		try {
			SnakeBitePolicyMonthlyReportCriteria criteria = new SnakeBitePolicyMonthlyReportCriteria();
			criteria.setStartDate(Utils.getDate("1-10-2013"));
			criteria.setEndDate(Utils.getDate("31-1-2014"));
			List<SnakeBitePolicyMonthlyReport> snakeBiteReportList = snakeBitePolicyMonthlyReportService.findSnakeBitePolicyMonthlyReportByCriteria(criteria);
			Map paramMap = new HashMap();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(snakeBiteReportList));
			InputStream inputStream = new FileInputStream("report-template/snakeBite/snakeBiteMontlyReport.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/snakeBitePolicyMonthlyReport.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * private double
	 * getTotalReportSumInsured(List<SnakeBitePolicyMonthlyReport>
	 * snakeBitePolicyList){ double total = 0.0;
	 * System.out.println(snakeBitePolicyList.size()
	 * +"This is snake bite pl list"); for(SnakeBitePolicyMonthlyReport
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

	@Test
	public void prepareSnakeBitePolicyMonthlyReportTest() {
		try {
			// creating SnakeBiteBeneficiaryList

			List<SnakeBiteBeneficiary> beneList = new ArrayList<SnakeBiteBeneficiary>();
			SnakeBiteBeneficiary snakeBiteBeneficiary = new SnakeBiteBeneficiary();
			snakeBiteBeneficiary.setIdNo("123");
			// Name name = new Name();
			// name.setFirstName("firstName");
			// name.setMiddleName("middleName");
			// name.setLastName("lastName");
			// snakeBiteBeneficiary.setName(name);
			ResidentAddress residentAddress1 = new ResidentAddress();
			residentAddress1.setResidentAddress("residentAddress");
			snakeBiteBeneficiary.setResidentAddress(residentAddress1);
			snakeBiteBeneficiary.setInitialId("initialId");
			beneList.add(snakeBiteBeneficiary);

			// creating SnakeBitePolicyMonthlyReportList

			List<SnakeBitePolicyMonthlyReport> snakeBiteReportList = new ArrayList<SnakeBitePolicyMonthlyReport>();
			SnakeBitePolicyMonthlyReport snakeBitePolicyMonthlyReport = new SnakeBitePolicyMonthlyReport();
			snakeBitePolicyMonthlyReport.setCustomerName("customerName");
			snakeBitePolicyMonthlyReport.setPolicyNo("policyNo");
			snakeBitePolicyMonthlyReport.setNrc("nrc");
			snakeBitePolicyMonthlyReport.setAddress("address");
			snakeBitePolicyMonthlyReport.setRemark("remark");
			snakeBitePolicyMonthlyReport.setServiceCharges("serviceCharges");
			snakeBitePolicyMonthlyReport.setPremium(1000.0);
			snakeBitePolicyMonthlyReport.setNetpremium("1000.0");
			snakeBitePolicyMonthlyReport.setBeneList(beneList);
			snakeBitePolicyMonthlyReport.setPaymentDate("12-6-2014");
			snakeBitePolicyMonthlyReport.setReceiptNo("receiptNo");
			snakeBitePolicyMonthlyReport.setAddress("address");
			snakeBiteReportList.add(snakeBitePolicyMonthlyReport);

			Map paramMap = new HashMap();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(snakeBiteReportList));
			InputStream inputStream = new FileInputStream("report-template/snakeBite/snakeBiteMontlyReport.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/snakeBitePolicyMonthlyReport.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
