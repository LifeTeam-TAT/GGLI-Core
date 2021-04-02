package org.ace.insurance.travel.personTravel.policy.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.common.Utils;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.travel.personTravel.PersonTravelMonthlyIncomeReport;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.persistence.interfaces.IPersonTravelPolicyDAO;
import org.ace.insurance.web.manage.report.travel.personTravel.criteria.PersonTravelCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("PersonTravelPolicyDAO")
public class PersonTravelPolicyDAO extends BasicDAO implements IPersonTravelPolicyDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(PersonTravelPolicy personTravelPolicy) throws DAOException {
		try {
			em.persist(personTravelPolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to insert personTravelPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(PersonTravelPolicy personTravelPolicy) throws DAOException {
		try {
			em.merge(personTravelPolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to update LifePolicy", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPolicyById(String id) throws DAOException {
		PersonTravelPolicy policy = null;
		try {
			policy = em.find(PersonTravelPolicy.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to insert personTravelPolicy", pe);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPolicyByProposalId(String proposalId) throws DAOException {
		PersonTravelPolicy policy = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT p FROM PersonTravelPolicy p WHERE p.personTravelProposal.id=:proposalId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("proposalId", proposalId);
			policy = (PersonTravelPolicy) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to Find PersonTravelPolicy", pe);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findPolicyIdByProposalId(String proposalId) throws DAOException {
		String policyId = null;
		try {
			Query q = em.createQuery("SELECT p.id FROM PersonTravelPolicy p WHERE p.personTravelProposal.id=:proposalId");
			q.setParameter("proposalId", proposalId);
			policyId = (String) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to Find PersonTravelPolicy Id", pe);
		}
		return policyId;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) throws DAOException {
		List<Payment> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT p FROM PersonTravelPolicy pt, Payment p, TLF t WHERE ");
			if (criteria.getReceiptNoCriteria() != null) {
				switch (criteria.getReceiptNoCriteria()) {
					case RECEIPTNO: {
						query.append(" p.receiptNo like :receiptNo AND pt.id = p.referenceNo AND p.isPO = TRUE ");
						query.append("AND pt.id = t.referenceNo AND t.clearing = TRUE AND t.paid = FALSE ");
						break;
					}
				}
			}
			query.append("Order By p.receiptNo DESC ");
			Query q = em.createQuery(query.toString());
			q.setMaxResults(max);
			if (criteria.getReceiptNoCriteria() != null) {
				switch (criteria.getReceiptNoCriteria()) {
					case RECEIPTNO: {
						q.setParameter("receiptNo", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
				}
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelMonthlyIncomeReport> findPersonTravelMonthlyIncome(PersonTravelCriteria criteria) throws DAOException {
		List<PersonTravelMonthlyIncomeReport> result = new ArrayList<PersonTravelMonthlyIncomeReport>();
		try {
			StringBuffer query = new StringBuffer("SELECT p FROM PersonTravelMonthlyIncomeReport p ");
			query.append(" WHERE p.id IS NOT NULL AND p.paymentDate>=:startDate AND p.paymentDate<=:endDate");
			if (criteria.getProductId() != null) {
				query.append(" AND p.productId=:productId");
			}
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				query.append(" AND p.entityId=:entityId");
			} else if (criteria.getBranch() != null) {
				query.append(" AND p.branchId=:branchId");
			}

			if (criteria.getSalePoint() != null) {
				query.append(" AND p.salePointId=:salePointId");
			}

			if (criteria.getAgentId() != null) {
				query.append(" AND p.agentId=:agentId");
			}
			if (criteria.getVehicleNo() != null && !criteria.getVehicleNo().isEmpty()) {
				query.append(" AND p.vehicleNo LIKE :vehicleNo");
			}
			if (criteria.getProposalNo() != null && !criteria.getProposalNo().isEmpty()) {
				query.append(" AND p.proposalNo LIKE :proposalNo");
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				query.append(" AND p.policyNo LIKE :policyNo");
			}
			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
			q.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));
			if (criteria.getAgentId() != null) {
				q.setParameter("agentId", criteria.getAgentId());
			}
			if (criteria.getProductId() != null) {
				q.setParameter("productId", criteria.getProductId());
			}
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				q.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}

			if (criteria.getVehicleNo() != null && !criteria.getVehicleNo().isEmpty()) {
				q.setParameter("vehicleNo", "%" + criteria.getVehicleNo() + "%");
			}
			if (criteria.getProposalNo() != null && !criteria.getProposalNo().isEmpty()) {
				q.setParameter("proposalNo", "%" + criteria.getProposalNo() + "%");
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				q.setParameter("policyNo", "%" + criteria.getPolicyNo() + "%");
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravel Monthly Income Report By Criteria", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePersonTravelPolicy(PersonTravelPolicy policy) throws DAOException {
		try {
			PersonTravelPolicy travelPolicy = em.merge(policy);
			em.remove(travelPolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifePolicy", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelPolicy> findPaymentOrderByReceiptNo(String receiptNo, String bpmsReceiptNo) throws DAOException {
		List<PersonTravelPolicy> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT pt FROM PersonTravelPolicy pt, Payment p, TLF t WHERE pt.id = p.referenceNo ");
			query.append("AND pt.id = t.referenceNo AND p.isPO = TRUE AND t.clearing = TRUE AND t.paid = FALSE ");
			if (!StringUtils.isBlank(receiptNo)) {
				query.append("AND t.tlfNo = :receiptNo ");
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				query.append("AND t.tlfNo = :receiptNo ");
			}
			Query q = em.createQuery(query.toString());
			if (!StringUtils.isBlank(receiptNo)) {
				q.setParameter("receiptNo", receiptNo);
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				q.setParameter("bpmsReceiptNo", bpmsReceiptNo);
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelPolicy PaymentOrder", pe);
		}
		return result;
	}
}
