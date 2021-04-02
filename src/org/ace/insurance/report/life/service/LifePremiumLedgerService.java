package org.ace.insurance.report.life.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.persistence.interfaces.ILifePolicyDAO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.JRGenerateUtility;
import org.ace.insurance.report.life.LifePremiumLedgerReport;
import org.ace.insurance.report.life.persistence.interfaces.ILifePremiumLedgerDAO;
import org.ace.insurance.report.life.service.interfaces.ILifePremiumLedgerService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service(value = "LifePremiumLedgerService")
public class LifePremiumLedgerService implements ILifePremiumLedgerService {
	@Resource(name = "LifePremiumLedgerDAO")
	private ILifePremiumLedgerDAO lifePremiumLedgerDAO;

	@Resource(name = "LifePolicyDAO")
	private ILifePolicyDAO lifePolicyDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePremiumLedgerReport findLifePremiumLedgerReport(String lifePolicyId) {
		LifePolicy lifePolicy;
		List<Payment> paymentList;
		Date surveyDate;
		try {
			lifePolicy = lifePolicyDAO.findById(lifePolicyId);
			paymentList = lifePremiumLedgerDAO.findCompletePaymentByReferenceNo(lifePolicyId);
			surveyDate = lifePremiumLedgerDAO.getLifeSurveyDate(lifePolicy.getLifeProposal().getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find findLifePremiumLedgerReport by lifePolicyId.", e);
		}
		return new LifePremiumLedgerReport(lifePolicy, paymentList, surveyDate);

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void generateLifePremiumLedgerReportt(LifePremiumLedgerReport ledgerReport, String fullReportFilePath) {
		Map<String, Object> paramMap = new HashMap();
		String fullTemplateFilePath = "";
		String firstComission = "";
		String renewalComission = "";
		String agent = "";
		LifePolicy lifePolicy = ledgerReport.getLifePolicy();
		if (lifePolicy.getAgent() != null) {
			firstComission = Utils.getCurrencyFormatString(Double.valueOf(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getFirstCommission()));
			renewalComission = Utils.getCurrencyFormatString(Double.valueOf(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getRenewalCommission()));
			agent = lifePolicy.getAgent().getFullName();
		}
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("sumInsured", lifePolicy.getTotalSumInsured());
		paramMap.put("agent", agent);
		paramMap.put("firstComission", firstComission);
		paramMap.put("renewalComission", renewalComission);
		paramMap.put("paymentType", lifePolicy.getPaymentType().getName());
		paramMap.put("basicPremium", lifePolicy.getTotalPremium());
		paramMap.put("basicTermPremium", lifePolicy.getTotalBasicTermPremium());
		paramMap.put("customerNameAndAddress", lifePolicy.getCustomerName() + " / " + lifePolicy.getCustomerAddress());
		paramMap.put("surveyDate", ledgerReport.getSurveyDate());
		paramMap.put("startDate", lifePolicy.getActivedPolicyStartDate());
		paramMap.put("dueDate", ledgerReport.getNextPaymentDate());
		paramMap.put("lastPaymentDate", lifePolicy.getPolicyInsuredPersonList().get(0).getLastPaymentDate());
		paramMap.put("endDate", lifePolicy.getPolicyInsuredPersonList().get(0).getEndDate());
		paramMap.put("paymentInfos", new JRBeanCollectionDataSource(ledgerReport.getPremiumLedgerInfoList()));
		paramMap.put("paymentTimes",lifePolicy.getPolicyInsuredPersonList().get(0).getPaymentTimes());
		paramMap.put("termPremium",lifePolicy.getTotalTermPremium());
		if (ProductIDConfig.isPersonalAccidentKYT(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct())
				|| ProductIDConfig.isPersonalAccidentUSD(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct())) {
			fullTemplateFilePath = "report-template/personalAccident/personalAccidentPremiumLedgerReport.jrxml";
		} else {
			fullTemplateFilePath = "report-template/life/lifePremiumLedgerReport.jrxml";
		}
		new JRGenerateUtility().generateReport(fullTemplateFilePath, fullReportFilePath, paramMap);
	}
}
