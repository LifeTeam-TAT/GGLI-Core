package org.ace.insurance.web.common.document.life;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.web.common.Utils;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.web.ApplicationSetting;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class ShortEndowUnderwritingDOCFactory extends BasicDAO {
	/* Sanction Letter */
	public static void generateShortEndowLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
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
			double totalPremiumByPeriod = lifeProposal.getTotalPremium() * insuredPerson.getPeriodYears();
			double totalPremium = lifeProposal.getTotalPremium();
			paramMap.put("totalPremium", formatter1.format(totalPremiumByPeriod));
			paramMap.put("extraPayment", "");
			/* int month = lifeProposal.getPaymentType().getMonth(); */
			kyat = Math.floor(totalPremium);
			pyar = (totalPremium - kyat) * 100;
			double sixMonthPremium = totalPremium / 2;
			double sixMonthKyat = Math.floor(sixMonthPremium);
			double sixMonthPyar = (sixMonthPremium - sixMonthKyat) * 100;
			double threeMonthPremium = totalPremium / 4;
			double threeMonthKyat = Math.floor(threeMonthPremium);
			double threeMonthPyar = (threeMonthPremium - threeMonthKyat) * 100;
			double oneMonthPremium = totalPremium / 12;
			double oneMonthKyat = Math.floor(oneMonthPremium);
			double oneMonthPyar = (oneMonthPremium - oneMonthKyat) * 100;
			formatter1 = new DecimalFormat("#,###");
			String oneYearPaymentKyat = formatter1.format(kyat);
			String sixYearPaymentKyat = formatter1.format(sixMonthKyat);
			String threeYearPaymentKyat = formatter1.format(threeMonthKyat);
			String oneMonthPaymentKyat = formatter1.format(oneMonthKyat);
			formatter2 = new DecimalFormat("00");
			String oneYearPaymentPyar = formatter2.format(pyar);
			String sixYearPaymentPyar = formatter2.format(sixMonthPyar);
			String threeYearPaymentPyar = formatter2.format(threeMonthPyar);
			String oneMonthPaymentPyar = formatter2.format(oneMonthPyar);

			paramMap.put("oneYearPaymentKyat", oneYearPaymentKyat);
			paramMap.put("oneYearPaymentPyar", oneYearPaymentPyar);
			paramMap.put("sixMonthPaymentKyat", sixYearPaymentKyat);
			paramMap.put("sixMonthPaymentPyar", sixYearPaymentPyar);
			paramMap.put("threeMonthPaymentKyat", threeYearPaymentKyat);
			paramMap.put("threeMonthPaymentPyar", threeYearPaymentPyar);
			paramMap.put("oneMonthPaymentKyat", oneMonthPaymentKyat);
			paramMap.put("oneMonthPaymentPyar", oneMonthPaymentPyar);

			paramMap.put("premiumKyat", termPremiumKyat);
			paramMap.put("premiumPyar", termPremiumPyar);
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SE_SANCTION);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Short Term Endowment Life Sanction", e);
		}
	}

	/* Acceptance Letter */
	public static void generateShortEndowLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
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
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SE_INFORM_ACCEPTED_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Short Term Endowment Life AcceptanceLetter", e);
		}
	}

	/* Reject Letter */
	public static void generateShortEndowLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (lifeProposal.getCustomer() != null) {

				paramMap.put("isCustomer", true);
				if (lifeProposal.getCustomer().getFullIdNo() != null) {
					paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
				} else {
					paramMap.put("nrc", "");
				}
			}
			if (lifeProposal.getOrganization() != null) {
				paramMap.put("orgName", lifeProposal.getOrganization().getName());
				paramMap.put("orgNrc", lifeProposal.getOrganization().getRegNo());
				paramMap.put("isCustomer", false);
			} else {
				paramMap.put("orgNrc", "");
			}
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SE_INFORM_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Short Term Endowment Life RejectLetter", e);
		}
	}

	/* Policy Issue Letter */
	public static void generateShortEndowLifePolicy(LifePolicy lifePolicy, String dirPath, String fileName) {
		try {
			List<JasperPrint> jpList = generateShortEndowLifePolicyJasperPrint(lifePolicy, dirPath, fileName);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document short Term Endowment life policy issue", e);
		}
	}

	/* Migrate Policy Issue Letter */
	public static void generateMigrateShortEndowLifePolicy(LifePolicy lifePolicy, String dirPath, String fileName, List<Payment> paymentList, double extraPremium) {
		try {
			List<JasperPrint> jpList = generateShortEndowLifePolicyJasperPrint(lifePolicy, dirPath, fileName);

			// public life policy
			LifePolicy oldLifePolicy = lifePolicy.getLifeProposal().getLifePolicy();
			PolicyInsuredPerson oldInsuredPerson = oldLifePolicy.getInsuredPersonInfo().get(0);

			PolicyInsuredPerson policyInsuredPerson = lifePolicy.getInsuredPersonInfo().get(0);

			double migrateTotalPremium = 0.0;
			for (Payment p : paymentList) {
				migrateTotalPremium += p.getBasicPremium() + p.getAddOnPremium();
			}

			double oldtotalPremium = migrateTotalPremium + extraPremium;

			Map<String, Object> migrateMap = new HashMap<String, Object>();
			DecimalFormat curFormat = new DecimalFormat("#,###");
			migrateMap.put("insuredPerson", oldInsuredPerson.getName() != null ? oldInsuredPerson.getFullName() : "");
			migrateMap.put("oldpolicyNo", oldLifePolicy.getPolicyNo());
			migrateMap.put("oldSumInsured", curFormat.format(oldLifePolicy.getSumInsured()));
			migrateMap.put("oldperiodYears", oldInsuredPerson.getPeriodYears());
			migrateMap.put("oldstartDate", oldInsuredPerson.getStartDate());
			migrateMap.put("oldtotalPremium", curFormat.format(oldtotalPremium));
			migrateMap.put("newpolicyNo", lifePolicy.getPolicyNo());
			migrateMap.put("newsumInsured", curFormat.format(lifePolicy.getSumInsured()));
			migrateMap.put("newPaymentType", lifePolicy.getPaymentType().getName().toString());
			migrateMap.put("termPremium", curFormat.format(lifePolicy.getTotalBasicTermPremium()));
			migrateMap.put("oneYearPremium", curFormat.format(lifePolicy.getPremium()));
			migrateMap.put("newperiodYears", policyInsuredPerson.getPeriodYears());
			migrateMap.put("newstartDate", policyInsuredPerson.getStartDate());
			migrateMap.put("newtotalPremium", curFormat.format(migrateTotalPremium));
			migrateMap.put("lastPaymentDate", lifePolicy.getActivedPolicyEndDate());
			migrateMap.put("extraValue", curFormat.format(extraPremium));
			InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyMigrateIssue.jrxml");

			JasperReport jreport1 = JasperCompileManager.compileReport(inputStream1);
			JasperPrint jprint1 = JasperFillManager.fillReport(jreport1, migrateMap, new JREmptyDataSource());

			jpList.add(jprint1);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document short Term Endowment life policy issue", e);
		}
	}

	/* Policy Issue Letter */
	private static List<JasperPrint> generateShortEndowLifePolicyJasperPrint(LifePolicy lifePolicy, String dirPath, String fileName) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		try {
			Map<String, Object> coverMap = new HashMap<String, Object>();
			DecimalFormat curFormat = new DecimalFormat("#,###");
			PolicyInsuredPerson policyInsuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
			coverMap.put("policyNo", lifePolicy.getPolicyNo());
			coverMap.put("customerName", policyInsuredPerson.getFullName());
			coverMap.put("totalSumInsured", lifePolicy.getSumInsured());
			InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyIssueCover.jrxml");
			JasperReport jreport1 = JasperCompileManager.compileReport(inputStream1);
			JasperPrint jprint1 = JasperFillManager.fillReport(jreport1, coverMap, new JREmptyDataSource());
			jpList.add(jprint1);

			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			paramIssueDetails.put("phoneNo", lifePolicy.getCustomerPhoneNo());
			paramIssueDetails.put("email", lifePolicy.getCustomerEmail());
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName() + " (" + agent.getLiscenseNo() + ")");
			} else {
				paramIssueDetails.put("agent", "");
			}
			paramIssueDetails.put("productName", policyInsuredPerson.getProduct().getName());
			paramIssueDetails.put("periodYears", policyInsuredPerson.getPeriodYears());
			paramIssueDetails.put("sumInsured", curFormat.format(lifePolicy.getSumInsured()));
			paramIssueDetails.put("customerName", policyInsuredPerson.getFullName());
			paramIssueDetails.put("occupation", policyInsuredPerson.getOccupation() == null ? "" : policyInsuredPerson.getOccupation().getName());
			paramIssueDetails.put("customerAddress", policyInsuredPerson.getResidentAddress().getFullResidentAddress());
			paramIssueDetails.put("ageForNextYear", policyInsuredPerson.getAge());
			paramIssueDetails.put("totalPremium", lifePolicy.getTotalPremium());
			paramIssueDetails.put("totalTermPremium", lifePolicy.getTotalTermPremium());
			paramIssueDetails.put("paymentType", lifePolicy.getPaymentType().getMonth());
			paramIssueDetails.put("startDate", policyInsuredPerson.getStartDate());
			paramIssueDetails.put("endDate", policyInsuredPerson.getEndDate());
			paramIssueDetails.put("lastPaymentDate", policyInsuredPerson.getLastPaymentDate());
			List<PolicyInsuredPersonBeneficiaries> benfList = policyInsuredPerson.getPolicyInsuredPersonBeneficiariesList();
			if (benfList.size() > 1) {
				paramIssueDetails.put("policyInsuredPersonBeneficiariesList", null);
			} else {
				paramIssueDetails.put("policyInsuredPersonBeneficiariesList", benfList);
			}
			paramIssueDetails.put("timeSlotListStr", policyInsuredPerson.getTimeSlotListStr());
			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.SE_POLICY_ISSUE);
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			jpList.add(jprint2);

			if (benfList.size() > 1) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("policyNo", lifePolicy.getPolicyNo());
				paramMap.put("idNo", policyInsuredPerson.getIdNo());
				paramMap.put("insuredPersonName", lifePolicy.getCustomerName());
				paramMap.put("nrc", policyInsuredPerson.getIdNo());
				paramMap.put("listDataSource", new JRBeanCollectionDataSource(benfList));
				InputStream inputStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/lifePolicyBeneficiaries.jrxml");
				JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
				JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, paramMap, new JREmptyDataSource());
				jpList.add(jprint3);
			}
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document short Term Endowment life policy issue", e);
		}
		return jpList;
	}

}
