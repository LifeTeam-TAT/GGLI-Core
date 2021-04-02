package org.ace.insurance.payment.persistence.interfacs;

import java.util.List;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PaymentReferenceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.CashDeno;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.report.ClaimVoucher.ClaimVoucherDTO;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.insurance.report.TLF.TLFVoucherDTO;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.web.common.PaymentTableDTO;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IPaymentDAO {
	public Payment insert(Payment payment) throws DAOException;

	public void insertAgentCommission(AgentCommission agentCommission) throws DAOException;

	public void insertStaffAgentCommission(StaffAgentCommission staffagentCommission) throws DAOException;

	public Payment update(Payment payment) throws DAOException;

	public List<PaymentDTO> findByReceiptNo(List<String> receiptList, PolicyReferenceType referenceType, Boolean complete) throws DAOException;

	public List<Payment> findByReceiptNo(String receiptNo, PolicyReferenceType referenceType, Boolean complete) throws DAOException;

	public List<Payment> findByPolicy(String policyId) throws DAOException;

	public AgentCommission findAgentCommissionByReferenceNo(String referenceNo) throws DAOException;

	public TLF insertTLF(TLF tlf) throws DAOException;

	public void deleteTLFs(List<TLF> tlfList) throws DAOException;

	public String findCheckOfAccountNameByCode(String accountName, String branchCode, String currencyCode) throws DAOException;

	public CashDeno insertCashDeno(CashDeno cashDeno) throws DAOException;

	public List<Payment> findByProposal(String proposalId, PolicyReferenceType PolicyReferenceType, Boolean complete) throws DAOException;

	public List<Payment> findByClaimProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete) throws DAOException;

	// newly added
	public List<AgentCommission> find(AgentCommissionDetailCriteria criteria) throws DAOException;

	public List<AgentCommissionDTO> findAgentCommissionForPayment() throws DAOException;

	public List<AgentCommission> findAgentCommissionPaymentByAgent(String invoiceNo, String agentId) throws DAOException;

	public void paymentAgentCommission(AgentCommission commission) throws DAOException;

	// new
	public void updateAgentCommissionInfo(AgentCommission agentComission) throws DAOException;

	public void updateStaffAgentCommissionInfo(StaffAgentCommission agentComission) throws DAOException;

	public void updateCoinsuranceForInvoice(Coinsurance coinsurance) throws DAOException;

	public List<Coinsurance> findCoInsuranceForDashboardByType(CoinsuranceType type, WorkflowTask workFlowTask) throws DAOException;

	public void updateCoinsurance(Coinsurance coinsurance) throws DAOException;

	public Payment findClaimProposal(String claimId, PolicyReferenceType referenceType, Boolean complete) throws DAOException;

	public List<Payment> findByClaimProposalComplete(String proposalId, PolicyReferenceType referenceType) throws DAOException;

	public String findCOAAccountNameByCode(String acccountCode) throws DAOException;

	public void updateBillCollection(String receiptNo, boolean complete) throws DAOException;

	public List<Payment> findPaymentByReferenceNoAndMaxDate(String referenceNo) throws DAOException;

	public void paymentCoinsuranceCommission(Coinsurance coinsurance) throws DAOException;

	// new
	public List<Payment> findPaymentByReceiptNo(String receiptNo) throws DAOException;

	public Payment findPaymentByReferenceNo(String referenceNo) throws DAOException;

	public String findCOASetupAccountNameByCode(String acccountCode, String branchCode) throws DAOException;

	public List<TLF> findTLFbyTLFNo(String tlfNo, Boolean isClearing) throws DAOException;

	public List<TLF> findTLFbyENONo(String enoNo) throws DAOException;

	public void insertTLFList(List<TLF> tlfList) throws DAOException;

	public TLF updateTLF(TLF tlf) throws DAOException;

	public void deletePayments(List<Payment> paymentList) throws DAOException;

	public List<TLF> findTLFbyReferenceNoAndReferenceType(String referenceNo, PolicyReferenceType policyReferenceType) throws DAOException;

	public List<TLFVoucherDTO> findTLFVoucher(TLFVoucherCriteria criteria) throws DAOException;

	public List<Payment> findPaymentByReferenceNoAndMaxDateForAgentInvoice(String referenceNo) throws DAOException;

	public Payment findPaymentByReferenceNoAndIsComplete(String referenceNo, Boolean complete, Boolean reverse) throws DAOException;

	public List<Payment> findBCPaymentByReceiptNo(PolicyCriteria policyCriteria) throws DAOException;

	public boolean isLatestBCReceiptNo(String receiptNo) throws DAOException;

	public List<ClaimVoucherDTO> findClaimVoucher(String receiptNo, String damage) throws DAOException;

	public double findActiveRate() throws DAOException;

	public Payment findByChalanNo(String chalanNo) throws DAOException;

	public List<PaymentTableDTO> findByChalanNoForClaim(List<String> receiptList, PaymentReferenceType referenceType, Boolean complete) throws DAOException;

	/* for Life Surrender */
	public List<PaymentTrackDTO> findPaymentTrack(String policyNo) throws DAOException;

	public Double findSummaryByReferenceNo(String policyId, PolicyReferenceType referenceType, Boolean complete) throws DAOException;

	public List<PaymentDTO> findNoPaidPaymentByReferenceNo(String policyId);

	public boolean havePendingPayment(String policyNo);

	public Payment findById(String paymentId);

	public Payment findPaymentForCashReceipt(int lastPaymentTerm, String policyId);

	List<Payment> findAllBCPaymentByPolicyNo(String policyNo, ReferenceType referenceType) throws DAOException;

	Payment findPaymentByReferenceNoAndTermOne(String referenceNo) throws DAOException;

	public List<Payment> findByReceiptNoandPolicyId(String receiptNo, String policyId);

	void reversetPaymentByReceiptNo(String receiptNo) throws DAOException;

	List<Payment> findPaymentByReceiptNoAndComplete(TLFVoucherCriteria criteria) throws DAOException;

	String findCOAAccountNameByCCOAID(String CCOAId) throws DAOException;

	String findCCOAByCode(String acCode, String branchId, String currencyId);

	String findCOAAccountCodeByCCOAID(String CCOAId) throws DAOException;

	List<StaffAgentCommission> findStaffAgentCommission(StaffSanctionCriteria criteria) throws DAOException;

	List<StaffAgentCommission> findStaffCommissionPaymentByAgent(String invoiceNo, String agentId) throws DAOException;

	void paymentStaffCommission(StaffAgentCommission commission) throws DAOException;

	List<Payment> findByBankWalletId(String id);

}
