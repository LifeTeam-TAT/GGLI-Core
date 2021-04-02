package org.ace.insurance.report.agent.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.AgentSanctionCriteria;
import org.ace.insurance.report.agent.AgentSanctionReport;
import org.ace.insurance.report.agent.persistence.AgentSanctionDTO;
import org.ace.insurance.report.agent.persistence.interfaces.IAgentSanctionDAO;
import org.ace.insurance.report.agent.service.interfaces.IAgentSanctionService;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.web.common.ntw.eng.AbstractProcessor;
import org.ace.insurance.web.common.ntw.eng.DefaultProcessor;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Service(value = "AgentSanctionService")
public class AgentSanctionService implements IAgentSanctionService {

	@Resource(name = "AgentSanctionDAO")
	private IAgentSanctionDAO agentSanctionDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "CurrencyService")
	private ICurrencyService currencyService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentSanctionReport> findAgentCommissionIndividual(AgentSanctionCriteria criteria, AgentComissionInfo a) {
		List<AgentSanctionReport> result = null;
		try {
			result = agentSanctionDAO.findIndividual(criteria, a, false);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommission by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void sanctionAgent(Map<String, List<AgentComissionInfo>> agentComissionMap, Currency currency) {
		try {
			String sanctionNo = "";
			double taxRate = 0.0;
			double totalComission = 0;
			double rate = 1.0;
			double withHoldingTax = 0.0;
			double homeWithHoldingTax = 0.0;
			if (CurrencyUtils.isUSD(currency)) {
				rate = paymentDAO.findActiveRate();
			}
			for (Map.Entry<String, List<AgentComissionInfo>> entry : agentComissionMap.entrySet()) {
				sanctionNo = customIDGenerator.getNextId(SystemConstants.AGENT_SANCTION_NO, null, true);
				for (AgentComissionInfo agentCommissionInfo : entry.getValue()) {
					totalComission = totalComission + agentCommissionInfo.getTotalComission();
				}

				// TODO , fixed for all products?
				// taxRate = (totalComission > 1500000) ? 2 : 0.0;
				for (AgentComissionInfo agentCommissionInfo : entry.getValue()) {
					withHoldingTax = Utils.getPercentOf(taxRate, agentCommissionInfo.getTotalComission());
					homeWithHoldingTax = rate * withHoldingTax;
					agentSanctionDAO.updateAgentCommissionStaus(agentCommissionInfo.getId(), sanctionNo, new Date(), withHoldingTax, homeWithHoldingTax);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to sanctionAgent.", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AgentSanctionReport findIndividualAgentCommission(AgentCommissionDetailCriteria criteria, AgentCommission a) {
		AgentSanctionReport result = null;
		try {
			result = agentSanctionDAO.findIndividualAgentCommission(criteria, a);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionDetailReport by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentSanctionDTO> findAgentCommissionByEnquiry(AgentSanctionCriteria criteria) {
		List<AgentSanctionDTO> result = new ArrayList<AgentSanctionDTO>();
		try {
			result = agentSanctionDAO.findAllAgentCommissionInfoByEnquiry(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find findAgentCommissionByEnquiry.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentComissionInfo> findBySanctionNo(AgentSanctionDTO agentSanctionDTO) {
		List<AgentComissionInfo> result = null;
		try {
			result = agentSanctionDAO.findAgentCommissionInfoBySanction(agentSanctionDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionDetailReport by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AgentComissionInfo> findAgents(AgentSanctionCriteria criteria) {
		List<AgentComissionInfo> result = null;
		try {
			result = agentSanctionDAO.findAgents(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Agents.", e);
		}
		return result;
	}

	public void generateReport(Map<String, List<AgentComissionInfo>> agentComissionMap, AgentSanctionCriteria criteria, boolean isEnquiry, String filePath, String fileName)
			throws Exception {
		List<JasperPrint> jasperPrintList = new ArrayList<>();
		AgentComissionInfo agentCommissionInfo = null;
		String agentName = "";
		String agentCode = "";
		String licenseNo = "";
		String startDate = "";
		String endDate = "";
		String totalComissionLetter = "";
		String currencyCode = "";
		String currencySymbol = "";
		Date currentDate = new Date();
		String sanctionNo = "";
		AbstractProcessor processor = new DefaultProcessor();
		double totalCommission = 0.0;
		AgentCommission agentCommission = null;
		Currency currency = null;
		double totalWithHoldingTax = 0.0;
		double totalSI = 0.0;
		double totalFirstPremium = 0.0;
		double totalRenewalPremium = 0.0;
		for (Map.Entry<String, List<AgentComissionInfo>> entry : agentComissionMap.entrySet()) {
			totalSI = 0.0;
			totalFirstPremium = 0.0;
			totalRenewalPremium = 0.0;
			totalCommission = 0.0;
			totalWithHoldingTax = 0.0;
			agentCommissionInfo = entry.getValue().get(0);
			// get sanctionNo for each AgentComission by currency
			agentCommission = agentSanctionDAO.findAgentCommissionById(agentCommissionInfo.getId());
			sanctionNo = agentCommission.getSanctionNo();
			// calculate total commission for each agent
			for (AgentComissionInfo agentComissionInfo : entry.getValue()) {
				totalCommission = totalCommission + agentComissionInfo.getTotalComission();
			}
			totalWithHoldingTax = totalCommission >= 1500000 ? Utils.getPercentOf(2, totalCommission) : 0;

			// get Currency from AgentCommission
			currency = currencyService.findCurrencyByCurrencyCode(agentCommission.getCUR());
			agentName = agentCommissionInfo.getAgentName() != null ? agentCommissionInfo.getAgentName() : "";
			agentCode = agentCommissionInfo.getAgentCode() != null ? agentCommissionInfo.getAgentCode() : "";
			licenseNo = agentCommissionInfo.getLicenseNo() != null ? agentCommissionInfo.getLicenseNo() : "";
			startDate = agentCommissionInfo.getStartDate() != null ? Utils.getDateFormatString(agentCommissionInfo.getStartDate()) : "";
			endDate = agentCommissionInfo.getEndDate() != null ? Utils.getDateFormatString(agentCommissionInfo.getEndDate()) : "";
			totalComissionLetter = processor.getNameWithDecimal(totalCommission, currency);
			currencyCode = agentCommissionInfo.getCurrencyCode() == null ? "KYT" : agentCommissionInfo.getCurrencyCode();
			currencySymbol = agentCommissionInfo.getCurrencySymbol();

			// agentComissionReport
			List<AgentSanctionReport> singleList = null;
			List<AgentSanctionReport> groupList = new ArrayList<AgentSanctionReport>();
			for (AgentComissionInfo agentComissionInfo : entry.getValue()) {
				if (InsuranceType.LIFE.equals(criteria.getInsuranceType())) {
					AgentSanctionReport report = null;
					if (isEnquiry) {
						report = agentSanctionDAO.findLifeIndividualForSanctionEnquiry(criteria, agentComissionInfo);
					} else {
						report = agentSanctionDAO.findLifeIndividualForSanction(criteria, agentComissionInfo);
					}
					groupList.add(report);
				} else {
					singleList = agentSanctionDAO.findIndividual(criteria, agentComissionInfo, isEnquiry);
					if (!singleList.isEmpty()) {
						groupList.addAll(singleList);
					}

				}
			}
			totalCommission = 0.0;
			for (AgentSanctionReport report : groupList) {
				totalSI = totalSI + report.getSumInsured();
				totalFirstPremium = totalFirstPremium + report.getPremium();
				totalRenewalPremium = totalRenewalPremium + report.getReinstatementPremium();
				totalCommission = totalCommission + report.getComission();
			}
			totalWithHoldingTax = totalCommission >= 1500000 ? Utils.getPercentOf(2, totalCommission) : 0;

			/***** Agent Sanction ****/
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("sanctionNo", sanctionNo);
			paramMap.put("agentName", agentName);
			paramMap.put("agentCode", agentCode);
			paramMap.put("licenseNo", licenseNo);
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
			// groupList.remove(0);
			paramMap.put("typeOfProduct", groupList.get(0).getTypeOfProduct());
			paramMap.put("currencyCode", currencyCode);
			paramMap.put("currencySymbol", currencySymbol);
			paramMap.put("currentDate", currentDate);
			double totalAgentCommission = totalCommission - totalWithHoldingTax;
			String fromatTotalComission = Utils.getCurrencyFormatString(totalAgentCommission);
			totalComissionLetter = processor.getNameWithDecimal(totalAgentCommission, currency);
			paramMap.put("totalComissionLetter", totalComissionLetter);
			paramMap.put("totalComission", fromatTotalComission);

			/***** Attachment List ****/
			Map<String, Object> attachMap = new HashMap<>(paramMap);
			attachMap.put("totalSI", totalSI);
			attachMap.put("totalFirstPremium", totalFirstPremium);
			attachMap.put("totalRenewalPremium", totalRenewalPremium);
			attachMap.put("totalComission", totalCommission);
			attachMap.put("withHoldingTax", totalWithHoldingTax);
			attachMap.put("netCommission", totalCommission);
			attachMap.put("currencyCode", currencyCode);
			attachMap.put("TableDataSource", new JRBeanCollectionDataSource(groupList));

			InputStream policyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/agentSanctionReport.jrxml");
			JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
			JasperPrint policyJP = JasperFillManager.fillReport(policyJR, paramMap, new JREmptyDataSource());
			jasperPrintList.add(policyJP);

			InputStream attachIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/agentComissionReport.jrxml");
			JasperReport attachJR = JasperCompileManager.compileReport(attachIS);
			JasperPrint attachJP = JasperFillManager.fillReport(attachJR, attachMap, new JREmptyDataSource());
			jasperPrintList.add(attachJP);
		}

		JRExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
		FileHandler.forceMakeDirectory(filePath);
		FileOutputStream outputStream = new FileOutputStream(filePath + fileName);
		exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.exportReport();
		outputStream.close();

	}
}
