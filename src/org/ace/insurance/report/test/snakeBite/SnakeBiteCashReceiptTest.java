package org.ace.insurance.report.test.snakeBite;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyCriteria;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
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

public class SnakeBiteCashReceiptTest {
	private static Logger logger = Logger.getLogger(SnakeBiteCashReceiptTest.class);
	private static IPaymentService paymentService;
	private static ISnakeBitePolicyService snakeBitePolicyService;
	
	
	@BeforeClass
    public static void init() {
        logger.info("SnakeBiteCashReceiptTest is started.........................................");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
        BeanFactory factory = context;
        snakeBitePolicyService = (ISnakeBitePolicyService)factory.getBean("SnakeBitePolicyService");
        paymentService = (IPaymentService)factory.getBean("PaymentService");
        logger.info("SnakeBiteCashReceiptTest instance has been loaded.");
    }
	
	
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main(SnakeBiteCashReceiptTest.class.getName());
    }
    
//    @Test
    public void findSnakeBitePolicyMonthlyReportTest(){
    	SnakeBitePolicyCriteria criteria = new SnakeBitePolicyCriteria();
    	List<SnakeBitePolicy> snakeBitePolicyList = snakeBitePolicyService.findSnakeBitePolicyByReceiptNo("CASH/1312/0000004755/001");
    	Payment payment = (Payment) paymentService.findPaymentByReceiptNo("CASH/1312/0000004755/001");
    	try {
			Map paramMap = new HashMap();
			double commissionPercentage = 0.0;
			double totalSumInsured = 0.0;
			double totalPremium = 0.0;
			double agentCommission = 0.0;
			double netPremium = 0.0;
			if(snakeBitePolicyList.size() > 0){
				for(SnakeBitePolicy snakeBitePolicy:snakeBitePolicyList){
					 totalSumInsured += snakeBitePolicy.getSumInsured();
					 totalPremium += snakeBitePolicy.getPremium();
				}	
				paramMap.put("sumInsured", totalSumInsured);
				paramMap.put("premium",totalPremium);
				paramMap.put("paymentType", snakeBitePolicyList.get(0).getPaymentType().getName());
				paramMap.put("cashReceiptNo",snakeBitePolicyList.get(0).getReferenceNo());
				paramMap.put("fromDate", snakeBitePolicyList.get(0).getPolicyStartDate());
				paramMap.put("toDate", snakeBitePolicyList.get(0).getPolicyEndDate());
			 if(snakeBitePolicyList.get(0).getAgent() != null){
				 commissionPercentage = snakeBitePolicyList.get(0).getProduct().getFirstCommission();
				 agentCommission = totalPremium *  commissionPercentage / 100;
				 paramMap.put("agentCommission", agentCommission);
				 paramMap.put("agent", snakeBitePolicyList.get(0).getAgent().getName());
			 }else {
				 paramMap.put("agent","");
				 paramMap.put("agentCommission", 0.0);
			}
				paramMap.put("netPremium", payment.getBasicPremium());
				paramMap.put("totalAmount",payment.getBasicPremium());	
				paramMap.put("policyType", snakeBitePolicyList.get(0).getProduct().getName());
			
			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paramMap.put("chequeNo", payment.getChequeNo());
				paramMap.put("bank", payment.getBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
				paramMap.put("receiptType", "Temporary Receipt");
				paramMap.put("receiptName", "Receipt No");
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				paramMap.put("receiptName", "Cash Receipt No");
				paramMap.put("receiptType", "Cash Receipt");
			}
			paramMap.put("bookNo", payment.getReferenceNo());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("confirmDate", new Date());
			}
			InputStream inputStream = new FileInputStream("report-template/life/snakeBiteCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
    		JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
    		JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/SnakeBiteCashReceipt.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
   }
    
    @Test
    public void prepareSnakeBitePolicyMonthlyReportTest(){
    	try {
			Map paramMap = new HashMap();
			paramMap.put("policyNo" ,"policyNo");
			paramMap.put("cashReceiptNo" ,"cashReceiptNo");
			paramMap.put("receiptName" ,"receiptName");
			paramMap.put("sumInsured" ,1000.0);
			paramMap.put("premium" ,1000.0);
			paramMap.put("discountAmount" ,1000.0);
			paramMap.put("agentCommission" ,1000.0);
			paramMap.put("netPremium" ,1000.0);
			paramMap.put("serviceCharges" ,1000.0);
			paramMap.put("stempFees" ,1000.0);
			paramMap.put("totalAmount" ,1000.0);
			paramMap.put("paymentType" ,"paymentType");
			paramMap.put("policyType" ,"policyType");
			paramMap.put("confirmDate" , new Date());
			paramMap.put("fromDate" , new Date());
			paramMap.put("toDate" , new Date());
			paramMap.put("agent" ,"agent");
			paramMap.put("customerName" ,"customerName");
			paramMap.put("customerAddress" ,"customerAddress");
			paramMap.put("receiptType" ,"receiptType");
			paramMap.put("chequeNo" ,"chequeNo");
			paramMap.put("bank" ,"bank");
			paramMap.put("chequePayment" , true);
			paramMap.put("premiumName" , "premiumName");
			paramMap.put("addOnPremiumName" , "addOnPremiumName");
			paramMap.put("bookNo" , "bookNo");
			InputStream inputStream = new FileInputStream("report-template/life/snakeBiteCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
    		JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
    		JasperExportManager.exportReportToPdfFile(jprint, "D:/temp/SnakeBiteCashReceipt.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
   }

}
