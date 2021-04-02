package org.ace.insurance.report.test.coinsurance;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.web.common.Utils;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
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

public class CoInsuranceLetterFireTest {
	private static Logger logger = Logger.getLogger(CoInsuranceLetterFireTest.class);
	private static ICoinsuranceService coinsuranceService;

	@BeforeClass
	public static void init() {
		logger.info("CoInsuranceLetterTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		coinsuranceService = (ICoinsuranceService) factory.getBean("CoinsuranceService");

	}

	@AfterClass
	public static void finished() {
		logger.info("CoInsuranceLetterTest has been finished.........................................");
	}

	@Test
	public void generateCoInsuranceLetter() {
		try {
			String insuranceType = "FIRE";
			Coinsurance coinsurance = coinsuranceService.findById("ISCOI001001000000000118112013");
			InputStream inputStream = null;
			Map paramMap = new HashMap();
			String sumInsured = Utils.formattedCurrency(coinsurance.getSumInsuranced());
			String receivedSumInsured = Utils.formattedCurrency(coinsurance.getReceivedSumInsured());
			String netPremium = Utils.formattedCurrency(coinsurance.getNetPremium());
			String permium = Utils.formattedCurrency(coinsurance.getPremium());
			String comissionAmount = Utils.formattedCurrency(coinsurance.getCommissionAmount());
			String startDate = Utils.formattedDate(coinsurance.getStartDate());
			String endDate = Utils.formattedDate(coinsurance.getEndDate());
			// String commenceDate =
			// Utils.formattedDate(coinsurance.getStartDate());
			String todayDate = Utils.formattedDate(new Date());
			paramMap.put("sumInsured", sumInsured);
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
			paramMap.put("policyNo", coinsurance.getPolicyNo());
			// paramMap.put("commenceDate", commenceDate);
			paramMap.put("receivedSumInsured", receivedSumInsured);
			paramMap.put("netPremium", netPremium);
			paramMap.put("customerName", coinsurance.getCustomerName());
			paramMap.put("customerAddress", coinsurance.getCustomerAddress());
			paramMap.put("permium", permium);
			paramMap.put("comissionAmount", comissionAmount);
			paramMap.put("coInsuranceCompany", coinsurance.getCoinsuranceCompany().getName());
			paramMap.put("todayDate", todayDate);
			paramMap.put("letterNo", "");

			System.out.println("LIFE");

			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory("D:/temp/");
			String outputFile = "D:/temp/coInsuranceLetter.pdf";
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (

		Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	@Test
	public void prepareGenerateCoInsuranceLetter() {
		try {
			String insuranceType = "FIRE";
			InputStream inputStream = null;
			Map paramMap = new HashMap();
			paramMap.put("sumInsured", "sumInsured");
			paramMap.put("startDate", "startDate");
			paramMap.put("endDate", "endDate");
			paramMap.put("policyNo", "policyNo");
			paramMap.put("commenceDate", "commenceDate");
			paramMap.put("customerAddress", "customerAddress");
			paramMap.put("customerName", "customerName");
			paramMap.put("permium", "permium");
			paramMap.put("comissionAmount", "comissionAmount");
			paramMap.put("netPremium", "netPremium");
			paramMap.put("coInsuranceCompany", "coInsuranceCompany");
			paramMap.put("receivedSumInsured", "receivedSumInsured");
			paramMap.put("todayDate", "todayDate");
			paramMap.put("letterNo", "letterNo");

			if (insuranceType.equals("MOTOR")) {
				paramMap.put("registerationNo", "registerationNo");
				paramMap.put("typeOfBody", "typeOfBody");
				paramMap.put("manufacture", "manufacture");
				inputStream = new FileInputStream("report-template/coinsurance/InCoInsuranceMotorPolicy.jrxml");
			} else if (insuranceType.equals("FIRE")) {
				paramMap.put("propertyLocation", "propertyLocation");
				inputStream = new FileInputStream("report-template/coinsurance/InCoInsuranceFirePolicy.jrxml");
			} else {
				System.out.println("LIFE");
			}

			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory("D:/temp/");
			String outputFile = "D:/temp/coInsuranceLetter.pdf";
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(CoInsuranceLetterFireTest.class.getName());
	}

}
