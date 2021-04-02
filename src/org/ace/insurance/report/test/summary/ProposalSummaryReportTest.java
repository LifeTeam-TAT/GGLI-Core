package org.ace.insurance.report.test.summary;

public class ProposalSummaryReportTest {
	// private static Logger logger =
	// Logger.getLogger(MotorProposalReport.class);
	// private static IProposalSummaryReportService
	// proposalSummaryReportService;
	// private SummaryReportCriteria criteria;
	//
	// @BeforeClass
	// public static void init() {
	// logger.info("MotorProposalReportTest is
	// started.........................................");
	// ApplicationContext context = new
	// ClassPathXmlApplicationContext("spring-beans.xml");
	// BeanFactory factory = context;
	// proposalSummaryReportService = (IProposalSummaryReportService)
	// factory.getBean("ProposalSummaryReportService");
	// logger.info("MotorProposalReportTest instance has been loaded.");
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(ProposalSummaryReportTest.class.getName());
	// }
	//
	// // @Test
	// public void generateProposalSummaryReport() {
	// try {
	// List<ProposalSummaryReport> proposalSummaryReports = new
	// ArrayList<ProposalSummaryReport>();
	// criteria = new SummaryReportCriteria();
	// Calendar cal = Calendar.getInstance();
	// cal.add(Calendar.DAY_OF_MONTH, -7);
	// criteria.setStartDate(cal.getTime());
	// Date endDate = new Date();
	// criteria.setEndDate(endDate);
	// cal.setTime(endDate);
	// criteria.setYear(cal.get(Calendar.YEAR));
	// criteria.setReportType("Weekly Report");
	// criteria.setLifeProductType(LifeProductType.PUBLIC_LIFE);
	// proposalSummaryReports =
	// proposalSummaryReportService.findMotorProposal(criteria);
	//
	// proposalSummaryReports =
	// proposalSummaryReportService.findMotorProposal(criteria);
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("Criteria", criteria);
	// params.put("ProposalSummaryReports", new
	// JRBeanCollectionDataSource(proposalSummaryReports));
	// InputStream inputStream = new
	// FileInputStream("report-template/summary/proposalSummaryReport.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, params, new
	// JRBeanCollectionDataSource(proposalSummaryReports));
	// JasperExportManager.exportReportToPdfFile(jprint,
	// "D:/temp/proposalSummaryReport.pdf");
	// System.out.println(proposalSummaryReports);
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate
	// proposal SummaryReport", e);
	// }
	// }
	//
	// @Test
	// public void prepareGenerateProposalSummaryReport() {
	// try {
	// List<ProposalSummaryReport> proposalSummaryReports = new
	// ArrayList<ProposalSummaryReport>();
	//
	// ProposalSummaryReport report = new ProposalSummaryReport();
	// report.setName("Fire Insurance");
	// report.setProductInfoList(getFireProductInfoList());
	//
	// ProposalSummaryReport report1 = new ProposalSummaryReport();
	// report1.setName("Life Insurance");
	// report1.setProductInfoList(getLifeProductInfoList());
	//
	// ProposalSummaryReport report2 = new ProposalSummaryReport();
	// report2.setName("Motor Insurance");
	// report2.setProductInfoList(getMotorProductInfoList());
	//
	// proposalSummaryReports.add(report);
	// proposalSummaryReports.add(report1);
	// proposalSummaryReports.add(report2);
	// Map<String, Object> params = new HashMap<String, Object>();
	// SummaryReportCriteria criteria = new SummaryReportCriteria();
	// criteria.setYear(2014);
	// criteria.setStartDate(new Date());
	// criteria.setEndDate(new Date());
	// criteria.setMonth(0);
	// criteria.setReportType("Monthly Report");
	// params.put("Criteria", criteria);
	// params.put("ProposalPremiumSummaryReports", new
	// JRBeanCollectionDataSource(proposalSummaryReports));
	// params.put("ReportTitle", "ReportTitle");
	// params.put("ReportDate", new Date());
	// InputStream inputStream = new
	// FileInputStream("report-template/summary/proposalSummaryReport.jrxml");
	// JasperReport jreport = JasperCompileManager.compileReport(inputStream);
	// JasperPrint jprint = JasperFillManager.fillReport(jreport, params, new
	// JRBeanCollectionDataSource(proposalSummaryReports));
	// JasperExportManager.exportReportToPdfFile(jprint,
	// "D:/temp/ProposalSummaryReports.pdf");
	// } catch (Exception e) {
	// throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate
	// ProposalSummary Report", e);
	// }
	// }
	//
	// public List<ProductInfo> getLifeProductInfoList() {
	// List<ProductInfo> lifeProductInfoList = new ArrayList<ProductInfo>();
	// ProposalSummaryReport p = new ProposalSummaryReport();
	//
	// ProductInfo productInfo = p.new ProductInfo();
	// productInfo.setAgentCount(1000L);
	// productInfo.setProductName("SPORTMAN");
	// productInfo.setSalemanCount(1000L);
	//
	// ProductInfo productInfo1 = p.new ProductInfo();
	// productInfo1.setAgentCount(2000L);
	// productInfo1.setProductName("GROUP LIFE");
	// productInfo1.setSalemanCount(2000L);
	//
	// ProductInfo productInfo2 = p.new ProductInfo();
	// productInfo2.setAgentCount(3000L);
	// productInfo2.setProductName("SNAKE BITE");
	// productInfo2.setSalemanCount(3000L);
	//
	// ProductInfo productInfo3 = p.new ProductInfo();
	// productInfo3.setAgentCount(4000L);
	// productInfo3.setProductName("PUBLIC LIFE");
	// productInfo3.setSalemanCount(4000L);
	//
	// lifeProductInfoList.add(productInfo);
	// lifeProductInfoList.add(productInfo1);
	// lifeProductInfoList.add(productInfo2);
	// lifeProductInfoList.add(productInfo3);
	// return lifeProductInfoList;
	// }
	//
	// public List<ProductInfo> getFireProductInfoList() {
	// List<ProductInfo> fireProductInfoList = new ArrayList<ProductInfo>();
	// ProposalSummaryReport p = new ProposalSummaryReport();
	//
	// ProductInfo productInfo = p.new ProductInfo();
	// productInfo.setAgentCount(1000L);
	// productInfo.setProductName("FURNITURE");
	// productInfo.setSalemanCount(1000L);
	//
	// ProductInfo productInfo1 = p.new ProductInfo();
	// productInfo1.setAgentCount(2000L);
	// productInfo1.setProductName("BUILDING");
	// productInfo1.setSalemanCount(2000L);
	//
	// ProductInfo productInfo2 = p.new ProductInfo();
	// productInfo2.setAgentCount(3000L);
	// productInfo2.setProductName("MACHINERY");
	// productInfo2.setSalemanCount(3000L);
	//
	// ProductInfo productInfo3 = p.new ProductInfo();
	// productInfo3.setAgentCount(4000L);
	// productInfo3.setProductName("DECLARATION POLICY");
	// productInfo3.setSalemanCount(4000L);
	//
	// ProductInfo productInfo4 = p.new ProductInfo();
	// productInfo4.setAgentCount(5000L);
	// productInfo4.setProductName("STOCK OF GOODS");
	// productInfo4.setSalemanCount(5000L);
	//
	// fireProductInfoList.add(productInfo);
	// fireProductInfoList.add(productInfo1);
	// fireProductInfoList.add(productInfo2);
	// fireProductInfoList.add(productInfo3);
	// fireProductInfoList.add(productInfo4);
	// return fireProductInfoList;
	// }
	//
	// public List<ProductInfo> getMotorProductInfoList() {
	// List<ProductInfo> motorProductInfoList = new ArrayList<ProductInfo>();
	// ProposalSummaryReport p = new ProposalSummaryReport();
	//
	// ProductInfo productInfo = p.new ProductInfo();
	// productInfo.setAgentCount(1000L);
	// productInfo.setProductName("COMMERCIAL CAR");
	// productInfo.setSalemanCount(1000L);
	//
	// ProductInfo productInfo1 = p.new ProductInfo();
	// productInfo1.setAgentCount(2000L);
	// productInfo1.setProductName("PRIVATE CAR");
	// productInfo1.setSalemanCount(2000L);
	//
	// ProductInfo productInfo2 = p.new ProductInfo();
	// productInfo2.setAgentCount(3000L);
	// productInfo2.setProductName("FA TRUCK");
	// productInfo2.setSalemanCount(3000L);
	//
	// ProductInfo productInfo3 = p.new ProductInfo();
	// productInfo3.setAgentCount(4000L);
	// productInfo3.setProductName("COMMERCIAL TRUCK");
	// productInfo3.setSalemanCount(4000L);
	//
	// ProductInfo productInfo4 = p.new ProductInfo();
	// productInfo4.setAgentCount(5000L);
	// productInfo4.setProductName("FA CAR");
	// productInfo4.setSalemanCount(5000L);
	//
	// ProductInfo productInfo5 = p.new ProductInfo();
	// productInfo5.setAgentCount(6000L);
	// productInfo5.setProductName("MOTOR CYCLE");
	// productInfo5.setSalemanCount(6000L);
	//
	// ProductInfo productInfo6 = p.new ProductInfo();
	// productInfo6.setAgentCount(7000L);
	// productInfo6.setProductName("MOBILE PLANT");
	// productInfo6.setSalemanCount(7000L);
	//
	// motorProductInfoList.add(productInfo);
	// motorProductInfoList.add(productInfo1);
	// motorProductInfoList.add(productInfo2);
	// motorProductInfoList.add(productInfo3);
	// motorProductInfoList.add(productInfo4);
	// motorProductInfoList.add(productInfo5);
	// motorProductInfoList.add(productInfo6);
	// return motorProductInfoList;
	// }
	//
	// public SummaryReportCriteria getCriteria() {
	// return criteria;
	// }
	//
	// public void setCriteria(SummaryReportCriteria criteria) {
	// this.criteria = criteria;
	// }

}
