package org.ace.insurance.report.test.coinsurance;

import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class CommonCoInsurancePolicyTestCase {
	private static Logger logger = Logger.getLogger(CommonCoInsurancePolicyTestCase.class);
	private static IPaymentService paymentService;
	private static ICoinsuranceService coinsuranceService;

	@BeforeClass
	public static void init() {
		// logger.info("CommonInformRejectLetterTest is
		// started.........................................");
		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("spring-beans.xml");
		// BeanFactory factory = context;
		// firePolicyService = (IFirePolicyService)
		// factory.getBean("FirePolicyService");
		// paymentService = (IPaymentService) factory.getBean("PaymentService");
		// coinsuranceService = (ICoinsuranceService)
		// factory.getBean("CoinsuranceService");

	}

	@AfterClass
	public static void finished() {
		logger.info("CommonInformRejectLetterTest has been finished.........................................");
	}

	// @Test
	// /*
	// * public void generateoutCoInsuranceFirePolicy() throws Exception {
	// double
	// * building=0.0; double furniture=0.0; double machinery=0.0; double
	// * goods=0.0; List<FirePolicyAddOn> firePolicyAddOnList=new
	// * ArrayList<FirePolicyAddOn>(); FirePolicy
	// * firePolicy=firePolicyService.findFirePolicyByPolicyNo(
	// * "GGF/1311/0000000213/001"); for(PolicyBuildingInfo
	// * policyBuildingInfo:firePolicy.getPolicyBuildingInfoList()){
	// * for(FirePolicyProductInfo firePolicyProductInfo :
	// * policyBuildingInfo.getFirePolicyProductInfoList()){ for(FirePolicyAddOn
	// * firePolicyAddOn:firePolicyProductInfo.getFirePolicyAddOnList()){
	// * firePolicyAddOnList.add(firePolicyAddOn); }
	// * if(firePolicyProductInfo.getProduct().getName().equals("BUILDING")){
	// * building+=firePolicyProductInfo.getSumInsured();
	// * }if(firePolicyProductInfo.getProduct().getName().equals("FURNITURE")){
	// * furniture+=firePolicyProductInfo.getSumInsured();
	// * }if(firePolicyProductInfo.getProduct().getName().equals("MACHINERY")){
	// * machinery+=firePolicyProductInfo.getSumInsured();
	// * }if(firePolicyProductInfo.getProduct().getName().equals("STOCK OF
	// GOODS")
	// * ){ goods+=firePolicyProductInfo.getSumInsured(); } }
	// *
	// * } List<Payment> paymentList=
	// * paymentService.findPaymentByReferenceNoAndMaxDate(
	// * "ISLIF009HO000000078226082013"); List<Coinsurance> coinsuranceList=
	// * coinsuranceService.findByPolicyNo("GGF/1310/0000000203/HO"); double
	// * totalPremium = firePolicy.getTotalPremium(); double agentComission =
	// * firePolicy.getAgentCommission(); double comission =
	// * firePolicy.getPolicyBuildingInfoList().get(0).
	// *
	// getFirePolicyProductInfoList().get(0).getProduct().getFirstCommission();
	// * // OutCoinsuranceFirePolicyDTO outCoinsuranceFirePolicyDTO=new
	// * OutCoinsuranceFirePolicyDTO("", new Date(),
	// * firePolicy.getTotalSumInsured(),
	// firePolicy.getActivedPolicyStartDate(),
	// * firePolicy.getActivedPolicyEndDate(), firePolicy.getPolicyNo(),
	// * paymentList.get(0).getPaymentDate(), firePolicy.getCustomerName(),
	// * firePolicy.getCustomerAddress(), firePolicy.getPropertyLocation(),
	// * building, furniture, machinery, goods,
	// paymentList.get(0).getReceiptNo(),
	// * paymentList.get(0).getPaymentDate(), totalPremium, comission,
	// * agentComission , coinsuranceList); OutCoinsuranceFirePolicyDTO
	// * outCoinsuranceFirePolicyDTO=new OutCoinsuranceFirePolicyDTO();
	// * InputStream inputStream = new FileInputStream(
	// * "report-template/coinsurance/OutCoInsuranceFirePolicy.jrxml"); String
	// * outputFilePdf = "D:/temp/InCoInsuranceMotorPolicy.pdf"; String
	// * outputFileHtml = "D:/temp/InCoInsuranceMotorPolicy.html";
	// *
	// * try { Map paramMap = new HashMap(); String sumInsured =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.getSumInsured());
	// * String buildingSumInsured =
	// *
	// Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.getBuildingSumInsured
	// * ()); String furnitureSumInsured =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.
	// * getFurnitureSumInsured()); String machinerySumInsured =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.
	// * getMachinerySumInsured()); String goodsSumInsured =
	// *
	// Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.getGoodsSumInsured())
	// * ; String policyTotalPremium =
	// *
	// Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.getPolicyTotalPremium
	// * ()); String agentComissionPercentage =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.
	// * getAgentComissionPercentage()); // String policyNetPremium =
	// *
	// Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.getPolicyNetPremium()
	// * ); String policyAgentComission =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.
	// * getPolicyAgentComission()); String companyTotalSumInsured =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.
	// * getCompanyTotalSumInsured()); String companyTotalNetPremium =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.
	// * getCompanyTotalNetPremium()); String companyTotalComission =
	// * Utils.formattedCurrency(outCoinsuranceFirePolicyDTO.
	// * getCompanyTotalComission()); SimpleDateFormat sdf = new
	// * SimpleDateFormat("dd-MM-yyyy"); String todayDate =
	// * sdf.format(outCoinsuranceFirePolicyDTO.getTodayDate()); String
	// * activedPolicyStartDate =
	// * sdf.format(outCoinsuranceFirePolicyDTO.getActivedPolicyStartDate());
	// * String activedPolicyEndDate =
	// * sdf.format(outCoinsuranceFirePolicyDTO.getActivedPolicyEndDate());
	// String
	// * commenceDate =
	// sdf.format(outCoinsuranceFirePolicyDTO.getCommenceDate());
	// * String paymentDate =
	// * sdf.format(outCoinsuranceFirePolicyDTO.getPaymentDate());
	// * paramMap.put("sumInsured", sumInsured); paramMap.put("startDate",
	// * activedPolicyStartDate); paramMap.put("endDate", activedPolicyEndDate);
	// * paramMap.put("policyNo", outCoinsuranceFirePolicyDTO.getPolicyNo());
	// * paramMap.put("commenceDate", commenceDate);
	// paramMap.put("customerName",
	// * outCoinsuranceFirePolicyDTO.getCustomerName());
	// * paramMap.put("customerAddress",
	// * outCoinsuranceFirePolicyDTO.getCustomerAddress());
	// * paramMap.put("todayDate", todayDate); paramMap.put("propertyLocation",
	// * outCoinsuranceFirePolicyDTO.getPropertyLocation());
	// * paramMap.put("letterNo", outCoinsuranceFirePolicyDTO.getLetterNo());
	// * paramMap.put("paymentDate", paymentDate); paramMap.put("cashReceptNo",
	// * outCoinsuranceFirePolicyDTO.getCashReceptNo());
	// * paramMap.put("policyTotalPremium", policyTotalPremium);
	// * paramMap.put("agentComissionPercentage", agentComissionPercentage); //
	// * paramMap.put("policyNetPremium", policyNetPremium);
	// * paramMap.put("buildingSumInsured", buildingSumInsured);
	// * paramMap.put("furnitureSumInsured", furnitureSumInsured);
	// * paramMap.put("machinerySumInsured", machinerySumInsured);
	// * paramMap.put("goodsSumInsured", goodsSumInsured);
	// * paramMap.put("companyTotalSumInsured", companyTotalSumInsured);
	// * paramMap.put("companyTotalNetPremium", companyTotalNetPremium);
	// * paramMap.put("companyTotalComission", companyTotalComission); //
	// * paramMap.put( "TableDataSource", new
	// * JRBeanCollectionDataSource(outCoinsuranceFirePolicyDTO.changeAddOn(
	// * outCoinsuranceFirePolicyDTO.getAddOnList())));
	// * paramMap.put("TableDataSource2", new JRBeanCollectionDataSource(
	// * outCoinsuranceFirePolicyDTO.getCoinsuranceList())); JasperDesign
	// * jasperDesign = JRXmlLoader.load(inputStream); JasperReport jasperReport
	// =
	// * JasperCompileManager.compileReport(jasperDesign); JasperPrint
	// jasperPrint
	// * = JasperFillManager.fillReport(jasperReport, paramMap, new
	// * JREmptyDataSource());
	// * JasperExportManager.exportReportToPdfFile(jasperPrint, outputFilePdf);
	// * JasperExportManager.exportReportToHtmlFile(jasperPrint,
	// outputFileHtml);
	// * } catch (Exception e) { throw new
	// SystemException(ErrorCode.SYSTEM_ERROR,
	// * "Failed to generate outCoInsuranceFireAcceptanceLetter", e); } }
	// */

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(CommonCoInsurancePolicyTestCase.class.getName());
	}

}
