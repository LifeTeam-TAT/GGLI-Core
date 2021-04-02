package org.ace.insurance.report.test.coinsurance;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
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

public class MotorCoinsuranceCashReceiptTest {
	private static Logger logger = Logger.getLogger(MotorCoinsuranceCashReceiptTest.class);
    private static ICoinsuranceService coinsuranceService;
    
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main(MotorCoinsuranceCashReceiptTest.class.getName());
    }
    
	@BeforeClass
    public static void init() {
        logger.info("coinsuranceCashReceiptTest is started.........................................");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
        BeanFactory factory = context;
        coinsuranceService = (ICoinsuranceService)factory.getBean("CoinsuranceService");
        logger.info("coinsuranceCashReceiptTest instance has been loaded.");
                
    }
	
    @AfterClass
    public static void finished() {
        logger.info("AgentComReportTest has been finished.........................................");
    }
    

    @Test
    public void prepareTest() {
    	try {
        	Map cashReceiptMap = new HashMap();
        	cashReceiptMap.put("policyNo" ,"policyNo");
        	cashReceiptMap.put("cashReceiptNo" ,"cashReceiptNo");
        	cashReceiptMap.put("receiptName" ,"receiptName");
        	cashReceiptMap.put("sumInsured" ,1000.0);
        	cashReceiptMap.put("premium" ,1000.0);
        	cashReceiptMap.put("discountAmount" ,1000.0);
        	cashReceiptMap.put("addOnPremium" ,1000.0);
        	cashReceiptMap.put("netPremium" ,1000.0);
        	cashReceiptMap.put("serviceCharges" ,1000.0);
        	cashReceiptMap.put("stempFees" ,1000.0);
        	cashReceiptMap.put("totalAmount" ,1000.0);
        	cashReceiptMap.put("paymentType" ,"paymentType");
        	cashReceiptMap.put("policyType" ,"policyType");
        	cashReceiptMap.put("confirmDate" , new Date());
        	cashReceiptMap.put("fromDate" , new Date());
        	cashReceiptMap.put("toDate" , new Date());
        	cashReceiptMap.put("agent" ,"agent");
        	cashReceiptMap.put("insuredPerson" ,"insuredPerson");
        	cashReceiptMap.put("customerAddress" ,"customerAddress");
        	cashReceiptMap.put("receiptType" ,"receiptType");
        	cashReceiptMap.put("registrationNo" ,"registrationNo");
        	cashReceiptMap.put("chequeNo" ,"chequeNo");
        	cashReceiptMap.put("bank" ,"bank");
        	cashReceiptMap.put("chequePayment", false);
        	cashReceiptMap.put("premiumName" ,"premiumName");
        	cashReceiptMap.put("additionalPremium" ,1000.0);
        	InputStream inputStream = new FileInputStream("report-template/coinsurance/coinsuranceMotorCashReceipt.jrxml");
    		JasperReport jreport = JasperCompileManager.compileReport(inputStream);
    		JasperPrint jprint = JasperFillManager.fillReport(jreport, cashReceiptMap, new JREmptyDataSource());
    		JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/motorCoinsuranceCashReceipt.pdf");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

}
