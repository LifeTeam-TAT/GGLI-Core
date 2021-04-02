package org.ace.insurance.life.policyLog;

import java.util.List;

import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.proxy.LCB001;
import org.ace.insurance.proxy.WorkflowCriteria;
import org.ace.insurance.proxy.service.interfaces.IProxyService;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCase {
	private static IProxyService proxyService;
	private static Logger logger = Logger.getLogger(TestCase.class);

	@BeforeClass
	public static void init() {
		logger.info("ReportTest is started.........................................");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		proxyService = (IProxyService) factory.getBean("ProxyService");
		logger.info("ReportTest instance has been loaded.");

	}

	@AfterClass
	public static void finished() {
		logger.info("ReportTest has been finished.........................................");
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(TestCase.class.getName());
	}

	@Test
	public void find() {
		WorkflowCriteria wfc = new WorkflowCriteria(WorkflowReferenceType.LIFE_CLAIM, WorkflowTask.CLAIM_PAYMENT, null);
		List<LCB001> s = proxyService.find_LCB001(wfc);
		System.out.println(s.get(0).getBeneficiaryName() + s.get(0).getTotalSumInsured());
	}
}
