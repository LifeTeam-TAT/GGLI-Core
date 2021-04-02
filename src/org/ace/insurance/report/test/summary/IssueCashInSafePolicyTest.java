package org.ace.insurance.report.test.summary;

public class IssueCashInSafePolicyTest {
	// private static Logger logger =
	// Logger.getLogger(IssueCashInSafePolicyTest.class);
	// private static ICashInSafeProposalService cashInSafeProposalService;
	//
	// @BeforeClass
	// public static void init() {
	// logger.info("IssueCashInSafePolicyTest is
	// started.........................................");
	// ApplicationContext context = new
	// ClassPathXmlApplicationContext("spring-beans.xml");
	// BeanFactory factory = context;
	// cashInSafeProposalService = (ICashInSafeProposalService)
	// factory.getBean("CashInSafeProposalService");
	// logger.info("IssueCashInSafePolicyTest instance has been loaded.");
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(IssueCashInSafePolicyTest.class.getName());
	// }
	//
	// // @Test
	// public void generateIssueCashInSafePolicyTest() {
	// try {
	// CashInSafeProposal cashInSafeProposal =
	// cashInSafeProposalService.findCashInSafeProposalById("ISFIR047001000000001108012014");
	// Map paramMap = new HashMap();
	// paramMap.put("policyNo", cashInSafeProposal.getPolicyNo());
	// paramMap.put("totalSumInsured", cashInSafeProposal.getSumInsured());
	// paramMap.put("basicPremium", cashInSafeProposal.getPremium());
	// if (cashInSafeProposal.getAgent() != null) {
	// paramMap.put("agentName", cashInSafeProposal.getAgent().getName());
	// paramMap.put("agentLiscenseNo",
	// cashInSafeProposal.getAgent().getLiscenseNo());
	// } else {
	// paramMap.put("agentName", "");
	// paramMap.put("agentLiscenseNo", "-");
	// }
	// paramMap.put("customerName", cashInSafeProposal.getCustomerName());
	// paramMap.put("customerAddress", cashInSafeProposal.getCustomerAddress());
	// paramMap.put("fullPropertyLocation",
	// cashInSafeProposal.getInsuredPropertyAddress());
	// paramMap.put("fromDate", cashInSafeProposal.getCommenmanceDate());
	// paramMap.put("toDate", cashInSafeProposal.getPolicyEndDate());
	// paramMap.put("informDate", new Date());
	// InputStream inputStream = new
	// FileInputStream("report-template/fire/issueCashInSafePolicy.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new
	// JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jprint,
	// "D:/temp/IssueCashInSafePolicy.pdf");
	//
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate
	// CashInSafe Policy", e);
	// }
	// }
	//
	// @Test
	// public void prepareGenerateIssueCashInSafePolicyTest() {
	// try {
	// Map paramMap = new HashMap();
	// paramMap.put("policyNo", "policyNo");
	// paramMap.put("totalSumInsured", 1000.0);
	// paramMap.put("basicPremium", 100.0);
	// paramMap.put("agentName", "Aung Than Naing");
	// paramMap.put("agentLiscenseNo", "agentLiscenseNo");
	// paramMap.put("customerName", "customerName");
	// paramMap.put("customerAddress", "customerAddress");
	// paramMap.put("fullPropertyLocation", "fullPropertyLocation");
	// paramMap.put("fromDate", new Date());
	// paramMap.put("toDate", new Date());
	// paramMap.put("informDate", new Date());
	// InputStream inputStream = new
	// FileInputStream("report-template/fire/issueCashInSafePolicy.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new
	// JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jprint,
	// "D:/temp/IssueCashInSafePolicy.pdf");
	//
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate
	// CashInSafe Policy", e);
	// }
	// }

}
