
package org.ace.insurance.payment.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PaymentReferenceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.common.utils.BusinessUtils;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.insurance.life.claim.LifeClaimBeneficiary;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.migrate.ShortEndowmentExtraValue;
import org.ace.insurance.life.migrate.service.interfaces.IShortEndowmentExtraValueService;
import org.ace.insurance.life.paidUp.LifePaidUpProposal;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonKeyFactorValue;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.service.interfaces.IGroupMicroHealthService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.AgentPaymentCashReceiptDTO;
import org.ace.insurance.payment.CashDeno;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.PaymentOrderCashReceiptDTO;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.TLFBuilder;
import org.ace.insurance.payment.enums.DoubleEntry;
import org.ace.insurance.payment.persistence.interfacs.IAgentCommissionDAO;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.report.ClaimVoucher.ClaimVoucherDTO;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.insurance.report.TLF.TLFVoucherDTO;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.COACode;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.coinsurancecompany.service.interfaces.ICoinsuranceCompanyService;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.organization.service.interfaces.IOrganizationService;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.persistence.interfaces.ISalePointDAO;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PaymentTableDTO;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.agent.payment.StaffCommissionDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "PaymentService")
public class PaymentService extends BaseService implements IPaymentService {

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "CurrencyService")
	private ICurrencyService currencyService;

	@Resource(name = "LifePolicyService")
	private ILifePolicyService lifePolicyService;

	@Resource(name = "MedicalPolicyService")
	private IMedicalPolicyService medicalPolicyService;

	@Resource(name = "BranchService")
	private IBranchService branchService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowService;

	@Resource(name = "TravelProposalService")
	private ITravelProposalService travelProposalService;

	@Resource(name = "CustomerService")
	private ICustomerService customerService;

	@Resource(name = "OrganizationService")
	private IOrganizationService organizationService;

	@Resource(name = "CoinsuranceCompanyService")
	private ICoinsuranceCompanyService coinsuranceCompanyService;

	@Resource(name = "AgentCommissionDAO")
	private IAgentCommissionDAO agentCommissionDAO;

	@Resource(name = "PersonTravelProposalService")
	private IPersonTravelProposalService personTravelProposalService;

	@Resource(name = "PersonTravelPolicyService")
	private IPersonTravelPolicyService personTravelPolicyService;

	@Resource(name = "ShortEndowmentExtraValueService")
	private IShortEndowmentExtraValueService shortEndowmentExtraValueService;

	@Resource(name = "GroupfarmerProposalService")
	private IGroupfarmerProposalService groupFarmerProposalService;

	@Resource(name = "GroupMicroHealthService")
	private IGroupMicroHealthService groupMicroHealthService;

	@Resource(name = "SalePointDAO")
	private ISalePointDAO salePointDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> prePayment(List<Payment> paymentList) {
		try {
			String receiptNo = null;
			PaymentChannel channel = paymentList.get(0).getPaymentChannel();
			if (paymentList != null && paymentList.size() != 0) {
				if (channel.equals(PaymentChannel.TRANSFER)) {
					receiptNo = customIDGenerator.getNextId(SystemConstants.TRANSFER_RECEIPT_NO, null, true);
				} else if (channel.equals(PaymentChannel.CASHED)) {
					receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
				} else if (channel.equals(PaymentChannel.CHEQUE) || channel.equals(PaymentChannel.SUNDRY)) {
					receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
				}

				for (Payment payment : paymentList) {
					payment.setReceiptNo(receiptNo);
					payment.setPrefix(getPrefix(Payment.class));
					payment = paymentDAO.insert(payment);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> prePaymentAndTlfLifeClaim(List<Payment> paymentList, LifeClaimBeneficiary beneficiary, LifeClaim lifeClaim, double premium) {
		try {
			String customerId = beneficiary.getId();
			Branch branch = lifeClaim.getBranch();
			String receiptNo = null;
			String accountName = lifeClaim.getLifePolicy().getProductGroup().getAccountCode();
			Payment pay = paymentList.get(0);
			if (paymentList.get(0).getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.TRANSFER_RECEIPT_NO, null, true);
			} else if (paymentList.get(0).getPaymentChannel().equals(PaymentChannel.CASHED)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
			} else if (paymentList.get(0).getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
			}
			for (Payment payment : paymentList) {
				payment.setReceiptNo(receiptNo);
				payment.setPrefix(getPrefix(Payment.class));
				payment = paymentDAO.insert(payment);
			}
			addNewTLF_For_LIFECLAIM_Debit(pay, customerId, branch, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_LIFECLAIM_Credit(pay, customerId, branch, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_ServiceChargesDebit(pay, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_ServiceChargesCredit(pay, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
			if (premium > 0.0) {
				addNewTLF_For_LIFECLAIM_Premium_Debit(pay, premium, customerId, branch, CurrencyUtils.getCurrencyCode(null));
				addNewTLF_For_LIFECLAIM_Premium_Credit(pay, premium, customerId, branch, accountName, CurrencyUtils.getCurrencyCode(null));
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentAndTLFMedical(Payment payment, MedicalClaimProposalDTO medicalClaimProposalDTO) {
		String customerId = null;
		Branch branch = null;
		try {
			String receiptNo = null;
			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
			} else {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
			}
			payment.setConfirmDate(new Date());
			payment.setReceiptNo(receiptNo);
			payment = paymentDAO.insert(payment);

			customerId = medicalClaimProposalDTO.getPolicyInsuredPersonDTO().getCustomer().getId();
			branch = medicalClaimProposalDTO.getBranch();

			addNewTLF_For_LIFECLAIM_Debit(payment, customerId, branch, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_LIFECLAIM_Credit(payment, customerId, branch, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_ServiceChargesDebit(payment, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_ServiceChargesCredit(payment, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
	}

	// use by motor claim

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> preClaimPayment(List<Payment> paymentList) {
		try {
			for (Payment payment : paymentList) {
				payment.setPrefix(getPrefix(Payment.class));
				payment = paymentDAO.insert(payment);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findActivedRate() {
		double rate = 0.0;
		try {
			rate = paymentDAO.findActiveRate();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
		return rate;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> prePaymentForChalen(List<Payment> paymentList) {
		try {
			for (Payment payment : paymentList) {
				payment = paymentDAO.insert(payment);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> prePaymentForClaim(List<Payment> paymentList) {
		try {
			for (Payment payment : paymentList) {
				payment.setPrefix(getPrefix(Payment.class));
				payment = paymentDAO.insert(payment);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
		return paymentList;
	}

	/**
	 * This method will be called from Confirm Stage. Will Insert Data Into TLF
	 * and Payment as a pre stage.
	 * 
	 * @param accountPaymentList
	 * @param customerId
	 * @param branch
	 * @param agentCommissionList
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void preActivatePayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, List<AgentCommission> agentCommissionList, boolean isRenewal,
			String currencyCode, SalePoint salePoint) {

		try {
			String tlfNo = accountPaymentList.get(0).getPayment().getReceiptNo();
			// 1. Premium Debit
			addNewTLF_For_CashDebitForPremium(accountPaymentList, customerId, branch, tlfNo, isRenewal, currencyCode, salePoint);

			for (AccountPayment accountPayment : accountPaymentList) {
				Payment payment = accountPayment.getPayment();
				if (isCoInsurance(payment.getReferenceNo(), payment.getReferenceType())) {
					// Net Premium
					// 2. Income Narration : Being amount of ... or Accrued
					addNewTLF_For_CoInsuCashPaymentCredit(payment, customerId, branch, tlfNo, isRenewal, currencyCode);
					// Co-In Premium
					// 3. Income Contra: Our Share premium for
					// // 4. Cash - Being amount of ... or Accrued

					// For Co-Access Unauthorized
					if (!branch.isCoInsuAccess()) {
						// 5. Narration : Transfer from 'branch' ...
						addNewTLF_For_SundryDr(payment, customerId, branch, agentCommissionList, tlfNo, isRenewal, currencyCode);
						// 6. Narration: Transfer from 'branch' ...
						addNewTLF_For_InterBranchCr(payment, customerId, branch, agentCommissionList, tlfNo, isRenewal, currencyCode);
						// 7. Narration: Transfer from 'branch' ...
						addNewTLF_For_SundryCr(payment, customerId, branch, agentCommissionList, tlfNo, isRenewal, currencyCode);
						// 8. Narration: Transfer from 'branch' ...
						addNewTLF_For_InterBranchDr(payment, customerId, branch, agentCommissionList, tlfNo, isRenewal, currencyCode);
					}
				} else {
					// 9. Premium Credit
					addNewTLF_For_PremiumCredit(payment, customerId, branch, accountPayment.getAcccountName(), tlfNo, isRenewal, currencyCode, salePoint);
				}
			}

			String eNo = accountPaymentList.get(0).getPayment().getId();
			Payment paymentByIndex = accountPaymentList.get(0).getPayment();

			// Default SalePoint(Crstal Tower) will insert in agent invoice
			// SalePoint salePointForAgent = salePointDAO.findById("1");
			if (agentCommissionList != null && !agentCommissionList.isEmpty()) {
				// 10. Narration: Agent Commission Payable for . . . .
				addAgentCommissionTLF(agentCommissionList, branch, paymentByIndex, eNo, isRenewal, currencyCode, salePoint);
			}

			Payment payment = accountPaymentList.get(0).getPayment();
			// 11. Service Charges & Stamp Fees Debit
			addNewTLF_For_CashDebitForSCSTFees(payment, customerId, branch, tlfNo, isRenewal, currencyCode, salePoint);
			// 12. Service Charges & Stamp Fees Credit
			addNewTLF_For_SCSTFees(payment, customerId, branch, tlfNo, isRenewal, currencyCode, salePoint);
			// 13. Bank Charges Debit
			addNewTLF_For_BankChargesDebit(payment, customerId, branch, tlfNo, isRenewal, currencyCode, salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void preActivatePaymentForInterBranch(List<AccountPayment> accountPaymentList, String customerId, Branch userBranch, List<AgentCommission> agentCommissionList,
			boolean isRenewal, String currencyCode, SalePoint salePoint, Branch proposalBranch) {
		String tlfNo = accountPaymentList.get(0).getPayment().getReceiptNo();
		// Premium receivable Debit
		addNewTLF_For_CashDebitForPremiumForInterBranch(accountPaymentList, customerId, userBranch, tlfNo, isRenewal, currencyCode, salePoint, proposalBranch);

		// Premium Debit
		addNewTLF_For_CashDebitForPremium(accountPaymentList, customerId, userBranch, tlfNo, isRenewal, currencyCode, salePoint);

		for (AccountPayment accountPayment : accountPaymentList) {
			Payment payment = accountPayment.getPayment();

			// 9. Premium Payable Credit
			addNewTLF_For_PremiumCreditPayableForInterBranch(payment, customerId, userBranch, proposalBranch.getPayableACName(), tlfNo, isRenewal, currencyCode, salePoint,
					proposalBranch);

			// premium Credit
			addNewTLF_For_PremiumCreditInterBranch(payment, customerId, proposalBranch, accountPayment.getAcccountName(), tlfNo, isRenewal, currencyCode, salePoint);

		}

		if (agentCommissionList != null && !agentCommissionList.isEmpty()) {
			// 10. Narration: Agent Commission Payable for . . . .
			String eNo = accountPaymentList.get(0).getPayment().getId();
			Payment paymentByIndex = accountPaymentList.get(0).getPayment();
			addAgentCommissionTLF(agentCommissionList, proposalBranch, paymentByIndex, eNo, isRenewal, currencyCode, salePoint);
		}
		Payment payment = accountPaymentList.get(0).getPayment();
		// bank transfer charges
		addNewTLF_For_BankChargesDebit(payment, customerId, proposalBranch, tlfNo, isRenewal, currencyCode, salePoint);
	}

	/**
	 * This method will be called from Confirm Stage. Will Insert Data Into TLF
	 * and Payment as a pre stage.
	 * 
	 * @param accountPaymentList
	 * @param customerId
	 * @param branch
	 * @param agentCommissionList
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void preActivateEndorsementPayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String currencyCode, SalePoint salePoint) {

		try {
			String tlfNo = accountPaymentList.get(0).getPayment().getReceiptNo();
			Payment pay = accountPaymentList.get(0).getPayment();
			double totalNetPremium = 0;
			for (AccountPayment accountPayment : accountPaymentList) {
				totalNetPremium = totalNetPremium + accountPayment.getPayment().getNetPremium();
			}
			if (totalNetPremium > 0) {
				addNewTLF_For_CashDebitForPremiumEndorsement(accountPaymentList, customerId, branch, tlfNo, false, currencyCode, salePoint);
			} else if (totalNetPremium < 0 && !pay.getReferenceType().equals(PolicyReferenceType.LIFE_POLICY)) {
				addNewTLF_For_CashCreditForPremium(accountPaymentList, customerId, branch, true, tlfNo, false, false, currencyCode, salePoint);
			}
			for (AccountPayment accountPayment : accountPaymentList) {
				Payment payment = accountPayment.getPayment();
				if (payment.getNetPremium() > 0) {
					addNewTLF_For_PremiumCredit(payment, customerId, branch, accountPayment.getAcccountName(), tlfNo, false, currencyCode, salePoint);
				} else if (payment.getNetPremium() < 0 && !payment.getReferenceType().equals(PolicyReferenceType.LIFE_POLICY)) {
					addNewTLF_For_PremiumDebit(payment, customerId, branch, accountPayment.getAcccountName(), true, tlfNo, false, false, currencyCode, salePoint);
				}
			}
			addNewTLF_For_CashDebitForSCSTFees(pay, customerId, branch, tlfNo, false, currencyCode, salePoint);
			addNewTLF_For_SCSTFees(pay, customerId, branch, tlfNo, false, currencyCode, salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}

	}

	/**
	 * This method is used to activate Payment and TLF from 'payment stage'.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void activatePaymentAndTLF(List<Payment> paymentList, List<AgentCommission> agentCommissionList, Branch branch, String currencyCode, SalePoint salePoint) {
		try {
			/** update Payment **/
			for (Payment payment : paymentList) {
				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY))
					payment.setPO(true);
				payment.setComplete(true);
				payment.setPaymentDate(new Date());
				paymentDAO.update(payment);
			}

			/** update TLF **/
			List<TLF> tlfList = null;
			String policyNo = null;
			for (Payment payment : paymentList) {
				tlfList = findTLFbyTLFNo(payment.getReceiptNo(), false);
				policyNo = getPolicyNo(payment);
				for (TLF tlf : tlfList) {
					tlf.setPaid(true);
					tlf.setSettlementDate(new Date());
					tlf.setPaymentChannel(payment.getPaymentChannel());
					tlf.setPolicyNo(policyNo);
					tlf.getCommonCreateAndUpateMarks().setCreatedDate(new Date());
					if (salePoint != null)
						tlf.setSalePoint(salePoint);
					updateTLF(tlf);
				}
			}

			/** Add AgentCommission **/
			if (agentCommissionList != null && !agentCommissionList.isEmpty()) {
				addAgentCommission(agentCommissionList);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void activateSkipPaymentAndTLF(List<Payment> paymentList, List<AgentCommission> agentCommissionList, Branch branch, String currencyCode, SalePoint salePoint) {
		try {
			/** update Payment **/
			for (Payment payment : paymentList) {
				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY))
					payment.setPO(true);
				payment.setComplete(true);
				payment.setPaymentDate(payment.getConfirmDate());
				paymentDAO.update(payment);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	public String getPolicyNo(Payment payment) {
		String policyNo = null;
		IPolicy policy = null;
		switch (payment.getReferenceType()) {

			case LIFE_POLICY:
			case LIFE_BILL_COLLECTION:
			case PA_POLICY:
			case FARMER_POLICY:
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
				policy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
				policyNo = policy.getPolicyNo();
				break;
			case MEDICAL_POLICY:
			case MEDICAL_BILL_COLLECTION:
			case HEALTH_POLICY:
			case HEALTH_POLICY_BILL_COLLECTION:
			case MICRO_HEALTH_POLICY:
			case CRITICAL_ILLNESS_POLICY:
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				policy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
				policyNo = policy.getPolicyNo();
				break;

			case PERSON_TRAVEL_POLICY:
				policy = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
				policyNo = policy.getPolicyNo();
				break;
			case GROUP_FARMER_PROPOSAL:
				GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(payment.getReferenceNo());
				policyNo = proposal.getProposalNo();
				break;
			case GROUP_MICRO_HEALTH:
				GroupMicroHealth groupMicroHealth = groupMicroHealthService.findById(payment.getReferenceNo());
				policyNo = groupMicroHealth.getProposalNo();
				break;
			case TRAVEL_PROPOSAL:
				TravelProposal travelProposal = travelProposalService.findTravelProposalById(payment.getReferenceNo());
				policyNo = travelProposal.getProposalNo();
				break;
			default:
				break;
		}
		return policyNo;
	}

	/**
	 * This method is used to activate clearing for TLF from 'clearing stage'.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void activateTLFClearing(List<Payment> paymentList) {
		try {
			Date settlementDate = new Date();
			for (Payment payment : paymentList) {
				List<TLF> tlfList = findTLFbyTLFNo(payment.getReceiptNo(), true);
				for (TLF tlf : tlfList) {
					tlf.setPaid(true);
					tlf.setSettlementDate(settlementDate);
					updateTLF(tlf);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the tlf clearing", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private boolean isCoInsurance(String referenceNo, PolicyReferenceType referenceType) {
		boolean isCoInsurance = false;
		double maxSumInsured = 0.0;
		double totalSumInsured = 0.0;
		IPolicy policy = null;
		// TODO FIX PSH
		switch (referenceType) {

			default:
				break;
		}

		if (policy != null) {
			maxSumInsured = policy.getProductGroup().getMaxSumInsured();
			totalSumInsured = policy.getTotalSumInsured();
			isCoInsurance = (totalSumInsured > maxSumInsured) ? true : false;
		}

		return isCoInsurance;
	}

	/**
	 * This method is no longer in use since payment stage call the method named
	 * 'activatePaymentAndTLF()'.
	 * 
	 * **Remove this method when all the changes that need to be made are done.
	 * **
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void activatePayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, List<AgentCommission> agentCommissionList) {
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void activateClaimPayment(List<AccountPayment> accountPaymentList, Branch branch) {
		try {
			for (AccountPayment accountPayment : accountPaymentList) {
				Payment payment = accountPayment.getPayment();
				payment.setComplete(true);
				payment.setPaymentDate(new Date());
				payment.setReceiptNo(accountPayment.getPayment().getReceiptNo());
				payment = paymentDAO.update(payment);
			}
			// update TLF
			for (AccountPayment accountPayment : accountPaymentList) {
				List<TLF> tlfList = findTLFbyENONo(accountPayment.getPayment().getReceiptNo());
				for (TLF tlf : tlfList) {
					tlf.setPaid(true);
					tlf.setSettlementDate(new Date());
					updateTLF(tlf);
				}
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void activateEndorsementPayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String currencyCode, SalePoint salePoint) {
		try {
			Payment pay = accountPaymentList.get(0).getPayment();
			String tlfNo = pay.getReceiptNo();
			double totalNetPremium = 0;
			for (AccountPayment accountPayment : accountPaymentList) {
				totalNetPremium = totalNetPremium + accountPayment.getPayment().getNetPremium();
			}
			if (totalNetPremium > 0) {
				addNewTLF_For_CashDebitForPremium(accountPaymentList, customerId, branch, tlfNo, false, currencyCode, salePoint);
			} else if (totalNetPremium < 0 && !pay.getReferenceType().equals(PolicyReferenceType.LIFE_POLICY)) {
				addNewTLF_For_CashCreditForPremium(accountPaymentList, customerId, branch, true, tlfNo, false, false, currencyCode, salePoint);
			}
			for (AccountPayment accountPayment : accountPaymentList) {
				Payment payment = accountPayment.getPayment();
				if (payment.getNetPremium() > 0) {
					addNewTLF_For_PremiumCredit(payment, customerId, branch, accountPayment.getAcccountName(), tlfNo, false, currencyCode, salePoint);
				} else if (payment.getNetPremium() < 0 && !payment.getReferenceType().equals(PolicyReferenceType.LIFE_POLICY)) {
					addNewTLF_For_PremiumDebit(payment, customerId, branch, accountPayment.getAcccountName(), true, tlfNo, false, false, currencyCode, salePoint);
				}
			}
			addNewTLF_For_CashDebitForSCSTFees(pay, customerId, branch, tlfNo, false, currencyCode, salePoint);
			addNewTLF_For_SCSTFees(pay, customerId, branch, tlfNo, false, currencyCode, salePoint);
			// addCashDeno(payment, customerId, branch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private void addAgentCommission(List<AgentCommission> agentCommissionList, Branch branch, Payment payment, boolean isRenewal, String currencyCode, SalePoint salePoint) {
		try {
			for (AgentCommission agentCommission : agentCommissionList) {
				String prefix = customIDGenerator.getPrefix(AgentCommission.class, false);
				agentCommission.setPrefix(prefix);
				paymentDAO.insertAgentCommission(agentCommission);
				boolean coInsureance = isCoInsurance(agentCommission.getReferenceNo(), agentCommission.getReferenceType());
				addNewTLF_For_AgentCommissionDr(agentCommission, coInsureance, branch, payment, payment.getId(), isRenewal, currencyCode, salePoint);
				addNewTLF_For_AgentCommissionCr(agentCommission, coInsureance, branch, payment, payment.getId(), isRenewal, currencyCode, salePoint);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addAgentCommission(List<AgentCommission> agentCommissionList) {
		try {
			for (AgentCommission agentCommission : agentCommissionList) {
				String prefix = customIDGenerator.getPrefix(AgentCommission.class, false);
				agentCommission.setPrefix(prefix);
				paymentDAO.insertAgentCommission(agentCommission);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add agent commission No into TLF.", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addStaffAgentCommission(List<StaffAgentCommission> staffagentCommissionList) {
		try {
			for (StaffAgentCommission staffagentCommission : staffagentCommissionList) {
				String prefix = customIDGenerator.getPrefix(StaffAgentCommission.class, false);
				staffagentCommission.setPrefix(prefix);
				paymentDAO.insertStaffAgentCommission(staffagentCommission);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add agent commission No into TLF.", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addAgentCommissionTLF(List<AgentCommission> agentCommissionList, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			for (AgentCommission agentCommission : agentCommissionList) {
				boolean coInsureance = isCoInsurance(agentCommission.getReferenceNo(), agentCommission.getReferenceType());
				addNewTLF_For_AgentCommissionDr(agentCommission, coInsureance, branch, payment, eno, isRenewal, currencyCode, salePoint);
				addNewTLF_For_AgentCommissionCredit(agentCommission, coInsureance, branch, payment, eno, isRenewal, currencyCode, salePoint);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add agent Commission into TLF.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addStaffCommissionTLF(List<StaffAgentCommission> staffCommissionList, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			for (StaffAgentCommission staffCommission : staffCommissionList) {
				boolean coInsureance = isCoInsurance(staffCommission.getReferenceNo(), staffCommission.getReferenceType());
				addNewTLF_For_StaffCommissionDr(staffCommission, coInsureance, branch, payment, eno, isRenewal, currencyCode, salePoint);
				addNewTLF_For_StaffCommissionCredit(staffCommission, coInsureance, branch, payment, eno, isRenewal, currencyCode, salePoint);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add agent Commission into TLF.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_StaffCommissionDr(StaffAgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			String receiptNo = payment.getReceiptNo();
			String coaCode = null;
			String coaCodeMI = null;
			String accountName = null;
			double ownCommission = 0.0;
			Product product;
			TLF tlf = new TLF();
			// switch (ac.getReferenceType()) {
			//
			// case LIFE_POLICY:
			// case LIFE_BILL_COLLECTION:
			// LifePolicy lifePolicy =
			// lifePolicyService.findLifePolicyById(payment.getReferenceNo());
			// product =
			// lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			// // coaCode = ProductIDConfig.isFarmer(product) ?
			// // COACode.FARMER_AGENT_COMMISSION : coInsureance ?
			// // COACode.LIFE_SUNDRY : COACode.LIFE_AGENT_COMMISSION;
			// if (coInsureance) {
			// coaCode = COACode.LIFE_SUNDRY;
			// } else if (ProductIDConfig.isFarmer(product)) {
			// coaCode = COACode.FARMER_AGENT_COMMISSION;
			// } else if (ProductIDConfig.isGroupLife(product)) {
			// coaCode = COACode.GROUP_LIFE_AGENT_COMMISSION;
			// } else if (ProductIDConfig.isPublicLife(product)) {
			// coaCode = COACode.ENDOWMENT_LIFE_AGENT_COMMISSION;
			// } else if (ProductIDConfig.isSportMan(product)) {
			// coaCode = COACode.SPORTMAN_AGENT_COMMISSION;
			// } else if (ProductIDConfig.isSnakeBite(product)) {
			// coaCode = COACode.SNAKE_BITE_AGENT_COMMISSION;
			// } else {
			// coaCode = COACode.LIFE_AGENT_COMMISSION;
			// }
			// break;
			// case SNAKE_BITE_POLICY:
			// coaCode = COACode.SNAKE_BITE_AGENT_COMMISSION;
			// break;
			// case MEDICAL_POLICY:
			// case HEALTH_POLICY:
			// case MEDICAL_BILL_COLLECTION:
			// case HEALTH_POLICY_BILL_COLLECTION:
			// coaCode = COACode.HEALTH_AGENT_COMMISSION;
			// break;
			// case MICRO_HEALTH_POLICY:
			// coaCode = COACode.MICROHEALTH_AGENT_COMMISSION;
			// break;
			// case CRITICAL_ILLNESS_POLICY:
			// case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			// coaCode = COACode.CRITICAL_ILLNESS_AGENT_COMMISSION;
			// break;
			// // CARGO_POLICY is handled explicitly below
			//
			// case PA_POLICY:
			// coaCode = COACode.PA_AGENT_COMMISSION;
			// break;
			// case PERSON_TRAVEL_POLICY:
			// PersonTravelPolicy travelPolicy =
			// personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
			// product = travelPolicy.getProduct();
			// if (ProductIDConfig.isUnder100MileTravelInsurance(product)) {
			// coaCode = COACode.UNDER100_TRAVEL_AGENT_COMMISSION;
			// } else {
			// coaCode = COACode.GENERAL_TRAVEL_AGENT_COMMISSION;
			// }
			// break;
			// case PUBLIC_TERM_LIFE_POLICY:
			// coaCode = COACode.PUBLICTERMLIFE_AGENT_COMMISSION;
			// break;
			// case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
			// case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			// coaCode = COACode.SINGLE_PREMIUM_ENDOWMENT_AGENT_COMMISSION;
			// break;
			// case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			// case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			// coaCode = COACode.SINGLE_PREMIUM_CREDIT_AGENT_COMMISSION;
			// break;
			// case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			// case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			// coaCode =
			// COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_AGENT_COMMISSION;
			// break;
			// case GROUP_FARMER_PROPOSAL:
			// case FARMER_POLICY:
			// coaCode = COACode.FARMER_AGENT_COMMISSION;
			// break;
			//
			// case SHORT_ENDOWMENT_LIFE_POLICY:
			// case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
			// coaCode = COACode.SHORT_ENDOWMENT_AGENT_COMMISSION;
			// break;
			// case GROUP_MICRO_HEALTH:
			// coaCode = COACode.MICROHEALTH_AGENT_COMMISSION;
			// break;
			// case STUDENT_LIFE_POLICY:
			// case STUDENT_LIFE_POLICY_BILL_COLLECTION:
			// coaCode = COACode.STUDENT_LIFE_AGENT_COMMISSION;
			// break;
			// default:
			// break;
			// }

			coaCode = COACode.STAFF_BENEFITS;
			String cur = KeyFactorChecker.getKyatId();
			double rate = payment.getRate();
			String narration = getNarrationStaff(payment, ac, isRenewal);

			accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			ownCommission = Utils.getTwoDecimalPoint(ac.getCommission());
			// if
			// (ac.getReferenceType().equals(PolicyReferenceType.SNAKE_BITE_POLICY))
			// {
			// tlf.setPaid(true);
			// }

			String trantypeId = KeyFactorChecker.getTRDebit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, ownCommission, ac.getStaff().getId(), branch.getId(), accountName, receiptNo, narration, eno, ac.getReferenceNo(),
					ac.getReferenceType(), isRenewal, cur, rate);
			tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			tlf.setAgentTransaction(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_StaffCommissionCredit(StaffAgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal,
			String currencyCode, SalePoint salePoint) {
		try {
			String receiptNo = payment.getReceiptNo();
			String coaCode = null;
			String accountName = null;
			double commission = 0.0;

			// switch (ac.getReferenceType()) {
			// case LIFE_BILL_COLLECTION:
			// case LIFE_POLICY:
			// LifePolicy policy =
			// lifePolicyService.findLifePolicyById(payment.getReferenceNo());
			// Product product =
			// policy.getPolicyInsuredPersonList().get(0).getProduct();
			// // coaCode = ProductIDConfig.isFarmer(product) ?
			// // COACode.FARMER_AGENT_PAYABLE : coInsureance ?
			// // COACode.LIFE_CO_AGENT_PAYABLE :
			// // COACode.LIFE_AGENT_PAYABLE;
			// if (coInsureance) {
			// coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
			// } else if (ProductIDConfig.isFarmer(product)) {
			// coaCode = COACode.FARMER_AGENT_PAYABLE;
			// } else if (ProductIDConfig.isGroupLife(product)) {
			// coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
			// } else if (ProductIDConfig.isPublicLife(product)) {
			// coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
			// } else if (ProductIDConfig.isSportMan(product)) {
			// coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
			// } else if (ProductIDConfig.isSnakeBite(product)) {
			// coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
			// } else {
			// coaCode = COACode.LIFE_AGENT_PAYABLE;
			// }
			// break;
			// case SNAKE_BITE_POLICY:
			// coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
			// break;
			// case MEDICAL_POLICY:
			// case HEALTH_POLICY:
			// case HEALTH_POLICY_BILL_COLLECTION:
			// coaCode = COACode.HEALTH_AGENT_PAYABLE;
			// break;
			// case MEDICAL_BILL_COLLECTION:
			// coaCode = COACode.LIFE_AGENT_PAYABLE;
			// break;
			//
			// case CRITICAL_ILLNESS_POLICY:
			// case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
			// coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
			// break;
			// case MICRO_HEALTH_POLICY:
			// coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
			// break;
			// case PA_POLICY:
			// coaCode = COACode.PA_AGENT_PAYABLE;
			// break;
			// case PERSON_TRAVEL_POLICY:
			// coaCode = COACode.PERSON_TRAVEL_AGENT_PAYABLE;
			// break;
			// case GROUP_FARMER_PROPOSAL:
			// case FARMER_POLICY:
			// coaCode = COACode.FARMER_AGENT_PAYABLE;
			// break;
			// case SHORT_ENDOWMENT_LIFE_POLICY:
			// case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
			// coaCode = COACode.SHORT_ENDOWMENT_AGENT_PAYABLE;
			// break;
			// case GROUP_MICRO_HEALTH:
			// coaCode = COACode.MICROHEALTH_AGENT_COMMISSION;
			// break;
			// case STUDENT_LIFE_POLICY:
			// case STUDENT_LIFE_POLICY_BILL_COLLECTION:
			// coaCode = COACode.STUDENT_LIFE_AGENT_PAYABLE;
			// break;
			// case PUBLIC_TERM_LIFE_POLICY:
			// coaCode = COACode.PUBLICTERMLIFE_AGENT_PAYABLE;
			// break;
			// case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
			// case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
			// coaCode = COACode.SINGLE_PREMIUM_ENDOWMENT_AGENT_PAYABLE;
			// break;
			// case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			// case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			// coaCode = COACode.SINGLE_PREMIUM_CREDIT_AGENT_PAYABLE;
			// break;
			// case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
			// case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
			// coaCode = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_AGENT_PAYABLE;
			// break;
			// default:
			// break;
			// }

			coaCode = COACode.PAYABLE_TO_STAFF_BENEFITS;
			accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			commission = Utils.getTwoDecimalPoint(ac.getCommission());
			String narration = getNarrationStaff(payment, ac, isRenewal);
			String cur = KeyFactorChecker.getKyatId();
			double rate = payment.getRate();
			String trantypeId = KeyFactorChecker.getTRCredit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, commission, ac.getStaff().getId(), branch.getId(), accountName, receiptNo, narration, eno, ac.getReferenceNo(),
					ac.getReferenceType(), isRenewal, cur, rate);
			TLF tlf = tlfBuilder.getTLFInstance();
			// if
			// (ac.getReferenceType().equals(PolicyReferenceType.SNAKE_BITE_POLICY))
			// {
			// tlf.setPaid(true);
			// }

			setIDPrefixForInsert(tlf);
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			tlf.setAgentTransaction(true);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDTO> findPaymentByRecipNo(List<String> receiptList, PolicyReferenceType referenceType, Boolean complete) {
		List<PaymentDTO> result = null;
		try {
			result = paymentDAO.findByReceiptNo(receiptList, referenceType, complete);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by CahsReceiptNo : ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByPolicy(String policyId) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findByPolicy(policyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ReferenceNo : " + policyId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AgentCommission findAgentCommissionByReferenceNo(String referenceNo) {
		AgentCommission result = null;
		try {
			result = paymentDAO.findAgentCommissionByReferenceNo(referenceNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a AgentCommission by ReferenceNo : " + referenceNo, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findByProposal(proposalId, referenceType, complete);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PaymentList by Proposal Id : " + proposalId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByClaimProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findByClaimProposal(proposalId, referenceType, complete);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PaymentList by Proposal Id : " + proposalId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findClaimProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete) {
		Payment result = null;
		try {
			result = paymentDAO.findClaimProposal(proposalId, referenceType, complete);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PaymentList by Proposal Id : " + proposalId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentCommission> findAgentCommissionDetail(AgentCommissionDetailCriteria criteria) {
		List<AgentCommission> result = null;
		try {
			result = paymentDAO.find(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionDetailReport by criteria.", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<StaffAgentCommission> findStaffAgentCommissionDetail(StaffSanctionCriteria criteria) {
		List<StaffAgentCommission> result = null;
		try {
			result = paymentDAO.findStaffAgentCommission(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommissionDetailReport by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentCommissionDTO> findAgentCommissionForPayment() {
		List<AgentCommissionDTO> result = null;
		try {
			result = paymentDAO.findAgentCommissionForPayment();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommission For Payment", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentCommission> findAgentCommissionPaymentByAgent(String invoiceNo, String agentId) {
		List<AgentCommission> result = null;
		try {
			result = paymentDAO.findAgentCommissionPaymentByAgent(invoiceNo, agentId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommission For Payment By Agent", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<StaffAgentCommission> findStaffCommissionPaymentByAgent(String invoiceNo, String agentId) {
		List<StaffAgentCommission> result = null;
		try {
			result = paymentDAO.findStaffCommissionPaymentByAgent(invoiceNo, agentId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find AgentCommission For Payment By Agent", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findCheckOfAccountCode(String acccountName, Branch branch, String currencyCode) {
		String result = null;
		try {
			result = paymentDAO.findCheckOfAccountNameByCode(acccountName, branch.getId(), currencyCode);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find CheckOfAccountCode", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findCOAAccountNameByCode(String acccountCode) {
		String result = null;
		try {
			result = paymentDAO.findCOAAccountNameByCode(acccountCode);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to findCOAAccountNameByCode", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentAgentCommission(List<AgentCommission> commissionList, AgentCommissionDTO commissionDTO, Branch branch, String currencyCode) {

		try {
			SalePoint salePoint = salePointDAO.findById("1");
			LifePolicy lifePolicy = null;
			MedicalPolicy medicalPolicy = null;
			PersonTravelPolicy personTravelPolicy = null;
			GroupFarmerProposal groupFarmerproposal = null;
			Branch policyBranch = null;
			for (AgentCommission comm : commissionList) {
				comm.setBank(commissionDTO.getBank());
				comm.setChequeNo(commissionDTO.getChequeNo());
				comm.setPaymentChannel(commissionDTO.getPaymentChannel());
				comm.setStatus(true);
				lifePolicy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
				medicalPolicy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
				personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyById(comm.getReferenceNo());
				groupFarmerproposal = groupFarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
				if (lifePolicy != null) {
					policyBranch = lifePolicy.getBranch();
				} else if (medicalPolicy != null) {
					policyBranch = medicalPolicy.getBranch();
				} else if (personTravelPolicy != null) {
					policyBranch = personTravelPolicy.getBranch();
				} else if (groupFarmerproposal != null) {
					policyBranch = groupFarmerproposal.getBranch();
				}
				paymentDAO.paymentAgentCommission(comm);

				boolean isInterBranch = false;

				if (policyBranch.getId().equals(branch.getId())) {
					addNewTLF_For_AGENT_COMMISSION_CREDIT(comm, branch, currencyCode, salePoint);
					addNewTLF_For_AGENT_COMMISSION_DEBIT(comm, branch, currencyCode, salePoint, isInterBranch);
				} else {
					// For Inter Branch
					isInterBranch = true;
					addNewTLF_For_AGENT_COMMISSION_CREDIT_FOR_INTERBRANCH(comm, branch, currencyCode, salePoint, policyBranch);// CCV
					addNewTLF_For_AGENT_COMMISSION_DEBIT_FOR_INTERBRACH(comm, policyBranch, currencyCode, salePoint, branch);// TDV

					addNewTLF_For_AGENT_COMMISSION_CREDIT(comm, branch, currencyCode, salePoint);// CCV
					addNewTLF_For_AGENT_COMMISSION_DEBIT(comm, policyBranch, currencyCode, salePoint, isInterBranch);// TDV
				}
				if (comm.getWithHoldingTax() > 0) {
					addNewTLF_For_AGENT_COMMISSION_TAX_CREDIT(comm, branch, currencyCode);
					addNewTLF_For_AGENT_COMMISSION_TAX_DEBIT(comm, branch, currencyCode);
				}
				workFlowDTOService.deleteWorkFlowByRefNo(comm.getId());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update AgentCommission After Payment", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentStaffCommission(List<StaffAgentCommission> commissionList, StaffCommissionDTO commissionDTO, Branch branch, String currencyCode) {

		try {
			SalePoint salePoint = salePointDAO.findById("1");
			LifePolicy lifePolicy = null;
			MedicalPolicy medicalPolicy = null;
			PersonTravelPolicy personTravelPolicy = null;
			GroupFarmerProposal groupFarmerproposal = null;
			Branch policyBranch = null;
			for (StaffAgentCommission comm : commissionList) {
				comm.setBank(commissionDTO.getBank());
				comm.setChequeNo(commissionDTO.getChequeNo());
				comm.setPaymentChannel(commissionDTO.getPaymentChannel());
				comm.setStatus(true);
				lifePolicy = lifePolicyService.findLifePolicyById(comm.getReferenceNo());
				medicalPolicy = medicalPolicyService.findMedicalPolicyById(comm.getReferenceNo());
				personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyById(comm.getReferenceNo());
				groupFarmerproposal = groupFarmerProposalService.findGroupFarmerById(comm.getReferenceNo());
				if (lifePolicy != null) {
					policyBranch = lifePolicy.getBranch();
				} else if (medicalPolicy != null) {
					policyBranch = medicalPolicy.getBranch();
				} else if (personTravelPolicy != null) {
					policyBranch = personTravelPolicy.getBranch();
				} else if (groupFarmerproposal != null) {
					policyBranch = groupFarmerproposal.getBranch();
				}
				paymentDAO.paymentStaffCommission(comm);

				boolean isInterBranch = false;

				if (policyBranch.getId().equals(branch.getId())) {
					addNewTLF_For_STAFF_COMMISSION_CREDIT(comm, branch, currencyCode, salePoint);
					addNewTLF_For_STAFF_COMMISSION_DEBIT(comm, branch, currencyCode, salePoint, isInterBranch);
				} else {
					// For Inter Branch
					isInterBranch = true;
					addNewTLF_For_STAFF_COMMISSION_CREDIT_FOR_INTERBRANCH(comm, branch, currencyCode, salePoint, policyBranch);// CCV
					addNewTLF_For_STAFF_COMMISSION_DEBIT_FOR_INTERBRACH(comm, policyBranch, currencyCode, salePoint, branch);// TDV

					addNewTLF_For_STAFF_COMMISSION_CREDIT(comm, branch, currencyCode, salePoint);// CCV
					addNewTLF_For_STAFF_COMMISSION_DEBIT(comm, policyBranch, currencyCode, salePoint, isInterBranch);// TDV
				}
				if (comm.getWithHoldingTax() > 0) {
					addNewTLF_For_STAFF_COMMISSION_TAX_CREDIT(comm, branch, currencyCode);
					addNewTLF_For_STAFF_COMMISSION_TAX_DEBIT(comm, branch, currencyCode);
				}
				workFlowDTOService.deleteWorkFlowByRefNo(comm.getId());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update AgentCommission After Payment", e);
		}
	}

	// Agent Commission payable for Fire Insurance(Product Name), Received No to
	// Agent Name for commission amount of Amount
	private String getNarrationAgent(Payment payment, AgentCommission agentCommission, boolean isRenewal) {
		StringBuffer nrBf = new StringBuffer();
		double commission = 0.0;
		String agentName = "";
		String insuranceName = "";
		// Agent Commission payable for Fire Insurance(Product Name), Received
		// No to Agent Name for commission amount of Amount
		nrBf.append("Agent Commission payable for ");
		switch (payment.getReferenceType()) {

			case LIFE_POLICY:
				insuranceName = "Life Insurance, ";
				break;
			case LIFE_BILL_COLLECTION:
				insuranceName = "Life Bill collection, ";
				break;

			case MEDICAL_POLICY:
				insuranceName = "Medical Insurance, ";
				break;
			case HEALTH_POLICY:
				insuranceName = "Health Insurance, ";
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				insuranceName = "Health Bill collection, ";
				break;
			case MICRO_HEALTH_POLICY:
				insuranceName = "Micro Health Insurance, ";
				break;
			case CRITICAL_ILLNESS_POLICY:
				insuranceName = "Critical Illness Insurance, ";
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				insuranceName = "Critical Illness Bill collection, ";
				break;

			case PA_POLICY:
				insuranceName = "Personal Accident Insurance, ";
				break;

			case STUDENT_LIFE_POLICY:
				insuranceName = "Student Life Insurance, ";
				break;
			case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				insuranceName = "Single Premium Credit Life Insurance, ";
				break;
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				insuranceName = "Short Term Single Premium Credit Life Insurance, ";
				break;
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				insuranceName = "Single Premium Endowment Life Insurance, ";
				break;
			case STUDENT_LIFE_POLICY_BILL_COLLECTION:
				insuranceName = "Student Life Bill collection, ";
				break;
			case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				insuranceName = "Single Premium Credit Life Bill collection, ";
				break;
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				insuranceName = "Short Term Single Premium Credit Life Bill collection, ";
				break;
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
				insuranceName = "Single Premium Endowment Life Bill collection, ";
				break;
			default:
				break;
		}

		agentName = agentCommission.getAgent() == null ? "" : agentCommission.getAgent().getFullName();
		commission = agentCommission.getCommission();
		nrBf.append(insuranceName);
		nrBf.append(payment.getReceiptNo());
		nrBf.append(" to ");
		nrBf.append(agentName);
		nrBf.append(" for commission amount of ");
		nrBf.append(Utils.getCurrencyFormatString(commission));
		nrBf.append(".");

		return nrBf.toString();
	}

	private String getNarrationStaff(Payment payment, StaffAgentCommission staffCommission, boolean isRenewal) {
		StringBuffer nrBf = new StringBuffer();
		double commission = 0.0;
		String agentName = "";
		String insuranceName = "";
		// Agent Commission payable for Fire Insurance(Product Name), Received
		// No to Agent Name for commission amount of Amount
		nrBf.append("Staff Commission payable for ");
		switch (payment.getReferenceType()) {

			case LIFE_POLICY:
				insuranceName = "Life Insurance, ";
				break;
			case LIFE_BILL_COLLECTION:
				insuranceName = "Life Bill collection, ";
				break;

			case MEDICAL_POLICY:
				insuranceName = "Medical Insurance, ";
				break;
			case HEALTH_POLICY:
				insuranceName = "Health Insurance, ";
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				insuranceName = "Health Bill collection, ";
				break;
			case MICRO_HEALTH_POLICY:
				insuranceName = "Micro Health Insurance, ";
				break;
			case CRITICAL_ILLNESS_POLICY:
				insuranceName = "Critical Illness Insurance, ";
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				insuranceName = "Critical Illness Bill collection, ";
				break;

			case PA_POLICY:
				insuranceName = "Personal Accident Insurance, ";
				break;

			case STUDENT_LIFE_POLICY:
				insuranceName = "Student Life Insurance, ";
				break;
			case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				insuranceName = "Single Premium Credit Life Insurance, ";
				break;
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				insuranceName = "Short Term Single Premium Credit Life Insurance, ";
				break;
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				insuranceName = "Single Premium Endowment Life Insurance, ";
				break;
			case STUDENT_LIFE_POLICY_BILL_COLLECTION:
				insuranceName = "Student Life Bill collection, ";
				break;
			case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				insuranceName = "Single Premium Credit Life Bill collection, ";
				break;
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				insuranceName = "Short Term Single Premium Credit Life Bill collection, ";
				break;
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
				insuranceName = "Single Premium Endowment Life Bill collection, ";
				break;
			default:
				break;
		}

		agentName = staffCommission.getStaff() == null ? "" : staffCommission.getStaff().getFullName();
		commission = staffCommission.getCommission();
		nrBf.append(insuranceName);
		nrBf.append(payment.getReceiptNo());
		nrBf.append(" to ");
		nrBf.append(agentName);
		nrBf.append(" for commission amount of ");
		nrBf.append(Utils.getCurrencyFormatString(commission));
		nrBf.append(".");

		return nrBf.toString();
	}

	private String getNarrationInterBranch(Payment payment, String branch) {
		StringBuffer nrBf = new StringBuffer();
		// Transfer from 'branch' for co-insurance receipt No 'receiptNo'
		nrBf.append("Transfer from ");
		nrBf.append(branch);
		nrBf.append(" for Co-Insurance Receipt No ");
		nrBf.append(payment.getReceiptNo());

		return nrBf.toString();
	}

	private String getNarrationCoInsuIncome(Payment payment, boolean isRenewal, double totalNetPremium) {
		IPolicy policy = null;
		switch (payment.getReferenceType()) {

			case LIFE_POLICY:
			case PA_POLICY:
				policy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
				break;
			case LIFE_BILL_COLLECTION:
				policy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
				break;

			default:
				break;
		}
		// Our Share premium for Suminsured SI (less Agent Commission)
		// Received by Received No From Customer name with the premium amoutn of
		// Premium Amount
		StringBuffer nrBf = new StringBuffer();
		if (isRenewal) {
			nrBf.append("Our Share renewal premium for Suminsured ");
		} else {
			nrBf.append("Our Share premium for Suminsured ");
		}
		nrBf.append(Utils.getCurrencyFormatString(policy.getTotalSumInsured()));
		nrBf.append(" (less Agent Commission) received by ");
		nrBf.append(payment.getReceiptNo());
		nrBf.append(" from ");
		nrBf.append(policy.getCustomerName());
		nrBf.append(" with the premium amount of ");
		nrBf.append(totalNetPremium);
		nrBf.append(". ");

		return nrBf.toString();
	}

	private String getNarrationCoInsuPayment(Payment payment, boolean isRenewal, double totalNetpremium) {
		StringBuffer nrBf = new StringBuffer();
		String customerName = "";
		String premiumString = "";
		double si = 0.0;
		double premium = 0.0;

		if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
			nrBf.append("Accrued");
			switch (payment.getReferenceType()) {

				case LIFE_POLICY:
					premiumString = " Life Premium  Co-Insurance";
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					break;
				case LIFE_BILL_COLLECTION:
					premiumString = " Life Bill Collection ";
					LifePolicy lifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifeBillPolicy.getTotalSumInsured();
					premium = lifeBillPolicy.getTotalPremium();
					customerName = lifeBillPolicy.getCustomerName();
					break;
				case PA_POLICY:
					premiumString = " Personal Accident Premium  Co-Insurance";
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append("to be paid by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			nrBf.append(" for Sum Insured ");
			nrBf.append(Utils.getCurrencyFormatString(si));
			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(totalNetpremium));
			nrBf.append(". ");
		} else {

			nrBf.append("Being amount of ");
			switch (payment.getReferenceType()) {

				case LIFE_POLICY:
					premiumString = "Life Premium Co-Insurance";
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					break;

				case PA_POLICY:
					premiumString = "Personal Accident Premium Co-Insurance";
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append(" received by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			nrBf.append(" for Sum Insured ");
			nrBf.append(Utils.getCurrencyFormatString(si));
			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(totalNetpremium));
			nrBf.append(". ");
		}

		return nrBf.toString();
	}

	private String getNarrationPremium(Payment payment, boolean isRenewal) {
		StringBuffer nrBf = new StringBuffer();
		String customerName = "";
		String premiumString = "";
		int totalInsuredPerson = 0;
		double si = 0.0;
		String unit = "";
		double premium = 0.0;

		if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
			nrBf.append("Accrued");
			switch (payment.getReferenceType()) {
				case FARMER_POLICY:
				case LIFE_POLICY:
					premiumString = " Life Premium ";
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = (lifePolicy.isEndorsementApplied()) ? lifePolicy.getTotalEndorsementPremium() : lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					totalInsuredPerson = lifePolicy.getInsuredPersonInfo().size() > 1 ? lifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case LIFE_BILL_COLLECTION:
					premiumString = " Life Bill Collection ";
					LifePolicy lifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifeBillPolicy.getTotalSumInsured();
					premium = lifeBillPolicy.getTotalPremium();
					customerName = lifeBillPolicy.getCustomerName();
					break;
				case PA_POLICY:
					premiumString = " Personal Accident Premium ";
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = (paPolicy.isEndorsementApplied()) ? paPolicy.getTotalEndorsementPremium() : paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;

				case MEDICAL_POLICY:
					premiumString = "Old Health Premium ";
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalPolicy.getPremium();
					customerName = medicalPolicy.getCustomerName();
					break;
				case HEALTH_POLICY:
					premiumString = "Health Premium ";
					MedicalPolicy healthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthPolicy.getPremium();
					customerName = healthPolicy.getCustomerName();
					break;
				case MICRO_HEALTH_POLICY:
					premiumString = "Micro Health Premium ";
					MedicalPolicy microHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = microHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = microHealthPolicy.getPremium();
					customerName = microHealthPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY:
					premiumString = "Critical Health Premium ";
					MedicalPolicy criticalHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = criticalHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = criticalHealthPolicy.getPremium();
					customerName = criticalHealthPolicy.getCustomerName();
					break;
				case MEDICAL_BILL_COLLECTION:
					premiumString = "Old Health Bill Collection Premium ";
					MedicalPolicy medicalBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalBillPolicy.getPremium();
					customerName = medicalBillPolicy.getCustomerName();
					break;
				case HEALTH_POLICY_BILL_COLLECTION:
					premiumString = "Health Bill Collection Premium ";
					MedicalPolicy healthBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthBillPolicy.getPremium();
					customerName = healthBillPolicy.getCustomerName();
					break;
				// case MICRO_HEALTH_POLICY_BILL_COLLECTION:
				// premiumString = "Micro Health Bill Collection Premium ";
				// MedicalPolicy microHealthBillPolicy =
				// medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
				// unit = microHealthBillPolicy.getTotalUnit() + " Unit(s) ";
				// premium = microHealthBillPolicy.getPremium();
				// customerName = microHealthBillPolicy.getCustomerName();
				// break;
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					premiumString = "Critical Illness Bill Collection Premium ";
					MedicalPolicy criticalIllnessBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = criticalIllnessBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = criticalIllnessBillPolicy.getPremium();
					customerName = criticalIllnessBillPolicy.getCustomerName();
					break;

				case PERSON_TRAVEL_POLICY:
					premiumString = " Personal Travel Premium ";
					PersonTravelPolicy personTravel = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
					unit = personTravel.getPersonTravelPolicyInfo().getTotalUnit() + " Unit(s) ";
					premium = personTravel.getPersonTravelPolicyInfo().getPremium();
					customerName = personTravel.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					premiumString = " Short Term Endowment Life Premium ";
					LifePolicy seLifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = seLifePolicy.getTotalSumInsured();
					premium = seLifePolicy.getTotalPremium();
					customerName = seLifePolicy.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = " Short Term Endowment Life Bill Collection ";
					LifePolicy seLifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = seLifeBillPolicy.getTotalSumInsured();
					premium = seLifeBillPolicy.getTotalPremium();
					customerName = seLifeBillPolicy.getCustomerName();
					break;
				case GROUP_FARMER_PROPOSAL:
					premiumString = " Life Premium ";
					GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(payment.getReferenceNo());
					si = proposal.getTotalSI();
					premium = proposal.getPremium();
					totalInsuredPerson = proposal.getNoOfInsuredPerson();
					customerName = proposal.getCustomerName();
					break;
				case STUDENT_LIFE_POLICY:
					premiumString = "Student Life Premium ";
					LifePolicy studentPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentPolicy.getTotalSumInsured();
					premium = (studentPolicy.isEndorsementApplied()) ? studentPolicy.getTotalEndorsementPremium() : studentPolicy.getTotalPremium();
					customerName = studentPolicy.getCustomerName();
					break;
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					premiumString = " Student Life Bill Collection ";
					LifePolicy studentBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentBillPolicy.getTotalSumInsured();
					premium = studentBillPolicy.getTotalPremium();
					customerName = studentBillPolicy.getCustomerName();
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					premiumString = " Public Term Life Life Premium ";
					LifePolicy pLifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = pLifePolicy.getTotalSumInsured();
					premium = pLifePolicy.getTotalPremium();
					customerName = pLifePolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					premiumString = "Single Premium Credit Life Premium ";
					LifePolicy singlePremiumCreditPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = singlePremiumCreditPolicy.getTotalSumInsured();
					premium = (singlePremiumCreditPolicy.isEndorsementApplied()) ? singlePremiumCreditPolicy.getTotalEndorsementPremium()
							: singlePremiumCreditPolicy.getTotalPremium();
					customerName = singlePremiumCreditPolicy.getCustomerName();
					totalInsuredPerson = singlePremiumCreditPolicy.getInsuredPersonInfo().size() > 1 ? singlePremiumCreditPolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = "Single Premium Credit Life Bill Collection ";
					LifePolicy singlePremiumCreditBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = singlePremiumCreditBillPolicy.getTotalSumInsured();
					premium = singlePremiumCreditBillPolicy.getTotalPremium();
					customerName = singlePremiumCreditBillPolicy.getCustomerName();
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					premiumString = "Short Term Single Premium Credit Life Premium ";
					LifePolicy shortTermSinglePremiumCreditPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = shortTermSinglePremiumCreditPolicy.getTotalSumInsured();
					premium = (shortTermSinglePremiumCreditPolicy.isEndorsementApplied()) ? shortTermSinglePremiumCreditPolicy.getTotalEndorsementPremium()
							: shortTermSinglePremiumCreditPolicy.getTotalPremium();
					customerName = shortTermSinglePremiumCreditPolicy.getCustomerName();
					totalInsuredPerson = shortTermSinglePremiumCreditPolicy.getInsuredPersonInfo().size() > 1 ? shortTermSinglePremiumCreditPolicy.getInsuredPersonInfo().size()
							: 0;
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = "Short Term Single Premium Credit Life Bill Collection ";
					LifePolicy shortTermSinglePremiumCreditBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = shortTermSinglePremiumCreditBillPolicy.getTotalSumInsured();
					premium = shortTermSinglePremiumCreditBillPolicy.getTotalPremium();
					customerName = shortTermSinglePremiumCreditBillPolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					premiumString = "Single Premium Endowment Life Premium ";
					LifePolicy singlePremiumEndowmentPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = singlePremiumEndowmentPolicy.getTotalSumInsured();
					premium = (singlePremiumEndowmentPolicy.isEndorsementApplied()) ? singlePremiumEndowmentPolicy.getTotalEndorsementPremium()
							: singlePremiumEndowmentPolicy.getTotalPremium();
					customerName = singlePremiumEndowmentPolicy.getCustomerName();
					totalInsuredPerson = singlePremiumEndowmentPolicy.getInsuredPersonInfo().size() > 1 ? singlePremiumEndowmentPolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = "Single Premium Endowment Life Bill Collection ";
					LifePolicy singlePremiumEndowmentBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = singlePremiumEndowmentBillPolicy.getTotalSumInsured();
					premium = singlePremiumEndowmentBillPolicy.getTotalPremium();
					customerName = singlePremiumEndowmentBillPolicy.getCustomerName();
					break;
				case SIMPLE_LIFE_POLICY:
					premiumString = "Simple Life Premium  ";
					LifePolicy simplelifepolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = simplelifepolicy.getTotalSumInsured();
					premium = simplelifepolicy.getTotalPremium();
					customerName = simplelifepolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append("to be paid by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			if (payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION)
					|| payment.getReferenceType().equals(PolicyReferenceType.MICRO_HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION)
					|| PolicyReferenceType.PERSON_TRAVEL_POLICY.equals(payment.getReferenceType())) {
				nrBf.append(" for ");
				nrBf.append(unit);
			} else {
				nrBf.append(" for Sum Insured ");
				nrBf.append(Utils.getCurrencyFormatString(si));
			}

			if (totalInsuredPerson > 0
					&& (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(payment.getReferenceType()) || PolicyReferenceType.LIFE_POLICY.equals(payment.getReferenceType()))) {
				nrBf.append(" and for total number of insured person ");
				nrBf.append(Integer.toString(totalInsuredPerson));
			}

			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(premium));
			nrBf.append(". ");

		} else {
			nrBf.append("Being amount of ");
			switch (payment.getReferenceType()) {
				case FARMER_POLICY:
				case LIFE_POLICY:
					nrBf.append(" Life Premium ");
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = (lifePolicy.isEndorsementApplied()) ? lifePolicy.getTotalEndorsementPremium() : lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					totalInsuredPerson = lifePolicy.getInsuredPersonInfo().size() > 1 ? lifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case LIFE_BILL_COLLECTION:
					premiumString = " Life Bill Collection ";
					LifePolicy lifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifeBillPolicy.getTotalSumInsured();
					premium = (lifeBillPolicy.isEndorsementApplied()) ? lifeBillPolicy.getTotalEndorsementPremium() : lifeBillPolicy.getTotalPremium();
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					nrBf.append(" Short Term Endowment Life Premium ");
					LifePolicy selifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = selifePolicy.getTotalSumInsured();
					premium = selifePolicy.getTotalPremium();
					customerName = selifePolicy.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					nrBf.append(" Short Term Endowment Life Bill Collection ");
					LifePolicy selifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = selifeBillPolicy.getTotalSumInsured();
					premium = selifeBillPolicy.getTotalPremium();
					customerName = selifeBillPolicy.getCustomerName();
					break;
				case MEDICAL_POLICY:
					nrBf.append(" Medical Premium ");
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalPolicy.getTotalPremium();
					customerName = medicalPolicy.getCustomerName();
					break;
				case MEDICAL_BILL_COLLECTION:
					nrBf.append(" Medical Bill Collection Premium ");
					MedicalPolicy medicalBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalBillPolicy.getTotalPremium();
					customerName = medicalBillPolicy.getCustomerName();
					break;
				case HEALTH_POLICY:
					nrBf.append(" Health Premium ");
					MedicalPolicy healthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthPolicy.getTotalPremium();
					customerName = healthPolicy.getCustomerName();
					break;
				case HEALTH_POLICY_BILL_COLLECTION:
					nrBf.append(" Health Bill Collection Premium ");
					MedicalPolicy healthBillPolicy1 = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthBillPolicy1.getTotalUnit() + " Unit(s) ";
					premium = healthBillPolicy1.getTotalPremium();
					customerName = healthBillPolicy1.getCustomerName();
					break;
				case MICRO_HEALTH_POLICY:
					nrBf.append(" Micro Health Premium ");
					MedicalPolicy microHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = microHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = microHealthPolicy.getTotalPremium();
					customerName = microHealthPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY:
					nrBf.append(" Critical Illness Premium ");
					MedicalPolicy ciPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = ciPolicy.getTotalUnit() + " Unit(s) ";
					premium = ciPolicy.getTotalPremium();
					customerName = ciPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					nrBf.append(" Critical Illness Bill Collection Premium ");
					MedicalPolicy ciBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = ciBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = ciBillPolicy.getTotalPremium();
					customerName = ciBillPolicy.getCustomerName();
					break;

				case PA_POLICY:
					nrBf.append(" Personal Accident Premium ");
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = (paPolicy.isEndorsementApplied()) ? paPolicy.getTotalEndorsementPremium() : paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;

				case TRAVEL_PROPOSAL:
					// Travel Narration does not like others, so return from
					// code.
					TravelProposal travelProposal = travelProposalService.findTravelProposalById(payment.getReferenceNo());
					StringBuffer narration = new StringBuffer();
					String fromDate = Utils.getDateFormatString(travelProposal.getFromDate());
					String toDate = Utils.getDateFormatString(travelProposal.getToDate());
					narration.append("Premium Income from ");
					narration.append(travelProposal.getExpressList().size());
					narration.append(" Gates collected for ");
					narration.append(travelProposal.getTotalPassenger() + " * 270 = ");
					narration.append(travelProposal.getTotalNetPremium());
					narration.append(" Per attached period on ");
					narration.append(fromDate + " to " + toDate);
					return narration.toString();
				case PERSON_TRAVEL_POLICY:
					nrBf.append(" Person Travel Premium ");
					PersonTravelPolicy personTravel = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
					unit = personTravel.getPersonTravelPolicyInfo().getTotalUnit() + " Unit(s) ";
					premium = personTravel.getPersonTravelPolicyInfo().getPremium();
					customerName = personTravel.getCustomerName();
					break;
				case GROUP_FARMER_PROPOSAL:
					nrBf.append(" Life Premium ");
					GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(payment.getReferenceNo());
					si = proposal.getTotalSI();
					premium = proposal.getPremium();
					customerName = proposal.getCustomerName();
					totalInsuredPerson = proposal.getNoOfInsuredPerson();
					break;
				case STUDENT_LIFE_POLICY:
					nrBf.append(" Life Premium ");
					LifePolicy studentPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentPolicy.getTotalSumInsured();
					premium = (studentPolicy.isEndorsementApplied()) ? studentPolicy.getTotalEndorsementPremium() : studentPolicy.getTotalPremium();
					customerName = studentPolicy.getCustomerName();
					totalInsuredPerson = studentPolicy.getInsuredPersonInfo().size() > 1 ? studentPolicy.getInsuredPersonInfo().size() : 0;
					break;
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					premiumString = "Student Life Bill Collection ";
					LifePolicy studentBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentBillPolicy.getTotalSumInsured();
					premium = (studentBillPolicy.isEndorsementApplied()) ? studentBillPolicy.getTotalEndorsementPremium() : studentBillPolicy.getTotalPremium();
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					premiumString = " Public Term Life Life Premium ";
					LifePolicy pLifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = pLifePolicy.getTotalSumInsured();
					premium = pLifePolicy.getTotalPremium();
					customerName = pLifePolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					nrBf.append(" Single Premium Credit Life Premium ");
					LifePolicy splifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = splifePolicy.getTotalSumInsured();
					premium = (splifePolicy.isEndorsementApplied()) ? splifePolicy.getTotalEndorsementPremium() : splifePolicy.getTotalPremium();
					customerName = splifePolicy.getCustomerName();
					totalInsuredPerson = splifePolicy.getInsuredPersonInfo().size() > 1 ? splifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = " Single Premium Credit Life Bill Collection ";
					LifePolicy splifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = splifeBillPolicy.getTotalSumInsured();
					premium = (splifeBillPolicy.isEndorsementApplied()) ? splifeBillPolicy.getTotalEndorsementPremium() : splifeBillPolicy.getTotalPremium();
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					nrBf.append(" Short Term Single Premium Credit Life Premium ");
					LifePolicy stsplifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stsplifePolicy.getTotalSumInsured();
					premium = (stsplifePolicy.isEndorsementApplied()) ? stsplifePolicy.getTotalEndorsementPremium() : stsplifePolicy.getTotalPremium();
					customerName = stsplifePolicy.getCustomerName();
					totalInsuredPerson = stsplifePolicy.getInsuredPersonInfo().size() > 1 ? stsplifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = " Short Term Single Premium Credit Life Bill Collection ";
					LifePolicy stspBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stspBillPolicy.getTotalSumInsured();
					premium = (stspBillPolicy.isEndorsementApplied()) ? stspBillPolicy.getTotalEndorsementPremium() : stspBillPolicy.getTotalPremium();
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					nrBf.append(" Single Premium Endowment Life Premium ");
					LifePolicy spelifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spelifePolicy.getTotalSumInsured();
					premium = (spelifePolicy.isEndorsementApplied()) ? spelifePolicy.getTotalEndorsementPremium() : spelifePolicy.getTotalPremium();
					customerName = spelifePolicy.getCustomerName();
					totalInsuredPerson = spelifePolicy.getInsuredPersonInfo().size() > 1 ? spelifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = " Single Premium Endowment Life Bill Collection ";
					LifePolicy spelifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spelifeBillPolicy.getTotalSumInsured();
					premium = (spelifeBillPolicy.isEndorsementApplied()) ? spelifeBillPolicy.getTotalEndorsementPremium() : spelifeBillPolicy.getTotalPremium();
					break;
				case SIMPLE_LIFE_POLICY:
					premiumString = "Simple Life Premium  ";
					LifePolicy simplelifepolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = simplelifepolicy.getTotalSumInsured();
					premium = simplelifepolicy.getTotalPremium();
					customerName = simplelifepolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append(" received by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			if (payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_POLICY) || PolicyReferenceType.PERSON_TRAVEL_POLICY.equals(payment.getReferenceType())
					|| payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION)
					|| payment.getReferenceType().equals(PolicyReferenceType.MICRO_HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION)) {
				nrBf.append(" for ");
				nrBf.append(unit);
			} else {
				nrBf.append(" for Sum Insured ");
				nrBf.append(Utils.getCurrencyFormatString(si));
			}
			if (totalInsuredPerson > 0
					&& (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(payment.getReferenceType()) || PolicyReferenceType.LIFE_POLICY.equals(payment.getReferenceType()))) {
				nrBf.append(" and for total number of insured person ");
				nrBf.append(Integer.toString(totalInsuredPerson));
			}
			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(premium));
			nrBf.append(". ");
		}

		return nrBf.toString();
	}

	private String getNarrationReceiablePremiumForInterBranch(Payment payment, boolean isRenewal, String branchName) {
		StringBuffer nrBf = new StringBuffer();
		String customerName = "";
		String premiumString = "";
		int totalInsuredPerson = 0;
		double si = 0.0;
		String unit = "";
		double premium = 0.0;

		if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
			nrBf.append(" Receivable from " + "(" + branchName + ")");
			nrBf.append(" Accrued");
			switch (payment.getReferenceType()) {
				case FARMER_POLICY:
				case LIFE_POLICY:
					premiumString = " Life Premium ";
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = (lifePolicy.isEndorsementApplied()) ? lifePolicy.getTotalEndorsementPremium() : lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					totalInsuredPerson = lifePolicy.getInsuredPersonInfo().size() > 1 ? lifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case LIFE_BILL_COLLECTION:
					premiumString = " Life Bill Collection ";
					LifePolicy lifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifeBillPolicy.getTotalSumInsured();
					premium = lifeBillPolicy.getTotalPremium();
					customerName = lifeBillPolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					premiumString = " Single Premium Credit Life Premium ";
					LifePolicy spclifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spclifePolicy.getTotalSumInsured();
					premium = (spclifePolicy.isEndorsementApplied()) ? spclifePolicy.getTotalEndorsementPremium() : spclifePolicy.getTotalPremium();
					customerName = spclifePolicy.getCustomerName();
					totalInsuredPerson = spclifePolicy.getInsuredPersonInfo().size() > 1 ? spclifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = " Single Premium Credit Life Bill Collection ";
					LifePolicy spclifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spclifeBillPolicy.getTotalSumInsured();
					premium = spclifeBillPolicy.getTotalPremium();
					customerName = spclifeBillPolicy.getCustomerName();
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					premiumString = " Short Term Single Premium Credit Life Premium ";
					LifePolicy stspclifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stspclifePolicy.getTotalSumInsured();
					premium = (stspclifePolicy.isEndorsementApplied()) ? stspclifePolicy.getTotalEndorsementPremium() : stspclifePolicy.getTotalPremium();
					customerName = stspclifePolicy.getCustomerName();
					totalInsuredPerson = stspclifePolicy.getInsuredPersonInfo().size() > 1 ? stspclifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = " Short Term Single Premium Credit Life Bill Collection ";
					LifePolicy stspclifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stspclifeBillPolicy.getTotalSumInsured();
					premium = stspclifeBillPolicy.getTotalPremium();
					customerName = stspclifeBillPolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					premiumString = " Single Premium Endowment Life Premium ";
					LifePolicy spelifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spelifePolicy.getTotalSumInsured();
					premium = (spelifePolicy.isEndorsementApplied()) ? spelifePolicy.getTotalEndorsementPremium() : spelifePolicy.getTotalPremium();
					customerName = spelifePolicy.getCustomerName();
					totalInsuredPerson = spelifePolicy.getInsuredPersonInfo().size() > 1 ? spelifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = " Single Premium Endowment Life Bill Collection ";
					LifePolicy spelifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spelifeBillPolicy.getTotalSumInsured();
					premium = spelifeBillPolicy.getTotalPremium();
					customerName = spelifeBillPolicy.getCustomerName();
					break;
				case PA_POLICY:
					premiumString = " Personal Accident Premium ";
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = (paPolicy.isEndorsementApplied()) ? paPolicy.getTotalEndorsementPremium() : paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;

				case MEDICAL_POLICY:
					premiumString = "Old Health Premium ";
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalPolicy.getPremium();
					customerName = medicalPolicy.getCustomerName();
					break;
				case HEALTH_POLICY:
					premiumString = "Health Premium ";
					MedicalPolicy healthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthPolicy.getPremium();
					customerName = healthPolicy.getCustomerName();
					break;
				case MICRO_HEALTH_POLICY:
					premiumString = "Micro Health Premium ";
					MedicalPolicy microHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = microHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = microHealthPolicy.getPremium();
					customerName = microHealthPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY:
					premiumString = "Critical Health Premium ";
					MedicalPolicy criticalHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = criticalHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = criticalHealthPolicy.getPremium();
					customerName = criticalHealthPolicy.getCustomerName();
					break;
				case MEDICAL_BILL_COLLECTION:
					premiumString = "Old Health Bill Collection Premium ";
					MedicalPolicy medicalBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalBillPolicy.getPremium();
					customerName = medicalBillPolicy.getCustomerName();
					break;
				case HEALTH_POLICY_BILL_COLLECTION:
					premiumString = "Health Bill Collection Premium ";
					MedicalPolicy healthBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthBillPolicy.getPremium();
					customerName = healthBillPolicy.getCustomerName();
					break;
				// case MICRO_HEALTH_POLICY_BILL_COLLECTION:
				// premiumString = "Micro Health Bill Collection Premium ";
				// MedicalPolicy microHealthBillPolicy =
				// medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
				// unit = microHealthBillPolicy.getTotalUnit() + " Unit(s) ";
				// premium = microHealthBillPolicy.getPremium();
				// customerName = microHealthBillPolicy.getCustomerName();
				// break;
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					premiumString = "Critical Illness Bill Collection Premium ";
					MedicalPolicy criticalIllnessBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = criticalIllnessBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = criticalIllnessBillPolicy.getPremium();
					customerName = criticalIllnessBillPolicy.getCustomerName();
					break;

				case PERSON_TRAVEL_POLICY:
					premiumString = " Personal Travel Premium ";
					PersonTravelPolicy personTravel = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
					unit = personTravel.getPersonTravelPolicyInfo().getTotalUnit() + " Unit(s) ";
					premium = personTravel.getPersonTravelPolicyInfo().getPremium();
					customerName = personTravel.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					premiumString = " Short Term Endowment Life Premium ";
					LifePolicy seLifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = seLifePolicy.getTotalSumInsured();
					premium = seLifePolicy.getTotalPremium();
					customerName = seLifePolicy.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = " Short Term Endowment Life Bill Collection ";
					LifePolicy seLifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = seLifeBillPolicy.getTotalSumInsured();
					premium = seLifeBillPolicy.getTotalPremium();
					customerName = seLifeBillPolicy.getCustomerName();
					break;
				case GROUP_FARMER_PROPOSAL:
					premiumString = " Life Premium ";
					GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(payment.getReferenceNo());
					si = proposal.getTotalSI();
					premium = proposal.getPremium();
					totalInsuredPerson = proposal.getNoOfInsuredPerson();
					customerName = proposal.getCustomerName();
					break;
				case STUDENT_LIFE_POLICY:
					premiumString = "Student Life Premium ";
					LifePolicy studentPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentPolicy.getTotalSumInsured();
					premium = (studentPolicy.isEndorsementApplied()) ? studentPolicy.getTotalEndorsementPremium() : studentPolicy.getTotalPremium();
					customerName = studentPolicy.getCustomerName();
					break;
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					premiumString = " Student Life Bill Collection ";
					LifePolicy studentBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentBillPolicy.getTotalSumInsured();
					premium = studentBillPolicy.getTotalPremium();
					customerName = studentBillPolicy.getCustomerName();
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					premiumString = " Public Term Life Premium ";
					LifePolicy pLifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = pLifePolicy.getTotalSumInsured();
					premium = pLifePolicy.getTotalPremium();
					customerName = pLifePolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append("to be paid by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			if (payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION)
					|| payment.getReferenceType().equals(PolicyReferenceType.MICRO_HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION)
					|| PolicyReferenceType.PERSON_TRAVEL_POLICY.equals(payment.getReferenceType())) {
				nrBf.append(" for ");
				nrBf.append(unit);
			} else {
				nrBf.append(" for Sum Insured ");
				nrBf.append(Utils.getCurrencyFormatString(si));
			}

			if (totalInsuredPerson > 0
					&& (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(payment.getReferenceType()) || PolicyReferenceType.LIFE_POLICY.equals(payment.getReferenceType()))) {
				nrBf.append(" and for total number of insured person ");
				nrBf.append(Integer.toString(totalInsuredPerson));
			}

			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(premium));
			nrBf.append(". ");

		} else {
			nrBf.append(" Receivable from " + "(" + branchName + ")");
			nrBf.append(" Being amount of ");
			switch (payment.getReferenceType()) {
				case FARMER_POLICY:
				case LIFE_POLICY:
					nrBf.append(" Life Premium ");
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = (lifePolicy.isEndorsementApplied()) ? lifePolicy.getTotalEndorsementPremium() : lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					totalInsuredPerson = lifePolicy.getInsuredPersonInfo().size() > 1 ? lifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case LIFE_BILL_COLLECTION:
					premiumString = " Life Bill Collection ";
					LifePolicy lifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifeBillPolicy.getTotalSumInsured();
					premium = (lifeBillPolicy.isEndorsementApplied()) ? lifeBillPolicy.getTotalEndorsementPremium() : lifeBillPolicy.getTotalPremium();
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					nrBf.append("Single Premium Credit Life Premium ");
					LifePolicy spclifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spclifePolicy.getTotalSumInsured();
					premium = (spclifePolicy.isEndorsementApplied()) ? spclifePolicy.getTotalEndorsementPremium() : spclifePolicy.getTotalPremium();
					customerName = spclifePolicy.getCustomerName();
					totalInsuredPerson = spclifePolicy.getInsuredPersonInfo().size() > 1 ? spclifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = "Single Premium Credit Life Bill Collection ";
					LifePolicy spclifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spclifeBillPolicy.getTotalSumInsured();
					premium = (spclifeBillPolicy.isEndorsementApplied()) ? spclifeBillPolicy.getTotalEndorsementPremium() : spclifeBillPolicy.getTotalPremium();
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					nrBf.append(" Short Term Single Premium Credit Life Premium ");
					LifePolicy stsplifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stsplifePolicy.getTotalSumInsured();
					premium = (stsplifePolicy.isEndorsementApplied()) ? stsplifePolicy.getTotalEndorsementPremium() : stsplifePolicy.getTotalPremium();
					customerName = stsplifePolicy.getCustomerName();
					totalInsuredPerson = stsplifePolicy.getInsuredPersonInfo().size() > 1 ? stsplifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = " Short Term Single Premium Credit Life Bill Collection ";
					LifePolicy stsplifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stsplifeBillPolicy.getTotalSumInsured();
					premium = (stsplifeBillPolicy.isEndorsementApplied()) ? stsplifeBillPolicy.getTotalEndorsementPremium() : stsplifeBillPolicy.getTotalPremium();
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					nrBf.append("  Single Premium Endowment Life Premium ");
					LifePolicy spelifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spelifePolicy.getTotalSumInsured();
					premium = (spelifePolicy.isEndorsementApplied()) ? spelifePolicy.getTotalEndorsementPremium() : spelifePolicy.getTotalPremium();
					customerName = spelifePolicy.getCustomerName();
					totalInsuredPerson = spelifePolicy.getInsuredPersonInfo().size() > 1 ? spelifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = " Single Premium Endowment Life Bill Collection ";
					LifePolicy speBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = speBillPolicy.getTotalSumInsured();
					premium = (speBillPolicy.isEndorsementApplied()) ? speBillPolicy.getTotalEndorsementPremium() : speBillPolicy.getTotalPremium();
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					nrBf.append(" Short Term Endowment Life Premium ");
					LifePolicy selifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = selifePolicy.getTotalSumInsured();
					premium = selifePolicy.getTotalPremium();
					customerName = selifePolicy.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					nrBf.append(" Short Term Endowment Life Bill Collection ");
					LifePolicy selifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = selifeBillPolicy.getTotalSumInsured();
					premium = selifeBillPolicy.getTotalPremium();
					customerName = selifeBillPolicy.getCustomerName();
					break;
				case MEDICAL_POLICY:
					nrBf.append(" Medical Premium ");
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalPolicy.getTotalPremium();
					customerName = medicalPolicy.getCustomerName();
					break;
				case MEDICAL_BILL_COLLECTION:
					nrBf.append(" Medical Bill Collection Premium ");
					MedicalPolicy medicalBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalBillPolicy.getTotalPremium();
					customerName = medicalBillPolicy.getCustomerName();
					break;
				case HEALTH_POLICY:
					nrBf.append(" Health Premium ");
					MedicalPolicy healthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthPolicy.getTotalPremium();
					customerName = healthPolicy.getCustomerName();
					break;
				case HEALTH_POLICY_BILL_COLLECTION:
					nrBf.append(" Health Bill Collection Premium ");
					MedicalPolicy healthBillPolicy1 = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthBillPolicy1.getTotalUnit() + " Unit(s) ";
					premium = healthBillPolicy1.getTotalPremium();
					customerName = healthBillPolicy1.getCustomerName();
					break;
				case MICRO_HEALTH_POLICY:
					nrBf.append(" Micro Health Premium ");
					MedicalPolicy microHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = microHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = microHealthPolicy.getTotalPremium();
					customerName = microHealthPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY:
					nrBf.append(" Critical Illness Premium ");
					MedicalPolicy ciPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = ciPolicy.getTotalUnit() + " Unit(s) ";
					premium = ciPolicy.getTotalPremium();
					customerName = ciPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					nrBf.append(" Critical Illness Bill Collection Premium ");
					MedicalPolicy ciBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = ciBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = ciBillPolicy.getTotalPremium();
					customerName = ciBillPolicy.getCustomerName();
					break;

				case PA_POLICY:
					nrBf.append(" Personal Accident Premium ");
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = (paPolicy.isEndorsementApplied()) ? paPolicy.getTotalEndorsementPremium() : paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;

				case TRAVEL_PROPOSAL:
					// Travel Narration does not like others, so return from
					// code.
					TravelProposal travelProposal = travelProposalService.findTravelProposalById(payment.getReferenceNo());
					StringBuffer narration = new StringBuffer();
					String fromDate = Utils.getDateFormatString(travelProposal.getFromDate());
					String toDate = Utils.getDateFormatString(travelProposal.getToDate());
					narration.append(" Receivable from " + "(" + branchName + ")");
					narration.append("Premium Income from ");
					narration.append(travelProposal.getExpressList().size());
					narration.append(" Gates collected for ");
					narration.append(travelProposal.getTotalPassenger() + " * 270 = ");
					narration.append(travelProposal.getTotalNetPremium());
					narration.append(" Per attached period on ");
					narration.append(fromDate + " to " + toDate);
					return narration.toString();
				case PERSON_TRAVEL_POLICY:
					nrBf.append(" Person Travel Premium ");
					PersonTravelPolicy personTravel = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
					unit = personTravel.getPersonTravelPolicyInfo().getTotalUnit() + " Unit(s) ";
					premium = personTravel.getPersonTravelPolicyInfo().getPremium();
					customerName = personTravel.getCustomerName();
					break;
				case GROUP_FARMER_PROPOSAL:
					nrBf.append(" Life Premium ");
					GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(payment.getReferenceNo());
					si = proposal.getTotalSI();
					premium = proposal.getPremium();
					customerName = proposal.getCustomerName();
					totalInsuredPerson = proposal.getNoOfInsuredPerson();
					break;
				case STUDENT_LIFE_POLICY:
					nrBf.append(" Life Premium ");
					LifePolicy studentPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentPolicy.getTotalSumInsured();
					premium = (studentPolicy.isEndorsementApplied()) ? studentPolicy.getTotalEndorsementPremium() : studentPolicy.getTotalPremium();
					customerName = studentPolicy.getCustomerName();
					totalInsuredPerson = studentPolicy.getInsuredPersonInfo().size() > 1 ? studentPolicy.getInsuredPersonInfo().size() : 0;
					break;
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					premiumString = "Student Life Bill Collection ";
					LifePolicy studentBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentBillPolicy.getTotalSumInsured();
					premium = (studentBillPolicy.isEndorsementApplied()) ? studentBillPolicy.getTotalEndorsementPremium() : studentBillPolicy.getTotalPremium();
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					nrBf.append(" Public Term Life Premium ");
					LifePolicy pelifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = pelifePolicy.getTotalSumInsured();
					premium = pelifePolicy.getTotalPremium();
					customerName = pelifePolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append(" received by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			if (payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_POLICY) || PolicyReferenceType.PERSON_TRAVEL_POLICY.equals(payment.getReferenceType())
					|| payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION)
					|| payment.getReferenceType().equals(PolicyReferenceType.MICRO_HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION)) {
				nrBf.append(" for ");
				nrBf.append(unit);
			} else {
				nrBf.append(" for Sum Insured ");
				nrBf.append(Utils.getCurrencyFormatString(si));
			}
			if (totalInsuredPerson > 0
					&& (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(payment.getReferenceType()) || PolicyReferenceType.LIFE_POLICY.equals(payment.getReferenceType()))) {
				nrBf.append(" and for total number of insured person ");
				nrBf.append(Integer.toString(totalInsuredPerson));
			}
			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(premium));
			nrBf.append(". ");
		}

		return nrBf.toString();
	}

	private String getNarrationPremiumPayableForInterBranch(Payment payment, String branchName) {
		StringBuffer nrBf = new StringBuffer();
		String customerName = "";
		String premiumString = "";
		int totalInsuredPerson = 0;
		double si = 0.0;
		String unit = "";
		double premium = 0.0;

		if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
			nrBf.append(" Payable to " + "(" + branchName + ") ");
			nrBf.append(" Accrued");
			switch (payment.getReferenceType()) {
				case FARMER_POLICY:
				case LIFE_POLICY:
					premiumString = " Life Premium ";
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = (lifePolicy.isEndorsementApplied()) ? lifePolicy.getTotalEndorsementPremium() : lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					totalInsuredPerson = lifePolicy.getInsuredPersonInfo().size() > 1 ? lifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case LIFE_BILL_COLLECTION:
					premiumString = " Life Bill Collection ";
					LifePolicy lifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifeBillPolicy.getTotalSumInsured();
					premium = lifeBillPolicy.getTotalPremium();
					customerName = lifeBillPolicy.getCustomerName();
					break;
				case PA_POLICY:
					premiumString = " Personal Accident Premium ";
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = (paPolicy.isEndorsementApplied()) ? paPolicy.getTotalEndorsementPremium() : paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;

				case MEDICAL_POLICY:
					premiumString = "Old Health Premium ";
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalPolicy.getPremium();
					customerName = medicalPolicy.getCustomerName();
					break;
				case HEALTH_POLICY:
					premiumString = "Health Premium ";
					MedicalPolicy healthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthPolicy.getPremium();
					customerName = healthPolicy.getCustomerName();
					break;
				case MICRO_HEALTH_POLICY:
					premiumString = "Micro Health Premium ";
					MedicalPolicy microHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = microHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = microHealthPolicy.getPremium();
					customerName = microHealthPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY:
					premiumString = "Critical Health Premium ";
					MedicalPolicy criticalHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = criticalHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = criticalHealthPolicy.getPremium();
					customerName = criticalHealthPolicy.getCustomerName();
					break;
				case MEDICAL_BILL_COLLECTION:
					premiumString = "Old Health Bill Collection Premium ";
					MedicalPolicy medicalBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalBillPolicy.getPremium();
					customerName = medicalBillPolicy.getCustomerName();
					break;
				case HEALTH_POLICY_BILL_COLLECTION:
					premiumString = "Health Bill Collection Premium ";
					MedicalPolicy healthBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthBillPolicy.getPremium();
					customerName = healthBillPolicy.getCustomerName();
					break;
				// case MICRO_HEALTH_POLICY_BILL_COLLECTION:
				// premiumString = "Micro Health Bill Collection Premium ";
				// MedicalPolicy microHealthBillPolicy =
				// medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
				// unit = microHealthBillPolicy.getTotalUnit() + " Unit(s) ";
				// premium = microHealthBillPolicy.getPremium();
				// customerName = microHealthBillPolicy.getCustomerName();
				// break;
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					premiumString = "Critical Illness Bill Collection Premium ";
					MedicalPolicy criticalIllnessBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = criticalIllnessBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = criticalIllnessBillPolicy.getPremium();
					customerName = criticalIllnessBillPolicy.getCustomerName();
					break;

				case PERSON_TRAVEL_POLICY:
					premiumString = " Personal Travel Premium ";
					PersonTravelPolicy personTravel = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
					unit = personTravel.getPersonTravelPolicyInfo().getTotalUnit() + " Unit(s) ";
					premium = personTravel.getPersonTravelPolicyInfo().getPremium();
					customerName = personTravel.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					premiumString = " Short Term Endowment Life Premium ";
					LifePolicy seLifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = seLifePolicy.getTotalSumInsured();
					premium = seLifePolicy.getTotalPremium();
					customerName = seLifePolicy.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = " Short Term Endowment Life Bill Collection ";
					LifePolicy seLifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = seLifeBillPolicy.getTotalSumInsured();
					premium = seLifeBillPolicy.getTotalPremium();
					customerName = seLifeBillPolicy.getCustomerName();
					break;
				case GROUP_FARMER_PROPOSAL:
					premiumString = " Life Premium ";
					GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(payment.getReferenceNo());
					si = proposal.getTotalSI();
					premium = proposal.getPremium();
					totalInsuredPerson = proposal.getNoOfInsuredPerson();
					customerName = proposal.getCustomerName();
					break;
				case STUDENT_LIFE_POLICY:
					premiumString = "Student Life Premium ";
					LifePolicy studentPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentPolicy.getTotalSumInsured();
					premium = (studentPolicy.isEndorsementApplied()) ? studentPolicy.getTotalEndorsementPremium() : studentPolicy.getTotalPremium();
					customerName = studentPolicy.getCustomerName();
					break;
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					premiumString = " Student Life Bill Collection ";
					LifePolicy studentBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentBillPolicy.getTotalSumInsured();
					premium = studentBillPolicy.getTotalPremium();
					customerName = studentBillPolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					premiumString = "Single Premium Credit Life Premium ";
					LifePolicy spPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spPolicy.getTotalSumInsured();
					premium = (spPolicy.isEndorsementApplied()) ? spPolicy.getTotalEndorsementPremium() : spPolicy.getTotalPremium();
					customerName = spPolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = " Single Premium Credit Life Bill Collection ";
					LifePolicy spBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spBillPolicy.getTotalSumInsured();
					premium = spBillPolicy.getTotalPremium();
					customerName = spBillPolicy.getCustomerName();
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					premiumString = "Short Term Single Premium Credit Life Premium ";
					LifePolicy stspPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stspPolicy.getTotalSumInsured();
					premium = (stspPolicy.isEndorsementApplied()) ? stspPolicy.getTotalEndorsementPremium() : stspPolicy.getTotalPremium();
					customerName = stspPolicy.getCustomerName();
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = " Short Term Single Premium Credit Life Bill Collection ";
					LifePolicy stspBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stspBillPolicy.getTotalSumInsured();
					premium = stspBillPolicy.getTotalPremium();
					customerName = stspBillPolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					premiumString = "Single Premium Endowment Life Premium ";
					LifePolicy spePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spePolicy.getTotalSumInsured();
					premium = (spePolicy.isEndorsementApplied()) ? spePolicy.getTotalEndorsementPremium() : spePolicy.getTotalPremium();
					customerName = spePolicy.getCustomerName();
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = " Single Premium Endowment Life Bill Collection ";
					LifePolicy speBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = speBillPolicy.getTotalSumInsured();
					premium = speBillPolicy.getTotalPremium();
					customerName = speBillPolicy.getCustomerName();
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					premiumString = " Public Term Life Premium ";
					LifePolicy pLifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = pLifePolicy.getTotalSumInsured();
					premium = pLifePolicy.getTotalPremium();
					customerName = pLifePolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append("to be paid by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			if (payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION)
					|| payment.getReferenceType().equals(PolicyReferenceType.MICRO_HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION)
					|| PolicyReferenceType.PERSON_TRAVEL_POLICY.equals(payment.getReferenceType())) {
				nrBf.append(" for ");
				nrBf.append(unit);
			} else {
				nrBf.append(" for Sum Insured ");
				nrBf.append(Utils.getCurrencyFormatString(si));
			}

			if (totalInsuredPerson > 0
					&& (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(payment.getReferenceType()) || PolicyReferenceType.LIFE_POLICY.equals(payment.getReferenceType()))) {
				nrBf.append(" and for total number of insured person ");
				nrBf.append(Integer.toString(totalInsuredPerson));
			}

			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(premium));
			nrBf.append(". ");

		} else {

			nrBf.append(" Payable to " + "(" + branchName + ")");
			nrBf.append(" Being amount of ");
			switch (payment.getReferenceType()) {

				case FARMER_POLICY:
				case LIFE_POLICY:
					nrBf.append(" Life Premium ");
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifePolicy.getTotalSumInsured();
					premium = (lifePolicy.isEndorsementApplied()) ? lifePolicy.getTotalEndorsementPremium() : lifePolicy.getTotalPremium();
					customerName = lifePolicy.getCustomerName();
					totalInsuredPerson = lifePolicy.getInsuredPersonInfo().size() > 1 ? lifePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case LIFE_BILL_COLLECTION:
					premiumString = " Life Bill Collection ";
					LifePolicy lifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = lifeBillPolicy.getTotalSumInsured();
					premium = (lifeBillPolicy.isEndorsementApplied()) ? lifeBillPolicy.getTotalEndorsementPremium() : lifeBillPolicy.getTotalPremium();
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					nrBf.append(" Short Term Endowment Life Premium ");
					LifePolicy selifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = selifePolicy.getTotalSumInsured();
					premium = selifePolicy.getTotalPremium();
					customerName = selifePolicy.getCustomerName();
					break;
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					nrBf.append(" Short Term Endowment Life Bill Collection ");
					LifePolicy selifeBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = selifeBillPolicy.getTotalSumInsured();
					premium = selifeBillPolicy.getTotalPremium();
					customerName = selifeBillPolicy.getCustomerName();
					break;
				case MEDICAL_POLICY:
					nrBf.append(" Medical Premium ");
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalPolicy.getTotalPremium();
					customerName = medicalPolicy.getCustomerName();
					break;
				case MEDICAL_BILL_COLLECTION:
					nrBf.append(" Medical Bill Collection Premium ");
					MedicalPolicy medicalBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = medicalBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = medicalBillPolicy.getTotalPremium();
					customerName = medicalBillPolicy.getCustomerName();
					break;
				case HEALTH_POLICY:
					nrBf.append(" Health Premium ");
					MedicalPolicy healthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthPolicy.getTotalUnit() + " Unit(s) ";
					premium = healthPolicy.getTotalPremium();
					customerName = healthPolicy.getCustomerName();
					break;
				case HEALTH_POLICY_BILL_COLLECTION:
					nrBf.append(" Health Bill Collection Premium ");
					MedicalPolicy healthBillPolicy1 = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = healthBillPolicy1.getTotalUnit() + " Unit(s) ";
					premium = healthBillPolicy1.getTotalPremium();
					customerName = healthBillPolicy1.getCustomerName();
					break;
				case MICRO_HEALTH_POLICY:
					nrBf.append(" Micro Health Premium ");
					MedicalPolicy microHealthPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = microHealthPolicy.getTotalUnit() + " Unit(s) ";
					premium = microHealthPolicy.getTotalPremium();
					customerName = microHealthPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY:
					nrBf.append(" Critical Illness Premium ");
					MedicalPolicy ciPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = ciPolicy.getTotalUnit() + " Unit(s) ";
					premium = ciPolicy.getTotalPremium();
					customerName = ciPolicy.getCustomerName();
					break;
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					nrBf.append(" Critical Illness Bill Collection Premium ");
					MedicalPolicy ciBillPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					unit = ciBillPolicy.getTotalUnit() + " Unit(s) ";
					premium = ciBillPolicy.getTotalPremium();
					customerName = ciBillPolicy.getCustomerName();
					break;

				case PA_POLICY:
					nrBf.append(" Personal Accident Premium ");
					LifePolicy paPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = paPolicy.getTotalSumInsured();
					premium = (paPolicy.isEndorsementApplied()) ? paPolicy.getTotalEndorsementPremium() : paPolicy.getTotalPremium();
					customerName = paPolicy.getCustomerName();
					break;

				case TRAVEL_PROPOSAL:
					// Travel Narration does not like others, so return from
					// code.
					TravelProposal travelProposal = travelProposalService.findTravelProposalById(payment.getReferenceNo());
					StringBuffer narration = new StringBuffer();
					String fromDate = Utils.getDateFormatString(travelProposal.getFromDate());
					String toDate = Utils.getDateFormatString(travelProposal.getToDate());
					narration.append("Payable to " + "(" + branchName + ") ");
					narration.append("Premium Income from ");
					narration.append(travelProposal.getExpressList().size());
					narration.append(" Gates collected for ");
					narration.append(travelProposal.getTotalPassenger() + " * 270 = ");
					narration.append(travelProposal.getTotalNetPremium());
					narration.append(" Per attached period on ");
					narration.append(fromDate + " to " + toDate);
					return narration.toString();
				case PERSON_TRAVEL_POLICY:
					nrBf.append(" Person Travel Premium ");
					PersonTravelPolicy personTravel = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
					unit = personTravel.getPersonTravelPolicyInfo().getTotalUnit() + " Unit(s) ";
					premium = personTravel.getPersonTravelPolicyInfo().getPremium();
					customerName = personTravel.getCustomerName();
					break;
				case GROUP_FARMER_PROPOSAL:
					nrBf.append(" Life Premium ");
					GroupFarmerProposal proposal = groupFarmerProposalService.findGroupFarmerById(payment.getReferenceNo());
					si = proposal.getTotalSI();
					premium = proposal.getPremium();
					customerName = proposal.getCustomerName();
					totalInsuredPerson = proposal.getNoOfInsuredPerson();
					break;
				case STUDENT_LIFE_POLICY:
					nrBf.append(" Life Premium ");
					LifePolicy studentPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentPolicy.getTotalSumInsured();
					premium = (studentPolicy.isEndorsementApplied()) ? studentPolicy.getTotalEndorsementPremium() : studentPolicy.getTotalPremium();
					customerName = studentPolicy.getCustomerName();
					totalInsuredPerson = studentPolicy.getInsuredPersonInfo().size() > 1 ? studentPolicy.getInsuredPersonInfo().size() : 0;
					break;
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					premiumString = "Student Life Bill Collection ";
					LifePolicy studentBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = studentBillPolicy.getTotalSumInsured();
					premium = (studentBillPolicy.isEndorsementApplied()) ? studentBillPolicy.getTotalEndorsementPremium() : studentBillPolicy.getTotalPremium();
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					nrBf.append("  Single Premium Credit Life Premium ");
					LifePolicy spPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spPolicy.getTotalSumInsured();
					premium = (spPolicy.isEndorsementApplied()) ? spPolicy.getTotalEndorsementPremium() : spPolicy.getTotalPremium();
					customerName = spPolicy.getCustomerName();
					totalInsuredPerson = spPolicy.getInsuredPersonInfo().size() > 1 ? spPolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = "Student Life Bill Collection ";
					LifePolicy spBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spBillPolicy.getTotalSumInsured();
					premium = (spBillPolicy.isEndorsementApplied()) ? spBillPolicy.getTotalEndorsementPremium() : spBillPolicy.getTotalPremium();
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					nrBf.append(" Short Term Single Premium Credit Life Premium ");
					LifePolicy stspPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stspPolicy.getTotalSumInsured();
					premium = (stspPolicy.isEndorsementApplied()) ? stspPolicy.getTotalEndorsementPremium() : stspPolicy.getTotalPremium();
					customerName = stspPolicy.getCustomerName();
					totalInsuredPerson = stspPolicy.getInsuredPersonInfo().size() > 1 ? stspPolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					premiumString = "Student Life Bill Collection ";
					LifePolicy stspBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = stspBillPolicy.getTotalSumInsured();
					premium = (stspBillPolicy.isEndorsementApplied()) ? stspBillPolicy.getTotalEndorsementPremium() : stspBillPolicy.getTotalPremium();
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					nrBf.append("Single Premium Endowment Life Premium ");
					LifePolicy spePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = spePolicy.getTotalSumInsured();
					premium = (spePolicy.isEndorsementApplied()) ? spePolicy.getTotalEndorsementPremium() : spePolicy.getTotalPremium();
					customerName = spePolicy.getCustomerName();
					totalInsuredPerson = spePolicy.getInsuredPersonInfo().size() > 1 ? spePolicy.getInsuredPersonInfo().size() : 0;
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					premiumString = "Single Premium Endowment Life Bill Collection ";
					LifePolicy speBillPolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = speBillPolicy.getTotalSumInsured();
					premium = (speBillPolicy.isEndorsementApplied()) ? speBillPolicy.getTotalEndorsementPremium() : speBillPolicy.getTotalPremium();
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					nrBf.append(" Public Term Life Premium ");
					LifePolicy pelifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					si = pelifePolicy.getTotalSumInsured();
					premium = pelifePolicy.getTotalPremium();
					customerName = pelifePolicy.getCustomerName();
					break;
				default:
					break;
			}
			nrBf.append(premiumString);
			nrBf.append(" received by ");
			nrBf.append(payment.getReceiptNo());
			nrBf.append(" from ");
			nrBf.append(customerName);
			if (payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_POLICY) || PolicyReferenceType.PERSON_TRAVEL_POLICY.equals(payment.getReferenceType())
					|| payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION)
					|| payment.getReferenceType().equals(PolicyReferenceType.MICRO_HEALTH_POLICY) || payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY)
					|| payment.getReferenceType().equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION)) {
				nrBf.append(" for ");
				nrBf.append(unit);
			} else {
				nrBf.append(" for Sum Insured ");
				nrBf.append(Utils.getCurrencyFormatString(si));
			}
			if (totalInsuredPerson > 0
					&& (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(payment.getReferenceType()) || PolicyReferenceType.LIFE_POLICY.equals(payment.getReferenceType()))) {
				nrBf.append(" and for total number of insured person ");
				nrBf.append(Integer.toString(totalInsuredPerson));
			}
			nrBf.append(" and the premium amount of ");
			nrBf.append(Utils.getCurrencyFormatString(premium));
			nrBf.append(". ");
		}

		return nrBf.toString();
	}

	private String getNarrationSCST(Payment payment, boolean isRenewal) {
		String premiumName = "";
		StringBuffer nrBf = new StringBuffer();
		// Being amount of Service charges for (Product name) Received No
		// (Receive No)

		nrBf.append("Being amount of service charges for ");
		switch (payment.getReferenceType()) {

			case FARMER_POLICY:
			case LIFE_POLICY:
				premiumName = " Life Premium ";
				break;
			case LIFE_BILL_COLLECTION:
				premiumName = " Life Bill Collection ";
				break;
			case SHORT_ENDOWMENT_LIFE_POLICY:
				premiumName = " Short Term Life Premium ";
				break;
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				premiumName = " Short Term Life Bill Collection ";
			case PA_POLICY:
				premiumName = (isRenewal) ? "Personal Accident Renewal Premium " : "Personal Accident Premium ";
				break;

			case MEDICAL_POLICY:
				premiumName = "Medical Insurance ";
				break;
			case HEALTH_POLICY:
				premiumName = "Health Insurance ";
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				premiumName = "Health Bill collection ";
				break;
			case MICRO_HEALTH_POLICY:
				premiumName = "Micro Health Insurance ";
				break;
			case CRITICAL_ILLNESS_POLICY:
				premiumName = "Critical Illness Insurance ";
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				premiumName = "Critical Illness Bill collection ";
				break;
			case STUDENT_LIFE_POLICY:
				premiumName = " Student Life Premium ";
				break;
			case STUDENT_LIFE_POLICY_BILL_COLLECTION:
				premiumName = " Student Life Bill Collection ";
				break;
			case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				premiumName = " Single Premium Credit Life Premium ";
				break;
			case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				premiumName = " Single Premium Credit Life Bill Collection ";
				break;
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				premiumName = " Short Term Single Premium Credit Life Premium ";
				break;
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
				premiumName = " Short Term Single Premium Credit Life Bill Collection ";
				break;
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				premiumName = " Single Premium Endowment Life Premium ";
				break;
			case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
				premiumName = " Single Premium Endowment Life Bill Collection ";
				break;
			default:
				break;
		}
		nrBf.append(premiumName);
		nrBf.append(" for Receipt No ");
		nrBf.append(payment.getReceiptNo());
		nrBf.append(". ");

		return nrBf.toString();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CashDebitForPremium(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			double totalNetPremium = 0;
			double homeAmount = 0;
			String coaCode = null;
			Product product;
			Payment payment = accountPaymentList.get(0).getPayment();
			// TLF Home Amount
			if (isRenewal) {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getRenewalNetPremium() - accountPayment.getPayment().getBankCharges();
				}
			} else {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getNetPremium() - accountPayment.getPayment().getBankCharges();
				}
			}

			// TODO FIXME TOK don't set total amount as homeamount , wrong if
			// not
			// home currency
			homeAmount = totalNetPremium;
			// TLF COAID
			if (PaymentChannel.TRANSFER.equals(payment.getPaymentChannel())) {
				coaCode = payment.getAccountBank() == null ? paymentDAO.findCheckOfAccountNameByCode(COACode.CHEQUE, branch.getId(), currencyCode)
						: paymentDAO.findCCOAByCode(payment.getAccountBank().getAcode(), branch.getId(), currencyCode);
			} else if (PaymentChannel.CASHED.equals(payment.getPaymentChannel())) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getId(), currencyCode);
			} else if (PaymentChannel.CHEQUE.equals(payment.getPaymentChannel())) {
				String coaCodeType = "";
				switch (payment.getReferenceType()) {
					case LIFE_POLICY:
					case LIFE_BILL_COLLECTION:
						LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
						product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
						if (ProductIDConfig.isGroupLife(product)) {
							coaCodeType = COACode.GROUP_LIFE_PAYMENT_ORDER;
						} else if (ProductIDConfig.isPublicLife(product)) {
							coaCodeType = COACode.ENDOWMENT_LIFE_PAYMENT_ORDER;
						} else if (ProductIDConfig.isSnakeBite(product)) {
							coaCodeType = COACode.SNAKE_BITE_PAYMENT_ORDER;
						} else if (ProductIDConfig.isSportMan(product)) {
							coaCodeType = COACode.SPORTMAN_PAYMENT_ORDER;
						} else {
							coaCodeType = COACode.LIFE_PAYMENT_ORDER;
						}
						break;
					case MEDICAL_POLICY:
					case HEALTH_POLICY:
					case HEALTH_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.HEALTH_PAYMENT_ORDER;
						break;
					case GROUP_MICRO_HEALTH:
					case MICRO_HEALTH_POLICY:
						coaCodeType = COACode.MICRO_HEALTH_PAYMENT_ORDER;
						break;
					case CRITICAL_ILLNESS_POLICY:
					case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.CRITICAL_ILLNESS_PAYMENT_ORDER;
						break;
					case PA_POLICY:
						coaCodeType = COACode.PA_PAYMENT_ORDER;
						break;
					case TRAVEL_PROPOSAL:
					case PERSON_TRAVEL_POLICY:
						coaCodeType = COACode.PERSON_TRAVEL_PAYMENT_ORDER;
						break;
					case GROUP_FARMER_PROPOSAL:
					case FARMER_POLICY:
						coaCodeType = COACode.FARMER_PAYMENT_ORDER;
						break;
					case SHORT_ENDOWMENT_LIFE_POLICY:
					case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_ENDOWMENT_PAYMENT_ORDER;
						break;
					case STUDENT_LIFE_POLICY:
					case STUDENT_LIFE_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.STUDENT_LIFE_PAYMENT_ORDER;
						break;
					case PUBLIC_TERM_LIFE_POLICY:
						coaCodeType = COACode.PUBLICTERMLIFE_PAYMENT_ORDER;
						break;
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_PAYMENT_ORDER;
						break;
					case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
						break;
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
						break;
					case SIMPLE_LIFE_POLICY:
					case SIMPLE_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SIMPLE_LIFE_INSURANCE_PAYMENT_ORDER;
						break;
					default:
						break;
				}
				coaCode = paymentDAO.findCheckOfAccountNameByCode(coaCodeType, branch.getId(), currencyCode);
			} else if (PaymentChannel.SUNDRY.equals(payment.getPaymentChannel())) {
				String coaCodeType = "";
				switch (payment.getReferenceType()) {
					case LIFE_POLICY:
					case LIFE_BILL_COLLECTION:
						coaCodeType = COACode.LIFE_SUNDRY;
						break;
					case MEDICAL_POLICY:
					case MEDICAL_BILL_COLLECTION:
						coaCodeType = COACode.MEDICAL_SUNDRY;
						break;
					case HEALTH_POLICY:
					case HEALTH_POLICY_BILL_COLLECTION:
					case GROUP_MICRO_HEALTH:
						coaCodeType = COACode.HEALTH_SUNDRY;
						break;
					case MICRO_HEALTH_POLICY:
						coaCodeType = COACode.MICRO_HEALTH_SUNDRY;
						break;
					case CRITICAL_ILLNESS_POLICY:
					case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.CRITICAL_ILLNESS_SUNDRY;
						break;
					case PA_POLICY:
						coaCodeType = COACode.PA_SUNDRY;
						break;
					case PERSON_TRAVEL_POLICY:
						coaCodeType = COACode.PERSON_TRAVEL_SUNDRY;
						break;
					case GROUP_FARMER_PROPOSAL:
					case FARMER_POLICY:
						coaCodeType = COACode.FARMER_SUNDRY;
						break;
					case TRAVEL_PROPOSAL:
						coaCodeType = COACode.TRAVEL_SUNDRY;
						break;
					case SHORT_ENDOWMENT_LIFE_POLICY:
					case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_ENDOWMENT_SUNDRY;
						break;
					case STUDENT_LIFE_POLICY:
					case STUDENT_LIFE_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.STUDENT_LIFE_SUNDRY;
						break;
					case PUBLIC_TERM_LIFE_POLICY:
						coaCodeType = COACode.PUBLICTERMLIFE_SUNDRY;
						break;
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_SUNDRY;
						break;
					case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_CREDIT_SUNDRY;
						break;
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_SUNDRY;
						break;
					case SIMPLE_LIFE_POLICY:
					case SIMPLE_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SIMPLE_LIFE_INSURANCE_SUNDRY;
						break;
					default:
						break;
				}
				coaCode = paymentDAO.findCheckOfAccountNameByCode(coaCodeType, branch.getId(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo, getNarrationPremium(payment, isRenewal), payment,
					isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			tlf.setPaymentChannel(payment.getPaymentChannel());
			paymentDAO.insertTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	// For interbranch
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CashDebitForPremiumForInterBranch(List<AccountPayment> accountPaymentList, String customerId, Branch userBranch, String tlfNo, boolean isRenewal,
			String currencyCode, SalePoint salePoint, Branch proposalBranch) {
		try {
			double totalNetPremium = 0;
			double homeAmount = 0;
			String coaCode = null;
			Product product;
			Payment payment = accountPaymentList.get(0).getPayment();
			// TLF Home Amount
			if (isRenewal) {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getRenewalNetPremium();
				}
			} else {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getNetPremium() - accountPayment.getPayment().getBankCharges();
				}
			}

			// TODO FIXME TOK don't set total amount as homeamount , wrong if
			// not
			// home currency
			homeAmount = totalNetPremium;
			// TLF COAID
			if (PaymentChannel.TRANSFER.equals(payment.getPaymentChannel())) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userBranch.getReceivableACName(), userBranch.getId(), currencyCode);
			} else if (PaymentChannel.CASHED.equals(payment.getPaymentChannel())) {
				// coaCode =
				// paymentDAO.findCheckOfAccountNameByCode(COACode.CASH,
				// branch.getBranchCode(), currencyCode);
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userBranch.getReceivableACName(), userBranch.getId(), currencyCode);
			} else if (PaymentChannel.CHEQUE.equals(payment.getPaymentChannel())) {
				String coaCodeType = "";
				switch (payment.getReferenceType()) {
					case LIFE_POLICY:
					case LIFE_BILL_COLLECTION:
						LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
						product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
						if (ProductIDConfig.isGroupLife(product)) {
							coaCodeType = COACode.GROUP_LIFE_PAYMENT_ORDER;
						} else if (ProductIDConfig.isPublicLife(product)) {
							coaCodeType = COACode.ENDOWMENT_LIFE_PAYMENT_ORDER;
						} else if (ProductIDConfig.isSnakeBite(product)) {
							coaCodeType = COACode.SNAKE_BITE_PAYMENT_ORDER;
						} else if (ProductIDConfig.isSinglePremiumEndowmentLife(product)) {
							coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_PAYMENT_ORDER;
						} else if (ProductIDConfig.isSportMan(product)) {
							coaCodeType = COACode.SPORTMAN_PAYMENT_ORDER;
						} else {
							coaCodeType = COACode.LIFE_PAYMENT_ORDER;
						}
						break;
					case MEDICAL_POLICY:
					case HEALTH_POLICY:
					case HEALTH_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.HEALTH_PAYMENT_ORDER;
						break;
					case GROUP_MICRO_HEALTH:
					case MICRO_HEALTH_POLICY:
						coaCodeType = COACode.MICRO_HEALTH_PAYMENT_ORDER;
						break;
					case CRITICAL_ILLNESS_POLICY:
					case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.CRITICAL_ILLNESS_PAYMENT_ORDER;
						break;
					case PA_POLICY:
						coaCodeType = COACode.PA_PAYMENT_ORDER;
						break;
					case TRAVEL_PROPOSAL:
					case PERSON_TRAVEL_POLICY:
						coaCodeType = COACode.PERSON_TRAVEL_PAYMENT_ORDER;
						break;
					case GROUP_FARMER_PROPOSAL:
					case FARMER_POLICY:
						coaCodeType = COACode.FARMER_PAYMENT_ORDER;
						break;
					case PUBLIC_TERM_LIFE_POLICY:
						coaCodeType = COACode.PUBLICTERMLIFE_PAYMENT_ORDER;
						break;
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_PAYMENT_ORDER;
						break;
					case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
						break;
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
						break;
					case SHORT_ENDOWMENT_LIFE_POLICY:
					case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_ENDOWMENT_PAYMENT_ORDER;
						break;
					case STUDENT_LIFE_POLICY:
					case STUDENT_LIFE_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.STUDENT_LIFE_PAYMENT_ORDER;
						break;
					default:
						break;
				}
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userBranch.getReceivableACName(), userBranch.getId(), currencyCode);
			} else if (PaymentChannel.SUNDRY.equals(payment.getPaymentChannel())) {
				String coaCodeType = "";
				switch (payment.getReferenceType()) {
					case LIFE_POLICY:
					case LIFE_BILL_COLLECTION:
						coaCodeType = COACode.LIFE_SUNDRY;
						break;
					case MEDICAL_POLICY:
					case MEDICAL_BILL_COLLECTION:
						coaCodeType = COACode.MEDICAL_SUNDRY;
						break;
					case HEALTH_POLICY:
					case HEALTH_POLICY_BILL_COLLECTION:
					case GROUP_MICRO_HEALTH:
						coaCodeType = COACode.HEALTH_SUNDRY;
						break;
					case MICRO_HEALTH_POLICY:
						coaCodeType = COACode.MICRO_HEALTH_SUNDRY;
						break;
					case CRITICAL_ILLNESS_POLICY:
					case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.CRITICAL_ILLNESS_SUNDRY;
						break;
					case PA_POLICY:
						coaCodeType = COACode.PA_SUNDRY;
						break;
					case PERSON_TRAVEL_POLICY:
						coaCodeType = COACode.PERSON_TRAVEL_SUNDRY;
						break;
					case GROUP_FARMER_PROPOSAL:
					case FARMER_POLICY:
						coaCodeType = COACode.FARMER_SUNDRY;
						break;
					case PUBLIC_TERM_LIFE_POLICY:
						coaCodeType = COACode.PUBLICTERMLIFE_SUNDRY;
						break;
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_SUNDRY;
						break;
					case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SINGLE_PREMIUM_CREDIT_SUNDRY;
						break;
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_SUNDRY;
						break;
					case TRAVEL_PROPOSAL:
						coaCodeType = COACode.TRAVEL_SUNDRY;
						break;
					case SHORT_ENDOWMENT_LIFE_POLICY:
					case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCodeType = COACode.SHORT_ENDOWMENT_SUNDRY;
						break;
					case STUDENT_LIFE_POLICY:
					case STUDENT_LIFE_POLICY_BILL_COLLECTION:
						coaCodeType = COACode.STUDENT_LIFE_SUNDRY;
						break;
					default:
						break;
				}
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userBranch.getReceivableACName(), userBranch.getId(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, homeAmount, customerId, proposalBranch.getId(), coaCode, tlfNo,
					getNarrationReceiablePremiumForInterBranch(payment, isRenewal, userBranch.getName()), payment, isRenewal, true);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			tlf.setPaymentChannel(payment.getPaymentChannel());
			paymentDAO.insertTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CashDebitForPremiumEndorsement(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String tlfNo, boolean isRenewal,
			String currenyCode, SalePoint salePoint) {
		try {

			double totalNetPremium = 0;
			double homeAmount = 0;
			String coaCode = null;
			Payment payment = accountPaymentList.get(0).getPayment();
			// TLF Home Amount
			if (isRenewal) {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getRenewalNetPremium();
				}
			} else {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getNetPremium();
				}
			}

			homeAmount = totalNetPremium;

			// TLF COAID
			if (PaymentChannel.TRANSFER.equals(payment.getPaymentChannel())) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.FIRE_PREMIUM, branch.getId(), currenyCode);
			} else if (PaymentChannel.CASHED.equals(payment.getPaymentChannel())) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getId(), currenyCode);
			} else if (PaymentChannel.CHEQUE.equals(payment.getPaymentChannel())) {
				String coaCodeType = "";
				switch (payment.getReferenceType()) {

					case LIFE_POLICY:
						coaCodeType = COACode.LIFE_PAYMENT_ORDER;
						break;
					case LIFE_BILL_COLLECTION:
						coaCodeType = COACode.LIFE_PAYMENT_ORDER;
						break;

					case PA_POLICY:
						coaCodeType = COACode.PA_PAYMENT_ORDER;
						break;
					default:
						break;
				}
				coaCode = paymentDAO.findCheckOfAccountNameByCode(coaCodeType, branch.getId(), currenyCode);
			}
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo, getNarrationPremium(payment, isRenewal), payment,
					isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			if (null != payment.getPaymentChannel()) {
				tlf.setPaymentChannel(payment.getPaymentChannel());
			}
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_PremiumCreditPayableForInterBranch(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal,
			String currenyCode, SalePoint salePoint, Branch proposalBranch) {
		try {

			double homeAmount = payment.getNetPremium() - payment.getBankCharges();
			if (isRenewal) {
				homeAmount = payment.getRenewalNetPremium();
			}
			String coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName, proposalBranch.getId(), currenyCode);
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo,
					getNarrationPremiumPayableForInterBranch(payment, proposalBranch.getName()), payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_PremiumCredit(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal, String currenyCode,
			SalePoint salePoint) {
		try {

			double homeAmount = payment.getNetPremium();
			if (isRenewal) {
				homeAmount = payment.getRenewalNetPremium();
			}
			String coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName, branch.getId(), currenyCode);
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo, getNarrationPremium(payment, isRenewal), payment,
					isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_PremiumCreditInterBranch(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal, String currenyCode,
			SalePoint salePoint) {
		try {

			double homeAmount = payment.getNetPremium();
			if (isRenewal) {
				homeAmount = payment.getRenewalNetPremium();
			}
			String coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName, branch.getId(), currenyCode);
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo, getNarrationPremium(payment, isRenewal), payment,
					isRenewal, true);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_UnderwritingExpenseCr(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal, String currenyCode) {
		try {
			// TLF Home Amount
			double homeAmount = payment.getNetPremium();
			if (isRenewal) {
				homeAmount = payment.getRenewalNetPremium();
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	// FIXME UNUSED
	// @Override
	// @Transactional(propagation = Propagation.REQUIRED)
	// public void addNewTLF_For_UnderwritingExpenseDr(Payment payment, String
	// customerId, Branch branch, String accountName, String tlfNo, boolean
	// isRenewal, String currenyCode) {
	// try {
	// // TLF Home Amount
	// double homeAmount = payment.getNetPremium();
	// if (isRenewal) {
	// homeAmount = payment.getRenewalNetPremium();
	// }
	// if (payment.getReferenceType().equals(PolicyReferenceType.CARGO_POLICY))
	// {
	// if (payment.getReferenceType().equals(PolicyReferenceType.CARGO_POLICY))
	// {
	// IPolicy policy =
	// cargoPolicyService.findCargoPolicyById(payment.getReferenceNo());
	// List<CoinsuredProductGroup> groups =
	// coinsuranceCompanyService.findCoinsuredProductGroupsByProductGroupId(policy.getProductGroup().getId());
	// CoinsuredProductGroup productGroup = groups.get(0);
	// double underwritingExpenses = homeAmount *
	// productGroup.getCommissionPercent() / 100;
	// double miunderwritingExpensesPremium = homeAmount *
	// groups.get(0).getPrecentage() / 100;
	// underwritingExpenses = underwritingExpenses -
	// miunderwritingExpensesPremium;
	// accountName = COACode.MARINE_COPREMIUM;
	// String coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName,
	// branch.getBranchCode(), currenyCode);
	// TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT,
	// underwritingExpenses, customerId, branch.getBranchCode(), coaCode, tlfNo,
	// getNarrationPremium(payment, isRenewal), payment, isRenewal);
	// TLF tlf = tlfBuilder.getTLFInstance();
	// setIDPrefixForInsert(tlf);
	// paymentDAO.insertTLF(tlf);
	// }
	// }
	//
	// } catch (DAOException e) {
	// throw new SystemException(e.getErrorCode(), "Faield to add a new TLF",
	// e);
	// }
	// }

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_PremiumDebit(Payment payment, String customerId, Branch branch, String accountName, boolean isEndorse, String tlfNo, boolean isClearing,
			boolean isRenewal, String currencyCode, SalePoint salePoint) {
		try {
			double netPremium = 0.0;
			if (isRenewal) {
				netPremium = payment.getRenewalNetPremium();
			} else {
				netPremium = payment.getNetPremium();
			}

			netPremium = netPremium * (isEndorse ? -1 : 1);
			double homeAmount = 0;
			// TLF Home Amount
			homeAmount = netPremium;

			// TLF COAID
			String coaCode = "";
			if (payment.getAccountBank() == null) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName, branch.getId(), currencyCode);
			} else {
				coaCode = paymentDAO.findCCOAByCode(payment.getAccountBank().getAcode(), branch.getId(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo, getNarrationPremium(payment, isRenewal), payment,
					isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			tlf.setClearing(isClearing);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLFPremiumForBank(Payment payment, String customerId, Branch branch, String accountName, boolean isEndorse, String tlfNo, boolean isClearing,
			boolean isRenewal, String currencyCode) {
		try {
			double netPremium = 0.0;
			if (isRenewal) {
				netPremium = payment.getRenewalTotalPremium() + payment.getStampFees();
			} else {
				netPremium = payment.getTotalPremium() + payment.getStampFees();
			}

			netPremium = netPremium * (isEndorse ? -1 : 1);
			double homeAmount = 0;
			// TLF Home Amount
			homeAmount = netPremium;

			// TLF COAID
			String coaCode = "";
			if (payment.getAccountBank() == null) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName, branch.getBranchCode(), currencyCode);
			} else {
				coaCode = payment.getAccountBank().getAcode();
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, homeAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, getNarrationPremium(payment, isRenewal),
					payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setClearing(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CashCreditForPremium(List<AccountPayment> accountPaymentList, String customerId, Branch branch, boolean isEndorse, String tlfNo, boolean isClearing,
			boolean isRenewal, String currencyCode, SalePoint salePoint) {
		try {

			double totalNetPremium = 0;
			double homeAmount = 0;
			String narration = null;
			String coaCode = null;
			Payment payment = accountPaymentList.get(0).getPayment();
			String enoNo = payment.getReceiptNo();
			Product product = null;

			if (isRenewal) {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getRenewalNetPremium();
				}
			} else {
				for (AccountPayment accountPayment : accountPaymentList) {
					totalNetPremium = totalNetPremium + accountPayment.getPayment().getNetPremium();
				}
			}
			totalNetPremium = totalNetPremium * (isEndorse ? -1 : 1);

			// TLF Home Amount
			homeAmount = totalNetPremium;

			// TLF COAID
			switch (payment.getPaymentChannel()) {
				case TRANSFER: {
					if (payment.getAccountBank() == null) {
						coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CHEQUE, branch.getId(), currencyCode);
					} else {
						coaCode = paymentDAO.findCCOAByCode(payment.getAccountBank().getAcode(), branch.getId(), currencyCode);
					}
				}
					break;
				case CASHED:
					coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getId(), currencyCode);
					break;
				case CHEQUE: {
					String coaCodeType = "";
					switch (payment.getReferenceType()) {

						case MEDICAL_POLICY:
						case HEALTH_POLICY:
						case HEALTH_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.HEALTH_PAYMENT_ORDER;
							break;
						case LIFE_POLICY:
						case LIFE_BILL_COLLECTION:
							LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
							product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
							if (ProductIDConfig.isGroupLife(product)) {
								coaCodeType = COACode.GROUP_LIFE_PAYMENT_ORDER;
							} else if (ProductIDConfig.isPublicLife(product)) {
								coaCodeType = COACode.ENDOWMENT_LIFE_PAYMENT_ORDER;
							} else if (ProductIDConfig.isSnakeBite(product)) {
								coaCodeType = COACode.SNAKE_BITE_PAYMENT_ORDER;
							} else if (ProductIDConfig.isSportMan(product)) {
								coaCodeType = COACode.SPORTMAN_PAYMENT_ORDER;
							} else {
								coaCodeType = COACode.LIFE_PAYMENT_ORDER;
							}
							break;
						case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
						case SHORT_ENDOWMENT_LIFE_POLICY:
							coaCodeType = COACode.SHORT_ENDOWMENT_PAYMENT_ORDER;
							break;
						case CRITICAL_ILLNESS_POLICY:
						case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.CRITICAL_ILLNESS_PAYMENT_ORDER;
							break;
						case MICRO_HEALTH_POLICY:
						case GROUP_MICRO_HEALTH:
							coaCodeType = COACode.MICRO_HEALTH_PAYMENT_ORDER;
							break;
						case PA_POLICY:
							coaCodeType = COACode.PA_PAYMENT_ORDER;
							break;
						case PERSON_TRAVEL_POLICY:
						case TRAVEL_PROPOSAL:
							coaCodeType = COACode.PERSON_TRAVEL_PAYMENT_ORDER;
							break;
						case PUBLIC_TERM_LIFE_POLICY:
							coaCodeType = COACode.PUBLICTERMLIFE_PAYMENT_ORDER;
							break;
						case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
						case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_PAYMENT_ORDER;
							break;
						case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
						case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
							break;
						case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
						case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
							break;
						case GROUP_FARMER_PROPOSAL:
						case FARMER_POLICY:
							coaCodeType = COACode.FARMER_PAYMENT_ORDER;
							break;
						case STUDENT_LIFE_POLICY:
						case STUDENT_LIFE_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.STUDENT_LIFE_PAYMENT_ORDER;
						case SIMPLE_LIFE_POLICY:
						case SIMPLE_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SIMPLE_LIFE_INSURANCE_PAYMENT_ORDER;
							break;
						default:
							break;
					}
					coaCode = paymentDAO.findCheckOfAccountNameByCode(coaCodeType, branch.getId(), currencyCode);
				}
					break;
				case SUNDRY: {
					String coaCodeType = "";
					switch (payment.getReferenceType()) {

						case LIFE_POLICY:
						case LIFE_BILL_COLLECTION:
							coaCodeType = COACode.LIFE_SUNDRY;
							break;

						case SHORT_ENDOWMENT_LIFE_POLICY:
						case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SHORT_ENDOWMENT_SUNDRY;
							break;
						case MEDICAL_BILL_COLLECTION:
						case MEDICAL_POLICY:
							coaCodeType = COACode.MEDICAL_SUNDRY;
							break;
						case HEALTH_POLICY:
						case HEALTH_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.HEALTH_SUNDRY;
							break;
						case MICRO_HEALTH_POLICY:
						case GROUP_MICRO_HEALTH:
							coaCodeType = COACode.MICRO_HEALTH_SUNDRY;
							break;
						case CRITICAL_ILLNESS_POLICY:
						case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.CRITICAL_ILLNESS_SUNDRY;
							break;
						case PA_POLICY:
							coaCodeType = COACode.PA_SUNDRY;
							break;
						case PERSON_TRAVEL_POLICY:
							coaCodeType = COACode.PERSON_TRAVEL_SUNDRY;
							break;
						case TRAVEL_PROPOSAL:
							coaCodeType = COACode.TRAVEL_SUNDRY;
							break;
						case GROUP_FARMER_PROPOSAL:
						case FARMER_POLICY:
							coaCodeType = COACode.FARMER_SUNDRY;
							break;
						case STUDENT_LIFE_POLICY:
						case STUDENT_LIFE_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.STUDENT_LIFE_SUNDRY;
							break;
						case PUBLIC_TERM_LIFE_POLICY:
							coaCodeType = COACode.PUBLICTERMLIFE_SUNDRY;
							break;
						case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
						case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_SUNDRY;
							break;
						case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
						case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SINGLE_PREMIUM_CREDIT_SUNDRY;
							break;
						case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
						case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_SUNDRY;
							break;
						case SIMPLE_LIFE_POLICY:
						case SIMPLE_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SIMPLE_LIFE_INSURANCE_SUNDRY;
							break;
						default:
							break;
					}
					coaCode = paymentDAO.findCheckOfAccountNameByCode(coaCodeType, branch.getId(), currencyCode);
				}
					break;
			}
			// TLF Narration
			narration = "Cash refund for " + enoNo;
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setClearing(isClearing);
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addTLFCoInsuSunaryPayment(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode) {
		try {
			double homeAmount = 0;
			String narration = null;
			String coaCode = null;
			TLFBuilder tlfBuilderDebit = null;
			TLFBuilder tlfBuilderCredit = null;
			TLF tlfDebit = null;
			TLF tlfCredit = null;
			// TLF Home Amount
			if (isRenewal) {
				homeAmount = payment.getRenewalTotalPremium();
			} else {
				homeAmount = payment.getTotalPremium();
			}

			coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.INLAND_SUNDRY, branch.getBranchCode(), currencyCode);
			narration = getNarrationCoInsuPayment(payment, isRenewal, homeAmount);
			if (PaymentChannel.CHEQUE.equals(payment.getPaymentChannel())) {
				String trantypeId = KeyFactorChecker.getTRCredit();
				tlfBuilderCredit = new TLFBuilder(trantypeId, homeAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, isRenewal);
				tlfCredit = tlfBuilderCredit.getTLFInstance();
				setIDPrefixForInsert(tlfCredit);
				paymentDAO.insertTLF(tlfCredit);
				trantypeId = KeyFactorChecker.getTRDebit();
				tlfBuilderDebit = new TLFBuilder(trantypeId, homeAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, isRenewal);
				tlfDebit = tlfBuilderDebit.getTLFInstance();
				setIDPrefixForInsert(tlfDebit);
				paymentDAO.insertTLF(tlfDebit);
			} else {
				String trantypeId = KeyFactorChecker.getTRDebit();
				tlfBuilderDebit = new TLFBuilder(trantypeId, homeAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, isRenewal);
				tlfDebit = tlfBuilderDebit.getTLFInstance();
				setIDPrefixForInsert(tlfDebit);
				paymentDAO.insertTLF(tlfDebit);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CoInsuCashPaymentCredit(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode) {
		try {
			double homeAmount = 0;
			String narration = null;
			String coaCode = null;
			// TLF Home Amount
			if (isRenewal) {
				homeAmount = payment.getRenewalNetPremium();
			} else {
				homeAmount = payment.getNetPremium();
			}
			// TLF COAID
			switch (payment.getReferenceType()) {

				case LIFE_POLICY:
					coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SUNDRY, branch.getId(), currencyCode);
					break;

				default:
					break;
			}
			// TLF Narration
			narration = getNarrationCoInsuPayment(payment, isRenewal, homeAmount);
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, homeAmount, customerId, branch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CoInsuCashPaymentDebit(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode) {
		try {
			double homeAmount = 0;
			String narration = null;
			String coaCode = null;
			// TLF Home Amount
			if (isRenewal) {
				homeAmount = payment.getRenewalNetPremium();
			} else {
				homeAmount = payment.getNetPremium();
			}
			// TLF COAID
			switch (payment.getReferenceType()) {

				case LIFE_POLICY:
					coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SUNDRY, branch.getBranchCode(), currencyCode);
					break;

				default:
					break;
			}
			// TLF Narration
			narration = getNarrationCoInsuPayment(payment, isRenewal, homeAmount);
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, homeAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addTLFCoInsuPreIncomeCr(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal, String currencyCode) {
		try {
			String coaCodeOwn = null;
			String coaCodeMI = null;
			String coaCodeUEMCR = null;
			String coaCodeUEICR = null;
			double totalPremium = 0.0;
			double ownNetPremium = 0.0;
			double miNetPremium = 0.0;
			double underwritingExpense = 0.0;
			TLFBuilder tlfBuilderOwn = null;
			TLFBuilder tlfBuilderMI = null;
			TLFBuilder tlfBuilderUEDR = null;
			TLFBuilder tlfBuilderUECR = null;
			List<TLF> tlfList = new ArrayList<TLF>();
			if (isRenewal) {
				totalPremium = payment.getRenewalTotalPremium();
			} else {
				totalPremium = payment.getTotalPremium();
			}
			String narration = getNarrationCoInsuIncome(payment, isRenewal, totalPremium);
			ownNetPremium = totalPremium - miNetPremium;
			coaCodeMI = paymentDAO.findCheckOfAccountNameByCode(COACode.MARINE_COPREMIUM, branch.getBranchCode(), currencyCode);

			String trantypeId = KeyFactorChecker.getTRCredit();
			tlfBuilderOwn = new TLFBuilder(trantypeId, ownNetPremium, customerId, branch.getBranchCode(), coaCodeOwn, tlfNo, narration, payment, isRenewal);
			tlfBuilderMI = new TLFBuilder(trantypeId, miNetPremium, customerId, branch.getBranchCode(), coaCodeMI, tlfNo, narration, payment, isRenewal);

			TLF tlfOwn = tlfBuilderOwn.getTLFInstance();
			setIDPrefixForInsert(tlfOwn);
			TLF tlfMI = tlfBuilderMI.getTLFInstance();
			setIDPrefixForInsert(tlfMI);
			TLF tlfUEDR = tlfBuilderUEDR.getTLFInstance();
			setIDPrefixForInsert(tlfUEDR);
			TLF tlfUECR = tlfBuilderUECR.getTLFInstance();
			setIDPrefixForInsert(tlfUECR);
			tlfList.add(tlfOwn);
			tlfList.add(tlfMI);
			tlfList.add(tlfUEDR);
			tlfList.add(tlfUECR);
			paymentDAO.insertTLFList(tlfList);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AgentCommissionDr(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			String receiptNo = payment.getReceiptNo();
			String coaCode = null;
			String coaCodeMI = null;
			String accountName = null;
			double ownCommission = 0.0;
			Product product;
			TLF tlf = new TLF();
			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					// coaCode = ProductIDConfig.isFarmer(product) ?
					// COACode.FARMER_AGENT_COMMISSION : coInsureance ?
					// COACode.LIFE_SUNDRY : COACode.LIFE_AGENT_COMMISSION;
					if (coInsureance) {
						coaCode = COACode.LIFE_SUNDRY;
					} else if (ProductIDConfig.isFarmer(product)) {
						coaCode = COACode.FARMER_AGENT_COMMISSION;
					} else if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_COMMISSION;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_COMMISSION;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_COMMISSION;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SNAKE_BITE_AGENT_COMMISSION;
					} else {
						coaCode = COACode.LIFE_AGENT_COMMISSION;
					}
					break;
				case SNAKE_BITE_POLICY:
					coaCode = COACode.SNAKE_BITE_AGENT_COMMISSION;
					break;
				case MEDICAL_POLICY:
				case HEALTH_POLICY:
				case MEDICAL_BILL_COLLECTION:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_COMMISSION;
					break;
				case MICRO_HEALTH_POLICY:
					coaCode = COACode.MICROHEALTH_AGENT_COMMISSION;
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_COMMISSION;
					break;
				// CARGO_POLICY is handled explicitly below

				case PA_POLICY:
					coaCode = COACode.PA_AGENT_COMMISSION;
					break;
				case PERSON_TRAVEL_POLICY:
					PersonTravelPolicy travelPolicy = personTravelPolicyService.findPersonTravelPolicyById(payment.getReferenceNo());
					product = travelPolicy.getProduct();
					if (ProductIDConfig.isUnder100MileTravelInsurance(product)) {
						coaCode = COACode.UNDER100_TRAVEL_AGENT_COMMISSION;
					} else {
						coaCode = COACode.GENERAL_TRAVEL_AGENT_COMMISSION;
					}
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					coaCode = COACode.PUBLICTERMLIFE_AGENT_COMMISSION;
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SINGLE_PREMIUM_ENDOWMENT_AGENT_COMMISSION;
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SINGLE_PREMIUM_CREDIT_AGENT_COMMISSION;
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_AGENT_COMMISSION;
					break;
				case SIMPLE_LIFE_POLICY:
				case SIMPLE_LIFE_BILL_COLLECTION:
					coaCode = COACode.SIMPLE_LIFE_INSURANCE_AGENT_COMMISSION;
					break;
				case GROUP_FARMER_PROPOSAL:
				case FARMER_POLICY:
					coaCode = COACode.FARMER_AGENT_COMMISSION;
					break;

				case SHORT_ENDOWMENT_LIFE_POLICY:
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SHORT_ENDOWMENT_AGENT_COMMISSION;
					break;
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.MICROHEALTH_AGENT_COMMISSION;
					break;
				case STUDENT_LIFE_POLICY:
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					coaCode = COACode.STUDENT_LIFE_AGENT_COMMISSION;
					break;
				default:
					break;
			}
			String cur = KeyFactorChecker.getKyatId();
			double rate = payment.getRate();
			String narration = getNarrationAgent(payment, ac, isRenewal);

			accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			ownCommission = Utils.getTwoDecimalPoint(ac.getCommission());
			if (ac.getReferenceType().equals(PolicyReferenceType.SNAKE_BITE_POLICY)) {
				tlf.setPaid(true);
			}

			String trantypeId = KeyFactorChecker.getTRDebit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, ownCommission, ac.getAgent().getId(), branch.getId(), accountName, receiptNo, narration, eno, ac.getReferenceNo(),
					ac.getReferenceType(), isRenewal, cur, rate);
			tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			tlf.setAgentTransaction(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AgentCommissionCr(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			String receiptNo = payment.getReceiptNo();
			String coaCode = null;
			String accountName = null;
			double commission = 0.0;
			Product product = null;

			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
					// coaCode = coInsureance ? COACode.LIFE_CO_AGENT_PAYABLE :
					// COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (coInsureance) {
						coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
					} else if (ProductIDConfig.isFarmer(product)) {
						coaCode = COACode.FARMER_AGENT_PAYABLE;
					} else if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else {
						coaCode = COACode.LIFE_AGENT_PAYABLE;
					}
					break;
				case SNAKE_BITE_POLICY:
					coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					break;
				case LIFE_BILL_COLLECTION:
					coaCode = COACode.LIFE_AGENT_PAYABLE;
					break;
				case MEDICAL_POLICY:
				case MEDICAL_BILL_COLLECTION:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
					break;
				case PA_POLICY:
					coaCode = COACode.PA_AGENT_PAYABLE;
					break;
				case GROUP_FARMER_PROPOSAL:
					coaCode = COACode.FARMER_AGENT_PAYABLE;
					break;
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.GROUP_MICRO_HEALTH_AGENT_PAYABLE;
				default:
					break;
			}

			accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getBranchCode(), currencyCode);
			commission = Utils.getTwoDecimalPoint(ac.getCommission());
			String narration = getNarrationAgent(payment, ac, isRenewal);
			String cur = payment.getCur();
			double rate = payment.getRate();
			String trantypeId = KeyFactorChecker.getTRCredit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, commission, ac.getAgent().getId(), branch.getBranchCode(), accountName, receiptNo, narration, eno,
					ac.getReferenceNo(), ac.getReferenceType(), isRenewal, cur, rate);
			TLF tlf = tlfBuilder.getTLFInstance();
			if (ac.getReferenceType().equals(PolicyReferenceType.SNAKE_BITE_POLICY)) {
				tlf.setPaid(true);
			}

			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AgentCommissionDebit(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			String receiptNo = payment.getReceiptNo();
			String coaCode = null;
			String accountName = null;
			double commission = 0.0;
			Product product = null;

			// only use for cargo currently
			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					// coaCode = coInsureance ? COACode.LIFE_CO_AGENT_PAYABLE :
					// COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (coInsureance) {
						coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
					} else if (ProductIDConfig.isFarmer(product)) {
						coaCode = COACode.FARMER_AGENT_PAYABLE;
					} else if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
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
				case SNAKE_BITE_POLICY:
					coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					break;
				case MEDICAL_POLICY:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.LIFE_AGENT_PAYABLE;
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
					break;
				case PA_POLICY:
					coaCode = COACode.PA_AGENT_PAYABLE;
					break;
				case GROUP_FARMER_PROPOSAL:
					coaCode = COACode.FARMER_AGENT_PAYABLE;
					break;
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.LIFE_AGENT_PAYABLE;
					break;
				default:
					break;
			}

			accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getBranchCode(), currencyCode);
			commission = Utils.getTwoDecimalPoint(ac.getCommission());
			String narration = getNarrationAgent(payment, ac, isRenewal);
			String cur = payment.getCur();
			double rate = payment.getRate();
			String trantypeId = KeyFactorChecker.getTRDebit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, commission, ac.getAgent().getId(), branch.getBranchCode(), accountName, receiptNo, narration, eno,
					ac.getReferenceNo(), ac.getReferenceType(), isRenewal, cur, rate);
			TLF tlf = tlfBuilder.getTLFInstance();
			if (ac.getReferenceType().equals(PolicyReferenceType.SNAKE_BITE_POLICY)) {
				tlf.setPaid(true);
			}
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AgentCommissionCredit(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			String receiptNo = payment.getReceiptNo();
			String coaCode = null;
			String accountName = null;
			double commission = 0.0;

			switch (ac.getReferenceType()) {
				case LIFE_BILL_COLLECTION:
				case LIFE_POLICY:
					LifePolicy policy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					Product product = policy.getPolicyInsuredPersonList().get(0).getProduct();
					// coaCode = ProductIDConfig.isFarmer(product) ?
					// COACode.FARMER_AGENT_PAYABLE : coInsureance ?
					// COACode.LIFE_CO_AGENT_PAYABLE :
					// COACode.LIFE_AGENT_PAYABLE;
					if (coInsureance) {
						coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
					} else if (ProductIDConfig.isFarmer(product)) {
						coaCode = COACode.FARMER_AGENT_PAYABLE;
					} else if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
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
				case SNAKE_BITE_POLICY:
					coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					break;
				case MEDICAL_POLICY:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case MEDICAL_BILL_COLLECTION:
					coaCode = COACode.LIFE_AGENT_PAYABLE;
					break;

				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
					break;
				case PA_POLICY:
					coaCode = COACode.PA_AGENT_PAYABLE;
					break;
				case PERSON_TRAVEL_POLICY:
					coaCode = COACode.PERSON_TRAVEL_AGENT_PAYABLE;
					break;
				case GROUP_FARMER_PROPOSAL:
				case FARMER_POLICY:
					coaCode = COACode.FARMER_AGENT_PAYABLE;
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SHORT_ENDOWMENT_AGENT_PAYABLE;
					break;
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.MICROHEALTH_AGENT_COMMISSION;
					break;
				case STUDENT_LIFE_POLICY:
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
					coaCode = COACode.STUDENT_LIFE_AGENT_PAYABLE;
					break;
				case PUBLIC_TERM_LIFE_POLICY:
					coaCode = COACode.PUBLICTERMLIFE_AGENT_PAYABLE;
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SINGLE_PREMIUM_ENDOWMENT_AGENT_PAYABLE;
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SINGLE_PREMIUM_CREDIT_AGENT_PAYABLE;
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					coaCode = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_AGENT_PAYABLE;
					break;
				case SIMPLE_LIFE_POLICY:
				case SIMPLE_LIFE_BILL_COLLECTION:
					coaCode = COACode.SIMPLE_LIFE_INSURANCE_AGENT_PAYABLE;
					break;
				default:
					break;
			}

			accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			commission = Utils.getTwoDecimalPoint(ac.getCommission());
			String narration = getNarrationAgent(payment, ac, isRenewal);
			String cur = KeyFactorChecker.getKyatId();
			double rate = payment.getRate();
			String trantypeId = KeyFactorChecker.getTRCredit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, commission, ac.getAgent().getId(), branch.getId(), accountName, receiptNo, narration, eno, ac.getReferenceNo(),
					ac.getReferenceType(), isRenewal, cur, rate);
			TLF tlf = tlfBuilder.getTLFInstance();
			if (ac.getReferenceType().equals(PolicyReferenceType.SNAKE_BITE_POLICY)) {
				tlf.setPaid(true);
			}

			setIDPrefixForInsert(tlf);
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			tlf.setAgentTransaction(true);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CashDebitForSCSTFees(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode, SalePoint salePoint) {
		try {
			String narration = null;
			String coaCode = null;
			Product product = null;
			if (payment.getServicesCharges() > 0 || payment.getStampFees() > 0) {
				if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
					coaCode = payment.getAccountBank() == null ? paymentDAO.findCheckOfAccountNameByCode(COACode.CHEQUE, branch.getId(), currencyCode)
							: payment.getAccountBank().getAcode();
				} else if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
					coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getId(), currencyCode);
				} else if (PaymentChannel.CHEQUE.equals(payment.getPaymentChannel())) {
					String coaCodeType = "";
					switch (payment.getReferenceType()) {
						case LIFE_POLICY:
						case LIFE_BILL_COLLECTION:
							LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
							product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
							if (ProductIDConfig.isGroupLife(product)) {
								coaCodeType = COACode.GROUP_LIFE_PAYMENT_ORDER;
							} else if (ProductIDConfig.isPublicLife(product)) {
								coaCodeType = COACode.ENDOWMENT_LIFE_PAYMENT_ORDER;
							} else if (ProductIDConfig.isSnakeBite(product)) {
								coaCodeType = COACode.SNAKE_BITE_PAYMENT_ORDER;
							} else if (ProductIDConfig.isSportMan(product)) {
								coaCodeType = COACode.SPORTMAN_PAYMENT_ORDER;
							} else {
								coaCodeType = COACode.LIFE_PAYMENT_ORDER;
							}
							break;
						case MEDICAL_POLICY:
						case MEDICAL_BILL_COLLECTION:
						case HEALTH_POLICY:
						case HEALTH_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.HEALTH_PAYMENT_ORDER;
							break;
						case CRITICAL_ILLNESS_POLICY:
						case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.CRITICAL_ILLNESS_PAYMENT_ORDER;
							break;
						case MICRO_HEALTH_POLICY:
							coaCodeType = COACode.MICRO_HEALTH_PAYMENT_ORDER;
							break;
						case PA_POLICY:
							coaCodeType = COACode.PA_PAYMENT_ORDER;
							break;
						case TRAVEL_PROPOSAL:
						case PERSON_TRAVEL_POLICY:
							coaCodeType = COACode.PERSON_TRAVEL_PAYMENT_ORDER;
							break;
						case SHORT_ENDOWMENT_LIFE_POLICY:
						case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SHORT_ENDOWMENT_PAYMENT_ORDER;
							break;
						case SNAKE_BITE_POLICY:
							coaCodeType = COACode.SNAKE_BITE_PAYMENT_ORDER;
							break;
						case GROUP_FARMER_PROPOSAL:
							coaCodeType = COACode.FARMER_PAYMENT_ORDER;
							break;
						case STUDENT_LIFE_POLICY:
						case STUDENT_LIFE_POLICY_BILL_COLLECTION:
							coaCodeType = COACode.STUDENT_LIFE_PAYMENT_ORDER;
							break;
						case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
						case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SINGLE_PREMIUM_ENDOWMENT_PAYMENT_ORDER;
							break;
						case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
						case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
							break;
						case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
						case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SHORT_TERM_SINGLE_PREMIUM_CREDIT_PAYMENT_ORDER;
							break;
						case SIMPLE_LIFE_POLICY:
						case SIMPLE_LIFE_BILL_COLLECTION:
							coaCodeType = COACode.SIMPLE_LIFE_INSURANCE_PAYMENT_ORDER;
							break;
						default:
							break;
					}
					coaCode = paymentDAO.findCheckOfAccountNameByCode(coaCodeType, branch.getId(), currencyCode);
				}
			}

			if (payment.getServicesCharges() > 0) {
				narration = getNarrationSCST(payment, isRenewal);
				double sericeCharges = Utils.getTwoDecimalPoint(payment.getServicesCharges());
				TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, sericeCharges, customerId, branch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
				TLF tlf = tlfBuilder.getTLFInstance();
				tlf.setPaymentChannel(payment.getPaymentChannel());
				tlf.setSalePoint(salePoint);
				setIDPrefixForInsert(tlf);
				paymentDAO.insertTLF(tlf);
			}

			if (payment.getStampFees() > 0) {
				narration = "Stamp fees for " + payment.getReceiptNo();
				double stampFees = Utils.getTwoDecimalPoint(payment.getStampFees());

				TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, stampFees, customerId, branch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
				TLF tlf = tlfBuilder.getTLFInstance();
				setIDPrefixForInsert(tlf);
				tlf.setPaymentChannel(payment.getPaymentChannel());
				tlf.setSalePoint(salePoint);
				paymentDAO.insertTLF(tlf);

			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_BankChargesDebit(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode, SalePoint salePoint) {
		try {
			String narration = null;
			if (payment.getBankCharges() > 0) {
				String coaCode = "";
				switch (payment.getReferenceType()) {

					default:
						coaCode = COACode.BANK_CHARGES;
						break;
				}
				currencyCode = KeyFactorChecker.getKyatId();
				String accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
				double bankCharges = Utils.getTwoDecimalPoint(payment.getBankCharges());
				narration = "Bank Charges for " + payment.getReceiptNo();
				TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, bankCharges, customerId, branch.getId(), accountName, tlfNo, narration, payment, isRenewal);
				TLF tlf = tlfBuilder.getTLFInstance();
				setIDPrefixForInsert(tlf);
				tlf.setPaymentChannel(payment.getPaymentChannel());
				tlf.setSalePoint(salePoint);
				paymentDAO.insertTLF(tlf);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLFForCargoStampFees(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode) {
		try {
			String narration = null;
			String coaCodeCR = null;
			TLFBuilder tlfBuilderCR = null;
			TLF tlfCR = null;
			coaCodeCR = paymentDAO.findCheckOfAccountNameByCode(COACode.CARGO_STAMP_FEES, branch.getBranchCode(), currencyCode);
			narration = "Stamp fees for " + payment.getReceiptNo();
			double stampFees = Utils.getTwoDecimalPoint(payment.getStampFees());
			if (PaymentChannel.CHEQUE.equals(payment.getPaymentChannel())) {
				String trantypeId = KeyFactorChecker.getTRCredit();
				tlfBuilderCR = new TLFBuilder(trantypeId, stampFees, customerId, branch.getBranchCode(), coaCodeCR, tlfNo, narration, payment, isRenewal);
				tlfCR = tlfBuilderCR.getTLFInstance();
				setIDPrefixForInsert(tlfCR);
				paymentDAO.insertTLF(tlfCR);
			} else {
				tlfBuilderCR = new TLFBuilder(DoubleEntry.CREDIT, stampFees, customerId, branch.getBranchCode(), coaCodeCR, tlfNo, narration, payment, isRenewal);
				tlfCR = tlfBuilderCR.getTLFInstance();
				setIDPrefixForInsert(tlfCR);
				paymentDAO.insertTLF(tlfCR);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_SCSTFees(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode, SalePoint salePoint) {
		try {
			String narration = null;
			if (payment.getServicesCharges() > 0) {
				String coaCode = null;
				String accountName = null;
				switch (payment.getReferenceType()) {

					case GROUP_FARMER_PROPOSAL:
					case TRAVEL_PROPOSAL:
					case PERSON_TRAVEL_POLICY:
					case SNAKE_BITE_POLICY:
					case LIFE_POLICY:
					case MEDICAL_POLICY:
					case HEALTH_POLICY:
					case HEALTH_POLICY_BILL_COLLECTION:
					case MICRO_HEALTH_POLICY:
					case CRITICAL_ILLNESS_POLICY:
					case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION:
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
					case SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION:
					case SIMPLE_LIFE_POLICY:
					case SIMPLE_LIFE_BILL_COLLECTION:
					case LIFE_BILL_COLLECTION:
						coaCode = COACode.LIFE_SERVICE_CHARGES;
						break;
					case SHORT_ENDOWMENT_LIFE_POLICY:
					case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
						coaCode = COACode.SHORT_ENDOWMENT_SERVICE_CHARGES;
						break;
					case STUDENT_LIFE_POLICY:
					case STUDENT_LIFE_POLICY_BILL_COLLECTION:
						coaCode = COACode.STUDENT_LIFE_SERVICE_CHARGES;
						break;
					default:
						break;
				}

				accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
				double serviceCharges = Utils.getTwoDecimalPoint(payment.getServicesCharges());
				narration = getNarrationSCST(payment, isRenewal);
				TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, serviceCharges, customerId, branch.getId(), accountName, tlfNo, narration, payment, isRenewal);
				TLF tlf = tlfBuilder.getTLFInstance();
				setIDPrefixForInsert(tlf);
				tlf.setPaymentChannel(payment.getPaymentChannel());
				tlf.setSalePoint(salePoint);
				paymentDAO.insertTLF(tlf);
			}

			if (payment.getStampFees() > 0) {
				String coaCode = "";
				switch (payment.getReferenceType()) {

					default:
						coaCode = COACode.STAMP_FEES;
						break;
				}
				String accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
				double stampFees = Utils.getTwoDecimalPoint(payment.getStampFees());
				narration = "Stamp fees for " + payment.getReceiptNo();
				TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, stampFees, customerId, branch.getId(), accountName, tlfNo, narration, payment, isRenewal);
				TLF tlf = tlfBuilder.getTLFInstance();
				setIDPrefixForInsert(tlf);
				tlf.setPaymentChannel(payment.getPaymentChannel());
				tlf.setSalePoint(salePoint);
				paymentDAO.insertTLF(tlf);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AGENT_COMMISSION_CREDIT(AgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			StringBuffer narration = new StringBuffer();
			narration.append("Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			if (ac.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				bankId = ac.getBank().getId();

				coaCode = paymentDAO.findCCOAByCode(ac.getBank().getAcode(), branch.getId(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
			} else if (ac.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getId(), currencyCode);
			} else if (ac.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				bankId = ac.getBank().getId();
				coaCode = paymentDAO.findCCOAByCode(ac.getBank().getAcode(), branch.getId(), currencyCode);
				// coaCode =
				// paymentDAO.findCheckOfAccountNameByCode(COACode.MANDALAY_INTERBRANCH,
				// branch.getBranchCode(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
				narration.append("Narration you want to append.");
			}
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getAgent().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
					ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getAgent().getId(), branch.getId(), coaCode, ac.getChequeNo(), bankId, null, narration.toString(),
					enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setAgentTransaction(false);
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_STAFF_COMMISSION_CREDIT(StaffAgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			StringBuffer narration = new StringBuffer();
			narration.append("Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			if (ac.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				bankId = ac.getBank().getId();

				coaCode = paymentDAO.findCCOAByCode(ac.getBank().getAcode(), branch.getId(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
			} else if (ac.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getId(), currencyCode);
			} else if (ac.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				bankId = ac.getBank().getId();
				coaCode = paymentDAO.findCCOAByCode(ac.getBank().getAcode(), branch.getId(), currencyCode);
				// coaCode =
				// paymentDAO.findCheckOfAccountNameByCode(COACode.MANDALAY_INTERBRANCH,
				// branch.getBranchCode(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
				narration.append("Narration you want to append.");
			}
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getStaff().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
					ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getStaff().getId(), branch.getId(), coaCode, ac.getChequeNo(), bankId, null, narration.toString(),
					enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setAgentTransaction(false);
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AGENT_COMMISSION_DEBIT(AgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint, boolean isInterBranch) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			Product product = null;
			StringBuffer narration = new StringBuffer();
			narration.append("Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			narration.append(" Narration you want to append.");
			if (ac.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = ac.getBank().getId();
			}

			boolean isCoinsurance = isCoInsurance(ac.getReferenceNo(), ac.getReferenceType());

			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					// coaCode = isCoinsurance ? COACode.LIFE_CO_AGENT_PAYABLE :
					// COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(ac.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (isCoinsurance) {
						coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
					}
					if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else {
						coaCode = COACode.LIFE_AGENT_PAYABLE;
					}
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MEDICAL_POLICY:
				case MEDICAL_BILL_COLLECTION:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
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

			String accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getAgent().getName().getFullName());
			TLFBuilder tlfBuilder = null;
			if (isInterBranch) {
				tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
						ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getAgent().getId(), branch.getId(), accountName, ac.getChequeNo(), bankId, null,
						narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false, true);
			} else {
				tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
						ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getAgent().getId(), branch.getId(), accountName, ac.getChequeNo(), bankId, null,
						narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);
			}

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			// pay agent
			tlf.setAgentTransaction(false);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_STAFF_COMMISSION_DEBIT(StaffAgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint, boolean isInterBranch) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			Product product = null;
			StringBuffer narration = new StringBuffer();
			narration.append("Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			narration.append(" Narration you want to append.");
			if (ac.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = ac.getBank().getId();
			}

			boolean isCoinsurance = isCoInsurance(ac.getReferenceNo(), ac.getReferenceType());

			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					// coaCode = isCoinsurance ? COACode.LIFE_CO_AGENT_PAYABLE :
					// COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(ac.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (isCoinsurance) {
						coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
					}
					if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else {
						coaCode = COACode.LIFE_AGENT_PAYABLE;
					}
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MEDICAL_POLICY:
				case MEDICAL_BILL_COLLECTION:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
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

			String accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getStaff().getName().getFullName());
			TLFBuilder tlfBuilder = null;
			if (isInterBranch) {
				tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
						ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getStaff().getId(), branch.getId(), accountName, ac.getChequeNo(), bankId, null,
						narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false, true);
			} else {
				tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
						ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getStaff().getId(), branch.getId(), accountName, ac.getChequeNo(), bankId, null,
						narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);
			}

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			// pay agent
			tlf.setAgentTransaction(false);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AGENT_COMMISSION_TAX_CREDIT(AgentCommission ac, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			StringBuffer narration = new StringBuffer();
			narration.append("Commission WithHolding Tax Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.AGENT_WITHHOLDING_TAX, branch.getId(), currencyCode);
			narration.append(" Narration you want to append.");
			narration.append(" amount of " + ac.getWithHoldingTax() + " as per attach, to " + ac.getAgent().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, ac.getPaymentChannel(), ac.getWithHoldingTax(), ac.getRate(), currencyCode, ac.getHomeWithHoldingTax(),
					ac.getAgent().getId(), branch.getId(), coaCode, ac.getChequeNo(), bankId, null, narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_STAFF_COMMISSION_TAX_CREDIT(StaffAgentCommission ac, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			StringBuffer narration = new StringBuffer();
			narration.append("Commission WithHolding Tax Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.AGENT_WITHHOLDING_TAX, branch.getId(), currencyCode);
			narration.append(" Narration you want to append.");
			narration.append(" amount of " + ac.getWithHoldingTax() + " as per attach, to " + ac.getStaff().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, ac.getPaymentChannel(), ac.getWithHoldingTax(), ac.getRate(), currencyCode, ac.getHomeWithHoldingTax(),
					ac.getStaff().getId(), branch.getId(), coaCode, ac.getChequeNo(), bankId, null, narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AGENT_COMMISSION_TAX_DEBIT(AgentCommission ac, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			Product product = null;
			StringBuffer narration = new StringBuffer();
			narration.append("Commission WithHolding Tax Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			narration.append(" Narration you want to append.");
			if (ac.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = ac.getBank().getId();
			}
			boolean isCoinsurance = isCoInsurance(ac.getReferenceNo(), ac.getReferenceType());

			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					coaCode = isCoinsurance ? COACode.LIFE_CO_AGENT_PAYABLE : COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(ac.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else {
						coaCode = COACode.LIFE_AGENT_PAYABLE;
					}
					break;
				case MEDICAL_POLICY:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
					break;
				case PA_POLICY:
					coaCode = COACode.PA_AGENT_PAYABLE;
					break;
				case GROUP_FARMER_PROPOSAL:
				case FARMER_POLICY:
					coaCode = COACode.FARMER_AGENT_PAYABLE;
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					coaCode = COACode.SHORT_ENDOWMENT_AGENT_PAYABLE;
					break;
				case PERSON_TRAVEL_POLICY:
					coaCode = COACode.PERSON_TRAVEL_AGENT_PAYABLE;
					break;
				default:
					break;
			}

			String accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			narration.append(" amount of " + ac.getWithHoldingTax() + " as per attach, to " + ac.getAgent().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getWithHoldingTax(), ac.getRate(), currencyCode, ac.getHomeWithHoldingTax(),
					ac.getAgent().getId(), branch.getId(), accountName, ac.getChequeNo(), bankId, null, narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(),
					false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_STAFF_COMMISSION_TAX_DEBIT(StaffAgentCommission ac, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			Product product = null;
			StringBuffer narration = new StringBuffer();
			narration.append("Commission WithHolding Tax Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			narration.append(" Narration you want to append.");
			if (ac.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = ac.getBank().getId();
			}
			boolean isCoinsurance = isCoInsurance(ac.getReferenceNo(), ac.getReferenceType());

			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					coaCode = isCoinsurance ? COACode.LIFE_CO_AGENT_PAYABLE : COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(ac.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else {
						coaCode = COACode.LIFE_AGENT_PAYABLE;
					}
					break;
				case MEDICAL_POLICY:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
					break;
				case PA_POLICY:
					coaCode = COACode.PA_AGENT_PAYABLE;
					break;
				case GROUP_FARMER_PROPOSAL:
				case FARMER_POLICY:
					coaCode = COACode.FARMER_AGENT_PAYABLE;
					break;
				case SHORT_ENDOWMENT_LIFE_POLICY:
					coaCode = COACode.SHORT_ENDOWMENT_AGENT_PAYABLE;
					break;
				case PERSON_TRAVEL_POLICY:
					coaCode = COACode.PERSON_TRAVEL_AGENT_PAYABLE;
					break;
				default:
					break;
			}

			String accountName = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getId(), currencyCode);
			narration.append(" amount of " + ac.getWithHoldingTax() + " as per attach, to " + ac.getStaff().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getWithHoldingTax(), ac.getRate(), currencyCode, ac.getHomeWithHoldingTax(),
					ac.getStaff().getId(), branch.getId(), accountName, ac.getChequeNo(), bankId, null, narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(),
					false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CoInsuCommReceived_Debit(Coinsurance c, Branch branch, double commission, String currencyCode) {
		try {
			String coaCode = null;
			String enoNo = c.getInvoiceNo();
			PolicyReferenceType rtype = null;
			if (c.getInsuranceType().equals(InsuranceType.LIFE)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SUNDRY, branch.getBranchCode(), currencyCode);
				rtype = PolicyReferenceType.LIFE_POLICY;
			}
			String narration = "10 Percent Coinsurance Commission receive from ( " + c.getCoinsuranceCompany().getName() + " )" + enoNo;

			String trantypeId = KeyFactorChecker.getTRDebit();

			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, commission, c.getCoinsuranceCompany().getId(), c.getBranch().getBranchCode(), coaCode, null, narration, enoNo,
					c.getPolicyNo(), rtype, false, null, 1.0);
			TLF tlf = tlfBuilder.getTLFInstance();

			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CoInsuCommReceived_Credit(Coinsurance c, Branch branch, double commission, String currencyCode) {
		try {
			String coaCode = null;
			String enoNo = c.getInvoiceNo();
			PolicyReferenceType rtype = null;
			if (c.getInsuranceType().equals(InsuranceType.LIFE)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_COINSURANCE_COMMISSION, branch.getBranchCode(), currencyCode);
				rtype = PolicyReferenceType.LIFE_POLICY;
			}
			String narration = "10 Percent Coinsurance Commission receive from ( " + c.getCoinsuranceCompany().getName() + " )" + enoNo;

			String trantypeId = KeyFactorChecker.getTRCredit();

			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, commission, c.getCoinsuranceCompany().getId(), c.getBranch().getBranchCode(), coaCode, null, narration, enoNo,
					c.getPolicyNo(), rtype, false, null, 1.0);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CoinsuCompanyPayment_Debit(Coinsurance c, Branch branch, double premium, String currencyCode) {
		try {
			String bankId = null;
			String coaCode = null;
			String enoNo = c.getInvoiceNo();
			PolicyReferenceType rtype = null;
			String narration = "Cash Payment to Coinsurance Company( " + c.getCoinsuranceCompany().getName() + " )" + enoNo;
			if (c.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = c.getCompanybank().getId();
			}
			if (c.getInsuranceType().equals(InsuranceType.LIFE)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SUNDRY, branch.getBranchCode(), currencyCode);
				rtype = PolicyReferenceType.LIFE_POLICY;
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, c.getPaymentChannel(), premium, c.getCoinsuranceCompany().getId(), c.getBranch().getBranchCode(), coaCode,
					c.getChequeNo(), bankId, null, narration, enoNo, c.getPolicyNo(), rtype, false);

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLFNetCoinsuPayment_Debit(Coinsurance c, Branch branch, double premium, String currencyCode) {
		try {
			String bankId = null;
			String coaCode = null;
			String enoNo = c.getInvoiceNo();
			PolicyReferenceType rtype = null;
			String narration = "Cash Payment to Coinsurance Company( " + c.getCoinsuranceCompany().getName() + " )" + enoNo;
			if (c.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = c.getCompanybank().getId();
			}
			coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.MARINE_COPREMIUM, branch.getBranchCode(), currencyCode);
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, c.getPaymentChannel(), premium, c.getCoinsuranceCompany().getId(), c.getBranch().getBranchCode(), coaCode,
					c.getChequeNo(), bankId, null, narration, enoNo, c.getPolicyNo(), rtype, false);

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_CoinsuCompanyPayment_Credit(Coinsurance c, Branch branch, double premium, String currencyCode) {
		try {
			String bankId = null;
			String coaCode = null;
			String enoNo = c.getInvoiceNo();
			PolicyReferenceType rtype = null;
			String narration = "Cash Payment to Coinsurance Company( " + c.getCoinsuranceCompany().getName() + " )" + enoNo;
			if (c.getPaymentChannel() == PaymentChannel.CHEQUE) {
				coaCode = c.getCompanybank().getAcode();
				bankId = c.getCompanybank().getId();
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getBranchCode(), currencyCode);
			}
			if (c.getInsuranceType().equals(InsuranceType.LIFE)) {
				rtype = PolicyReferenceType.LIFE_POLICY;
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, c.getPaymentChannel(), premium, c.getCoinsuranceCompany().getId(), c.getBranch().getBranchCode(), coaCode,
					c.getChequeNo(), bankId, null, narration, enoNo, c.getPolicyNo(), rtype, false);
			TLF tlf = tlfBuilder.getTLFInstance();

			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_InCoinsuPremiumIncome_Debit(Coinsurance c, Branch branch, String currencyCode) {
		try {
			String bankId = null;
			String coaCode = null;
			String enoNo = c.getInvoiceNo();
			PolicyReferenceType rtype = null;
			double premium = 0.0;
			String narration = "Cash Received from Coinsurance Company( " + c.getCoinsuranceCompany().getName() + " )" + enoNo;
			if (c.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				coaCode = c.getCustomerbank().getAcode();
				bankId = c.getCustomerbank().getId();
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getBranchCode(), currencyCode);
			}
			if (c.getInsuranceType().equals(InsuranceType.LIFE)) {
				rtype = PolicyReferenceType.LIFE_POLICY;
			}
			premium = Utils.getTwoDecimalPoint(c.getNetPremium());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, c.getPaymentChannel(), premium, c.getCoinsuranceCompany().getId(), c.getBranch().getBranchCode(), coaCode,
					c.getChequeNo(), bankId, null, narration, enoNo, c.getPolicyNo(), rtype, false);
			TLF tlf = tlfBuilder.getTLFInstance();

			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_InCoinsuPremiumIncome_Credit(Coinsurance c, Branch branch, String currencyCode) {
		try {
			String bankId = null;
			String coaCode = null;
			String enoNo = c.getInvoiceNo();
			PolicyReferenceType rtype = null;
			String narration = "Cash Received from Coinsurance Company( " + c.getCoinsuranceCompany().getName() + " )" + enoNo;
			double premium = 0.0;
			if (c.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = c.getCustomerbank().getId();
			}
			if (c.getInsuranceType().equals(InsuranceType.LIFE)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_COINSURANCE_PREMIUM, branch.getBranchCode(), currencyCode);
				rtype = PolicyReferenceType.LIFE_POLICY;
			}
			premium = Utils.getTwoDecimalPoint(c.getNetPremium());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, c.getPaymentChannel(), premium, c.getCoinsuranceCompany().getId(), c.getBranch().getBranchCode(), coaCode,
					c.getChequeNo(), bankId, null, narration, enoNo, c.getPolicyNo(), rtype, false);
			TLF tlf = tlfBuilder.getTLFInstance();

			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAgentCommission(List<AgentCommission> agentCommissionList, AgentCommission agentCommission, boolean isCheque, List<WorkFlowDTO> workFlowDTOList) {
		try {
			for (WorkFlowDTO workFlowDTO : workFlowDTOList) {
				workFlowDTOService.addNewWorkFlow(workFlowDTO);
			}
			String receiptNo = customIDGenerator.getNextId(SystemConstants.AGENT_COMMISSION_INVOICE_NO, null, true);
			for (AgentCommission ac : agentCommissionList) {
				ac.setInvoiceNo(receiptNo);
				if (isCheque) {
					ac.setBank(agentCommission.getBank());
					ac.setBankaccountno(agentCommission.getBankaccountno());
				}
				ac.setPaymentChannel(agentCommission.getPaymentChannel());
				paymentDAO.updateAgentCommissionInfo(ac);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Agent Commission", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStaffAgentCommission(List<StaffAgentCommission> agentCommissionList, StaffAgentCommission agentCommission, boolean isCheque,
			List<WorkFlowDTO> workFlowDTOList) {
		try {
			for (WorkFlowDTO workFlowDTO : workFlowDTOList) {
				workFlowDTOService.addNewWorkFlow(workFlowDTO);
			}
			String receiptNo = customIDGenerator.getNextId(SystemConstants.AGENT_COMMISSION_INVOICE_NO, null, true);
			for (StaffAgentCommission ac : agentCommissionList) {
				ac.setInvoiceNo(receiptNo);
				if (isCheque) {
					ac.setBank(agentCommission.getBank());
					ac.setBankaccountno(agentCommission.getBankaccountno());
				}
				ac.setPaymentChannel(agentCommission.getPaymentChannel());
				paymentDAO.updateStaffAgentCommissionInfo(ac);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addCashDeno(Payment payment, String customerId, Branch branch, String receiptDeno, String refundDeno) {
		try {
			CashDeno cashDeno = new CashDeno();
			cashDeno.setSourceBR(branch.getBranchCode());
			cashDeno.setAmount(payment.getTotalAmount());
			cashDeno.setFromType(payment.getReferenceType().name());
			cashDeno.setBranchCode(branch.getBranchCode());
			cashDeno.setDeno_detail(receiptDeno);
			cashDeno.setDenoRefund_detail(refundDeno);
			cashDeno.setSettlementDate(new Date());
			cashDeno.setCash_date(new Date());
			cashDeno.setStatus("R");
			cashDeno.setTlf_eno(payment.getReceiptNo());
			cashDeno.setUserNo(getLoginUser().getUsercode());
			cashDeno.setRate(1.0);
			cashDeno.setCur("KYT");
			cashDeno.setPrefix(getPrefix(CashDeno.class));
			paymentDAO.insertCashDeno(cashDeno);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCoinsuranceForInvoice(List<Coinsurance> coinsuranceList, List<WorkFlowDTO> workFlowDTOList) {
		try {
			for (WorkFlowDTO workFlowDTO : workFlowDTOList) {
				workFlowDTOService.addNewWorkFlow(workFlowDTO);
			}
			String receiptNo = null;
			for (Coinsurance c : coinsuranceList) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.COINSURANCE_INVOICE_NO, null, true);
				c.setInvoiceNo(receiptNo);
				paymentDAO.updateCoinsuranceForInvoice(c);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Coinsurance", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findCoInsuranceForDashboardByType(CoinsuranceType type, WorkflowTask workFlowTask) {
		List<Coinsurance> result = null;
		try {
			result = paymentDAO.findCoInsuranceForDashboardByType(type, workFlowTask);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Out CoInsurance For Payment Dashboard", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentInCoinsurance(Coinsurance coInsu, Branch branch, String currencyCode) {
		try {
			paymentDAO.updateCoinsurance(coInsu);
			addNewTLF_For_InCoinsuPremiumIncome_Debit(coInsu, branch, currencyCode);
			addNewTLF_For_InCoinsuPremiumIncome_Credit(coInsu, branch, currencyCode);
			workFlowDTOService.deleteWorkFlowByRefNo(coInsu.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update InCoinsurance After Payment", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByClaimProposalComplete(String proposalId, PolicyReferenceType referenceType) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findByClaimProposalComplete(proposalId, referenceType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PaymentList by Proposal Id : " + proposalId, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AgentPaymentCashReceiptDTO getAgentPaymentCashReceipt(Object proposal, InsuranceType insuranceType, Branch branch, double discountPercent) {
		AgentPaymentCashReceiptDTO ret = null;
		double agentCommission = 0.0;
		String agentExpenseAcountCode = null;
		String agentExpenseAccountName = null;
		String agentPayableAccountCode = null;
		String agentPayableAccountName = null;
		String currencyCode = "";
		// Motor
		switch (insuranceType) {

			case LIFE:
				currencyCode = KeyFactorChecker.getKyatId();
				/*
				 * agentExpenseAcountCode =
				 * paymentDAO.findCheckOfAccountNameByCode(COACode.
				 * LIFE_AGENT_COMMISSION, branch.getBranchCode(), currencyCode);
				 * agentExpenseAccountName =
				 * paymentDAO.findCOAAccountNameByCode(agentExpenseAcountCode);
				 * agentPayableAccountCode =
				 * paymentDAO.findCheckOfAccountNameByCode(COACode.
				 * LIFE_AGENT_PAYABLE, branch.getBranchCode(), currencyCode);
				 * agentPayableAccountName =
				 * paymentDAO.findCOAAccountNameByCode(agentPayableAccountCode);
				 */
				Product product = null;
				// Proposal
				if (proposal instanceof LifeProposal) {
					LifeProposal lifeProposal = (LifeProposal) proposal;
					List<ProposalInsuredPerson> proposalInsuredPersonList = lifeProposal.getProposalInsuredPersonList();
					product = proposalInsuredPersonList.get(0).getProduct();
					for (ProposalInsuredPerson pip : proposalInsuredPersonList) {
						double commissionPercent = product.getFirstCommission();
						if (commissionPercent > 0) {
							double totalPersonPremium = pip.getBasicTermPremium() + pip.getAddOnPremium();
							double commission = (totalPersonPremium / 100) * commissionPercent;
							agentCommission = agentCommission + commission;
						}
					}
				}
				// Policy
				if (proposal instanceof LifePolicy) {
					LifePolicy lifePolicy = (LifePolicy) proposal;
					int paymentType = 0;
					double commissionPercent = 0.0;
					Payment payment = paymentDAO.findPaymentByReferenceNoAndIsComplete(lifePolicy.getId(), false, false);
					if (payment != null) {
						int toterm = payment.getToTerm();
						int fromterm = payment.getFromTerm();
						paymentType = lifePolicy.getLifeProposal().getPaymentType().getMonth();
						List<PolicyInsuredPerson> policyInsuredPersonList = lifePolicy.getPolicyInsuredPersonList();
						product = policyInsuredPersonList.get(0).getProduct();
						int i = 0;
						int j = 0;
						for (PolicyInsuredPerson pip : policyInsuredPersonList) {
							int check = 12 / paymentType;
							j = toterm;
							for (i = fromterm; i <= j; i++) {
								if (i <= check) {
									commissionPercent = product.getFirstCommission();
								} else
									commissionPercent = product.getRenewalCommission();
								if (commissionPercent > 0) {
									double totalPremium = pip.getBasicTermPremium() + pip.getAddOnTermPremium();
									double commission = (totalPremium / 100) * commissionPercent;
									agentCommission = agentCommission + commission;
								}
							}
						}
					}
				}
				if (null != product) {
					if (ProductIDConfig.isPublicLife(product)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.ENDOWMENT_LIFE_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.ENDOWMENT_LIFE_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isShortEndowmentLife(product)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SHORT_ENDOWMENT_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SHORT_ENDOWMENT_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isFarmer(product)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.FARMER_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.FARMER_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isGroupLife(product)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.GROUP_LIFE_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.GROUP_LIFE_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isSportMan(product)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SPORTMAN_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SPORTMAN_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isSnakeBite(product)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SNAKE_BITE_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SNAKE_BITE_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isStudentLife(product)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.STUDENT_LIFE_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.STUDENT_LIFE_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					}

				} else {
					agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_AGENT_COMMISSION, branch.getId(), currencyCode);
					agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
					agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_AGENT_PAYABLE, branch.getId(), currencyCode);
					agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
				}

				ret = new AgentPaymentCashReceiptDTO(null, agentExpenseAcountCode, agentExpenseAccountName, agentCommission, agentPayableAccountCode, agentPayableAccountName,
						agentCommission, null);
				break;
			case MEDICAL:
				currencyCode = KeyFactorChecker.getKyatId();
				// TODO check if following coacodes are same for new medical
				// products
				/*
				 * agentExpenseAcountCode =
				 * paymentDAO.findCheckOfAccountNameByCode(COACode.
				 * HEALTH_AGENT_COMMISSION, branch.getBranchCode(),
				 * currencyCode); agentExpenseAccountName =
				 * paymentDAO.findCOAAccountNameByCode(agentExpenseAcountCode);
				 * agentPayableAccountCode =
				 * paymentDAO.findCheckOfAccountNameByCode(COACode.
				 * LIFE_AGENT_PAYABLE, branch.getBranchCode(), currencyCode);
				 * agentPayableAccountName =
				 * paymentDAO.findCOAAccountNameByCode(agentPayableAccountCode);
				 */
				Product medicalProduct = null;
				// Proposal
				if (proposal instanceof MedicalProposal) {
					MedicalProposal medicalProposal = (MedicalProposal) proposal;
					List<MedicalProposalInsuredPerson> proposalInsuredPersonList = medicalProposal.getMedicalProposalInsuredPersonList();
					double commissionPercent = proposalInsuredPersonList.get(0).getProduct().getFirstCommission();
					medicalProduct = proposalInsuredPersonList.get(0).getProduct();
					double totalPremium = 0.0;
					for (MedicalProposalInsuredPerson proposalInsuredPerson : proposalInsuredPersonList) {
						totalPremium += BusinessUtils.getTermPremium(proposalInsuredPerson.getTotalPremium(), medicalProposal.getPaymentType().getMonth());
					}
					if (commissionPercent > 0) {
						double totalPersonPremium = totalPremium;
						double commission = (totalPersonPremium / 100) * commissionPercent;
						agentCommission = agentCommission + commission;
					}
				}
				// Policy
				if (proposal instanceof MedicalPolicy) {
					MedicalPolicy medicalPolicy = (MedicalPolicy) proposal;
					int paymentType = 0;
					double commissionPercent = 0.0;
					Payment payment = paymentDAO.findPaymentByReferenceNoAndIsComplete(medicalPolicy.getId(), false, false);
					if (payment != null) {
						int toterm = payment.getToTerm();
						int fromterm = payment.getFromTerm();
						paymentType = medicalPolicy.getPaymentType().getMonth();
						List<MedicalPolicyInsuredPerson> medicalPolicyInsuredPersonList = medicalPolicy.getPolicyInsuredPersonList();
						medicalProduct = medicalPolicyInsuredPersonList.get(0).getProduct();
						int i = 0;
						int j = 0;
						int check = 12 / paymentType;
						j = toterm;
						for (i = fromterm; i <= j; i++) {
							if (i <= check) {
								commissionPercent = medicalPolicyInsuredPersonList.get(0).getProduct().getFirstCommission();
							} else
								commissionPercent = medicalPolicyInsuredPersonList.get(0).getProduct().getRenewalCommission();
							double premium = 0.0;
							double totalAddOnPremium = 0.0;
							for (MedicalPolicyInsuredPerson medicalPolicyInsuredPerson : medicalPolicyInsuredPersonList) {
								premium += BusinessUtils.getTermPremium(medicalPolicyInsuredPerson.getPremium(), paymentType);
								totalAddOnPremium += BusinessUtils.getTermPremium(medicalPolicyInsuredPerson.getAddOnPremium(), paymentType);
							}
							if (commissionPercent > 0) {
								double totalPremium = premium + totalAddOnPremium;
								double commission = (totalPremium / 100) * commissionPercent;
								agentCommission = agentCommission + commission;
							}
						}
					}
				}

				if (null != medicalProduct) {
					if (ProductIDConfig.isMicroHealthInsurance(medicalProduct)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.MICROHEALTH_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.MICRO_HEALTH_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isIndividualHealthInsurance(medicalProduct)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isGroupHealthInsurancae(medicalProduct)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isIndividualCriticalIllnessInsurance(medicalProduct)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CRITICAL_ILLNESS_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CRITICAL_ILLNESS_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isGroupCriticalIllnessInsurance(medicalProduct)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CRITICAL_ILLNESS_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CRITICAL_ILLNESS_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					} else if (ProductIDConfig.isMedicalInsurance(medicalProduct)) {
						agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_COMMISSION, branch.getId(), currencyCode);
						agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
						agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_PAYABLE, branch.getId(), currencyCode);
						agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
					}

				} else {
					agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_COMMISSION, branch.getId(), currencyCode);
					agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
					agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.HEALTH_AGENT_PAYABLE, branch.getId(), currencyCode);
					agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
				}
				ret = new AgentPaymentCashReceiptDTO(null, agentExpenseAcountCode, agentExpenseAccountName, agentCommission, agentPayableAccountCode, agentPayableAccountName,
						agentCommission, null);
				break;
			case PERSONAL_ACCIDENT:
				agentExpenseAcountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.PA_AGENT_COMMISSION, branch.getId(), currencyCode);
				agentExpenseAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentExpenseAcountCode);
				agentPayableAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.PA_AGENT_PAYABLE, branch.getId(), currencyCode);
				agentPayableAccountName = paymentDAO.findCOAAccountNameByCCOAID(agentPayableAccountCode);
				// Proposal
				if (proposal instanceof LifeProposal) {
					LifeProposal lifeProposal = (LifeProposal) proposal;
					ProposalInsuredPerson person = lifeProposal.getProposalInsuredPersonList().get(0);
					double commissionPercent = person.getProduct().getFirstCommission();
					if (commissionPercent > 0) {
						double totalPersonPremium = person.getBasicTermPremium() + person.getAddOnPremium();
						double commission = (totalPersonPremium / 100) * commissionPercent;
						agentCommission = agentCommission + commission;
					}
					currencyCode = person.getProduct().getCurrency().getId();
				}
				// Policy
				if (proposal instanceof LifePolicy) {
					LifePolicy lifePolicy = (LifePolicy) proposal;
					int paymentType = 0;
					double commissionPercent = 0.0;
					Payment payment = paymentDAO.findPaymentByReferenceNoAndIsComplete(lifePolicy.getId(), false, false);
					if (payment != null) {
						int toterm = payment.getToTerm();
						int fromterm = payment.getFromTerm();
						paymentType = lifePolicy.getLifeProposal().getPaymentType().getMonth();
						PolicyInsuredPerson person = lifePolicy.getPolicyInsuredPersonList().get(0);
						int i = 0;
						int j = 0;
						int check = 12 / paymentType;
						j = toterm;
						for (i = fromterm; i <= j; i++) {
							if (i <= check) {
								commissionPercent = person.getProduct().getFirstCommission();
							} else
								commissionPercent = person.getProduct().getRenewalCommission();
							if (commissionPercent > 0) {
								double totalPremium = person.getBasicTermPremium() + person.getAddOnTermPremium();
								double commission = (totalPremium / 100) * commissionPercent;
								agentCommission = agentCommission + commission;
							}
						}
					}
				}
				ret = new AgentPaymentCashReceiptDTO(null, agentExpenseAcountCode, agentExpenseAccountName, agentCommission, agentPayableAccountCode, agentPayableAccountName,
						agentCommission, null);
				break;
			default:
				break;
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PaymentOrderCashReceiptDTO getPaymentOrderCashReceipt(Object proposal, InsuranceType insuranceType, Branch branch, PaymentDTO payment) {
		PaymentOrderCashReceiptDTO ret = null;
		String suspendAccountCode = null;
		String suspendAccountName = null;
		String premiumIncomeAccountCode = null;
		String premiumIncomeAccountName = null;
		String suspendCoaCode = null;
		String currencyCode = null;
		if (proposal instanceof LifeProposal) {
			currencyCode = CurrencyUtils.getCurrencyCode(null);
		} else if (proposal instanceof PersonTravelProposal) {
			currencyCode = CurrencyUtils.getCurrencyCode(null);
		}
		if (insuranceType.equals(InsuranceType.LIFE)) {
			suspendAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_PAYMENT_ORDER, branch.getBranchCode(), currencyCode);
			suspendAccountName = paymentDAO.findCOAAccountNameByCCOAID(suspendAccountCode);
			if (proposal instanceof LifeProposal) {
				LifeProposal lifeproposal = (LifeProposal) proposal;
				if (lifeproposal.getProposalInsuredPersonList().get(0).getProduct().getId().equals(ProductIDConfig.getGroupLifeId())) {
					premiumIncomeAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.GROUP_LIFE_PREMIUM, branch.getBranchCode(), currencyCode);
					premiumIncomeAccountName = paymentDAO.findCOAAccountNameByCCOAID(premiumIncomeAccountCode);
				} else {
					premiumIncomeAccountCode = paymentDAO.findCheckOfAccountNameByCode(COACode.PUBLIC_LIFE_PREMIUM, branch.getBranchCode(), currencyCode);
					premiumIncomeAccountName = paymentDAO.findCOAAccountNameByCCOAID(premiumIncomeAccountCode);
				}

			}
		} else if (proposal instanceof PersonTravelProposal) {
			switch (payment.getPaymentChannel()) {
				case TRANSFER:
					suspendCoaCode = COACode.PERSON_TRAVEL_PAYMENT_ORDER;
					break;
				case CHEQUE:
					suspendCoaCode = payment.getAccountBank() != null ? payment.getAccountBank().getAcode() : null;
					break;
				default:
					break;
			}
			suspendAccountCode = paymentDAO.findCheckOfAccountNameByCode(suspendCoaCode, branch.getBranchCode(), currencyCode);
			suspendAccountName = paymentDAO.findCOAAccountNameByCCOAID(suspendAccountCode);
			String coaCode = null;
			if (ProductIDConfig.isUnder100MileTravelInsurance(((PersonTravelProposal) proposal).getProduct())) {
				coaCode = COACode.UNDER100_TRAVEL_AGENT_COMMISSION;
			} else {
				coaCode = COACode.GENERAL_TRAVEL_AGENT_COMMISSION;
			}
			premiumIncomeAccountCode = paymentDAO.findCheckOfAccountNameByCode(coaCode, branch.getBranchCode(), currencyCode);
			premiumIncomeAccountName = paymentDAO.findCOAAccountNameByCode(premiumIncomeAccountCode);
		}

		ret = new PaymentOrderCashReceiptDTO(null, suspendAccountCode, suspendAccountName, 0.0, premiumIncomeAccountCode, premiumIncomeAccountName, 0.0);
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_ReinstatementCredit(Payment payment, String customerId, Branch branch, String accountName, String currencyCode) {
		try {
			double reinstatementPremium = Utils.getTwoDecimalPoint(payment.getReinstatementPremium());
			String enoNo = payment.getReceiptNo();
			if (reinstatementPremium > 0) {
				String coaCode = null;
				String narration = null;

				/** for COACode **/
				switch (payment.getReferenceType()) {

					case LIFE_POLICY:
						coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName, branch.getBranchCode(), currencyCode);
						break;
					default:
						break;
				}

				/** for narration **/

				TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, reinstatementPremium, customerId, branch.getBranchCode(), coaCode, null, narration, payment, false);
				TLF tlf = tlfBuilder.getTLFInstance();
				setIDPrefixForInsert(tlf);
				// After payment, this will be updated.
				tlf.setPaid(false);
				tlf.setSettlementDate(null);
				paymentDAO.insertTLF(tlf);

			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_ServiceChargesCredit(Payment payment, String customerId, Branch branch, boolean processInConfirm, String currencyCode) {
		try {
			String tlfNo = payment.getReceiptNo();
			String enoNo = payment.getReceiptNo();
			double serviceCharges = Utils.getTwoDecimalPoint(payment.getServicesCharges());
			if (payment.getServicesCharges() > 0) {
				String coaCode = null;
				String narration = null;
				/** for COACode **/
				switch (payment.getReferenceType()) {
					case LIFE_CLAIM:
					case LIFE_DIS_CLAIM:
					case LIFE_SURRENDER_CLAIM:
					case SHORTTERMLIFE_SURRENDER_CLAIM:
					case MEDICAL_CLAIM:
						coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SERVICE_CHARGES, branch.getBranchCode(), currencyCode);
						break;

					default:
						break;
				}

				/** for narration **/
				switch (payment.getReferenceType()) {
					case LIFE_CLAIM:
					case LIFE_DIS_CLAIM:
					case LIFE_SURRENDER_CLAIM:
					case SHORTTERMLIFE_SURRENDER_CLAIM:
					case MEDICAL_CLAIM:
						narration = "Medical Claim service charges for " + enoNo;
						break;
					default:
						break;
				}

				String trantypeId = KeyFactorChecker.getTRCredit();
				TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, serviceCharges, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);
				TLF tlf = tlfBuilder.getTLFInstance();
				setIDPrefixForInsert(tlf);
				if (processInConfirm) {
					tlf.setPaid(false);
					tlf.setSettlementDate(null);
				}
				paymentDAO.insertTLF(tlf);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_ServiceChargesDebit(Payment payment, String customerId, Branch branch, boolean processInConfrim, String currencyCode) {
		try {
			String narration = null;
			String enoNo = payment.getReceiptNo();
			String tlfNo = payment.getReceiptNo();
			double serviceCharges = Utils.getTwoDecimalPoint(payment.getServicesCharges());

			if (serviceCharges > 0) {
				String coaCode = null;
				switch (payment.getReferenceType()) {
					case LIFE_CLAIM:
						coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_CLAIM, branch.getBranchCode(), currencyCode);
						narration = "Life Death Claim service charges for " + enoNo;
						break;
					case LIFE_DIS_CLAIM:
						coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_DISABILITY_CLAIM, branch.getBranchCode(), currencyCode);
						narration = "Life Death Claim service charges for " + enoNo;
						break;
					case MEDICAL_CLAIM:
						coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_CLAIM, branch.getBranchCode(), currencyCode);
						narration = "Medical  Claim service charges for " + enoNo;
						break;
					case LIFE_SURRENDER_CLAIM:
						coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SURRENDER_CLAIM, branch.getBranchCode(), currencyCode);
						narration = "Endowment surender service charges for " + enoNo;
						break;
					case SHORTTERMLIFE_SURRENDER_CLAIM:
						coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SHORTTERMLIFE_SURRENDER_CLAIM, branch.getBranchCode(), currencyCode);
						narration = "Short Term Endowment surender service charges for " + enoNo;
						break;

					default:
						break;
				}

				String trantypeId = KeyFactorChecker.getTRDebit();
				TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, serviceCharges, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);
				TLF tlf = tlfBuilder.getTLFInstance();
				setIDPrefixForInsert(tlf);
				if (processInConfrim) {
					tlf.setPaid(false);
					tlf.setSettlementDate(null);
				}
				paymentDAO.insertTLF(tlf);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFECLAIM_Debit(Payment payment, String customerId, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String tlfNo = payment.getReceiptNo();
			String enoNo = payment.getReceiptNo();
			String narration = "Cash Payment For" + payment.getReferenceType() + " " + enoNo;
			double claimAmount = Utils.getTwoDecimalPoint(payment.getNetClaimAmount());

			if (payment.getReferenceType().equals(PolicyReferenceType.LIFE_CLAIM) || payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_DEATH_CLAIM)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_CLAIM, branch.getBranchCode(), currencyCode);
			} else if (payment.getReferenceType().equals(PolicyReferenceType.LIFE_DIS_CLAIM) || payment.getReferenceType().equals(PolicyReferenceType.MEDICAL_CLAIM)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_DISABILITY_CLAIM, branch.getBranchCode(), currencyCode);
			}
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, claimAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);

			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFECLAIM_Credit(Payment payment, String customerId, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String enoNo = payment.getReceiptNo();
			String tlfNo = payment.getReceiptNo();
			String narration = "Cash Payment For" + payment.getReferenceType() + " " + enoNo;
			double claimAmount = Utils.getTwoDecimalPoint(payment.getNetClaimAmount());

			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				coaCode = paymentDAO.findCCOAByCode(payment.getBank().getAcode(), branch.getId(), currencyCode);
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getId(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, claimAmount, customerId, branch.getId(), coaCode, tlfNo, narration, payment, false);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFECLAIM_Premium_Debit(Payment payment, double premium, String customerId, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String enoNo = payment.getReceiptNo();
			String tlfNo = payment.getReceiptNo();
			String narration = "Life Claim Premium For" + payment.getReferenceType() + " " + enoNo;

			if (payment.getReferenceType().equals(PolicyReferenceType.LIFE_CLAIM)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_CLAIM, branch.getBranchCode(), currencyCode);
			} else if (payment.getReferenceType().equals(PolicyReferenceType.LIFE_DIS_CLAIM)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_DISABILITY_CLAIM, branch.getBranchCode(), currencyCode);
			}

			String trantypeId = KeyFactorChecker.getTRDebit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, premium, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);

			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFECLAIM_Premium_Credit(Payment payment, double premium, String customerId, Branch branch, String accountName, String currencyCode) {
		try {
			String enoNo = payment.getReceiptNo();
			String tlfNo = payment.getReceiptNo();
			String narration = "Life Claim Premium For" + payment.getReferenceType() + " " + enoNo;
			String coaCode = paymentDAO.findCheckOfAccountNameByCode(accountName, branch.getBranchCode(), currencyCode);

			String trantypeId = KeyFactorChecker.getTRCredit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, premium, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);

			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void extendPaymentTimes(List<Payment> paymentList, Branch branch, String currencyCode) {
		try {
			String receiptNo = null;
			PolicyReferenceType referenceType = null;
			MedicalPolicy medicalPolicy = null;
			LifePolicy lifePolicy = null;
			SalePoint salePoint = null;

			for (Payment payment : paymentList) {

				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
					receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
				} else if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
					receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
				} else {
					receiptNo = customIDGenerator.getNextId(SystemConstants.TRANSFER_RECEIPT_NO, null, true);
				}

				payment.setReceiptNo(receiptNo);
				payment.setPrefix(getPrefix(Payment.class));

				payment = paymentDAO.insert(payment);

				referenceType = payment.getReferenceType();

				if (referenceType.equals(PolicyReferenceType.MEDICAL_BILL_COLLECTION) || referenceType.equals(PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION)
						|| referenceType.equals(PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION)) {
					medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
					salePoint = medicalPolicy.getSalePoint();
					if (medicalPolicy.getAgent() != null) {
						addAgentcommissionMedicalProposalBillCollection(medicalPolicy, payment);
					}
				}

				if (referenceType.equals(PolicyReferenceType.LIFE_BILL_COLLECTION) || PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION.equals(referenceType)
						|| PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION.equals(referenceType)) {
					lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
					salePoint = lifePolicy.getSalePoint();
					/** Add Agent Commission TLF **/
					if (lifePolicy.getAgent() != null) {
						addAgentcommissionLifeProposalBillCollection(lifePolicy, payment);
					}
				}

				/** Add Payment TLF **/
				preActivateBillCollection(payment, branch);

				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
					List<Payment> tempList = new ArrayList<Payment>();
					tempList.add(payment);
					if (payment.getReferenceType().equals(PolicyReferenceType.LIFE_BILL_COLLECTION)
							|| PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION.equals(payment.getReferenceType())
							|| PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION.equals(payment.getReferenceType())) {
						prePaymentTransfer(lifePolicy.getLifeProposal(), tempList, branch, currencyCode, salePoint);
					} else {
						prePaymentTransfer(medicalPolicy.getMedicalProposal(), tempList, branch, currencyCode, salePoint);

					}
				}

			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to insert payment : " + e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private void addAgentcommissionLifeProposalBillCollection(LifePolicy policy, Payment payment) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			int paymentType = policy.getPaymentType().getMonth();
			double commissionPercent = 0.00;
			int toterm = payment.getToTerm();
			int fromterm = payment.getFromTerm();
			List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
			double totalCommission = 0.0;
			int i = 0;
			int j = 0;
			Product product = policy.getInsuredPersonInfo().get(0).getProduct();
			int check = 12 / paymentType;
			j = toterm;

			double extraAmount = 0.0;
			if (PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION.equals(payment.getReferenceType())) {
				ShortEndowmentExtraValue shortEndowmentExtraValue = shortEndowmentExtraValueService.findShortEndowmentExtraValue(policy.getId());
				if (shortEndowmentExtraValue != null) {
					extraAmount = shortEndowmentExtraValue.getExtraAmount();
				}
			}

			for (i = fromterm; i <= j; i++) {
				if (i <= check) {
					commissionPercent = product.getFirstCommission();
				} else
					commissionPercent = product.getRenewalCommission();

				if (commissionPercent > 0) {
					double totalPremium = policy.getTotalBasicTermPremium() + policy.getTotalAddOnTermPremium();

					if (extraAmount > 0) {
						totalPremium -= extraAmount;
						extraAmount = 0;
					}

					double commission = (totalPremium / 100) * commissionPercent;
					totalCommission = totalCommission + commission;
				}
			}

			String currencyId = product.getCurrency().getId();
			agentCommissionList.add(new AgentCommission(policy.getId(), payment.getReferenceType(), policy.getAgent(), totalCommission, new Date()));
			addAgentCommissionTLF(agentCommissionList, policy.getBranch(), payment, policy.getLifeProposal().getId(), false, currencyId, policy.getSalePoint());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Add Agentcommission TLF for LifeBillCollection ( LifePolicy Id :  " + policy.getId() + " )", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private void addAgentcommissionMedicalProposalBillCollection(MedicalPolicy policy, Payment payment) {
		try {
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			int paymentType = policy.getPaymentType().getMonth();
			double commissionPercent = 0.00;
			int toterm = payment.getToTerm();
			int fromterm = payment.getFromTerm();
			List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
			double totalCommission = 0.0;
			int i = 0;
			int j = 0;
			Product product = policy.getPolicyInsuredPersonList().get(0).getProduct();
			int check = 12 / paymentType;
			j = toterm;
			for (i = fromterm; i <= j; i++) {
				if (i <= check) {
					commissionPercent = product.getFirstCommission();
				} else
					commissionPercent = product.getRenewalCommission();

				if (commissionPercent > 0) {
					double totalPremium = policy.getBasicTermPremium() + policy.getAddonTermPremium();
					double commission = (totalPremium / 100) * commissionPercent;
					totalCommission = totalCommission + commission;
				}
			}
			String currencyId = product.getCurrency().getId();
			agentCommissionList.add(new AgentCommission(policy.getId(), payment.getReferenceType(), policy.getAgent(), totalCommission, new Date()));
			addAgentCommissionTLF(agentCommissionList, policy.getBranch(), payment, payment.getId(), false, currencyId, policy.getSalePoint());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Add Agentcommission TLF for LifeBillCollection ( LifePolicy Id :  " + policy.getId() + " )", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void preActivateBillCollection(Payment payment, Branch userBranch) {
		try {

			LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
			MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyById(payment.getReferenceNo());
			Branch policyBranch = lifePolicy == null ? medicalPolicy.getBranch() : lifePolicy.getBranch();
			String customerId = "";
			Product product = null;
			if (lifePolicy != null) {
				customerId = lifePolicy.getCustomer() != null ? lifePolicy.getCustomer().getId() : lifePolicy.getOrganization().getId();
				product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
			}
			if (medicalPolicy != null) {
				customerId = medicalPolicy.getCustomer() != null ? medicalPolicy.getCustomer().getId() : medicalPolicy.getOrganization().getId();
			}
			String accountCode = null;

			if (null != product && KeyFactorChecker.isSimpleLife(product)) {
				for (PolicyInsuredPersonKeyFactorValue vehKF : lifePolicy.getPolicyInsuredPersonList().get(0).getPolicyInsuredPersonkeyFactorValueList()) {
					if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {

						switch (vehKF.getValue()) {
							case "DEATH COVER":
								accountCode = "Death_Cover_Premium";
								break;
							case "DEATH AND TOTAL PERMANENT DISABLE (TPD)":
								accountCode = "Death_Total_Permanent _Disable_Premium";
								break;
							case "DEATH, ADDITIONAL COVER FOR ACCIDENTAL DEATH AND ACCIDENTAL TPD":
								accountCode = "Death_AdditionalCover_AccidentalDeath_TPD_Premium";
								break;
							case "DEATH AND TPD, ADDITIONAL COVER FOR ACCIDENTAL DEATH AND ACCIDENTAL TPD":
								accountCode = "Death_TPD_AdditionalCover_AccidentalDeath_TPD_Premium";
								break;
							case "ACCIDENTAL DEATH AND ACCIDENTAL TPD ONLY":
								accountCode = "AccidentalDeath_AccidentalTPDOnly_Premium";
								break;

							default:
								break;
						}
					}
				}
			} else {
				accountCode = lifePolicy == null ? medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getProductGroup().getAccountCode()
						: lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getProductGroup().getAccountCode();
			}
			String currencyId = lifePolicy == null ? medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getCurrency().getId()
					: lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getCurrency().getId();
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			String receiptNo = payment.getReceiptNo();
			accountPaymentList.add(new AccountPayment(accountCode, payment));
			SalePoint salePoint = (lifePolicy == null) ? medicalPolicy.getSalePoint() : lifePolicy.getSalePoint();

			// For Inter Branch
			if (!policyBranch.getId().equals(userBranch.getId())) {
				preActivatePaymentForInterBranch(accountPaymentList, customerId, userBranch, null, false, currencyId, salePoint, policyBranch);

			} else {
				addNewTLF_For_CashDebitForPremium(accountPaymentList, customerId, policyBranch, receiptNo, false, currencyId, salePoint);
				addNewTLF_For_PremiumCredit(payment, customerId, policyBranch, accountCode, receiptNo, false, currencyId, salePoint);

				addNewTLF_For_CashDebitForSCSTFees(payment, customerId, policyBranch, receiptNo, false, currencyId, salePoint);
				addNewTLF_For_SCSTFees(payment, customerId, policyBranch, receiptNo, false, currencyId, salePoint);

				addNewTLF_For_BankChargesDebit(payment, customerId, policyBranch, receiptNo, false, currencyId, salePoint);

			}
		} catch (DAOException e) {
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReferenceNoAndMaxDate(String referenceNo) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findPaymentByReferenceNoAndMaxDate(referenceNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ReferenceNo and Max PaymentDate : ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findPaymentByReferenceNo(String referenceNo) {
		Payment result = null;
		try {
			result = paymentDAO.findPaymentByReferenceNo(referenceNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ReferenceNo and Max PaymentDate : ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findPaymentByReferenceNoAndTermOne(String referenceNo) {
		Payment result = null;
		try {
			result = paymentDAO.findPaymentByReferenceNoAndTermOne(referenceNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ReferenceNo and Max PaymentDate : ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReferenceNoAndMaxDateForAgentInvoice(String referenceNo) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findPaymentByReferenceNoAndMaxDateForAgentInvoice(referenceNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ReferenceNo and Max PaymentDate : ", e);
		}
		return result;
	}

	private double calculatePremiumForCoInsuOut(Payment payment, List<AgentCommission> agentCommissionList) {
		PolicyReferenceType referenceType = payment.getReferenceType();
		double maxSumInsured = 0.0;
		double premiumPercent = 0.0;
		double agentCommission = 0.0;
		double totalNetPremium = payment.getNetPremium();
		double coOutPremium = 0.0;

		if (referenceType.equals(PolicyReferenceType.LIFE_POLICY)) {
			LifePolicy policy = lifePolicyService.findLifePolicyById(payment.getReferenceNo());
			maxSumInsured = policy.getPolicyInsuredPersonList().get(0).getProduct().getProductGroup().getMaxSumInsured();
			if (policy.getAgent() != null) {
				totalNetPremium = totalNetPremium - policy.getAgentCommission();
			}
			premiumPercent = Utils.getTwoDecimalPercent(maxSumInsured, policy.getTotalSumInsured());
			totalNetPremium = Utils.getPercentOf(premiumPercent, totalNetPremium);
		}
		if (agentCommissionList != null && !agentCommissionList.isEmpty()) {
			for (AgentCommission commission : agentCommissionList) {
				if (commission.getReferenceNo().equals(payment.getReferenceNo())) {
					agentCommission = commission.getCommission();
				}
			}
		}
		coOutPremium = payment.getNetPremium() - (totalNetPremium + agentCommission);
		coOutPremium = Utils.getTwoDecimalPoint(coOutPremium);
		return coOutPremium;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_SundryDr(Payment payment, String customerId, Branch branch, List<AgentCommission> agentCommissionList, String tlfNo, boolean isRenewal,
			String currencyCode) {
		try {

			String coaCode = null;
			double coOutPremium = calculatePremiumForCoInsuOut(payment, agentCommissionList);
			switch (payment.getReferenceType()) {

				case LIFE_POLICY:
					coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SUNDRY, branch.getId(), currencyCode);
					break;

				default:
					break;
			}
			String narration = getNarrationInterBranch(payment, branch.getName());
			String trantypeId = KeyFactorChecker.getTRDebit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, coOutPremium, customerId, branch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_InterBranchCr(Payment payment, String customerId, Branch branch, List<AgentCommission> agentCommissionList, String tlfNo, boolean isRenewal,
			String currencyCode) {
		try {

			String coaCode = null;
			double coOutPremium = calculatePremiumForCoInsuOut(payment, agentCommissionList);
			coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.INTER_BRANCH_HO, branch.getId(), currencyCode);
			String narration = getNarrationInterBranch(payment, branch.getName());
			String trantypeId = KeyFactorChecker.getTRCredit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, coOutPremium, customerId, branch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_SundryCr(Payment payment, String customerId, Branch branch, List<AgentCommission> agentCommissionList, String tlfNo, boolean isRenewal,
			String currencyCode) {
		try {

			String coaCode = null;
			double coOutPremium = calculatePremiumForCoInsuOut(payment, agentCommissionList);
			Branch ygnBranch = branchService.findBranchById(ProductIDConfig.getYangonBranchId());
			switch (payment.getReferenceType()) {

				case LIFE_POLICY:
					coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SUNDRY, branch.getId(), currencyCode);
					break;

				default:
					break;
			}
			String narration = getNarrationInterBranch(payment, branch.getName());

			String trantypeId = KeyFactorChecker.getTRCredit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, coOutPremium, customerId, ygnBranch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_InterBranchDr(Payment payment, String customerId, Branch branch, List<AgentCommission> agentCommissionList, String tlfNo, boolean isRenewal,
			String currencyCode) {
		try {

			String coaCode = null;
			double coOutPremium = calculatePremiumForCoInsuOut(payment, agentCommissionList);
			coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.MANDALAY_INTERBRANCH, branch.getId(), currencyCode);
			Branch ygnBranch = branchService.findBranchById(ProductIDConfig.getYangonBranchId());
			String narration = getNarrationInterBranch(payment, branch.getName());
			String trantypeId = KeyFactorChecker.getTRDebit();
			TLFBuilder tlfBuilder = new TLFBuilder(trantypeId, coOutPremium, customerId, ygnBranch.getId(), coaCode, tlfNo, narration, payment, isRenewal);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void activateSnakeBitePayment(Payment payment, String customerId, Branch branch, List<AgentCommission> agentCommissionList, String currencyCode, SalePoint salePoint) {
		try {
			payment.setComplete(true);
			payment.setPaymentDate(new Date());
			payment = paymentDAO.update(payment);
			// Add TLF For Premium
			addNewTLF_For_PremiumDebitSnakeBite(payment, customerId, branch, currencyCode);
			addNewTLF_For_PremiumCreditSnakeBite(payment, customerId, branch, currencyCode);

			// Add Agent_Commission
			if (agentCommissionList != null && !agentCommissionList.isEmpty() && agentCommissionList.size() > 0) {
				addAgentCommission(agentCommissionList, branch, payment, false, currencyCode, salePoint);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_PremiumDebitSnakeBite(Payment payment, String customerId, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String enoNo = payment.getReceiptNo();
			String narration = "Cash receipt for " + enoNo;
			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CHEQUE, branch.getBranchCode(), currencyCode);
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getBranchCode(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, payment.getNetPremium(), customerId, branch.getBranchCode(), coaCode, null, narration, payment, false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_PremiumCreditSnakeBite(Payment payment, String customerId, Branch branch, String currencyCode) {
		try {
			String enoNo = payment.getReceiptNo();
			String narration = "Cash receipt for " + enoNo;
			String coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SNAKE_BITE_PREMIUM, branch.getBranchCode(), currencyCode);

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, payment.getNetPremium(), customerId, branch.getBranchCode(), coaCode, null, narration, payment, false);
			TLF tlf = tlfBuilder.getTLFInstance();

			tlf.setPaid(true);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReceiptNo(String receiptNo) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findPaymentByReceiptNo(receiptNo);
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Fail findPaymentByReceiptNo()");
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findBCPaymentByReceiptNo(PolicyCriteria policyCriteria) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findBCPaymentByReceiptNo(policyCriteria);
			if (result != null && !result.isEmpty() && !paymentDAO.isLatestBCReceiptNo(policyCriteria.getCriteriaValue())) {
				throw new SystemException(ErrorCode.NO_LATEST_BC_RECEIPTNO, "");
			}
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Fail findPaymentByReceiptNo()");
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findBCPaymentByPolicyNo(String policyNo, ReferenceType referenceType) {
		try {
			return paymentDAO.findAllBCPaymentByPolicyNo(policyNo, referenceType);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Fail findPaymentByReceiptNo()");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void activatePaymentTransfer(List<AccountPayment> accountPaymentList, String customerId, Branch branch, boolean isRenewal, String currencyCode, SalePoint salePoint) {
		try {
			String tlfNo = accountPaymentList.get(0).getPayment().getReceiptNo();
			addNewTLF_For_CashCreditForPremium(accountPaymentList, customerId, branch, false, tlfNo, true, isRenewal, currencyCode, salePoint);
			for (AccountPayment accountPayment : accountPaymentList) {
				Payment payment = accountPayment.getPayment();
				if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
					payment.setPO(false);
					payment = paymentDAO.update(payment);
				}
				addNewTLF_For_PremiumDebit(payment, customerId, branch, accountPayment.getAcccountName(), false, tlfNo, true, isRenewal, currencyCode, salePoint);
			}
			Payment payment = accountPaymentList.get(0).getPayment();
			addNewTLF_For_BankChargesDebit(payment, customerId, branch, tlfNo, isRenewal, currencyCode, salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void activateCargoPaymentTransfer(List<AccountPayment> accountPaymentList, String customerId, Branch branch, boolean isRenewal, String currencyCode,
			SalePoint salePoint) {
		try {
			String tlfNo = accountPaymentList.get(0).getPayment().getReceiptNo();
			for (AccountPayment accountPayment : accountPaymentList) {
				Payment payment = accountPayment.getPayment();
				if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
					payment.setPO(false);
					payment = paymentDAO.update(payment);
				}
				// Bank DR
				addNewTLF_For_PremiumDebit(payment, customerId, branch, accountPayment.getAcccountName(), false, tlfNo, true, isRenewal, currencyCode, salePoint);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update the actual Payment", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TLF> findTLFbyTLFNo(String tlfNo, Boolean isClearing) {
		List<TLF> results;
		try {
			results = paymentDAO.findTLFbyTLFNo(tlfNo, isClearing);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Fail findPaymentByReceiptNo()");
		}

		return results;
	}

	@Override
	public List<TLF> findTLFbyENONo(String enoNo) {
		List<TLF> results;
		try {
			results = paymentDAO.findTLFbyENONo(enoNo);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Fail findPaymentByReceiptNo()");
		}

		return results;
	}

	@Override
	public List<TLF> findTLFbyReferenceNoAndReferenceType(String referenceNo, PolicyReferenceType policyReferenceType) {
		List<TLF> results;
		try {
			results = paymentDAO.findTLFbyReferenceNoAndReferenceType(referenceNo, policyReferenceType);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Fail findPaymentByReceiptNo()");
		}

		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TLF updateTLF(TLF tlf) {
		try {
			tlf = paymentDAO.updateTLF(tlf);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Organization", e);
		}

		return tlf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePayments(List<Payment> paymentList) {
		try {
			paymentDAO.deletePayments(paymentList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a Customer", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClaimVoucherDTO> findClaimVoucher(String receiptNo, String damage) {
		List<ClaimVoucherDTO> claimVoucherList = null;
		try {
			claimVoucherList = paymentDAO.findClaimVoucher(receiptNo, damage);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find claimVoucher", e);
		}

		return claimVoucherList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentTransfer(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch, String currencyCode, SalePoint salePoint) {
		try {
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			AccountPayment accountPayment;
			for (Payment payment : paymentList) {
				accountPayment = new AccountPayment(payment.getAccountBank().getAcode(), payment);
				accountPaymentList.add(accountPayment);
			}
			activatePaymentTransfer(accountPaymentList, lifeProposal.getCustomerId(), branch, false, currencyCode, salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a LifeProposal ID : " + lifeProposal.getId(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentTransfer(MedicalProposal medicalProposal, List<Payment> paymentList, Branch branch, String currencyCode, SalePoint salePoint) {
		try {
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			AccountPayment accountPayment;
			String customerId = medicalProposal.getCustomer() != null ? medicalProposal.getCustomer().getId() : medicalProposal.getOrganization().getId();
			for (Payment payment : paymentList) {
				accountPayment = new AccountPayment(payment.getAccountBank().getAcode(), payment);
				accountPaymentList.add(accountPayment);
			}
			activatePaymentTransfer(accountPaymentList, customerId, branch, false, currencyCode, salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a LifeProposal ID : " + medicalProposal.getId(), e);
		}
	}

	private void setIDPrefixForInsert(TLF tlf) {
		String tlfPrefix = customIDGenerator.getPrefix(TLF.class, false);
		tlf.setPrefix(tlfPrefix);
	}

	@Override
	public Payment findPaymentByReferenceNoAndIsComplete(String referenceNo, Boolean complete) {
		Payment p = new Payment();
		try {
			p = paymentDAO.findPaymentByReferenceNoAndIsComplete(referenceNo, complete, false);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find tlfVoucher", e);
		}

		return p;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findByChalanNo(String chalanNo) {
		Payment result = null;
		try {
			result = paymentDAO.findByChalanNo(chalanNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ChalanNo : ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentTableDTO> findByChalanNoForClaim(List<String> receiptList, PaymentReferenceType referenceType, Boolean complete) {
		List<PaymentTableDTO> result = null;
		try {
			result = paymentDAO.findByChalanNoForClaim(receiptList, referenceType, complete);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ChalanNo : ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void activateMedicalClaimPayment(Payment payment) {
		try {
			paymentDAO.update(payment);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Payment by ChalanNo :", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFESURRENDERCLAIM_Debit(Payment payment, String customerId, Branch branch, String currencyCode, String proposalNo, SalePoint salePoint) {
		try {
			String coaCode = null;
			String tlfNo = payment.getReceiptNo();
			String enoNo = payment.getReceiptNo();
			String narration = "Cash Payment For" + payment.getReferenceType() + " " + enoNo;
			double claimAmount = Utils.getTwoDecimalPoint(payment.getClaimAmount());
			if (payment.getReferenceType() == PolicyReferenceType.LIFE_SURRENDER_CLAIM) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_SURRENDER_CLAIM, branch.getBranchCode(), currencyCode);
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SHORTTERMLIFE_SURRENDER_CLAIM, branch.getBranchCode(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, claimAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setAgentTransaction(false);
			tlf.setPolicyNo(proposalNo);
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF for LIFESURRENDERCLAIM_Debit", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFESURRENDERCLAIM_Credit(Payment payment, String customerId, Branch branch, String currencyCode, String proposalNo, SalePoint salePoint) {
		try {
			String coaCode = null;
			String enoNo = payment.getReceiptNo();
			String tlfNo = payment.getReceiptNo();
			String narration = "Cash Payment For" + payment.getReferenceType() + " " + enoNo;
			double claimAmount = Utils.getTwoDecimalPoint(payment.getClaimAmount());

			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				coaCode = payment.getBank().getAcode();
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getBranchCode(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, claimAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaymentChannel(payment.getPaymentChannel());
			tlf.setAgentTransaction(false);
			tlf.setPolicyNo(proposalNo);
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF For LIFESURRENDERCLAIM_Credit", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<PaymentTrackDTO> findPaymentTrack(String policyNo) {
		List<PaymentTrackDTO> paymentTrackList = null;
		try {
			paymentTrackList = paymentDAO.findPaymentTrack(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find payment track for policyNo=" + policyNo, e);
		}

		return paymentTrackList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> prePaymentAndTlfLifePaidUp(List<Payment> paymentList, LifePaidUpProposal paidUpProposal) {
		try {
			String customerId = paidUpProposal.getLifePolicy().getCustomerId();
			Branch branch = paidUpProposal.getBranch();
			String receiptNo = null;
			Payment pay = paymentList.get(0);
			if (paymentList.get(0).getPaymentChannel().equals(PaymentChannel.CASHED)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
			} else if (paymentList.get(0).getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
			}

			for (Payment payment : paymentList) {
				payment.setReceiptNo(receiptNo);
				payment.setPrefix(getPrefix(Payment.class));
				payment = paymentDAO.insert(payment);
			}
			addNewTLF_For_LIFEPAIDUPCLAIM_Debit(pay, customerId, branch, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_LIFEPAIDUPCLAIM_Credit(pay, customerId, branch, CurrencyUtils.getCurrencyCode(null));

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment for PaidUp Proposal", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFEPAIDUPCLAIM_Debit(Payment payment, String customerId, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String tlfNo = payment.getReceiptNo();
			String enoNo = payment.getReceiptNo();
			String narration = "Cash Payment For" + payment.getReferenceType() + " " + enoNo;
			double claimAmount = Utils.getTwoDecimalPoint(payment.getClaimAmount());
			if (payment.getReferenceType().equals(PolicyReferenceType.LIFE_PAIDUP_CLAIM)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.LIFE_PAIDUP_CLAIM, branch.getBranchCode(), currencyCode);
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.SHORTTERMLIFE_PAIDUP_CLAIM, branch.getBranchCode(), currencyCode);
			}
			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, claimAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF for LIFEPAIDUPCLAIM_Debit", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_LIFEPAIDUPCLAIM_Credit(Payment payment, String customerId, Branch branch, String currencyCode) {
		try {
			String coaCode = null;
			String enoNo = payment.getReceiptNo();
			String tlfNo = payment.getReceiptNo();
			String narration = "Cash Payment For" + payment.getReferenceType() + " " + enoNo;
			double claimAmount = Utils.getTwoDecimalPoint(payment.getClaimAmount());

			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				coaCode = payment.getBank().getAcode();
			} else {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(COACode.CASH, branch.getBranchCode(), currencyCode);
			}

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, claimAmount, customerId, branch.getBranchCode(), coaCode, tlfNo, narration, payment, false);
			TLF tlf = tlfBuilder.getTLFInstance();
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF For LIFEPAIDUPCLAIM_Credit", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean havePendingPayment(String policyNo) {
		boolean result = false;
		try {
			result = paymentDAO.havePendingPayment(policyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find No Paid Payment By PolicyNo", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Payment updateForCashReceiptGenerated(PaymentDTO paymentDto) {
		Payment result = null;
		try {
			result = paymentDAO.findById(paymentDto.getId());
			result.setAlreadyGenerated(true);
			result = paymentDAO.update(result);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Payment ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> prePaymentAndTlf(List<Payment> paymentList, LifeSurrenderProposal surrenderProposal) {
		try {
			String customerId = surrenderProposal.getLifePolicy().getCustomerId();
			Branch branch = surrenderProposal.getBranch();
			String receiptNo = null;
			PaymentChannel channel = paymentList.get(0).getPaymentChannel();
			if (channel.equals(PaymentChannel.CASHED)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
			} else if (channel.equals(PaymentChannel.CHEQUE)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
			}

			PolicyReferenceType referenceType = null;
			String currencyCode = CurrencyUtils.getCurrencyCode(null);
			LifePolicy policy = surrenderProposal.getLifePolicy();
			for (Payment payment : paymentList) {
				payment.setReceiptNo(receiptNo);
				payment.setPrefix(getPrefix(Payment.class));
				payment = paymentDAO.insert(payment);
				referenceType = payment.getReferenceType();
				if (referenceType.equals(PolicyReferenceType.LIFE_SURRENDER_CLAIM) || referenceType.equals(PolicyReferenceType.SHORTTERMLIFE_SURRENDER_CLAIM)) {
					/** Add surrender TLF **/
					addNewTLF_For_LIFESURRENDERCLAIM_Debit(payment, customerId, branch, currencyCode, surrenderProposal.getProposalNo(), policy.getSalePoint());
					addNewTLF_For_LIFESURRENDERCLAIM_Credit(payment, customerId, branch, currencyCode, surrenderProposal.getProposalNo(), policy.getSalePoint());
				} else {
					/** Add Payment TLF **/
					preActivateBillCollection(payment, branch);
					/** Add agent commission TLF **/
					if (policy.getAgent() != null) {
						addAgentcommissionLifeProposalBillCollection(policy, payment);
					}
				}

				addNewTLF_For_ServiceChargesDebit(payment, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
				addNewTLF_For_ServiceChargesCredit(payment, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment for Surrender Proposal", e);
		}
		return paymentList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findPaymentForCashReceipt(int lastPaymentTem, String policyId) {
		Payment result = null;
		try {
			result = paymentDAO.findPaymentForCashReceipt(lastPaymentTem, policyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Payment ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> prePaymentAndTlfLifeClaimProposal(Payment payment, LifeClaimProposal lifeClaimProposal) {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			String customerId = lifeClaimProposal.getLifePolicy().getCustomerId();
			Branch branch = lifeClaimProposal.getLifePolicy().getBranch();
			String receiptNo = null;
			String accountName = lifeClaimProposal.getLifePolicy().getProductGroup().getAccountCode();
			if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.TRANSFER_RECEIPT_NO, null, true);
			} else if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
			} else if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
			}
			payment.setReceiptNo(receiptNo);
			payment.setPrefix(getPrefix(Payment.class));
			payment = paymentDAO.insert(payment);
			addNewTLF_For_LIFECLAIM_Debit(payment, customerId, branch, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_LIFECLAIM_Credit(payment, customerId, branch, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_ServiceChargesDebit(payment, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
			addNewTLF_For_ServiceChargesCredit(payment, customerId, branch, false, CurrencyUtils.getCurrencyCode(null));
			if (payment.getClaimAmount() > 0.0) {
				addNewTLF_For_LIFECLAIM_Premium_Debit(payment, payment.getClaimAmount(), customerId, branch, CurrencyUtils.getCurrencyCode(null));
				addNewTLF_For_LIFECLAIM_Premium_Credit(payment, payment.getClaimAmount(), customerId, branch, accountName, CurrencyUtils.getCurrencyCode(null));
			}
			paymentList.add(payment);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Payment", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByReceiptNoandPolicyId(String receiptNo, String policyId) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findByReceiptNoandPolicyId(receiptNo, policyId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find All Payment ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void extendPaymentTimesAccountSkip(List<Payment> paymentList, Branch branch, String currencyCode) {
		try {
			String receiptNo = null;
			for (Payment payment : paymentList) {

				if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE) || payment.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
					receiptNo = customIDGenerator.getNextId(SystemConstants.CHEQUE_RECEIPT_NO, null, true);
				} else if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
					receiptNo = customIDGenerator.getNextId(SystemConstants.CASH_RECEIPT_NO, null, true);
				} else {
					receiptNo = customIDGenerator.getNextId(SystemConstants.TRANSFER_RECEIPT_NO, null, true);
				}
				payment.setReceiptNo(receiptNo);
				payment.setPrefix(getPrefix(Payment.class));
				payment = paymentDAO.insert(payment);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to insert payment : " + e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TLFVoucherDTO> findTLFVoucher(TLFVoucherCriteria criteria) {
		List<TLFVoucherDTO> tlfVoucherList = null;
		try {
			tlfVoucherList = paymentDAO.findTLFVoucher(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find tlfVoucher", e);
		}

		return tlfVoucherList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AGENT_COMMISSION_CREDIT_FOR_INTERBRANCH(AgentCommission ac, Branch userbranch, String currencyCode, SalePoint salePoint, Branch policyBranch) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			StringBuffer narration = new StringBuffer();
			narration.append("Payable To (" + userbranch.getName() + ") Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			if (ac.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				bankId = ac.getBank().getId();
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userbranch.getPayableACName(), userbranch.getId(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
			} else if (ac.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userbranch.getPayableACName(), userbranch.getId(), currencyCode);
			} else if (ac.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				bankId = ac.getBank().getId();
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userbranch.getPayableACName(), userbranch.getId(), currencyCode);
				// coaCode =
				// paymentDAO.findCheckOfAccountNameByCode(COACode.MANDALAY_INTERBRANCH,
				// branch.getBranchCode(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
				narration.append("Narration you want to append.");
			}
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getAgent().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
					ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getAgent().getId(), policyBranch.getId(), coaCode, ac.getChequeNo(), bankId, null, narration.toString(),
					enoNo, ac.getReferenceNo(), ac.getReferenceType(), false, true);

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setAgentTransaction(false);
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_STAFF_COMMISSION_CREDIT_FOR_INTERBRANCH(StaffAgentCommission ac, Branch userbranch, String currencyCode, SalePoint salePoint, Branch policyBranch) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			StringBuffer narration = new StringBuffer();
			narration.append("Payable To (" + userbranch.getName() + ") Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			if (ac.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				bankId = ac.getBank().getId();
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userbranch.getPayableACName(), userbranch.getId(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
			} else if (ac.getPaymentChannel().equals(PaymentChannel.CASHED)) {
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userbranch.getPayableACName(), userbranch.getId(), currencyCode);
			} else if (ac.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				bankId = ac.getBank().getId();
				coaCode = paymentDAO.findCheckOfAccountNameByCode(userbranch.getPayableACName(), userbranch.getId(), currencyCode);
				// coaCode =
				// paymentDAO.findCheckOfAccountNameByCode(COACode.MANDALAY_INTERBRANCH,
				// branch.getBranchCode(), currencyCode);
				narration.append(" with " + ac.getBank().getName() + "(" + ac.getChequeNo() + ") ");
				narration.append("Narration you want to append.");
			}
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getStaff().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.CREDIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
					ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getStaff().getId(), policyBranch.getId(), coaCode, ac.getChequeNo(), bankId, null, narration.toString(),
					enoNo, ac.getReferenceNo(), ac.getReferenceType(), false, true);

			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setAgentTransaction(false);
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_AGENT_COMMISSION_DEBIT_FOR_INTERBRACH(AgentCommission ac, Branch policyBranch, String currencyCode, SalePoint salePoint, Branch userBranch) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			Product product = null;
			StringBuffer narration = new StringBuffer();
			narration.append("Receivable from (" + policyBranch.getName() + ") Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			narration.append(" Narration you want to append.");
			if (ac.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = ac.getBank().getId();
			}

			boolean isCoinsurance = isCoInsurance(ac.getReferenceNo(), ac.getReferenceType());

			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					// coaCode = isCoinsurance ? COACode.LIFE_CO_AGENT_PAYABLE :
					// COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(ac.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (isCoinsurance) {
						coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
					}
					if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else {
						coaCode = COACode.LIFE_AGENT_PAYABLE;
					}
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MEDICAL_POLICY:
				case MEDICAL_BILL_COLLECTION:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
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

			String accountName = paymentDAO.findCheckOfAccountNameByCode(policyBranch.getReceivableACName(), policyBranch.getId(), currencyCode);
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getAgent().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
					ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getAgent().getId(), userBranch.getId(), accountName, ac.getChequeNo(), bankId, null,
					narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			// pay agent
			tlf.setAgentTransaction(false);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTLF_For_STAFF_COMMISSION_DEBIT_FOR_INTERBRACH(StaffAgentCommission ac, Branch policyBranch, String currencyCode, SalePoint salePoint, Branch userBranch) {
		try {
			String coaCode = null;
			String bankId = null;
			String enoNo = ac.getInvoiceNo();
			Product product = null;
			StringBuffer narration = new StringBuffer();
			narration.append("Receivable from (" + policyBranch.getName() + ") Commission Payment on " + ac.getReferenceType().getLabel() + " for " + ac.getInvoiceNo());
			narration.append(" Narration you want to append.");
			if (ac.getPaymentChannel() == PaymentChannel.CHEQUE) {
				bankId = ac.getBank().getId();
			}

			boolean isCoinsurance = isCoInsurance(ac.getReferenceNo(), ac.getReferenceType());

			switch (ac.getReferenceType()) {

				case LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
					// coaCode = isCoinsurance ? COACode.LIFE_CO_AGENT_PAYABLE :
					// COACode.LIFE_AGENT_PAYABLE;
					LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(ac.getReferenceNo());
					product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
					if (isCoinsurance) {
						coaCode = COACode.LIFE_CO_AGENT_PAYABLE;
					}
					if (ProductIDConfig.isGroupLife(product)) {
						coaCode = COACode.GROUP_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isPublicLife(product)) {
						coaCode = COACode.ENDOWMENT_LIFE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSnakeBite(product)) {
						coaCode = COACode.SNAKE_BITE_AGENT_PAYABLE;
					} else if (ProductIDConfig.isSportMan(product)) {
						coaCode = COACode.SPORTMAN_AGENT_PAYABLE;
					} else {
						coaCode = COACode.LIFE_AGENT_PAYABLE;
					}
					break;
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					coaCode = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
					break;
				case MEDICAL_POLICY:
				case MEDICAL_BILL_COLLECTION:
				case HEALTH_POLICY:
				case HEALTH_POLICY_BILL_COLLECTION:
					coaCode = COACode.HEALTH_AGENT_PAYABLE;
					break;
				case MICRO_HEALTH_POLICY:
				case GROUP_MICRO_HEALTH:
					coaCode = COACode.MICRO_HEALTH_AGENT_PAYABLE;
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

			String accountName = paymentDAO.findCheckOfAccountNameByCode(policyBranch.getReceivableACName(), policyBranch.getId(), currencyCode);
			narration.append(" amount of " + (ac.getCommission() - ac.getWithHoldingTax()) + " as per attach, to " + ac.getStaff().getName().getFullName());

			TLFBuilder tlfBuilder = new TLFBuilder(DoubleEntry.DEBIT, ac.getPaymentChannel(), ac.getCommission() - ac.getWithHoldingTax(), ac.getRate(), currencyCode,
					ac.getHomeCommission() - ac.getHomeWithHoldingTax(), ac.getStaff().getId(), userBranch.getId(), accountName, ac.getChequeNo(), bankId, null,
					narration.toString(), enoNo, ac.getReferenceNo(), ac.getReferenceType(), false);
			TLF tlf = tlfBuilder.getTLFInstance();
			tlf.setPaid(true);
			// pay agent
			tlf.setAgentTransaction(false);
			tlf.setPaymentChannel(ac.getPaymentChannel());
			tlf.setSalePoint(salePoint);
			setIDPrefixForInsert(tlf);
			paymentDAO.insertTLF(tlf);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TLF", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String findCCOAByCode(String acCode, String branchId, String currencyId) {
		String result = null;
		try {
			result = paymentDAO.findCCOAByCode(acCode, branchId, currencyId);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Failed to find ccoaid BY acCode,currencyId,branchId", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String findCOAAccountNameByCCOAID(String id) {
		String result = null;
		try {
			result = paymentDAO.findCOAAccountNameByCCOAID(id);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Failed to find ccoaid BY acCode,currencyId,branchId", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String findCOAAccountCodeByCCOAID(String id) {
		String result = null;
		try {
			result = paymentDAO.findCOAAccountCodeByCCOAID(id);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Failed to find ACODE BY CCOAID", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Payment> findByBankWalletId(String id) {
		List<Payment> result = null;
		try {
			result = paymentDAO.findByBankWalletId(id);
		} catch (DAOException pe) {
			throw new SystemException(pe.getErrorCode(), "Failed to find ", pe);
		}
		return result;
	}

}
