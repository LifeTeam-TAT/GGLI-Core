/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 * 
 ***************************************************************************************/
package org.ace.insurance.system.common.agent.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.ace.insurance.common.AgentCriteria;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.common.utils.BusinessUtils;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.product.Product;
import org.ace.insurance.report.agent.persistence.interfaces.IAgentInvoiceReportDAO;
import org.ace.insurance.report.agent.persistence.interfaces.IAgentSanctionDAO;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.AGT001;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.COACode;
import org.ace.insurance.system.common.agent.history.AgentHistory;
import org.ace.insurance.system.common.agent.persistence.interfaces.IAgentDAO;
import org.ace.insurance.system.common.agent.service.interfaces.IAgentService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
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
import org.ace.java.component.service.BaseService;
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

@Service(value = "AgentService")
public class AgentService extends BaseService implements IAgentService {

	@Resource(name = "AgentDAO")
	private IAgentDAO agentDAO;

	@Resource(name = "AgentSanctionDAO")
	private IAgentSanctionDAO agentSanctionDAO;

	@Resource(name = "AgentInvoiceReportDAO")
	private IAgentInvoiceReportDAO agentInvoiceDAO;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "UserProcessService")
	private IUserProcessService userProcessService;

	@Resource(name = "MedicalPolicyService")
	private IMedicalPolicyService medicalPolicyService;

	@Resource(name = "LifePolicyService")
	private ILifePolicyService lPolicyService;

	@Resource(name = "PersonTravelPolicyService")
	private IPersonTravelPolicyService personTravelPolicyService;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "CurrencyService")
	private ICurrencyService currencyService;

	@Resource(name = "GroupfarmerProposalService")
	private IGroupfarmerProposalService groupFarmerProposalService;

	@Resource(name = "GroupMicroHealthService")
	private IGroupMicroHealthService groupMicroHealthService;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewAgent(Agent agent) {
		try {
			String agnetCode = null;
			agent.setPrefix(getPrefix(Agent.class));
			if (agent.getGroupType().equals(ProductGroupType.LIFE)) {
				agnetCode = customIDGenerator.getNextId(SystemConstants.AGENT_LIFE_NO, null, agent.getBranch(), false);
			} else if (agent.getGroupType().equals(ProductGroupType.NONLIFE)) {
				agnetCode = customIDGenerator.getNextId(SystemConstants.AGENT_NONELIFE_NO, null, agent.getBranch(),
						false);
			} else if (agent.getGroupType().equals(ProductGroupType.COMPOSITE)) {
				agnetCode = customIDGenerator.getNextId(SystemConstants.AGENT_COMPOSITE_NO, null, agent.getBranch(),
						false);
			}
			agent.setCodeNo(agnetCode);
			agentDAO.insert(agent);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Agent", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Agent updateAgent(Agent agent) {
		try {
			Agent existingAgent = findAgentById(agent.getId());
			if (!existingAgent.getGroupType().equals(agent.getGroupType())) {
				if (agent.getGroupType().equals(ProductGroupType.LIFE)) {
					String agnetCode = customIDGenerator.getNextId(SystemConstants.AGENT_LIFE_NO, null, false);
					agent.setCodeNo(agnetCode);
				} else if (agent.getGroupType().equals(ProductGroupType.NONLIFE)) {
					String agnetCode = customIDGenerator.getNextId(SystemConstants.AGENT_NONELIFE_NO, null, false);
					agent.setCodeNo(agnetCode);
				} else if (agent.getGroupType().equals(ProductGroupType.COMPOSITE)) {
					String agnetCode = customIDGenerator.getNextId(SystemConstants.AGENT_COMPOSITE_NO, null, false);
					agent.setCodeNo(agnetCode);
				}
			}
			agent = agentDAO.update(agent);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Agent", e);
		}
		return agent;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteAgent(Agent agent) {
		try {
			agentDAO.delete(agent);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete  Agent", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<Agent> findAllAgent() {
		List<Agent> result = null;
		try {
			result = agentDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all  Agents", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Agent findAgentById(String id) {
		Agent result = null;
		try {
			result = agentDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Agent (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AGT001> findAgentByCriteria(AgentCriteria criteria, int max) {
		List<AGT001> result = null;
		try {
			result = agentDAO.findByCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(),
					"Faield to find a Agent (criteriaValue : " + criteria.getCriteriaValue() + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void generateAgentInvoice(List<AgentCommission> agentCommissionList, boolean isEnquiry, String dirPath,
			String fileName) {
		AgentCommission agentCom = null;
		Agent agent = null;
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
		String currencyCode = KeyFactorChecker.getKyatId();
		Product product = null;
		List<AgentCommissionReportDTO> agentCommissionReportDTOList = new ArrayList<AgentCommissionReportDTO>();
		/**** Same Data for all AgentCommission *****/
		if (agentCommissionList != null && !agentCommissionList.isEmpty()) {
			agentCom = agentCommissionList.get(0);
			AgentCommission agentCommission = agentSanctionDAO.findAgentCommissionById(agentCom.getId());
			invoiceNo = agentCommission.getInvoiceNo();
			//TODO FIXME PSH
			currencyCode = KeyFactorChecker.getKyatId();
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
		/******************************* END **********************************/

		/************************** Loop by Each Agent ************************/

		Map<String, List<AgentCommission>> map = new HashMap<String, List<AgentCommission>>();
		for (AgentCommission agentCommission : agentCommissionList) {
			if (!map.containsKey(agentCommission.getAgent().getCodeNo())) {
				List<AgentCommission> commissionList = new ArrayList<AgentCommission>();
				commissionList.add(agentCommission);
				map.put(agentCommission.getAgent().getCodeNo(), commissionList);
			} else {
				map.get(agentCommission.getAgent().getCodeNo()).add(agentCommission);
			}
		}

		// loop and print for each agent
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		for (Map.Entry<String, List<AgentCommission>> entry : map.entrySet()) {
			List<AgentCommission> agentCommissions = entry.getValue();
			agent = agentCommissions.get(0).getAgent();
			List<JasperPrint> printList = null;
			if (agentCommissions.size() > 0) {
				for (AgentCommission ac : agentCommissions) {
					totalCommission = totalCommission + (ac.getCommission() - ac.getWithHoldingTax());
					dates.add(ac.getCommissionStartDate());
				}
				minDate = Collections.min(dates);
				maxDate = Collections.max(dates);
			}
			// life is separate
			if (BusinessUtils.isLifePolicyReferenceType(agentCom.getReferenceType())) {
				List<AgentCommissionReportDTO> dtoList = agentInvoiceDAO
						.getAgentCommissionReportDTOForLife(agentCommissions);
				agentCommissionReportDTOList.addAll(dtoList);
			} else {
				for (AgentCommission agentCommission : agentCommissions) {
					List<Payment> paymentList = null;
					if (isEnquiry) {
						paymentList = paymentService.findPaymentByReceiptNo(agentCommission.getReceiptNo());
					} else {
						paymentList = paymentService
								.findPaymentByReferenceNoAndMaxDateForAgentInvoice(agentCommission.getReferenceNo());
					}
					Payment payment = paymentList.get(0);
					AgentCommissionReportDTO agentCommissionReportDTO = new AgentCommissionReportDTO(
							payment.getReceiptNo(), payment.getPaymentDate(), getPolicyNo(agentCommission),
							agentCommission.getCommission(), agentCommission.getWithHoldingTax(),
							agentCommission.getPercentage(), getPremium(agentCommission, payment, isEnquiry),
							getRenewalPremium(agentCommission, payment, isEnquiry), getSumInsured(agentCommission),
							getCustomerName(agentCommission));
					agentCommissionReportDTOList.add(agentCommissionReportDTO);
				}
			}

			printList = generateAgentCommissionInvoiceSlip(invoiceNo, agent, agentCommissions, maxDate, minDate,
					crCCOACode, drCCOACode, coaNameCr, coaNameDr, totalCommission, agentCommissionReportDTOList,
					dirPath, fileName);
			jasperPrintList.addAll(printList);

			// clear data for next loop
			agentCommissionReportDTOList = new ArrayList<AgentCommissionReportDTO>();
			dates = new ArrayList<Date>();
			totalCommission = 0.0;
		}
		/******************************* END **********************************/

		// print
		try {
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Agent Commission Invoice Slip", e);
		}

	}

	private List<JasperPrint> generateAgentCommissionInvoiceSlip(String invoiceNo, Agent agent,
			List<AgentCommission> agentCommisionSelectList, Date maxdate, Date mindate, String codeForCoInsuPayCr,
			String codeForCoInsuPayDr, String coaNameCr, String coaNameDr, Double totalcommission,
			List<AgentCommissionReportDTO> agentCommissionReportDTOList, String dirPath, String fileName) {
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		try {
			AgentCommission agentCommission = agentCommisionSelectList.get(0);
			Currency currency = currencyService.findCurrencyByCurrencyCode(agentCommission.getCUR());
			boolean isUSD = CurrencyUtils.isUSD(currency);
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			AbstractProcessor processor = new DefaultProcessor();
			String wordTotalAmmoumt = isUSD ? processor.getNameWithDecimal(totalcommission, currency)
					: convertor.getName(totalcommission);
			if (agentCommission.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("currentDate", new Date());
				paramMap.put("agentName", agent.getFullName());
				paramMap.put("idNo", agent.getIdNo());
				paramMap.put("address", agent.getFullAddress());
				paramMap.put("totalcommission", totalcommission);
				paramMap.put("agentCode", agent.getLiscenseNo());
				paramMap.put("licenseNo", agent.getLiscenseNo());
				paramMap.put("wordTotalAmmoumt", wordTotalAmmoumt);
				paramMap.put("isUSD", isUSD);
				InputStream inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/agent/AgentCommissionPaymentOrder.jrxml");
				JasperReport jreport = JasperCompileManager.compileReport(inputStream);
				JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
				jasperPrintList.add(jprint);
			}

			PolicyReferenceType producttype = agentCommission.getReferenceType();
			Map<String, Object> paramMap3 = new HashMap();
			if (agentCommission.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				paramMap3.put("cash", true);
				paramMap3.put("paymenttype", "Cash Payment");
			} else {
				paramMap3.put("cash", false);
				paramMap3.put("paymenttype", "Cheque Payment");
			}
			paramMap3.put("coaCodeCr", codeForCoInsuPayCr);
			paramMap3.put("coaCodeDr", codeForCoInsuPayDr);
			paramMap3.put("agentName", agent.getFullName());
			paramMap3.put("licenseNo", agent.getLiscenseNo());
			paramMap3.put("invoiceNo", agentCommission.getInvoiceNo());
			paramMap3.put("coaNameDr", coaNameDr);
			paramMap3.put("coaNameCr", coaNameCr);
			paramMap3.put("nrc", agent.getIdNo());
			paramMap3.put("maxdate", maxdate);
			paramMap3.put("mindate", mindate);
			if (isUSD) {
				paramMap3.put("amount", agentCommission.getHomeCommission());
				paramMap3.put("wordAmount",
						processor.getNameWithDecimal(agentCommission.getHomeCommission(), currency));
				paramMap3.put("rate", agentCommission.getRate());
				paramMap3.put("CUR", agentCommission.getCUR());
				paramMap3.put("commission", agentCommission.getCommission());
				paramMap3.put("USD", true);
				paramMap3.put("wordAmountUSD", processor.getNameWithDecimal(agentCommission.getCommission(), currency));
			} else {
				paramMap3.put("amount", totalcommission);
				paramMap3.put("wordAmount", processor.getNameWithDecimal(totalcommission, currency));
			}

			InputStream inputStream3 = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("report-template/agent/AgentCommission.jrxml");
			JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
			JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, paramMap3, new JREmptyDataSource());
			jasperPrintList.add(jprint3);

			Map<String, Object> paramMap4 = new HashMap();
			paramMap4.put("genInvoiceNo", agentCommission.getInvoiceNo());
			paramMap4.put("date", new Date());
			paramMap4.put("agentName", agent.getFullName());
			paramMap4.put("agentNo", agent.getLiscenseNo());
			paramMap4.put("currencyCode", agentCommission.getCUR() == null ? "KYT" : agentCommission.getCUR());
			if (agent.getFullAddress() != null) {
				paramMap4.put("address", agent.getFullAddress());
			} else {
				paramMap4.put("address", "-");
			}

			if (agent.getIdNo() != null) {
				paramMap4.put("nrc", agent.getIdNo());
			} else {
				paramMap4.put("nrc", "-");
			}

			if (producttype != null) {
				switch (producttype) {
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "SPC LIFE INSURANCE");
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "STSPC LIFE INSURANCE");
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "SPE LIFE INSURANCE");
					break;
				case STUDENT_LIFE_POLICY:
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "STUDENT LIFE INSURANCE");
					break;
				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "LIFE INSURANCE");
					break;
				case MEDICAL_BILL_COLLECTION:
				case MEDICAL_POLICY:
					paramMap4.put("typeOfProduct", "MEDICAL INSURANCE");
					break;
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "HEALTH INSURANCE");
					break;
				case MICRO_HEALTH_POLICY:
				case GROUP_MICRO_HEALTH:
				case GROUP_MICRO_HEALTH_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "MICRO HEALTH INSURANCE");
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					paramMap4.put("typeOfProduct", "CRITICAL ILLNESS INSURANCE");
					break;

				case PA_POLICY:
					paramMap4.put("typeOfProduct", "PERSONAL ACCIDENT INSURANCE");
					break;
				case GROUP_FARMER_PROPOSAL:
				case FARMER_POLICY:
					paramMap4.put("typeOfProduct", "FARMER INSURANCE");
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					paramMap4.put("typeOfProduct", "PUBLIC TERM LIFEINSURANCE");
					break;
				case PERSON_TRAVEL_POLICY:
					paramMap4.put("typeOfProduct", "PERSON TRAVEL INSURANCE");
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					paramMap4.put("typeOfProduct", "SHORT TERM ENDOWMENT LIFE INSURANCE");
					break;
				default:
					paramMap4.put("typeOfProduct", "-");
					break;
				}
			} else {
				paramMap4.put("typeOfProduct", producttype);
			}
			if (agentCommisionSelectList.size() != 0) {
				paramMap4.put("TableDataSource", new JRBeanCollectionDataSource(agentCommissionReportDTOList));
			} else {
				paramMap4.put("TableDataSource", new JREmptyDataSource());
			}
			double totalPremium = 0.0;
			double totalCommissionAmount = 0.0;
			double totalRenewalPremium = 0.0;
			for (AgentCommissionReportDTO agentCommissionReportDTO : agentCommissionReportDTOList) {
				totalPremium += agentCommissionReportDTO.getPremium();
				totalCommissionAmount += agentCommissionReportDTO.getCommissionAmount();
				totalRenewalPremium += agentCommissionReportDTO.getRenewalPremium();
			}
			paramMap4.put("totalPremium", totalPremium);
			paramMap4.put("totalCommissionAmount", totalCommissionAmount);
			paramMap4.put("totalRenewalPremium", totalRenewalPremium);
			InputStream inputStream4 = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("report-template/agent/AgentComissionCustomerReceipt.jrxml");
			JasperReport jreport4 = JasperCompileManager.compileReport(inputStream4);
			JasperPrint jprint4 = JasperFillManager.fillReport(jreport4, paramMap4, new JREmptyDataSource());
			jasperPrintList.add(jprint4);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Agent Commission Invoice Slip", e);
		}
		return jasperPrintList;
	}

	public String getPolicyNo(AgentCommission comm) {
		String policyNo = null;
		IPolicy policy = null;
		switch (comm.getReferenceType()) {
		case STUDENT_LIFE_POLICY:
		case STUDENT_LIFE_POLICY_BILL_COLLECTION:
		case LIFE_POLICY:
		case LIFE_BILL_COLLECTION:
		case PA_POLICY:
		case FARMER_POLICY:
		case PUBLIC_TERM_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
		case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			policy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			policyNo = policy.getPolicyNo();
			break;
		case MEDICAL_BILL_COLLECTION:
		case MEDICAL_POLICY:
		case HEALTH_POLICY:
		case HEALTH_POLICY_BILL_COLLECTION:
		case MICRO_HEALTH_POLICY:
		case CRITICAL_ILLNESS_POLICY:
		case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			policy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
			policyNo = policy.getPolicyNo();
			break;

		case PERSON_TRAVEL_POLICY:
			policy = personTravelPolicyService.findPersonTravelPolicyById(comm.getReferenceNo());
			policyNo = policy.getPolicyNo();
			break;
		case GROUP_FARMER_PROPOSAL:
			GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
			policyNo = proposal.getProposalNo();
			break;
		case GROUP_MICRO_HEALTH:
			GroupMicroHealth groupMicroHealth = groupMicroHealthService.findById(comm.getReferenceNo());
			policyNo = groupMicroHealth.getProposalNo();
			break;
		default:
			break;
		}
		return policyNo;
	}

	public String getCustomerName(AgentCommission comm) {
		String customerName = null;
		IPolicy policy = null;
		switch (comm.getReferenceType()) {
		case MEDICAL_POLICY:
		case MEDICAL_BILL_COLLECTION:
		case HEALTH_POLICY:
		case HEALTH_POLICY_BILL_COLLECTION:
		case MICRO_HEALTH_POLICY:
		case CRITICAL_ILLNESS_POLICY:
		case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			policy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
			break;
		case STUDENT_LIFE_POLICY:
		case STUDENT_LIFE_POLICY_BILL_COLLECTION:
		case LIFE_POLICY:
		case LIFE_BILL_COLLECTION:
		case PA_POLICY:
		case FARMER_POLICY:
		case PUBLIC_TERM_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
		case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			policy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			break;
		case PERSON_TRAVEL_POLICY:
			policy = personTravelPolicyService.findPersonTravelPolicyById(comm.getReferenceNo());
			break;
		default:
			break;
		}
		if (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(comm.getReferenceType())) {
			GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
			customerName = proposal.getOrganization().getName();
		} else if (PolicyReferenceType.GROUP_MICRO_HEALTH.equals(comm.getReferenceType())) {
			customerName = "-";
		} else {
			customerName = policy.getCustomerName();
		}
		return customerName;
	}

	public double getPremium(AgentCommission comm, Payment payment, boolean isEnquiry) {
		double premium = 0.0;
		switch (comm.getReferenceType()) {
		case MEDICAL_POLICY:
		case MEDICAL_BILL_COLLECTION:
		case HEALTH_POLICY:
		case HEALTH_POLICY_BILL_COLLECTION:
		case MICRO_HEALTH_POLICY:
		case CRITICAL_ILLNESS_POLICY:
		case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
			premium = ProposalType.RENEWAL.equals(medicalPolicy.getMedicalProposal().getProposalType()) ? 0.0
					: comm.getPremium();
			break;
		case STUDENT_LIFE_POLICY:
		case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
		case LIFE_POLICY:
		case PA_POLICY:
		case FARMER_POLICY:
			LifePolicy lPolicy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			premium = lPolicy.getTotalPremium();
			break;
		case PUBLIC_TERM_LIFE_POLICY:
			LifePolicy plPolicy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			premium = plPolicy.getTotalPremium();
			break;
		case LIFE_BILL_COLLECTION:
		case STUDENT_LIFE_POLICY_BILL_COLLECTION:
		case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			LifePolicy lifePolicy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			if (isEnquiry) {
				int toTerm = payment.getToTerm();
				int paymentMonth = lifePolicy.getPaymentType().getMonth();
				if (toTerm * paymentMonth <= 12) {
					premium = lifePolicy.getTotalTermPremium();
				} else {
					premium = 0.0;
				}
			} else {
				int lastPaymentTerm = lifePolicy.getLastPaymentTerm();
				int paymentCountForYear = 12 / lifePolicy.getPaymentType().getMonth();
				if (lastPaymentTerm <= paymentCountForYear) {
					premium = lifePolicy.getTotalTermPremium();
				} else {
					premium = 0.0;
				}
			}
			break;
		case PERSON_TRAVEL_POLICY:
			PersonTravelPolicy travelPolicy = personTravelPolicyService
					.findPersonTravelPolicyById(comm.getReferenceNo());
			premium = travelPolicy.getPremium();
			break;
		case GROUP_FARMER_PROPOSAL:
			GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
			premium = proposal.getPremium();
			break;
		default:
			break;
		}
		return premium;
	}

	public double getRenewalPremium(AgentCommission comm, Payment payment, boolean isEnquiry) {
		double renewalPremium = 0.0;
		switch (comm.getReferenceType()) {

		case MEDICAL_POLICY:
		case MEDICAL_BILL_COLLECTION:
		case HEALTH_POLICY:
		case HEALTH_POLICY_BILL_COLLECTION:
		case MICRO_HEALTH_POLICY:
		case CRITICAL_ILLNESS_POLICY:
		case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
			renewalPremium = ProposalType.RENEWAL.equals(medicalPolicy.getMedicalProposal().getProposalType())
					? comm.getPremium()
					: 0.0;
			break;
		case PA_POLICY:
		case LIFE_POLICY:
		case FARMER_POLICY:
			renewalPremium = 0.0;
			break;
		case PUBLIC_TERM_LIFE_POLICY:
			renewalPremium = 0.0;
			break;
		case LIFE_BILL_COLLECTION:
			LifePolicy lPolicy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			if (isEnquiry) {
				int toTerm = payment.getToTerm();
				int paymentMonth = lPolicy.getPaymentType().getMonth();
				if (toTerm * paymentMonth <= 12) {
					renewalPremium = 0.0;
				} else {
					renewalPremium = lPolicy.getTotalTermPremium();
				}
			} else {
				int lastPaymentTerm = lPolicy.getLastPaymentTerm();
				int paymentCountForYear = 12 / lPolicy.getPaymentType().getMonth();
				// within one year payment
				if (lastPaymentTerm <= paymentCountForYear) {
					renewalPremium = 0.0;
				} else {
					renewalPremium = lPolicy.getTotalTermPremium();
				}
			}
			break;

		default:
			break;
		}
		return renewalPremium;
	}

	public double getSumInsured(AgentCommission comm) {
		double sumInsured = 0.0;
		IPolicy policy = null;
		GroupFarmerProposal proposal = null;
		GroupMicroHealth groupMicroHealth = null;
		switch (comm.getReferenceType()) {
		case MEDICAL_BILL_COLLECTION:
		case MEDICAL_POLICY:
		case HEALTH_POLICY:
		case HEALTH_POLICY_BILL_COLLECTION:
		case MICRO_HEALTH_POLICY:
		case CRITICAL_ILLNESS_POLICY:
		case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			policy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
			break;
		case LIFE_POLICY:
		case LIFE_BILL_COLLECTION:
		case PA_POLICY:
		case FARMER_POLICY:
		case PUBLIC_TERM_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_POLICY:
		case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
		case STUDENT_LIFE_POLICY:
		case STUDENT_LIFE_POLICY_BILL_COLLECTION:
		case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
		case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
		case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			policy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			break;
		case PERSON_TRAVEL_POLICY:
			policy = personTravelPolicyService.findPersonTravelPolicyById(comm.getReferenceNo());
			break;
		case GROUP_FARMER_PROPOSAL:
			proposal = groupFarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
			break;
		case GROUP_MICRO_HEALTH:
			groupMicroHealth = groupMicroHealthService.findById(comm.getReferenceNo());
			break;
		default:
			break;
		}
		if (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(comm.getReferenceType())) {
			sumInsured = proposal.getTotalSI();
		} else if (PolicyReferenceType.GROUP_MICRO_HEALTH.equals(comm.getReferenceType())) {
			sumInsured = groupMicroHealth.getTotalPremium();
		} else {
			sumInsured = policy.getTotalSumInsured();
		}
		return sumInsured;
	}

	public double getCommissionPercentage(AgentCommission comm, Payment payment, boolean isEnquiry) {
		double commissionPercentage = 0.0;
		PolicyReferenceType rType = comm.getReferenceType();
		if (rType.equals(PolicyReferenceType.LIFE_POLICY)) {
			LifePolicy lPolicy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			commissionPercentage = lPolicy.getPolicyInsuredPersonList().get(0).getProduct().getFirstCommission();
		} else if (rType.equals(PolicyReferenceType.LIFE_BILL_COLLECTION)) {
			LifePolicy lPolicy = lPolicyService.findLifePolicyById(comm.getReferenceNo());
			if (isEnquiry) {
				int toTerm = payment.getToTerm();
				int paymentMonth = lPolicy.getPaymentType().getMonth();
				if (toTerm * paymentMonth <= 12) {
					commissionPercentage = lPolicy.getPolicyInsuredPersonList().get(0).getProduct()
							.getFirstCommission();
				} else {
					commissionPercentage = lPolicy.getPolicyInsuredPersonList().get(0).getProduct()
							.getRenewalCommission();
				}
			} else {
				int lastPaymentTerm = lPolicy.getLastPaymentTerm();
				int paymentCountForYear = 12 / lPolicy.getPaymentType().getMonth();
				// within one year payment
				if (lastPaymentTerm <= paymentCountForYear) {
					commissionPercentage = lPolicy.getPolicyInsuredPersonList().get(0).getProduct()
							.getFirstCommission();
				} else {
					commissionPercentage = lPolicy.getPolicyInsuredPersonList().get(0).getProduct()
							.getRenewalCommission();
				}
			}
		}
		return commissionPercentage;
	}

	public Date getPaymentDate(AgentCommission comm) {
		Date paymentDate = null;
		List<Payment> paymentList = paymentService
				.findPaymentByReferenceNoAndMaxDateForAgentInvoice(comm.getReferenceNo());
		if (paymentList != null) {
			return paymentDate = paymentList.get(0).getPaymentDate();
		}
		return paymentDate;
	}

	public String getCashReceipt(AgentCommission comm) {
		String receiptNo = null;
		List<Payment> paymentList = paymentService
				.findPaymentByReferenceNoAndMaxDateForAgentInvoice(comm.getReferenceNo());
		if (paymentList != null) {
			return receiptNo = paymentList.get(0).getReceiptNo();
		}
		return receiptNo;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isExitingAgent(Agent agent) {
		boolean result;
		try {
			result = agentDAO.isExitingAgent(agent);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to check  Agent", e);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewAgentHistory(AgentHistory agentHistory) throws SystemException {
		try {
			agentDAO.inserthistory(agentHistory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Agent", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void CreateHistoryAndRemoveAgentById(String agentId) throws SystemException {
		try {
			Agent agent = findAgentById(agentId);
			AgentHistory agenthistory = new AgentHistory(agent);
			addNewAgentHistory(agenthistory);
			deleteAgent(agent);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to Delete Agent", e);
		}

	}
}
