package org.ace.insurance.report.test;

public class CommonInformRejectAndAcceptanceLetterTest {
	// private static Logger logger =
	// Logger.getLogger(CommonInformRejectAndAcceptanceLetterTest.class);
	// private static ILifeProposalService lifeProposalService;
	// private static IFireProposalService fireProposalService;
	// private static IMotorProposalService motorProposalService;
	// private static IAcceptedInfoService acceptedInfoService;
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
	// // @Test
	// public void genetateLifeRejectLetter() throws Exception {
	// LifeProposal lifeProposal =
	// lifeProposalService.findLifeProposalById("ISLIF001HO000000000114062013");
	// AcceptedInfo acceptedInfo =
	// acceptedInfoService.findAcceptedInfoByReferenceNo("ISLIF001HO000000000114062013",
	// ReferenceType.LIFE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/life/LifeInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/LifeInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/LifeInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", lifeProposal.getCustomerName());
	// paramMap.put("imformedDate", acceptedInfo.getInformDate());
	// if (lifeProposal.getCustomer() != null) {
	// paramMap.put("nrc", lifeProposal.getCustomer().getIdNo());
	// } else {
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
	// // @Test
	// public void genetateFireRejectLetter() throws Exception {
	// FireProposal fireProposal =
	// fireProposalService.findFireProposalById("ISFIR001HO000000002212062013");
	// AcceptedInfo acceptedInfo =
	// acceptedInfoService.findAcceptedInfoByReferenceNo("ISFIR001HO000000002212062013",
	// ReferenceType.FIRE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/fire/FireInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/FireInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/FireInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", fireProposal.getCustomerName());
	// paramMap.put("imformedDate", acceptedInfo.getInformDate());
	// if (fireProposal.getCustomer() != null) {
	// paramMap.put("nrc", fireProposal.getCustomer().getIdNo());
	// } else {
	// paramMap.put("nrc", "");
	// }
	// paramMap.put("proposalNo", fireProposal.getProposalNo());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// }
	//
	// // @Test
	// public void genetateMotorRejectLetter() throws Exception {
	// MotorProposal motorProposal =
	// motorProposalService.findMotorProposalById("ISMOT0010001000000000110062013");
	// AcceptedInfo acceptedInfo =
	// acceptedInfoService.findAcceptedInfoByReferenceNo("ISMOT0010001000000000110062013",
	// ReferenceType.MOTOR_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/motor/MotorInformRejectLetter.jrxml");
	// String outputFilePdf = "D:/temp/MotorInformRejectLetter.pdf";
	// String outputFileHtml = "D:/temp/MotorInformRejectLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", motorProposal.getCustomerName());
	// paramMap.put("imformedDate", acceptedInfo.getInformDate());
	// if (motorProposal.getCustomer() != null) {
	// paramMap.put("nrc", motorProposal.getCustomer().getIdNo());
	// } else {
	// paramMap.put("nrc", "");
	// }
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
	// // @Test
	// public void genetateLifeAcceptanceLetter() throws Exception {
	//
	// LifeProposal lifeProposal =
	// lifeProposalService.findLifeProposalById("ISLIF001HO000000000114062013");
	// AcceptedInfo acceptedInfo =
	// acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposal.getId(),
	// ReferenceType.LIFE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/life/LifeInformAcceptanceLetter.jrxml");
	// String outputFilePdf = "D:/temp/LifeInformAcceptanceLetter.pdf";
	// Map paramMap = new HashMap();
	// if (isPublicLife(lifeProposal)) {
	// paramMap.put("publicLife", true);
	// } else {
	// paramMap.put("publicLife", false);
	// }
	// paramMap.put("customerName", lifeProposal.getCustomerName());
	// paramMap.put("agent", lifeProposal.getAgent() == null ? "(\t)" :
	// lifeProposal.getAgent().getName());
	// paramMap.put("proposalNo", lifeProposal.getProposalNo());
	// paramMap.put("periodYears",
	// lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
	// paramMap.put("approvedSumInsured", lifeProposal.getApprovedSumInsured());
	// paramMap.put("totalPremium", acceptedInfo.getTotalPremium());
	// paramMap.put("discountAmount", acceptedInfo.getDiscountAmount());
	// paramMap.put("netPremium", acceptedInfo.getNetPremium());
	// DecimalFormat formatter1 = new DecimalFormat("##,###.00");
	// String netTermPremium =
	// formatter1.format(acceptedInfo.getNetTermPremium());
	// paramMap.put("netTermPremium", netTermPremium);
	// paramMap.put("servicesCharges", acceptedInfo.getServicesCharges());
	// paramMap.put("stampFees", acceptedInfo.getStampFees());
	// paramMap.put("netTermAmount", acceptedInfo.getNetTermAmount());
	// paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
	// paramMap.put("imformedDate", acceptedInfo.getInformDate());
	// paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
	// paramMap.put("imformedDate", acceptedInfo.getInformDate());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// }
	//
	// public static List<AddOn> createSelectedAddOnList(FireProposal
	// fireProposal) {
	// List<AddOn> addOnList = new ArrayList<AddOn>();
	// for (FireProductInfo productInfo :
	// fireProposal.getBuildingInfoList().get(0).getFireProductInfoList()) {
	// for (FireAddOn fireAddOn : productInfo.getFireAddOnList()) {
	// addOnList.add(fireAddOn.getAddOn());
	// }
	// }
	// System.out.println("Find addonlist **" + addOnList.size());
	// for (AddOn addon : addOnList) {
	// System.out.println("name =" + addon.getName() + " value =" +
	// addon.getValue());
	// }
	// return addOnList;
	// }
	//
	// // @Test
	// public void genetateFireAcceptanceLetter() throws Exception {
	//
	// FireProposal fireProposal =
	// fireProposalService.findFireProposalById("ISFIR001HO000000001112062013");
	// AcceptedInfo acceptedInfo =
	// acceptedInfoService.findAcceptedInfoByReferenceNo("ISFIR001HO000000001112062013",
	// ReferenceType.FIRE_PROPOSAL);
	//
	// // FireProposal
	// //
	// fireProposal=fireProposalService.findFireProposalById("ISFIR001HO000000051111022016");
	// // AcceptedInfo
	// //
	// acceptedInfo=acceptedInfoService.findAcceptedInfoByReferenceNo("ISFIR0010001000000000110062013",
	// // ReferenceType.FIRE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/fire/FireInformAcceptanceLetter.jrxml");
	// String outputFilePdf = "D:/temp/FireInformAcceptanceLetter.pdf";
	// String outputFileHtml = "D:/temp/FireInformAcceptanceLetter.html";
	// Map paramMap = new HashMap();
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(createSelectedAddOnList(fireProposal)));
	// paramMap.put("customerName", "U John Htet");
	// paramMap.put("proposalNo", fireProposal.getProposalNo());
	// paramMap.put("agentName", "U Aye Lwin");
	// paramMap.put("agentLiscenseNo", "A-1235");
	// paramMap.put("totalSumInsured", 1000.0);
	// paramMap.put("informDate", new Date());
	// paramMap.put("period", 12);
	// paramMap.put("periodType", "MONTH");
	// paramMap.put("NumberOfBuilding", "testing");
	// paramMap.put("basicPremium", 2000.0);
	// paramMap.put("addOnPremium", 1000.0);
	// paramMap.put("discountAmount", 100.0);
	// paramMap.put("netPremium", 50000.0);
	// paramMap.put("stampFees", 100.0);
	// paramMap.put("servicesCharges", 100.0);
	// paramMap.put("totalAmount", 8000.0);
	// paramMap.put("customerAddress", fireProposal.getCustomerAddress());
	// paramMap.put("fullPropertyLocation",
	// fireProposal.getFullPropertyLocation());
	// // paramMap.put("periodYears",
	// // lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
	// // paramMap.put("approvedSumInsured",
	// // lifeProposal.getApprovedSumInsured());
	// // paramMap.put("totalPremium", acceptedInfo.getTotalPremium());
	// // paramMap.put("discountAmount", acceptedInfo.getDiscountAmount());
	// // paramMap.put("netPremium", acceptedInfo.getNetPremium());
	// // paramMap.put("netTermPremium", acceptedInfo.getNetTermPremium());
	// // paramMap.put("servicesCharges", acceptedInfo.getServicesCharges());
	// // paramMap.put("stampFees", acceptedInfo.getStampFees());
	// // paramMap.put("netTermAmount", acceptedInfo.getNetTermAmount());
	// // paramMap.put("customerAddress",lifeProposal.getCustomerAddress());
	// // paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	//
	// }
	//
	// // @Test
	// public void genetateCloneFireAcceptanceLetter() throws Exception {
	// // manybuildingInfoid= ISFIR001HO000000001112062013
	// FireProposal fireProposal =
	// fireProposalService.findFireProposalById("ISFIR0010001000000000110062013");
	// AcceptedInfo acceptedInfo =
	// acceptedInfoService.findAcceptedInfoByReferenceNo("ISFIR0010001000000000110062013",
	// ReferenceType.FIRE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/fire/FireInformAcceptanceLetter.jrxml");
	// String outputFilePdf = "D:/temp/cloneFireInfoAcceptanceLetter.pdf";
	// String outputFileHtml = "D:/temp/cloneFireInfoAcceptanceLetter.html";
	// List<AddOn> addOnList = createSelectedAddOnList(fireProposal);
	// List<FireAcceptanceLetterAddOnDTO> fireAcceptanceLetterAddOnDTOList =
	// null;
	// String agentName = "";
	// String agentLiscenseNo = "";
	// if (fireProposal.getAgent() != null) {
	// agentName = fireProposal.getAgent().getFullName();
	// agentLiscenseNo = "( " + fireProposal.getAgent().getLiscenseNo() + ")";
	// }
	// Map paramMap = new HashMap();
	// paramMap.put("customerName", fireProposal.getCustomerName());
	// paramMap.put("proposalNo", fireProposal.getProposalNo());
	// paramMap.put("agentName", agentName);
	// paramMap.put("agentLiscenseNo", agentLiscenseNo);
	// paramMap.put("totalSumInsured", fireProposal.getTotalSumInsured());
	// paramMap.put("informDate", acceptedInfo.getInformDate());
	// paramMap.put("period",
	// fireProposal.getBuildingInfoList().get(0).getFireProductInfoList().get(0).getPeriod());
	// paramMap.put("periodType",
	// fireProposal.getBuildingInfoList().get(0).getFireProductInfoList().get(0).getPeriodType().getLabel());
	// paramMap.put("basicPremium", acceptedInfo.getBasicPremium());
	// paramMap.put("addOnPremium", acceptedInfo.getAddOnPremium());
	// paramMap.put("discountAmount", acceptedInfo.getDiscountAmount());
	// paramMap.put("netPremium", acceptedInfo.getNetPremium());
	// paramMap.put("stampFees", acceptedInfo.getStampFees());
	// paramMap.put("servicesCharges", acceptedInfo.getServicesCharges());
	// paramMap.put("totalAmount", acceptedInfo.getTotalAmount());
	// paramMap.put("customerAddress", fireProposal.getCustomerAddress());
	// paramMap.put("fullPropertyLocation",
	// fireProposal.getFullPropertyLocation());
	// if (fireProposal.getBuildingInfoList().size() == 1) {
	// fireAcceptanceLetterAddOnDTOList =
	// createFireAcceptanceLetterAddOnDTOList(true, addOnList);
	// paramMap.put("buildingPremiumRate",
	// String.valueOf(fireProposal.getBuildingInfoList().get(0).getBuildingPremiumRate()));
	// paramMap.put("NumberOfBuilding", "Fire -");
	// paramMap.put("symbol", "% ,");
	// } else {
	// fireAcceptanceLetterAddOnDTOList =
	// createFireAcceptanceLetterAddOnDTOList(false, addOnList);
	// paramMap.put("buildingPremiumRate", "");
	// paramMap.put("NumberOfBuilding", "Various");
	// paramMap.put("symbol", "");
	// }
	// paramMap.put("TableDataSource", new
	// JRBeanCollectionDataSource(fireAcceptanceLetterAddOnDTOList));
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// }
	//
	// public List<FireAcceptanceLetterAddOnDTO>
	// createFireAcceptanceLetterAddOnDTOList(boolean isSingleBuilding,
	// List<AddOn> addOnList) {
	// List<FireAcceptanceLetterAddOnDTO> fireAcceptanceLetterAddOnDTOList = new
	// ArrayList<FireAcceptanceLetterAddOnDTO>();
	// if (isSingleBuilding && addOnList.size() != 0) {
	//
	// for (int i = 0; i < addOnList.size(); i++) {
	// if (i == addOnList.size() - 1) {
	// fireAcceptanceLetterAddOnDTOList.add(new
	// FireAcceptanceLetterAddOnDTO(addOnList.get(i), "%"));
	// } else {
	// fireAcceptanceLetterAddOnDTOList.add(new
	// FireAcceptanceLetterAddOnDTO(addOnList.get(i), "% ,"));
	// }
	// }
	// return fireAcceptanceLetterAddOnDTOList;
	// } else {
	// fireAcceptanceLetterAddOnDTOList.add(new FireAcceptanceLetterAddOnDTO("
	// ", new Double(0), " "));
	// return fireAcceptanceLetterAddOnDTOList;
	// }
	// }
	//
	// // @Test
	// public void genetateReport1() throws Exception {
	// //
	// // FireProposal
	// //
	// fireProposal=fireProposalService.findFireProposalById("ISFIR001HO000000051111022016");
	// // AcceptedInfo
	// //
	// acceptedInfo=acceptedInfoService.findAcceptedInfoByReferenceNo("ISFIR001HO000000051111022016",
	// // ReferenceType.FIRE_PROPOSAL);
	// InputStream inputStream = new
	// FileInputStream("report-template/fire/LyanTest.jrxml");
	// String outputFilePdf = "D:/temp/LyanTest.pdf";
	// String outputFileHtml = "D:/temp/LyanTest.html";
	//
	// Map paramMap = new HashMap();
	// paramMap.put("Test", "ot");
	// paramMap.put("nowTest", "john htet");
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	// paramMap, new JREmptyDataSource());
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	//
	// }
	//
	// public List<AddOn> calculateAddOnList(MotorProposal motorProposal) {
	// List<AddOn> addOnList = new ArrayList<AddOn>();
	// for (ProposalVehicle pv : motorProposal.getProposalVehicleList()) {
	// for (VehAddOn vehAddOn : pv.getVehAddOnList()) {
	// AddOn addOn = vehAddOn.getAddOn();
	// addOnList.add(addOn);
	// }
	// }
	// return addOnList;
	// }
	//
	// @Test
	// public void genetateMotorInformLetter() throws Exception {
	// MotorProposal motorProposal =
	// motorProposalService.findMotorProposalById("ISMOT0010001000000000110062013");
	// AcceptedInfo acceptedInfo =
	// acceptedInfoService.findAcceptedInfoByReferenceNo("ISMOT0010001000000000110062013",
	// ReferenceType.MOTOR_PROPOSAL);
	// List<AddOn> addOnList = new ArrayList<AddOn>();
	// addOnList = calculateAddOnList(motorProposal);
	// DecimalFormat formatter1 = new DecimalFormat("##,###.00");
	// String totalAmmount = formatter1.format(acceptedInfo.getTotalAmount());
	// // AbstractMynNumConvertor convertor = new DefaultConvertor();
	// // String
	// // myanmarTotalAmmoumt=convertor.getName(acceptedInfo.getTotalAmount());
	// InputStream inputStream = new
	// FileInputStream("report-template/motor/MotorInformAcceptanceLetter.jrxml");
	// InputStream inputStream2 = new
	// FileInputStream("report-template/motor/MyanmarMotorInformAcceptanceLetter.jrxml");
	// String outputFilePdf = "D:/temp/MotorInformAcceptanceLetter.pdf";
	// String outputFileHtml = "D:/temp/MotorInformAcceptanceLetter.html";
	// StringBuffer addionalCover = new StringBuffer();
	// addionalCover.append("");
	//
	// Map paramMap = new HashMap();
	// paramMap.put("currentDate", new Date());
	// paramMap.put("customerName", motorProposal.getCustomerName());
	// paramMap.put("customerAddress", motorProposal.getCustomerAddress());
	// paramMap.put("registrationNo",
	// motorProposal.getProposalVehicleList().get(0).getRegistrationNo());
	// paramMap.put("sumInsured", motorProposal.getSumInsured());
	// paramMap.put("basicPremium", acceptedInfo.getBasicPremium());
	// paramMap.put("discountAmount", acceptedInfo.getDiscountAmount());
	// paramMap.put("addOnPremium", acceptedInfo.getAddOnPremium());
	// paramMap.put("netPremium", acceptedInfo.getNetPremium());
	// paramMap.put("servicesCharges", acceptedInfo.getServicesCharges());
	// paramMap.put("stampFees", acceptedInfo.getStampFees());
	// paramMap.put("totalAmount", totalAmmount);
	// paramMap.put("agentName", motorProposal.getAgent().getName());
	// paramMap.put("agentLiscenseNo",
	// motorProposal.getAgent().getLiscenseNo());
	// paramMap.put("proposalNo", motorProposal.getProposalNo());
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	// JasperDesign jasperDesign2 = JRXmlLoader.load(inputStream2);
	// JasperReport jasperReport =
	// JasperCompileManager.compileReport(jasperDesign);
	// JasperReport jasperReport2 =
	// JasperCompileManager.compileReport(jasperDesign2);
	// JasperPrint jasperPrint = null;
	// JasperPrint jasperPrint2 = null;
	// Map paramMap2 = new HashMap(paramMap);
	// paramMap2.put("myanmarTotalAmmoumt", "myanmarTotalAmmoumt");
	// for (int j = 0; j < addOnList.size(); j++) {
	// addionalCover.append(addOnList.get(j).getName());
	// if ((j + 1) < addOnList.size()) {
	// addionalCover.append(", ");
	// }
	// }
	//
	// paramMap.put("addionalCover", addionalCover.toString());
	// paramMap2.put("addionalCover", addionalCover.toString());
	// jasperPrint = JasperFillManager.fillReport(jasperReport, paramMap, new
	// JREmptyDataSource());
	// jasperPrint2 = JasperFillManager.fillReport(jasperReport2, paramMap2, new
	// JREmptyDataSource());
	// List pages = jasperPrint2.getPages();
	// for (int j = 0; j < pages.size(); j++) {
	// JRPrintPage object = (JRPrintPage) pages.get(j);
	// jasperPrint.addPage(object);
	// }
	// JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// JasperExportManager.exportReportToHtmlFile(jasperPrint, outputFileHtml);
	// }
	//
	// public static void main(String[] args) {
	// org.junit.runner.JUnitCore.main(CommonInformRejectAndAcceptanceLetterTest.class.getName());
	// }
	//
	// private boolean isPublicLife(LifeProposal lifeProposal) {
	// if (lifeProposal.getProposalInsuredPersonList().size() == 1) {
	// return true;
	// }
	// return false;
	// }
}
