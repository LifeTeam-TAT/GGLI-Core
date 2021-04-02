package org.ace.insurance.report.test.life;

public class LifeInformRejectLetterTest {

	// private static Logger logger =
	// Logger.getLogger(LifeInformRejectLetterTest.class);
	// private static ILifeProposalService lifeProposalService;
	// private static IFireProposalService fireProposalService;
	// private static IMotorProposalService motorProposalService;
	// private static IAcceptedInfoService acceptedInfoService;
	//
	//
	// @BeforeClass
	// public static void init() {
	// logger.info("CommonInformRejectLetterTest is
	// started.........................................");
	// ApplicationContext context = new
	// ClassPathXmlApplicationContext("spring-beans.xml");
	// BeanFactory factory = context;
	// fireProposalService =
	// (IFireProposalService)factory.getBean("FireProposalService");
	// motorProposalService = (IMotorProposalService)
	// factory.getBean("MotorProposalService");
	// lifeProposalService = (ILifeProposalService)
	// factory.getBean("LifeProposalService");
	// acceptedInfoService = (IAcceptedInfoService)
	// factory.getBean("AcceptedInfoService");
	// logger.info("CommonInformRejectLetterTest instance has been loaded.");
	//
	// }
	//
	// @AfterClass
	// public static void finished() {
	// logger.info("CommonInformRejectLetterTest has been
	// finished.........................................");
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(LifeInformRejectLetterTest.class.getName());
	//
	// }
	//
	//// @Test
	// public void genetateLifeRejectLetter() throws Exception {
	// LifeProposal
	// lifeProposal=lifeProposalService.findLifeProposalById("ISLIF001HO000000000114062013");
	// AcceptedInfo
	// acceptedInfo=acceptedInfoService.findAcceptedInfoByReferenceNo("ISLIF001HO000000000114062013",
	// ReferenceType.LIFE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/life/LifeInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/LifeInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/LifeInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", lifeProposal.getCustomerName());
	// paramMap.put("imformedDate",acceptedInfo.getInformDate());
	// if(lifeProposal.getCustomer() !=null){
	// paramMap.put("nrc", lifeProposal.getCustomer().getIdNo());
	// }else{
	// paramMap.put("nrc", "");
	// }
	// paramMap.put("proposalNo", lifeProposal.getProposalNo());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// }
	//
	// @Test
	// public void prepareGenetateLifeRejectLetter() {
	// try{
	// InputStream inputStream = new
	// FileInputStream("report-template/life/LifeInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/LifeInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/LifeInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", "customerName");
	// paramMap.put("imformedDate", new Date());
	// paramMap.put("nrc", "nrc");
	// paramMap.put("proposalNo", "proposalNo");
	//
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR,
	// "Failed to generate LifeInformAcceptanceLetter", e);
	// }
	// }

}
