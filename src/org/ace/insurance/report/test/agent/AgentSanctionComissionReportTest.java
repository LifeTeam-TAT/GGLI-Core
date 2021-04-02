package org.ace.insurance.report.test.agent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.report.agent.AgentSanctionCriteria;
import org.ace.insurance.report.agent.AgentSanctionReport;
import org.ace.insurance.report.agent.persistence.interfaces.IAgentSanctionDAO;
import org.ace.insurance.report.agent.service.interfaces.IAgentSanctionService;
import org.ace.insurance.web.common.ntw.eng.AbstractProcessor;
import org.ace.insurance.web.common.ntw.eng.DefaultProcessor;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class AgentSanctionComissionReportTest {
	private static Logger logger = Logger.getLogger(AgentSanctionComissionReportTest.class);
	private static IAgentSanctionService agentSanctionService;
	private static IAgentSanctionDAO agentSanctionDAO;

	@BeforeClass
	public static void init() {
		logger.info("AgentSanctionComissionReportTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		agentSanctionService = (IAgentSanctionService) factory.getBean("AgentSanctionService");
		agentSanctionDAO = (IAgentSanctionDAO) factory.getBean("AgentSanctionDAO");
		logger.info("AgentSanctionComissionReportTest instance has been loaded.");

	}

	@AfterClass
	public static void finished() {
		logger.info("AgentSanctionComissionReportTest has been finished.........................................");
	}

	// @Test
	public void genetateAgent() throws Exception {
		String dirPath = "D:/temp/";
		String fileName = "AgentSanctionReport";
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		AgentSanctionCriteria criteria = new AgentSanctionCriteria();
		criteria.setStartDate(Utils.getDate("11-06-2013"));
		criteria.setEndDate(Utils.getDate("20-12-2013"));
		// criteria.setInsuranceType(InsuranceType.MOTOR);
		List<AgentComissionInfo> agentList = agentSanctionService.findAgents(criteria);
		for (AgentComissionInfo agent : agentList) {
			List<AgentSanctionReport> agentSanctionList = agentSanctionDAO.findIndividual(criteria, agent, false);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("agentName", "Pyae Phyo Aung Htun");
			paramMap.put("agentCode", agent.getAgentCode());
			paramMap.put("licenseNo", agent.getLicenseNo());
			paramMap.put("startDate", Utils.getDateFormatString(agent.getStartDate()));
			paramMap.put("endDate", Utils.getDateFormatString(agent.getEndDate()));
			paramMap.put("typeOfProduct", agent.getTypeOfProduct());
			paramMap.put("currentDate", new Date());
			paramMap.put("totalComission", org.ace.insurance.web.common.Utils.formattedCurrency(agent.getTotalComission()));
			AbstractProcessor processor = new DefaultProcessor();
			paramMap.put("totalComissionLetter", processor.getName(agent.getTotalComission()));
			InputStream policyIS = new FileInputStream("report-template/agent/agentSanctionReport.jrxml");
			JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
			JasperPrint policyJP = JasperFillManager.fillReport(policyJR, paramMap, new JREmptyDataSource());
			jasperPrintList.add(policyJP);
			if (agentSanctionList != null || !agentSanctionList.isEmpty()) {
				Map<String, Object> attachMap = new HashMap<String, Object>(paramMap);
				double totalSumInsured = 0.0;
				double totalFirstPremium = 0.0;
				double totalComission = 0.0;
				double totalReInstatement = 0.0;
				for (AgentSanctionReport agentSanctionReport : agentSanctionList) {
					totalSumInsured += agentSanctionReport.getSumInsured();
					totalFirstPremium += agentSanctionReport.getPremium();
					totalComission += agentSanctionReport.getComission();
					totalReInstatement += agentSanctionReport.getReinstatementPremium();
				}
				attachMap.put("totalSumInsured", totalSumInsured);
				attachMap.put("totalFirstPremium", totalFirstPremium);
				attachMap.put("totalComission", totalComission);
				attachMap.put("totalReInstatement", totalReInstatement);
				attachMap.put("TableDataSource", new JRBeanCollectionDataSource(agentSanctionList));
				InputStream attachIS = new FileInputStream("report-template/agent/agentComissionReport.jrxml");
				JasperReport attachJR = JasperCompileManager.compileReport(attachIS);
				JasperPrint attachJP = JasperFillManager.fillReport(attachJR, attachMap, new JREmptyDataSource());
				jasperPrintList.add(attachJP);
			}
		}
		JRExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
		exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream("D:/temp/AgentSanction.pdf"));
		exporter.exportReport();
	}

	@Test
	public void prepareGenetateAgent() throws Exception {
		String dirPath = "D:/temp/";
		String fileName = "AgentSanctionReport";
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		String agentName = "agentName";
		String agentCode = "agentCode";
		String typeOfProduct = "typeOfProduct";
		String licenseNo = "licenseNo";
		double totalComission = 10000.0;
		double totalSumInsured = 1000000.0;
		double totalFirstPremium = 1000.0;
		double totalReInstatement = 0.0;
		String totalComissionLetter = "totalComissionLetter";
		List<AgentSanctionReport> agentSanctionList = new ArrayList<AgentSanctionReport>();
		AgentSanctionReport agentSanctionReport = new AgentSanctionReport();
		agentSanctionReport.setPolicyHolder("policyHolder");
		agentSanctionReport.setCashReceiptNo("cashReceiptNo");
		agentSanctionReport.setPolicyHolder("policyHolder");
		agentSanctionReport.setPolicyNo("policyNo");
		agentSanctionReport.setAgentName("agentName");
		agentSanctionReport.setAgentCode("agentCode");
		agentSanctionReport.setLicenseNo("licenseNo");
		agentSanctionReport.setTypeOfProduct("typeOfProduct");
		agentSanctionReport.setMobile("mobile");
		agentSanctionReport.setAddress("address");
		agentSanctionReport.setSumInsured(10000.0);
		agentSanctionReport.setPremium(1000.0);
		agentSanctionReport.setReinstatementPremium(0.0);
		agentSanctionReport.setComissionRate(20.0f);
		agentSanctionReport.setComission(300.0);
		agentSanctionReport.setStartDate(new Date());
		agentSanctionReport.setEndDate(new Date());
		agentSanctionList.add(agentSanctionReport);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sanctionNo", "SanctionNo");
		paramMap.put("agentName", agentName);
		paramMap.put("agentCode", agentCode);
		paramMap.put("licenseNo", licenseNo);
		paramMap.put("startDate", Utils.getDateFormatString(new Date()));
		paramMap.put("endDate", Utils.getDateFormatString(new Date()));
		paramMap.put("typeOfProduct", typeOfProduct);
		paramMap.put("currentDate", new Date());
		paramMap.put("totalComission", org.ace.insurance.web.common.Utils.formattedCurrency(totalComission));
		AbstractProcessor processor = new DefaultProcessor();
		paramMap.put("totalComissionLetter", totalComissionLetter);
		InputStream policyIS = new FileInputStream("report-template/agent/agentSanctionReport.jrxml");
		JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
		JasperPrint policyJP = JasperFillManager.fillReport(policyJR, paramMap, new JREmptyDataSource());
		jasperPrintList.add(policyJP);
		Map<String, Object> attachMap = new HashMap<String, Object>(paramMap);
		attachMap.put("totalSumInsured", totalSumInsured);
		attachMap.put("totalFirstPremium", totalFirstPremium);
		attachMap.put("totalComission", totalComission);
		attachMap.put("totalReInstatement", totalReInstatement);
		attachMap.put("TableDataSource", new JRBeanCollectionDataSource(agentSanctionList));
		InputStream attachIS = new FileInputStream("report-template/agent/agentComissionReport.jrxml");
		JasperReport attachJR = JasperCompileManager.compileReport(attachIS);
		JasperPrint attachJP = JasperFillManager.fillReport(attachJR, attachMap, new JREmptyDataSource());
		jasperPrintList.add(attachJP);

		JRExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
		exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream("D:/temp/AgentSanction.pdf"));
		exporter.exportReport();
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(AgentSanctionComissionReportTest.class.getName());
	}

}
