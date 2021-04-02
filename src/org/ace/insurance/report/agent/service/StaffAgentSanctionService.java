package org.ace.insurance.report.agent.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import javax.annotation.Resource;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.persistence.StaffAgentCommissionDAO;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.product.Product;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.report.agent.persistence.interfaces.IStaffAgentSactionDAO;
import org.ace.insurance.report.agent.service.interfaces.IStaffAgentSanctionService;
import org.ace.insurance.report.agent.view.StaffAgentCommissionInfo;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.COACode;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.ntw.eng.AbstractProcessor;
import org.ace.insurance.web.common.ntw.eng.DefaultProcessor;
import org.ace.insurance.web.common.ntw.mym.AbstractMynNumConvertor;
import org.ace.insurance.web.common.ntw.mym.DefaultConvertor;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionReportDTO;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Service(value = "StaffAgentSanctionService")
public class StaffAgentSanctionService implements IStaffAgentSanctionService {

	@Resource(name = "StaffAgentSactionDAO")
	private IStaffAgentSactionDAO staffagentSanctionDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "CurrencyService")
	private ICurrencyService currencyService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;
	
	@Resource(name = "PaymentService")
	private IPaymentService paymentService;
	
	@Resource(name = "UserProcessService")
	private IUserProcessService userProcessService;
	
	@Resource(name = "LifePolicyService")
	private ILifePolicyService lPolicyService;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<StaffAgentCommissionInfo> findAgents(StaffSanctionCriteria criteria) {
		List<StaffAgentCommissionInfo> result = null;
		try {
			result = staffagentSanctionDAO.findStaffAgents(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Agents.", e);
		}
		return result;
	}

	@Override
	public void generateReport(Map<String, List<StaffAgentCommissionInfo>> agentComissionMap, StaffSanctionCriteria criteria, boolean isEnquiry, String filePath, String fileName)
			throws Exception {
		 List<JasperPrint> jasperPrintList = new ArrayList<>();
		 StaffAgentCommissionInfo StaffAgentCommissionInfo = null;
		 String staffName = "";
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
		 StaffAgentCommission staffagentCommission = null;
		 Currency currency = null;
		 double totalWithHoldingTax = 0.0;
		 double totalSI = 0.0;
		 double totalFirstPremium = 0.0;
		 double totalRenewalPremium = 0.0;
		 for (Map.Entry<String, List<StaffAgentCommissionInfo>> entry :
		 agentComissionMap.entrySet()) {
		 totalSI = 0.0;
		 totalFirstPremium = 0.0;
		 totalRenewalPremium = 0.0;
		 totalCommission = 0.0;
		 totalWithHoldingTax = 0.0;
		 StaffAgentCommissionInfo = entry.getValue().get(0);
		 // get sanctionNo for each AgentComission by currency
		 staffagentCommission =
		 staffagentSanctionDAO.findAgentCommissionById(StaffAgentCommissionInfo.getId());
		 sanctionNo = staffagentCommission.getSanctionNo();
		 // calculate total commission for each agent
		 for (StaffAgentCommissionInfo agentComissionInfo : entry.getValue()) {
		 totalCommission = totalCommission +
		 agentComissionInfo.getTotalComission();
		 }
		 totalWithHoldingTax = totalCommission >= 1500000 ? Utils.getPercentOf(2,
		 totalCommission) : 0;
		
		 // get Currency from AgentCommission
		 currency =
		 currencyService.findCurrencyById(staffagentCommission.getCUR());
		 staffName = StaffAgentCommissionInfo.getAgentName() != null ?
		 StaffAgentCommissionInfo.getAgentName() : "";
		 agentCode = StaffAgentCommissionInfo.getAgentCode() != null ?
		 StaffAgentCommissionInfo.getAgentCode() : "";
		 startDate = StaffAgentCommissionInfo.getStartDate() != null ?
		 Utils.getDateFormatString(StaffAgentCommissionInfo.getStartDate()) : "";
		 endDate = StaffAgentCommissionInfo.getEndDate() != null ?
		 Utils.getDateFormatString(StaffAgentCommissionInfo.getEndDate()) : "";
		 totalComissionLetter = processor.getNameWithDecimal(totalCommission,
		 currency);
		 currencyCode = StaffAgentCommissionInfo.getCurrencyCode() == null ? "KYT"
		 : StaffAgentCommissionInfo.getCurrencyCode();
		 currencySymbol = StaffAgentCommissionInfo.getCurrencySymbol();

		 /***** Agent Sanction ****/
		 Map<String, Object> paramMap = new HashMap<>();
		 paramMap.put("sanctionNo", sanctionNo);
		 paramMap.put("agentName", staffName);
		 paramMap.put("agentCode", agentCode);
		 paramMap.put("licenseNo", licenseNo);
		 paramMap.put("startDate", startDate);
		 paramMap.put("endDate", endDate);
		 // groupList.remove(0);
		// paramMap.put("typeOfProduct", groupList.get(0).getTypeOfProduct());
		 paramMap.put("currencyCode", currencyCode);
		 paramMap.put("currencySymbol", currencySymbol);
		 paramMap.put("currentDate", currentDate);
		 double totalAgentCommission = totalCommission - totalWithHoldingTax;
		 String fromatTotalComission =
		 Utils.getCurrencyFormatString(totalAgentCommission);
		 totalComissionLetter = processor.getNameWithDecimal(totalAgentCommission,
		 currency);
		 paramMap.put("totalComissionLetter", totalComissionLetter);
		 paramMap.put("totalComission", fromatTotalComission);
		
//		 /***** Attachment List ****/
//		 Map<String, Object> attachMap = new HashMap<>(paramMap);
//		 attachMap.put("totalSI", totalSI);
//		 attachMap.put("totalFirstPremium", totalFirstPremium);
//		 attachMap.put("totalRenewalPremium", totalRenewalPremium);
//		 attachMap.put("totalComission", totalCommission);
//		 attachMap.put("withHoldingTax", totalWithHoldingTax);
//		 attachMap.put("netCommission", totalCommission - totalWithHoldingTax);
//		 attachMap.put("currencyCode", currencyCode);
//		 attachMap.put("TableDataSource", new
//		 JRBeanCollectionDataSource(groupList));
		
		 InputStream policyIS =
		 Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/staffagentSanctionReport.jrxml");
		 JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
		 JasperPrint policyJP = JasperFillManager.fillReport(policyJR, paramMap,
		 new JREmptyDataSource());
		 jasperPrintList.add(policyJP);
		
//		 InputStream attachIS =
//		 Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/agentComissionReport.jrxml");
//		 JasperReport attachJR = JasperCompileManager.compileReport(attachIS);
//		 JasperPrint attachJP = JasperFillManager.fillReport(attachJR, attachMap,
//		 new JREmptyDataSource());
//		 jasperPrintList.add(attachJP);
		 }
		
		 JRExporter exporter = new JRPdfExporter();
		 exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
		 jasperPrintList);
		 FileHandler.forceMakeDirectory(filePath);
		 FileOutputStream outputStream = new FileOutputStream(filePath +
		 fileName);
		 exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM,
		 outputStream);
		 exporter.exportReport();
		 outputStream.close();

	}


	@Transactional(propagation = Propagation.REQUIRED)
	public void sanctionAgent(Map<String, List<StaffAgentCommissionInfo>> agentComissionMap, Currency currency) {
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
			for (Entry<String, List<StaffAgentCommissionInfo>> entry : agentComissionMap.entrySet()) {
				sanctionNo = customIDGenerator.getNextId(SystemConstants.AGENT_SANCTION_NO, null, true);
				for (StaffAgentCommissionInfo agentCommissionInfo : entry.getValue()) {
					totalComission = totalComission + agentCommissionInfo.getTotalComission();
				}

				// TODO , fixed for all products?
				// taxRate = (totalComission > 1500000) ? 2 : 0.0;
				for (StaffAgentCommissionInfo agentCommissionInfo : entry.getValue()) {
					withHoldingTax = Utils.getPercentOf(taxRate, agentCommissionInfo.getTotalComission());
					homeWithHoldingTax = rate * withHoldingTax;
					staffagentSanctionDAO.updateAgentCommissionStaus(agentCommissionInfo.getId(), sanctionNo, new Date(), withHoldingTax, homeWithHoldingTax);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to sanctionAgent.", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void generatestaffAgentInvoice(List<StaffAgentCommission> agentCommissions, boolean isEnquiry,
			String dirPath, String fileName) throws Exception {
		
		 List<JasperPrint> jasperPrintList = new ArrayList<>();
		StaffAgentCommission agentCom = null;
		Staff agent = null;
		Branch branch = userProcessService.getLoginUser().getBranch();
		double totalCommission = 0.0;
		Date maxDate = null;
		Date minDate = null;
		ArrayList<Date> dates = new ArrayList<Date>();
		String drCCOACode = "";
		String crCCOACode = "";
		String invoiceNo = null;
		String coaNameCr;
		String coaNameDr;
		double totalWithHoldingTax = 0.0;
		String currencyCode = KeyFactorChecker.getKyatId();
		Product product = null;
		List<StaffAgentCommissionInfo> agentCommissionReportDTOList = new ArrayList<StaffAgentCommissionInfo>();
		/**** Same Data for all AgentCommission *****/
		if (agentCommissions != null && !agentCommissions.isEmpty()) {
			agentCom = agentCommissions.get(0);
			StaffAgentCommission agentCommission = staffagentSanctionDAO.findAgentCommissionById(agentCom.getId());
			invoiceNo = agentCommission.getInvoiceNo();
			//TODO FIXME PSH
			currencyCode = KeyFactorChecker.getKyatId();
		}
		for (StaffAgentCommission ac : agentCommissions) {
			totalCommission = totalCommission + (ac.getCommission() - ac.getWithHoldingTax());
			dates.add(ac.getCommissionStartDate());
		}
		String coaCode = null;
		switch (agentCom.getReferenceType()) {

		case LIFE_POLICY:
		case LIFE_BILL_COLLECTION:
			LifePolicy lifePolicy = lPolicyService.findLifePolicyById(agentCom.getReferenceNo());
			product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			if (ProductIDConfig.isFarmer(product)) {
				coaCode = COACode.FARMER_AGENT_PAYABLE;
			} else if (ProductIDConfig.isGroupLife(product)) {
				coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
			} else if (ProductIDConfig.isGroupLife(product)) {
				coaCode = COACode.PUBLICTERMLIFE_AGENT_PAYABLE;
			} else if (ProductIDConfig.isPublicLife(product)) {
				coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
			} else if (ProductIDConfig.isSportMan(product)) {
				coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
			} else if (ProductIDConfig.isSnakeBite(product)) {
				coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
			} else {
				coaCode = COACode.LIFE_AGENT_PAYABLE;
			}
			break;
		case PA_POLICY:
			coaCode = COACode.PA_AGENT_PAYABLE;
			break;
		case GROUP_FARMER_PROPOSAL:
		case FARMER_POLICY:
			coaCode = COACode.FARMER_AGENT_PAYABLE;
			break;
		case PUBLIC_TERM_LIFE_POLICY:
			coaCode = COACode.PUBLICTERMLIFE_AGENT_PAYABLE;
			break;
		case SHORT_ENDOWMENT_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
			coaCode = COACode.SHORT_ENDOWMENT_AGENT_PAYABLE;
			break;
		case MEDICAL_BILL_COLLECTION:
		case MEDICAL_POLICY:
		case HEALTH_POLICY:
		case HEALTH_POLICY_BILL_COLLECTION:
			coaCode = COACode.HEALTH_AGENT_PAYABLE;
			break;
		case MICRO_HEALTH_POLICY:
			coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
			break;
		case CRITICAL_ILLNESS_POLICY:
		case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
			break;
		case GROUP_MICRO_HEALTH:
		case GROUP_MICRO_HEALTH_BILL_COLLECTION:
			coaCode = COACode.LIFE_AGENT_PAYABLE;
			break;
		case PERSON_TRAVEL_POLICY:
			coaCode = COACode.PERSON_TRAVEL_AGENT_PAYABLE;
			break;
		case STUDENT_LIFE_POLICY:
		case STUDENT_LIFE_POLICY_BILL_COLLECTION:
			coaCode = COACode.STUDENT_LIFE_AGENT_PAYABLE;
			break;
		case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			coaCode = COACode.SINGLE_PREMIUM_CREDIT_AGENT_PAYABLE;
			break;
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			coaCode = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_AGENT_PAYABLE;
			break;
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			coaCode = COACode.SINGLE_PREMIUM_ENDOWMENT_AGENT_PAYABLE;
			break;
		default:
			break;
		}

		drCCOACode = paymentService.findCheckOfAccountCode(coaCode, branch, currencyCode);
		if (agentCom.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
			crCCOACode = agentCom.getBank() == null
					? paymentService.findCheckOfAccountCode(COACode.CHEQUE, branch, currencyCode)
					: paymentService.findCCOAByCode(agentCom.getBank().getAcode(), branch.getId(), currencyCode);
		} else if (agentCom.getPaymentChannel().equals(PaymentChannel.CASHED)) {
			crCCOACode = paymentService.findCheckOfAccountCode(COACode.CASH, branch, currencyCode);
		} else if (agentCom.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
			crCCOACode = paymentService.findCheckOfAccountCode(COACode.YANGON_INTERBRANCH, branch, currencyCode);
		}
		// coa name for credit
		coaNameCr = paymentService.findCOAAccountNameByCCOAID(crCCOACode);
		// coa name for debit
		coaNameDr = paymentService.findCOAAccountNameByCCOAID(drCCOACode);

		
		crCCOACode = paymentService.findCOAAccountCodeByCCOAID(crCCOACode);
		drCCOACode = paymentService.findCOAAccountCodeByCCOAID(drCCOACode);


		StaffAgentCommission agentCommission = agentCommissions.get(0);
		Currency currency = currencyService.findCurrencyById(agentCommission.getCUR());
		boolean isUSD = CurrencyUtils.isUSD(currency);
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		AbstractProcessor processor = new DefaultProcessor();
		String wordTotalAmmoumt = isUSD ? processor.getNameWithDecimal(totalCommission, currency)
				: convertor.getName(totalCommission);
		
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("currentDate", new Date());
			paramMap.put("agentName", agentCommission.getStaff().getFullName());
			paramMap.put("idNo", agentCommission.getStaff().getIdNo());
			paramMap.put("address", agentCommission.getStaff().getFullAddress());
			paramMap.put("totalcommission", totalCommission);
			//paramMap.put("agentCode", agent.getLiscenseNo());
			//paramMap.put("licenseNo", agent.getLiscenseNo());
			paramMap.put("wordTotalAmmoumt", wordTotalAmmoumt);
			paramMap.put("isUSD", isUSD);
			InputStream inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("report-template/agent/staffAgentCommissionPaymentOrder.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);
		 JRExporter exporter = new JRPdfExporter();
		 exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST,
		 jasperPrintList);
		 FileHandler.forceMakeDirectory(dirPath);
		 FileOutputStream outputStream = new FileOutputStream(dirPath +
		 fileName);
		 exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM,
		 outputStream);
		 exporter.exportReport();
		 outputStream.close();
		}
	
		
	
	
}
