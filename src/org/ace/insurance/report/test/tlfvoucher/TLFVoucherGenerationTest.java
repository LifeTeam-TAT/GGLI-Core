package org.ace.insurance.report.test.tlfvoucher;

import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TLFVoucherGenerationTest {
	private static Logger logger = Logger.getLogger(TLFVoucherGenerationTest.class);
	private static IPaymentDAO paymentDAO;

	@BeforeClass
	public static void init() {
		logger.info("CashReceiptTestTest is started.........................................");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		paymentDAO = (IPaymentDAO) factory.getBean("PaymentDAO");
		logger.info("CashReceiptTestTest instance has been loaded.");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(TLFVoucherGenerationTest.class.getName());
	}

	// @Test
	// public void generateTLFVoucher() {
	// try {
	//
	// List<TLFVoucherDTO> cashReceiptDTOList =
	// paymentDAO.findTLFVoucher("CASH/1402/0000004192/001");
	// Map paramMap = new HashMap();
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(cashReceiptDTOList));
	// InputStream inputStream = new
	// FileInputStream("report-template/TLFVoucher/TLFVoucher.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new
	// JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jprint,
	// "D:/temp/CashReceiptTest.pdf");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// @Test
	// public void generateTLFVoucher() {
	// try {
	// List<TLFVoucherDTO> cashReceiptDTOList =
	// paymentDAO.findTLFVoucher("CASH/1404/0000009302/001");
	// Map paramMap = new HashMap();
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(cashReceiptDTOList));
	// InputStream inputStream = new
	// FileInputStream("report-template/TLFVoucher/TLFVoucherUSD.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new
	// JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jprint,
	// "D:/temp/TLFVoucherUSDTest.pdf");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
