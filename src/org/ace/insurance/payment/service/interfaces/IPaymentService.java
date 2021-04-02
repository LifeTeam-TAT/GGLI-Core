package org.ace.insurance.payment.service.interfaces;

import java.util.List;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PaymentReferenceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.insurance.life.claim.LifeClaimBeneficiary;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.paidUp.LifePaidUpProposal;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.AgentPaymentCashReceiptDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.PaymentOrderCashReceiptDTO;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.report.ClaimVoucher.ClaimVoucherDTO;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.insurance.report.TLF.TLFVoucherDTO;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.web.common.PaymentTableDTO;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.agent.payment.StaffCommissionDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;

public interface IPaymentService {

	public double findActivedRate();

	public boolean havePendingPayment(String policyNo);

	public List<Payment> prePaymentAndTlfLifeClaim(List<Payment> paymentList, LifeClaimBeneficiary beneficiary, LifeClaim lifeClaim, double premium);

	public List<Payment> prePaymentForChalen(List<Payment> paymentList);

	public void prePaymentAndTLFMedical(Payment payment, MedicalClaimProposalDTO medicalClaimProposalDTO);

	public List<Payment> prePayment(List<Payment> paymentList);

	public List<Payment> preClaimPayment(List<Payment> paymentList);

	public void activatePayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, List<AgentCommission> agentCommissionList);

	public void activatePaymentAndTLF(List<Payment> paymentList, List<AgentCommission> agentCommissionList, Branch branch, String currencyCode, SalePoint salePoint);

	public void activateSkipPaymentAndTLF(List<Payment> paymentList, List<AgentCommission> agentCommissionList, Branch branch, String currencyCode, SalePoint salePoint);

	public void addStaffAgentCommission(List<StaffAgentCommission> staffagentCommissionList);

	/**
	 * If complete is true, return payments which is already paid. If complete
	 * is false, return payments which is not paid. If complete is null, return
	 * payment which is already paid or not.
	 */
	public List<PaymentDTO> findPaymentByRecipNo(List<String> receiptList, PolicyReferenceType referenceType, Boolean complete);

	public List<Payment> findByPolicy(String policyId);

	public AgentCommission findAgentCommissionByReferenceNo(String referenceNo);

	/**
	 * If complete is true, return payments which is already paid. If complete
	 * is false, return payments which is not paid. If complete is null, return
	 * payment which is already paid or not.
	 */
	public List<Payment> findByProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete);

	public List<Payment> findByClaimProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete);

	public void activateClaimPayment(List<AccountPayment> accountPaymentList, Branch branch);

	// newly Added
	public List<AgentCommission> findAgentCommissionDetail(AgentCommissionDetailCriteria criteria);

	public List<AgentCommissionDTO> findAgentCommissionForPayment();

	public List<AgentCommission> findAgentCommissionPaymentByAgent(String invoiceNo, String agentId);

	public void updateAgentCommission(List<AgentCommission> agentCommissionList, AgentCommission agentCommission, boolean isCheque, List<WorkFlowDTO> workFlowDTOList);

	public void updateStaffAgentCommission(List<StaffAgentCommission> agentCommissionList, StaffAgentCommission agentCommission, boolean isCheque,
			List<WorkFlowDTO> workFlowDTOList);

	public void updateCoinsuranceForInvoice(List<Coinsurance> coinsuranceList, List<WorkFlowDTO> workFlowDTOList);

	public List<Coinsurance> findCoInsuranceForDashboardByType(CoinsuranceType type, WorkflowTask workFlowTask);

	public void paymentInCoinsurance(Coinsurance coInsu, Branch branch, String currencyCode);

	public List<Payment> prePaymentForClaim(List<Payment> paymentList);

	public Payment findClaimProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete);

	public List<Payment> findByClaimProposalComplete(String proposalId, PolicyReferenceType referenceType);

	// public void preActivateBillCollection(Payment payment);

	public String findCheckOfAccountCode(String acccountName, Branch branch, String currencyCode);

	public String findCOAAccountNameByCode(String acccountCode);

	public List<Payment> findPaymentByReferenceNoAndMaxDate(String referenceNo);

	public List<Payment> findPaymentByReceiptNo(String receiptNo);

	public AgentPaymentCashReceiptDTO getAgentPaymentCashReceipt(Object proposal, InsuranceType insuranceType, Branch branch, double discountPercent);

	public PaymentOrderCashReceiptDTO getPaymentOrderCashReceipt(Object proposal, InsuranceType insuranceType, Branch branch, PaymentDTO payment);

	public void activateTLFClearing(List<Payment> paymentList);

	public void addAgentCommission(List<AgentCommission> agentCommissionList);

	/**** TLF ***/

	public TLF updateTLF(TLF tlf);

	public List<TLF> findTLFbyTLFNo(String tlfNo, Boolean isClearing);

	public List<TLF> findTLFbyENONo(String enoNo);

	public void addNewTLF_For_CashDebitForPremium(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	public void addNewTLF_For_CoInsuCashPaymentCredit(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode);

	public void addNewTLF_For_AgentCommissionDebit(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	public void addNewTLF_For_CoInsuCommReceived_Debit(Coinsurance c, Branch branch, double commission, String currencyCode);

	public void addNewTLF_For_CoInsuCommReceived_Credit(Coinsurance c, Branch branch, double commission, String currencyCode);

	public void addNewTLF_For_CoinsuCompanyPayment_Debit(Coinsurance c, Branch branch, double premium, String currencyCode);

	public void addNewTLF_For_CoinsuCompanyPayment_Credit(Coinsurance c, Branch branch, double premium, String currencyCode);

	public void addNewTLF_For_InCoinsuPremiumIncome_Debit(Coinsurance c, Branch branch, String currencyCode);

	public void addNewTLF_For_InCoinsuPremiumIncome_Credit(Coinsurance c, Branch branch, String currencyCode);

	public void deletePayments(List<Payment> paymentList);

	public List<TLF> findTLFbyReferenceNoAndReferenceType(String referenceNo, PolicyReferenceType policyReferenceType);

	public Payment findPaymentByReferenceNo(String referenceNo);

	public List<TLFVoucherDTO> findTLFVoucher(TLFVoucherCriteria criteria);

	public List<ClaimVoucherDTO> findClaimVoucher(String receiptNo, String damage);

	public List<Payment> findPaymentByReferenceNoAndMaxDateForAgentInvoice(String referenceNo);

	public List<Payment> findBCPaymentByReceiptNo(PolicyCriteria policyCriteria);

	public Payment findPaymentByReferenceNoAndIsComplete(String referenceNo, Boolean complete);

	public Payment findByChalanNo(String chalanNo);

	public List<PaymentTableDTO> findByChalanNoForClaim(List<String> receiptList, PaymentReferenceType referenceType, Boolean complete);

	public void activateMedicalClaimPayment(Payment payment);

	public void addNewTLF_For_UnderwritingExpenseCr(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal, String currenyCode);

	// public void addNewTLF_For_UnderwritingExpenseDr(Payment payment, String
	// customerId, Branch branch, String accountName, String tlfNo, boolean
	// isRenewal, String currenyCode);

	/* for Life Surrender */

	public List<PaymentTrackDTO> findPaymentTrack(String policyNo);

	/* Life PaidUp */
	public List<Payment> prePaymentAndTlfLifePaidUp(List<Payment> paymentList, LifePaidUpProposal paidUpProposal);

	public Payment updateForCashReceiptGenerated(PaymentDTO paymentDto);

	List<Payment> prePaymentAndTlf(List<Payment> paymentList, LifeSurrenderProposal surrenderProposal);

	public Payment findPaymentForCashReceipt(int lastPaymentTerm, String policyId);

	List<Payment> findBCPaymentByPolicyNo(String policyNo, ReferenceType referenceType);

	Payment findPaymentByReferenceNoAndTermOne(String referenceNo);

	void addNewTLF_For_PremiumCredit(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal, String currenyCode,
			SalePoint salePoint);

	void addNewTLF_For_AgentCommissionCredit(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	void addNewTLF_For_AgentCommissionDr(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	void addNewTLF_For_CashDebitForSCSTFees(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode, SalePoint salePoint);

	void addNewTLF_For_SCSTFees(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode, SalePoint salePoint);

	void addNewTLF_For_PremiumDebit(Payment payment, String customerId, Branch branch, String accountName, boolean isEndorse, String tlfNo, boolean isClearing, boolean isRenewal,
			String currencyCode, SalePoint salePoint);

	void addNewTLF_For_CashCreditForPremium(List<AccountPayment> accountPaymentList, String customerId, Branch branch, boolean isEndorse, String tlfNo, boolean isClearing,
			boolean isRenewal, String currencyCode, SalePoint salePoint);

	void addNewTLF_For_AgentCommissionCr(AgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	void activateSnakeBitePayment(Payment payment, String customerId, Branch branch, List<AgentCommission> agentCommissionList, String currencyCode, SalePoint salePoint);

	void activatePaymentTransfer(List<AccountPayment> accountPaymentList, String customerId, Branch branch, boolean isRenewal, String currencyCode, SalePoint salePoint);

	void activateCargoPaymentTransfer(List<AccountPayment> accountPaymentList, String customerId, Branch branch, boolean isRenewal, String currencyCode, SalePoint salePoint);

	void prePaymentTransfer(LifeProposal lifeProposal, List<Payment> paymentList, Branch branch, String currencyCode, SalePoint salePoint);

	void prePaymentTransfer(MedicalProposal medicalProposal, List<Payment> paymentList, Branch branch, String currencyCode, SalePoint salePoint);

	void extendPaymentTimes(List<Payment> paymentList, Branch branch, String currencyCode);

	void extendPaymentTimesAccountSkip(List<Payment> paymentList, Branch branch, String currencyCode);

	void preActivatePayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, List<AgentCommission> agentCommissionList, boolean isRenewal,
			String currencyCode, SalePoint salePoint);

	void preActivateEndorsementPayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String currencyCode, SalePoint salePoint);

	void activateEndorsementPayment(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String currencyCode, SalePoint salePoint);

	void addAgentCommissionTLF(List<AgentCommission> agentCommissionList, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode, SalePoint salePoint);

	void paymentAgentCommission(List<AgentCommission> commissionList, AgentCommissionDTO commissionDTO, Branch branch, String currencyCode);

	void addNewTLF_For_AGENT_COMMISSION_CREDIT(AgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint);

	public List<Payment> findByReceiptNoandPolicyId(String receiptNo, String policyId);

	public List<Payment> prePaymentAndTlfLifeClaimProposal(Payment payment, LifeClaimProposal lifeClaimProposal);

	public void preActivatePaymentForInterBranch(List<AccountPayment> accountPaymentList, String customerId, Branch branch, List<AgentCommission> agentCommissionList,
			boolean isRenewal, String currencyCode, SalePoint salePoint, Branch proposalBranch);

	public void addNewTLF_For_CashDebitForPremiumForInterBranch(List<AccountPayment> accountPaymentList, String customerId, Branch branch, String tlfNo, boolean isRenewal,
			String currencyCode, SalePoint salePoint, Branch userBranch);

	public void addNewTLF_For_PremiumCreditPayableForInterBranch(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal,
			String currenyCode, SalePoint salePoint, Branch policyBranch);

	// inter Branch
	public void preActivateBillCollection(Payment payment, Branch userBranch);

	void addNewTLF_For_PremiumCreditInterBranch(Payment payment, String customerId, Branch branch, String accountName, String tlfNo, boolean isRenewal, String currenyCode,
			SalePoint salePoint);

	void addNewTLF_For_AGENT_COMMISSION_DEBIT(AgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint, boolean isInterBranch);

	void addNewTLF_For_BankChargesDebit(Payment payment, String customerId, Branch branch, String tlfNo, boolean isRenewal, String currencyCode, SalePoint salePoint);

	String findCCOAByCode(String acCode, String branchId, String currencyId);

	String findCOAAccountNameByCCOAID(String id);

	String findCOAAccountCodeByCCOAID(String id);

	void addStaffCommissionTLF(List<StaffAgentCommission> staffCommissionList, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	void addNewTLF_For_StaffCommissionDr(StaffAgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	void addNewTLF_For_StaffCommissionCredit(StaffAgentCommission ac, boolean coInsureance, Branch branch, Payment payment, String eno, boolean isRenewal, String currencyCode,
			SalePoint salePoint);

	List<StaffAgentCommission> findStaffAgentCommissionDetail(StaffSanctionCriteria criteria);

	List<StaffAgentCommission> findStaffCommissionPaymentByAgent(String invoiceNo, String agentId);

	void addNewTLF_For_STAFF_COMMISSION_CREDIT(StaffAgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint);

	void addNewTLF_For_STAFF_COMMISSION_DEBIT(StaffAgentCommission ac, Branch branch, String currencyCode, SalePoint salePoint, boolean isInterBranch);

	void addNewTLF_For_STAFF_COMMISSION_CREDIT_FOR_INTERBRANCH(StaffAgentCommission ac, Branch userbranch, String currencyCode, SalePoint salePoint, Branch policyBranch);

	void addNewTLF_For_STAFF_COMMISSION_DEBIT_FOR_INTERBRACH(StaffAgentCommission ac, Branch policyBranch, String currencyCode, SalePoint salePoint, Branch userBranch);

	void addNewTLF_For_STAFF_COMMISSION_TAX_CREDIT(StaffAgentCommission ac, Branch branch, String currencyCode);

	void addNewTLF_For_STAFF_COMMISSION_TAX_DEBIT(StaffAgentCommission ac, Branch branch, String currencyCode);

	void paymentStaffCommission(List<StaffAgentCommission> commissionList, StaffCommissionDTO commissionDTO, Branch branch, String currencyCode);

	List<Payment> findByBankWalletId(String id);
}