package org.ace.insurance.life.snakebite.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicyDTO;
import org.ace.insurance.life.snakebite.SnakeBitePolicyNoCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicyNoCriteriaItems;
import org.ace.insurance.life.snakebite.SnakeBitePolicySearch;
import org.ace.insurance.life.snakebite.persistence.interfaces.ISnakeBitePolicyDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("SnakeBitePolicyDAO")
public class SnakeBitePolicyDAO extends BasicDAO implements ISnakeBitePolicyDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy insert(SnakeBitePolicy snakeBitePolicy) throws DAOException {
		try {
			em.persist(snakeBitePolicy);
			insertProcessLog(TableName.SNAKEBITEPOLICY, snakeBitePolicy.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert SnakeBitePolicy", pe);
		}
		return snakeBitePolicy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy findSnakeBitePolicyByPolicyNo(String snakeBitePolicyNo) throws DAOException {
		SnakeBitePolicy result = null;
		try {
			Query q = em.createNamedQuery("SnakeBitePolicy.findBySnakeBitePolicyNo");
			q.setParameter("snakeBitePolicyNo", snakeBitePolicyNo);
			result = (SnakeBitePolicy) q.getSingleResult();
			em.flush();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Snake Bite Policy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy update(SnakeBitePolicy snakeBitePolicy) throws DAOException {
		try {
			em.merge(snakeBitePolicy);
			updateProcessLog(TableName.SNAKEBITEPOLICY, snakeBitePolicy.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SnakeBitePolicy", pe);
		}
		return snakeBitePolicy;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(SnakeBitePolicy snakeBitePolicy) throws DAOException {
		try {
			snakeBitePolicy = em.merge(snakeBitePolicy);
			em.remove(snakeBitePolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SnakeBitePolicy", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicy findById(String id) throws DAOException {
		SnakeBitePolicy result = null;
		try {
			result = em.find(SnakeBitePolicy.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find SnakeBitePolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findAll() throws DAOException {
		List<SnakeBitePolicy> result = null;
		try {
			Query q = em.createNamedQuery("SnakeBitePolicy.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SnakeBitePolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findByIdList(List<String> proposalIdList) throws DAOException {
		List<SnakeBitePolicy> result = new ArrayList<SnakeBitePolicy>();
		try {
			Query query = em.createQuery("SELECT l FROM SnakeBitePolicy l WHERE l.id IN :ids");
			query.setParameter("ids", proposalIdList);
			result = query.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find SnakeBitePolicy by Date: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("SnakeBitePolicy.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findSnakeBitePolicyByCriteria(SnakeBitePolicyCriteria snakeBitePolicyCriteria) throws DAOException {
		List<SnakeBitePolicy> result = new ArrayList<SnakeBitePolicy>();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT l FROM SnakeBitePolicy l WHERE l.id IS NOT NULL  AND l.referenceNo IS NULL");

			if (snakeBitePolicyCriteria.getAgent() != null) {
				sb.append(" AND l.agent.id = :agentId");

			} else if (snakeBitePolicyCriteria.getSaleMan() != null) {
				sb.append(" AND l.saleMan.id = :saleManId");

			} else if (snakeBitePolicyCriteria.getRefferal() != null) {
				sb.append(" AND l.referral.id = :referralId");
			}

			if (snakeBitePolicyCriteria.getBookNo() != null && !snakeBitePolicyCriteria.getBookNo().isEmpty()) {
				sb.append(" AND l.bookNo = :bookNo");
			}
			Query q = em.createQuery(sb.toString());

			if (snakeBitePolicyCriteria.getAgent() != null) {
				q.setParameter("agentId", snakeBitePolicyCriteria.getAgent());
			} else if (snakeBitePolicyCriteria.getSaleMan() != null) {
				q.setParameter("saleManId", snakeBitePolicyCriteria.getSaleMan());
			} else if (snakeBitePolicyCriteria.getRefferal() != null) {
				q.setParameter("referralId", snakeBitePolicyCriteria.getRefferal());
			}

			if (snakeBitePolicyCriteria.getBookNo() != null && !snakeBitePolicyCriteria.getBookNo().isEmpty()) {
				q.setParameter("bookNo", snakeBitePolicyCriteria.getBookNo());
			}

			result = q.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find SnakeBitePolicy by Criteria: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findSnakeBitePolicyByReceiptNo(String receiptNo) throws DAOException {
		List<SnakeBitePolicy> result = new ArrayList<SnakeBitePolicy>();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT l FROM SnakeBitePolicy l WHERE l.id IS NOT NULL ");

			if (receiptNo != null && !receiptNo.isEmpty()) {
				sb.append(" AND l.referenceNo = :receiptNo");

			}
			Query q = em.createQuery(sb.toString());

			if (receiptNo != null && !receiptNo.isEmpty()) {
				q.setParameter("receiptNo", receiptNo);
			}

			result = q.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find SnakeBitePolicyList by receiptNo: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SnakeBitePolicyDTO findSnakeBitePolicyForPaymentDashboard(String referenceNo) throws DAOException {
		SnakeBitePolicyDTO dto = new SnakeBitePolicyDTO();
		try {
			Query q = em.createQuery(
					"SELECT sum(l.premium), sum(l.sumInsured) FROM SnakeBitePolicy l WHERE l.id IS NOT NULL AND l.referenceNo = :referenceNo AND l.complete = False GROUP BY l.referenceNo");
			q.setParameter("referenceNo", referenceNo);
			Object[] a = (Object[]) q.getSingleResult();
			Double premium = (Double) a[0];
			Double sumInsured = (Double) a[1];
			List<SnakeBitePolicy> policyList = findSnakeBitePolicyByReceiptNo(referenceNo);
			dto = new SnakeBitePolicyDTO(referenceNo, policyList.get(0).getBookNo(), premium, sumInsured, policyList.get(0).getBranch(), policyList.get(0).getAgent(),
					policyList.get(0).getReferral(), policyList.get(0).getSaleMan(), policyList.get(0).getProduct());
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find SnakeBitePolicy for Payment Dashboard: ", pe);
		}
		return dto;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicy> findSnakeBitePolicyListByEmptyReceiptNo() throws DAOException {
		List<SnakeBitePolicy> result = new ArrayList<SnakeBitePolicy>();
		try {
			Query q = em.createQuery("SELECT l FROM SnakeBitePolicy l WHERE l.id IS NOT NULL AND l.referenceNo IS NULL");
			result = q.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find SnakeBitePolicyList by empty receiptNo: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> findBookNo() throws DAOException {
		List<String> result = new ArrayList<String>();
		try {
			Query q = em.createQuery("SELECT DISTINCT l.bookNo FROM SnakeBitePolicy l WHERE l.id IS NOT NULL AND l.referenceNo IS NULL");
			result = q.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find BookNo by empty receiptNo: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBitePolicySearch> findSnakeBitePolicyNoByCriteria(SnakeBitePolicyNoCriteria criteria) throws DAOException {
		SnakeBitePolicySearch policySearch = null;
		List<SnakeBitePolicySearch> ret = null;

		try {
			StringBuffer query = new StringBuffer();
			query.append("Select s.bookNo, s.snakeBitePolicyNo, s.customer, s.branch From SnakeBitePolicy s WHERE s.bookNo IS NOT NULL");
			if (criteria != null && criteria.getCriteriaValue() != null) {
				if (criteria.getSnakeBitePolicyNoCriteriaItems() == SnakeBitePolicyNoCriteriaItems.BOOK_NO) {
					query.append(" AND s.bookNo like :bookNo");
				}
				if (criteria.getSnakeBitePolicyNoCriteriaItems() == SnakeBitePolicyNoCriteriaItems.POLICY_NO) {
					query.append(" AND l.snakeBitePolicyNo like :snakeBitePolicyNo");
				}
			}

			Query q = em.createQuery(query.toString());
			if (criteria != null && criteria.getCriteriaValue() != null) {
				if (criteria.getSnakeBitePolicyNoCriteriaItems() == SnakeBitePolicyNoCriteriaItems.BOOK_NO) {
					q.setParameter("bookNo", "%" + criteria.getCriteriaValue() + "%");
				}
				if (criteria.getSnakeBitePolicyNoCriteriaItems() == SnakeBitePolicyNoCriteriaItems.POLICY_NO) {
					q.setParameter("snakeBitePolicyNo", "%" + criteria.getCriteriaValue() + "%");
				}
			}
			q.setMaxResults(30);
			List<Object> objects = q.getResultList();
			if (objects == null) {
				ret = Collections.EMPTY_LIST;
			} else {
				ret = new ArrayList<SnakeBitePolicySearch>();
				for (Object object : objects) {
					policySearch = new SnakeBitePolicySearch(object);
					ret.add(policySearch);
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Snake Bite Policy No. By Criteria", pe);
		}

		return ret;
	}

}