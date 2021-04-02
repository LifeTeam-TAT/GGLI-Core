package org.ace.insurance.report.test.agent;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AgentComissionCustomerReceiptTest {
	private static Logger logger = Logger.getLogger(AgentComissionCustomerReceiptTest.class);

	@BeforeClass
	public static void init() {
		logger.info("AgentComissionCustomerReceiptTest is started.........................................");
		logger.info("AgentComissionCustomerReceiptTest instance has been loaded.");

	}

	@AfterClass
	public static void finished() {
		logger.info("AgentComissionCustomerReceiptTest has been finished.........................................");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(AgentComissionCustomerReceiptTest.class.getName());
	}

	/*
	 * @Test public static void generateAgentCommissionInvoiceSlip(Agent
	 * agent,List<AgentSanctionReport> agentSanctionList2,List<AgentCommission>
	 * agentCommisionSelectList,Date maxdate,Date mindate,TLF
	 * tlfForCoInsuPayCr,TLF tlfForCoInsuPayDr,String coaNameCr,String
	 * coaNameDr,Double totalcommission,List<AgentCommissionReportDTO>
	 * agentCommissionReportDTOList,String dirPath, String fileName) { try{ List
	 * jasperPrintList = new ArrayList(); AbstractMynNumConvertor convertor =
	 * new DefaultConvertor(); String
	 * myanmarTotalAmmoumt=convertor.getName(totalcommission);
	 * 
	 * 
	 * 
	 * String producttype=
	 * agentCommisionSelectList.get(0).getReferenceType().toString(); Map
	 * paramMap3 = new HashMap();
	 * if(agentCommisionSelectList.get(0).getPaymentChannel
	 * ().equals(PaymentChannel.CASHED)){ paramMap3.put("cash",true);
	 * paramMap3.put("paymenttype","Cash Payment"); } else{
	 * paramMap3.put("cash",false); paramMap3.put("paymenttype","Cheque Payment"
	 * ); } paramMap3.put("coaCodeCr",tlfForCoInsuPayCr.getCoaId());
	 * paramMap3.put("coaCodeDr",tlfForCoInsuPayDr.getCoaId());
	 * paramMap3.put("agentName", agent.getName()); paramMap3.put("licenseNo",
	 * agent.getLiscenseNo());
	 * paramMap3.put("invoiceNo",agentCommisionSelectList
	 * .get(0).getInvoiceNo()); paramMap3.put("amount",totalcommission);
	 * paramMap3.put("coaNameDr",coaNameDr);
	 * paramMap3.put("coaNameCr",coaNameCr); paramMap3.put("nrc",
	 * agent.getIdNo()); paramMap3.put("maxdate", maxdate);
	 * paramMap3.put("mindate",mindate);
	 * paramMap3.put("amount",totalcommission); AbstractProcessor processor =
	 * new DefaultProcessor();
	 * paramMap3.put("wordAmount",processor.getNameWithPyar(totalcommission));
	 * InputStream inputStream3 =
	 * Thread.currentThread().getContextClassLoader().
	 * getResourceAsStream("report-template/agent/AgentCommission.jrxml");
	 * JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
	 * JasperPrint jprint3 = JasperFillManager.fillReport(jreport3,paramMap3,
	 * new JREmptyDataSource()); jasperPrintList.add(jprint3);
	 * 
	 * Map paramMap4 = new HashMap(); paramMap4.put("date", new Date());
	 * if(agent.getName() != null){ paramMap4.put("agentName", agent.getName());
	 * }else{ paramMap4.put("agentName", "-"); } if(agent.getCodeNo() != null){
	 * paramMap4.put("agentNo", agent.getLiscenseNo()); }else{
	 * paramMap4.put("agentNo", "-"); } if(agent.getFullAddress() != null){
	 * paramMap4.put("address", agent.getFullAddress()); }else{
	 * paramMap4.put("address", "-"); } if(producttype != null){
	 * if(producttype=="FIRE_POLICY"){ paramMap4.put("typeOfProduct",
	 * "FIRE INSURANCE"); } else if(producttype=="LIFE_POLICY"){
	 * paramMap4.put("typeOfProduct","LIFE INSURANCE"); }else
	 * paramMap4.put("typeOfProduct","MOTOR INSURANCE"); }else{
	 * paramMap4.put("typeOfProduct", "-"); }
	 * 
	 * if(agent.getIdNo() != null){ paramMap4.put("nrc", agent.getIdNo());
	 * }else{ paramMap4.put("nrc", "-"); }
	 * 
	 * if(agentCommisionSelectList.size() != 0){
	 * paramMap4.put("TableDataSource", new
	 * JRBeanCollectionDataSource(agentCommissionReportDTOList)); }else{
	 * paramMap4.put("TableDataSource", new JREmptyDataSource()); } double
	 * totalPremium = 0.0; double totalCommissionAmount = 0.0;
	 * for(AgentCommissionReportDTO
	 * agentCommissionReportDTO:agentCommissionReportDTOList){
	 * totalPremium+=agentCommissionReportDTO.getPremium();
	 * totalCommissionAmount+=agentCommissionReportDTO.getCommissionAmount(); }
	 * paramMap4.put("totalPremium",totalPremium );
	 * paramMap4.put("totalCommissionAmount",totalCommissionAmount);
	 * 
	 * InputStream inputStream4 =
	 * Thread.currentThread().getContextClassLoader().getResourceAsStream(
	 * "report-template/agent/AgentComissionCustomerReceipt.jrxml");
	 * JasperReport jreport4 = JasperCompileManager.compileReport(inputStream4);
	 * JasperPrint jprint4 = JasperFillManager.fillReport(jreport4,paramMap4,
	 * new JREmptyDataSource()); jasperPrintList.add(jprint4);
	 * 
	 * JRExporter exporter = new JRPdfExporter();
	 * exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
	 * jasperPrintList); FileHandler.forceMakeDirectory(dirPath); OutputStream
	 * outputStream = new FileOutputStream(dirPath + fileName);
	 * exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM,
	 * outputStream); exporter.exportReport(); outputStream.close();
	 * 
	 * } catch (Exception e) { throw new SystemException(ErrorCode.SYSTEM_ERROR,
	 * "Failed to generate Agent Commission Invoice Slip", e); }
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void generateAgentCommissionInvoiceSlip() {
	}
}
