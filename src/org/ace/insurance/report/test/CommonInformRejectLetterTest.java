package org.ace.insurance.report.test;

import org.ace.java.web.common.BaseBean;

public class CommonInformRejectLetterTest extends BaseBean {
	// private static Logger logger =
	// Logger.getLogger(CommonInformRejectLetterTest.class);
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
	//
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
	//
	// // @Test
	// public void genetateLifeRejectLetter() throws Exception {
	// LifeProposal
	// lifeProposal=lifeProposalService.findLifeProposalById("ISLIF001HO000000088013072013");
	// AcceptedInfo
	// acceptedInfo=acceptedInfoService.findAcceptedInfoByReferenceNo("ISLIF001HO000000088013072013",
	// ReferenceType.LIFE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/life/LifeInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/LifeInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/LifeInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", lifeProposal.getCustomerName());
	// paramMap.put("imformedDate",acceptedInfo.getInformDate());
	// paramMap.put("nrc", lifeProposal.getCustomer().getIdNo());
	// paramMap.put("proposalNo", lifeProposal.getProposalNo());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// }
	// @Test
	// public void genetateFireRejectLetter() throws Exception {
	// FireProposal
	// fireProposal=fireProposalService.findFireProposalById("ISFIR0010001000000000210062013");
	// AcceptedInfo
	// acceptedInfo=acceptedInfoService.findAcceptedInfoByReferenceNo("ISFIR0010001000000000210062013",
	// ReferenceType.FIRE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/fire/FireInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/FireInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/FireInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", fireProposal.getCustomerName());
	// paramMap.put("imformedDate",acceptedInfo.getInformDate());
	// paramMap.put("nrc", fireProposal.getCustomer().getIdNo());
	// paramMap.put("proposalNo", fireProposal.getProposalNo());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// }
	// // @Test
	// public void genetateMotorRejectLetter() throws Exception {
	// MotorProposal
	// motorProposal=motorProposalService.findMotorProposalById("ISMOT0010001000000000110062013");
	// AcceptedInfo
	// acceptedInfo=acceptedInfoService.findAcceptedInfoByReferenceNo("ISMOT0010001000000000110062013",
	// ReferenceType.MOTOR_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/motor/MotorInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/MotorInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/MotorInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", motorProposal.getCustomerName());
	// paramMap.put("imformedDate",acceptedInfo.getInformDate());
	// paramMap.put("nrc", motorProposal.getCustomer().getIdNo());
	// paramMap.put("proposalNo", motorProposal.getProposalNo());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(CommonInformRejectLetterTest.class.getName());
	// }

}
