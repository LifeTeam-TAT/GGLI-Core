package org.ace.insurance.report.test.coinsurance;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
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

public class OutCoinsuranceComissionPaymentTest {
	private static Logger logger = Logger.getLogger(OutCoinsuranceComissionPaymentTest.class);
    private static ICoinsuranceService coinsuranceService;
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main(OutCoinsuranceComissionPaymentTest.class.getName());
    }
    
	@BeforeClass
    public static void init() {
        logger.info("OutCoinsuranceComissionPaymentTest is started.........................................");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
        BeanFactory factory = context;
        coinsuranceService = (ICoinsuranceService)factory.getBean("CoinsuranceService");
        logger.info("OutCoinsuranceComissionPaymentTest instance has been loaded.");
                
    }
	
    @AfterClass
    public static void finished() {
        logger.info("AgentComReportTest has been finished.........................................");
    }
    
	@Test
	public  void generateOutCoinsuranceCommissionPayment() {
		Coinsurance coinsurance = coinsuranceService.findById("ISCOI001001000000000118112013");
		 String premiumtype ="premiumtype"; 
		 String insurerName ="insuranceName";
		 String coaName ="coaName";
		 String coaName2 ="coaName2";
		 String coaName3 ="coaName3";
		 String coaName4 ="coaName4";
		TLF tlfForCoInsuPayCr = new TLF();
		tlfForCoInsuPayCr.setCoaId("paymentCcoaCode");
		tlfForCoInsuPayCr.setHomeAmount(100000000);
		
		TLF tlfForCoInsuPayDr = new TLF();
		tlfForCoInsuPayDr.setCoaId("coaId");
		
		TLF tlfForCoInsuCommDebit = new TLF();
		tlfForCoInsuCommDebit.setCoaId("paymentCcoaCode");
		tlfForCoInsuCommDebit.setHomeAmount(200000000);
		
		TLF tlfForCoInsuCommCredit = new TLF();
		tlfForCoInsuCommCredit.setCoaId("coaId");
		
		try {
			Map paramMap = new HashMap();
			paramMap.put("invoiceno", coinsurance.getInvoiceNo());
			paramMap.put("accountCode", tlfForCoInsuPayCr.getCoaId());
			paramMap.put("accountCode2", tlfForCoInsuPayDr.getCoaId());
			paramMap.put("accountCode3", tlfForCoInsuCommDebit.getCoaId());
			paramMap.put("accountCode4", tlfForCoInsuCommCredit.getCoaId());
			paramMap.put("accountName", coaName);
			paramMap.put("accountName2", coaName2);
			paramMap.put("accountName3", coaName3);
			paramMap.put("accountName4", coaName4);
			paramMap.put("premium", premiumtype);
			paramMap.put("sumInsured", coinsurance.getSumInsuranced());
			paramMap.put("insuredName", insurerName);
			paramMap.put("amount", tlfForCoInsuPayCr.getHomeAmount());
			paramMap.put("amount2", tlfForCoInsuCommDebit.getHomeAmount());
			paramMap.put("companyName", coinsurance.getCoinsuranceCompany().getName());
	
			InputStream inputStream = new FileInputStream("report-template/coinsurance/coinsurance.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport,paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory("D:/temp/");
			String outputFile = "D:/temp/coinsurance.pdf";
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR,
					"Failed to generate OutCoinsuranceComissionPaymentTest", e);
		}
	}
	
	@Test
	public  void prepareGenerateOutCoinsuranceCommissionPayment() {
		Coinsurance coinsurance = new Coinsurance();
		coinsurance.setInvoiceNo("invoiceno");
		coinsurance.setSumInsuranced(1000.0);	
		CoinsuranceCompany coinsuranceCompany = new CoinsuranceCompany();
		coinsuranceCompany.setName("coinsuranceCompanyName");
		coinsurance.setCoinsuranceCompany(coinsuranceCompany);
		
		 String premiumtype ="premiumtype"; 
		 String insurerName ="insuranceName";
		 String coaName ="coaName";
		 String coaName2 ="coaName2";
		 String coaName3 ="coaName3";
		 String coaName4 ="coaName4";
		TLF tlfForCoInsuPayCr = new TLF();
		tlfForCoInsuPayCr.setCoaId("paymentCcoaCode");
		tlfForCoInsuPayCr.setHomeAmount(100000000);
		
		TLF tlfForCoInsuPayDr = new TLF();
		tlfForCoInsuPayDr.setCoaId("coaId");
		
		TLF tlfForCoInsuCommDebit = new TLF();
		tlfForCoInsuCommDebit.setCoaId("paymentCcoaCode");
		tlfForCoInsuCommDebit.setHomeAmount(200000000);
		
		TLF tlfForCoInsuCommCredit = new TLF();
		tlfForCoInsuCommCredit.setCoaId("coaId");
		
		try {
			Map paramMap = new HashMap();
			paramMap.put("invoiceno", coinsurance.getInvoiceNo());
			paramMap.put("accountCode", tlfForCoInsuPayCr.getCoaId());
			paramMap.put("accountCode2", tlfForCoInsuPayDr.getCoaId());
			paramMap.put("accountCode3", tlfForCoInsuCommDebit.getCoaId());
			paramMap.put("accountCode4", tlfForCoInsuCommCredit.getCoaId());
			paramMap.put("accountName", coaName);
			paramMap.put("accountName2", coaName2);
			paramMap.put("accountName3", coaName3);
			paramMap.put("accountName4", coaName4);
			paramMap.put("premium", premiumtype);
			paramMap.put("sumInsured", coinsurance.getSumInsuranced());
			paramMap.put("insuredName", insurerName);
			paramMap.put("amount", tlfForCoInsuPayCr.getHomeAmount());
			paramMap.put("amount2", tlfForCoInsuCommDebit.getHomeAmount());
			paramMap.put("companyName", coinsurance.getCoinsuranceCompany().getName());
	
			InputStream inputStream = new FileInputStream("report-template/coinsurance/coinsurance.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport,paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory("D:/temp/");
			String outputFile = "D:/temp/coinsurance.pdf";
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR,
					"Failed to generate OutCoinsuranceComissionPaymentTest", e);
		}
	}
}

