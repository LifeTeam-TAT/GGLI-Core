package org.ace.insurance.web.common.document.medical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.web.common.Utils;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonDTO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.web.ApplicationSetting;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class MedicalUnderwritingDocFactory extends BasicDAO {

	@SuppressWarnings("unchecked")
	public static void generateMedicalClaimLetter(MedicalClaimProposalDTO medicalClaimProposalDTO, ClaimAcceptedInfo claimAcceptedInfo, String acceptanceDirPath,
			String acceptanceDirFileName) {
		try {
			Map hospParamMap = new HashMap();
			MedicalPolicyInsuredPersonDTO policyInsuredPersonDTO = medicalClaimProposalDTO.getPolicyInsuredPersonDTO();
			String agentName = (medicalClaimProposalDTO.getAgent() == null) ? "" : medicalClaimProposalDTO.getAgent().getFullName();
			hospParamMap.put("customerName", policyInsuredPersonDTO.getFullName());
			hospParamMap.put("customerAddress", policyInsuredPersonDTO.getResidentAddress().getFullResidentAddress());
			hospParamMap.put("policyNo", medicalClaimProposalDTO.getPolicyNo());

			hospParamMap.put("agent", agentName);
			hospParamMap.put("claimNo", medicalClaimProposalDTO.getClaimRequestId());
			hospParamMap.put("currentDate", claimAcceptedInfo.getInformDate());
			hospParamMap.put("liabilitiesAmount", claimAcceptedInfo.getClaimAmount());
			hospParamMap.put("serviceCharges", claimAcceptedInfo.getServicesCharges());
			hospParamMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
			hospParamMap.put("Nrc", policyInsuredPersonDTO.getCustomer().getFullIdNo());

			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(hospParamMap, JasperTemplate.MED_HOSP_CLAIM_INFORM, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), acceptanceDirPath, acceptanceDirFileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeClaimAcceptanceLetter", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void generateMedicalCashReceipt(MedicalProposal medicalProposal, PaymentDTO payment, String dirPath, String fileName, HealthType healthType) {
		MedicalProposalInsuredPerson meProInsuDTO = medicalProposal.getMedicalProposalInsuredPersonList().get(0);
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
		if (HealthType.CRITICALILLNESS.equals(healthType)) {
			productTitle = "Critical Illness Insurance";
			policyType = "CRITICAL ILLNESS INSURANCE";
		}
		if (HealthType.MICROHEALTH.equals(healthType)) {
			productTitle = "MicroHealth Insurance";
			policyType = "MICROHEALTH INSURANCE";
		}
		if (HealthType.HEALTH.equals(healthType)) {
			productTitle = "Health Insurance";
			policyType = "HEALTH INSURANCE";
		}

		if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
			productTitle = "Health Insurance";
			policyType = "HEALTH INSURANCE";
		}
		if (medicalProposal.getCustomer() != null) {
			customerName = medicalProposal.getCustomer().getFullName();
			customerAddress = medicalProposal.getCustomer().getFullAddress();
		}
		if (medicalProposal.getOrganization() != null) {
			customerName = medicalProposal.getOrganization().getName();
			customerAddress = medicalProposal.getOrganization().getFullAddress();
		}
		proposalNo = medicalProposal.getProposalNo();
		paymentTypeName = medicalProposal.getPaymentType().getName();
		sumInsured = medicalProposal.getTotalBasicAndBasicPlusUnit();
		fromDate = meProInsuDTO.getStartDate();
		toDate = meProInsuDTO.getEndDate();
		ncbAmount = meProInsuDTO.getTotalNcbPremium();
		agentWithLiscenseNo = medicalProposal.getAgent() == null ? "" : medicalProposal.getAgent().getFullName() + " (" + medicalProposal.getAgent().getLiscenseNo() + ")";
		try {
			Map paramMap = new HashMap();
			paramMap.put("proposalNo", proposalNo);
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("sumInsured", (int) sumInsured);
			paramMap.put("premium", payment.getBasicPremium());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("addOnPremium", payment.getAddOnPremium());
			paramMap.put("netPremium", payment.getNetPremium());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("totalAmount", payment.getTotalAmount());
			paramMap.put("paymentType", paymentTypeName);
			paramMap.put("registrationNo", registrationNo);
			paramMap.put("policyType", policyType);
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("agent", agentWithLiscenseNo);
			paramMap.put("insuredPerson", customerName);
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("ncbAmount", ncbAmount);
			paramMap.put("currencyCode", CurrencyUtils.getCurrencyCode(null));
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			paramMap.put("policyBranch", medicalProposal.getBranch().getName());
			paramMap.put("productTitle", productTitle);

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
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CASH_RECEIPT, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate MedicalCashReceipt", e);
		}
	}

	public static void generateGroupMicroHealthCashReceipt(GroupMicroHealth gorupMicroHealth, PaymentDTO payment, String dirPath, String fileName) {
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

		productTitle = "Group Micro Health Insurance";
		policyType = "MicroHealth INSURANCE";
		customerName = " ";/* medicalProposal.getOrganization().getName(); */
		customerAddress = " ";/*
								 * medicalProposal.getOrganization().
								 * getFullAddress();
								 */
		paymentTypeName = "LUMPSUM";
		sumInsured = 0; /* medicalProposal.getTotalBasicAndBasicPlusUnit(); */
		fromDate = gorupMicroHealth.getStartDate();
		toDate = DateUtils.addYears(fromDate, 1);
		ncbAmount = 0.0;
		agentWithLiscenseNo = gorupMicroHealth.getAgent() == null ? "" : gorupMicroHealth.getAgent().getFullName() + " (" + gorupMicroHealth.getAgent().getLiscenseNo() + ")";
		try {
			Map paramMap = new HashMap();
			paramMap.put("proposalNo", gorupMicroHealth.getProposalNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("sumInsured", (int) sumInsured);
			paramMap.put("premium", payment.getBasicPremium());
			paramMap.put("discountAmount", payment.getDiscountAmount());
			paramMap.put("addOnPremium", payment.getAddOnPremium());
			paramMap.put("netPremium", payment.getNetPremium());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("stempFees", payment.getStampFees());
			paramMap.put("totalAmount", payment.getTotalAmount());
			paramMap.put("paymentType", paymentTypeName);
			paramMap.put("registrationNo", registrationNo);
			paramMap.put("policyType", policyType);
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("agent", agentWithLiscenseNo);
			paramMap.put("insuredPerson", customerName);
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("ncbAmount", ncbAmount);
			paramMap.put("currencyCode", CurrencyUtils.getCurrencyCode(null));
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			paramMap.put("productTitle", productTitle);

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
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CASH_RECEIPT, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate MedicalCashReceipt", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void generateMedicalClaimInitialReport(MedicalPolicy medicalPolicy, String dirPath, String fileName) {
		try {
			Map map = new HashMap();
			map.put("ownerLogo", ApplicationSetting.getMilogo());
			if (medicalPolicy.getAgent() != null) {
				map.put("agentNameAndNo", medicalPolicy.getAgent().getFullName() + '/' + medicalPolicy.getAgent().getLiscenseNo());
			} else {
				map.put("agentNameAndNo", "-");
			}
			map.put("policyNo", medicalPolicy.getPolicyNo());
			map.put("unit", medicalPolicy.getTotalUnit());
			// map.put("insuPersonName", medicalPolicy.getInsuredPersonName());
			map.put("fatherName", medicalPolicy.getPolicyInsuredPersonList().get(0).getFatherName());
			map.put("nrc", medicalPolicy.getPolicyInsuredPersonList().get(0).getFullIdNo());
			map.put("occupation", medicalPolicy.getPolicyInsuredPersonList().get(0).getCustomer().getOccupation().getName());
			map.put("todayDate", new Date());
			map.put("address", medicalPolicy.getPolicyInsuredPersonList().get(0).getFullAddress());
			map.put("telephoneNo", medicalPolicy.getPolicyInsuredPersonList().get(0).getCustomer().getContentInfo().getMobile());

			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(map, JasperTemplate.MED_CLAIM_INITIAL_REPORT, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document public life policy issue", e);
		}
	}

	// medical claim Acceptance Letter
	@SuppressWarnings("unchecked")
	public static void generateMedicalClaimAcceptanceLetter(MedicalClaimProposalDTO medicalClaimProposalDTO, String dirPath, String fileName) {
		try {
			Map paramMap = new HashMap();
			double totalAmt = 0.0;
			paramMap.put("policyNo", medicalClaimProposalDTO.getPolicyNo());
			paramMap.put("currentDate", new Date());
			paramMap.put("customerName", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullName());
			paramMap.put("claimNo", medicalClaimProposalDTO.getClaimRequestId());
			paramMap.put("unit", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getUnit());
			paramMap.put("agent", medicalClaimProposalDTO.getAgent() == null ? ""
					: medicalClaimProposalDTO.getAgent().getFullName() + " ( " + medicalClaimProposalDTO.getAgent().getLiscenseNo() + " )");
			paramMap.put("customerAddress", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullAddress());
			paramMap.put("ownerLogo", ApplicationSetting.getMilogo());
			paramMap.put("ownerAddress", ApplicationSetting.getCompanyAddressAdFilePath());
			if (medicalClaimProposalDTO.getHospitalizedClaimDTO() != null) {
				if (medicalClaimProposalDTO.getHospitalizedClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getHospitalizedClaimDTO().getHospitalizedAmount();
				}
			}
			if (medicalClaimProposalDTO.getOperationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getOperationClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getOperationClaimDTO().getOperationFee();
				}
			}
			if (medicalClaimProposalDTO.getMedicationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getMedicationClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getMedicationClaimDTO().getMedicationFee();
				}
			}
			if (medicalClaimProposalDTO.getDeathClaimDTO() != null) {
				if (medicalClaimProposalDTO.getDeathClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getDeathClaimDTO().getDeathClaimAmount();
				}
			}
			paramMap.put("totalClaimAmount", totalAmt);

			if (medicalClaimProposalDTO.getHospitalizedClaimDTO() != null) {
				if (medicalClaimProposalDTO.getHospitalizedClaimDTO().isApproved()) {
					paramMap.put("hospitalizedClaimAmount", medicalClaimProposalDTO.getHospitalizedClaimDTO().getHospitalizedAmount());
				} else {
					paramMap.put("hospitalizedClaimAmount", 0.0);
				}
			} else {
				paramMap.put("hospitalizedClaimAmount", 0.0);
			}

			if (medicalClaimProposalDTO.getOperationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getOperationClaimDTO().isApproved()) {
					paramMap.put("operationClaimAmount", medicalClaimProposalDTO.getOperationClaimDTO().getOperationFee());
				} else {
					paramMap.put("operationClaimAmount", 0.0);
				}
			} else {
				paramMap.put("operationClaimAmount", 0.0);
			}

			if (medicalClaimProposalDTO.getMedicationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getMedicationClaimDTO().isApproved()) {
					paramMap.put("medicationClaimAmount", medicalClaimProposalDTO.getMedicationClaimDTO().getMedicationFee());
				} else {
					paramMap.put("medicationClaimAmount", 0.0);
				}
			} else {
				paramMap.put("medicationClaimAmount", 0.0);
			}

			if (medicalClaimProposalDTO.getDeathClaimDTO() != null) {
				if (medicalClaimProposalDTO.getDeathClaimDTO().isApproved()) {
					paramMap.put("deathClaimAmount", medicalClaimProposalDTO.getDeathClaimDTO().getDeathClaimAmount());
				} else {
					paramMap.put("deathClaimAmount", 0.0);
				}
			} else {
				paramMap.put("deathClaimAmount", 0.0);
			}
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CLAIM_INFORM_ACCEPT_LETTER, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}

	}

	// medical claim Reject Letter
	@SuppressWarnings("unchecked")
	public static void generateMedicalClaimRejectLetter(MedicalClaimProposalDTO medicalClaimProposalDTO, String dirPath, String fileName) {
		try {
			Map paramMap = new HashMap();
			paramMap.put("customerName", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullName());
			if (medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer() != null) {
				paramMap.put("nrc", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("policyNo", medicalClaimProposalDTO.getPolicyNo());
			paramMap.put("currentDate", new Date());
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CLAIM_INFORM_REJECT_LETTER, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate medicalRejectLetter", e);
		}

	}

	@SuppressWarnings("unchecked")
	public static void generateMedicalAcceptanceLetter(MedicalProposal medicalProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName, HealthType healthType,
			Language language) {
		try {
			Map paramMap = new HashMap();
			Customer customer = medicalProposal.getCustomer();
			Organization organization = medicalProposal.getOrganization();
			String customerName = "";
			String customerAddress = "";
			String customerPhone = "";
			if (customer != null) {
				customerName = customer.getFullName();
				customerAddress = customer.getFullAddress();
				customerPhone = customer.getContentInfo().getPhone();
			} else if (organization != null) {
				customerName = organization.getName();
				customerAddress = organization.getFullAddress();
				customerPhone = organization.getContentInfo().getPhone();
			}
			List<JasperPrint> jpList = new ArrayList<JasperPrint>();
			for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
				String basicPlus = "";
				String additional1 = "";
				String basicUnit = "";

				Double termPremium = 0.0;
				if (person.getUnit() > 0) {
					basicPlus += "Basic";
				}
				if (person.getUnit() > 0 && person.getBasicPlusUnit() > 0) {
					basicPlus += " + ";
				}
				if (person.getBasicPlusUnit() != 0) {
					basicPlus += "Basic Plus (" + person.getBasicPlusUnit() + " Units)";
				}
				for (MedicalProposalInsuredPersonAddOn addOn : person.getInsuredPersonAddOnList()) {
					if (!additional1.isEmpty()) {
						additional1 += ", ";
					}
					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn1()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn1())) {
						additional1 += "Additional Cover (1) - " + addOn.getUnit() + " Units";
					}
					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn2()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn2())) {
						additional1 += "Additional Cover (2) - " + addOn.getUnit() + " Units";
					}
				}
				// if (medicalProposal.getPaymentType().getMonth() > 0) {
				// termPremium = person.getProposedPremium() *
				// medicalProposal.getPaymentType().getMonth() / 12;
				// } else {
				// termPremium = person.getProposedPremium();
				// }
				basicUnit = person.getUnit() + (person.getUnit() == 1 ? " Unit" : " Units");

				double stampFees = (person.getUnit() + person.getBasicPlusUnit()) * 300.0;

				paramMap.put("customerName", customerName);
				paramMap.put("basicPlus", basicPlus);
				paramMap.put("additional1", additional1);
				paramMap.put("basicUnit", basicUnit);
				paramMap.put("paymentType", medicalProposal.getPaymentType().getDescription());
				paramMap.put("oneYearPremium", person.getProposedPremium());
				// paramMap.put("termPremium", termPremium);
				paramMap.put("startDate", person.getStartDate());
				paramMap.put("endDate", person.getEndDate());
				paramMap.put("phoneNo", customerPhone);
				paramMap.put("proposalNo", medicalProposal.getProposalNo());
				paramMap.put("periodYears", person.getPeriodYears());
				paramMap.put("totalPremium", person.getTotalPremium());
				paramMap.put("nbcTotalPremium", person.getTotalNcbPremium());
				paramMap.put("customerAddress", customerAddress);
				paramMap.put("currentDate", Utils.formattedDate(new Date()));
				paramMap.put("ownerLogo", ApplicationSetting.getMilogo());
				// TODO FIXME accpetedInfo is totals , here is amount for each
				// insured person
				paramMap.put("servicesCharges", acceptedInfo.getServicesCharges());
				paramMap.put("discountAmount", acceptedInfo.getDiscountAmount());
				paramMap.put("stampFees", stampFees);
				paramMap.put("paymentType", medicalProposal.getPaymentType().getDescription());
				paramMap.put("termPremium", person.getTermPremium());
				paramMap.put("netTermAmount", person.getTotalPremium() - acceptedInfo.getDiscountAmount() - person.getTotalNcbPremium() + acceptedInfo.getServicesCharges());
				if (medicalProposal.getAgent() != null) {
					paramMap.put("agent", medicalProposal.getAgent().getFullName() + "( " + medicalProposal.getAgent().getLiscenseNo() + " )");
				} else {
					paramMap.put("agent", "( - )");
				}
				String templateDir = null;
				if (HealthType.CRITICALILLNESS.equals(healthType)) {
					templateDir = language.equals(Language.MYANMAR) ? JasperTemplate.CRITICAL_ILLNESS_INFORM_ACCEPT : JasperTemplate.CRITICAL_ILLNESS_INFORM_ACCEPT_ENG;
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, templateDir, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.HEALTH.equals(healthType)) {
					templateDir = language.equals(Language.MYANMAR) ? JasperTemplate.MED_HEALTH_INFORM_ACCEPT : JasperTemplate.MED_HEALTH_INFORM_ACCEPT_ENG;
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, templateDir, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.MICROHEALTH.equals(healthType)) {
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_INFORM_ACCEPT, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_INFORM_ACCEPT, new JREmptyDataSource());
					jpList.add(jprint_1);
				}

			}
			jasperListPDFExport(jpList, dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void generateMedicalRejectLetter(MedicalProposal medicalProposal, String dirPath, String fileName, HealthType healtType) {
		try {
			Map paramMap = new HashMap();
			Customer customer = medicalProposal.getCustomer();
			Organization organization = medicalProposal.getOrganization();
			String customerName = "";
			String customerAddress = "";
			String customerNrc = "";
			if (customer != null) {
				customerName = customer.getFullName();
				customerAddress = customer.getFullAddress();
				customerNrc = customer.getFullIdNo();
			} else if (organization != null) {
				customerName = organization.getName();
				customerAddress = organization.getFullAddress();
				customerNrc = organization.getRegNo();
			}
			paramMap.put("customerName", customerName);
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("agent", medicalProposal.getAgent() != null ? medicalProposal.getAgent().getFullName() : "");
			paramMap.put("nrc", customerNrc);
			paramMap.put("proposalNo", medicalProposal.getProposalNo());
			paramMap.put("todayDate", new Date());
			paramMap.put("ownerLogo", ApplicationSetting.getMilogo());

			if (HealthType.CRITICALILLNESS.equals(healtType)) {
				JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICAL_ILLNESS_INFORM_REJECT, new JREmptyDataSource());
				jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
			}
			if (HealthType.HEALTH.equals(healtType)) {
				JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_INFORM_REJECT, new JREmptyDataSource());
				jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
			}
			if (HealthType.MICROHEALTH.equals(healtType)) {
				JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_INFORM_REJECT, new JREmptyDataSource());
				jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
			}
			if (HealthType.MEDICAL_INSURANCE.equals(healtType)) {
				JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_INFORM_REJECT, new JREmptyDataSource());
				jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
			}
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate MedicalRejectLetter", e);
		}
	}

	public static void generateMedicalSanction1(MedicalSurvey medicalSurvey, String dirPath, String fileName) {
		try {
			MedicalProposal proposal = medicalSurvey.getMedicalProposal();
			MedicalProposalInsuredPerson person = proposal.getMedicalProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Date date = person.getCustomer().getDateOfBirth(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH + 1);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			String s1 = String.valueOf(person.getCustomer().getHeight());
			int in1 = 0;
			if (s1.contains(".")) {
				in1 = Integer.parseInt(s1.substring(s1.indexOf(".") + 1));
			}

			paramMap.put("agentName", proposal.getAgent().getFullName());
			paramMap.put("agentNo", proposal.getAgent().getCodeNo());
			paramMap.put("customerName", person.getFullName());
			paramMap.put("fatherName", person.getCustomer().getFatherName());
			paramMap.put("age", person.getAge());
			paramMap.put("birthYear", year);
			paramMap.put("birthMonth", month);
			paramMap.put("birthDate", day);
			paramMap.put("placeOfBirth", person.getCustomer().getPlaceOfBirth());
			paramMap.put("nationality", person.getCustomer().getCountry().getName());
			paramMap.put("metiralStatus", person.getCustomer().getMaritalStatus().getLabel());
			paramMap.put("gender", person.getCustomer().getGender().getLabel());
			paramMap.put("height", Math.floor(person.getCustomer().getHeight()));
			paramMap.put("inches", in1);
			paramMap.put("weight", person.getCustomer().getWeight());
			paramMap.put("nrc", person.getCustomer().getFullIdNo());
			paramMap.put("occupation", person.getCustomer().getOccupation().getName());
			paramMap.put("address", person.getCustomer().getResidentAddress().getFullResidentAddress());
			paramMap.put("phone", person.getCustomer().getContentInfo().getPhoneOrMoblieNo());
			paramMap.put("email", person.getCustomer().getContentInfo().getEmail());
			if (person.getCustomer().getIdType().equals(IdType.PASSPORTNO)) {
				paramMap.put("passportType", person.getCustomer().getPassportType().getLabel());
			}

			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_SANCTION, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Medical Sanction", e);
		}
	}

	public static void generateMedicalSanction(MedicalSurvey medicalSurvey, String dirPath, String fileName, HealthType healthtType) {
		try {
			List<JasperPrint> jpList = new ArrayList<>();
			MedicalProposal proposal = medicalSurvey.getMedicalProposal();
			for (MedicalProposalInsuredPerson person : proposal.getMedicalProposalInsuredPersonList()) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				String basicPlus = "";
				String basicUnit = "";
				String periodString = "";
				String additionalUnits = "";

				if (person.getUnit() > 0) {
					basicPlus += "Basic";
				}
				if (person.getUnit() > 0 && person.getBasicPlusUnit() > 0) {
					basicPlus += " + ";
				}
				if (person.getBasicPlusUnit() > 0) {
					basicPlus += "Basic Plus(" + person.getBasicPlusUnit() + " Units)";
				}
				for (MedicalProposalInsuredPersonAddOn addOn : person.getInsuredPersonAddOnList()) {
					if (!additionalUnits.isEmpty()) {
						additionalUnits += ", ";
					}
					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn1()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn1())) {
						additionalUnits += "Additional Cover (1) - " + addOn.getUnit() + " Units";
					}
					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn2()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn2())) {
						additionalUnits += "Additional Cover (2) - " + addOn.getUnit() + " Units";
					}
				}
				if (person.getGuardian() != null) {
					paramMap.put("guardianName", person.getGuardian().getCustomer().getFullName());
				}
				basicUnit = person.getUnit() + (person.getUnit() == 1 ? " Unit" : " Units");
				periodString += DateUtils.getDateFormatString(person.getStartDate()) + "  to  " + DateUtils.getDateFormatString(person.getEndDate());
				paramMap.put("premium", person.getTermPremium());
				paramMap.put("basicUnit", basicUnit);
				paramMap.put("proposalNo", proposal.getProposalNo());
				paramMap.put("customerName", person.getFullName());
				paramMap.put("basicPlus", basicPlus);
				paramMap.put("additionalUnits", additionalUnits);
				paramMap.put("age", person.getAge());
				paramMap.put("periodString", periodString);
				paramMap.put("paymentType", proposal.getPaymentType().getName());
				paramMap.put("conditionOfHealth", medicalSurvey.getConditionOfHealth());
				paramMap.put("remark", medicalSurvey.getRemark());
				paramMap.put("reportLogo", ApplicationSetting.getGGILogo());
				paramMap.put("addressLogo", ApplicationSetting.getGGIAddress());

				if (HealthType.CRITICALILLNESS.equals(healthtType)) {
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICAL_ILLNESS_SANCTION, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.HEALTH.equals(healthtType)) {
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_HEALTH_SANCTION, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.MICROHEALTH.equals(healthtType)) {
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_SANCTION, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.MEDICAL_INSURANCE.equals(healthtType)) {
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_SANCTION, new JREmptyDataSource());
					jpList.add(jprint_1);
				}

			}
			jasperListPDFExport(jpList, dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Medical Sanction", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void generateMedicalClaimCashReceipt(MedicalClaimProposalDTO medicalClaimProposalDTO, Payment payment, String dirPath, String fileName) {
		try {
			MedicalPolicyInsuredPersonDTO person = medicalClaimProposalDTO.getPolicyInsuredPersonDTO();
			// MedicalPolicyInsuredPerson insuredPerson =
			// MedicalPolicyInsuredPersonFactory.createMedicalPolicyInsuredPerson(person);
			String claimNo = medicalClaimProposalDTO.getClaimRequestId();
			String policyNo = medicalClaimProposalDTO.getPolicyNo();
			int unit = person.getUnit() + person.getBasicPlusUnit();
			String policyType = person.getProduct().getName();
			String paymentType = "";
			Double claimAmount = payment.getClaimAmount();
			Double discountAmount = payment.getDiscountAmount();
			Double totalAmount = payment.getNetClaimAmount();
			Date fromDate = new Date();
			Date toDate = new Date();
			String insuredPersonName = person.getFullName();
			String customerAddress = person.getCustomer().getFullAddress();
			String agentNameWithLiscenseNo = "";
			agentNameWithLiscenseNo = medicalClaimProposalDTO.getAgent() == null ? ""
					: medicalClaimProposalDTO.getAgent().getFullName() + " (" + medicalClaimProposalDTO.getAgent().getLiscenseNo() + ")";
			Map paramMap = new HashMap();

			if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				paramMap.put("receiptType", "Cash Payment");
			} else {
				paramMap.put("receiptType", "Cheque Payment");
				paramMap.put("chequeNo", payment.getChequeNo());
				paramMap.put("bank", payment.getBank().getName());
			}

			paramMap.put("claimNo", claimNo);
			paramMap.put("policyNo", policyNo);
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("claimAmount", claimAmount);
			paramMap.put("discountAmount", discountAmount);
			paramMap.put("totalAmount", totalAmount);
			paramMap.put("noOfUnits", unit);
			paramMap.put("policyType", policyType);
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("paymentType", paymentType);
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("agent", agentNameWithLiscenseNo);
			paramMap.put("insuredPersonName", insuredPersonName);
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			paramMap.put("ownerLogo", ApplicationSetting.getMilogo());
			paramMap.put("ownerAddress", ApplicationSetting.getCompanyAddressAdFilePath());
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CLAIM_CASH_RECEIPT, new JREmptyDataSource());
			jasperListPDFExport(Arrays.asList(jprint_1), dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate GroupLifeSanction", e);
		}

	}

	@SuppressWarnings("unchecked")
	public static void generateMedicalPolicyIssue(MedicalPolicy medicalPolicy, Payment payment, String dirPath, String fileName, HealthType healthType, Language language) {
		try {
			Map coverMap = new HashMap();
			Map map = new HashMap();
			Map beneMap = new HashMap();
			List jpList = new ArrayList();
			for (MedicalPolicyInsuredPerson insuredPerson : medicalPolicy.getPolicyInsuredPersonList()) {
				map.clear();
				String basicUnit = "0  Units";
				String basicPlus = "0  Units";
				String additional1 = "0  Units";
				String additional2 = "0  Units";
				String benePercent = "";
				double totalPremium = 0.0;
				double termPremium = 0.0;
				if (insuredPerson.getBasicPlusUnit() != 0) {
					basicPlus = insuredPerson.getBasicPlusUnit() + " Units";
					map.put("addOnUnit1", insuredPerson.getBasicPlusUnit());
					map.put("premiumAddon1", insuredPerson.getBasicPlusPremium());
				}
				basicUnit = insuredPerson.getUnit() + (insuredPerson.getUnit() == 1 ? " Unit" : " Units");
				for (MedicalPolicyInsuredPersonAddOn addOn : insuredPerson.getPolicyInsuredPersonAddOnList()) {

					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn2()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn1())) {
						additional1 = addOn.getUnit() + (addOn.getUnit() == 1 ? " Unit" : "  Units");
					}

					if (addOn.getAddOn().getId().equals(ProductIDConfig.getMedAddOn3()) || addOn.getAddOn().getId().equals(ProductIDConfig.getHealthAddOn2())) {
						additional2 = addOn.getUnit() + (addOn.getUnit() == 1 ? " Unit" : "  Units");
					}
				}

				termPremium = insuredPerson.getTotalTermPremium();

				totalPremium = insuredPerson.getPremium() + insuredPerson.getAddOnPremium() + insuredPerson.getBasicPlusPremium() - insuredPerson.getTotalNcbPremium();
				coverMap.put("policyNo", medicalPolicy.getPolicyNo());
				coverMap.put("customerName", insuredPerson.getCustomer().getFullName());
				coverMap.put("basicUnit", basicUnit);
				coverMap.put("basicPlus", basicPlus);
				coverMap.put("additional1", additional1);
				coverMap.put("additional2", additional2);

				coverMap.put("nextPaymentDate", medicalPolicy.getTimeSlotList() == null ? "-" : medicalPolicy.getTimeSlotList());
				coverMap.put("nextTermPremium", medicalPolicy.getTimeSlotList() == null ? 0 : termPremium);
				coverMap.put("reportLogo", ApplicationSetting.getCompanyLogoFilePath());
				coverMap.put("issueCover", ApplicationSetting.getRenewalIssueBackGroundLogo());
				// JasperPrint jprint =
				// JasperFactory.generateJasperPrint(coverMap,
				// JasperTemplate.MED_POLICY_COVER, new JREmptyDataSource());
				// jpList.add(jprint);

				if (null != payment) {
					map.put("cashReceiptNo", payment.getReceiptNo());
					map.put("date", payment.getPaymentDate());
				} else {
					map.put("cashReceiptNo", " ");
					map.put("date", medicalPolicy.getActivedPolicyStartDate());
				}

				map.put("policyNo", medicalPolicy.getPolicyNo());
				map.put("agentNameCode", medicalPolicy.getAgent() == null ? "" : medicalPolicy.getAgent().getFullName() + " ( " + medicalPolicy.getAgent().getLiscenseNo() + " )");
				map.put("fromDate", insuredPerson.getStartDate());
				map.put("toDate", insuredPerson.getEndDate());
				map.put("basicPremium", insuredPerson.getBasicTermPremium());
				map.put("basicUnit", basicUnit);
				map.put("premium", medicalPolicy.getTotalBasicTermPremiumDouble());
				map.put("paymentType", medicalPolicy.getPaymentType().getName());
				map.put("occupation", insuredPerson.getCustomer().getOccupation() == null ? "" : insuredPerson.getCustomer().getOccupation().getName());
				map.put("phNo", insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo());
				map.put("email", insuredPerson.getCustomer().getContentInfo().getEmail());
				map.put("oneYearPremium", insuredPerson.getPremium());
				map.put("termPremium", termPremium);

				for (MedicalPolicyInsuredPersonAddOn addOn : insuredPerson.getPolicyInsuredPersonAddOnList()) {
					if (ProductIDConfig.getMedAddOn1().equalsIgnoreCase(addOn.getAddOn().getId()) || ProductIDConfig.getHealthAddOn1().equalsIgnoreCase(addOn.getAddOn().getId())) {
						map.put("addOnUnit1", addOn.getUnit() + (addOn.getUnit() == 1 ? " Unit" : " Units"));
						map.put("premiumAddon1", addOn.getPremium());
					}
					if (ProductIDConfig.getMedAddOn2().equalsIgnoreCase(addOn.getAddOn().getId()) || ProductIDConfig.getHealthAddOn2().equalsIgnoreCase(addOn.getAddOn().getId())) {
						map.put("addOnUnit2", addOn.getUnit() + (addOn.getUnit() == 1 ? " Unit" : " Units"));
						map.put("premiumAddon2", addOn.getPremium());
					}
					if (ProductIDConfig.getMedAddOn3().equalsIgnoreCase(addOn.getAddOn().getId())) {
						map.put("addOnUnit3", addOn.getUnit() + (addOn.getUnit() == 1 ? " Unit" : " Units"));
						map.put("premiumAddon3", addOn.getPremium());
					}
				}
				map.put("insuredName", insuredPerson.getCustomer().getFullName());
				map.put("totalPremium", totalPremium);
				map.put("fatherName", insuredPerson.getFatherName());
				map.put("insuredNRC", insuredPerson.getFullIdNo());
				map.put("insuredAge", Utils.formattedDate(insuredPerson.getDateOfBirth()) + " (" + insuredPerson.getAge() + " year)");
				map.put("address", insuredPerson.getFullAddress());

				if (insuredPerson.getPolicyInsuredPersonBeneficiariesList().size() == 1) {
					for (MedicalPolicyInsuredPersonBeneficiaries benePerson : insuredPerson.getPolicyInsuredPersonBeneficiariesList()) {
						map.put("beneName", benePerson.getFullName());
						map.put("beneFatherName", benePerson.getFatherName());
						map.put("beneNrc", benePerson.getFullIdNo());
						map.put("beneAge", benePerson.getAge());
						map.put("beneAddress", benePerson.getResidentAddress().getFullResidentAddress());
						map.put("benePhNo", benePerson.getContentInfo().getPhone());
						benePercent = benePerson.getPercentage() + "%";
						map.put("benePercent", benePercent);
					}
				} else {
					map.put("benePercent", "see Attached");
					map.put("beneName", "see Attached");
				}

				String templateDir = null;
				if (HealthType.CRITICALILLNESS.equals(healthType)) {
					boolean isCriticalIllness = true;
					coverMap.put("isCriticalIllness", isCriticalIllness);
					templateDir = language.equals(Language.MYANMAR) ? JasperTemplate.MED_POLICY_COVER : JasperTemplate.CRITICAL_POLICY_COVER_ENG;
					JasperPrint jprint = JasperFactory.generateJasperPrint(coverMap, templateDir, new JREmptyDataSource());
					jpList.add(jprint);
					map.put("beneficiaryList", new JRBeanCollectionDataSource(insuredPerson.getPolicyInsuredPersonBeneficiariesList()));
					templateDir = language.equals(Language.MYANMAR) ? JasperTemplate.CRITICAL_ILLNESS_POLICY_ISSUE : JasperTemplate.CRITICAL_ILLNESS_POLICY_ISSUE_ENG;
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(map, templateDir, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.MICROHEALTH.equals(healthType)) {
					JasperPrint jprint = JasperFactory.generateJasperPrint(coverMap, JasperTemplate.MED_POLICY_COVER, new JREmptyDataSource());
					jpList.add(jprint);
					map.put("beneficiaryList", new JRBeanCollectionDataSource(insuredPerson.getPolicyInsuredPersonBeneficiariesList()));
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(map, JasperTemplate.MICRO_HEALTH_POLICY_ISSUE, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.HEALTH.equals(healthType)) {
					boolean isHealth = true;
					coverMap.put("isHealth", isHealth);
					templateDir = language.equals(Language.MYANMAR) ? JasperTemplate.MED_POLICY_COVER : JasperTemplate.HEATLH_POLICY_COVER_ENG;
					JasperPrint jprint = JasperFactory.generateJasperPrint(coverMap, templateDir, new JREmptyDataSource());
					jpList.add(jprint);
					templateDir = language.equals(Language.MYANMAR) ? JasperTemplate.MED_HEALTH_POLICY_ISSUE : JasperTemplate.MED_HEALTH_POLICY_ISSUE_ENG;
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(map, templateDir, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
				if (HealthType.MEDICAL_INSURANCE.equals(healthType)) {
					boolean isHealth = true;
					coverMap.put("isHealth", isHealth);
					JasperPrint jprint = JasperFactory.generateJasperPrint(coverMap, JasperTemplate.MED_POLICY_COVER, new JREmptyDataSource());
					jpList.add(jprint);
					map.put("beneficiaryList", new JRBeanCollectionDataSource(insuredPerson.getPolicyInsuredPersonBeneficiariesList()));
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(map, JasperTemplate.MED_POLICY_ISSUE, new JREmptyDataSource());
					jpList.add(jprint_1);
				}

				if (insuredPerson.getPolicyInsuredPersonBeneficiariesList().size() > 1) {
					templateDir = language.equals(Language.MYANMAR) ? JasperTemplate.MED_BENEFICIAL_ATTACHEMENT : JasperTemplate.MED_BENEFICIAL_ATTACHEMENT_ENG;
					beneMap.put("beneficiaryList", new JRBeanCollectionDataSource(insuredPerson.getPolicyInsuredPersonBeneficiariesList()));
					JasperPrint jprint_1 = JasperFactory.generateJasperPrint(beneMap, templateDir, new JREmptyDataSource());
					jpList.add(jprint_1);
				}
			}

			jasperListPDFExport(jpList, dirPath, fileName);

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document public life policy issue", e);
		}
	}

	public static <T> void generateAllMedicalClaimDocument(MedicalClaimProposalDTO medicalClaimProposalDTO, ClaimAcceptedInfo claimAcceptedInfo, Payment payment, boolean isInform,
			boolean isCashReceipt, String dirPath, String fileName) {
		try {
			List<JasperPrint> printList = new ArrayList<JasperPrint>();
			List<JasperPrint> informList = null;
			List<JasperPrint> cashReceiptList = null;

			/*** Export Inform Letter ****/

			if (isInform) {
				informList = generateMedicalClaimAcceptanceJasperPrint(medicalClaimProposalDTO, dirPath, fileName);
			} else {
				informList = generateMedicalClaimRejectJasperPrint(medicalClaimProposalDTO, dirPath, fileName);
			}
			printList.addAll(informList);

			/*** Export cashReceipt Letter ****/
			if (isCashReceipt) {
				cashReceiptList = generateMedicalClaimCashReceiptJasperPrint(medicalClaimProposalDTO, payment, dirPath, fileName);
				printList.addAll(cashReceiptList);
			}
			JasperFactory.exportReportToPdfFile(printList, dirPath, fileName);
		} catch (SystemException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate MotorProposalInfo Letter", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<JasperPrint> generateMedicalClaimAcceptanceJasperPrint(MedicalClaimProposalDTO medicalClaimProposalDTO, String dirPath, String fileName) {
		try {
			Map paramMap = new HashMap();
			double totalAmt = 0.0;
			paramMap.put("policyNo", medicalClaimProposalDTO.getPolicyNo());
			paramMap.put("currentDate", new Date());
			paramMap.put("customerName", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullName());
			paramMap.put("claimNo", medicalClaimProposalDTO.getClaimRequestId());
			paramMap.put("unit", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getUnit());
			paramMap.put("agent", medicalClaimProposalDTO.getAgent() == null ? ""
					: medicalClaimProposalDTO.getAgent().getFullName() + " ( " + medicalClaimProposalDTO.getAgent().getLiscenseNo() + " )");
			paramMap.put("customerAddress", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullAddress());
			paramMap.put("ownerLogo", ApplicationSetting.getMilogo());
			paramMap.put("ownerAddress", ApplicationSetting.getCompanyAddressAdFilePath());
			if (medicalClaimProposalDTO.getHospitalizedClaimDTO() != null) {
				if (medicalClaimProposalDTO.getHospitalizedClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getHospitalizedClaimDTO().getHospitalizedAmount();
				}
			}
			if (medicalClaimProposalDTO.getOperationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getOperationClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getOperationClaimDTO().getOperationFee();
				}
			}
			if (medicalClaimProposalDTO.getMedicationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getMedicationClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getMedicationClaimDTO().getMedicationFee();
				}
			}
			if (medicalClaimProposalDTO.getDeathClaimDTO() != null) {
				if (medicalClaimProposalDTO.getDeathClaimDTO().isApproved()) {
					totalAmt += medicalClaimProposalDTO.getDeathClaimDTO().getDeathClaimAmount();
				}
			}
			paramMap.put("totalClaimAmount", totalAmt);

			if (medicalClaimProposalDTO.getHospitalizedClaimDTO() != null) {
				if (medicalClaimProposalDTO.getHospitalizedClaimDTO().isApproved()) {
					paramMap.put("hospitalizedClaimAmount", medicalClaimProposalDTO.getHospitalizedClaimDTO().getHospitalizedAmount());
				} else {
					paramMap.put("hospitalizedClaimAmount", 0.0);
				}
			} else {
				paramMap.put("hospitalizedClaimAmount", 0.0);
			}

			if (medicalClaimProposalDTO.getOperationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getOperationClaimDTO().isApproved()) {
					paramMap.put("operationClaimAmount", medicalClaimProposalDTO.getOperationClaimDTO().getOperationFee());
				} else {
					paramMap.put("operationClaimAmount", 0.0);
				}
			} else {
				paramMap.put("operationClaimAmount", 0.0);
			}

			if (medicalClaimProposalDTO.getMedicationClaimDTO() != null) {
				if (medicalClaimProposalDTO.getMedicationClaimDTO().isApproved()) {
					paramMap.put("medicationClaimAmount", medicalClaimProposalDTO.getMedicationClaimDTO().getMedicationFee());
				} else {
					paramMap.put("medicationClaimAmount", 0.0);
				}
			} else {
				paramMap.put("medicationClaimAmount", 0.0);
			}

			if (medicalClaimProposalDTO.getDeathClaimDTO() != null) {
				if (medicalClaimProposalDTO.getDeathClaimDTO().isApproved()) {
					paramMap.put("deathClaimAmount", medicalClaimProposalDTO.getDeathClaimDTO().getDeathClaimAmount());
				} else {
					paramMap.put("deathClaimAmount", 0.0);
				}
			} else {
				paramMap.put("deathClaimAmount", 0.0);
			}
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CLAIM_INFORM_ACCEPT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint_1);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<JasperPrint> generateMedicalClaimRejectJasperPrint(MedicalClaimProposalDTO medicalClaimProposalDTO, String dirPath, String fileName) {
		try {
			Map paramMap = new HashMap();
			paramMap.put("customerName", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullName());
			if (medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer() != null) {
				paramMap.put("nrc", medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("policyNo", medicalClaimProposalDTO.getPolicyNo());
			paramMap.put("currentDate", new Date());
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CLAIM_INFORM_REJECT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint_1);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate medicalRejectLetter", e);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<JasperPrint> generateMedicalClaimCashReceiptJasperPrint(MedicalClaimProposalDTO medicalClaimProposalDTO, Payment payment, String dirPath, String fileName) {
		try {
			MedicalPolicyInsuredPersonDTO person = medicalClaimProposalDTO.getPolicyInsuredPersonDTO();
			// MedicalPolicyInsuredPerson insuredPerson =
			// MedicalPolicyInsuredPersonFactory.createMedicalPolicyInsuredPerson(person);
			String claimNo = medicalClaimProposalDTO.getClaimRequestId();
			String policyNo = medicalClaimProposalDTO.getPolicyNo();
			int unit = person.getUnit() + person.getBasicPlusUnit();
			String policyType = person.getProduct().getName();
			String paymentType = "";
			Double claimAmount = payment.getClaimAmount();
			Double discountAmount = payment.getDiscountAmount();
			Double totalAmount = payment.getNetClaimAmount();
			Date fromDate = new Date();
			Date toDate = new Date();
			String insuredPersonName = person.getFullName();
			String customerAddress = person.getCustomer().getFullAddress();
			String agentNameWithLiscenseNo = "";
			agentNameWithLiscenseNo = medicalClaimProposalDTO.getAgent() == null ? ""
					: medicalClaimProposalDTO.getAgent().getFullName() + " (" + medicalClaimProposalDTO.getAgent().getLiscenseNo() + ")";
			Map paramMap = new HashMap();

			if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				paramMap.put("receiptType", "Cash Payment");
			} else {
				paramMap.put("receiptType", "Cheque Payment");
				paramMap.put("chequeNo", payment.getChequeNo());
				paramMap.put("bank", payment.getBank().getName());
			}

			paramMap.put("claimNo", claimNo);
			paramMap.put("policyNo", policyNo);
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("claimAmount", claimAmount);
			paramMap.put("discountAmount", discountAmount);
			paramMap.put("totalAmount", totalAmount);
			paramMap.put("noOfUnits", unit);
			paramMap.put("policyType", policyType);
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("paymentType", paymentType);
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("agent", agentNameWithLiscenseNo);
			paramMap.put("insuredPersonName", insuredPersonName);
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			paramMap.put("ownerLogo", ApplicationSetting.getMilogo());
			paramMap.put("ownerAddress", ApplicationSetting.getCompanyAddressAdFilePath());
			JasperPrint jprint_1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_CLAIM_CASH_RECEIPT, new JREmptyDataSource());
			return Arrays.asList(jprint_1);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate GroupLifeSanction", e);
		}

	}
}
