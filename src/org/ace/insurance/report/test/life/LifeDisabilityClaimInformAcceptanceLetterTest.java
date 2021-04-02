package org.ace.insurance.report.test.life;

public class LifeDisabilityClaimInformAcceptanceLetterTest {

	// private static Logger logger = Logger
	// .getLogger(LifeDisabilityClaimInformAcceptanceLetterTest.class);
	// private static ILifeProposalService lifeProposalService;
	// private static IFireProposalService fireProposalService;
	// private static IMotorProposalService motorProposalService;
	// private static IAcceptedInfoService acceptedInfoService;
	// private static IClaimAcceptedInfoService claimAcceptedInfoService;
	// private static ILifeDisabilityClaimService lifeDisabilityClaimService;
	// private static String dirPath = "D:/temp/";
	// private static String fileName
	// ="lifeDisabilityClaimInformAcceptanceLetter.pdf";
	//
	// @BeforeClass
	// public static void init() {
	// logger.info("CommonInformRejectLetterTest is
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
	// org.junit.runner.JUnitCore.main(LifeDisabilityClaimInformAcceptanceLetterTest.class.getName());
	//
	// }
	//
	//// @Test
	// public static void generateLifeDisabilityClaimAcceptanceLetter() {
	// LifeDisabilityClaim disabilityClaim =
	// lifeDisabilityClaimService.findDisabilityClaimByRequestId("Requestid");
	// ClaimAcceptedInfo claimAcceptedInfo =
	// claimAcceptedInfoService.findClaimAcceptedInfoByReferenceNo("referenceNo",
	// ReferenceType.LIFE_DIS_CLAIM);
	// try {
	//
	// Map paramMap = new HashMap();
	// paramMap.put("customerName",
	// disabilityClaim.getClaimInsuredPerson().getFullName());
	// paramMap.put("customerAddress",
	// disabilityClaim.getClaimInsuredPerson().getFullResidentAddress());
	// paramMap.put("policyNo", disabilityClaim.getLifePolicy().getPolicyNo());
	// paramMap.put("agent",
	// disabilityClaim.getLifePolicy().getAgent().getName());
	// paramMap.put("claimNo", disabilityClaim.getClaimRequestId());
	// paramMap.put("currentDate", claimAcceptedInfo.getInformDate());
	// paramMap.put("liabilitiesAmount",claimAcceptedInfo.getClaimAmount());
	// paramMap.put("loanAmount", disabilityClaim.getTotalLoanAmount());
	// paramMap.put("loanInterest", disabilityClaim.getTotalLoanInterest());
	// paramMap.put("renewalAmount",disabilityClaim.getTotalRenewelAmount());
	// paramMap.put("renewalInterest",disabilityClaim.getTotalRenewelInterest());
	// // paramMap.put("totalAmount", lifeClaim.getTotalNetClaimAmount());
	// paramMap.put("serviceCharges",claimAcceptedInfo.getServicesCharges());
	// paramMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
	// paramMap.put("waitingPeriod", disabilityClaim.getWaitingPeriod());
	// //
	// paramMap.put("disabilityClaimType",disabilityClaim.getDisabilityClaimType());
	// paramMap.put("publicLife", isPublicLife(disabilityClaim));
	//
	// InputStream inputStream = new
	// FileInputStream("report-template/life/lifeDisabilityClaimInformAcceptanceLetter.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport,paramMap, new
	// JREmptyDataSource());
	// FileHandler.forceMakeDirectory(dirPath);
	// String outputFile = dirPath + fileName;
	// JasperExportManager.exportReportToPdfFile(jprint, outputFile);
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR,
	// "Failed to generate LifeDisabilityClaimAcceptanceLetter", e);
	// }
	// }
	//
	// @Test
	// public void prepaeGenerateLifeDisabilityClaimAcceptanceLetter() {
	//
	// try {
	//
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", "customerName");
	// paramMap.put("customerAddress", "customerAddress");
	// paramMap.put("policyNo", "policyNo");
	// paramMap.put("agent", "agent");
	// paramMap.put("claimNo", "claimNo");
	// paramMap.put("currentDate","currentDate");
	// paramMap.put("liabilitiesAmount",1000.0);
	// paramMap.put("loanAmount", 1000.0);
	// paramMap.put("loanInterest", 1000.0);
	// paramMap.put("renewalAmount",1000.0);
	// paramMap.put("renewalInterest",1000.0);
	// paramMap.put("serviceCharges", 100.0);
	// paramMap.put("totalAmount", 1000.0);
	// paramMap.put("waitingPeriod", 1);
	// paramMap.put("publicLife", true);
	//
	// InputStream inputStream = new
	// FileInputStream("report-template/life/lifeDisabilityClaimInformAcceptanceLetter.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport,paramMap, new
	// JREmptyDataSource());
	// FileHandler.forceMakeDirectory(dirPath);
	// String outputFile = dirPath + fileName;
	// JasperExportManager.exportReportToPdfFile(jprint, outputFile);
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR,
	// "Failed to generate LifeDisabilityClaimAcceptanceLetter", e);
	// }
	// }
	//
	// private static boolean isPublicLife(LifeClaim lifeClaim) {
	// if (lifeClaim.getLifePolicy().getPolicyInsuredPersonList().size() == 1) {
	// return true;
	// }
	// return false;
	// }
}
