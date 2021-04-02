package org.ace.insurance.payment.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PaymentReferenceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.medical.claim.MedicalBeneficiaryRole;
import org.ace.insurance.medical.claim.MedicalClaim;
import org.ace.insurance.medical.claim.MedicalClaimProposal;
import org.ace.insurance.medical.claim.MedicalClaimRole;
import org.ace.insurance.medical.claim.service.interfaces.IMedicalClaimProposalService;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.CashDeno;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.TLF;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.report.ClaimVoucher.ClaimVoucherDTO;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.insurance.report.TLF.TLFVoucherDTO;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.PaymentTableDTO;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimBeneficiaryDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimProposalDTO;
import org.ace.insurance.web.manage.medical.claim.factory.MedicalClaimProposalFactory;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("PaymentDAO")
public class PaymentDAO extends BasicDAO implements IPaymentDAO {
	@Resource(name = "IDConfigLoader")
	private IDConfigLoader idConfigLoader;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "MedicalClaimProposalService")
	private IMedicalClaimProposalService medicalClaimProposalService;

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment insert(Payment payment) throws DAOException {
		try {
			em.persist(payment);
			insertProcessLog(TableName.PAYMENT, payment.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Payment", pe);
		}
		return payment;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findActiveRate() throws DAOException {
		StringBuffer query = new StringBuffer();
		double rate = 0.0;
		query.append("Select rate from RateInfo where rateType = 'CS' and lastModify = 1 And Cur = 'USD'");
		Query q = em.createNativeQuery(query.toString());
		Number result = (Number) q.getSingleResult();
		rate = result.doubleValue();
		return rate;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertAgentCommission(AgentCommission agentCommission) throws DAOException {
		try {
			em.persist(agentCommission);
			insertProcessLog(TableName.AGENTCOMMISSION, agentCommission.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Payment", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertStaffAgentCommission(StaffAgentCommission staffagentCommission) throws DAOException {
		try {
			em.persist(staffagentCommission);
			insertProcessLog(TableName.STAFF_AGENTCOMMISSION, staffagentCommission.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Payment", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment update(Payment payment) throws DAOException {
		try {
			payment = em.merge(payment);
			updateProcessLog(TableName.PAYMENT, payment.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Payment", pe);
		}
		return payment;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TLF updateTLF(TLF tlf) throws DAOException {
		try {
			tlf = em.merge(tlf);
			updateProcessLog(TableName.TLF, tlf.getId());
			insertProcessLog(TableName.TLF, tlf.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Payment", pe);
		}
		return tlf;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDTO> findByReceiptNo(List<String> receiptList, PolicyReferenceType referenceType, Boolean complete) throws DAOException {
		List<PaymentDTO> result = new ArrayList<PaymentDTO>();
		try {
			StringBuffer buffer = new StringBuffer("SELECT m FROM Payment m WHERE m.receiptNo = :receiptNo AND m.referenceType = :referenceType");
			if (complete != null) {
				buffer.append(" AND m.complete = " + complete + " AND  m.reverse = false ");
			}
			Query q = em.createQuery(buffer.toString());
			for (String receiptNo : receiptList) {
				q.setParameter("receiptNo", receiptNo);
				q.setParameter("referenceType", referenceType);
				List<Payment> paymentList = q.getResultList();
				if (paymentList != null && !paymentList.isEmpty()) {
					PaymentDTO paymentDTO = new PaymentDTO(paymentList);
					result.add(paymentDTO);
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : ", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByReceiptNo(String receiptNo, PolicyReferenceType referenceType, Boolean complete) throws DAOException {
		List<Payment> paymentList = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT m FROM Payment m WHERE m.receiptNo = :receiptNo AND m.referenceType = :referenceType");
			if (complete != null) {
				buffer.append(" AND m.complete = " + complete);
			}
			Query q = em.createQuery(buffer.toString());
			q.setParameter("receiptNo", receiptNo);
			q.setParameter("referenceType", referenceType);
			paymentList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : ", pe);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findPaymentByReferenceNo(String referenceNo) throws DAOException {
		Payment result = null;
		try {
			Query q = em.createNamedQuery("Payment.findByReferenceNo");
			q.setParameter("referenceNo", referenceNo);
			result = (Payment) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo : " + referenceNo, pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findPaymentByReferenceNoAndTermOne(String referenceNo) throws DAOException {
		Payment result = null;
		try {
			Query q = em.createNamedQuery("Payment.findPaymentByReferenceNoAndTerm1");
			q.setParameter("referenceNo", referenceNo);
			result = (Payment) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo : " + referenceNo, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findPaymentByReferenceNoAndIsComplete(String referenceNo, Boolean complete, Boolean reverse) throws DAOException {
		Payment result = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT m FROM Payment m WHERE m.referenceNo = :referenceNo AND m.complete = :complete AND m.reverse = :reverse");
			Query q = em.createQuery(buffer.toString());
			q.setParameter("referenceNo", referenceNo);
			q.setParameter("complete", complete);
			q.setParameter("reverse", reverse);
			result = (Payment) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo And Is Compelte: " + referenceNo + complete, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AgentCommission findAgentCommissionByReferenceNo(String referenceNo) throws DAOException {
		AgentCommission result = null;
		try {
			Query q = em.createNamedQuery("AgentCommission.findByReferenceNo");
			q.setParameter("referenceNo", referenceNo);
			result = (AgentCommission) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo : " + referenceNo, pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByProposal(String proposalId, PolicyReferenceType referenceType, Boolean complete) throws DAOException {
		List<Payment> result = null;
		try {
			String policy = null;
			String policyHistory = null;
			String concatQuery = null;
			StringBuffer buffer = new StringBuffer();
			// referenceNo of payment to policy is missing (because of
			// endorsement and renewal process), then find in policy history
			switch (referenceType) {
				case GROUP_FARMER_PROPOSAL: {
					policy = "GroupFarmerProposal x";
					concatQuery = "x.id = :proposalId";

				}
					break;
				case LIFE_POLICY:
				case PA_POLICY:
				case FARMER_POLICY:
				case PUBLIC_TERM_LIFE_POLICY:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SHORT_ENDOWMENT_LIFE_POLICY:
				case SIMPLE_LIFE_POLICY:	
				case STUDENT_LIFE_POLICY: {
					policy = "LifePolicy x";
					policyHistory = "LifePolicyHistory x";
					concatQuery = "x.lifeProposal.id = :proposalId";
				}
					break;

				case MEDICAL_POLICY: {
					policy = "MedicalPolicy x";
					concatQuery = "x.medicalProposal.id = :proposalId";
				}
					break;
				case TRAVEL_PROPOSAL: {
					policy = "TravelProposal x";
					concatQuery = "x.id = :proposalId";
				}
					break;
				case PERSON_TRAVEL_POLICY: {
					policy = "PersonTravelProposal x";
					concatQuery = "x.id = :proposalId";
				}
					break;
				case CRITICAL_ILLNESS_POLICY:
				case MICRO_HEALTH_POLICY:
				case HEALTH_POLICY: {
					policy = "MedicalPolicy x";
					concatQuery = "x.medicalProposal.id = :proposalId";
				}
					break;
				case GROUP_MICRO_HEALTH: {
					policy = "GroupMicroHealth x";
					concatQuery = "x.id = :proposalId";
				}
					break;
				default:
					break;
			}

			buffer.append("SELECT p FROM Payment p WHERE p.referenceType = :referenceType ");
			if (complete != null) {
				buffer.append(" AND p.complete = :complete ");
			}

			buffer.append(" AND p.referenceNo IN (SELECT x.id FROM " + policy + " WHERE " + concatQuery + ")");
			if (PolicyReferenceType.LIFE_POLICY.equals(referenceType)) {
				buffer.append(" UNION ");
				buffer.append("SELECT p FROM Payment p WHERE p.referenceType = :referenceType ");
				if (complete != null) {
					buffer.append(" AND p.complete = :complete ");
				}
				buffer.append(" AND p.referenceNo IN (SELECT x.policyReferenceNo FROM " + policyHistory + " WHERE " + concatQuery
						+ " AND x.entryType != org.ace.insurance.common.PolicyHistoryEntryType.UNDERWRITING)");
			}

			Query query = em.createQuery(buffer.toString());
			query.setParameter("proposalId", proposalId);
			query.setParameter("referenceType", referenceType);
			if (complete != null) {
				query.setParameter("complete", complete);
			}

			result = query.getResultList();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find PaymentList by Proposal ID : " + proposalId, pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByClaimProposal(String claimId, PolicyReferenceType referenceType, Boolean complete) throws DAOException {
		List<Payment> result = null;
		try {
			Query q = em.createNamedQuery("Payment.findByReferenceNoAndReferenceType");
			q.setParameter("referenceNo", claimId);
			q.setParameter("referenceType", referenceType);
			result = q.getResultList();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo and ReferenceType : " + claimId, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findClaimProposal(String claimId, PolicyReferenceType referenceType, Boolean complete) throws DAOException {
		Payment result = null;
		try {
			Query q = em.createNamedQuery("Payment.findByReferenceNoAndReferenceType");
			q.setParameter("referenceNo", claimId);
			q.setParameter("referenceType", referenceType);
			result = (Payment) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo and ReferenceType : " + claimId, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TLF insertTLF(TLF tlf) throws DAOException {
		try {
			em.persist(tlf);
			insertProcessLog(TableName.TLF, tlf.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert TLF", pe);
		}
		return tlf;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertTLFList(List<TLF> tlfList) throws DAOException {
		try {
			for (TLF tlf : tlfList) {
				em.persist(tlf);
				insertProcessLog(TableName.TLF, tlf.getId());
				em.flush();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert TLF", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByPolicy(String policyId) throws DAOException {
		List<Payment> result = null;
		try {
			Query q = em.createNamedQuery("Payment.findByReferenceNo");
			q.setParameter("referenceNo", policyId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo : " + policyId, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public CashDeno insertCashDeno(CashDeno cashDeno) throws DAOException {
		try {
			em.persist(cashDeno);
			// insertProcessLog(TableName.CASHDENO, cashDeno.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert CashDeno", pe);
		}
		return cashDeno;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String findCheckOfAccountNameByCode(String acccountName, String branchCode, String currencyCode) throws DAOException {
		String result = null;
		try {
			Query query = em.createQuery("SELECT a.accountCode FROM CoaSetup a WHERE a.acccountName = :acccountName AND a.currency = :currency AND a.branchCode = :branchCode");
			query.setParameter("acccountName", acccountName);
			query.setParameter("currency", currencyCode);
			query.setParameter("branchCode", branchCode);
			result = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find check of accountName", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findCOAAccountNameByCode(String acccountCode) throws DAOException {
		String result = null;
		try {
			Query query = em.createQuery("SELECT a.acName FROM Coa a WHERE a.acode = :acccountCode");
			query.setParameter("acccountCode", acccountCode);
			result = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find COA account name", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String findCOAAccountNameByCCOAID(String CCOAId) throws DAOException {
		String result = null;
		try {
			Query query = em.createQuery("SELECT a.acName FROM Coa a INNER JOIN CurrencyChartOfAccount ccoa WHERE a.id=ccoa.coaid and ccoa.id = :ccoaid");
			query.setParameter("ccoaid", CCOAId);
			result = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find COA account name", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String findCOAAccountCodeByCCOAID(String CCOAId) throws DAOException {
		String result = null;
		try {
			Query query = em.createQuery("SELECT a.acode FROM Coa a INNER JOIN CurrencyChartOfAccount ccoa WHERE a.id=ccoa.coaid and ccoa.id = :ccoaid");
			query.setParameter("ccoaid", CCOAId);
			result = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find COA account name", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findCOASetupAccountNameByCode(String acccountCode, String branchCode) throws DAOException {
		String result = null;
		try {
			Query query = em.createQuery("SELECT a.acccountName FROM CoaSetup a WHERE a.accountCode = :acccountCode AND a.currency = :currency AND a.branchCode = :branchCode");
			query.setParameter("acccountCode", acccountCode);
			query.setParameter("currency", KeyFactorChecker.getKyatId());
			query.setParameter("branchCode", branchCode);
			result = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find COA Setup account name", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String findCCOAByCode(String acCode, String branchId, String currencyId) {
		String result = null;
		try {
			StringBuffer str = new StringBuffer();
			str.append("SELECT ccoa.id FROM CCOA ccoa JOIN Coa coa ON coa.id = ccoa.coaId ");
			str.append("WHERE coa.acCode = ?1 AND ccoa.currencyId = ?2 AND ccoa.branchId = ?3");
			Query query = em.createNativeQuery(str.toString());
			query.setParameter("1", acCode);
			query.setParameter("2", currencyId);
			query.setParameter("3", branchId);
			result = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find ccoaid BY acCode,currencyId,branchId", pe);
		}
		return result;
	}

	// newly added
	@SuppressWarnings("unchecked")
	public List<AgentCommission> find(AgentCommissionDetailCriteria criteria) throws DAOException {
		List<AgentCommission> result = new ArrayList<AgentCommission>();
		try {
			String branchId = idConfigLoader.getBranchId();
			String policyObject = null;
			String policyHistoryObject = null;
			switch (criteria.getInsuranceType()) {
				case SINGLE_PREMIUM_CREDIT_LIFE:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
				case STUDENT_LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					break;
				case LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					policyHistoryObject = "(SELECT p.policyReferenceNo FROM LifePolicyHistory p )";
					break;
				case PERSONAL_ACCIDENT:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					break;
				case FARMER:
					policyObject = "(SELECT p.id FROM LifePolicy p  )";
					break;
				case PUBLIC_TERM_LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					break;
				case SHORT_ENDOWMENT_LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p  )";
					break;
				case FIDELITY:
					policyObject = "(SELECT p.id FROM Fidelity p ) ";
					break;
				case TRAVEL_INSURANCE:
					policyObject = "(SELECT p.id FROM TravelProposal p  ) ";
					break;
				case MEDICAL:
				case HEALTH:
				case MICRO_HEALTH:
				case CRITICAL_ILLNESS:
					policyObject = "(SELECT p.id FROM MedicalPolicy p) ";
					break;
				case GROUP_MICRO_HEALTH:
					policyObject = "(SELECT p.id FROM GroupMicroHealth p ) ";
					break;

				case PERSON_TRAVEL:
					policyObject = "(SELECT p.id FROM PersonTravelPolicy p  )";
					break;

				case GROUP_FARMER:
					policyObject = "(SELECT p.id FROM GroupFarmerProposal p  )";
					break;
				default:
					break;
			}

			StringBuffer query = new StringBuffer();
			query.append(" SELECT DISTINCT ac FROM Agent a, AgentCommission ac WHERE a.id = ac.agent.id ");
			query.append(" AND ac.isPaid = 0 AND ac.status = 1 AND ac.invoiceDate IS NULL");
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}

			if (criteria.getAgent() != null) {
				query.append(" AND ac.agent.id = :agentId");
			}

			if (criteria.getSanctionNo() != null && !criteria.getSanctionNo().isEmpty()) {
				query.append(" AND ac.sanctionNo = :sanctionNo");
			}
			if (criteria.getInsuranceType().equals(InsuranceType.LIFE) || criteria.getInsuranceType().equals(InsuranceType.MEDICAL)
					|| criteria.getInsuranceType().equals(InsuranceType.HEALTH) || criteria.getInsuranceType().equals(InsuranceType.CRITICAL_ILLNESS)
					|| InsuranceType.SHORT_ENDOWMENT_LIFE.equals(criteria.getInsuranceType()) || InsuranceType.STUDENT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType())) {
				query.append(" AND (ac.referenceType = :referenceType OR ac.referenceType = :refBill)  ");
			} else {
				query.append(" AND ac.referenceType = :referenceType");
			}

			query.append(" AND ac.referenceNo IN " + policyObject);
			if (criteria.getInsuranceType().equals(InsuranceType.LIFE)) {
				query.append(" UNION ALL ");
				query.append(" SELECT DISTINCT ac FROM Agent a, AgentCommission ac WHERE a.id = ac.agent.id ");
				query.append(" AND ac.isPaid = 0 AND ac.status = 1 AND ac.invoiceDate IS NULL ");
				if (criteria.getStartDate() != null) {
					criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
					query.append(" AND ac.commissionStartDate >= :startDate");
				}

				if (criteria.getEndDate() != null) {
					criteria.setEndDate(Utils.resetStartDate(criteria.getEndDate()));
					query.append(" AND ac.commissionStartDate <= :endDate");
				}

				if (criteria.getAgent() != null) {
					query.append(" AND ac.agent.id = :agentId");
				}

				if (criteria.getSanctionNo() != null && !criteria.getSanctionNo().isEmpty()) {
					query.append(" AND ac.sanctionNo = :sanctionNo");
				}

				if (criteria.getInsuranceType().equals(InsuranceType.LIFE)) {
					query.append(" AND (ac.referenceType = :referenceType OR ac.referenceType = :refBill)  ");
				} else {
					query.append(" AND ac.referenceType = :referenceType");
				}

				query.append(" AND ac.referenceNo IN " + policyHistoryObject);
			}

			Query q = em.createQuery(query.toString());
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", criteria.getStartDate());
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", criteria.getEndDate());
			}

			if (criteria.getAgent() != null) {
				q.setParameter("agentId", criteria.getAgent().getId());
			}

			if (criteria.getSanctionNo() != null && !criteria.getSanctionNo().isEmpty()) {
				q.setParameter("sanctionNo", criteria.getSanctionNo());
			}

			switch (criteria.getInsuranceType()) {
				case STUDENT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.STUDENT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
					break;
				case LIFE:
					q.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.LIFE_BILL_COLLECTION);
					break;
				case PERSONAL_ACCIDENT:
					q.setParameter("referenceType", PolicyReferenceType.PA_POLICY);
					break;
				case FARMER:
					q.setParameter("referenceType", PolicyReferenceType.FARMER_POLICY);
					break;
				case PUBLIC_TERM_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY);
					break;
				case SHORT_ENDOWMENT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;

				// TODO FIX PSH
				case TRAVEL_INSURANCE:
					q.setParameter("referenceType", PolicyReferenceType.TRAVEL_PROPOSAL);
					break;
				case MEDICAL:
					q.setParameter("referenceType", PolicyReferenceType.MEDICAL_POLICY);
					q.setParameter("refBill", PolicyReferenceType.MEDICAL_BILL_COLLECTION);
					break;
				case HEALTH:
					q.setParameter("referenceType", PolicyReferenceType.HEALTH_POLICY);
					q.setParameter("refBill", PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
					break;
				case MICRO_HEALTH:
					q.setParameter("referenceType", PolicyReferenceType.MICRO_HEALTH_POLICY);
					break;
				case GROUP_MICRO_HEALTH:
					q.setParameter("referenceType", PolicyReferenceType.GROUP_MICRO_HEALTH);
					break;
				case CRITICAL_ILLNESS:
					q.setParameter("referenceType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY);
					q.setParameter("refBill", PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
					break;
				case PERSON_TRAVEL:
					q.setParameter("referenceType", PolicyReferenceType.PERSON_TRAVEL_POLICY);
					break;
				case GROUP_FARMER:
					q.setParameter("referenceType", PolicyReferenceType.GROUP_FARMER_PROPOSAL);
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				default:
					break;
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;

	}

	// Staff Agent Commission newly added
	@Override
	@SuppressWarnings("unchecked")
	public List<StaffAgentCommission> findStaffAgentCommission(StaffSanctionCriteria criteria) throws DAOException {

		List<StaffAgentCommission> result = new ArrayList<StaffAgentCommission>();
		try {
			String branchId = idConfigLoader.getBranchId();
			String policyObject = null;
			String policyHistoryObject = null;
			switch (criteria.getInsuranceType()) {
				case SINGLE_PREMIUM_CREDIT_LIFE:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
				case STUDENT_LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					break;
				case LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					policyHistoryObject = "(SELECT p.policyReferenceNo FROM LifePolicyHistory p )";
					break;
				case PERSONAL_ACCIDENT:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					break;
				case FARMER:
					policyObject = "(SELECT p.id FROM LifePolicy p  )";
					break;
				case PUBLIC_TERM_LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p )";
					break;
				case SHORT_ENDOWMENT_LIFE:
					policyObject = "(SELECT p.id FROM LifePolicy p  )";
					break;
				case FIDELITY:
					policyObject = "(SELECT p.id FROM Fidelity p ) ";
					break;
				case TRAVEL_INSURANCE:
					policyObject = "(SELECT p.id FROM TravelProposal p  ) ";
					break;
				case MEDICAL:
				case HEALTH:
				case MICRO_HEALTH:
				case CRITICAL_ILLNESS:
					policyObject = "(SELECT p.id FROM MedicalPolicy p) ";
					break;
				case GROUP_MICRO_HEALTH:
					policyObject = "(SELECT p.id FROM GroupMicroHealth p ) ";
					break;

				case PERSON_TRAVEL:
					policyObject = "(SELECT p.id FROM PersonTravelPolicy p  )";
					break;

				case GROUP_FARMER:
					policyObject = "(SELECT p.id FROM GroupFarmerProposal p  )";
					break;
				default:
					break;
			}

			StringBuffer query = new StringBuffer();
			query.append(" SELECT DISTINCT ac FROM Staff a, StaffAgentCommission ac WHERE a.id = ac.staff.id ");
			query.append(" AND ac.isPaid = 0 AND ac.status = 1 AND ac.invoiceDate IS NULL");
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}

			if (criteria.getStaff() != null) {
				query.append(" AND ac.staff.id = :agentId");
			}

			if (criteria.getSanctionNo() != null && !criteria.getSanctionNo().isEmpty()) {
				query.append(" AND ac.sanctionNo = :sanctionNo");
			}
			if (criteria.getInsuranceType().equals(InsuranceType.LIFE) || criteria.getInsuranceType().equals(InsuranceType.MEDICAL)
					|| criteria.getInsuranceType().equals(InsuranceType.HEALTH) || criteria.getInsuranceType().equals(InsuranceType.CRITICAL_ILLNESS)
					|| InsuranceType.SHORT_ENDOWMENT_LIFE.equals(criteria.getInsuranceType()) || InsuranceType.STUDENT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType())) {
				query.append(" AND (ac.referenceType = :referenceType OR ac.referenceType = :refBill)  ");
			} else {
				query.append(" AND ac.referenceType = :referenceType");
			}

			query.append(" AND ac.referenceNo IN " + policyObject);
			if (criteria.getInsuranceType().equals(InsuranceType.LIFE)) {
				query.append(" UNION ALL ");
				query.append(" SELECT DISTINCT ac FROM Staff a, StaffAgentCommission ac WHERE a.id = ac.staff.id ");
				query.append(" AND ac.isPaid = 0 AND ac.status = 1 AND ac.invoiceDate IS NULL ");
				if (criteria.getStartDate() != null) {
					criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
					query.append(" AND ac.commissionStartDate >= :startDate");
				}

				if (criteria.getEndDate() != null) {
					criteria.setEndDate(Utils.resetStartDate(criteria.getEndDate()));
					query.append(" AND ac.commissionStartDate <= :endDate");
				}

				if (criteria.getStaff() != null) {
					query.append(" AND ac.staff.id = :agentId");
				}

				if (criteria.getSanctionNo() != null && !criteria.getSanctionNo().isEmpty()) {
					query.append(" AND ac.sanctionNo = :sanctionNo");
				}

				if (criteria.getInsuranceType().equals(InsuranceType.LIFE)) {
					query.append(" AND (ac.referenceType = :referenceType OR ac.referenceType = :refBill)  ");
				} else {
					query.append(" AND ac.referenceType = :referenceType");
				}

				query.append(" AND ac.referenceNo IN " + policyHistoryObject);
			}

			Query q = em.createQuery(query.toString());
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", criteria.getStartDate());
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", criteria.getEndDate());
			}

			if (criteria.getStaff() != null) {
				q.setParameter("agentId", criteria.getStaff().getId());
			}

			if (criteria.getSanctionNo() != null && !criteria.getSanctionNo().isEmpty()) {
				q.setParameter("sanctionNo", criteria.getSanctionNo());
			}

			switch (criteria.getInsuranceType()) {
				case STUDENT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.STUDENT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
					break;
				case LIFE:
					q.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.LIFE_BILL_COLLECTION);
					break;
				case PERSONAL_ACCIDENT:
					q.setParameter("referenceType", PolicyReferenceType.PA_POLICY);
					break;
				case FARMER:
					q.setParameter("referenceType", PolicyReferenceType.FARMER_POLICY);
					break;
				case PUBLIC_TERM_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY);
					break;
				case SHORT_ENDOWMENT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;

				// TODO FIX PSH
				case TRAVEL_INSURANCE:
					q.setParameter("referenceType", PolicyReferenceType.TRAVEL_PROPOSAL);
					break;
				case MEDICAL:
					q.setParameter("referenceType", PolicyReferenceType.MEDICAL_POLICY);
					q.setParameter("refBill", PolicyReferenceType.MEDICAL_BILL_COLLECTION);
					break;
				case HEALTH:
					q.setParameter("referenceType", PolicyReferenceType.HEALTH_POLICY);
					q.setParameter("refBill", PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
					break;
				case MICRO_HEALTH:
					q.setParameter("referenceType", PolicyReferenceType.MICRO_HEALTH_POLICY);
					break;
				case GROUP_MICRO_HEALTH:
					q.setParameter("referenceType", PolicyReferenceType.GROUP_MICRO_HEALTH);
					break;
				case CRITICAL_ILLNESS:
					q.setParameter("referenceType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY);
					q.setParameter("refBill", PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
					break;
				case PERSON_TRAVEL:
					q.setParameter("referenceType", PolicyReferenceType.PERSON_TRAVEL_POLICY);
					break;
				case GROUP_FARMER:
					q.setParameter("referenceType", PolicyReferenceType.GROUP_FARMER_PROPOSAL);
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				default:
					break;
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of StaffAgentCommission by criteria.", pe);
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentCommissionDTO> findAgentCommissionForPayment() throws DAOException {
		List<AgentCommissionDTO> result = new ArrayList<AgentCommissionDTO>();
		List<Object[]> agentComList = new ArrayList<Object[]>();
		try {
			Query q = em.createQuery(
					"SELECT ac.agent, sum(ac.commission), sum(ac.withHoldingTax), max(ac.commissionStartDate), min(ac.commissionStartDate), ac.invoiceNo,ac.paymentChannel FROM AgentCommission ac, WorkFlow w WHERE ac.id = w.referenceNo AND ac.isPaid = False AND ac.invoiceDate IS NOT NULL GROUP BY ac.agent, ac.invoiceNo,ac.paymentChannel");
			agentComList = q.getResultList();
			Agent agent;
			Double commission;
			Double withHoldingTax;
			Date toDate;
			Date fromDate;
			String invoiceNo;
			PaymentChannel paymentChannel;
			for (Object[] items : agentComList) {
				agent = (Agent) items[0];
				commission = (Double) items[1];
				withHoldingTax = items[2] != null ? (Double) items[2] : 0;
				toDate = (Date) items[3];
				fromDate = (Date) items[4];
				invoiceNo = items[5].toString();
				paymentChannel = (PaymentChannel) items[6];
				result.add(new AgentCommissionDTO(agent, commission, withHoldingTax, fromDate, toDate, invoiceNo, paymentChannel));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Agent Commission For Payment", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentCommission> findAgentCommissionPaymentByAgent(String invoiceNo, String agentId) throws DAOException {
		List<AgentCommission> result = new ArrayList<AgentCommission>();

		try {
			Query q = em.createQuery("SELECT ac FROM AgentCommission ac, WorkFlow w " + "WHERE ac.id = w.referenceNo AND ac.isPaid = False AND ac.invoiceDate IS NOT NULL "
					+ "AND ac.invoiceNo=:invoiceNo AND ac.agent.id = :agentId");
			q.setParameter("invoiceNo", invoiceNo);
			q.setParameter("agentId", agentId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Agent Commission For Payment By Agent", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<StaffAgentCommission> findStaffCommissionPaymentByAgent(String invoiceNo, String agentId) throws DAOException {
		List<StaffAgentCommission> result = new ArrayList<>();

		try {
			Query q = em.createQuery("SELECT ac FROM StaffAgentCommission ac, WorkFlow w " + "WHERE ac.id = w.referenceNo AND ac.isPaid = False AND ac.invoiceDate IS NOT NULL "
					+ "AND ac.invoiceNo=:invoiceNo AND ac.staff.id = :agentId");
			q.setParameter("invoiceNo", invoiceNo);
			q.setParameter("agentId", agentId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Agent Commission For Payment By Agent", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentAgentCommission(AgentCommission commission) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE AgentCommission ac SET ac.isPaid = True, ac.paymentDate = :pDate, ac.chequeNo=:chequeNo WHERE ac.id = :id");
			q.setParameter("id", commission.getId());
			q.setParameter("pDate", new Date());
			q.setParameter("chequeNo", commission.getChequeNo());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Agent Commission after Payment", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentStaffCommission(StaffAgentCommission commission) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE StaffAgentCommission ac SET ac.isPaid = True, ac.paymentDate = :pDate, ac.chequeNo=:chequeNo WHERE ac.id = :id");
			q.setParameter("id", commission.getId());
			q.setParameter("pDate", new Date());
			q.setParameter("chequeNo", commission.getChequeNo());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Agent Commission after Payment", pe);
		}
	}

	// update Agent Commission
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAgentCommissionInfo(AgentCommission agentComission) throws DAOException {
		try {
			Query q = em.createQuery(
					"UPDATE AgentCommission ac SET ac.invoiceDate = :invoiceDate, ac.invoiceNo=:invoiceNo,ac.bank=:bank,ac.bankaccountno=:bankaccountno, ac.paymentChannel=:paymentChannel WHERE ac.id = :id");
			q.setParameter("invoiceDate", new Date());
			q.setParameter("id", agentComission.getId());
			q.setParameter("invoiceNo", agentComission.getInvoiceNo());
			q.setParameter("bank", agentComission.getBank());
			q.setParameter("bankaccountno", agentComission.getBankaccountno());
			q.setParameter("paymentChannel", agentComission.getPaymentChannel());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Agent Commission Info", pe);
		}
	}

	// update Staff Agent Commission
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStaffAgentCommissionInfo(StaffAgentCommission agentComission) throws DAOException {
		try {
			Query q = em.createQuery(
					"UPDATE StaffAgentCommission ac SET ac.invoiceDate = :invoiceDate, ac.invoiceNo=:invoiceNo,ac.bank=:bank,ac.bankaccountno=:bankaccountno, ac.paymentChannel=:paymentChannel WHERE ac.id = :id");
			q.setParameter("invoiceDate", new Date());
			q.setParameter("id", agentComission.getId());
			q.setParameter("invoiceNo", agentComission.getInvoiceNo());
			q.setParameter("bank", agentComission.getBank());
			q.setParameter("bankaccountno", agentComission.getBankaccountno());
			q.setParameter("paymentChannel", agentComission.getPaymentChannel());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Staff Agent Commission Info", pe);
		}
	}

	// Set InvoiceNo and InvoiceDate For Coinsurance
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCoinsuranceForInvoice(Coinsurance coinsurance) throws DAOException {
		try {
			Query q = em.createQuery(
					"UPDATE Coinsurance c SET c.invoiceDate = :invoiceDate, c.invoiceNo= :invoiceNo, c.accountNo = :accountNo, c.paymentChannel= :paymentChannel, c.companybank = :companybank WHERE c.id = :id");
			q.setParameter("invoiceDate", new Date());
			q.setParameter("id", coinsurance.getId());
			q.setParameter("invoiceNo", coinsurance.getInvoiceNo());
			q.setParameter("accountNo", coinsurance.getAccountNo());
			q.setParameter("paymentChannel", coinsurance.getPaymentChannel());
			q.setParameter("companybank", coinsurance.getCompanybank());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Coinsurance Info", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findCoInsuranceForDashboardByType(CoinsuranceType type, WorkflowTask workFlowTask) throws DAOException {
		List<Coinsurance> result = new ArrayList<Coinsurance>();
		try {
			Query q = em.createQuery(
					"SELECT c FROM Coinsurance c, WorkFlow w WHERE c.id = w.referenceNo AND w.workflowTask = :workFlowTask AND c.coinsuranceType=:coinsuType AND c.paymentDate IS NULL");
			q.setParameter("coinsuType", type);
			q.setParameter("workFlowTask", workFlowTask);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find CoInsurance For Payment Dashboard", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCoinsurance(Coinsurance coinsurance) throws DAOException {
		try {
			em.merge(coinsurance);
			updateProcessLog(TableName.COINSURANCE, coinsurance.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Coinsurance after Payment", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByClaimProposalComplete(String claimId, PolicyReferenceType referenceType) throws DAOException {
		List<Payment> result = null;
		try {
			Query q = em.createNamedQuery("Payment.findByReferenceNoAndReferenceTypeComplete");
			q.setParameter("referenceNo", claimId);
			q.setParameter("referenceType", referenceType);
			result = q.getResultList();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo and ReferenceType : " + claimId, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBillCollection(String receiptNo, boolean complete) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE Payment p SET p.complete = :complete WHERE p.receiptNo = :receiptNo ");
			q.setParameter("complete", complete);
			q.setParameter("receiptNo", receiptNo);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Bill Collection after Payment", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReferenceNoAndMaxDate(String referenceNo) throws DAOException {
		List<Payment> result = new ArrayList<Payment>();
		try {
			/* create query */
			Query query = em.createNamedQuery("Payment.findPaymentByReferenceNoAndMaxDate");
			query.setParameter("referenceNo", referenceNo);
			result = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find payment List by  ReferenceNo and Max Date : ", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReferenceNoAndMaxDateForAgentInvoice(String referenceNo) throws DAOException {
		List<Payment> result = new ArrayList<Payment>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT p from Payment p WHERE p.paymentDate=(select MAX(p1.paymentDate) from Payment p1 where  p1.referenceNo = :referenceNo1) "
					+ "AND p.referenceNo = :referenceNo");
			Query query = em.createQuery(queryString.toString());
			query.setParameter("referenceNo1", referenceNo);
			query.setParameter("referenceNo", referenceNo);
			result = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find payment List by  ReferenceNo and Max Date : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentCoinsuranceCommission(Coinsurance coinsurance) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE Coinsurance ac SET ac.chequeNo=:chequeNo WHERE ac.id = :id");
			q.setParameter("id", coinsurance.getId());
			// q.setParameter("pDate", new Date());
			q.setParameter("chequeNo", coinsurance.getChequeNo());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to updateCoinsurance Commissionn after Payment", pe);
		}
	}

	// new
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReceiptNo(String receiptNo) throws DAOException {
		List<Payment> result = null;
		try {
			Query q = em.createQuery("SELECT p FROM Payment p WHERE p.receiptNo = :receiptNo");
			q.setParameter("receiptNo", receiptNo);
			result = q.getResultList();
			em.flush();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : " + receiptNo, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isLatestBCReceiptNo(String receiptNo) throws DAOException {
		boolean latest = false;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT p FROM Payment p WHERE p.receiptNo = :receiptNo");
			query.append(" AND p.toTerm=(SELECT MAX(p1.toTerm) FROM Payment p1 WHERE p1.referenceNo=p.referenceNo)");
			Query q = em.createQuery(query.toString());
			q.setParameter("receiptNo", receiptNo);
			Payment p = (Payment) q.getSingleResult();
			latest = (p != null);
		} catch (NoResultException e) {
			e.printStackTrace();
			return latest;
		} catch (PersistenceException pe) {
			throw translate("Failed to check latest BillColl ReceiptNo", pe);
		}
		return latest;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findBCPaymentByReceiptNo(PolicyCriteria policyCriteria) throws DAOException {
		List<Payment> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT p FROM Payment p WHERE  p.referenceType = :referenceType");

			if (policyCriteria.getCriteriaValue() != null && !policyCriteria.getCriteriaValue().isEmpty()) {
				query.append(" AND p.receiptNo = :receiptNo ");
			}

			if (policyCriteria.getFromDate() != null) {
				query.append(" AND p.confirmDate >= :startDate ");
			}

			if (policyCriteria.getToDate() != null) {
				query.append(" AND p.confirmDate <= :endDate ");
			}

			Query q = em.createQuery(query.toString());
			if (policyCriteria.getCriteriaValue() != null && !policyCriteria.getCriteriaValue().isEmpty()) {
				q.setParameter("receiptNo", policyCriteria.getCriteriaValue());
			}
			if (policyCriteria.getFromDate() != null) {
				q.setParameter("startDate", policyCriteria.getFromDate());
			}

			if (policyCriteria.getToDate() != null) {
				q.setParameter("endDate", policyCriteria.getToDate());
			}
			if (ReferenceType.MEDICAL_BILL_COLLECTION.equals(policyCriteria.getReferenceType())) {
				q.setParameter("referenceType", PolicyReferenceType.MEDICAL_BILL_COLLECTION);
			} else if ((ReferenceType.HEALTH_POLICY_BILL_COLLECTION.equals(policyCriteria.getReferenceType()))) {
				q.setParameter("referenceType", PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
			} else if ((ReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION.equals(policyCriteria.getReferenceType()))) {
				q.setParameter("referenceType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
			} else if ((ReferenceType.LIFE_BILL_COLLECTION.equals(policyCriteria.getReferenceType()))) {
				q.setParameter("referenceType", PolicyReferenceType.LIFE_BILL_COLLECTION);
			} else if ((ReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION.equals(policyCriteria.getReferenceType()))) {
				q.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
			}

			result = q.getResultList();
			em.flush();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : " + policyCriteria.getCriteriaValue(), pe);
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findAllBCPaymentByPolicyNo(String policyNo, ReferenceType referenceType) throws DAOException {
		String policyType = null;
		switch (referenceType) {
			case STUDENT_LIFE_BILL_COLLECTION:
			case LIFE_BILL_COLLECTION:
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				policyType = "LifePolicy";
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				policyType = "MedicalPolicy";
				break;
			default:
				break;

		}
		List<Payment> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT p FROM Payment p WHERE  p.referenceType = :referenceType");

			query.append(" AND p.referenceNo IN (SELECT l.id from " + policyType + " l where l.policyNo =:policyNo) AND p.reverse='false'  ");

			Query q = em.createQuery(query.toString());
			q.setParameter("policyNo", policyNo);

			if (ReferenceType.MEDICAL_BILL_COLLECTION.equals(referenceType)) {
				q.setParameter("referenceType", PolicyReferenceType.MEDICAL_BILL_COLLECTION);
			} else if ((ReferenceType.HEALTH_POLICY_BILL_COLLECTION.equals(referenceType))) {
				q.setParameter("referenceType", PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
			} else if ((ReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION.equals(referenceType))) {
				q.setParameter("referenceType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
			} else if ((ReferenceType.LIFE_BILL_COLLECTION.equals(referenceType))) {
				q.setParameter("referenceType", PolicyReferenceType.LIFE_BILL_COLLECTION);
			} else if ((ReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION.equals(referenceType))) {
				q.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
			} else if ((ReferenceType.STUDENT_LIFE_BILL_COLLECTION.equals(referenceType))) {
				q.setParameter("referenceType", PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
			}

			result = q.getResultList();
			em.flush();
		} catch (

		NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : " + policyNo, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findReceiptNoByReferenceNo(String referenceNo) throws DAOException {
		String result = null;
		try {
			Query q = em.createNamedQuery("Payment.findReceiptNoByReferenceNo");
			q.setParameter("referenceNo", referenceNo);
			result = (String) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Cash Receipt No by Reference No : " + referenceNo, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findReceiptNoByProposalId(String proposalId, String policyObject) throws DAOException {
		String result = null;
		try {
			Query q = em.createQuery("select p.receiptNo from Payment p, " + policyObject + " fp where p.referenceNo = fp.id and fp.fireProposal.id = :proposalId ");
			q.setParameter("proposalId", proposalId);
			result = (String) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Cash Receipt No by Proposal ID : " + proposalId, pe);
		}
		return result;
	}

	/*
	 * private void setIDPrefixForInsert(TLF tlf) { String tlfPrefix =
	 * customIDGenerator.getPrefix(TLF.class); tlf.setPrefix(tlfPrefix); }
	 */

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TLF> findTLFbyTLFNo(String tlfNo, Boolean isClearing) throws DAOException {

		List<TLF> results;
		try {
			String query = "Select t From TLF t Where t.tlfNo = :tlfNo ";
			if (isClearing != null)
				query += "AND t.clearing = :clearing ";
			TypedQuery<TLF> q = em.createQuery(query, TLF.class);
			q.setParameter("tlfNo", tlfNo);
			if (isClearing != null)
				q.setParameter("clearing", isClearing);
			results = q.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : ", pe);
		}

		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TLF> findTLFbyReferenceNoAndReferenceType(String referenceNo, PolicyReferenceType policyReferenceType) throws DAOException {

		List<TLF> results;
		try {
			String query = "Select t From TLF t Where t.referenceNo = :referenceNo and t.referenceType = :referenceType";
			TypedQuery<TLF> q = em.createQuery(query, TLF.class);
			q.setParameter("referenceNo", referenceNo);
			q.setParameter("referenceType", policyReferenceType);

			results = q.getResultList();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by Reference No and Reference Type : ", pe);
		}

		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePayments(List<Payment> paymentList) throws DAOException {
		try {
			for (Payment payment : paymentList) {
				List<TLF> tlfList = findTLFbyTLFNo(payment.getReceiptNo(), null);
				for (TLF tlf : tlfList) {
					TLF margedTLF = em.merge(tlf);
					em.remove(margedTLF);
				}
				Payment margedPayment = em.merge(payment);
				em.remove(margedPayment);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Customer", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteTLFs(List<TLF> tlfList) throws DAOException {
		try {
			for (TLF tlf : tlfList) {
				TLF margedTLF = em.merge(tlf);
				em.remove(margedTLF);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Customer", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TLFVoucherDTO> findTLFVoucher(TLFVoucherCriteria criteria) throws DAOException {
		List<TLFVoucherDTO> result = new ArrayList<TLFVoucherDTO>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT NEW " + TLFVoucherDTO.class.getName()
					+ "(coa.acode, tlf.narration, tr.status, coa.acName, c , p.confirmDate, tlf.homeAmount, p.rate,p.amount, p.basicPremium, p.addOnPremium, tlf.localAmount) "
					+ " FROM TLF tlf LEFT JOIN Payment p left JOIN Coa coa INNER JOIN Currency c INNER JOIN TranType tr INNER JOIN CurrencyChartOfAccount ccoa  WHERE p.receiptNo IS NOT NULL");
			query.append(" and p.receiptNo=tlf.tlfNo and tlf.coaId =  ccoa.id and ccoa.coaid = coa.id and c.id =  tlf.currencyId AND tr.id = tlf.tranTypeId");
			if (null != criteria.getReceiptNo() && !criteria.getReceiptNo().isEmpty()) {
				query.append(" AND p.receiptNo = :receiptNo");
			}
			if (null != criteria.getBpmsReceiptNo() && !criteria.getBpmsReceiptNo().isEmpty()) {
				query.append(" AND p.bpmsReceiptNo = :bpmsReceiptNo");
			}

			Query q = em.createQuery(query.toString());

			if (null != criteria.getReceiptNo() && !criteria.getReceiptNo().isEmpty()) {
				q.setParameter("receiptNo", criteria.getReceiptNo());
			}
			if (null != criteria.getBpmsReceiptNo() && !criteria.getBpmsReceiptNo().isEmpty()) {
				q.setParameter("bpmsReceiptNo", criteria.getBpmsReceiptNo());
			}

			result = q.getResultList();

			// for GL Code
			// Query from COA by account base code
			String baseAcName = null;
			for (TLFVoucherDTO dto : result) {
				baseAcName = paymentDAO.findCOAAccountNameByCode(dto.getGlCode());
				dto.setBaseAcName(baseAcName);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CashReceiptDTO by receiptNo.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClaimVoucherDTO> findClaimVoucher(String claimNo, String damage) throws DAOException {
		List<ClaimVoucherDTO> result = new ArrayList<ClaimVoucherDTO>();
		String tableName;
		if (damage.equals("Own Vehicle Damage") || damage.equals("Third Party Vehicle Damage")) {
			tableName = "DamagedVehicle tb, ";
		} else if (damage.equals("Damage Other")) {
			tableName = "DamagedOther tb, ";
		} else {
			tableName = "PersonCasualty tb, ";
		}
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT NEW " + ClaimVoucherDTO.class.getName() + "(tlf.coaId, tlf.narration, coa.acName, tlf.status, p.claimAmount)");
			query.append("FROM MotorClaim mc, ");
			query.append(tableName);
			query.append("TLF tlf INNER JOIN Coa coa on tlf.coaId =  coa.acode, Payment p ");
			query.append("WHERE p.referenceNo = tlf.referenceNo AND ");
			query.append("tb.claim.id = mc.id AND tlf.referenceNo = tb.id ");
			query.append("AND mc.claimReferenceNo =:claimNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("claimNo", claimNo);

			result = q.getResultList();
			// for GL Code
			// Query from COA by account base code
			String baseAcName = null;
			for (ClaimVoucherDTO dto : result) {
				baseAcName = paymentDAO.findCOAAccountNameByCode(dto.getGlCode());
				dto.setBaseAcName(baseAcName);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CashReceiptDTO by receiptNo.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TLF> findTLFbyENONo(String enoNo) throws DAOException {
		List<TLF> results;
		try {
			String query = "Select t From TLF t Where t.enoNo = :enoNo";
			TypedQuery<TLF> q = em.createQuery(query, TLF.class);
			q.setParameter("enoNo", enoNo);
			results = q.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : ", pe);
		}
		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findByChalanNo(String chalanNo) throws DAOException {
		Payment result = null;
		try {
			Query q = em.createNamedQuery("Payment.findByChalanNo");
			q.setParameter("chalanNoid", chalanNo);
			result = (Payment) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReferenceNo : " + chalanNo, pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentTableDTO> findByChalanNoForClaim(List<String> receiptList, PaymentReferenceType referenceType, Boolean complete) throws DAOException {

		List<PaymentTableDTO> resultList = new ArrayList<PaymentTableDTO>();
		try {
			StringBuffer buffer = new StringBuffer(
					"SELECT mcp FROM  MedicalClaimProposal mcp inner join Payment p on mcp.id = p.referenceNo Where p.chalanNo = :receiptNo AND p.referenceType = :referenceType");
			buffer.append(" AND p.complete = " + complete);
			Query q = em.createQuery(buffer.toString());
			for (String receiptNo : receiptList) {
				q.setParameter("receiptNo", receiptNo);
				q.setParameter("referenceType", PaymentReferenceType.CLAIM);
				List<MedicalClaimProposal> medClaimProposalList = q.getResultList();

				if (medClaimProposalList != null && medClaimProposalList.size() > 0) {
					MedicalClaimProposalDTO medClaimProposalDTO = MedicalClaimProposalFactory.createMedicalClaimProposalDTO(medClaimProposalList.get(0));
					for (MedicalClaim mc : medClaimProposalList.get(0).getMedicalClaimList()) {
						if (mc.getClaimRole().equals(MedicalClaimRole.OPERATION_CLAIM) && mc.isApproved()) {
							medClaimProposalDTO.setOperationClaimDTO(medicalClaimProposalService.findOperationClaimById(mc.getId()));
						}
						if (mc.getClaimRole().equals(MedicalClaimRole.DEATH_CLAIM) && mc.isApproved()) {
							medClaimProposalDTO.setDeathClaimDTO(medicalClaimProposalService.findDeathClaimById(mc.getId()));
						}
						if (mc.getClaimRole().equals(MedicalClaimRole.HOSPITALIZED_CLAIM) && mc.isApproved()) {
							medClaimProposalDTO.setHospitalizedClaimDTO(medicalClaimProposalService.findHospitalizedClaimById(mc.getId()));
						}
						if (mc.getClaimRole().equals(MedicalClaimRole.MEDICATION_CLAIM) && mc.isApproved()) {
							medClaimProposalDTO.setMedicationClaimDTO(medicalClaimProposalService.findMedicationClaimById(mc.getId()));
						}
					}
					double hosAmt = 0.0;
					double optAmt = 0.0;
					double medAmt = 0.0;
					double deaAmt = 0.0;
					StringBuffer sb = new StringBuffer();
					if (medClaimProposalDTO.getHospitalizedClaimDTO() != null) {
						hosAmt = medClaimProposalDTO.getHospitalizedClaimDTO().getHospitalizedAmount();
					}

					if (medClaimProposalDTO.getOperationClaimDTO() != null) {
						optAmt = medClaimProposalDTO.getOperationClaimDTO().getOperationFee();
					}

					if (medClaimProposalDTO.getMedicationClaimDTO() != null) {
						medAmt = medClaimProposalDTO.getMedicationClaimDTO().getMedicationFee();
					}

					if (medClaimProposalDTO.getDeathClaimDTO() != null) {
						deaAmt = medClaimProposalDTO.getDeathClaimDTO().getDeathClaimAmount();
					}
					for (MedicalClaimBeneficiaryDTO medBene : medClaimProposalDTO.getMedicalClaimBeneficiariesList()) {
						if (medBene != null) {
							if (sb.length() > 0) {
								sb.append(",");
							}
							if (medBene.getBeneficiaryRole().equals(MedicalBeneficiaryRole.SUCCESSOR)) {
								sb.append(medBene.getName());
							} else if (medBene.getBeneficiaryRole().equals(MedicalBeneficiaryRole.INSURED_PERSON)) {
								sb.append(medBene.getMedicalPolicyInsuredPersonDTO().getFullName());
							} else if (medBene.getBeneficiaryRole().equals(MedicalBeneficiaryRole.GUARDIAN)) {
								sb.append(medBene.getMedPolicyGuardianDTO().getCustomerDTO().getFullName());
							} else if (medBene.getBeneficiaryRole().equals(MedicalBeneficiaryRole.BENEFICIARY)) {
								sb.append(medBene.getMedPolicyInsuBeneDTO().getFullName());
							}
						}
					}
					PaymentTableDTO paymentTableDTO = new PaymentTableDTO(sb, receiptNo, optAmt, medAmt, hosAmt, deaAmt, 0.0);
					resultList.add(paymentTableDTO);
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : ", pe);
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<PaymentTrackDTO> findPaymentTrack(String policyNo) throws DAOException {
		List<PaymentTrackDTO> resultList = new ArrayList<>();
		try {
			List<PaymentTrackDTO> tempList = null;

			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + PaymentTrackDTO.class.getName());
			buffer.append(" (p.id, p.referenceNo, p.paymentDate, p.receiptNo, p.paymentChannel, p.basicPremium, p.fromTerm, p.toTerm) ");
			buffer.append("FROM Payment p JOIN LifePolicy l ON l.id = p.referenceNo WHERE l.policyNo = :policyNo ");
			buffer.append("AND p.complete = 1 ");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("policyNo", policyNo);
			tempList = query.getResultList();
			if (!tempList.isEmpty())
				resultList.addAll(tempList);

			buffer = new StringBuffer();
			buffer.append("SELECT NEW " + PaymentTrackDTO.class.getName());
			buffer.append(" (p.id, p.referenceNo, p.paymentDate, p.receiptNo, p.paymentChannel, p.basicPremium, p.fromTerm, p.toTerm) ");
			buffer.append("FROM Payment p JOIN LifePolicyHistory l ON l.id = p.referenceNo WHERE l.policyNo = :policyNo ");
			buffer.append("AND p.complete = 1");
			query = em.createQuery(buffer.toString());
			query.setParameter("policyNo", policyNo);
			tempList = query.getResultList();
			if (!tempList.isEmpty())
				resultList.addAll(tempList);

			Collections.sort(resultList, new Comparator<PaymentTrackDTO>() {
				@Override
				public int compare(PaymentTrackDTO paymentTrack1, PaymentTrackDTO paymentTrack2) {
					return paymentTrack1.getPaymentDate().compareTo(paymentTrack2.getPaymentDate());
				}
			});
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment Track List by PolicyNo : =" + policyNo, pe);
		}

		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Double findSummaryByReferenceNo(String policyId, PolicyReferenceType referenceType, Boolean complete) throws DAOException {
		Double result = null;
		try {
			StringBuffer sf = new StringBuffer(
					"SELECT SUM(p.basicPremium) FROM Payment p WHERE p.referenceNo=:policyId AND p.referenceType=:referenceType AND p.complete=:complete");
			Query q = em.createQuery(sf.toString());
			q.setParameter("policyId", policyId);
			q.setParameter("referenceType", referenceType);
			q.setParameter("complete", complete);
			result = (Double) q.getSingleResult();
			return result;
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find AcceptedInfo", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PaymentDTO> findNoPaidPaymentByReferenceNo(String policyId) {
		List<PaymentDTO> result = null;
		try {
			StringBuilder query = new StringBuilder();
			query.append("Select New " + PaymentDTO.class.getName() + " (p.receiptNo,p.complete) from Payment p where p.referenceNo=:policyId");
			Query q = em.createQuery(query.toString());
			q.setParameter("policyId", policyId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find NoPaid Payment By ReferenceNo", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean havePendingPayment(String policyNo) {
		boolean result = false;
		try {
			StringBuilder query = new StringBuilder();
			query.append("SELECT CASE WHEN (COUNT(p.id) > 0)  THEN TRUE ELSE FALSE END  FROM Payment p ");
			query.append("LEFT OUTER JOIN LifePolicy l ON l.id = p.referenceNo LEFT OUTER JOIN LifePolicyHistory h ON h.id = p.referenceNo ");
			query.append("WHERE p.complete = FALSE AND l.policyNo = :policyNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("policyNo", policyNo);
			result = (boolean) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find NoPaid Payment By ReferenceNo", pe);
		}

		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findById(String paymentId) {
		Payment result = null;
		try {
			result = em.find(Payment.class, paymentId);
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment By Id", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Payment findPaymentForCashReceipt(int lastPaymentTerm, String policyId) {
		Payment result = null;
		try {
			Query q = em.createQuery("Select p from Payment p where p.referenceNo= :policyId and p.toTerm = :lastPaymentTerm");
			q.setParameter("lastPaymentTerm", lastPaymentTerm);
			q.setParameter("policyId", policyId);
			result = (Payment) q.getSingleResult();

		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment By Id", pe);
		}
		return result;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByReceiptNoandPolicyId(String receiptNo, String policyId) {
		List<Payment> result = null;
		try {
			StringBuilder query = new StringBuilder();
			query.append("SELECT p FROM Payment p  WHERE  p.isPO = 'true' ");

			if (!policyId.isEmpty() && policyId != null) {
				query.append(" AND p.referenceNo = :policyId ");
			}
			if (!receiptNo.isEmpty() && receiptNo != null) {
				query.append(" AND p.receiptNo = :receiptNo ");
			}
			Query q = em.createQuery(query.toString());

			if (!policyId.isEmpty() && policyId != null) {
				q.setParameter("policyId", policyId);
			}
			if (!receiptNo.isEmpty() && receiptNo != null) {
				q.setParameter("receiptNo", receiptNo);
			}
			result = q.getResultList();
			em.flush();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Payment by ReceiptNo : " + receiptNo, pe);
		}
		return result;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void reversetPaymentByReceiptNo(String receiptNo) throws DAOException {
		try {
			TypedQuery<Payment> query = em.createNamedQuery("Payment.reversePaymentByReceiptNo", Payment.class);
			query.setParameter("receiptNo", receiptNo);
			query.executeUpdate();
		} catch (PersistenceException pe) {
			throw translate("fail to reverse payment by receipt no", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReceiptNoAndComplete(TLFVoucherCriteria criteria) throws DAOException {
		try {
			List<Payment> result = new ArrayList<Payment>();
			StringBuffer query = new StringBuffer();
			query.append("SELECT p FROM Payment p WHERE p.receiptNo IS NOT NULL");

			if (null != criteria.getReceiptNo() && !criteria.getReceiptNo().isEmpty()) {
				query.append(" AND p.receiptNo = :receiptNo");
			}
			if (null != criteria.getBpmsReceiptNo() && !criteria.getBpmsReceiptNo().isEmpty()) {
				query.append(" AND p.bpmsReceiptNo = :bpmsReceiptNo");
			}

			Query q = em.createQuery(query.toString());

			if (null != criteria.getReceiptNo() && !criteria.getReceiptNo().isEmpty()) {
				q.setParameter("receiptNo", criteria.getReceiptNo());
			}
			if (null != criteria.getBpmsReceiptNo() && !criteria.getBpmsReceiptNo().isEmpty()) {
				q.setParameter("bpmsReceiptNo", criteria.getBpmsReceiptNo());
			}

			result = q.getResultList();

			// query.append(" SELECT p FROM Payment p WHERE p.receiptNo IS NOT
			// NULL", Payment.class);
			//
			// if (null != criteria.getReceiptNo() &&
			// !criteria.getReceiptNo().isEmpty()) {
			// query.append(" AND p.receiptNo = :receiptNo");
			// }
			// if (null != criteria.getBpmsReceiptNo() &&
			// !criteria.getBpmsReceiptNo().isEmpty()) {
			// query.append(" AND p.bpmsReceiptNo = :bpmsReceiptNo");
			// }
			//
			// Query q = em.createQuery(query.toString());
			//
			// if (null != criteria.getReceiptNo() &&
			// !criteria.getReceiptNo().isEmpty()) {
			// q.setParameter("receiptNo", criteria.getReceiptNo());
			// }
			// if (null != criteria.getBpmsReceiptNo() &&
			// !criteria.getBpmsReceiptNo().isEmpty()) {
			// q.setParameter("bpmsReceiptNo", criteria.getBpmsReceiptNo());
			// }
			// query.setParameter("criteria", criteria);
			return result;
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("fail to find payment by receiptNo", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByBankWalletId(String id) {
		List<Payment> result = null;
		try {
			TypedQuery<Payment> query = em.createNamedQuery("Payment.findByBankWalletId", Payment.class);
			query.setParameter("id", id);
			result = query.getResultList();
		} catch (PersistenceException pe) {
			throw translate("fail to find payment by wallet id", pe);
		}
		return result;
	}

}
