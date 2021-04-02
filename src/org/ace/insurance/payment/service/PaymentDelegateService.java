package org.ace.insurance.payment.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.migrate.ShortEndowmentExtraValue;
import org.ace.insurance.life.migrate.service.interfaces.IShortEndowmentExtraValueService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentDelegateService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "PaymentDelegateService")
public class PaymentDelegateService extends BaseService implements IPaymentDelegateService {
	@Resource(name = "PaymentService")
	private IPaymentService paymentService;
	@Resource(name = "LifeProposalService")
	private ILifeProposalService lifeProposalService;
	@Resource(name = "LifePolicyService")
	private ILifePolicyService lifePolicyService;
	@Resource(name = "MedicalPolicyService")
	private IMedicalPolicyService medicalPolicyService;
	@Resource(name = "MedicalProposalService")
	private IMedicalProposalService medicalProposalService;
	@Resource(name = "ShortEndowmentExtraValueService")
	private IShortEndowmentExtraValueService shortEndowmentExtraValueService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void payment(List<PaymentDTO> paymentDTOList, PolicyReferenceType referenceType, User responsiblePerson,
			String remark, Branch branch, SalePoint salePoint) throws SystemException {
		LifeProposal lifeProposal;
		MedicalProposal medicalProposal;
		LifePolicy lifePolicy;
		MedicalPolicy medicalPolicy;
		ShortEndowmentExtraValue extraValue = null;

		for (PaymentDTO dto : paymentDTOList) {
			switch (referenceType) {
			case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			case STUDENT_LIFE_POLICY:
			case LIFE_POLICY:
			case FARMER_POLICY: {
				lifeProposal = lifePolicyService.findLifePolicyById(dto.getReferenceNo()).getLifeProposal();
				PolicyReferenceType policyReferenceType = referenceType.equals(PolicyReferenceType.LIFE_POLICY)
						? PolicyReferenceType.LIFE_POLICY
						: referenceType.equals(PolicyReferenceType.STUDENT_LIFE_POLICY)
								? PolicyReferenceType.STUDENT_LIFE_POLICY
								: referenceType.equals(PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY)
										? PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY
										: referenceType.equals(
												PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY)
														? PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY
														: referenceType.equals(
																PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY)
																		? PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY
																		: PolicyReferenceType.FARMER_POLICY;
				WorkflowReferenceType proposalReferenceType = referenceType.equals(PolicyReferenceType.LIFE_POLICY)
						? WorkflowReferenceType.LIFE_PROPOSAL
						: referenceType.equals(PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY)
								? WorkflowReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
								: referenceType.equals(PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY)
										? WorkflowReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL
										: referenceType.equals(PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY)
												? WorkflowReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL
												: WorkflowReferenceType.FARMER_PROPOSAL;
				List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), policyReferenceType,
						false);
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.ISSUING,
						proposalReferenceType, responsiblePerson, responsiblePerson);
				lifeProposalService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, branch, null);
				String currencyCode = CurrencyUtils.getCurrencyCode(null);
				paymentService.activatePaymentAndTLF(paymentList, null, branch, currencyCode, salePoint);
			}
				break;

			case HEALTH_POLICY:
			case CRITICAL_ILLNESS_POLICY:
			case MICRO_HEALTH_POLICY:
			case MEDICAL_POLICY: {
				medicalProposal = medicalPolicyService.findMedicalPolicyById(dto.getReferenceNo()).getMedicalProposal();
				List<Payment> paymentList = paymentService.findByProposal(medicalProposal.getId(), referenceType,
						false);

				WorkflowTask workflowTask = null;
				WorkflowReferenceType wfReferenceType = null;

				switch (referenceType) {
				case HEALTH_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.HEALTH_PROPOSAL;
					break;
				case CRITICAL_ILLNESS_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL;
					break;
				case MICRO_HEALTH_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.MICRO_HEALTH_PROPOSAL;
					break;
				case MEDICAL_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.MEDICAL_PROPOSAL;
					break;
				default:
					break;
				}

				WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, workflowTask,
						wfReferenceType, responsiblePerson, responsiblePerson);
				medicalProposalService.paymentMedicalProposal(medicalProposal, workFlowDTO, paymentList, branch, null);
				String currencyCode = CurrencyUtils.getCurrencyCode(null);
				paymentService.activatePaymentAndTLF(paymentList, null, branch, currencyCode, salePoint);
			}
				break;
			case SHORT_ENDOWMENT_LIFE_POLICY: {
				lifeProposal = lifePolicyService.findLifePolicyById(dto.getReferenceNo()).getLifeProposal();
				WorkflowReferenceType proposalReferenceType = WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL;
				List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), referenceType, false);
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.ISSUING,
						proposalReferenceType, responsiblePerson, responsiblePerson);
				lifeProposalService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, branch, null);

				String currencyCode = CurrencyUtils.getCurrencyCode(null);
				paymentService.activatePaymentAndTLF(paymentList, null, branch, currencyCode, salePoint);
			}
				break;
			case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			case STUDENT_LIFE_POLICY_BILL_COLLECTION:
			case LIFE_BILL_COLLECTION:
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION: {
				lifePolicy = lifePolicyService.findLifePolicyById(dto.getReferenceNo());
				List<Payment> paymentList = paymentService.findPaymentByReceiptNo(dto.getReceiptNo());
				List<AgentCommission> agentCommissioList = null;

				if (lifePolicy.isHasExtraValue()) {
					extraValue = shortEndowmentExtraValueService
							.findShortEndowmentExtraValueByPolicyNo(lifePolicy.getPolicyNo());
				}

				if (lifePolicy.getAgent() != null) {
					agentCommissioList = getAgentCommissionsForLifeBillCollection(lifePolicy, paymentList.get(0),
							extraValue);
				}

				int paymentTimes = (dto.getToTerm() - dto.getFromTerm()) + 1;
				Date calculatedEndDate = getCalculatedEndDate(paymentTimes, lifePolicy.getActivedPolicyEndDate(),
						lifePolicy.getPaymentType());
				lifePolicy.setActivedPolicyEndDate(calculatedEndDate);
				lifePolicy.setLastPaymentTerm(dto.getToTerm());

				if (extraValue != null && !extraValue.isPaid()) {
					extraValue.setPaid(true);
					shortEndowmentExtraValueService.updateShortEndowmentExtraValue(extraValue);
					lifePolicy.setHasExtraValue(false);
				}

				lifePolicyService.activateBillCollection(lifePolicy);
				paymentService.activatePaymentAndTLF(paymentList, agentCommissioList, branch, remark, salePoint);
			}
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			case MEDICAL_BILL_COLLECTION: {
				medicalPolicy = medicalPolicyService.findMedicalPolicyById(dto.getReferenceNo());
				List<Payment> paymentList = paymentService.findPaymentByReceiptNo(dto.getReceiptNo());
				List<AgentCommission> agentCommissioList = null;
				if (medicalPolicy.getAgent() != null) {
					agentCommissioList = getAgentCommissionsForMedicalBillCollection(medicalPolicy, paymentList.get(0),
							referenceType);
				}
				int paymentTimes = (dto.getToTerm() - dto.getFromTerm()) + 1;
				Date calculatedEndDate = getCalculatedEndDate(paymentTimes, medicalPolicy.getActivedPolicyEndDate(),
						medicalPolicy.getPaymentType());
				medicalPolicy.setActivedPolicyEndDate(calculatedEndDate);
				medicalPolicy.setLastPaymentTerm(dto.getToTerm());
				medicalPolicyService.activateBillCollection(medicalPolicy);
				paymentService.activatePaymentAndTLF(paymentList, agentCommissioList, branch, null, salePoint);
			}
				;
				break;
			default:
				break;
			}
		}
	}

	private Date getCalculatedEndDate(int paymentTimes, Date activePolicyEndDate, PaymentType paymentType) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(activePolicyEndDate);
		int month = paymentType == null ? 0 : paymentType.getMonth();
		calendar.add(Calendar.MONTH, month * paymentTimes);
		return calendar.getTime();
	}

	private List<AgentCommission> getAgentCommissionsForLifeBillCollection(LifePolicy lifePolicy, Payment payment,
			ShortEndowmentExtraValue extraValue) {
		List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
		String currencyCode = CurrencyUtils.getCurrencyCode(null);
		Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
		int paymentType = 0;
		double commissionPercent = 0.00;
		paymentType = lifePolicy.getPaymentType().getMonth();
		int toterm = payment.getToTerm();
		int fromterm = payment.getFromTerm();
		AgentCommissionEntryType entryType = null;
		double commission = 0.0;
		double rate = payment.getRate();
		double totalPremium = 0.0;
		int i = 0;
		int j = 0;
		int check = 12 / paymentType;
		j = toterm;
		boolean isExtraValueUsed = false;

		for (i = fromterm; i <= j; i++) {
			if (i <= check) {
				commissionPercent = product.getFirstCommission();
				entryType = AgentCommissionEntryType.UNDERWRITING;
			} else {
				commissionPercent = product.getRenewalCommission();
				entryType = AgentCommissionEntryType.RENEWAL;
			}

			if (commissionPercent > 0) {
				totalPremium = lifePolicy.getTotalBasicTermPremium() + lifePolicy.getTotalAddOnTermPremium();
				if (!isExtraValueUsed && extraValue != null && !extraValue.isPaid()) {
					totalPremium -= extraValue.getExtraAmount();
					isExtraValueUsed = true;
				}
				commission = Utils.getPercentOf(commissionPercent, totalPremium);
			}
			// agent commission is inserted according to each
			// payment term, not payment time
			// payment time can be many (eg; 3 to 5), but agent
			// commission will insert for 3, 4 and 5
			agentCommissionList.add(new AgentCommission(lifePolicy.getId(), payment.getReferenceType(),
					lifePolicy.getAgent(), commission, new Date(), payment.getReceiptNo(), totalPremium,
					commissionPercent, entryType, rate, rate * commission, currencyCode, totalPremium));
		}

		return agentCommissionList;
	}

	private List<AgentCommission> getAgentCommissionsForMedicalBillCollection(MedicalPolicy medicalPolicy,
			Payment payment, PolicyReferenceType referenceType) {
		List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
		String currencyCode = CurrencyUtils.getCurrencyCode(null);
		Product product = medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct();
		int paymentType = 0;
		double commissionPercent = 0.00;
		paymentType = medicalPolicy.getPaymentType().getMonth();
		int toterm = payment.getToTerm();
		int fromterm = payment.getFromTerm();
		AgentCommissionEntryType entryType = null;
		double commission = 0.0;
		double rate = payment.getRate();
		double totalPremium = 0.0;
		int i = 0;
		int j = 0;
		int check = 12 / paymentType;
		j = toterm;
		for (i = fromterm; i <= j; i++) {
			if (i <= check) {
				commissionPercent = product.getFirstCommission();
				entryType = AgentCommissionEntryType.UNDERWRITING;
			} else {
				commissionPercent = product.getRenewalCommission();
				entryType = AgentCommissionEntryType.RENEWAL;
			}

			if (commissionPercent > 0) {
				totalPremium = medicalPolicy.getTotalTermPremium();
				commission = Utils.getPercentOf(commissionPercent, totalPremium);
			}
			// agent commission is inserted according to each
			// payment term, not payment time
			// payment time can be many (eg; 3 to 5), but agent
			// commission will insert for 3, 4 and 5
			agentCommissionList.add(new AgentCommission(medicalPolicy.getId(), referenceType, medicalPolicy.getAgent(),
					commission, new Date(), payment.getReceiptNo(), totalPremium, commissionPercent, entryType, rate,
					rate * commission, currencyCode, totalPremium));
		}

		return agentCommissionList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentSkipTLF(List<PaymentDTO> paymentDTOList, PolicyReferenceType referenceType,
			User responsiblePerson, String remark, Branch branch, SalePoint salePoint) throws SystemException {
		LifeProposal lifeProposal;
		MedicalProposal medicalProposal;
		LifePolicy lifePolicy;
		MedicalPolicy medicalPolicy;
		ShortEndowmentExtraValue extraValue = null;

		for (PaymentDTO dto : paymentDTOList) {
			switch (referenceType) {
			case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
			case STUDENT_LIFE_POLICY:
			case LIFE_POLICY:
			case FARMER_POLICY: {
				lifeProposal = lifePolicyService.findLifePolicyById(dto.getReferenceNo()).getLifeProposal();
				PolicyReferenceType policyReferenceType = referenceType.equals(PolicyReferenceType.LIFE_POLICY)
						? PolicyReferenceType.LIFE_POLICY
						: referenceType.equals(PolicyReferenceType.STUDENT_LIFE_POLICY)
								? PolicyReferenceType.STUDENT_LIFE_POLICY
								: referenceType.equals(PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY)
										? PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY
										: referenceType.equals(
												PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY)
														? PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY
														: referenceType.equals(
																PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY)
																		? PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY
																		: PolicyReferenceType.FARMER_POLICY;
				WorkflowReferenceType proposalReferenceType = referenceType.equals(PolicyReferenceType.LIFE_POLICY)
						? WorkflowReferenceType.LIFE_PROPOSAL
						: WorkflowReferenceType.FARMER_PROPOSAL;
				List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), policyReferenceType,
						false);
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.ISSUING,
						proposalReferenceType, responsiblePerson, responsiblePerson);
				lifeProposalService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, branch, null);
				String currencyCode = CurrencyUtils.getCurrencyCode(null);
				paymentService.activateSkipPaymentAndTLF(paymentList, null, branch, currencyCode, salePoint);
			}
				break;

			case HEALTH_POLICY:
			case CRITICAL_ILLNESS_POLICY:
			case MICRO_HEALTH_POLICY:
			case MEDICAL_POLICY: {
				medicalProposal = medicalPolicyService.findMedicalPolicyById(dto.getReferenceNo()).getMedicalProposal();
				List<Payment> paymentList = paymentService.findByProposal(medicalProposal.getId(), referenceType,
						false);

				WorkflowTask workflowTask = null;
				WorkflowReferenceType wfReferenceType = null;

				switch (referenceType) {
				case HEALTH_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.HEALTH_PROPOSAL;
					break;
				case CRITICAL_ILLNESS_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.CRITICAL_ILLNESS_PROPOSAL;
					break;
				case MICRO_HEALTH_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.MICRO_HEALTH_PROPOSAL;
					break;
				case MEDICAL_POLICY:
					workflowTask = WorkflowTask.ISSUING;
					wfReferenceType = WorkflowReferenceType.MEDICAL_PROPOSAL;
					break;
				default:
					break;
				}

				WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), remark, workflowTask,
						wfReferenceType, responsiblePerson, responsiblePerson);
				medicalProposalService.paymentMedicalProposal(medicalProposal, workFlowDTO, paymentList, branch, null);
				String currencyCode = CurrencyUtils.getCurrencyCode(null);
				paymentService.activateSkipPaymentAndTLF(paymentList, null, branch, currencyCode, salePoint);
			}
				break;
			case SHORT_ENDOWMENT_LIFE_POLICY: {
				lifeProposal = lifePolicyService.findLifePolicyById(dto.getReferenceNo()).getLifeProposal();
				WorkflowReferenceType proposalReferenceType = WorkflowReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL;
				List<Payment> paymentList = paymentService.findByProposal(lifeProposal.getId(), referenceType, false);
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), remark, WorkflowTask.ISSUING,
						proposalReferenceType, responsiblePerson, responsiblePerson);
				lifeProposalService.paymentLifeProposal(lifeProposal, workFlowDTO, paymentList, branch, null);

				String currencyCode = CurrencyUtils.getCurrencyCode(null);
				paymentService.activateSkipPaymentAndTLF(paymentList, null, branch, currencyCode, salePoint);
			}
				break;
			case STUDENT_LIFE_POLICY_BILL_COLLECTION:
			case LIFE_BILL_COLLECTION:
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION: {
				lifePolicy = lifePolicyService.findLifePolicyById(dto.getReferenceNo());
				List<Payment> paymentList = paymentService.findPaymentByReceiptNo(dto.getReceiptNo());
				List<AgentCommission> agentCommissioList = null;

				if (lifePolicy.isHasExtraValue()) {
					extraValue = shortEndowmentExtraValueService
							.findShortEndowmentExtraValueByPolicyNo(lifePolicy.getPolicyNo());
				}

				if (lifePolicy.getAgent() != null) {
					agentCommissioList = getAgentCommissionsForLifeBillCollection(lifePolicy, paymentList.get(0),
							extraValue);
				}

				int paymentTimes = (dto.getToTerm() - dto.getFromTerm()) + 1;
				Date calculatedEndDate = getCalculatedEndDate(paymentTimes, lifePolicy.getActivedPolicyEndDate(),
						lifePolicy.getPaymentType());
				lifePolicy.setActivedPolicyEndDate(calculatedEndDate);
				lifePolicy.setLastPaymentTerm(dto.getToTerm());

				if (extraValue != null && !extraValue.isPaid()) {
					extraValue.setPaid(true);
					shortEndowmentExtraValueService.updateShortEndowmentExtraValue(extraValue);
					lifePolicy.setHasExtraValue(false);
				}

				lifePolicyService.activateBillCollection(lifePolicy);
				paymentService.activateSkipPaymentAndTLF(paymentList, agentCommissioList, branch, remark, salePoint);
			}
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			case MEDICAL_BILL_COLLECTION: {
				medicalPolicy = medicalPolicyService.findMedicalPolicyById(dto.getReferenceNo());
				List<Payment> paymentList = paymentService.findPaymentByReceiptNo(dto.getReceiptNo());
				List<AgentCommission> agentCommissioList = null;
				if (medicalPolicy.getAgent() != null) {
					agentCommissioList = getAgentCommissionsForMedicalBillCollection(medicalPolicy, paymentList.get(0),
							referenceType);
				}
				int paymentTimes = (dto.getToTerm() - dto.getFromTerm()) + 1;
				Date calculatedEndDate = getCalculatedEndDate(paymentTimes, medicalPolicy.getActivedPolicyEndDate(),
						medicalPolicy.getPaymentType());
				medicalPolicy.setActivedPolicyEndDate(calculatedEndDate);
				medicalPolicy.setLastPaymentTerm(dto.getToTerm());
				medicalPolicyService.activateBillCollection(medicalPolicy);
				paymentService.activateSkipPaymentAndTLF(paymentList, agentCommissioList, branch, null, salePoint);
			}
				;
				break;
			default:
				break;
			}
		}
	}
}
