package org.ace.insurance.report.test.life;

public class LifeDisabilityClaimrDischargeCertificateTest {

	// private static Logger logger =
	// Logger.getLogger(LifeDisabilityClaimrDischargeCertificateTest.class);
	// private static ILifeProposalService lifeProposalService;
	// private static IFireProposalService fireProposalService;
	// private static IMotorProposalService motorProposalService;
	// private static IAcceptedInfoService acceptedInfoService;
	// private static IClaimAcceptedInfoService claimAcceptedInfoService;
	// private static ILifeDisabilityClaimService lifeDisabilityClaimService;
	//
	// @BeforeClass
	// public static void init() {
	// logger.info("LifeDisabilityClaimrDischargeCertificateTest is
	// started.........................................");
	// ApplicationContext context = new
	// ClassPathXmlApplicationContext("spring-beans.xml");
	// BeanFactory factory = context;
	// fireProposalService = (IFireProposalService)
	// factory.getBean("FireProposalService");
	// motorProposalService = (IMotorProposalService)
	// factory.getBean("MotorProposalService");
	// lifeProposalService = (ILifeProposalService)
	// factory.getBean("LifeProposalService");
	// acceptedInfoService = (IAcceptedInfoService)
	// factory.getBean("AcceptedInfoService");
	// lifeDisabilityClaimService = (ILifeDisabilityClaimService)
	// factory.getBean("LifeDisabilityClaimService");
	// logger.info("LifeDisabilityClaimrDischargeCertificateTest instance has
	// been loaded.");
	//
	// }
	//
	// @AfterClass
	// public static void finished() {
	// logger.info("LifeDisabilityClaimrDischargeCertificateTest has been
	// finished.........................................");
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(LifeDisabilityClaimrDischargeCertificateTest.class.getName());
	//
	// }
	//
	// @Test
	// public void generateLifeDisabilityClaimDischargeCertificate() {
	// LifeClaimDischargeFormDTO discharge = new LifeClaimDischargeFormDTO();
	// discharge.setPolicyNo("policyNo");
	// discharge.setCustomerName("customerName");
	// discharge.setClaimAmount(5000000000.00);
	// discharge.setSumInsured(5000000000.00);
	// discharge.setPresentDate(new Date());
	// discharge.setBeneficiaryName("beneficiaryName");
	// discharge.setCommenmanceDate(new Date());
	// discharge.setRenewelAmount(2000000000.00);
	// discharge.setRenewelInterest(100000000.00);
	// discharge.setLoanAmount(15000000);
	// discharge.setServiceCharges(4000000.00);
	// discharge.setNetClaimAmount(300000000.00);
	// discharge.setIdNo("12/784552");
	// discharge.setFatherName("Dad Dad");
	// discharge.setAddress("address");
	// try {
	// List jasperPrintList = new ArrayList();
	// Map paramMap = new HashMap();
	//
	// String presentDate = Utils.formattedDate(discharge.getPresentDate());
	// String liabilitiesAmount =
	// Utils.formattedCurrency(discharge.getClaimAmount());
	// paramMap.put("policyNo", discharge.getPolicyNo());
	// paramMap.put("customerName", discharge.getCustomerName());
	// paramMap.put("liabilitiesAmount", liabilitiesAmount);
	// paramMap.put("sumInsured", discharge.getSumInsured());
	// paramMap.put("presentDate", presentDate);
	//
	// paramMap.put("beneficiaryName", discharge.getBeneficiaryName());
	// // paramMap.put("disabilityDate",);
	// paramMap.put("commencementDate", discharge.getCommenmanceDate());
	// paramMap.put("premium", discharge.getRenewelAmount());
	// paramMap.put("renewelInterest", discharge.getRenewelInterest());
	// paramMap.put("loanAmount", discharge.getLoanAmount());
	// paramMap.put("loanInterest", discharge.getLoanInterest());
	// paramMap.put("serviceCharges", discharge.getServiceCharges());
	// paramMap.put("netAmount", discharge.getNetClaimAmount());
	// paramMap.put("witnessName", " ");
	// paramMap.put("witnessAddress", " ");
	// paramMap.put("nrc", discharge.getIdNo());
	// paramMap.put("fatherName", discharge.getFatherName());
	// paramMap.put("customerAddress", discharge.getAddress());
	//
	// InputStream inputStream = new
	// FileInputStream("report-template/life/dischargeCertificate.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new
	// JREmptyDataSource());
	// jasperPrintList.add(jprint);
	// jasperListPDFExport(jasperPrintList, "D:/temp/",
	// "dischargeCertificate.pdf");
	// /*
	// * FileHandler.forceMakeDirectory(dirPath); String outputFile =
	// * dirPath + fileName;
	// * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
	// */
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate
	// DischargeCertificate", e);
	// }
	// }
	//
	// private static void jasperListPDFExport(List jasperPrintList, String
	// dirPath, String fileName) {
	// JRExporter exporter = new JRPdfExporter();
	// exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
	// jasperPrintList);
	// try {
	// FileHandler.forceMakeDirectory(dirPath);
	// OutputStream outputStream = new FileOutputStream(dirPath + fileName);
	// exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM,
	// outputStream);
	// exporter.exportReport();
	// outputStream.close();
	// } catch (IOException ie) {
	// ie.printStackTrace();
	// } catch (JRException je) {
	// je.printStackTrace();
	// }
	// }

}
