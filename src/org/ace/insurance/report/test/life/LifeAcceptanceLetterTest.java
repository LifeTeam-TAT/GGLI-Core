package org.ace.insurance.report.test.life;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.web.common.Utils;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class LifeAcceptanceLetterTest {

	private static Logger logger = Logger.getLogger(LifeAcceptanceLetterTest.class);
	private static ILifeProposalService lifeProposalService;
	private static IAcceptedInfoService acceptedInfoService;

	@BeforeClass
	public static void init() {
		logger.info("LifeAcceptanceLetterTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		lifeProposalService = (ILifeProposalService) factory.getBean("LifeProposalService");
		acceptedInfoService = (IAcceptedInfoService) factory.getBean("AcceptedInfoService");
		logger.info("LifeAcceptanceLetterTest instance has been loaded.");

	}

	@AfterClass
	public static void finished() {
		logger.info("LifeAcceptanceLetterTest has been finished.........................................");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(LifeAcceptanceLetterTest.class.getName());

	}

	// @Test
	public void generateLifeAcceptanceLetter() {
		try {
			LifeProposal lifeProposal = lifeProposalService.findLifeProposalById("ISLIF001001000000361105112013");
			AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo("ISLIF001001000000361105112013", ReferenceType.LIFE_PROPOSAL);
			Map paramMap = new HashMap();
			String netTermPremium = Utils.formattedCurrency(acceptedInfo.getNetTermPremium());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
			paramMap.put("approvedSumInsured", lifeProposal.getApprovedSumInsured());
			paramMap.put("totalPremium", acceptedInfo.getTotalPremium());
			paramMap.put("discountAmount", acceptedInfo.getDiscountAmount());
			paramMap.put("netPremium", acceptedInfo.getNetPremium());
			paramMap.put("netTermPremium", netTermPremium);
			paramMap.put("servicesCharges", acceptedInfo.getServicesCharges());
			paramMap.put("stampFees", acceptedInfo.getStampFees());
			paramMap.put("netTermAmount", acceptedInfo.getNetTermAmount());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("imformedDate", acceptedInfo.getInformDate());
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("publicLife", isPublicLife(lifeProposal));
			InputStream inputStream = new FileInputStream("report-template/life/LifeInformAcceptanceLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory("D:/temp/");
			String outputFile = "D:/temp/LifeInformAcceptanceLetter.pdf";
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeInformAcceptanceLetter", e);
		}
	}

	@Test
	public void prepareGenerateLifeAcceptanceLetter() {
		try {
			Map paramMap = new HashMap();
			paramMap.put("customerName", "customerName");
			paramMap.put("proposalNo", "proposalNo");
			paramMap.put("periodYears", 1);
			paramMap.put("approvedSumInsured", 1000.0);
			paramMap.put("totalPremium", 100.0);
			paramMap.put("discountAmount", 100.0);
			paramMap.put("netPremium", 100.0);
			paramMap.put("netTermPremium", "netTermPremium");
			paramMap.put("servicesCharges", 100.0);
			paramMap.put("stampFees", 100.0);
			paramMap.put("netTermAmount", 100.0);
			paramMap.put("customerAddress", "customerAddress");
			paramMap.put("imformedDate", new Date());
			paramMap.put("paymentType", "paymentType");
			paramMap.put("agent", "agent");
			paramMap.put("publicLife", true);
			InputStream inputStream = new FileInputStream("report-template/life/LifeInformAcceptanceLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory("D:/temp/");
			String outputFile = "D:/temp/LifeInformAcceptanceLetter.pdf";
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeInformAcceptanceLetter", e);
		}
	}

	private static boolean isPublicLife(LifeProposal lifeProposal) {
		if (lifeProposal.getProposalInsuredPersonList().size() == 1) {
			return true;
		}
		return false;
	}

}
