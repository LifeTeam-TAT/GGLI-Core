package org.ace.insurance.web.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.cashreceipt.LifeCashReceiptListReportDTO;
import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.CoinsuranceDTO;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.insurance.life.claim.LifeDisabilityClaim;
import org.ace.insurance.life.migrate.persistence.interfaces.IShortEndowmentExtraValueDAO;
import org.ace.insurance.life.paidUp.LifePaidUpProposal;
import org.ace.insurance.life.policy.EndorsementLifePolicyPrint;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.PolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.AgentPaymentCashReceiptDTO;
import org.ace.insurance.payment.CoinsuranceCashReceiptDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.PaymentOrderCashReceiptDTO;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.product.Product;
import org.ace.insurance.report.ClaimVoucher.ClaimVoucherDTO;
import org.ace.insurance.report.TLF.TLFVoucherDTO;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.PolicyPersonTravelVehicle;
import org.ace.insurance.travel.personTravel.policy.PolicyTraveller;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.common.ntw.eng.AbstractProcessor;
import org.ace.insurance.web.common.ntw.eng.DefaultProcessor;
import org.ace.insurance.web.common.ntw.mym.AbstractMynNumConvertor;
import org.ace.insurance.web.common.ntw.mym.DefaultConvertor;
import org.ace.insurance.web.common.ntw.mym.NumberToNumberConvertor;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionReportDTO;
import org.ace.insurance.web.manage.agent.payment.StaffCommissionDTO;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionCashReceiptDTO;
import org.ace.insurance.web.manage.life.claim.LifeClaimDischargeFormDTO;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.web.ApplicationSetting;
import org.joda.time.DateTime;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class DocumentBuilder extends BasicDAO {

	@Resource(name = "ShortEndowmentExtraValueDAO")
	private IShortEndowmentExtraValueDAO extraValueDAO;

	public static void generateCustomerInfo(Customer customer, String dirPath, String fileName) {
		try {
			List<JasperPrint> jlist = new ArrayList<JasperPrint>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", customer.getFullName());
			params.put("dateOfBirth", customer.getDateOfBirth());
			params.put("idNo", customer.getFullIdNo());
			params.put("birthMark", customer.getBirthMark());
			params.put("maritalStatus", customer.getMaritalStatus().getLabel());
			params.put("nationality", customer.getCountry() == null ? "" : customer.getCountry().getName());
			params.put("religion", customer.getReligion() == null ? "" : customer.getReligion().getName());
			params.put("qualification", customer.getQualification() == null ? "" : customer.getQualification().getName());
			params.put("bankBranch", customer.getBankBranch() == null ? "" : customer.getBankBranch().getName());
			params.put("accountNo", customer.getBankAccountNo());
			params.put("industry", customer.getIndustry() == null ? "" : customer.getIndustry().getName());
			params.put("occupation", customer.getOccupation() == null ? "" : customer.getOccupation().getName());
			params.put("salary", customer.getSalary());
			params.put("residentAddress", customer.getFullAddress());
			params.put("officeAddress", customer.getOfficeAddress().getOfficeAddress());
			params.put("parmanentAddress", customer.getPermanentAddress().getFullAddress());
			params.put("mobile", customer.getContentInfo().getMobile());
			params.put("email", customer.getContentInfo().getEmail());
			params.put("fax", customer.getContentInfo().getFax());

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/medical/CustomerInfo.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, params, new JREmptyDataSource());
			if (jprint.getPages().size() > 1) {
				jprint.getPages().remove(1);
			}
			jlist.add(jprint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jlist);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate CustomerInfo", e);
		}
	}

	private static Map<String, Object> prepareCreditDebitReportParams(String insuredName, double siAmount, double totalPremium, CoinsuranceCashReceiptDTO dto) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cashReceiptNo", dto.getCashReceiptNo());
		params.put("sundryAccountCode", dto.getSundryAccountCode());
		params.put("sundryAccountName", dto.getSundryAccountName());
		params.put("sundryDrAmount", dto.getSundryDrAmount());
		params.put("sundryCrAmount", dto.getSundryCrAmount());
		params.put("agentCommissionAcountCode", dto.getAgentCommissionAcountCode());
		params.put("agentCommissionAccountName", dto.getAgentCommissionAccountName());
		params.put("agentCommissionCrAmount", dto.getAgentCommissionCrAmount());
		params.put("premiumIncomeAccountCode", dto.getPremiumIncomeAccountCode());
		params.put("premiumIncomeAccountName", dto.getPremiumIncomeAccountName());
		params.put("premiumIncomeCrAmount", dto.getPremiumIncomeCrAmount());
		params.put("insuredName", insuredName);
		params.put("siAmount", dto.getSiAmount());
		params.put("totalPremiumAmount", dto.getSundryCrAmount());
		params.put("date", new Date());
		return params;
	}

	private static Map<String, Object> prepareExpensePayableReportParams(String agentName, double siAmount, double netPremium, AgentPaymentCashReceiptDTO dto, PaymentDTO payment) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentExpenseAcountCode", dto.getAgentExpenseAcountCode());
		params.put("agentExpenseAccountName", dto.getAgentExpenseAccountName());
		params.put("agentPayableAccountCode", dto.getAgentPayableAccountCode());
		params.put("agentPayableAccountName", dto.getAgentPayableAccountName());
		params.put("cashReceiptNo", dto.getCashReceiptNo());
		params.put("agentExpenseDbAmount", dto.getAgentExpenseDbAmount());
		params.put("agentPayableCrAmount", dto.getAgentPayableCrAmount());
		params.put("confirmDate", dto.getConfirmDate());
		params.put("agentName", agentName);
		params.put("siAmount", siAmount);
		params.put("totalPremiumAmount", netPremium);
		params.put("confirmDate", payment.getConfirmDate());
		return params;
	}

	private static Map<String, Object> preparePaymentOrderReportParams(String insuredName, double siAmount, double netPremium, PaymentOrderCashReceiptDTO dto, PaymentDTO payment) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("suspendAccountCode", dto.getSuspendAccountCode());
		params.put("suspendAccountName", dto.getSuspendAccountName());
		params.put("premiumIncomeAccountCode", dto.getPremiumIncomeAccountCode());
		params.put("premiumIncomeAccountName", dto.getPremiumIncomeAccountName());
		params.put("totalPremiumAmount", netPremium);
		params.put("suspendDbAmount", netPremium);
		params.put("premiumIncomeCrAmount", netPremium);
		params.put("cashReceiptNo", dto.getCashReceiptNo());
		params.put("insuredName", insuredName);
		params.put("siAmount", siAmount);
		params.put("confirmDate", payment.getConfirmDate());
		return params;
	}

	public static void generatePAPolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<>();
			PolicyInsuredPerson person = lifePolicy.getPolicyInsuredPersonList().get(0);
			Customer customer = lifePolicy.getCustomer();
			boolean isMMK = KeyFactorChecker.isPersonalAccident(person.getProduct());
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			AbstractProcessor processor = new DefaultProcessor();
			double sumInsured = lifePolicy.getSumInsured();
			double premium = person.getPremium() + person.getAddOnTermPremium();
			paramMap.put("policyNo", lifePolicy.getPolicyNo());
			paramMap.put("agentLicenseNo", lifePolicy.getAgent() != null ? lifePolicy.getAgent().getLiscenseNo() : "");
			paramMap.put("insuredPersonName", lifePolicy.getCustomerName());
			paramMap.put("fatherName", customer == null ? person.getFatherName() : customer.getFatherName());
			paramMap.put("idNo", customer == null ? person.getIdNo() : customer.getIdNo());
			String occupation = customer == null ? person.getOccupation() == null ? "" : person.getOccupation().getName()
					: customer.getOccupation() != null ? customer.getOccupation().getName() : "";
			paramMap.put("Occupation", occupation);
			String residentAddress = customer == null ? person.getResidentAddress() == null ? "" : person.getResidentAddress().getFullResidentAddress()
					: customer.getResidentAddress() != null ? customer.getResidentAddress().getFullResidentAddress() : "";
			paramMap.put("address", residentAddress);
			paramMap.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(sumInsured));
			paramMap.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(premium));
			if (isMMK) {
				paramMap.put("sumInsuredInWord", convertor.getNameWithDecimal(sumInsured));
				paramMap.put("premiumInWord", convertor.getNameWithDecimal(premium));
			} else {
				paramMap.put("sumInsuredInWord", processor.getNameWithDecimal(sumInsured, person.getProduct().getCurrency()));
				paramMap.put("premiumInWord", processor.getNameWithDecimal(premium, person.getProduct().getCurrency()));
			}
			paramMap.put("policyStartDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getActivedPolicyStartDate()));
			paramMap.put("policyEndDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getActivedPolicyEndDate()));
			paramMap.put("period", person.getPeriodMonth());
			paramMap.put("beneficiaryList", person.getPolicyInsuredPersonBeneficiariesList());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramMap.put("agentName", agent.getFullName());
			} else {
				paramMap.put("agentName", "");
			}
			paramMap.put("isMMK", isMMK);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personalAccident/PA_PolicyIssue.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			List<JasperPrint> jpList = new ArrayList<>();
			jpList.add(jprint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document Personal Accident Policy Issue", e);
		}
	}

	public static void generatePAGroupPolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<>();
			PolicyInsuredPerson person = lifePolicy.getPolicyInsuredPersonList().get(0);
			boolean isMMK = KeyFactorChecker.isPersonalAccident(person.getProduct());
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			AbstractProcessor processor = new DefaultProcessor();
			double sumInsured = lifePolicy.getSumInsured();
			double premium = lifePolicy.getPremium();
			paramMap.put("policyNo", lifePolicy.getPolicyNo());
			paramMap.put("customerName", lifePolicy.getOrganizationName() != null ? lifePolicy.getOrganizationName() : "");
			paramMap.put("address", person.getResidentAddress() != null ? person.getResidentAddress().getFullResidentAddress() : "");
			paramMap.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(sumInsured));
			paramMap.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(premium));
			if (isMMK) {
				paramMap.put("sumInsuredInWord", convertor.getNameWithDecimal(sumInsured));
				paramMap.put("premiumInWord", convertor.getNameWithDecimal(premium));
			} else {
				paramMap.put("sumInsuredInWord", processor.getNameWithDecimal(sumInsured, person.getProduct().getCurrency()));
				paramMap.put("premiumInWord", processor.getNameWithDecimal(premium, person.getProduct().getCurrency()));
			}
			paramMap.put("policyStartDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getActivedPolicyStartDate()));
			paramMap.put("policyEndDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getActivedPolicyEndDate()));
			paramMap.put("period", person.getPeriodMonth());
			paramMap.put("beneficiaryList", person.getPolicyInsuredPersonBeneficiariesList());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramMap.put("agentName", agent.getFullName());
				paramMap.put("agentLicenseNo", agent.getLiscenseNo());
			}
			paramMap.put("isMMK", isMMK);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personalAccident/PA_GroupPolicyIssue.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());

			Map<String, Object> pa_insuredPesronMap = new HashMap<String, Object>();
			pa_insuredPesronMap.put("policyNo", lifePolicy.getPolicyNo());
			pa_insuredPesronMap.put("insuredPersonList", new JRBeanCollectionDataSource(lifePolicy.getInsuredPersonInfo()));
			InputStream pa_insuredPersonIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personalAccident/PA_PolicyInsuredPerson.jrxml");
			JasperReport pa_insuredPersonJR = JasperCompileManager.compileReport(pa_insuredPersonIS);
			JasperPrint pa_insuredPersonPrint = JasperFillManager.fillReport(pa_insuredPersonJR, pa_insuredPesronMap, new JREmptyDataSource());

			Map<String, Object> pa_benfMap = new HashMap<String, Object>();
			pa_benfMap.put("policyNo", lifePolicy.getPolicyNo());
			pa_benfMap.put("insuredPersonList", new JRBeanCollectionDataSource(lifePolicy.getInsuredPersonInfo()));
			InputStream pa_beneficiariesIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personalAccident/PA_PolicyBeneficiaries.jrxml");
			JasperReport pa_beneficiariesISJR = JasperCompileManager.compileReport(pa_beneficiariesIS);
			JasperPrint pa_beneficiariesISPrint = JasperFillManager.fillReport(pa_beneficiariesISJR, pa_benfMap, new JREmptyDataSource());
			List<JasperPrint> jpList = new ArrayList<>();
			jpList.add(jprint);
			jpList.add(pa_insuredPersonPrint);
			jpList.add(pa_beneficiariesISPrint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document Personal Accident Policy Issue", e);
		}
	}

	public static void generatePersonTravelPolicyIssue(PersonTravelPolicy personTravelPolicy, String dirPath, String fileName) {
		OutputStream outputStream = null;
		try {
			if (personTravelPolicy.getPersonTravelPolicyInfo().getPolicyTravellerList().size() == 1) {
				Map<String, Object> params = new HashMap<String, Object>();
				PolicyTraveller policyTraveller = personTravelPolicy.getPersonTravelPolicyInfo().getPolicyTravellerList().get(0);

				params.put("policyNo", personTravelPolicy.getPolicyNo());
				if (personTravelPolicy.getAgent() != null) {
					params.put("agentName", personTravelPolicy.getAgent().getFullName());
					params.put("agentCode", personTravelPolicy.getAgent().getLiscenseNo());
				} else {
					params.put("agentName", "");
					params.put("agentCode", "");
				}
				params.put("date", new Date());
				params.put("policyTravellerName", policyTraveller.getName());
				params.put("nrcNo", policyTraveller.getNrcNo());
				params.put("fatherName", policyTraveller.getFatherName());
				params.put("address", policyTraveller.getAddress());
				params.put("phoneNo", policyTraveller.getPhone());
				params.put("email", policyTraveller.getEmail());
				params.put("unit", policyTraveller.getUnit());
				params.put("premiumRate", personTravelPolicy.getPersonTravelPolicyInfo().getPremiumRate());
				params.put("premium", policyTraveller.getPremium());
				params.put("travelPath", personTravelPolicy.getPersonTravelPolicyInfo().getTravelPath());
				params.put("departureDate", personTravelPolicy.getPersonTravelPolicyInfo().getDepartureDate());
				params.put("arrivalDate", personTravelPolicy.getPersonTravelPolicyInfo().getArrivalDate());

				int c = 0;
				for (PolicyPersonTravelVehicle vehicle : personTravelPolicy.getPersonTravelPolicyInfo().getPolicyPersonTravelVehicleList()) {
					c++;
					String vehicleNo = vehicle.getUsageOfVehicle().getLabel() + " (" + vehicle.getRegistrationNo() + ")";
					params.put("usageOfVehicle" + c, vehicleNo);
					params.put("isUsageOfVehicle" + c, true);
				}
				params.put("beneficiariesList", policyTraveller.getPolicyTravellerBeneficiaryList());

				InputStream policyIS = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/travel/personTravel/personTravelIndividualPolicyIssue.jrxml");
				JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
				JasperPrint policyJP = JasperFillManager.fillReport(policyJR, params, new JREmptyDataSource());

				JRExporter exporter = new JRPdfExporter();
				exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, policyJP);
				FileHandler.forceMakeDirectory(dirPath);
				outputStream = new FileOutputStream(dirPath + fileName);
				exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
				exporter.exportReport();
			} else {

				Map<String, Object> coverMap = new HashMap<String, Object>();
				coverMap.put("date", new Date());
				coverMap.put("policyNo", personTravelPolicy.getPolicyNo());
				if (personTravelPolicy.getAgent() != null) {
					coverMap.put("agentNameCode", personTravelPolicy.getAgent().getFullName() + "/" + personTravelPolicy.getAgent().getLiscenseNo());
				} else {
					coverMap.put("agentNameCode", "");
				}
				String customerName = personTravelPolicy.getCustomer() != null ? personTravelPolicy.getCustomer().getFullName() : personTravelPolicy.getOrganization().getName();
				String idNo = personTravelPolicy.getCustomer() != null ? personTravelPolicy.getCustomer().getIdNo() : "";
				String fatherName = personTravelPolicy.getCustomer() != null ? personTravelPolicy.getCustomer().getFatherName() : "";
				String address = personTravelPolicy.getCustomer() != null ? personTravelPolicy.getCustomer().getFullAddress()
						: personTravelPolicy.getOrganization().getFullAddress();
				coverMap.put("customer", customerName);
				coverMap.put("idNo", idNo);
				coverMap.put("fatherName", fatherName);
				coverMap.put("address", address);
				coverMap.put("unit", personTravelPolicy.getPersonTravelPolicyInfo().getTotalUnit());
				coverMap.put("premiumRate", personTravelPolicy.getPersonTravelPolicyInfo().getPremiumRate());
				coverMap.put("departureDay", personTravelPolicy.getPersonTravelPolicyInfo().getDepartureDate());
				coverMap.put("arrivalDay", personTravelPolicy.getPersonTravelPolicyInfo().getArrivalDate());
				coverMap.put("premium", personTravelPolicy.getPersonTravelPolicyInfo().getPremium());
				coverMap.put("travelPlace", personTravelPolicy.getPersonTravelPolicyInfo().getTravelPath());

				int c = 0;
				for (PolicyPersonTravelVehicle vehicle : personTravelPolicy.getPersonTravelPolicyInfo().getPolicyPersonTravelVehicleList()) {
					c++;
					String vehicleNo = vehicle.getUsageOfVehicle().getLabel() + " (" + vehicle.getRegistrationNo() + ")";
					coverMap.put("usageOfVehicle" + c, vehicleNo);
					coverMap.put("isUsageOfVehicle" + c, true);
				}
				InputStream coverIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personTravel/personTravelPolicyIssue.jrxml");
				JasperReport coverJR = JasperCompileManager.compileReport(coverIS);
				JasperPrint coverPrint = JasperFillManager.fillReport(coverJR, coverMap, new JREmptyDataSource());
				Map<String, Object> insuredPesronMap = new HashMap<String, Object>();
				insuredPesronMap.put("policyNo", personTravelPolicy.getPolicyNo());
				insuredPesronMap.put("customerName", customerName);
				insuredPesronMap.put("insuredPersonDataList", new JRBeanCollectionDataSource(personTravelPolicy.getPersonTravelPolicyInfo().getPolicyTravellerList()));
				InputStream insuredPersonIS_2 = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/personTravel/persontravelInsuredPersonList.jrxml");
				JasperReport insuredPersonJR_2 = JasperCompileManager.compileReport(insuredPersonIS_2);
				JasperPrint insuredPersonPrint_2 = JasperFillManager.fillReport(insuredPersonJR_2, insuredPesronMap, new JREmptyDataSource());
				Map<String, Object> benfMap = new HashMap<String, Object>();
				benfMap.put("policyNo", personTravelPolicy.getPolicyNo());
				benfMap.put("listDataSource", new JRBeanCollectionDataSource(personTravelPolicy.getPersonTravelPolicyInfo().getPolicyTravellerList()));
				InputStream beneficiariesIS = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/personTravel/persontravelBeneficiaryPolicyIssue.jrxml");
				JasperReport beneficiariesISJR = JasperCompileManager.compileReport(beneficiariesIS);
				JasperPrint beneficiariesISPrint = JasperFillManager.fillReport(beneficiariesISJR, benfMap, new JREmptyDataSource());
				List<JasperPrint> jpList = new ArrayList<JasperPrint>();
				jpList.add(coverPrint);
				jpList.add(insuredPersonPrint_2);
				jpList.add(beneficiariesISPrint);
				JRExporter exporter = new JRPdfExporter();
				exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
				FileHandler.forceMakeDirectory(dirPath);
				outputStream = new FileOutputStream(dirPath + fileName);
				exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
				exporter.exportReport();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static void generatePublicLifePolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> coverMap = new HashMap<String, Object>();
			coverMap.put("policyNo", lifePolicy.getPolicyNo());
			// coverMap.put("customerName", lifePolicy.getCustomerName());
			coverMap.put("customerName", lifePolicy.getInsuredPersonInfo().get(0).getFullName());
			coverMap.put("totalSumInsured", lifePolicy.getSumInsured());
			InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyIssueCover.jrxml");
			JasperReport jreport1 = JasperCompileManager.compileReport(inputStream1);
			JasperPrint jprint1 = JasperFillManager.fillReport(jreport1, coverMap, new JREmptyDataSource());
			jpList.add(jprint1);
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			paramIssueDetails.put("receiptNo", paymentDTO.getReceiptNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName() + " (" + agent.getLiscenseNo() + ")");
			} else {
				paramIssueDetails.put("agent", "");
			}
			paramIssueDetails.put("productName", lifePolicy.getInsuredPersonInfo().get(0).getProduct().getName());
			paramIssueDetails.put("periodYears", lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears());
			paramIssueDetails.put("sumInsured", lifePolicy.getSumInsured());
			paramIssueDetails.put("customerName", lifePolicy.getInsuredPersonInfo().get(0).getFullName());
			paramIssueDetails.put("occupation",
					lifePolicy.getInsuredPersonInfo().get(0).getOccupation() == null ? "" : lifePolicy.getInsuredPersonInfo().get(0).getOccupation().getName());
			paramIssueDetails.put("customerAddress", lifePolicy.getInsuredPersonInfo().get(0).getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("ageForNextYear", lifePolicy.getInsuredPersonInfo().get(0).getAge());
			paramIssueDetails.put("totalPremium", lifePolicy.getTotalPremium());
			paramIssueDetails.put("totalTermPremium", lifePolicy.getTotalTermPremium());
			paramIssueDetails.put("paymentType", lifePolicy.getPaymentType().getMonth());
			paramIssueDetails.put("commenmanceDate", lifePolicy.getCommenmanceDate());
			paramIssueDetails.put("endDate", lifePolicy.getInsuredPersonInfo().get(0).getEndDate());
			paramIssueDetails.put("lastPaymentDate", lifePolicy.getInsuredPersonInfo().get(0).getLastPaymentDate());
			List<PolicyInsuredPersonBeneficiaries> benfList = lifePolicy.getInsuredPersonInfo().get(0).getPolicyInsuredPersonBeneficiariesList();
			if (benfList.size() > 2) {
				paramIssueDetails.put("policyInsuredPersonBeneficiariesList", null);
			} else {
				paramIssueDetails.put("policyInsuredPersonBeneficiariesList", benfList);
			}
			paramIssueDetails.put("timeSlotList", lifePolicy.getInsuredPersonInfo().get(0).getTimeSlotList());
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/publicLifePolicyIssue.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint2);
			if (benfList.size() > 2) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("policyNo", lifePolicy.getPolicyNo());
				paramMap.put("idNo", lifePolicy.getInsuredPersonInfo().get(0).getIdNo());
				paramMap.put("insuredPersonName", lifePolicy.getCustomerName());
				paramMap.put("nrc", lifePolicy.getInsuredPersonInfo().get(0).getIdNo());
				paramMap.put("listDataSource", new JRBeanCollectionDataSource(benfList));
				InputStream inputStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyBeneficiaries.jrxml");
				JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
				JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, paramMap, new JREmptyDataSource());
				jpList.add(jprint3);
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document public life policy issue", e);
		}
	}

	public static void generateSportManPolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> coverMap = new HashMap<String, Object>();
			coverMap.put("policyNo", lifePolicy.getPolicyNo());
			coverMap.put("customerName", lifePolicy.getCustomerName());
			coverMap.put("totalSumInsured", lifePolicy.getSumInsured());
			InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyIssueCover.jrxml");
			JasperReport jreport1 = JasperCompileManager.compileReport(inputStream1);
			JasperPrint jprint1 = JasperFillManager.fillReport(jreport1, coverMap, new JREmptyDataSource());
			jpList.add(jprint1);
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			paramIssueDetails.put("receiptNo", paymentDTO.getReceiptNo());
			/* Myanmar */
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			paramIssueDetails.put("myanSumInsured", convertor.getName(lifePolicy.getTotalSumInsured()));
			paramIssueDetails.put("myanPremium", convertor.getName(lifePolicy.getTotalPremium()));

			paramIssueDetails.put("myanSumInsuredNum", NumberToNumberConvertor.getMyanmarNumber(lifePolicy.getTotalSumInsured(), true));
			paramIssueDetails.put("myanPremiumNum", NumberToNumberConvertor.getMyanmarNumber(lifePolicy.getTotalPremium(), true));

			paramIssueDetails.put("sumInsured", lifePolicy.getSumInsured());
			paramIssueDetails.put("customerName", lifePolicy.getInsuredPersonInfo().get(0).getFullName());
			paramIssueDetails.put("customerAddress", lifePolicy.getInsuredPersonInfo().get(0).getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("age", lifePolicy.getInsuredPersonInfo().get(0).getAge());
			paramIssueDetails.put("totalPremium", lifePolicy.getTotalPremium());
			paramIssueDetails.put("commenmanceDate", lifePolicy.getCommenmanceDate());
			paramIssueDetails.put("endDate", lifePolicy.getActivedPolicyEndDate());
			paramIssueDetails.put("startDate", lifePolicy.getActivedPolicyStartDate());
			paramIssueDetails.put("regNo", lifePolicy.getPolicyInsuredPersonList().get(0).getIdNo());
			if (lifePolicy.getPolicyInsuredPersonList().get(0).getTypesOfSport() != null) {
				paramIssueDetails.put("typesOfSport", lifePolicy.getPolicyInsuredPersonList().get(0).getTypesOfSport().getName());
			} else {
				paramIssueDetails.put("typesOfSport", "");
			}
			List<PolicyInsuredPersonBeneficiaries> benfList = lifePolicy.getInsuredPersonInfo().get(0).getPolicyInsuredPersonBeneficiariesList();

			if (benfList.size() > 1) {
				paramIssueDetails.put("policyInsuredPersonBeneficiariesList", null);
			} else {
				// paramIssueDetails.put("policyInsuredPersonBeneficiariesList",
				// benfList);
				paramIssueDetails.put("befName", benfList.get(0).getFullName());
				paramIssueDetails.put("befAge", benfList.get(0).getAge());
				paramIssueDetails.put("befRegNo", benfList.get(0).getIdNo());
				paramIssueDetails.put("befAddress", benfList.get(0).getResidentAddress().getFullResidentAddress());
				paramIssueDetails.put("befRelationship", benfList.get(0).getRelationship().getName());
			}

			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/sportManPolicyIssue.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint2);
			if (benfList.size() > 1) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("policyNo", lifePolicy.getPolicyNo());
				paramMap.put("idNo", lifePolicy.getInsuredPersonInfo().get(0).getIdNo());
				paramMap.put("insuredPersonName", lifePolicy.getCustomerName());
				paramMap.put("nrc", lifePolicy.getInsuredPersonInfo().get(0).getIdNo());
				paramMap.put("listDataSource", new JRBeanCollectionDataSource(benfList));
				InputStream inputStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyBeneficiaries.jrxml");
				JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
				JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, paramMap, new JREmptyDataSource());
				jpList.add(jprint3);
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document public life policy issue", e);
		}
	}

	public static void generateFarmerPolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			PolicyInsuredPerson person = lifePolicy.getInsuredPersonInfo().get(0);
			Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			boolean isFarmer = KeyFactorChecker.isFarmer(product);
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			paramIssueDetails.put("premium", lifePolicy.getTotalPremium());
			paramIssueDetails.put("insuredPersonName", person.getFullName());
			paramIssueDetails.put("nrc", person.getIdNo());
			paramIssueDetails.put("fatherName", person.getFatherName());
			paramIssueDetails.put("address", person.getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("phone", "");
			paramIssueDetails.put("email", "");
			paramIssueDetails.put("agentName", lifePolicy.getAgent() != null ? lifePolicy.getAgent().getFullName() : "");
			paramIssueDetails.put("agentLicenseNo", lifePolicy.getAgent() != null ? lifePolicy.getAgent().getLiscenseNo() : "");
			paramIssueDetails.put("endDate", lifePolicy.getActivedPolicyEndDate());
			paramIssueDetails.put("startDate", lifePolicy.getActivedPolicyStartDate());
			paramIssueDetails.put("sumInsured", person.getSumInsured());
			List<PolicyInsuredPersonBeneficiaries> benfList = person.getPolicyInsuredPersonBeneficiariesList();
			paramIssueDetails.put("listDataSource", new JRBeanCollectionDataSource(benfList));
			InputStream inputStream = null;
			if (isFarmer) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/Farmer/FarmerPolicyIssue.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramIssueDetails, new JREmptyDataSource());
			if (jprint.getPages().size() > 1) {
				jprint.removePage(1);
			}
			jpList.add(jprint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document public life policy issue", e);
		}
	}

	// for publicTerm lIfe report
	public static void generatePublicTermLifePolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			PolicyInsuredPerson person = lifePolicy.getInsuredPersonInfo().get(0);
			Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			boolean isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			paramIssueDetails.put("premium", lifePolicy.getTotalPremium());
			paramIssueDetails.put("insuredPersonName", person.getFullName());
			paramIssueDetails.put("insuredPersonAge", person.getAgeForNextYear());
			paramIssueDetails.put("insuredPersonOccupation", person.getOccupation() == null ? "" : person.getOccupation().getName());
			paramIssueDetails.put("address", person.getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("customerName", lifePolicy.getCustomer().getFullName());
			paramIssueDetails.put("customerAge", lifePolicy.getCustomer().getAgeForNextYear());
			paramIssueDetails.put("customerOccupation", lifePolicy.getCustomer().getOccupation().getName());
			paramIssueDetails.put("customerAddress", lifePolicy.getCustomer().getFullAddress());
			if (null != lifePolicy.getAgent()) {
				paramIssueDetails.put("agentName", lifePolicy.getAgent().getFullName());
			} else {
				paramIssueDetails.put("agentName", "N/A");
			}

			paramIssueDetails.put("day", DateUtils.getDayFromDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("month", DateUtils.getMonthFromDate(lifePolicy.getActivedPolicyStartDate()) + 1);
			paramIssueDetails.put("year", DateUtils.getYearFromDate(lifePolicy.getActivedPolicyStartDate()));

			paramIssueDetails.put("endDate", lifePolicy.getActivedPolicyEndDate());
			paramIssueDetails.put("startDate", lifePolicy.getActivedPolicyStartDate());
			paramIssueDetails.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getSumInsured()));
			List<PolicyInsuredPersonBeneficiaries> benfList = person.getPolicyInsuredPersonBeneficiariesList();
			paramIssueDetails.put("listDataSource", new JRBeanCollectionDataSource(benfList));
			InputStream inputStream = null;
			if (isPublicTermLife) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/PublicTermLife/PublicTermLifePolicyIssue.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramIssueDetails, new JREmptyDataSource());

			jpList.add(jprint);
			// FOR PUBLICTERM LIFE 2
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/PublicTermLife/publictermlifeissure2.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());

			jpList.add(jprint2);

			// FOR PUBLICTERM LIFE 3
			InputStream inputStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/PublicTermLife/publictermlifeissue3.jrxml");
			JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
			JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint3);

			/*
			 * jpList.add(jprint3); //FOR PUBLICTERM LIFE4 InputStream
			 * inputStream4 = Thread.currentThread().getContextClassLoader().
			 * getResourceAsStream(
			 * "report-template/PublicTermLife/publictermlifeissue4.jrxml");
			 * JasperReport jreport4 =
			 * JasperCompileManager.compileReport(inputStream4); JasperPrint
			 * jprint4 = JasperFillManager.fillReport(jreport4,
			 * paramIssueDetails, new JREmptyDataSource()); jpList.add(jprint4);
			 * 
			 * //FOR PUBLICTERMLIFE5 InputStream inputStream5 =
			 * Thread.currentThread().getContextClassLoader().
			 * getResourceAsStream(
			 * "report-template/PublicTermLife/publictermlifeissue5.jrxml");
			 * JasperReport jreport5 =
			 * JasperCompileManager.compileReport(inputStream5); JasperPrint
			 * jprint5 = JasperFillManager.fillReport(jreport5,
			 * paramIssueDetails, new JREmptyDataSource()); jpList.add(jprint5);
			 * //FOR PUBLICTERM LIFE 6 InputStream inputStream6 =
			 * Thread.currentThread().getContextClassLoader().
			 * getResourceAsStream(
			 * "report-template/PublicTermLife/publictermlifeissue6.jrxml");
			 * JasperReport jreport6 =
			 * JasperCompileManager.compileReport(inputStream6); JasperPrint
			 * jprint6= JasperFillManager.fillReport(jreport6,
			 * paramIssueDetails, new JREmptyDataSource()); jpList.add(jprint6);
			 */

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document public life policy issue", e);
		}
	}

	// for Single Premium Endowment Life report
	public static void generateSinglePremiumEndowmentLifePolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			String phone = null;
			String email = null;
			DecimalFormat curFormat = new DecimalFormat("#,###");
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			PolicyInsuredPerson person = lifePolicy.getInsuredPersonInfo().get(0);
			Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			boolean isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName() + " (" + agent.getLiscenseNo() + ")");
			} else {
				paramIssueDetails.put("agent", "-");
			}
			paramIssueDetails.put("insuredPersonName", person.getFullName());
			paramIssueDetails.put("insuredPersonAge", person.getAge());
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(lifePolicy.getTotalPremium()));
			paramIssueDetails.put("insuredPersonAge", person.getAgeForNextYear());
			paramIssueDetails.put("occupation", person.getOccupation().getName());
			paramIssueDetails.put("insuredPersonAddress", person.getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("phone", phone != null ? phone : "-");
			paramIssueDetails.put("email", email != null ? email : "-");
			paramIssueDetails.put("periodYears", person.getPeriodYears());
			paramIssueDetails.put("activedPolicyEndDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getEndDate()));
			paramIssueDetails.put("activedPolicyStartDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getStartDate()));
			paramIssueDetails.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getSumInsured()));
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getTotalPermium()));
			paramIssueDetails.put("fullname", person.getPolicyInsuredPersonBeneficiariesList().get(0).getFullName());
			paramIssueDetails.put("percentage", person.getPolicyInsuredPersonBeneficiariesList().get(0).getPercentage());
			paramIssueDetails.put("day", DateUtils.getDayFromDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("month", DateUtils.getMonthFromDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("year", DateUtils.getYearFromDate(lifePolicy.getActivedPolicyStartDate()));

			// Single Premium Endowment Policy Cover
			InputStream inputStream = null;
			if (isSinglePremiumEndowmentLife) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/singlePremiumEndowmentLife/SinglePremiumEndowmentLifePolicyCover.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramIssueDetails, new JREmptyDataSource());

			jpList.add(jprint);

			// Single Premium Endowment Policy Issue
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("report-template/singlePremiumEndowmentLife/SinglePremiumEndowmentLifePolicyIssue.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());

			jpList.add(jprint2);

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document single premium endowment life policy issue", e);
		}
	}

	// for Short Term Single Premium Credit Life report
	public static void generateShortTermSinglePremiumCreditLifePolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			String phone = null;
			String email = null;
			DecimalFormat curFormat = new DecimalFormat("#,###");
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			PolicyInsuredPerson person = lifePolicy.getInsuredPersonInfo().get(0);
			Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			boolean isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName() + " (" + agent.getLiscenseNo() + ")");
			} else {
				paramIssueDetails.put("agent", "-");
			}
			paramIssueDetails.put("insuredPersonName", person.getFullName());
			paramIssueDetails.put("insuredPersonAge", person.getAge());
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(lifePolicy.getTotalPremium()));
			paramIssueDetails.put("insuredPersonAge", person.getAgeForNextYear());
			paramIssueDetails.put("occupation", person.getOccupation().getName());
			paramIssueDetails.put("insuredPersonAddress", person.getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("phone", phone != null ? phone : "-");
			paramIssueDetails.put("email", email != null ? email : "-");
			paramIssueDetails.put("periodYears", person.getPeriodYears());
			paramIssueDetails.put("activedPolicyEndDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getEndDate()));
			paramIssueDetails.put("activedPolicyStartDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getStartDate()));
			paramIssueDetails.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getSumInsured()));
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getTotalPermium()));
			paramIssueDetails.put("fullname", person.getPolicyInsuredPersonBeneficiariesList().get(0).getFullName());
			paramIssueDetails.put("percentage", person.getPolicyInsuredPersonBeneficiariesList().get(0).getPercentage());
			paramIssueDetails.put("day", DateUtils.getDayFromDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("month", DateUtils.getMonthFromDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("year", DateUtils.getYearFromDate(lifePolicy.getActivedPolicyStartDate()));

			// Short Term Single Premium Credit Life Policy Cover
			InputStream inputStream = null;
			if (isShortTermSinglePremiumCreditLife) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/shorttermsinglepremiumcreditlife/ShortTermSinglePremiumCreditLifePolicyCover.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramIssueDetails, new JREmptyDataSource());

			jpList.add(jprint);

			// Short Term Single Premium Credit Life Policy Issue
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("report-template/shorttermsinglepremiumcreditlife/ShortTermSinglePremiumCreditLifePolicyIssue.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());

			jpList.add(jprint2);

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document ShortTermSinglePremiumCreditLifepolicy issue", e);
		}
	}

	// for Single Premium Credit Life report
	public static void generateSinglePremiumCreditLifePolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			String phone = null;
			String email = null;
			DecimalFormat curFormat = new DecimalFormat("#,###");
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			PolicyInsuredPerson person = lifePolicy.getInsuredPersonInfo().get(0);
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName() + " (" + agent.getLiscenseNo() + ")");
			} else {
				paramIssueDetails.put("agent", "-");
			}
			// Customer
			paramIssueDetails.put("customerName", lifePolicy.getCustomerName());
			paramIssueDetails.put("customerAge", lifePolicy.getCustomer().getAgeForNextYear());
			paramIssueDetails.put("customeroccupation", lifePolicy.getCustomer().getOccupation().getName());
			paramIssueDetails.put("customerAddress", lifePolicy.getCustomer().getResidentAddress().getFullResidentAddress());
			// InsuredPerson
			paramIssueDetails.put("insuredPersonName", person.getFullName());
			paramIssueDetails.put("insuredPersonAge", person.getAge());
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(lifePolicy.getTotalPremium()));
			paramIssueDetails.put("insuredPersonAge", person.getAgeForNextYear());
			paramIssueDetails.put("occupation", person.getOccupation().getName());
			paramIssueDetails.put("insuredPersonAddress", person.getResidentAddress().getFullResidentAddress());

			paramIssueDetails.put("periodYears", person.getPeriodYears());
			paramIssueDetails.put("activedPolicyEndDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getEndDate()));
			paramIssueDetails.put("activedPolicyStartDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getStartDate()));
			paramIssueDetails.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getSumInsured()));
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getTotalPermium()));

			// Bank
			Bank bank = paymentDTO.getBank();
			if (bank != null) {
				paramIssueDetails.put("bank", paymentDTO.getBank().getName());
				paramIssueDetails.put("bankAddress", paymentDTO.getBank().getBranch().getAddress());
			} else {
				paramIssueDetails.put("bank", "-");
				paramIssueDetails.put("bankAddress", "-");
			}

			paramIssueDetails.put("day", DateUtils.getDayFromDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("month", DateUtils.getMonthFromDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("year", DateUtils.getYearFromDate(lifePolicy.getActivedPolicyStartDate()));

			// Single Premium Credit Life Policy Cover
			InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SINGLEPREMIUMCREDITLIFE_POLICY_COVER_LETTER);
			JasperReport jreport1 = JasperCompileManager.compileReport(inputStream1);
			JasperPrint jprint1 = JasperFillManager.fillReport(jreport1, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint1);

			// Single Premium Credit Life Policy Issue
			InputStream inputStream2 = null;
			inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SINGLEPREMIUMCREDITLIFE_POLICY_ISSUE_LETTER);
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint2);

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document SinglePremiumCreditLifePolicyIssue", e);
		}
	}

	// for Simple Life report
	public static void generateSimpleLifePolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			String phone = null;
			String email = null;
			DecimalFormat curFormat = new DecimalFormat("#,###");
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			List<PolicyInsuredPerson> insuredPersonList = new ArrayList<PolicyInsuredPerson>();
			PolicyInsuredPerson person = lifePolicy.getInsuredPersonInfo().get(0);
			Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			Organization organization = lifePolicy.getLifeProposal().getOrganization();
			
			//label for policy holder is Organization
			if (lifePolicy.getLifeProposal().getAuditLog() !=null) {
				if(lifePolicy.getLifeProposal().getAuditLog().getOrganization() !=null) {
					boolean isOrg = true;
					paramIssueDetails.put("isOrg", isOrg);
					paramIssueDetails.put("companyName", lifePolicy.getLifeProposal().getAuditLog().getOrganization().getName());	
				}	
			} else if (lifePolicy.getLifeProposal().getOrganization() != null) {
				boolean isOrg = true;
				paramIssueDetails.put("isOrg", isOrg);
				paramIssueDetails.put("companyName", organization.getName());
			} 
			
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName());
			} else {
				paramIssueDetails.put("agent", "-");
			}
			if (agent != null) {
				paramIssueDetails.put("liscenseNo", agent.getLiscenseNo());
			} else {
				paramIssueDetails.put("liscenseNo", "-");
			}
			// CoverOptions
			for (PolicyInsuredPersonKeyFactorValue vehKF : lifePolicy.getPolicyInsuredPersonList().get(0).getPolicyInsuredPersonkeyFactorValueList()) {
				if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {
					paramIssueDetails.put("coveroptions", vehKF.getValue());
				}
			}
			paramIssueDetails.put("phone", phone != null ? phone : "-");
			paramIssueDetails.put("submittedDate", DateUtils.getDateFormatString(lifePolicy.getLifeProposal().getSubmittedDate()));
			paramIssueDetails.put("insuredPersonName", person.getFullName());
			paramIssueDetails.put("insurPersonNRC", person.getIdNo());
			paramIssueDetails.put("father", person.getFatherName());
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(lifePolicy.getTotalPremium()));
			paramIssueDetails.put("occupation", person.getOccupation().getName());
			paramIssueDetails.put("insuredPersonAddress", person.getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("phone", phone != null ? phone : "-");
			paramIssueDetails.put("email", email != null ? email : "-");
			paramIssueDetails.put("startdate", DateUtils.getDateFormatString(person.getStartDate()));
			paramIssueDetails.put("enddate", DateUtils.getDateFormatString(person.getEndDate()));
			
			if (person.getPeriodMonth() != 0) {
				paramIssueDetails.put("periodYears", person.getPeriodMonth());
			} else if (person.getPeriodYear() != 0) {
				paramIssueDetails.put("periodYears", person.getPeriodYear());
			} else {
				paramIssueDetails.put("periodYears", person.getPeriodWeek());
			}
			paramIssueDetails.put("reportLogo", ApplicationSetting.getReportLogo());
			paramIssueDetails.put("periodType", person.getPeriodType());
			paramIssueDetails.put("activedPolicyStartDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getStartDate()));
			paramIssueDetails.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getSumInsured()));
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(person.getTotalPermium()));
			// PolicyInsuredPersonBeneficiaries
			paramIssueDetails.put("insurPersonBeneNRC",
					person.getPolicyInsuredPersonBeneficiariesList().get(0).getIdNo() != null ? person.getPolicyInsuredPersonBeneficiariesList().get(0).getIdNo()
							: "Still Applying");
			paramIssueDetails.put("fullname", person.getPolicyInsuredPersonBeneficiariesList().get(0).getFullName());
			paramIssueDetails.put("fulladdress", person.getPolicyInsuredPersonBeneficiariesList().get(0).getFullAddress());
			paramIssueDetails.put("insuBeneFatherName", "-");
			paramIssueDetails.put("insuBenePhone",
					person.getPolicyInsuredPersonBeneficiariesList().get(0).getContentInfo().getPhone() != null
							? person.getPolicyInsuredPersonBeneficiariesList().get(0).getContentInfo().getPhone()
							: "-");
			paramIssueDetails.put("percentage", person.getPolicyInsuredPersonBeneficiariesList().get(0).getPercentage());


			// Simple Life Policy Issue
			InputStream inputStream2 = null;
			inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/SimpleLife/SimpleLifePolicyIssue.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint2);
			
			//If Bene person is more than (1), show the list of Bene person
			List<PolicyInsuredPersonBeneficiaries> beneifitPersonList = person.getPolicyInsuredPersonBeneficiariesList();
			int size = beneifitPersonList.size();
			
			if(size > 1) {
				Map<String, Object> benfMap = new HashMap<String, Object>();
				beneifitPersonList.remove(0);
				benfMap.put("listDataSource", new JRBeanCollectionDataSource(beneifitPersonList));
				InputStream beneficiariesIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/BENEFICIARY_ATTACH.jrxml");
				JasperReport beneficiariesISJR = JasperCompileManager.compileReport(beneficiariesIS);
				JasperPrint beneficiariesISPrint = JasperFillManager.fillReport(beneficiariesISJR, benfMap, new JREmptyDataSource());
			
				jpList.add(beneficiariesISPrint);
				
			}

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document SimpleLifePolicy issue", e);
		}
	}

	public static void generateSimpleLifePolicyforOrganization(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			String phone = null;
			String email = null;
			DecimalFormat curFormat = new DecimalFormat("#,###");
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			PolicyInsuredPerson person = lifePolicy.getInsuredPersonInfo().get(0);
			Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			// paramIssueDetails.put("policyNo",
			// lifePolicy.getLifeProposal().getPay);
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName());
			} else {
				paramIssueDetails.put("agent", "-");
			}
			if (agent != null) {
				paramIssueDetails.put("liscenseNo", agent.getLiscenseNo());
			} else {
				paramIssueDetails.put("liscenseNo", "-");
			}
			// CoverOptions
			for (PolicyInsuredPersonKeyFactorValue vehKF : lifePolicy.getPolicyInsuredPersonList().get(0).getPolicyInsuredPersonkeyFactorValueList()) {
				if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {
					paramIssueDetails.put("coveroptions", vehKF.getValue());
				}
			}
			paramIssueDetails.put("reportLogo", ApplicationSetting.getReportLogo());
			paramIssueDetails.put("submittedDate", DateUtils.getDateFormatString(lifePolicy.getLifeProposal().getSubmittedDate()));
			paramIssueDetails.put("companyName", lifePolicy.getLifeProposal().getAuditLog().getOrganization().getName());
			paramIssueDetails.put("ownerName", lifePolicy.getLifeProposal().getAuditLog().getOrganization().getOwnerName());
			paramIssueDetails.put("companyAddress", lifePolicy.getLifeProposal().getAuditLog().getOrganization().getFullAddress());
			paramIssueDetails.put("phone", phone != null ? phone : "-");
			paramIssueDetails.put("email", email != null ? email : "-");

			if (person.getPeriodMonth() != 0) {
				paramIssueDetails.put("periodYears", person.getPeriodMonth());
			} else if (person.getPeriodYear() != 0) {
				paramIssueDetails.put("periodYears", person.getPeriodYear());
			} else {
				paramIssueDetails.put("periodYears", person.getPeriodWeek());
			}
			paramIssueDetails.put("periodType", person.getPeriodType());
			paramIssueDetails.put("activedPolicyStartDate", DateUtils.getDateFormatString(lifePolicy.getInsuredPersonInfo().get(0).getStartDate()));
			paramIssueDetails.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(lifePolicy.getTotalSumInsured()));
			paramIssueDetails.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(lifePolicy.getTotalPremium()));
			paramIssueDetails.put("totalInsuredPerson", lifePolicy.getInsuredPersonInfo().size());
			paramIssueDetails.put("salePersonName", lifePolicy.getSalePersonName() != null ? lifePolicy.getSalePersonName() : "-");

			//InsuredPerson
//			Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
//			insuredPesronMap_2.put("policyNo", lifePolicy.getPolicyNo());
//			insuredPesronMap_2.put("companyName", lifePolicy.getOrganizationName());
//			insuredPesronMap_2.put("reportLogo", ApplicationSetting.getReportLogo());
//			insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(lifePolicy.getInsuredPersonInfo()));
//			InputStream insuredPersonIS_2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyInsuredPersonandLifePolicyBeneficiaries.jrxml");
//			JasperReport insuredPersonJR_2 = JasperCompileManager.compileReport(insuredPersonIS_2);
//			JasperPrint insuredPersonPrint_2 = JasperFillManager.fillReport(insuredPersonJR_2, insuredPesronMap_2, new JREmptyDataSource());
			
			
			// Simple Life Policy Issue
			InputStream inputStream2 = null;
			inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/SimpleLife/SimpleLifePolicyIssueforOrg.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint2);
//			jpList.add(insuredPersonPrint_2);

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document SimpleLifePolicy issue", e);
		}
	}

	// want
	public static void generateGroupLifePolicy(LifePolicy lifePolicy, PaymentDTO paymentDTO, String dirPath, String fileName) {
		try {
			Map<String, Object> coverMap = new HashMap<String, Object>();
			coverMap.put("policyNo", lifePolicy.getPolicyNo());
			coverMap.put("customerName", lifePolicy.getCustomerName());
			coverMap.put("totalSumInsured", lifePolicy.getTotalSumInsured());
			InputStream coverIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyIssueCover.jrxml");
			JasperReport coverJR = JasperCompileManager.compileReport(coverIS);
			JasperPrint coverPrint = JasperFillManager.fillReport(coverJR, coverMap, new JREmptyDataSource());
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			paramIssueDetails.put("receiptNo", paymentDTO.getReceiptNo());
			paramIssueDetails.put("commenmanceDate", lifePolicy.getCommenmanceDate());
			if (lifePolicy.getAgent() != null) {
				paramIssueDetails.put("agent", lifePolicy.getAgent().getFullName() + "(" + lifePolicy.getAgent().getCodeNo() + ")");
			} else {
				paramIssueDetails.put("agent", "");
			}
			paramIssueDetails.put("customerName", lifePolicy.getOwnerName());
			paramIssueDetails.put("organizationName", lifePolicy.getOrganizationName());
			paramIssueDetails.put("customerAddress", lifePolicy.getCustomerAddress());
			paramIssueDetails.put("totalInsuredPerson", lifePolicy.getInsuredPersonInfo().size());
			paramIssueDetails.put("totalSumInsured", lifePolicy.getTotalSumInsured());
			paramIssueDetails.put("startDate", lifePolicy.getInsuredPersonInfo().get(0).getStartDate());
			paramIssueDetails.put("endDate", lifePolicy.getInsuredPersonInfo().get(0).getEndDate());
			paramIssueDetails.put("periodYears", lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears());
			paramIssueDetails.put("totalPremium", lifePolicy.getTotalPremium());
			InputStream policyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/groupLifePolicyIssue.jrxml");
			JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
			JasperPrint policyPrint = JasperFillManager.fillReport(policyJR, paramIssueDetails, new JREmptyDataSource());

			Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
			insuredPesronMap_2.put("policyNo", lifePolicy.getPolicyNo());
			insuredPesronMap_2.put("customerName", lifePolicy.getCustomerName());
			insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(lifePolicy.getInsuredPersonInfo()));
			InputStream insuredPersonIS_2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyInsuredPerson.jrxml");
			JasperReport insuredPersonJR_2 = JasperCompileManager.compileReport(insuredPersonIS_2);
			JasperPrint insuredPersonPrint_2 = JasperFillManager.fillReport(insuredPersonJR_2, insuredPesronMap_2, new JREmptyDataSource());
			Map<String, Object> benfMap = new HashMap<String, Object>();
			benfMap.put("policyNo", lifePolicy.getPolicyNo());
			benfMap.put("listDataSource", new JRBeanCollectionDataSource(lifePolicy.getInsuredPersonInfo()));
			InputStream beneficiariesIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/groupLifePolicyBeneficiaries.jrxml");
			JasperReport beneficiariesISJR = JasperCompileManager.compileReport(beneficiariesIS);
			JasperPrint beneficiariesISPrint = JasperFillManager.fillReport(beneficiariesISJR, benfMap, new JREmptyDataSource());
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			jpList.add(coverPrint);
			jpList.add(policyPrint);
			// jpList.add(insuredPersonPrint);
			jpList.add(insuredPersonPrint_2);
			jpList.add(beneficiariesISPrint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document group life policy issue", e);
		}
	}

	/*
	 * Fire Renewal Proposal Cash Receipt
	 */

	/*
	 * Life Proposal Cash Receipt
	 */
	public static <T> void generateLifeCashReceipt(T lifeInfo, PaymentDTO payment, String dirPath, String fileName) {
		try {
			LifePolicy lifePolicy = null;
			LifeProposal lifeProposal = null;
			// populate data
			Calendar cal = Calendar.getInstance();
			Date fromDate;
			Date toDate;
			boolean isHasAgent = false;
			Product product = null;
			double sumInsured;
			double totalPremium;
			String paymentType;
			String policyType;
			String insuredPerson;
			String proposalNo;
			String childName = null;
			if (Utils.isLifeProposal(lifeInfo)) {
				lifeProposal = (LifeProposal) lifeInfo;
				ProposalInsuredPerson person = lifeProposal.getProposalInsuredPersonList().get(0);
				product = person.getProduct();

				if (!ProposalType.ENDORSEMENT.equals(lifeProposal.getProposalType())) {
					cal.setTime(person.getStartDate());
					fromDate = cal.getTime();
					if (KeyFactorChecker.isPersonalAccident(person.getProduct()) || KeyFactorChecker.isPersonalAccidentUSD(person.getProduct())
							|| KeyFactorChecker.isSimpleLife(person.getProduct())) {
						toDate = person.getEndDate();
					} else {
						if (person.getPaymentTerm() > 0) {
							cal.add(Calendar.MONTH, lifeProposal.getPaymentType().getMonth());
						} else {
							cal.add(Calendar.MONTH, person.getPeriodMonth());
						}
						toDate = cal.getTime();
					}
				} else {
					fromDate = lifeProposal.getLifePolicy().getCommenmanceDate();
					toDate = lifeProposal.getLifePolicy().getActivedPolicyEndDate();
				}
				isHasAgent = (lifeProposal.getAgent() == null) ? false : true;
				sumInsured = lifeProposal.getSumInsured();
				paymentType = lifeProposal.getPaymentType().getName();
				policyType = product.getName();
				insuredPerson = lifeProposal.getCustomerName();
				proposalNo = lifeProposal.getProposalNo();
				totalPremium = lifeProposal.getTotalPremium();
				if (KeyFactorChecker.isStudentLife(product.getId())) {
					childName = person.getFullName();
				}
				// if (KeyFactorChecker.isFarmer(product)) {
				// sumInsured = person.getUnit();
				// }
			} else {
				lifePolicy = (LifePolicy) lifeInfo;
				sumInsured = lifePolicy.getSumInsured();
				product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
				fromDate = lifePolicy.getCommenmanceDate();
				toDate = lifePolicy.getActivedPolicyEndDate();
				isHasAgent = (lifePolicy.getAgent() == null) ? false : true;
				paymentType = lifePolicy.getPaymentType().getName();
				policyType = product.getName();
				insuredPerson = lifePolicy.getCustomerName();
				proposalNo = lifePolicy.getLifeProposal().getProposalNo();
				totalPremium = lifePolicy.getTotalPremium();
				PolicyInsuredPerson person = lifePolicy.getPolicyInsuredPersonList().get(0);
				if (KeyFactorChecker.isStudentLife(product.getId())) {
					childName = person.getFullName();
				}
			}

			String customerAddress = (Utils.isLifeProposal(lifeInfo)) ? lifeProposal.getCustomerAddress() : lifePolicy.getCustomerAddress();
			String customerName = (Utils.isLifeProposal(lifeInfo)) ? lifeProposal.getCustomerName() : lifePolicy.getCustomerName();
			String agentNameWithLiscenseNo = "";
			if (Utils.isLifeProposal(lifeInfo)) {
				agentNameWithLiscenseNo = lifeProposal.getAgent() == null ? "" : lifeProposal.getAgent().getFullName() + " (" + lifeProposal.getAgent().getLiscenseNo() + ")";
			} else {
				agentNameWithLiscenseNo = lifePolicy.getAgent() == null ? "" : lifePolicy.getAgent().getFullName() + " (" + lifePolicy.getAgent().getLiscenseNo() + ")";
			}
			double totalpremiun = (Utils.isLifeProposal(lifeInfo)) ? lifeProposal.getTotalPremium() : lifePolicy.getLifeProposal().getTotalPremium();
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (Utils.isLifeProposal(lifeInfo) && lifeProposal.getLifePolicy() != null && lifeProposal.getProposalType().equals(ProposalType.ENDORSEMENT)) {
				paramMap.put("premiumName", "Endorsement Premium");
				paramMap.put("addOnPremiumName", "Endorsement Additional Premium");
			} else {
				paramMap.put("premiumName", "Premium");
				paramMap.put("addOnPremiumName", "Additional Premium");
			}
			if (payment != null && payment.getPaymentChannel() != null) {
				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)
						|| payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
					paramMap.put("chequeNo", payment.getChequeNo() == null ? payment.getPoNo() : payment.getChequeNo());
					if (PaymentChannel.SUNDRY.equals(payment.getPaymentChannel())) {
						paramMap.put("bank", payment.getAccountBank().getName());
						paramMap.put("chequePayment", Boolean.TRUE);
					} else {
						paramMap.put("bank", payment.getBank().getName());
						paramMap.put("chequePayment", Boolean.TRUE);
						paramMap.put("poNo", Boolean.FALSE);
						paramMap.put("receiptType", "Temporary Receipt");
						if (payment.getChequeNo() == null) {
							paramMap.put("poNo", Boolean.TRUE);
							paramMap.put("chequePayment", Boolean.FALSE);
						}
					}

				} else {
					paramMap.put("receiptType", "Cash Receipt");
				}
				paramMap.put("receiptName", "Receipt No.");
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				paramMap.put("receiptType", "Cash Receipt");
				paramMap.put("receiptName", "Cash Receipt No.");
			}
			paramMap.put("proposalNo", proposalNo);
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("sumInsured", sumInsured);
			paramMap.put("premium", payment.getBasicPremium());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("addOnPremium", payment.getAddOnPremium());
			paramMap.put("netPremium", payment.getNetPremium());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("totalAmount", payment.getTotalAmount());
			paramMap.put("paymentType", paymentType);
			paramMap.put("policyType", policyType);
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("agent", agentNameWithLiscenseNo);
			paramMap.put("insuredPerson", insuredPerson);
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("childName", childName);
			if (lifeProposal != null) {
				paramMap.put("policyBranch", lifeProposal.getBranch().getName());
			} else {
				paramMap.put("policyBranch", lifePolicy.getBranch().getName());
			}
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			String resource = null;
			if (KeyFactorChecker.isPersonalAccident(product) || KeyFactorChecker.isPersonalAccidentUSD(product)) {
				resource = "report-template/personalAccident/PACashReceipt.jrxml";
			} else if (KeyFactorChecker.isStudentLife(product.getId())) {
				resource = "report-template/studentLife/studentLifeCashReceipt.jrxml";
			} else {
				resource = "report-template/life/lifeCashReceipt.jrxml";
			}
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);
			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			// FileHandler.forceMakeDirectory(dirPath);
			// String outputFile = dirPath + fileName;
			// JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate GroupLifeSanction", e);
		}
	}

	public static void generateGroupLIfeSanction(LifeProposal lifeProposal, String staffName, String dirPath, String fileName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<Double, Integer> ratePairs = new HashMap<Double, Integer>();
		int totalNoOfPerson = 0;
		try {
			for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
				double premium = insuredPerson.getProposedPremium();
				if (insuredPerson.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
					totalNoOfPerson += 1;
					if (ratePairs.containsKey(premium)) {
						ratePairs.put(premium, ratePairs.get(premium) + 1);
					} else {
						ratePairs.put(premium, 1);
					}

				}
			}
			paramMap.put("insuredName", lifeProposal.getCustomerName());
			paramMap.put("totalNoOfPerson", totalNoOfPerson);
			paramMap.put("totalSI", lifeProposal.getTotalSumInsured());
			paramMap.put("totalPremium", lifeProposal.getTotalPremium());
			paramMap.put("staffName", staffName);
			paramMap.put("personPremiumRate", ratePairs.entrySet());
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/groupLifeSanction.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate GroupLifeSanction", e);
		}
	}

	public static void generateSportManSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("customerName", insuredPerson.getFullName());
			paramMap.put("age", insuredPerson.getAge());
			paramMap.put("sumInsured", lifeProposal.getSumInsured());
			paramMap.put("period", insuredPerson.getPeriodYears());
			/*
			 * paramMap.put("medicalInfo", insuredPerson.getClsOfHealth()
			 * .getLabel());
			 */
			paramMap.put("premiumForOneThousandKyat", insuredPerson.getPremiumForOneThousandKyat());
			double totalTermPremim = lifeProposal.getTotalTermPremium();
			double kyat = Math.floor(totalTermPremim);
			double pyar = (totalTermPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###");
			String termPremiumKyat = formatter1.format(kyat);
			DecimalFormat formatter2 = new DecimalFormat("00");
			String termPremiumPyar = formatter2.format(pyar);
			int month = lifeProposal.getPaymentType().getMonth();
			switch (month) {
				case 12: {
					paramMap.put("paymentYear", "1");
					paramMap.put("paymentMonth", "");
					paramMap.put("oneYearPaymentKyat", termPremiumKyat);
					paramMap.put("oneYearPaymentPyar", termPremiumPyar);
					paramMap.put("sixMonthPaymentKyat", "");
					paramMap.put("sixMonthPaymentPyar", "");
					paramMap.put("threeMonthPaymentKyat", "");
					paramMap.put("threeMonthPaymentPyar", "");
				}
					break;
				case 6: {
					paramMap.put("paymentYear", "0");
					paramMap.put("paymentMonth", "6");
					paramMap.put("oneYearPaymentKyat", "");
					paramMap.put("oneYearPaymentPyar", "");
					paramMap.put("sixMonthPaymentKyat", termPremiumKyat);
					paramMap.put("sixMonthPaymentPyar", termPremiumPyar);
					paramMap.put("threeMonthPaymentKyat", "");
					paramMap.put("threeMonthPaymentPyar", "");
				}
					break;
				case 3: {
					paramMap.put("paymentYear", "");
					paramMap.put("paymentMonth", "3");
					paramMap.put("oneYearPaymentKyat", "");
					paramMap.put("oneYearPaymentPyar", "");
					paramMap.put("sixMonthPaymentKyat", "");
					paramMap.put("sixMonthPaymentPyar", "");
					paramMap.put("threeMonthPaymentKyat", termPremiumKyat);
					paramMap.put("threeMonthPaymentPyar", termPremiumPyar);
				}
					break;
			}
			paramMap.put("premiumKyat", termPremiumKyat);
			paramMap.put("premiumPyar", termPremiumPyar);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/publicLifeSanction.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PublicLifeSanction", e);
		}
	}

	public static void generatePublicLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("customerName", insuredPerson.getFullName());
			paramMap.put("age", insuredPerson.getAge());
			paramMap.put("sumInsured", lifeProposal.getSumInsured());
			paramMap.put("period", insuredPerson.getPeriodYears());
			paramMap.put("medicalInfo", insuredPerson.getClsOfHealth().getLabel());
			paramMap.put("premiumForOneThousandKyat", insuredPerson.getPremiumForOneThousandKyat());
			double totalTermPremim = lifeProposal.getTotalTermPremium();
			double kyat = Math.floor(totalTermPremim);
			double pyar = (totalTermPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###");
			String termPremiumKyat = formatter1.format(kyat);
			DecimalFormat formatter2 = new DecimalFormat("00");
			String termPremiumPyar = formatter2.format(pyar);
			int month = lifeProposal.getPaymentType().getMonth();
			switch (month) {
				case 12:
					paramMap.put("paymentYear", "1");
					paramMap.put("paymentMonth", "");
					paramMap.put("oneYearPaymentKyat", termPremiumKyat);
					paramMap.put("oneYearPaymentPyar", termPremiumPyar);
					paramMap.put("sixMonthPaymentKyat", "");
					paramMap.put("sixMonthPaymentPyar", "");
					paramMap.put("threeMonthPaymentKyat", "");
					paramMap.put("threeMonthPaymentPyar", "");
					break;
				case 6:
					paramMap.put("paymentYear", "0");
					paramMap.put("paymentMonth", "6");
					paramMap.put("oneYearPaymentKyat", "");
					paramMap.put("oneYearPaymentPyar", "");
					paramMap.put("sixMonthPaymentKyat", termPremiumKyat);
					paramMap.put("sixMonthPaymentPyar", termPremiumPyar);
					paramMap.put("threeMonthPaymentKyat", "");
					paramMap.put("threeMonthPaymentPyar", "");
					break;
				case 3:
					paramMap.put("paymentYear", "");
					paramMap.put("paymentMonth", "3");
					paramMap.put("oneYearPaymentKyat", "");
					paramMap.put("oneYearPaymentPyar", "");
					paramMap.put("sixMonthPaymentKyat", "");
					paramMap.put("sixMonthPaymentPyar", "");
					paramMap.put("threeMonthPaymentKyat", termPremiumKyat);
					paramMap.put("threeMonthPaymentPyar", termPremiumPyar);
					break;
				default:
					paramMap.put("paymentYear", "");
					paramMap.put("paymentMonth", "");
					paramMap.put("oneYearPaymentKyat", "");
					paramMap.put("oneYearPaymentPyar", "");
					paramMap.put("sixMonthPaymentKyat", "");
					paramMap.put("sixMonthPaymentPyar", "");
					paramMap.put("threeMonthPaymentKyat", "");
					paramMap.put("threeMonthPaymentPyar", "");
					break;
			}
			paramMap.put("premiumKyat", termPremiumKyat);
			paramMap.put("premiumPyar", termPremiumPyar);
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/publicLifeSanction.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PublicLifeSanction", e);
		}
	}

	public static void generatePersonalAccidentSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Customer customer = lifeProposal.getCustomer();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			double totalPremim = insuredPerson.getTotalPermium();
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int nextYear = year + 1;
			String finialYear = String.valueOf(year + "-" + nextYear);
			paramMap.put("branchCode", lifeProposal.getBranch().getBranchCode());
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("finialYear", finialYear);
			paramMap.put("date", new Date());
			paramMap.put("insuredPersonName", lifeProposal.getCustomerName());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("age", customer == null ? insuredPerson.getAge() : customer.getAgeForNextYear());
			if (customer != null && customer.getOccupation() != null) {
				paramMap.put("occupation", customer.getOccupation().getName());
			} else {
				paramMap.put("occupation", insuredPerson.getOccupation() != null ? insuredPerson.getOccupation().getName() : "");
			}
			paramMap.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(lifeProposal.getSumInsured()));
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			AbstractProcessor processor = new DefaultProcessor();
			paramMap.put("period", insuredPerson.getPeriodMonth());
			paramMap.put("premiumRate", insuredPerson.getProduct().getFixedValue());
			paramMap.put("beneficiaryName", insuredPerson.getInsuredPersonBeneficiariesList().get(0).getFullName());
			paramMap.put("relationshipName", insuredPerson.getInsuredPersonBeneficiariesList().get(0).getRelationship().getName());
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(totalPremim));
			convertor = new DefaultConvertor();
			boolean isMMK = KeyFactorChecker.isPersonalAccident(insuredPerson.getProduct());
			if (isMMK) {
				paramMap.put("sumInsuredInWord", convertor.getNameWithDecimal(lifeProposal.getSumInsured()));
				paramMap.put("premiumInWord", convertor.getNameWithDecimal(totalPremim));
			} else {
				paramMap.put("sumInsuredInWord", processor.getNameWithDecimal(lifeProposal.getSumInsured(), insuredPerson.getProduct().getCurrency()));
				paramMap.put("premiumInWord", processor.getNameWithDecimal(totalPremim, insuredPerson.getProduct().getCurrency()));
			}
			paramMap.put("isMMK", isMMK);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personalAccident/PASanction.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PublicLifeSanction", e);
		}
	}

	public static void generatePersonalAccidentGroupSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			double totalPremim = lifeProposal.getPremium();
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int nextYear = year + 1;
			String finialYear = String.valueOf(year + "-" + nextYear);
			paramMap.put("branchCode", lifeProposal.getBranch().getBranchCode());
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("finialYear", finialYear);
			paramMap.put("date", new Date());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("insuredPersonName", insuredPerson.getFullName());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("age", insuredPerson.getAge());
			if (insuredPerson.getOccupation() != null) {
				paramMap.put("occupation", insuredPerson.getOccupation().getName());
			}
			paramMap.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(lifeProposal.getSumInsured()));
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			AbstractProcessor processor = new DefaultProcessor();
			paramMap.put("period", insuredPerson.getPeriodMonth());
			paramMap.put("premiumRate", insuredPerson.getProduct().getFixedValue());
			paramMap.put("beneficiaryName", insuredPerson.getInsuredPersonBeneficiariesList().get(0).getFullName());
			paramMap.put("relationshipName", insuredPerson.getInsuredPersonBeneficiariesList().get(0).getRelationship().getName());
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(totalPremim));
			convertor = new DefaultConvertor();
			boolean isMMK = KeyFactorChecker.isPersonalAccident(insuredPerson.getProduct());
			if (isMMK) {
				paramMap.put("sumInsuredInWord", convertor.getNameWithDecimal(lifeProposal.getSumInsured()));
				paramMap.put("premiumInWord", convertor.getNameWithDecimal(totalPremim));
			} else {
				paramMap.put("sumInsuredInWord", processor.getNameWithDecimal(lifeProposal.getSumInsured(), insuredPerson.getProduct().getCurrency()));
				paramMap.put("premiumInWord", processor.getNameWithDecimal(totalPremim, insuredPerson.getProduct().getCurrency()));
			}
			paramMap.put("isMMK", isMMK);

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personalAccident/PA_Group_Sanction.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			Map<String, Object> benfMap = new HashMap<String, Object>();
			benfMap.put("listDataSource", new JRBeanCollectionDataSource(lifeProposal.getProposalInsuredPersonList()));
			InputStream beneficiariesIS = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("report-template/personalAccident/PA_GroupSanction_Beneficiaries.jrxml");
			JasperReport beneficiariesISJR = JasperCompileManager.compileReport(beneficiariesIS);
			JasperPrint beneficiariesISPrint = JasperFillManager.fillReport(beneficiariesISJR, benfMap, new JREmptyDataSource());
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			jpList.add(jprint);
			jpList.add(beneficiariesISPrint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			/*
			 * String outputFile = dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PublicLifeSanction", e);
		}
	}

	public static void generateFarmerSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isFarmer = KeyFactorChecker.isFarmer(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			double totalPremim = insuredPerson.getTotalPermium();
			double kyat = Math.floor(totalPremim);
			double pyar = (totalPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###");
			DecimalFormat formatter2 = new DecimalFormat("00");
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("fatherName", insuredPerson.getFatherName());
			paramMap.put("status", "");
			paramMap.put("customerName", insuredPerson.getFullName());
			paramMap.put("age", insuredPerson.getAge());
			paramMap.put("sumInsured", insuredPerson.getProposedSumInsured());
			paramMap.put("period", insuredPerson.getPeriodMonth());
			paramMap.put("medicalInfo", insuredPerson.getClsOfHealth() != null ? insuredPerson.getClsOfHealth().getLabel() : "");
			paramMap.put("basicPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getProposedPremium()));
			paramMap.put("addonPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getAddOnPremium()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(totalPremim));
			paramMap.put("premiumKyat", formatter1.format(kyat));
			paramMap.put("premiumPyar", formatter2.format(pyar));
			InputStream inputStream = null;
			if (isFarmer) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/Farmer/farmerSanction.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PublicLifeSanction", e);
		}
	}

	// for publicTerm life report
	public static void generatePublicTermLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			double totalPremim = insuredPerson.getTotalPermium();
			double kyat = Math.floor(totalPremim);
			double pyar = (totalPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###");
			DecimalFormat formatter2 = new DecimalFormat("00");
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("fatherName", insuredPerson.getFatherName());
			paramMap.put("status", "");
			paramMap.put("customerName", insuredPerson.getFullName());
			paramMap.put("age", insuredPerson.getAge());
			paramMap.put("sumInsured", insuredPerson.getProposedSumInsured());
			paramMap.put("period", insuredPerson.getPeriodMonth());
			paramMap.put("medicalInfo", insuredPerson.getClsOfHealth() != null ? insuredPerson.getClsOfHealth().getLabel() : "");
			paramMap.put("basicPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getProposedPremium()));
			paramMap.put("addonPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getAddOnPremium()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(totalPremim));
			paramMap.put("premiumKyat", formatter1.format(kyat));
			paramMap.put("premiumPyar", formatter2.format(pyar));
			InputStream inputStream = null;
			if (isPublicTermLife) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/PublicTermLife/publicTermLifeSanction.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PublicLifeSanction", e);
		}
	}

	// for SinglePremiumEndowmentLife report
	public static void generateSinglePremiumEndowmentLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			double totalPremim = insuredPerson.getTotalPermium();
			double kyat = Math.floor(totalPremim);
			double pyar = (totalPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###");
			DecimalFormat formatter2 = new DecimalFormat("00");
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("fatherName", insuredPerson.getFatherName());
			paramMap.put("customerName", insuredPerson.getFullName());
			paramMap.put("age", insuredPerson.getAge());
			paramMap.put("sumInsured", insuredPerson.getProposedSumInsured());
			paramMap.put("period", insuredPerson.getPeriodYears());
			paramMap.put("medicalInfo", insuredPerson.getClsOfHealth() != null ? insuredPerson.getClsOfHealth().getLabel() : "");
			paramMap.put("basicPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getProposedPremium()));
			paramMap.put("addonPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getAddOnPremium()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(totalPremim));
			paramMap.put("premiumKyat", formatter1.format(kyat));
			paramMap.put("premiumPyar", formatter2.format(pyar));
			InputStream inputStream = null;
			if (isSinglePremiumEndowmentLife) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/singlePremiumEndowmentLife/SinglePremiumEndowmentLifeSanction.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate SinglePremiumEndowmentLifeSanction", e);
		}
	}

	// for single premium credit life report
	public static void generateSinglePremiumCreditLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("customerName", lifeProposal.getCustomer().getFullName());
			paramMap.put("insuredPersonName", insuredPerson.getFullName());
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("insuredPersonAge", insuredPerson.getAgeForNextYear());
			paramMap.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(lifeProposal.getSumInsured()));
			paramMap.put("period", insuredPerson.getPeriodYears());
			paramMap.put("medicalInfo", insuredPerson.getClsOfHealth() != null ? insuredPerson.getClsOfHealth().getLabel() : "");
			double totalTermPremim = lifeProposal.getTotalTermPremium();
			double kyat = Math.floor(totalTermPremim);
			double pyar = (totalTermPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###.00");
			String termPremiumKyat = formatter1.format(kyat);
			DecimalFormat formatter2 = new DecimalFormat("00");
			String termPremiumPyar = formatter2.format(pyar);
			double totalPremim = insuredPerson.getTotalPermium();
			paramMap.put("totalPremium", formatter1.format(totalPremim));
			paramMap.put("premiumKyat", termPremiumKyat);
			paramMap.put("premiumPyar", termPremiumPyar);
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SINGLEPREMIUMCREDITLIFE_SANCTION_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate SinglePremiumCreditLifeSanction", e);
		}
	}

	// for ShortTermSinglePremiumCreditLife report
	public static void generateShortTermSinglePremiumCreditLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);

			Map<String, Object> paramMap = new HashMap<String, Object>();
			double totalPremim = insuredPerson.getTotalPermium();
			double kyat = Math.floor(totalPremim);
			double pyar = (totalPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###");
			DecimalFormat formatter2 = new DecimalFormat("00");
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("fatherName", insuredPerson.getFatherName());
			paramMap.put("customerName", insuredPerson.getFullName());
			paramMap.put("age", insuredPerson.getAge());
			paramMap.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getProposedSumInsured()));
			paramMap.put("period", insuredPerson.getPeriodYears());
			paramMap.put("medicalInfo", insuredPerson.getClsOfHealth() != null ? insuredPerson.getClsOfHealth().getLabel() : "");
			paramMap.put("basicPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getProposedPremium()));
			paramMap.put("addonPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getAddOnPremium()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(totalPremim));
			paramMap.put("premiumKyat", formatter1.format(kyat));
			paramMap.put("premiumPyar", formatter2.format(pyar));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SHORTTERMSINGLEPREMIUMCREDITLIFE_SANCTION_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate ShortTermSinglePremiumCreditLifeSanction", e);
		}
	}

	// for Simplel  life report
	public static void generateSimpleLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isSimpleLife = KeyFactorChecker.isSimpleLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			double totalPremim = insuredPerson.getTotalPermium();
			double kyat = Math.floor(totalPremim);
			double pyar = (totalPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###");
			DecimalFormat formatter2 = new DecimalFormat("00");
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("fatherName", insuredPerson.getFatherName());
			paramMap.put("status", "");
			paramMap.put("customerName", insuredPerson.getFullName());
			paramMap.put("age", insuredPerson.getAge());
			paramMap.put("sumInsured", insuredPerson.getProposedSumInsured());
			paramMap.put("period", insuredPerson.getPeriod());
			paramMap.put("medicalInfo", insuredPerson.getClsOfHealth() != null ? insuredPerson.getClsOfHealth().getLabel() : "");
			paramMap.put("basicPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getProposedPremium()));
			paramMap.put("addonPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(insuredPerson.getAddOnPremium()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(totalPremim));
			paramMap.put("premiumKyat", formatter1.format(kyat));
			paramMap.put("premiumPyar", formatter2.format(pyar));
			
			// CoverOptions
			for (InsuredPersonKeyFactorValue vehKF : insuredPerson.getKeyFactorValueList()) {
				if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {
					//paramMap.put("coveroptions", vehKF.getValue());
					int result = vehKF.getValue().length();
					paramMap.put("coveroptions", result >= 71 ?  vehKF.getValue().substring(0,52) + "\n" +"AND ACCIDENTAL TPD": vehKF.getValue());
				}
			}
			
			InputStream inputStream = null;
			if (isSimpleLife) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/SimpleLife/simpleLifeSanction.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate SimpleLifeSanction", e);
		}
	}

	private static boolean isDifferenceAddOnList(List<AddOn> oldList, List<AddOn> newList) {
		for (AddOn newAddOn : newList) {
			if (!oldList.contains(newAddOn)) {
				return true;
			}
		}

		for (AddOn oldAddOn : oldList) {
			if (!newList.contains(oldAddOn)) {
				return true;
			}
		}
		return false;
	}

	public static void generatePrintSetUpLifePolicy(EndorsementLifePolicyPrint endorsementPolicyPrint, LifeProposal lifeProposal, LifePolicy lifePolicy,
			LifePolicyHistory lifePolicyHistory, String dirPath, String fileName) {
		try {
			List jpList = new ArrayList();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifePolicy.getLifeProposal().getProposalNo());
			paramMap.put("extraRegulation", endorsementPolicyPrint.getExtraRegulation());
			paramMap.put("currentDate", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("submittedDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getLifeProposal().getSubmittedDate()));
			paramMap.put("commenmanceDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicyHistory.getCommenmanceDate()));
			paramMap.put("policyNo", lifePolicyHistory.getPolicyNo());
			paramMap.put("customerName", lifePolicyHistory.getCustomerName());
			paramMap.put("customerAddress", lifePolicyHistory.getCustomerAddress());
			paramMap.put("endorsementDescription", endorsementPolicyPrint.getEndorsementDescription());
			paramMap.put("endorseChange", endorsementPolicyPrint.getEndorseChange());
			paramMap.put("endorseChangeDetail", endorsementPolicyPrint.getEndorseChangeDetail());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyEndorsementIssue.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			if (jprint.getPages().size() > 1) {
				jprint.removePage(1);
			}
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PrintSetUp FirePolicy", e);
		}

	}

	/*
	 * for acceptanceLetter
	 */
	public static void generateLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		int numberOfEmployee = 0;
		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (proposalInsuredPerson.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				numberOfEmployee += 1;
			}
		}
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
			paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
			paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
			paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermAmount()));
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("totalEmployee", numberOfEmployee);
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream;
			if (isPublicLife(lifeProposal)) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/endowmentLifeAcceptanceInform.jrxml");
			} else {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/groupLifeAcceptanceInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	public static void generateFarmerAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isFarmer = KeyFactorChecker.isFarmer(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
			paramMap.put("approvedSumInsured", lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured());
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream = null;
			if (isFarmer) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/Farmer/farmerAcceptanceInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// for public term life report
	public static void generatePublicTermLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
			paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()));
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream = null;
			if (isPublicTermLife) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/PublicTermLife/publicTermLifeAcceptanceInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// for single premium endowment life Acceptance Letter
	public static void generateSinglePremiumEndowmentLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
			paramMap.put("approvedSumInsured", new BigDecimal(lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()).toPlainString());
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream = null;
			if (isSinglePremiumEndowmentLife) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/singlePremiumEndowmentLife/SinglePremiumEndowmentLifeAcceptanceInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// for single premium credit life Acceptance Letter
	public static void generateSinglePremiumCreditLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
			paramMap.put("approvedSumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agentPhone", lifeProposal.getAgent().getContentInfo().getPhoneOrMoblieNo());
			} else {
				paramMap.put("agentPhone", "( - )");
			}

			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SINGLEPREMIUMCREDITLIFE_INFORM_ACCEPTED_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// for single premium credit life Acceptance Letter
	public static void generateShortTermSinglePremiumCreditLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodYears());
			paramMap.put("approvedSumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream = null;
			if (isShortTermSinglePremiumCreditLife) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/shorttermsinglepremiumcreditlife/ShortTermSinglePremiumCreditLifeAcceptanceInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// for simple life Acceptance Letter
	public static void generateSimpleLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isSimpleLife = KeyFactorChecker.isSimpleLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriod());
			paramMap.put("approvedSumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()));
			paramMap.put("totalPremium", org.ace.insurance.common.Utils.getCurrencyFormatString(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream = null;
			if (isSimpleLife) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/SimpleLife/SimpleLifeAcceptanceInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	public static void generatePersonalAccidentAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		int numberOfEmployee = 0;

		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (proposalInsuredPerson.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				numberOfEmployee += 1;
			}
		}
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomer().getFullName());
			paramMap.put("customerAddress", lifeProposal.getCustomer().getFullAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getProposalInsuredPersonList().get(0).getPeriodMonth());
			paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
			paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
			paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermAmount()));
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("totalEmployee", numberOfEmployee);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			paramMap.put("isUSD", KeyFactorChecker.isPersonalAccidentUSD(product));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/personalAccident/personalAccidentAcceptanceInform.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;

			JasperExportManager.exportReportToPdfFile(jprint, outputFile);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	private static boolean isPublicLife(LifeProposal lifeProposal) {
		if (lifeProposal.getProposalInsuredPersonList().size() == 1) {
			return true;
		}
		return false;
	}

	private static boolean isEndownmentLife(LifeProposal lifeProposal) {
		if (lifeProposal.getProposalInsuredPersonList().size() > 1) {
			return true;
		}
		return false;
	}

	private static boolean isPersonalAccident(Product product) {
		if (KeyFactorChecker.isPersonalAccident(product)) {
			return true;
		}
		return false;
	}

	private static boolean isPersonalAccidentUSD(Product product) {
		if (KeyFactorChecker.isPersonalAccidentUSD(product)) {
			return true;
		}
		return false;
	}

	// for lifeRejectLetter
	public static void generateLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("imformedDate", acceptedInfo.getInformDate());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/LifeInformRejectLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	public static void generateFarmerRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isFarmer = KeyFactorChecker.isFarmer(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = null;
			if (isFarmer) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/Farmer/FarmerRejectLetter.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	// for Public Term life RejectLetter
	public static void generatePublicTermLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = null;
			if (isPublicTermLife) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/PublicTermLife/PublicTermLifeRejectLetter.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	// for Single Premium Endowment life RejectLetter
	public static void generateSinglePremiumEndowmentLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());

			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = null;
			if (isSinglePremiumEndowmentLife) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/singlePremiumEndowmentLife/SinglePremiumEndowmentLifeRejectLetter.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	// for Single Premium Credit life RejectLetter
	public static void generateSinglePremiumCreditLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());

			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("insuredpersonnrc", lifeProposal.getProposalInsuredPersonList().get(0).getIdNo());
			paramMap.put("insuredpersonName", lifeProposal.getProposalInsuredPersonList().get(0).getFullName());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SINGLEPREMIUMCREDITLIFE_INFORM_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	// for Short Term Single Premium Credit Life RejectLetter
	public static void generateShortTermSinglePremiumCreditLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			boolean isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());

			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = null;
			if (isShortTermSinglePremiumCreditLife) {
				inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("report-template/shorttermsinglepremiumcreditlife/ShortTermSinglePremiumCreditLifeRejectLetter.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	// for PersonalAccident RejectLetter
	public static void generatePersonalAccidentRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("imformedDate", acceptedInfo.getInformDate());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("report-template/personalAccident/personalAccidedntInformRejectLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	// for Agent Commission Payment
	public static void generateAgentCommissionPayment(AgentCommissionDTO agentCommission, TLF tlfForCoInsuPayCr, TLF tlfForCoInsuPayDr, String coaNameCr, String coaNameDr,
			String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("coaCodeCr", tlfForCoInsuPayCr.getCoaId());
			paramMap.put("coaCodeDr", tlfForCoInsuPayDr.getCoaId());
			paramMap.put("agentName", agentCommission.getAgent().getFullName());
			paramMap.put("licenseNo", agentCommission.getAgent().getLiscenseNo());
			paramMap.put("invoiceNo", agentCommission.getInvoiceNo());
			paramMap.put("amount", agentCommission.getCommission());
			paramMap.put("coaNameDr", coaNameDr);
			paramMap.put("coaNameCr", coaNameCr);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/AgentCommission.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate agentCommission", e);
		}
	}

	public static void generateStaffCommissionPayment(StaffCommissionDTO staffCommission, TLF tlfForCoInsuPayCr, TLF tlfForCoInsuPayDr, String coaNameCr, String coaNameDr,
			String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("coaCodeCr", tlfForCoInsuPayCr.getCoaId());
			paramMap.put("coaCodeDr", tlfForCoInsuPayDr.getCoaId());
			paramMap.put("agentName", staffCommission.getStaff().getFullName());
			paramMap.put("licenseNo", staffCommission.getStaff().getStaff_no());
			paramMap.put("invoiceNo", staffCommission.getInvoiceNo());
			paramMap.put("amount", staffCommission.getCommission());
			paramMap.put("coaNameDr", coaNameDr);
			paramMap.put("coaNameCr", coaNameCr);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/AgentCommission.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate agentCommission", e);
		}
	}

	// for OutCoinsurance Commission Payment
	public static void generateOutCoinsuranceCommissionPayment(Coinsurance coinsurance, TLF tlfForCoInsuPayCr, TLF tlfForCoInsuPayDr, TLF tlfForCoInsuCommDebit,
			TLF tlfForCoInsuCommCredit, String premiumtype, String insurerName, String coaName, String coaName2, String coaName3, String coaName4, String dirPath,
			String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("invoiceno", coinsurance.getInvoiceNo());
			paramMap.put("accountCode", tlfForCoInsuPayCr.getCoaId());
			paramMap.put("accountCode2", tlfForCoInsuPayDr.getCoaId());
			paramMap.put("accountCode3", tlfForCoInsuCommDebit.getCoaId());
			paramMap.put("accountCode4", tlfForCoInsuCommCredit.getCoaId());
			paramMap.put("accountName", coaName);
			paramMap.put("accountName2", coaName2);
			paramMap.put("accountName3", coaName3);
			paramMap.put("accountName4", coaName4);
			paramMap.put("premium", premiumtype);
			paramMap.put("sumInsured", coinsurance.getSumInsuranced());
			paramMap.put("insuredName", insurerName);
			paramMap.put("amount", tlfForCoInsuPayCr.getHomeAmount());
			paramMap.put("amount2", tlfForCoInsuCommDebit.getHomeAmount());
			paramMap.put("companyName", coinsurance.getCoinsuranceCompany().getName());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/coinsurance/coinsurance.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate CoinsuranceCommission", e);
		}
	}

	/*
	 * life Policy Ledger
	 */
	public static void generatePublicLifePolicyLedger(LifePolicy lifePolicy, Date surveydate, String dirPath, String fileName) {
		try {
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
			DateTime policyEndDate = new DateTime(lifePolicy.getActivedPolicyStartDate());
			String endYear = String.valueOf(policyEndDate.getYear() + lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears());
			String endMonth = String.valueOf(policyEndDate.getMonthOfYear());
			String endDay = String.valueOf(policyEndDate.getDayOfMonth());
			DateTime policyStartDate = new DateTime(lifePolicy.getActivedPolicyStartDate());
			String startYear = String.valueOf(policyStartDate.getYear());
			String startMonth = String.valueOf(policyStartDate.getMonthOfYear());
			String startDay = String.valueOf(policyStartDate.getDayOfMonth());
			DateTime endDate = new DateTime(insuredPerson.getLastPaymentDate());
			String paymentEndYear = String.valueOf(endDate.getYear());
			String paymentEndMonth = String.valueOf(endDate.getMonthOfYear());
			String paymentEndDay = String.valueOf(endDate.getDayOfMonth());
			paramMap.put("endDay", endDay);
			paramMap.put("endMonth", endMonth);
			paramMap.put("endYear", endYear);
			paramMap.put("startDay", startDay);
			paramMap.put("startMonth", startMonth);
			paramMap.put("startYear", startYear);
			paramMap.put("paymentEndDay", paymentEndDay);
			paramMap.put("paymentEndMonth", paymentEndMonth);
			paramMap.put("paymentEndYear", paymentEndYear);
			paramMap.put("policyNo", lifePolicy.getPolicyNo());
			paramMap.put("period", lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears());
			paramMap.put("agent", lifePolicy.getAgent() == null ? "" : lifePolicy.getAgent().getFullName() + "(" + lifePolicy.getAgent().getLiscenseNo() + ")");
			paramMap.put("sumInsured", lifePolicy.getTotalSumInsured());
			/* Myanmar */
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			paramMap.put("myanSumInsured", convertor.getName(lifePolicy.getTotalSumInsured()));
			paramMap.put("customerName", lifePolicy.getCustomer() == null ? lifePolicy.getOrganization().getName() : lifePolicy.getCustomer().getFullName());
			paramMap.put("customerAddress", lifePolicy.getCustomerAddress());
			paramMap.put("age", lifePolicy.getInsuredPersonInfo().get(0).getAge() + " Y");
			paramMap.put("submittedDate", lifePolicy.getLifeProposal().getSubmittedDate());
			paramMap.put("surveyDate", surveydate);
			paramMap.put("activePolicySD", lifePolicy.getCommenmanceDate());
			paramMap.put("totalPremium", lifePolicy.getTotalPremium());
			paramMap.put("termPremium", lifePolicy.getTotalTermPremium());
			paramMap.put("paymentType", lifePolicy.getPaymentType().getName());
			paramMap.put("occupation",
					lifePolicy.getCustomer() == null ? "" : lifePolicy.getCustomer().getOccupation() == null ? "" : lifePolicy.getCustomer().getOccupation().getName());
			List<PolicyInsuredPersonBeneficiaries> benfList = lifePolicy.getInsuredPersonInfo().get(0).getPolicyInsuredPersonBeneficiariesList();
			if (benfList.size() > 2) {
				paramMap.put("TableDataSource", null);
			} else {
				paramMap.put("TableDataSource", new JRBeanCollectionDataSource(lifePolicy.getInsuredPersonInfo().get(0).getPolicyInsuredPersonBeneficiariesList()));
			}
			paramMap.put("timeSlotList", lifePolicy.getInsuredPersonInfo().get(0).getTimeSlotList());
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyLedger.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramMap, new JREmptyDataSource());
			jpList.add(jprint2);
			if (benfList.size() > 2) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("insuredPersonName", lifePolicy.getCustomerName());
				param.put("policyNo", lifePolicy.getPolicyNo());
				param.put("idNo", lifePolicy.getInsuredPersonInfo().get(0).getIdNo());
				param.put("listDataSource", new JRBeanCollectionDataSource(benfList));
				InputStream inputStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyBeneficiariesLedger.jrxml");
				JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
				JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, param, new JREmptyDataSource());
				jpList.add(jprint3);
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double getTotalSI(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getSumInsured();
		}
		return result;
	}

	public static double getTotalPremium(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getPremium();
		}
		return result;
	}

	public static double getTotalOwnSI(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getSumInsuredOwn();
		}
		return result;
	}

	public static double getTotalOwnPremium(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getPremiumOwn();
		}
		return result;
	}

	public static double getTotalMISI(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getSumInsuredMI();
		}
		return result;
	}

	public static double getTotalMIPremium(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getPremiumMI();
		}
		return result;
	}

	public static double getTotalAgentComm(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getAgentCommission();
		}
		return result;
	}

	public static double getTotalUnder(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getUnderwritingExpense();
		}
		return result;
	}

	public static double getTotalNetPremium(List<CoinsuranceDTO> coinsuranceLists) {
		double result = 0.0;
		for (CoinsuranceDTO c : coinsuranceLists) {
			result += c.getNetPremium();
		}
		return result;
	}

	/*
	 * Fire Proposal Cash Receipt
	 */

	public static void generateLifeCashReceiptList(List<LifeCashReceiptListReportDTO> lifeCashReceiptList, String dirPath, String fileName) {
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		for (LifeCashReceiptListReportDTO reportDTO : lifeCashReceiptList) {
			List ret = generateLifeCashReceipt(reportDTO.getProposal(), reportDTO.getPayment(), reportDTO.getCoinsuranceCashReceipt());
			jasperPrintList.addAll(ret);
		}
		jasperListPDFExport(jasperPrintList, dirPath, fileName);
	}

	/*
	 * Life Proposal Cash Receipt
	 */
	public static List generateLifeCashReceipt(LifeProposal lifeProposal, PaymentDTO payment, CoinsuranceCashReceiptDTO coinsuranceDTO) {
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			boolean hasCoinsurance = coinsuranceDTO != null && coinsuranceDTO.isHasCoinsurance() ? true : false;
			if (lifeProposal.getLifePolicy() != null) {
				paramMap.put("premiumName", "Endorsement Premium");
				paramMap.put("addOnPremiumName", "Endorsement Additional Premium");
			} else {
				paramMap.put("premiumName", "Premium");
				paramMap.put("addOnPremiumName", "Additional Premium");
			}
			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paramMap.put("chequeNo", payment.getChequeNo());
				paramMap.put("bank", payment.getBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
				if (hasCoinsurance) {
					paramMap.put("receiptType", "Temporary Receipt (Coinsurance)");
				} else {
					paramMap.put("receiptType", "Temporary Receipt");
				}
				paramMap.put("receiptName", "Receipt No.");
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				if (hasCoinsurance) {
					paramMap.put("receiptType", "Cash Receipt (Coinsurance)");
				} else {
					paramMap.put("receiptType", "Cash Receipt");
				}
				paramMap.put("receiptName", "Cash Receipt No.");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("sumInsured", lifeProposal.getSumInsured());
			paramMap.put("premium", payment.getBasicPremium());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("addOnPremium", payment.getAddOnPremium());
			paramMap.put("netPremium", payment.getNetPremium());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("totalAmount", payment.getTotalAmount());
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("policyType", lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getName());
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("fromDate", lifeProposal.getProposalInsuredPersonList().get(0).getStartDate());
			paramMap.put("toDate", lifeProposal.getProposalInsuredPersonList().get(0).getEndDate());
			String agent = "";
			if (lifeProposal.getAgent() != null) {
				agent = lifeProposal.getAgent().getFullName() + " (" + lifeProposal.getAgent().getLiscenseNo() + ")";
			}
			paramMap.put("agent", agent);
			paramMap.put("insuredPerson", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifeCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);
			// Check whether life proposal is needed for co-insurance, to
			// generate compile report
			if (hasCoinsurance) {
				// prepare params
				coinsuranceDTO.setCashReceiptNo(payment.getReceiptNo());
				Map<String, Object> params2 = prepareCreditDebitReportParams(lifeProposal.getCustomerName(), lifeProposal.getTotalSumInsured(), lifeProposal.getTotalPremium(),
						coinsuranceDTO);
				InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/coinsurance/creditAndDebitVoucher.jrxml");
				JasperReport jReport2 = JasperCompileManager.compileReport(inputStream2);
				JasperPrint jPrint2 = JasperFillManager.fillReport(jReport2, params2, new JREmptyDataSource());
				// add jasper print to list
				jasperPrintList.add(jPrint2);
			}
			// jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate FireCashReceipt", e);
		}
		return jasperPrintList;
	}

	public static void generateTravelCashReceipt(TravelProposal travelProposal, PaymentDTO payment, String dirPath, String fileName, PaymentOrderCashReceiptDTO orderDto) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", travelProposal.getProposalNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("receiptType", "Cash Receipt");
			paramMap.put("receiptName", "Cash Receipt No");
			String bankName = null;
			String paymentChannel = null;
			String chequeNo = null;
			if (payment.getBank() != null) {
				if (payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
					bankName = payment.getAccountBank().getName();
				} else if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
					bankName = payment.getBank().getName();
				}
				chequeNo = payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY) ? payment.getChequeNo()
						: payment.getPoNo();
			}
			paramMap.put("bank", bankName);
			paramMap.put("chequeNo", chequeNo);
			paramMap.put("premium", travelProposal.getTotalNetPremium());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("netPremium", travelProposal.getTotalNetPremium());
			paramMap.put("paymentType", travelProposal.getPaymentType().getName());
			paramMap.put("numberOfTravelExpress", travelProposal.getExpressList().size());
			paramMap.put("numberOfPassenger", travelProposal.getTotalPassenger());
			paramMap.put("numberOfUnit", travelProposal.getTotalUnit());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("commission", travelProposal.getTotalCommission());
			paramMap.put("totalAmount", travelProposal.getTotalNetPremium());
			paramMap.put("confirmDate", new Date());
			paramMap.put("chequePayment", false);
			paramMap.put("policyBranch", travelProposal.getBranch().getName());
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/travel/travelCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());

			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("totalPassenger", travelProposal.getTotalPassenger());
			paramMap2.put("branch", travelProposal.getBranch().getDescription());
			paramMap2.put("totalUnit", travelProposal.getTotalUnit());
			paramMap2.put("totalNetPremium", travelProposal.getTotalNetPremium());
			paramMap2.put("Date", new Date());
			paramMap2.put("fromDate", Utils.formattedDate(travelProposal.getFromDate()));
			paramMap2.put("toDate", Utils.formattedDate(travelProposal.getToDate()));
			paramMap2.put("TableDataSource", new JRBeanCollectionDataSource(travelProposal.getExpressList()));
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/travel/travelCashReceipt2.jrxml");
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramMap2, new JREmptyDataSource());
			jasperPrintList.add(jprint);
			jasperPrintList.add(jprint2);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(outputFile));
			exporter.exportReport();

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate MotorCashReceipt", e);
		}
	}

	public static void generatePersonTravelCashReceipt(PersonTravelProposal personTravelProposal, PaymentDTO payment, String dirPath, String fileName,
			PaymentOrderCashReceiptDTO orderDto) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			paramMap.put("proposalNo", personTravelProposal.getProposalNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());

			String customerName = null;
			customerName = personTravelProposal.getCustomer() != null ? personTravelProposal.getCustomer().getFullName() : personTravelProposal.getOrganization().getName();
			paramMap.put("customerName", customerName);

			String bankName = null;
			String paymentChannel = null;
			String chequeNo = null;
			if (payment.getBank() != null) {
				if (payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
					bankName = payment.getAccountBank().getName();
				} else if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
					bankName = payment.getBank().getName();
				}
				paymentChannel = payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY) ? "Cheque/PO No"
						: "Transfer No";
				chequeNo = payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY) ? payment.getChequeNo()
						: payment.getPoNo();
			}
			paramMap.put("bank", bankName);
			paramMap.put("chequeOrPo", paymentChannel);
			paramMap.put("chequeNo", chequeNo);
			paramMap.put("premium", personTravelProposal.getPersonTravelInfo().getPremium());
			paramMap.put("netPremium", personTravelProposal.getPersonTravelInfo().getPremium() - payment.getDiscountAmount());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("policyType", personTravelProposal.getProduct().getName());
			paramMap.put("fromDate", personTravelProposal.getPersonTravelInfo().getDepartureDate());
			paramMap.put("toDate", personTravelProposal.getPersonTravelInfo().getArrivalDate());
			paramMap.put("confirmDate", new Date());
			paramMap.put("policyBranch", personTravelProposal.getBranch().getName());
			paramMap.put("numberOfPassenger", personTravelProposal.getPersonTravelInfo().getNoOfPassenger());
			paramMap.put("numberOfUnit", personTravelProposal.getPersonTravelInfo().getTotalUnit());
			String agent = "";
			if (personTravelProposal.getAgent() != null) {
				agent = personTravelProposal.getAgent().getFullName() + " ( " + personTravelProposal.getAgent().getLiscenseNo() + " ) ";
			}
			paramMap.put("agent", agent);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/travel/personTravel/personTravelCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, jprint);
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(outputFile));
			exporter.exportReport();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate MotorCashReceipt", e);
		}
	}

	/**
	 * Life Payment Bill Collection Cash Receipt.
	 * 
	 * @param lineBeans
	 *            set line bean data list for report.
	 * @param dirPath
	 *            set directory path for report file.
	 * @param fileName
	 *            set file name for report file.
	 */
	public static void generateLifePaymentBillCashReceipt(List<BillCollectionCashReceiptDTO> cashReceiptDTOs, String dirPath, String fileName, boolean isEnquiry) {
		try {
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			LifePolicy policy = null;
			String policyNo = "";
			double sumInsured = 0.0;
			Date fromDate;
			String agentName = "";
			String insuredPerson = "";
			String customerAddress = "";
			String sumInsuredLabel;
			String unitOrSI = "";
			double extraAmount = 0.0;
			Date activePolicyStartDate = null;
			PaymentType paymentType = null;
			String policyBranch = "";
			for (BillCollectionCashReceiptDTO cashReceiptDTO : cashReceiptDTOs) {
				Payment payment = cashReceiptDTO.getPayment();
				if (ReferenceType.MEDICAL_POLICY.equals(cashReceiptDTO.getReferenceType()) || ReferenceType.MEDICAL_BILL_COLLECTION.equals(cashReceiptDTO.getReferenceType())
						|| ReferenceType.HEALTH_POLICY.equals(cashReceiptDTO.getReferenceType()) || ReferenceType.CRITICAL_ILLNESS_POLICY.equals(cashReceiptDTO.getReferenceType())
						|| ReferenceType.MICRO_HEALTH_POLICY.equals(cashReceiptDTO.getReferenceType())) {
					MedicalPolicy medicalPolicy = cashReceiptDTO.getMedicalPolicy();
					policyNo = medicalPolicy.getPolicyNo();
					sumInsured = medicalPolicy.getTotalUnit();
					activePolicyStartDate = medicalPolicy.getActivedPolicyStartDate();
					paymentType = medicalPolicy.getPaymentType();
					fromDate = medicalPolicy.getActivedPolicyEndDate();
					agentName = medicalPolicy.getAgent() == null ? "" : medicalPolicy.getAgent().getFullName();
					insuredPerson = medicalPolicy.getCustomerName();
					customerAddress = medicalPolicy.getCustomerAddress();
					sumInsuredLabel = "Units";
					unitOrSI = "Unit";
					policyBranch = medicalPolicy.getBranch().getName();
				} else {
					LifePolicy lifePolicy = cashReceiptDTO.getLifePolicy();
					policyNo = lifePolicy.getPolicyNo();
					sumInsured = lifePolicy.getTotalSumInsured();
					activePolicyStartDate = lifePolicy.getActivedPolicyStartDate();
					fromDate = lifePolicy.getActivedPolicyEndDate();
					agentName = lifePolicy.getAgent() == null ? "" : lifePolicy.getAgent().getFullName();
					insuredPerson = lifePolicy.getCustomerName();
					customerAddress = lifePolicy.getCustomerAddress();
					paymentType = lifePolicy.getPaymentType();
					sumInsuredLabel = "Sum Insured";
					unitOrSI = "SI";
					policyBranch = lifePolicy.getBranch().getName();
					if (cashReceiptDTO.getBillCollection() != null)
						extraAmount = cashReceiptDTO.getBillCollection().getExtraAmount();
				}

				AgentPaymentCashReceiptDTO agentCashReceipt = cashReceiptDTO.getAgentComission();
				String cashReceiptNo = payment.getReceiptNo();
				int paymentTimes = payment.getToTerm() - payment.getFromTerm() + 1;
				double basicPremium = 0.0;
				double totalAmount = payment.getBasicPremium();
				/*
				 * if (paymentTimes > 1) { basicPremium =
				 * org.ace.insurance.common.Utils.getTwoDecimalPoint(totalAmount
				 * / paymentTimes); } else { basicPremium =
				 * payment.getBasicPremium(); }
				 */
				basicPremium = payment.getBasicPremium();
				double addOnPremium = payment.getAddOnPremium();
				String paymentTerm = payment.getPaymentType().getName() + " ( " + payment.getFromTerm() + " to " + payment.getToTerm() + " )";
				double renewalInterest = payment.getRenewalInterest();
				double loanInterest = payment.getLoanInterest();
				double serviceCharges = payment.getServicesCharges();

				// double totalAmount = basicPremium + addOnPremium +
				// serviceCharges + loanInterest + renewalInterest -
				// payment.getRefund() - extraAmount;
				Date confirmDate = payment.getConfirmDate();
				Date toDate = null;
				if (isEnquiry) {
					int term = payment.getFromTerm();
					Calendar calendar1 = Calendar.getInstance();
					calendar1.setTime(activePolicyStartDate);
					calendar1.add(Calendar.MONTH, payment.getPaymentType().getMonth() * (term - 1));
					fromDate = calendar1.getTime();

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(fromDate);
					calendar.add(Calendar.MONTH, payment.getPaymentType().getMonth() * paymentTimes);
					toDate = calendar.getTime();

				} else {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(fromDate);
					calendar.add(Calendar.MONTH, payment.getPaymentType().getMonth() * paymentTimes);
					toDate = calendar.getTime();
				}
				Map<String, Object> paramMap = new HashMap<String, Object>();
				if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
					paramMap.put("chequePayment", Boolean.FALSE);
					paramMap.put("receiptType", "Cash Receipt");
					paramMap.put("receiptName", "Cash Receipt No.");
				} else {
					PaymentChannel channel = payment.getPaymentChannel();
					paramMap.put("chequeNo", channel.equals(PaymentChannel.CHEQUE) || channel.equals(PaymentChannel.SUNDRY) ? payment.getChequeNo() : payment.getPoNo());
					paramMap.put("chequeType", channel.equals(PaymentChannel.CHEQUE) || channel.equals(PaymentChannel.SUNDRY) ? "Cheque No." : "PO No.");
					paramMap.put("bank", payment.getAccountBank().getName());
					if (channel.equals(PaymentChannel.SUNDRY)) {
						paramMap.put("bank", payment.getAccountBank().getName());
					} else if (channel.equals(PaymentChannel.CHEQUE)) {
						paramMap.put("bank", payment.getBank().getName());
					}
					paramMap.put("chequePayment", Boolean.TRUE);
					paramMap.put("receiptType", "Temporary Receipt");
					paramMap.put("receiptName", "Receipt No.");
				}
				paramMap.put("policyNo", policyNo);
				paramMap.put("cashReceiptNo", cashReceiptNo);
				paramMap.put("sumInsured", sumInsured);
				paramMap.put("paymentTimes", paymentTimes);
				paramMap.put("paymentTerm", paymentTerm);
				paramMap.put("basicPremium", basicPremium);
				paramMap.put("addOnPremium", addOnPremium);
				paramMap.put("renewalInterest", renewalInterest);
				paramMap.put("loanInterest", loanInterest);
				paramMap.put("serviceCharges", serviceCharges);
				paramMap.put("totalAmount", payment.getNetPremium());
				paramMap.put("confirmDate", confirmDate);
				paramMap.put("fromDate", fromDate);
				paramMap.put("toDate", toDate);
				paramMap.put("agent", agentName);
				paramMap.put("insuredPerson", insuredPerson);
				paramMap.put("customerAddress", customerAddress);
				paramMap.put("sumInsuredLabel", sumInsuredLabel);
				paramMap.put("extraAmount", extraAmount);
				paramMap.put("policyBranch", policyBranch);
				paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
				InputStream inputStream;
				if (extraAmount > 0.0) {
					paramMap.put("extraAmount", extraAmount);
					inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/shortTermLifePaymentBillCollectionCashReceipt.jrxml");
				} else {
					inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePaymentBillCollectionCashReceipt.jrxml");
				}
				JasperReport jreport = JasperCompileManager.compileReport(inputStream);
				JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
				jpList.add(jprint);

				/*
				 * if ((agentCashReceipt != null) && agentName != null) {
				 * agentCashReceipt.setCashReceiptNo(payment.getReceiptNo());
				 * Map<String, Object> params = new HashMap<String, Object>();
				 * params.put("agentExpenseAcountCode",
				 * agentCashReceipt.getAgentExpenseAcountCode());
				 * params.put("agentExpenseAccountName",
				 * agentCashReceipt.getAgentExpenseAccountName());
				 * params.put("agentPayableAccountCode",
				 * agentCashReceipt.getAgentPayableAccountCode());
				 * params.put("agentPayableAccountName",
				 * agentCashReceipt.getAgentPayableAccountName());
				 * params.put("cashReceiptNo",
				 * agentCashReceipt.getCashReceiptNo());
				 * params.put("agentExpenseDbAmount",
				 * agentCashReceipt.getAgentExpenseDbAmount());
				 * params.put("agentPayableCrAmount",
				 * agentCashReceipt.getAgentPayableCrAmount());
				 * params.put("confirmDate", agentCashReceipt.getConfirmDate());
				 * params.put("agentName", agentName); params.put("siAmount",
				 * sumInsured); params.put("totalPremiumAmount",
				 * payment.getNetPremium()); params.put("confirmDate",
				 * payment.getConfirmDate()); params.put("unitOrSI", unitOrSI);
				 * InputStream agentInputStream =
				 * Thread.currentThread().getContextClassLoader().
				 * getResourceAsStream(
				 * "report-template/agent/expenseAndPayableVoucher.jrxml");
				 * JasperReport agentJreport =
				 * JasperCompileManager.compileReport(agentInputStream);
				 * JasperPrint agentJprint =
				 * JasperFillManager.fillReport(agentJreport, params, new
				 * JREmptyDataSource()); jpList.add(agentJprint); } }
				 */
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(outputFile));
			exporter.exportReport();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifePaymentBillCashReceipt", e);
		}
	}

	// Agent Commission Invoice Slip
	public static void generateAgentCommissionInvoiceSlip(Agent agent, List<AgentCommission> agentCommisionSelectList, Date maxdate, Date mindate, String codeForCoInsuPayCr,
			String codeForCoInsuPayDr, String coaNameCr, String coaNameDr, Double totalcommission, List<AgentCommissionReportDTO> agentCommissionReportDTOList, String dirPath,
			String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			String myanmarTotalAmmoumt = convertor.getName(totalcommission);
			if (agentCommisionSelectList.get(0).getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("currentDate", new Date());
				if (agent.getName() == null) {
					paramMap.put("agentName", "");
				} else {
					paramMap.put("agentName", agent.getFullName());
				}
				if (agent.getIdNo() == null) {
					paramMap.put("idNo", "");
				} else {
					paramMap.put("idNo", agent.getIdNo());
				}
				if (agent.getFullAddress() == null) {
					paramMap.put("address", "");
				} else {
					paramMap.put("address", agent.getFullAddress());
				}
				if (totalcommission == null) {
					paramMap.put("totalcommission", "");
				} else {
					paramMap.put("totalcommission", totalcommission);
				}
				if (agent.getName() == null) {
					paramMap.put("agentCode", "");
				} else {
					paramMap.put("agentCode", agent.getLiscenseNo());
				}
				if (agent.getIdNo() == null) {
					paramMap.put("licenseNo", "");
				} else {
					paramMap.put("licenseNo", agent.getLiscenseNo());
				}
				paramMap.put("myanmarTotalAmmoumt", myanmarTotalAmmoumt);
				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/AgentCommissionPaymentOrder.jrxml");
				JasperReport jreport = JasperCompileManager.compileReport(inputStream);
				JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
				jasperPrintList.add(jprint);
			}

			String producttype = agentCommisionSelectList.get(0).getReferenceType().toString();
			Map<String, Object> paramMap3 = new HashMap<String, Object>();
			if (agentCommisionSelectList.get(0).getPaymentChannel().equals(PaymentChannel.CASHED)) {
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
			paramMap3.put("invoiceNo", agentCommisionSelectList.get(0).getInvoiceNo());
			paramMap3.put("amount", totalcommission);
			paramMap3.put("coaNameDr", coaNameDr);
			paramMap3.put("coaNameCr", coaNameCr);
			paramMap3.put("nrc", agent.getIdNo());
			paramMap3.put("maxdate", maxdate);
			paramMap3.put("mindate", mindate);
			paramMap3.put("amount", totalcommission);
			AbstractProcessor processor = new DefaultProcessor();
			paramMap3.put("wordAmount", processor.getName(totalcommission));
			InputStream inputStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/AgentCommission.jrxml");
			JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
			JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, paramMap3, new JREmptyDataSource());
			jasperPrintList.add(jprint3);

			Map<String, Object> paramMap4 = new HashMap<String, Object>();
			paramMap4.put("date", new Date());
			if (agent.getName() != null) {
				paramMap4.put("agentName", agent.getFullName());
			} else {
				paramMap4.put("agentName", "-");
			}
			if (agent.getCodeNo() != null) {
				paramMap4.put("agentNo", agent.getLiscenseNo());
			} else {
				paramMap4.put("agentNo", "-");
			}
			if (agent.getFullAddress() != null) {
				paramMap4.put("address", agent.getFullAddress());
			} else {
				paramMap4.put("address", "-");
			}
			if (producttype != null) {
				if (producttype == "FIRE_POLICY") {
					paramMap4.put("typeOfProduct", "FIRE INSURANCE");
				} else if (producttype == "LIFE_POLICY") {
					paramMap4.put("typeOfProduct", "LIFE INSURANCE");
				} else
					paramMap4.put("typeOfProduct", "MOTOR INSURANCE");
			} else {
				paramMap4.put("typeOfProduct", "-");
			}

			if (agent.getIdNo() != null) {
				paramMap4.put("nrc", agent.getIdNo());
			} else {
				paramMap4.put("nrc", "-");
			}

			if (agentCommisionSelectList.size() != 0) {
				paramMap4.put("TableDataSource", new JRBeanCollectionDataSource(agentCommissionReportDTOList));
			} else {
				paramMap4.put("TableDataSource", new JREmptyDataSource());
			}
			double totalPremium = 0.0;
			double totalCommissionAmount = 0.0;
			for (AgentCommissionReportDTO agentCommissionReportDTO : agentCommissionReportDTOList) {
				totalPremium += agentCommissionReportDTO.getPremium();
				totalCommissionAmount += agentCommissionReportDTO.getCommissionAmount();
			}
			paramMap4.put("totalPremium", totalPremium);
			paramMap4.put("totalCommissionAmount", totalCommissionAmount);

			InputStream inputStream4 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/agent/AgentComissionCustomerReceipt.jrxml");
			JasperReport jreport4 = JasperCompileManager.compileReport(inputStream4);
			JasperPrint jprint4 = JasperFillManager.fillReport(jreport4, paramMap4, new JREmptyDataSource());
			jasperPrintList.add(jprint4);

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

	/* LifePolicy Notification Letter */
	public static void generateLifePolicyNotification(List<LifePolicy> policies, String dirPath, String fileName) {
		try {
			List jpList = new ArrayList();
			for (LifePolicy lifePolicy : policies) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				int policyPeriodYears = 0;
				Date activePolicyStartDate = lifePolicy.getPolicyInsuredPersonList().get(0).getStartDate();
				Date activePolicyEndDate = lifePolicy.getPolicyInsuredPersonList().get(0).getEndDate();
				Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
				int policyPeriodMonths = org.ace.insurance.common.Utils.monthsBetween(activePolicyStartDate, activePolicyEndDate, false, false);
				if (policyPeriodMonths > 12) {
					policyPeriodYears = policyPeriodMonths / 12;
				}
				int month = lifePolicy.getPaymentType().getMonth();
				int remainingTerm = 0, currentTerm = 0, lastPaymentTerm = lifePolicy.getLastPaymentTerm() != 0 ? lifePolicy.getLastPaymentTerm() : 1;
				if ((policyPeriodMonths / month) != lastPaymentTerm) {
					remainingTerm = (policyPeriodMonths / month) - lastPaymentTerm;
					currentTerm = lastPaymentTerm + 1;
				}
				Calendar cal = Calendar.getInstance();
				paramMap.put("notificationDate", org.ace.insurance.web.common.Utils.formattedDate(new Date()));
				paramMap.put("insuredPersonName", lifePolicy.getInsuredPersonInfo().get(0).getFullName());
				paramMap.put("insuredPersonAddress", lifePolicy.getCustomerAddress());
				paramMap.put("salePersonName", lifePolicy.getSalePersonName());
				paramMap.put("phoneNo", lifePolicy.getCustomerPhoneNo());
				paramMap.put("sumInsured", lifePolicy.getTotalSumInsuredString());
				paramMap.put("policyNo", lifePolicy.getPolicyNo());
				paramMap.put("basicTermPremium", lifePolicy.getTotalBasicTermPremiumString());
				paramMap.put("activePolicyStartDate", org.ace.insurance.web.common.Utils.formattedDate(activePolicyStartDate));
				paramMap.put("activePolicyEndDate", org.ace.insurance.web.common.Utils.formattedDate(activePolicyEndDate));
				paramMap.put("policyLifeTime", policyPeriodYears);
				paramMap.put("month", month);
				paramMap.put("currentTerm", currentTerm);
				paramMap.put("remainingTerm", remainingTerm);
				paramMap.put("currentYear", cal.get(Calendar.YEAR));
				paramMap.put("isPublicLife", KeyFactorChecker.isPublicLife(product));
				InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyNotification2.jrxml");
				JasperReport report = JasperCompileManager.compileReport(inputStr);
				JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
				jpList.add(print);
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifePolicyNotification", e);
		}
	}

	public static void generateMedicalPolicyNotification(List<MedicalPolicy> policies, String dirPath, String fileName, ReferenceType referenceType) {
		try {
			List jpList = new ArrayList();
			for (MedicalPolicy medicalPolicy : policies) {
				String basicUnit = "0  Units";
				Map<String, Object> paramMap = new HashMap<String, Object>();
				int policyPeriodYears = 0;
				MedicalPolicyInsuredPerson insuredPerson = medicalPolicy.getPolicyInsuredPersonList().get(0);
				Date activePolicyStartDate = insuredPerson.getStartDate();
				Date activePolicyEndDate = insuredPerson.getEndDate();
				int policyPeriodMonths = org.ace.insurance.common.Utils.monthsBetween(activePolicyStartDate, activePolicyEndDate, false, false);
				if (policyPeriodMonths > 12 || policyPeriodMonths == 12) {
					policyPeriodYears = policyPeriodMonths / 12;
				}
				int month = medicalPolicy.getPaymentType().getMonth();
				int remainingTerm = 0, currentTerm = 0, lastPaymentTerm = medicalPolicy.getLastPaymentTerm() != 0 ? medicalPolicy.getLastPaymentTerm() : 1;
				if ((policyPeriodMonths / month) != lastPaymentTerm) {
					remainingTerm = (policyPeriodMonths / month) - lastPaymentTerm;
					currentTerm = lastPaymentTerm + 1;
				}
				Calendar cal = Calendar.getInstance();
				paramMap.put("notificationDate", org.ace.insurance.web.common.Utils.formattedDate(new Date()));
				paramMap.put("insuredPersonName", insuredPerson.getFullName());
				paramMap.put("insuredPersonAddress", medicalPolicy.getCustomerAddress());
				paramMap.put("salePersonName", medicalPolicy.getSalePersonName());
				paramMap.put("phoneNo", medicalPolicy.getCustomerPhoneNo());
				basicUnit = insuredPerson.getUnit() + (insuredPerson.getUnit() == 1 ? " Unit" : " Units");
				String basicPlus = "", additional1 = null, additional2 = null;
				if (insuredPerson.getUnit() > 0) {
					basicPlus += "Basic";
				}
				if (insuredPerson.getUnit() > 0 && insuredPerson.getBasicPlusUnit() > 0) {
					basicPlus += " + ";
				}
				if (insuredPerson.getBasicPlusUnit() != 0) {
					basicPlus += "Basic Plus (" + insuredPerson.getBasicPlusUnit() + " Units)";
				}
				for (MedicalPolicyInsuredPersonAddOn addOn : insuredPerson.getPolicyInsuredPersonAddOnList()) {
					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn2()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn1())) {
						additional1 = "Additional Cover (1)- " + addOn.getUnit() + (addOn.getUnit() == 1 ? " Unit" : " Units");
					}
					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn3()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn2())) {
						additional2 = ", Additional Cover (2)- " + addOn.getUnit() + (addOn.getUnit() == 1 ? " Unit" : " Units");
					}
				}

				paramMap.put("basicUnit", basicUnit);
				paramMap.put("basicPlus", basicPlus);
				paramMap.put("additional1", additional1);
				paramMap.put("additional2", additional2);
				paramMap.put("policyNo", medicalPolicy.getPolicyNo());
				paramMap.put("termPremium", medicalPolicy.getTotalBasicTermPremiumString());
				paramMap.put("activePolicyStartDate", org.ace.insurance.web.common.Utils.formattedDate(activePolicyStartDate));
				paramMap.put("activePolicyEndDate", org.ace.insurance.web.common.Utils.formattedDate(activePolicyEndDate));
				paramMap.put("policyLifeTime", policyPeriodYears);
				paramMap.put("month", month);
				paramMap.put("currentTerm", currentTerm);
				paramMap.put("remainingTerm", remainingTerm);
				paramMap.put("currentYear", cal.get(Calendar.YEAR));

				if (ReferenceType.CRITICAL_ILLNESS_POLICY.equals(referenceType)) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/medical/criticalIllnessPolicyNotification.jrxml");
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				}
				if (ReferenceType.HEALTH_POLICY.equals(referenceType)) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/medical/medicalHealthPolicyNotification.jrxml");
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				}
				if (ReferenceType.MEDICAL_POLICY.equals(referenceType)) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/medical/medicalPolicyNotification.jrxml");
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				}
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifePolicyNotification", e);
		}
	}

	/*
	 * Co-insurance CashReceipt
	 */
	public static void generatecoinsuranceCashReceipt(Coinsurance coinsurance, String dirPath, String fileName, String sLCredit, String sLDebit, String debitLetter) {
		try {
			List jpList = new ArrayList();

			boolean isCheque = coinsurance.getCustomerbank() != null ? true : false;
			Map cashReceiptMap = new HashMap();
			cashReceiptMap.put("isCheque", isCheque);
			cashReceiptMap.put("insuranceType", coinsurance.getInsuranceType().getLabel());
			cashReceiptMap.put("policyNo", coinsurance.getPolicyNo());
			cashReceiptMap.put("chequeNo", isCheque ? coinsurance.getChequeNo() : "");
			cashReceiptMap.put("cashReceiptNo", coinsurance.getInvoiceNo());
			cashReceiptMap.put("bank", isCheque ? coinsurance.getCustomerbank().getName() : "");
			cashReceiptMap.put("sumInsured", coinsurance.getReceivedSumInsured());
			cashReceiptMap.put("premium", coinsurance.getPremium());
			cashReceiptMap.put("discountAmount", 0.0);
			cashReceiptMap.put("additionalPremium", 0.0);
			cashReceiptMap.put("netPremium", coinsurance.getNetPremium());
			cashReceiptMap.put("serviceCharges", 0.0);
			cashReceiptMap.put("stempFees", 0.0);
			cashReceiptMap.put("totalAmount", coinsurance.getNetPremium());
			cashReceiptMap.put("receiptType", isCheque ? "Temporary Receipt" : "Cash Receipt");
			cashReceiptMap.put("paymentType", "");
			cashReceiptMap.put("confirmDate", coinsurance.getInvoiceDate());
			cashReceiptMap.put("fromDate", coinsurance.getStartDate());
			cashReceiptMap.put("toDate", coinsurance.getEndDate());
			String agentName = (coinsurance.getAgentName() != null && !coinsurance.getAgentName().isEmpty()) ? coinsurance.getAgentName() : " - ";
			cashReceiptMap.put("agent", agentName);
			cashReceiptMap.put("insuredPerson", coinsurance.getCustomerName());
			cashReceiptMap.put("customerAddress", coinsurance.getCustomerAddress());

			JasperPrint cashReceiptPrint = null;

			jpList.add(cashReceiptPrint);

			if (isCheque) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				AbstractProcessor processor = new DefaultProcessor();
				paramMap.put("kyats", processor.getName(coinsurance.getPremium()));
				paramMap.put("insuredName", coinsurance.getCustomerName());
				paramMap.put("premium", coinsurance.getPremium() - org.ace.insurance.common.Utils.getPercentOf(coinsurance.getCommissionPercent(), coinsurance.getPremium()));
				paramMap.put("invoiceNo", coinsurance.getInvoiceNo());
				paramMap.put("chequeNo", coinsurance.getChequeNo());
				paramMap.put("insuranceType", coinsurance.getInsuranceType().getLabel());
				paramMap.put("date", new Date());
				// Need to populate data

				paramMap.put("GLCode", sLCredit.substring(0, 3) + "000");
				paramMap.put("SLCode", sLCredit);

				paramMap.put("GLCodeDebit", sLDebit.substring(0, 3) + "000");
				paramMap.put("SLCodeDebit", sLDebit);
				paramMap.put("debitLetter", debitLetter);
				InputStream coverIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/coinsurance/coinsuredCreditDebit.jrxml");
				JasperReport coverJR = JasperCompileManager.compileReport(coverIS);
				JasperPrint creditDebitPrint = JasperFillManager.fillReport(coverJR, paramMap, new JREmptyDataSource());
				jpList.add(creditDebitPrint);
			}

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// For LifeDeathClaim acceptance Letter

	public static void generateLifeDeathClaimAcceptanceLetter(LifeClaim lifeClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("customerAddress", lifeClaim.getClaimInsuredPerson().getFullResidentAddress());
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			String agentName = (lifeClaim.getLifePolicy().getAgent() == null) ? null : lifeClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("claimNo", lifeClaim.getClaimRequestId());
			paramMap.put("currentDate", claimAcceptedInfo.getInformDate());
			paramMap.put("liabilitiesAmount", claimAcceptedInfo.getClaimAmount());
			paramMap.put("loanAmount", lifeClaim.getTotalLoanAmount());
			paramMap.put("loanInterest", lifeClaim.getTotalLoanInterest());
			paramMap.put("renewalAmount", lifeClaim.getTotalRenewelAmount());
			paramMap.put("renewalInterest", lifeClaim.getTotalRenewelInterest());
			// paramMap.put("totalAmount", lifeClaim.getTotalNetClaimAmount());
			paramMap.put("serviceCharges", claimAcceptedInfo.getServicesCharges());
			paramMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
			paramMap.put("publicLife", isPublicLife(lifeClaim));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifeClaimInformAcceptanceLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeClaimAcceptanceLetter", e);
		}
	}

	private static boolean isPublicLife(LifeClaim lifeClaim) {
		if (lifeClaim.getLifePolicy().getPolicyInsuredPersonList().size() == 1) {
			return true;
		}
		return false;
	}

	// For life Death Claim Reject Letter

	public static void generateLifeDeathClaimRejectLetter(LifeClaim lifeClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("currentDate", claimAcceptedInfo.getInformDate());
			if (lifeClaim.getClaimInsuredPerson() != null) {
				paramMap.put("nrc", lifeClaim.getClaimInsuredPerson().getIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/LifeClaimInformRejectLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeDeathClaimRejectLetter", e);
		}
	}

	// For life Disability Claim Acceptance letter

	public static void generateLifeDisabilityClaimAcceptanceLetter(LifeDisabilityClaim disabilityClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", disabilityClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("customerAddress", disabilityClaim.getClaimInsuredPerson().getFullResidentAddress());
			paramMap.put("policyNo", disabilityClaim.getLifePolicy().getPolicyNo());
			String agentName = (disabilityClaim.getLifePolicy().getAgent() == null) ? null : disabilityClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("claimNo", disabilityClaim.getClaimRequestId());
			paramMap.put("currentDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("liabilitiesAmount", claimAcceptedInfo.getClaimAmount());
			paramMap.put("loanAmount", disabilityClaim.getTotalLoanAmount());
			paramMap.put("loanInterest", disabilityClaim.getTotalLoanInterest());
			paramMap.put("renewalAmount", disabilityClaim.getTotalRenewelAmount());
			paramMap.put("renewalInterest", disabilityClaim.getTotalRenewelInterest());
			// paramMap.put("totalAmount", lifeClaim.getTotalNetClaimAmount());
			paramMap.put("serviceCharges", claimAcceptedInfo.getServicesCharges());
			paramMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
			paramMap.put("waitingPeriod", disabilityClaim.getWaitingPeriod());
			// paramMap.put("disabilityClaimType",disabilityClaim.getDisabilityClaimType());
			paramMap.put("publicLife", isPublicLife(disabilityClaim));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifeDisabilityClaimInformAcceptanceLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeDisabilityClaimAcceptanceLetter", e);
		}
	}

	// For Life Disability Claim Reject Letter

	public static void generateLifeDisabilityClaimRejectLetter(LifeDisabilityClaim disabilityClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", disabilityClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("currentDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			if (disabilityClaim.getClaimInsuredPerson() != null) {
				paramMap.put("nrc", disabilityClaim.getClaimInsuredPerson().getIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("policyNo", disabilityClaim.getLifePolicy().getPolicyNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/LifeDisabilityClaimInformRejectLetter.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeDisabilityClaimRejectLetter", e);
		}
	}

	// For LifeDisabilityClaim Cash Receipt

	public static void generateLifeDisabilityClaimCashReceipt(LifeClaim lifeClaim, PaymentDTO payment, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();

			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paramMap.put("bankAccountNo", payment.getBankAccountNo());
				paramMap.put("bank", payment.getBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
				paramMap.put("receiptType", "Cheque Payment");
				paramMap.put("receiptName", "Payment No.");
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				paramMap.put("receiptType", "Cash Payment");
				paramMap.put("receiptName", "Payment No.");
			}
			paramMap.put("claimNo", lifeClaim.getClaimRequestId());
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("currentDate", payment.getConfirmDate());
			String agentName = (lifeClaim.getLifePolicy().getAgent() == null) ? null : lifeClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("insuredPerson", lifeClaim.getLifePolicy().getCustomerName());
			paramMap.put("customerAddress", lifeClaim.getLifePolicy().getCustomerAddress());
			paramMap.put("liabilitiesAmount", payment.getClaimAmount());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("totalAmount", payment.getTotalClaimAmount());
			paramMap.put("loanAmount", lifeClaim.getClaimInsuredPerson().getLoanAmount());
			paramMap.put("loanInterest", payment.getLoanInterest());
			paramMap.put("renewelAmount", lifeClaim.getClaimInsuredPerson().getRenewelAmount());
			paramMap.put("renewelInterest", payment.getRenewalInterest());
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifeDisabilityClaimCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate FireCashReceipt", e);
		}
	}

	// prepare LifeDeathClaim Cash Receipt
	public static void generateLifeDeathClaimCashReceipt(LifeClaim lifeClaim, PaymentDTO payment, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();

			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paramMap.put("bankAccountNo", payment.getBankAccountNo());
				paramMap.put("bank", payment.getBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
				paramMap.put("receiptType", "Cheque Payment");
				paramMap.put("receiptName", "Payment No.");
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				paramMap.put("receiptType", "Cash Payment");
				paramMap.put("receiptName", "Payment No.");
			}
			paramMap.put("claimNo", lifeClaim.getClaimRequestId());
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("currentDate", payment.getConfirmDate());
			String agentName = (lifeClaim.getLifePolicy().getAgent() == null) ? null : lifeClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("insuredPerson", lifeClaim.getLifePolicy().getCustomerName());
			paramMap.put("customerAddress", lifeClaim.getLifePolicy().getCustomerAddress());
			paramMap.put("liabilitiesAmount", payment.getClaimAmount());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("totalAmount", payment.getTotalClaimAmount());
			paramMap.put("loanAmount", lifeClaim.getClaimInsuredPerson().getLoanAmount());
			paramMap.put("loanInterest", payment.getLoanInterest());
			paramMap.put("renewelAmount", lifeClaim.getClaimInsuredPerson().getRenewelAmount());
			paramMap.put("renewelInterest", payment.getRenewalInterest());
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifeDeathClaimCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate FireCashReceipt", e);
		}
	}

	// prepare DischareCertificate

	public static void generateLifeDisabilityClaimDischargeCertificate(LifeClaimDischargeFormDTO discharge, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();

			String presentDate = Utils.formattedDate(discharge.getPresentDate());
			String liabilitiesAmount = Utils.formattedCurrency(discharge.getClaimAmount());
			paramMap.put("policyNo", discharge.getPolicyNo());
			paramMap.put("customerName", discharge.getCustomerName());
			paramMap.put("liabilitiesAmount", liabilitiesAmount);
			paramMap.put("sumInsured", discharge.getSumInsured());
			paramMap.put("presentDate", presentDate);

			paramMap.put("beneficiaryName", discharge.getBeneficiaryName());
			// paramMap.put("disabilityDate",);
			paramMap.put("commencementDate", discharge.getCommenmanceDate());
			paramMap.put("premium", discharge.getRenewelAmount());
			paramMap.put("renewelInterest", discharge.getRenewelInterest());
			paramMap.put("loanAmount", discharge.getLoanAmount());
			paramMap.put("loanInterest", discharge.getLoanInterest());
			paramMap.put("serviceCharges", discharge.getServiceCharges());
			paramMap.put("netAmount", discharge.getNetClaimAmount());
			paramMap.put("witnessName", " ");
			paramMap.put("witnessAddress", " ");
			paramMap.put("nrc", discharge.getIdNo());
			paramMap.put("fatherName", discharge.getFatherName());
			paramMap.put("customerAddress", discharge.getAddress());

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/dischargeCertificate.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate DischargeCertificate", e);
		}
	}

	// prepare LifeDeathClaim Discharge Certificate

	public static void generateLifeClaimDischargeCertificate(LifeClaimDischargeFormDTO discharge, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String presentDate = Utils.formattedDate(discharge.getPresentDate());
			String liabilitiesAmount = Utils.formattedCurrency(discharge.getClaimAmount());
			paramMap.put("policyNo", discharge.getPolicyNo());
			paramMap.put("customerName", discharge.getInsuredPersonName());
			paramMap.put("liabilitiesAmount", liabilitiesAmount);
			paramMap.put("beneficiaryName", discharge.getBeneficiaryName());
			paramMap.put("sumInsured", discharge.getSumInsured());
			paramMap.put("commencementDate", discharge.getCommenmanceDate());
			paramMap.put("premium", discharge.getRenewelAmount());
			paramMap.put("renewelInterest", discharge.getRenewelInterest());
			paramMap.put("loanAmount", discharge.getLoanAmount());
			paramMap.put("loanInterest", discharge.getLoanInterest());
			paramMap.put("netAmount", discharge.getNetClaimAmount());
			paramMap.put("nrc", discharge.getIdNo());
			paramMap.put("fatherName", discharge.getFatherName());
			paramMap.put("customerAddress", discharge.getAddress());
			paramMap.put("presentDate", presentDate);
			paramMap.put("serviceCharges", discharge.getServiceCharges());
			paramMap.put("maturityDate", discharge.getMaturityDate());
			paramMap.put("witnessName", " ");
			paramMap.put("witnessAddress", " ");

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifeClaimDischargeCertificate.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate DischargeCertificate", e);
		}
	}

	/* SnakeBite Policy Cash Receipt */
	public static void generateSnakeBiteCashReceipt(List<SnakeBitePolicy> snakeBitePolicyList, Payment payment, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			double commissionPercentage = 0.0;
			double totalSumInsured = 0.0;
			double totalPremium = 0.0;
			double agentCommission = 0.0;
			double netPremium = 0.0;
			if (snakeBitePolicyList.size() > 0) {
				for (SnakeBitePolicy snakeBitePolicy : snakeBitePolicyList) {
					totalSumInsured += snakeBitePolicy.getSumInsured();
					totalPremium += snakeBitePolicy.getPremium();
				}
				paramMap.put("sumInsured", totalSumInsured);
				paramMap.put("premium", totalPremium);
				paramMap.put("paymentType", snakeBitePolicyList.get(0).getPaymentType().getName());
				paramMap.put("cashReceiptNo", snakeBitePolicyList.get(0).getReferenceNo());
				paramMap.put("fromDate", snakeBitePolicyList.get(0).getPolicyStartDate());
				paramMap.put("toDate", snakeBitePolicyList.get(0).getPolicyEndDate());
				if (snakeBitePolicyList.get(0).getAgent() != null) {
					commissionPercentage = snakeBitePolicyList.get(0).getProduct().getFirstCommission();
					agentCommission = totalPremium * commissionPercentage / 100;
					paramMap.put("agentCommission", agentCommission);
					paramMap.put("agent", snakeBitePolicyList.get(0).getAgent().getFullName());
				} else {
					paramMap.put("agent", "");
					paramMap.put("agentCommission", 0.0);
				}
				paramMap.put("netPremium", payment.getBasicPremium());
				paramMap.put("totalAmount", payment.getBasicPremium());
				paramMap.put("policyType", snakeBitePolicyList.get(0).getProduct().getName());

				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
					paramMap.put("chequeNo", payment.getChequeNo());
					paramMap.put("bank", payment.getBank().getName());
					paramMap.put("chequePayment", Boolean.TRUE);
					paramMap.put("receiptType", "Temporary Receipt");
					paramMap.put("receiptName", "Receipt No.");

				} else if (payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
					paramMap.put("chequePayment", Boolean.TRUE);
					paramMap.put("bank", payment.getAccountBank().getName());
				} else {
					paramMap.put("chequePayment", Boolean.FALSE);
					paramMap.put("receiptName", "Cash Receipt No.");
					paramMap.put("receiptType", "Cash Receipt");
				}
				paramMap.put("bookNo", payment.getReferenceNo());
				paramMap.put("discountAmount", payment.getDiscountAmount());
				paramMap.put("stempFees", payment.getStampFees());
				paramMap.put("serviceCharges", payment.getServicesCharges());
				paramMap.put("confirmDate", new Date());
				paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			}
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/snakeBiteCashReceipt.jrxml");
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate SnakeBite Policy", e);
		}
	}

	// genetateGroupLifeRenewalNotificationLetter
	public static void genetateGroupLifeRenewalNotificationLetter(List<LifePolicy> lifePolicyList, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			for (LifePolicy lifePolicy : lifePolicyList) {
				paramMap.put("startDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getActivedPolicyStartDate()));
				paramMap.put("endDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getActivedPolicyEndDate()));
				paramMap.put("policyNo", lifePolicy.getPolicyNo());
				paramMap.put("salePersonName", lifePolicy.getSalePersonName());
				paramMap.put("customerName", lifePolicy.getCustomerName());
				paramMap.put("customerAddress", lifePolicy.getCustomerAddress());
				paramMap.put("phoneNo", lifePolicy.getCustomerPhoneNo());
				paramMap.put("totalInsuredPerson", lifePolicy.getInsuredPersonInfo().size());
				paramMap.put("periodYears", lifePolicy.getInsuredPersonInfo().get(0).getPeriodYears());
				paramMap.put("date", Utils.formattedDate(new Date()));
				paramMap.put("totalSumInsured", lifePolicy.getTotalSumInsured());
				paramMap.put("totalPremium", lifePolicy.getTotalPremium());
				paramMap.put("agentWithLicenceNo", lifePolicy.agentNameWithLiscenceNo());
				paramMap.put("compLogo", ApplicationSetting.getNewCompLogo());
				paramMap.put("compAddress", ApplicationSetting.getNewCompAddress());
				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/GroupLifeRenewalNotificationLetter.jrxml");
				JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
				JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramMap, new JREmptyDataSource());
				jasperPrintList.add(jasperPrint);
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			FileHandler.forceMakeDirectory(dirPath);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(dirPath + fileName));
			exporter.exportReport();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate genetateFireRenewalNotificationLetter", e);
		}
	}

	// for generate TLFVoucher
	public static void generateTLFVoucher(List<TLFVoucherDTO> cashReceiptDTOList, String fullTemplateFilePath, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(cashReceiptDTOList));
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullTemplateFilePath);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// for generate ClaimVoucher
	public static void generateClaimVoucher(List<ClaimVoucherDTO> claimVoucherDTOList, String fullTemplateFilePath, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(claimVoucherDTOList));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullTemplateFilePath);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	/*
	 * for Life Surrender Proposal Inform
	 */
	public static void generateLifeSurrenderInformForm(LifeSurrenderProposal surrenderProposal, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		LifePolicy lifePolicy = surrenderProposal.getLifePolicy();
		double loanAmount = 0.0;
		double loanInterest = 0.0;
		double renewalAmount = 0.0;
		double renewalInterest = 0.0;
		try {

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("currentDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("policyNo", surrenderProposal.getPolicyNo());
			paramMap.put("customerName", lifePolicy.getCustomerName());
			paramMap.put("customerAddress", lifePolicy.getCustomerAddress());
			paramMap.put("surrenderAmount", surrenderProposal.getSurrenderAmount());
			paramMap.put("proposalNo", surrenderProposal.getProposalNo());
			paramMap.put("serviceCharges", claimAcceptedInfo.getServicesCharges());
			paramMap.put("loanAmount", loanAmount);
			paramMap.put("loanInterest", loanInterest);
			paramMap.put("renewalAmount", renewalAmount);
			paramMap.put("renewalInterest", renewalInterest);
			paramMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
			InputStream inputStream;
			if (surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/lifeSurrenderInform.jrxml");
			} else {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/shorttermlifeSurrenderInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report lifeSurrenderInform", e);
		}
	}

	public static void generateLifeSurrenderRejectLetter(LifeSurrenderProposal surrenderProposal, ClaimAcceptedInfo claimAcceptedInfo, String rejectDirPath,
			String rejectFileName) {
		LifePolicy lifePolicy = surrenderProposal.getLifePolicy();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", surrenderProposal.getProposalNo());
			paramMap.put("informedDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("customerName", lifePolicy.getCustomerName());
			paramMap.put("nrc", lifePolicy.getCustomerNRC());
			InputStream inputStream;
			if (surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/lifeSurrenderInformRejectLetter.jrxml");
			} else {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/shorttermlifePaidUpInformRejectLetter.jrxml");

			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(rejectDirPath);
			String outputFile = rejectDirPath + rejectFileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report Life Surrender Reject Letter", e);
		}
	}

	public static void generateLifeSurrenderCashReceipt(LifeSurrenderProposal surrenderProposal, PaymentDTO payment, String dirPath, String fileName) {
		LifePolicy lifePolicy = surrenderProposal.getLifePolicy();
		double suminsured = lifePolicy.getSumInsured();
		Date startDate = lifePolicy.getActivedPolicyStartDate();
		Date endDate = lifePolicy.getActivedPolicyEndDate();
		int paidMonth = org.ace.insurance.common.Utils.monthsBetween(startDate, endDate, false, true);
		String proposalNo = surrenderProposal.getProposalNo();
		String customerName = lifePolicy.getCustomerName();
		String customerAddress = lifePolicy.getCustomerAddress();
		String policyNo = surrenderProposal.getPolicyNo();
		double surrenderAmount = surrenderProposal.getSurrenderAmount();
		double lifePremium = surrenderProposal.getLifePremium();
		double serviceCharges = payment.getServicesCharges();
		double loan = 0.0;
		double loanInterest = 0.0;
		double teeMyaeInterest = 0.0;
		double totalAmount = payment.getTotalClaimAmount() - (loan + loanInterest + teeMyaeInterest + lifePremium);

		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String totalAmountInMMText = convertor.getName(totalAmount);
		String totalAmountInMM = NumberToNumberConvertor.getMyanmarNumber(totalAmount, true);
		String surrenderAmountInMM = NumberToNumberConvertor.getMyanmarNumber(surrenderAmount, true);
		String loanInMM = NumberToNumberConvertor.getMyanmarNumber(loan, true);
		String loanInterestInMM = NumberToNumberConvertor.getMyanmarNumber(loanInterest, true);
		String lifePremiumInMM = NumberToNumberConvertor.getMyanmarNumber(lifePremium, true);
		String serviceChargesInMM = NumberToNumberConvertor.getMyanmarNumber(serviceCharges, true);
		String teeMyaeInterestInMM = NumberToNumberConvertor.getMyanmarNumber(teeMyaeInterest, true);

		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paramMap.put("chequeNo", payment.getChequeNo());
				paramMap.put("bank", payment.getBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
				paramMap.put("receiptType", "Cheque Payment");
				paramMap.put("receiptName", "Payment No.");
			} else if (payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
				paramMap.put("bank", payment.getAccountBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				paramMap.put("receiptType", "Cash Payment");
				paramMap.put("receiptName", "Payment No.");
			}
			paramMap.put("proposalNo", proposalNo);
			paramMap.put("policyNo", policyNo);
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("surrenderAmount", surrenderAmount);
			paramMap.put("loan", loan);
			paramMap.put("loanInterest", loanInterest);
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("lifePremium", lifePremium);
			paramMap.put("teeMyaeinterest", teeMyaeInterest);
			paramMap.put("suminsured", suminsured);
			paramMap.put("paidPremium", surrenderProposal.getReceivedPremium());
			paramMap.put("totalAmount", totalAmount);
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("insuredPerson", customerName);
			paramMap.put("claimType", "Surrender Claim");
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			Currency currency = new Currency();
			currency.setCurrencyCode(CurrencyUtils.getCurrencyCode(null));
			paramMap.put("currencyFormat", CurrencyUtils.getCurrencyFormat(currency));
			paramMap.put("currencyCode", currency.getCurrencyCode());
			paramMap.put("currencySymbol", "KYT");
			InputStream inputStream;
			if (surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/lifeSurrenderClaimCashReceipt.jrxml");
			} else {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/shorttermlifeSurrenderClaimCashReceipt.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			Map map = new HashMap();
			map.put("policyNo", policyNo);
			map.put("insuredPerson", customerName);
			map.put("totalAmountInMMText", totalAmountInMMText);
			map.put("totalAmountInMM", totalAmountInMM);
			map.put("confirmDate", payment.getConfirmDate());
			map.put("nrcNo", lifePolicy.getCustomerNRC());
			map.put("loanInMM", loanInMM);
			map.put("loanInterestInMM", loanInterestInMM);
			map.put("lifePremiumInMM", lifePremiumInMM);
			map.put("teeMyaeInterestInMM", teeMyaeInterestInMM);
			map.put("serviceChargesInMM", serviceChargesInMM);
			map.put("surrenderAmountInMM", surrenderAmountInMM);
			InputStream stream;
			if (surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {
				stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/lifeSurrenderCashReceiptForm.jrxml");
			} else {
				stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/shorttermlifeSurrenderCashReceiptForm.jrxml");
			}
			JasperReport report = JasperCompileManager.compileReport(stream);
			JasperPrint print = JasperFillManager.fillReport(report, map, new JREmptyDataSource());
			jasperPrintList.add(print);
			jasperListPDFExport(jasperPrintList, dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeSurrenderCashReceipt", e);
		}
	}

	public static void generateLifeSurrenderIssue(LifeSurrenderProposal proposal, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("empty", " ");
			paramMap.put("policyNo", proposal.getPolicyNo());
			paramMap.put("todayDate", Utils.formattedDate(new Date()));
			paramMap.put("policyDate", Utils.formattedDate(proposal.getLifePolicy().getActivedPolicyStartDate()));
			paramMap.put("insuredPersonName", proposal.getLifePolicy().getInsuredPersonInfo().get(0).getFullName());
			paramMap.put("timesUpOrDeadDate", " ");
			paramMap.put("requestedPerson", proposal.getLifePolicy().getInsuredPersonInfo().get(0).getFullName());
			paramMap.put("sumInsured", org.ace.insurance.common.Utils.getCurrencyFormatString(proposal.getLifePolicy().getSumInsured()));
			paramMap.put("premium", org.ace.insurance.common.Utils.getCurrencyFormatString(proposal.getLifePremium()));
			// currently only premium , add the new minus values later
			double netAmountToMinus = proposal.getLifePremium();
			paramMap.put("netAmountToMinus", org.ace.insurance.common.Utils.getCurrencyFormatString(netAmountToMinus));
			paramMap.put("netAmountToPay", org.ace.insurance.common.Utils.getCurrencyFormatString(proposal.getSurrenderAmount()));
			// final amount = netAmountToPay - netAmountToMinus
			double finalAmount = proposal.getSurrenderAmount() - proposal.getLifePremium();
			paramMap.put("finalAmount", org.ace.insurance.common.Utils.getCurrencyFormatString(finalAmount));
			paramMap.put("insuredPersonNRC", proposal.getLifePolicy().getInsuredPersonInfo().get(0).getIdNo());
			paramMap.put("reqAmount", org.ace.insurance.common.Utils.getCurrencyFormatString(proposal.getSurrenderAmount()));
			paramMap.put("paymentDate", Utils.formattedDate(new Date()));
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			paramMap.put("finalAmountText", convertor.getName(finalAmount));
			paramMap.put("finalAmountNumeric", org.ace.insurance.common.Utils.getCurrencyFormatString(finalAmount));
			InputStream stream;
			if (proposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {

				stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/lifeSurrenderIssueForm.jrxml");
			} else {
				stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/shorttermlifeSurrenderIssueForm.jrxml");
			}
			JasperReport report = JasperCompileManager.compileReport(stream);
			JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
			jasperPrintList.add(print);
			jasperListPDFExport(jasperPrintList, dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeSurrenderIssue", e);
		}
	}

	/* Life PaidUp Proposal */
	public static void generateLifePaidUpInformForm(LifePaidUpProposal lifePaidUpProposal, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		LifePolicy lifePolicy = lifePaidUpProposal.getLifePolicy();
		InputStream paidUpSactionIS;

		try {
			Map sanctionParamMap = new HashMap();
			sanctionParamMap.put("todayDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			sanctionParamMap.put("customerName", lifePolicy.getCustomerName());
			sanctionParamMap.put("policyNo", lifePaidUpProposal.getPolicyNo());
			sanctionParamMap.put("policyStartDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			sanctionParamMap.put("policyPeriod", lifePaidUpProposal.getPolicyPeriod());
			sanctionParamMap.put("sumInsured", lifePaidUpProposal.getSumInsured());
			sanctionParamMap.put("paymentYear", lifePaidUpProposal.getPaymentYear());
			sanctionParamMap.put("paiedPremium", lifePaidUpProposal.getReceivedPremium());
			sanctionParamMap.put("paidUpAmount", lifePaidUpProposal.getRealPaidUpAmount());

			if (lifePaidUpProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {
				paidUpSactionIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/lifePaidUpSanction.jrxml");

			} else {
				paidUpSactionIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/shorttermlifePaidUpSanction.jrxml");
			}
			JasperReport paidUpSactionJR = JasperCompileManager.compileReport(paidUpSactionIS);
			JasperPrint paidUpSactionJP = JasperFillManager.fillReport(paidUpSactionJR, sanctionParamMap, new JREmptyDataSource());
			Map calculationParamMap = new HashMap();
			calculationParamMap.put("policyNo", lifePaidUpProposal.getPolicyNo());
			calculationParamMap.put("policyPeriod", lifePaidUpProposal.getPolicyPeriod());
			calculationParamMap.put("sumInsured", lifePaidUpProposal.getSumInsured());
			calculationParamMap.put("paymentYear", lifePaidUpProposal.getPaymentYear());
			calculationParamMap.put("paidUpAmount", lifePaidUpProposal.getPaidUpAmount());
			calculationParamMap.put("paymentType", lifePolicy.getPaymentType().getMonth());
			Date startDate = lifePolicy.getActivedPolicyStartDate();
			Date endDate = lifePolicy.getActivedPolicyEndDate();
			int paidMonth = org.ace.insurance.common.Utils.monthsBetween(startDate, endDate, false, true);
			int reqPaymentTerm = paidMonth % 12;
			if (reqPaymentTerm >= 6) {
				reqPaymentTerm = 12 - reqPaymentTerm;
			}
			calculationParamMap.put("reqPaymentTerm", reqPaymentTerm);
			calculationParamMap.put("reqAmount", lifePaidUpProposal.getReqAmount());
			InputStream calculationIS;
			if (lifePaidUpProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {
				calculationIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/lifePaidUpCalculation.jrxml");
			} else {
				calculationIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/shorttermlifePaidUpCalculation.jrxml");
			}
			JasperReport calculationJR = JasperCompileManager.compileReport(calculationIS);
			JasperPrint calculationJP = JasperFillManager.fillReport(calculationJR, calculationParamMap, new JREmptyDataSource());
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("todayDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("policyNo", lifePaidUpProposal.getPolicyNo());
			paramMap.put("customerName", lifePolicy.getCustomerName());
			paramMap.put("paidUpAmount", lifePaidUpProposal.getPaidUpAmount());
			InputStream inputStream;
			if (lifePaidUpProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {

				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/lifePaidUpInform.jrxml");
			} else {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/shorttermlifePaidUpInform.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			List jpList = new ArrayList();
			jpList.add(paidUpSactionJP);
			jpList.add(calculationJP);
			jpList.add(jprint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report lifePaidUpInform", e);
		}
	}

	public static void generateLifePaidUpRejectLetter(LifePaidUpProposal lifePaidUpProposal, ClaimAcceptedInfo claimAcceptedInfo, String rejectDirPath, String rejectFileName) {
		LifePolicy lifePolicy = lifePaidUpProposal.getLifePolicy();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifePaidUpProposal.getProposalNo());
			paramMap.put("informedDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("customerName", lifePolicy.getCustomerName());
			paramMap.put("nrc", lifePolicy.getCustomerNRC());
			InputStream inputStream;
			if (lifePaidUpProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct().getId().equals(KeyFactorChecker.getPublicLifeID())) {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/surrender/lifeSurrenderInformRejectLetter.jrxml");
			} else {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/paidUp/shorttermlifePaidUpInformRejectLetter.jrxml");
			}
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(rejectDirPath);
			String outputFile = rejectDirPath + rejectFileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report Life PaidUp Reject Letter", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void generateGroupFarmerCashReceipt(GroupFarmerProposal groupFarmerProposal, PaymentDTO payment, String dirPath, String fileName) {
		String registrationNo = "";
		String policyType = null;
		String customerName = "";
		String customerAddress = "";
		String proposalNo = "";
		String paymentTypeName = "";
		double sumInsured = 0.0;
		double ncbAmount = 0.0;
		String productTitle = "";
		Date fromDate = null;
		Date toDate = null;
		String agentWithLiscenseNo = "";

		proposalNo = groupFarmerProposal.getProposalNo();
		paymentTypeName = groupFarmerProposal.getPaymentType().getName();
		sumInsured = groupFarmerProposal.getTotalSI();
		fromDate = groupFarmerProposal.getSubmittedDate();
		toDate = groupFarmerProposal.getEndDate();
		agentWithLiscenseNo = groupFarmerProposal.getAgent() == null ? ""
				: groupFarmerProposal.getAgent().getFullName() + " (" + groupFarmerProposal.getAgent().getLiscenseNo() + ")";
		try {
			Map paramMap = new HashMap();
			paramMap.put("totalInsuredPerson", groupFarmerProposal.getNoOfInsuredPerson());
			paramMap.put("customerName", groupFarmerProposal.getOrganization().getName());
			paramMap.put("proposalNo", proposalNo);
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("sumInsured", sumInsured);
			paramMap.put("premium", payment.getBasicPremium());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("netPremium", payment.getNetPremium());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("totalAmount", payment.getTotalAmount());
			paramMap.put("paymentType", paymentTypeName);
			paramMap.put("registrationNo", registrationNo);
			paramMap.put("policyType", PolicyReferenceType.GROUP_FARMER_PROPOSAL.getLabel());
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("agent", agentWithLiscenseNo);
			paramMap.put("currencyCode", CurrencyUtils.getCurrencyCode(null));
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			paramMap.put("productTitle", "FARMER INSURNACE");
			paramMap.put("policyBranch", groupFarmerProposal.getBranch().getName());

			if (payment != null && payment.getPaymentChannel() != null) {
				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)
						|| payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {

					if (payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
						paramMap.put("bank", payment.getAccountBank().getName());
						paramMap.put("chequePayment", Boolean.FALSE);
						paramMap.put("poNo", Boolean.TRUE);
					} else {
						paramMap.put("hasChequeNo", Boolean.TRUE);
						paramMap.put("chequeNo", payment.getChequeNo() == null ? payment.getPoNo() : payment.getChequeNo());
						paramMap.put("bank", payment.getBank().getName());
						paramMap.put("chequePayment", Boolean.TRUE);
						paramMap.put("poNo", Boolean.FALSE);
						if (payment.getChequeNo() == null) {
							paramMap.put("poNo", Boolean.TRUE);
							paramMap.put("chequePayment", Boolean.FALSE);
						}
					}
					paramMap.put("receiptName", "Receipt No");
				} else {
					paramMap.put("chequePayment", Boolean.FALSE);
					paramMap.put("receiptName", "Cash Receipt No");
				}
				paramMap.put("receiptType", " -- ");
			}
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.GROUPFARMER_CASH_RECEIPT, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Group Farmer CashReceipt", e);
		}
	}

	public static void generateGroupFarmerPolicy(List<LifePolicy> lifePolicyList, String dirPath, String fileName) {
		try {
			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			for (LifePolicy lifePolicy : lifePolicyList) {
				for (PolicyInsuredPerson insuredPerson : lifePolicy.getPolicyInsuredPersonList()) {
					boolean isFarmer = KeyFactorChecker.isFarmer(insuredPerson.getProduct());
					paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
					paramIssueDetails.put("premium", lifePolicy.getTotalPremium());
					paramIssueDetails.put("insuredPersonName", insuredPerson.getFullName());
					paramIssueDetails.put("nrc", insuredPerson.getIdNo());
					paramIssueDetails.put("fatherName", insuredPerson.getFatherName());
					paramIssueDetails.put("address", insuredPerson.getResidentAddress().getFullResidentAddress());
					paramIssueDetails.put("phone", "");
					paramIssueDetails.put("agentName", lifePolicy.getAgent() != null ? lifePolicy.getAgent().getFullName() : "");
					paramIssueDetails.put("agentLicenseNo", lifePolicy.getAgent() != null ? lifePolicy.getAgent().getLiscenseNo() : "");
					paramIssueDetails.put("endDate", lifePolicy.getActivedPolicyEndDate());
					paramIssueDetails.put("startDate", lifePolicy.getActivedPolicyStartDate());
					paramIssueDetails.put("sumInsured", insuredPerson.getSumInsured());
					List<PolicyInsuredPersonBeneficiaries> benfList = insuredPerson.getPolicyInsuredPersonBeneficiariesList();
					paramIssueDetails.put("listDataSource", new JRBeanCollectionDataSource(benfList));
					InputStream inputStream = null;
					if (isFarmer) {
						inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/Farmer/FarmerPolicyIssue.jrxml");
					}
					JasperReport jreport = JasperCompileManager.compileReport(inputStream);
					JasperPrint jprint = JasperFillManager.fillReport(jreport, paramIssueDetails, new JREmptyDataSource());
					if (jprint.getPages().size() > 1) {
						jprint.removePage(1);
					}
					jpList.add(jprint);

				}
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document public life policy issue", e);
		}
	}

	/* Student Life */
	public static void generateStudentLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("customerName", lifeProposal.getCustomer().getFullName());
			paramMap.put("insuredPersonName", insuredPerson.getFullName());
			paramMap.put("age", lifeProposal.getCustomer().getAgeForNextYear());
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("insuredPersonAge", insuredPerson.getAge());
			paramMap.put("sumInsured", Utils.formattedCurrency(lifeProposal.getSumInsured()));
			paramMap.put("period", insuredPerson.getPeriodYears());
			paramMap.put("premiumTerm", insuredPerson.getPremiumTerm());
			paramMap.put("medicalInfo", lifeProposal.getCustomerClsOfHealth() != null ? lifeProposal.getCustomerClsOfHealth().getLabel() : "");
			double totalTermPremim = lifeProposal.getTotalTermPremium();
			double kyat = Math.floor(totalTermPremim);
			double pyar = (totalTermPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###.00");
			String termPremiumKyat = formatter1.format(kyat);
			DecimalFormat formatter2 = new DecimalFormat("00");
			String termPremiumPyar = formatter2.format(pyar);
			double totalPremium = lifeProposal.getTotalPremium();
			paramMap.put("totalPremium", formatter1.format(totalTermPremim));
			paramMap.put("premiumKyat", termPremiumKyat);
			paramMap.put("premiumPyar", termPremiumPyar);
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_SANCTION_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Student Life Sanction", e);
		}
	}

	public static void generateStudentLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("childName", insuredPerson.getFullName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYear", insuredPerson.getPeriodYears());
			paramMap.put("premiumTerm", insuredPerson.getPremiumTerm());
			paramMap.put("sumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
			paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
			paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermAmount()));
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_ACCEPTED_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Student Life AcceptanceLetter", e);
		}
	}

	public static void generateStudentLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (lifeProposal.getCustomer() != null) {
				if (lifeProposal.getCustomer().getFullIdNo() != null) {
					paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
				} else {
					paramMap.put("nrc", "");
				}
			}
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("todayDate", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Student Life RejectLetter", e);
		}
	}

	public static void generateStudentLifePolicyJasperPrint(LifePolicy lifePolicy, String dirPath, String fileName) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		try {
			Map<String, Object> coverMap = new HashMap<String, Object>();
			double payAmount = 0.0;
			Date insuredPersonDateOfBirth;
			Date firstBeneDate;
			Date secondBeneDate;
			Date thirdBeneDate;
			Date fourthBeneDate;
			String phone = null;
			String email = null;
			DecimalFormat curFormat = new DecimalFormat("#,###");
			PolicyInsuredPerson policyInsuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
			coverMap.put("policyNo", lifePolicy.getPolicyNo());
			coverMap.put("customerName", lifePolicy.getCustomerName());
			coverMap.put("insuredPersonName", policyInsuredPerson.getFullName());
			coverMap.put("totalsumInsured", curFormat.format(lifePolicy.getSumInsured()));
			coverMap.put("nextPremiumDate", policyInsuredPerson.getTimeSlotListStr());
			InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_POLICY_COVER_LETTER);
			JasperReport jreport1 = JasperCompileManager.compileReport(inputStream1);
			JasperPrint jprint1 = JasperFillManager.fillReport(jreport1, coverMap, new JREmptyDataSource());
			jpList.add(jprint1);

			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName() + " (" + agent.getLiscenseNo() + ")");
			} else {
				paramIssueDetails.put("agent", "-");
			}

			paramIssueDetails.put("insuredPersonName", policyInsuredPerson.getFullName());
			paramIssueDetails.put("insuredPersonNRC", policyInsuredPerson.getIdNo());
			paramIssueDetails.put("insuredPersonDOB", "(" + policyInsuredPerson.getAge() + " year)" + Utils.formattedDate(policyInsuredPerson.getDateOfBirth()));
			paramIssueDetails.put("InsageForNextYear", policyInsuredPerson.getAge());
			paramIssueDetails.put("insuredPersonAddress",
					policyInsuredPerson.getResidentAddress() != null ? policyInsuredPerson.getResidentAddress().getFullResidentAddress() : "");

			if (Gender.MALE.equals(lifePolicy.getCustomer().getGender())) {
				paramIssueDetails.put("fatherName", lifePolicy.getCustomerName());
				paramIssueDetails.put("motherName", policyInsuredPerson.getParentName());
			} else if (Gender.FEMALE.equals(lifePolicy.getCustomer().getGender())) {
				paramIssueDetails.put("motherName", lifePolicy.getCustomerName());
				paramIssueDetails.put("fatherName", policyInsuredPerson.getParentName());
			}

			paramIssueDetails.put("schoolName", policyInsuredPerson.getSchoolName());
			paramIssueDetails.put("gradeInfo", policyInsuredPerson.getGradeInfoName());

			paramIssueDetails.put("customerName", lifePolicy.getCustomer().getFullName());
			paramIssueDetails.put("customerNRC", lifePolicy.getCustomer().getFullIdNo());
			paramIssueDetails.put("customerDOB", "(" + lifePolicy.getCustomer().getAgeForNextYear() + " year)" + Utils.formattedDate(lifePolicy.getCustomer().getDateOfBirth()));
			paramIssueDetails.put("cusageForNextYear", lifePolicy.getCustomer().getAgeForNextYear());
			paramIssueDetails.put("customerAddress", lifePolicy.getCustomer().getFullAddress());
			paramIssueDetails.put("workAddress", lifePolicy.getCustomer().getFullOfficeAddress());
			if (lifePolicy.getCustomer().getContentInfo() != null) {
				phone = lifePolicy.getCustomer().getContentInfo().getPhoneOrMoblieNo();
				email = lifePolicy.getCustomer().getContentInfo().getEmail();
			}

			paramIssueDetails.put("phone", phone);
			paramIssueDetails.put("email", email != null ? email : "-");
			paramIssueDetails.put("sumInsured", curFormat.format(lifePolicy.getSumInsured()));
			paramIssueDetails.put("periodYears", policyInsuredPerson.getPeriodYears());

			paramIssueDetails.put("totalPremium", lifePolicy.getTotalPremium());
			paramIssueDetails.put("termPremium", lifePolicy.getTotalTermPremium());
			paramIssueDetails.put("paymentType", lifePolicy.getPaymentType().getName());
			paramIssueDetails.put("paymentTypeMonth", lifePolicy.getPaymentType().getMonth());
			paramIssueDetails.put("activedPolicyStartDate", lifePolicy.getActivedPolicyStartDate());
			paramIssueDetails.put("nextPremiumDate", policyInsuredPerson.getTimeSlotListStr());
			paramIssueDetails.put("premiumDueDate", policyInsuredPerson.getLastPaymentDate());

			insuredPersonDateOfBirth = policyInsuredPerson.getDateOfBirth();
			firstBeneDate = DateUtils.addYears(insuredPersonDateOfBirth, 17);
			secondBeneDate = DateUtils.addYears(firstBeneDate, 1);
			thirdBeneDate = DateUtils.addYears(secondBeneDate, 1);
			fourthBeneDate = DateUtils.addYears(thirdBeneDate, 1);

			int payAmountPercentage = 25;
			payAmount = (lifePolicy.getSumInsured() * payAmountPercentage) / 100;

			int year = DateUtils.getYearFromDate(lifePolicy.getActivedPolicyStartDate());
			int month = DateUtils.getMonthFromDate(lifePolicy.getActivedPolicyStartDate()) + 1;
			int day = DateUtils.getDayFromDate(lifePolicy.getActivedPolicyStartDate());

			paramIssueDetails.put("firstBeneDate", firstBeneDate);
			paramIssueDetails.put("secondBeneDate", secondBeneDate);
			paramIssueDetails.put("thirdBeneDate", thirdBeneDate);
			paramIssueDetails.put("fourthBeneDate", fourthBeneDate);
			paramIssueDetails.put("payAmount", curFormat.format(payAmount));
			paramIssueDetails.put("year", year);
			paramIssueDetails.put("month", month);
			paramIssueDetails.put("day", day);
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_POLICY_ISSUE_LETTER);
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint2);

			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document Student life policy issue", e);
		}
	}

}
