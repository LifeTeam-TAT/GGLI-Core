package org.ace.insurance.report.test.coinsurance;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.web.common.ntw.eng.AbstractProcessor;
import org.ace.insurance.web.common.ntw.eng.DefaultProcessor;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
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

public class CoinsuranceCreditDebitTest {
	private static Logger logger = Logger.getLogger(CoinsuranceCreditDebitTest.class);
	private static ICoinsuranceService coinsuranceService;

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(CoinsuranceCreditDebitTest.class.getName());
	}

	@BeforeClass
	public static void init() {
		logger.info("CoinsuranceCreditDebitTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		coinsuranceService = (ICoinsuranceService) factory.getBean("CoinsuranceService");
		logger.info("CoinsuranceCreditDebitTest instance has been loaded.");

	}

	@AfterClass
	public static void finished() {
		logger.info("AgentComReportTest has been finished.........................................");
	}

	@Test
	public void test() {
		try {
			// co-insurance type = IN
			Coinsurance coinsurance = coinsuranceService.findById("ISCOI001002000000012505112013");
			Map paramMap = new HashMap();
			AbstractProcessor processor = new DefaultProcessor();
			paramMap.put("kyats", processor.getName(coinsurance.getPremium()));
			paramMap.put("insuredName", coinsurance.getCustomerName());
			paramMap.put("premium", coinsurance.getPremium());
			paramMap.put("invoiceNo", coinsurance.getInvoiceNo());
			paramMap.put("chequeNo", coinsurance.getCustomerbank() == null ? "" : coinsurance.getChequeNo());
			paramMap.put("insuranceType", coinsurance.getInsuranceType().getLabel());
			paramMap.put("date", new Date());
			// Need to populate data
			paramMap.put("GLCode", "FRE/1307/0000000026/HO");
			paramMap.put("SLCode", "CASH/1309/0000001323/HO");
			paramMap.put("debit", "undefined");

			InputStream inputStream = new FileInputStream("report-template/coinsurance/coinsuredCreditDebit.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/coinsuredCreditDebit.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void prepareTest() {
		try {
			// co-insurance type = IN
			Coinsurance coinsurance = new Coinsurance();
			coinsurance.setCustomerName("insuredName");
			coinsurance.setChequeNo("chequeNo");
			// coinsurance.setInsuranceType(InsuranceType.FIRE);
			coinsurance.setInvoiceNo("invoiceNo");
			coinsurance.setPremium(1000.0);

			Map paramMap = new HashMap();
			AbstractProcessor processor = new DefaultProcessor();
			paramMap.put("kyats", processor.getName(coinsurance.getPremium()));
			paramMap.put("insuredName", coinsurance.getCustomerName());
			paramMap.put("premium", coinsurance.getPremium());
			paramMap.put("invoiceNo", coinsurance.getInvoiceNo());
			paramMap.put("chequeNo", coinsurance.getCustomerbank() == null ? "" : coinsurance.getChequeNo());
			paramMap.put("insuranceType", coinsurance.getInsuranceType().getLabel());
			paramMap.put("date", new Date());
			// Need to populate data
			paramMap.put("GLCode", "FRE/1307/0000000026/HO");
			paramMap.put("SLCode", "CASH/1309/0000001323/HO");
			paramMap.put("debit", "undefined");

			InputStream inputStream = new FileInputStream("report-template/coinsurance/coinsuredCreditDebit.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/coinsuredCreditDebit.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
