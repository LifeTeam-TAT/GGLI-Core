package org.ace.insurance.report.test.travel;

public class TravelCashReceiptTest {
	// private static Logger logger =
	// Logger.getLogger(MotorCashReceiptTest.class);
	// private static ITravelProposalService travelProposalService;
	// private static IPaymentService paymentService;
	//
	// @BeforeClass
	// public static void init() {
	// logger.info("ReportTest is
	// started.........................................");
	// ApplicationContext context = new
	// ClassPathXmlApplicationContext("spring-beans.xml");
	// BeanFactory factory = context;
	// travelProposalService = (ITravelProposalService)
	// factory.getBean("TravelProposalService");
	// paymentService = (IPaymentService) factory.getBean("PaymentService");
	// logger.info("ReportTest instance has been loaded.");
	//
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(TravelCashReceiptTest.class.getName());
	// }
	//
	// // @Test
	// public void test() {
	// TravelProposal travelProposal =
	// travelProposalService.findTravelProposalById("ISTRA001001000000038107072014");
	// List<Payment> payment =
	// paymentService.findByProposal("ISTRA001001000000038107072014",
	// PolicyReferenceType.TRAVEL_PROPOSAL, null);
	// System.out.println("paymentDTO" + payment.get(0).getPaymentChannel());
	// System.out.println("paymentDTO" + payment.get(0).getReceiptNo());
	// // generateMotorCashReceipt(motorProposal, paymentDTO, null);
	// }
	//
	// // public static <T> void generateMotorCashReceipt(T motorInfo,
	// PaymentDTO
	// // payment, CoinsuranceCashReceiptDTO coinsuranceDTO) {
	// // MotorProposal motorProposal = null;
	// // boolean isProposal = false;
	// // String registrationNo = null;
	// // String policyType = null;
	// // String proposalNo = (isProposal) ? motorProposal.getProposalNo() :
	// // motorPolicy.getMotorProposal().getProposalNo();
	// // String paymentTypeName = (isProposal) ?
	// // motorProposal.getPaymentType().getName() :
	// // motorPolicy.getPaymentType().getName();
	// // double sumInsured = (isProposal) ? motorProposal.getSumInsured() :
	// // motorPolicy.getMotorProposal().getSumInsured();
	// // double totalPremium = (isProposal) ? motorProposal.getTotalPremium() :
	// // motorPolicy.getMotorProposal().getTotalPremium();
	// // try {
	// // List jasperPrintList = new ArrayList();
	// // Map paramMap = new HashMap();
	// // if (isProposal && motorProposal.getMotorPolicy() != null) {
	// // paramMap.put("premiumName", "Endorsement Premium");
	// // paramMap.put("addOnPremiumName", "Endorsement Additional Premium");
	// // } else {
	// // paramMap.put("premiumName", "Premium");
	// // paramMap.put("addOnPremiumName", "Additional Premium");
	// // }
	// // if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
	// // paramMap.put("chequeNo", payment.getChequeNo());
	// // paramMap.put("bank", payment.getBank().getName());
	// // paramMap.put("chequePayment", Boolean.TRUE);
	// // paramMap.put("receiptType", "Temporary Receipt");
	// // paramMap.put("receiptName", "Receipt No");
	// // } else {
	// // paramMap.put("chequePayment", Boolean.FALSE);
	// // paramMap.put("receiptType", "Cash Receipt");
	// // paramMap.put("receiptName", "Cash Receipt No");
	// // }
	// // paramMap.put("proposalNo", proposalNo);
	// // paramMap.put("cashReceiptNo", payment.getReceiptNo());
	// // paramMap.put("sumInsured", sumInsured);
	// // paramMap.put("premium", payment.getBasicPremium());
	// // paramMap.put("discountAmount", payment.getDiscountAmount());
	// // paramMap.put("addOnPremium", payment.getAddOnPremium());
	// // paramMap.put("netPremium", payment.getNetPremium());
	// // paramMap.put("serviceCharges", payment.getServicesCharges());
	// // paramMap.put("stempFees", payment.getStampFees());
	// // paramMap.put("totalAmount", payment.getTotalAmount());
	// // paramMap.put("paymentType", paymentTypeName);
	// // paramMap.put("registrationNo", registrationNo);
	// // paramMap.put("policyType", policyType);
	// // paramMap.put("confirmDate", payment.getConfirmDate());
	// // InputStream inputStream = new
	// // FileInputStream("report-template/travel/travelCashReceipt.jrxml");
	// // JasperReport jreport =
	// JasperCompileManager.compileReport(inputStream);
	// // JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap,
	// new
	// // JREmptyDataSource());
	// // jasperPrintList.add(jprint);
	// //
	// // JRExporter exporter = new JRPdfExporter();
	// // exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
	// // jasperPrintList);
	// // exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new
	// // FileOutputStream("D:/temp/MotorCashReceipt.pdf"));
	// // exporter.exportReport();
	// // } catch (Exception e) {
	// // throw new SystemException(ErrorCode.SYSTEM_ERROR,
	// // "Failed to generate MotorCashReceipt", e);
	// // }
	// // }
	//
	// private static void jasperListPDFExport(List jasperPrintList, String
	// dirPath, String fileName) {
	// JRExporter exporter = new JRPdfExporter();
	// exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
	// jasperPrintList);
	// try {
	// FileHandler.forceMakeDirectory(dirPath);
	// exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new
	// FileOutputStream(dirPath + fileName));
	// exporter.exportReport();
	// } catch (IOException ie) {
	// ie.printStackTrace();
	// } catch (JRException je) {
	// je.printStackTrace();
	// }
	// }
	//
	// @Test
	// public void prepareTest() throws IOException {
	// try {
	// TravelProposal travelProposal =
	// travelProposalService.findTravelProposalById("ISTRA001001000000000118092014
	// ");
	// List<Payment> payment =
	// paymentService.findByProposal("ISTRA001001000000000118092014 ",
	// PolicyReferenceType.TRAVEL_PROPOSAL, null);
	//
	// List jasperPrintList = new ArrayList();
	// Map paramMap = new HashMap();
	// paramMap.put("proposalNo", travelProposal.getProposalNo());
	// paramMap.put("cashReceiptNo", payment.get(0).getReceiptNo());
	// paramMap.put("receiptType", "Cash Receipt");
	// paramMap.put("receiptName", "Cash Receipt No");
	// paramMap.put("chequeNo", payment.get(0).getChequeNo());
	// if (payment.get(0).getBank() != null) {
	// paramMap.put("bank", payment.get(0).getBank().getName());
	// }
	// paramMap.put("premium", travelProposal.getTotalNetPremium());
	// paramMap.put("discountAmount", payment.get(0).getDiscountAmount());
	// paramMap.put("netPremium", travelProposal.getTotalNetPremium());
	// paramMap.put("paymentType", travelProposal.getPaymentType().getName());
	// paramMap.put("numberOfTravelExpress",
	// travelProposal.getExpressList().size());
	// paramMap.put("numberOfPassenger", travelProposal.getTotalPassenger());
	// paramMap.put("numberOfUnit", travelProposal.getTotalUnit());
	// paramMap.put("serviceCharges", payment.get(0).getServicesCharges());
	// paramMap.put("stempFees", payment.get(0).getStampFees());
	// paramMap.put("commission", travelProposal.getTotalCommission());
	// paramMap.put("totalAmount", travelProposal.getTotalNetPremium());
	// paramMap.put("confirmDate", new Date());
	// paramMap.put("chequePayment", false);
	// InputStream inputStream = new
	// FileInputStream("report-template/travel/travelCashReceipt.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new
	// JREmptyDataSource());
	//
	// Map paramMap2 = new HashMap();
	// paramMap2.put("branch", travelProposal.getBranch().getDescription());
	// paramMap2.put("totalPassenger", travelProposal.getTotalPassenger());
	// paramMap2.put("totalUnit", travelProposal.getTotalUnit());
	// paramMap2.put("totalNetPremium", travelProposal.getTotalNetPremium());
	// paramMap2.put("fromDate", Utils.getDateFormatString(new Date()));
	// paramMap2.put("toDate", Utils.getDateFormatString(new Date()));
	// paramMap2.put("Date", new Date());
	// paramMap2.put("TableDataSource", new
	// JRBeanCollectionDataSource(travelProposal.getExpressList()));
	// InputStream inputStream2 = new
	// FileInputStream("report-template/travel/travelCashReceipt2.jrxml");
	// JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
	// JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramMap2,
	// new JREmptyDataSource());
	// jasperPrintList.add(jprint);
	// jasperPrintList.add(jprint2);
	// JRExporter exporter = new JRPdfExporter();
	// exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
	// jasperPrintList);
	// FileHandler.forceMakeDirectory("D:/temp");
	// OutputStream outputStream = new
	// FileOutputStream("D:/temp/TravelCashReceipt.pdf");
	// exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM,
	// outputStream);
	// exporter.exportReport();
	// outputStream.close();
	//
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (JRException e) {
	// e.printStackTrace();
	// }
	// }
}
