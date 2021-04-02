package org.ace.insurance.report.fidelity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.report.fidelity.service.interfaces.IFidelityDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCaseCusReport {
	private static IFidelityDailyIncomeReportService fService;
	private static IBranchService branchService;
	private static IPaymentService pService;
	private static Date startDate;
	private static Date endDate;

	@BeforeClass
	public static void init() {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		BeanFactory factory = context;
		pService = (IPaymentService) factory.getBean("PaymentService");
		branchService = (IBranchService) factory.getBean("BranchService");
		fService = (IFidelityDailyIncomeReportService) factory.getBean("FidelityDailyIncomeReportService");
	}

	@AfterClass
	public static void finish() {
	}

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main(TestCaseCusReport.class.getName());
	}

	@Test
	public void test() {
		List<FidelityDailyIncomeReport> result = new ArrayList<FidelityDailyIncomeReport>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

		try {
			startDate = formatter.parse("06.01.2014");
			endDate = formatter.parse("06.01.2015");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Branch branch = branchService.findBranchById("BANCH00000000000000129032013");
		// Payment payment =
		// pService.findPaymentByReceiptNo("CASH/1401/0000004067/001");

		FidelityDailyIncomeReportCriteria criteria = new FidelityDailyIncomeReportCriteria(startDate, endDate, branch);
		result = fService.findFidelityDailyIncomeReport(criteria);
		int count = 1;
		for (FidelityDailyIncomeReport fidelityDailyIncomeReport : result) {
			System.out.println(fidelityDailyIncomeReport.getReceiptNo());
			System.out.println("*****");
			count++;
		}
		System.out.println(count);

	}
}
