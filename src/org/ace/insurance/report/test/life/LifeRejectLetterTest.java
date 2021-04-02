package org.ace.insurance.report.test.life;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.apache.log4j.Logger;
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

public class LifeRejectLetterTest {
	private static Logger logger = Logger.getLogger(LifeRejectLetterTest.class);
	private static ILifeProposalService lifeProposalService;
	private static IAcceptedInfoService acceptedInfoService;

	@BeforeClass
	public static void init() {
		logger.info("LifeRejectLetterTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		lifeProposalService = (ILifeProposalService) factory.getBean("LifeProposalService");
		acceptedInfoService = (IAcceptedInfoService) factory.getBean("AcceptedInfoService");
		logger.info("LifeRejectLetterTest instance has been loaded.");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(LifeRejectLetterTest.class.getName());
	}

	@Test
	public void generateLifePolicyIssueSingle() throws Exception {
		try {
			String dirPath = "D:/temp/";
			String fileName = "LifeInformRejectLetter.pdf";
			LifeProposal lifeProposal = lifeProposalService.findLifeProposalById("ISLIF001001000000361105112013");
			AcceptedInfo acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo("ISLIF001001000000361105112013", ReferenceType.LIFE_PROPOSAL);
			Map paramMap = new HashMap();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("imformedDate", acceptedInfo.getInformDate());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = new FileInputStream("report-template/life/LifeInformRejectLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}
}
