package org.ace.insurance.report.test;

public class ReportTest {
	// private static Logger logger =
	// Logger.getLogger(MotorProposalServiceTest.class);
	// private static ISalesReportService salesReportService;
	// private static IFireProposalReportService fireProposalReportService;
	// private static IFirePolicyReportService firePolicyReportService;
	// private static IFireDailyIncomeReportService
	// fireDailyIncomeReportService;
	// private static IFirePremiumPaymentReportService
	// premiumPaymentReportService;
	//
	// @BeforeClass
	// public static void init() {
	// logger.info("ReportTest is
	// started.........................................");
	// ApplicationContext context = new
	// ClassPathXmlApplicationContext("spring-beans.xml");
	// BeanFactory factory = context;
	// salesReportService = (ISalesReportService)
	// factory.getBean("SalesReportService");
	// fireProposalReportService = (IFireProposalReportService)
	// factory.getBean("FireProposalReportService");
	// firePolicyReportService = (IFirePolicyReportService)
	// factory.getBean("FirePolicyReportService");
	// fireDailyIncomeReportService = (IFireDailyIncomeReportService)
	// factory.getBean("FireDailyIncomeReportService");
	// premiumPaymentReportService = (IFirePremiumPaymentReportService)
	// factory.getBean("FirePremiumPaymentReportService");
	// logger.info("ReportTest instance has been loaded.");
	//
	// }
	//
	// @AfterClass
	// public static void finished() {
	// logger.info("ReportTest has been
	// finished.........................................");
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(ReportTest.class.getName());
	// }
	//
	// // @Test
	// public void genetateFirePremiumPaymentReport() throws Exception {
	// List<FirePremiumPaymentReport> firePremiumPaymentList =
	// premiumPaymentReportService.findPremiumPayment(new
	// FirePremiumPaymentCriteria());
	// InputStream inputStream = new
	// FileInputStream("report-template/firePremiumPaymentReport.jrxml");
	// String outputFilePdf = "D:/temp/firePremiumPaymentReport.pdf";
	// // String outputFileHtml = "D:/temp/fireProposalReport.html";
	// Map paramMap = new HashMap();
	// paramMap.put("ReportTitle", "Fire Premium Payment Report");
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(firePremiumPaymentList));
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JRBeanCollectionDataSource(firePremiumPaymentList));
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// // JasperExportManager.exportReportToHtmlFile(jasperPrint,
	// // outputFileHtml);
	// }
	//
	// // @Test
	// public void genetateFireDailyIncomeReport() throws Exception {
	// List<FireDailyIncomeReport> fireDailyIncomeReportList =
	// fireDailyIncomeReportService.findFireDailyIncome(new
	// FireDailyIncomeReportCriteria());
	// InputStream inputStream = new
	// FileInputStream("report-template/fireDailyIncomeReport.jrxml");
	// String outputFilePdf = "D:/temp/fireDailyIncomeReport.pdf";
	// // String outputFileHtml = "D:/temp/fireProposalReport.html";
	// Map paramMap = new HashMap();
	// paramMap.put("ReportTitle", "Fire Daily Income Report");
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(fireDailyIncomeReportList));
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JRBeanCollectionDataSource(fireDailyIncomeReportList));
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// // JasperExportManager.exportReportToHtmlFile(jasperPrint,
	// // outputFileHtml);
	// }
	//
	// // @Test
	// public void genetateFireProposalReport() throws Exception {
	// List<FireProposalReport> fireProposalList =
	// fireProposalReportService.findFireProposal(new FireProposalCriteria());
	// System.out.println("sizeTest" + fireProposalList.size());
	// InputStream inputStream = new
	// FileInputStream("report-template/fireProposalReport.jrxml");
	// String outputFilePdf = "D:/temp/fireProposalReport.pdf";
	// // String outputFileHtml = "D:/temp/fireProposalReport.html";
	// Map paramMap = new HashMap();
	// paramMap.put("ReportTitle", "Fire Proposal Report");
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(fireProposalList));
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JRBeanCollectionDataSource(fireProposalList));
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// // JasperExportManager.exportReportToHtmlFile(jasperPrint,
	// // outputFileHtml);
	// }
	//
	// // @Test
	// public void report() throws Exception {
	// List<SalesReport> saleReports = salesReportService.findSalesReport(new
	// SalesReportCriteria());
	// String outputFilePathPdf = "D:/temp/BasicReport.pdf";
	// String outputFilePathHtml = "D:/temp/BasicReport.html";
	// Map paramMap = new HashMap();
	// paramMap.put("ReportTitle", "Sale Report");
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(saleReports));
	//
	// InputStream inputStream = new
	// FileInputStream("report-template/saleReportTemplate.jrxml");
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JRBeanCollectionDataSource(saleReports));
	// JasperExportManager.exportReportToPdfFile(jasperPrint,
	// outputFilePathPdf);
	// // JasperExportManager.exportReportToHtmlFile(jasperPrint,
	// // outputFilePathHtml);
	// }
	//
	// // @Test
	// public void generateLifePolicyIssueSingle() throws Exception {
	// // String exp =
	// // ""+" - "+""+"% -
	// "+((org.ace.insurance.system.common.relationship.RelationShip)new
	// // Object()).getName();
	//
	// // final String templateFileName1 = "lifePolicyIssueEnquiryCover.jrxml";
	// final String templateFileName2 = "lifePolicyIssue.jrxml";
	// // final String templatePath1 = "/report-template/" + templateFileName1;
	// final String templatePath2 =
	// "D:/dev/workspace/insurance-cus/ggip/report-template/" +
	// templateFileName2;
	// final String outputFileName = "lifePolicyIssue.pdf";
	// final String outputFilePath = "D:/temp/" + outputFileName;
	//
	// // Map paramIssueCover = new HashMap();
	// // paramIssueCover.put("policyNo", "GGLE/1308/00000000008/HO");
	// // paramIssueCover.put("policyNo", "U Aung Myo Thura");
	// // paramIssueCover.put("policyNo", "5000000");
	//
	// List<PolicyInsuredPersonBeneficiaries>
	// policyInsuredPersonBeneficiariesList = new
	// ArrayList<PolicyInsuredPersonBeneficiaries>();
	//
	// Map paramIssueDetails = new HashMap();
	// paramIssueDetails.put("policyNo", "CASH/1208/0000000001/HO");
	// paramIssueDetails.put("receiptNo", "GGLE/1208/0000000001/HO");
	// paramIssueDetails.put("commenmanceDate", new Date());
	// paramIssueDetails.put("agent", "Zaw Than Oo" + "LIC0001");
	// paramIssueDetails.put("productName", "PUBLIC LIFE");
	// paramIssueDetails.put("periodYears", 10);
	// paramIssueDetails.put("sumInsured", 5000000.00);
	// Properties p = new Properties();
	// InputStream input;
	// try {
	// input = new
	// FileInputStream("D:/komoe/ggip/resources/LANGUAGE_en.properties");
	// p.load(input);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// String values = p.getProperty("aa");
	// System.out.println("myanmar sar is ****" + values);
	// paramIssueDetails.put("customerName", values);
	// paramIssueDetails.put("occupation", "Programmer");
	// paramIssueDetails.put("customerAddress", "No-3, 81th Street, Thait Phan
	// Road, Ahlone Township, Yangon, Myanmar");
	// paramIssueDetails.put("ageForNextYear", 28);
	// paramIssueDetails.put("totalPremium", 513150.00);
	// paramIssueDetails.put("totalTermPremium", 256575.00);
	// paramIssueDetails.put("paymentType", 6);
	// paramIssueDetails.put("endDate", new Date(2013, 12, 30));
	// paramIssueDetails.put("lastPaymentDate", new Date(2013, 8, 12));
	// paramIssueDetails.put("policyInsuredPersonBeneficiariesList",
	// populateBeneficiaries());
	// paramIssueDetails.put("timeSlotList", populateTimeSlots());
	// Map paramMap = new HashMap();
	// paramMap.put("policyNo", "CASH/1208/0000000001/HO");
	// paramMap.put("agent", "Zaw Than Oo" + "LIC0001");
	// paramMap.put("listDataSource", new
	// JRBeanCollectionDataSource(populateBeneficiaries()));
	// InputStream inputStream = new
	// FileInputStream("report-template/life/publicLifePolicyIssue.jrxml");
	// InputStream inputStream1 = new
	// FileInputStream("report-template/lifePolicyRelationship.jrxml");
	// JasperReport jreport1 = JasperCompileManager.compileReport(inputStream);
	// JasperReport jreport2 = JasperCompileManager.compileReport(inputStream1);
	// JasperPrint jprint1 = JasperFillManager.fillReport(jreport1,
	// paramIssueDetails, new JREmptyDataSource());
	// JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramMap,
	// new JREmptyDataSource());
	// List pages = jprint2.getPages();
	// for (int j = 0; j < pages.size(); j++) {
	// JRPrintPage object = (JRPrintPage) pages.get(j);
	// jprint1.addPage(object);
	//
	// }
	// // JasperViewer.viewReport(jprint1,false);
	// JasperExportManager.exportReportToPdfFile(jprint1,
	// "D:/temp/lifePolicyIssueCover.pdf");
	// /*
	// * JRExporter exporter = new JRPdfExporter();
	// *
	// * List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
	// * jprintlist.add(jprint1); jprintlist.add(jprint2);
	// * exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
	// * jprintlist);
	// *
	// * OutputStream output = new FileOutputStream(new File(outputFilePath));
	// * exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, output);
	// *
	// * exporter.exportReport();
	// */
	// }
	//
	// private List<Date> populateTimeSlots() {
	// List<Date> timeSlotList = new ArrayList<Date>();
	// timeSlotList.add(Utils.getDate("20-01-2013"));
	// timeSlotList.add(Utils.getDate("20-04-2013"));
	// timeSlotList.add(Utils.getDate("20-08-2013"));
	// timeSlotList.add(Utils.getDate("20-012-2013"));
	// return timeSlotList;
	// }
	//
	// private List<PolicyInsuredPersonBeneficiaries> populateBeneficiaries() {
	// Properties p = new Properties();
	// InputStream input;
	// try {
	// input = new
	// FileInputStream("D:/komoe/ggip/resources/LANGUAGE_en.properties");
	// p.load(input);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// String values = p.getProperty("aa");
	// System.out.println("myanmar sar is ****" + values);
	// List<PolicyInsuredPersonBeneficiaries> ret = new
	// ArrayList<PolicyInsuredPersonBeneficiaries>();
	// PolicyInsuredPersonBeneficiaries person1 = populateBeneficiary(values,
	// "Hla", 90f, "FATHER", "Arr Pu Kyii");
	// PolicyInsuredPersonBeneficiaries person2 = populateBeneficiary("Mg Zaw",
	// "Htun", 90f, "FATHER", "Arr Pu Kyii");
	// PolicyInsuredPersonBeneficiaries person3 = populateBeneficiary("Mg Zaw
	// Zaw Zaw", "Htun", 90f, "FATHER", "Arr Pu Kyii");
	// ret.add(person1);
	// ret.add(person2);
	// ret.add(person3);
	// return ret;
	// }
	//
	// private PolicyInsuredPersonBeneficiaries populateBeneficiary(String
	// firstName, String lastName, float percentage, String relationshipName,
	// String relationshipDescription) {
	// PolicyInsuredPersonBeneficiaries person = new
	// PolicyInsuredPersonBeneficiaries();
	// Name name = new Name();
	// name.setFirstName(firstName);
	// name.setLastName(lastName);
	//
	// RelationShip relationship = new RelationShip();
	// relationship.setName(relationshipName);
	// relationship.setDescription(relationshipDescription);
	//
	// person.setName(name);
	// person.setPercentage(percentage);
	// person.setRelationship(relationship);
	// return person;
	// }
	//
	// @Test
	// public void genetateFirePolicyReport() throws Exception {
	// List<FirePolicyReport> firePolicyList =
	// firePolicyReportService.findFirePolicy(new FirePolicyReportCriteria());
	// List<FirePolicyReport> temp = new ArrayList<FirePolicyReport>();
	// // JRRewindableDataSource
	// System.out.println("sizeTest" + firePolicyList.size());
	// InputStream inputStream = new
	// FileInputStream("report-template/firePolicyReport.jrxml");
	// InputStream inputStream1 = new
	// FileInputStream("report-template/firePolicyReport_subreport.jrxml");
	// InputStream inputStream2 = new
	// FileInputStream("report-template/firePolicyReport_subreport1.jrxml");
	// String outputFilePdf = "D:/temp/firePolicyReport.pdf";
	// // String outputFileHtml = "D:/temp/firePolicyReport.html";
	// Map paramMap = new HashMap();
	// paramMap.put("ReportTitle", "Fire Policy Report");
	// paramMap.put("TableDataSource", new JRBeanCollectionDataSource(temp));
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperDesign jasperDesign1 = JRXmlLoader.load(inputStream1);
	// JasperDesign jasperDesign2 = JRXmlLoader.load(inputStream2);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// // JasperReport jasperReport1 =
	// // JasperCompileManager.compileReport(jasperDesign1);
	// // JasperReport jasperReport2 =
	// // JasperCompileManager.compileReport(jasperDesign2);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JRBeanCollectionDataSource(temp));
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// // JasperExportManager.exportReportToHtmlFile(jasperPrint,
	// // outputFileHtml);
	// }
}
