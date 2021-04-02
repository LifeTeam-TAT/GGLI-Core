/**
 * 
 */
package org.ace.insurance.coinsurance.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.coinsurance.CoinsuranceAttachment;
import org.ace.insurance.coinsurance.CoinsuranceToOtherCompanyCriteria;
import org.ace.insurance.coinsurance.persistence.interfaces.ICoinsuranceDAO;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PolicyUtils;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This serves as the DAO implementation of the {@link ICoinsuranceDAO} to
 * manipulate the Co-insurance objects.
 * 
 * @author ACN
 * @version 1.0.0
 * @Date 2013/05/07
 */
@Repository("CoinsuranceDAO")
public class CoinsuranceDAO extends BasicDAO implements ICoinsuranceDAO {
	/**
	 * @see org.ace.insurance.coinsurance.persistence.interfaces.ICoinsuranceDAO#insert(org.ace.insurance.coinsurance.Coinsurance,
	 *      org.ace.insurance.common.interfaces.IPolicy)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance insert(Coinsurance coinsurance, IPolicy policy) throws DAOException {
		try {
			em.persist(coinsurance);
			insertProcessLog(TableName.COINSURANCE, coinsurance.getId());
			em.flush();

			if (policy != null) {
				processPolicy(policy);
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Co-insurance (ID : " + coinsurance.getId() + ")", pe);
		}
		return coinsurance;
	}

	/**
	 * @see org.ace.insurance.coinsurance.persistence.interfaces.ICoinsuranceDAO#update(org.ace.insurance.coinsurance.Coinsurance)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Coinsurance coinsurance) throws DAOException {
		try {
			em.merge(coinsurance);
			updateProcessLog(TableName.COINSURANCE, coinsurance.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Co-insurance (ID : " + coinsurance.getId() + ")", pe);
		}
	}

	/**
	 * @see org.ace.insurance.coinsurance.persistence.interfaces.ICoinsuranceDAO#delete(org.ace.insurance.coinsurance.Coinsurance)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Coinsurance coinsurance) throws DAOException {
		String id = coinsurance.getId();
		try {
			coinsurance = em.merge(coinsurance);
			em.remove(coinsurance);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Co-insurance (ID : " + id + ")", pe);
		}
	}

	/**
	 * @see org.ace.insurance.coinsurance.persistence.interfaces.ICoinsuranceDAO#findById(java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance findById(String id) throws DAOException {
		Coinsurance ret = null;
		try {
			ret = em.find(Coinsurance.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Co-insurance (ID : " + id + ")", pe);
		}
		return ret;
	}

	/**
	 * @see org.ace.insurance.coinsurance.persistence.interfaces.ICoinsuranceDAO#findAll()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findAll() throws DAOException {
		List<Coinsurance> ret = null;
		try {
			Query q = em.createNamedQuery("Coinsurance.findAll");
			ret = q.getResultList();
			em.flush();
			if (ret == null) {
				ret = Collections.EMPTY_LIST;
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Co-insurances", pe);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findByPolicyNO(String policyNo) throws DAOException {
		List<Coinsurance> ret = null;
		try {
			Query q = em.createNamedQuery("Coinsurance.findByPolicyNo");
			q.setParameter("policyNo", policyNo);
			ret = q.getResultList();
			em.flush();
			if (ret == null) {
				ret = Collections.EMPTY_LIST;
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Co-insurances By Policy For Invoice...", pe);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findByCargoPolicyNo(String policyNo) throws DAOException {
		List<Coinsurance> ret = null;
		try {
			Query q = em.createNamedQuery("Coinsurance.findByCargoPolicyNo");
			q.setParameter("policyNo", policyNo);
			ret = q.getResultList();
			em.flush();
			if (ret == null) {
				ret = Collections.EMPTY_LIST;
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Co-insurances By Policy For Invoice...", pe);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance findByPolicyByOutPolicyNo(String policyNo) throws DAOException {
		Coinsurance ret = null;
		try {
			Query q = em.createNamedQuery("Coinsurance.findByPolicyByOutPolicyNo");
			q.setParameter("policyNo", policyNo);
			ret = (Coinsurance) q.getSingleResult();
			em.flush();

		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Co-insurances By Policy For Invoice...", pe);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Coinsurance findINPolicyNo(String policyNo) throws DAOException {
		Coinsurance ret = null;
		try {
			Query q = em.createNamedQuery("Coinsurance.findINPolicyNo");
			q.setParameter("policyNo", policyNo);
			ret = (Coinsurance) q.getSingleResult();
			em.flush();

		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Co-insurances By Policy For Invoice...", pe);
		}
		return ret;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addAttachment(Coinsurance coinsurance) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM CoinsuranceAttachment a WHERE a.coinsurance.id = :coinsuranceId");
			delQuery.setParameter("coinsuranceId", coinsurance.getId());
			delQuery.executeUpdate();

			for (CoinsuranceAttachment att : coinsurance.getCoinsuranceAttachmentList()) {
				if (att.getId() == null) {
					em.persist(att);
				}
			}
			updateProcessLog(TableName.COINSURANCE, coinsurance.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Attachment", pe);
		}
	}

	// -------------------- private and utility methods --------------------

	private void processPolicy(IPolicy policy) {
		String policyObjectName = null;
		InsuranceType type = PolicyUtils.classifyPolicy(policy);
		switch (type) {

			case LIFE:
				policyObjectName = "LifePolicy";
				break;

		}

		String query = prepareQueryString(policyObjectName);
		Query q = em.createQuery(query);
		q.setParameter("id", policy.getId());
		q.executeUpdate();
		em.flush();
	}

	private String prepareQueryString(String policyObjectName) {
		return "UPDATE " + policyObjectName + " p SET p.isCoinsuranceApplied = TRUE WHERE p.id = :id";
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findFireCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) throws DAOException {
		List<Coinsurance> result = new ArrayList<Coinsurance>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT c FROM Coinsurance c, FirePolicy f WHERE c.policyNo = f.policyNo AND c.invoiceNo IS NULL ");

			if (criteria.getAgent() != null) {
				queryString.append(" AND f.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND f.commenmanceDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND f.commenmanceDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND f.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND f.organization.id = :organizationId");
			}
			if (criteria.getSaleMan() != null) {
				queryString.append(" AND f.saleMan.id = :saleManId");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND f.branch.id = :branchId");
			}
			if (!criteria.getPolicyNo().isEmpty()) {
				queryString.append(" AND f.policyNo = :policyNo");
			}
			if (criteria.getCoinsuranceCompany() != null) {
				queryString.append(" AND c.coinsuranceCompany.id = :coinsuranceCompanyId");
			}
			if (criteria.getBank() != null) {
				queryString.append(" AND f.bankCustomer.id = :bankCustomer");
			}
			queryString.append(" order by c.coinsuranceCompany.id");

			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getSaleMan() != null) {
				query.setParameter("saleManId", criteria.getSaleMan().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (!criteria.getPolicyNo().isEmpty()) {
				query.setParameter("policyNo", criteria.getPolicyNo());
			}
			if (criteria.getCoinsuranceCompany() != null) {
				query.setParameter("coinsuranceCompanyId", criteria.getCoinsuranceCompany().getId());
			}
			if (criteria.getBank() != null) {
				query.setParameter("bankCustomer", criteria.getBank().getId());
			}
			result = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed findFireCoinsuranceByCriteria : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findCargoCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) throws DAOException {
		List<Coinsurance> result = new ArrayList<Coinsurance>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT c FROM Coinsurance c, CargoPolicy f WHERE c.policyNo = f.policyNo AND c.invoiceNo IS NULL ");

			if (criteria.getAgent() != null) {
				queryString.append(" AND f.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND f.commenmanceDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND f.commenmanceDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND f.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND f.organization.id = :organizationId");
			}
			if (criteria.getSaleMan() != null) {
				queryString.append(" AND f.saleMan.id = :saleManId");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND f.branch.id = :branchId");
			}
			if (!criteria.getPolicyNo().isEmpty()) {
				queryString.append(" AND f.policyNo = :policyNo");
			}
			if (criteria.getCoinsuranceCompany() != null) {
				queryString.append(" AND c.coinsuranceCompany.id = :coinsuranceCompanyId");
			}
			if (criteria.getBank() != null) {
				queryString.append(" AND f.bankCustomer.id = :bankCustomer");
			}
			queryString.append(" order by c.coinsuranceCompany.id");

			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getSaleMan() != null) {
				query.setParameter("saleManId", criteria.getSaleMan().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (!criteria.getPolicyNo().isEmpty()) {
				query.setParameter("policyNo", criteria.getPolicyNo());
			}
			if (criteria.getCoinsuranceCompany() != null) {
				query.setParameter("coinsuranceCompanyId", criteria.getCoinsuranceCompany().getId());
			}
			if (criteria.getBank() != null) {
				query.setParameter("bankCustomer", criteria.getBank().getId());
			}
			result = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed findFireCoinsuranceByCriteria : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coinsurance> findMotorCoinsuranceByCriteria(CoinsuranceToOtherCompanyCriteria criteria) throws DAOException {
		List<Coinsurance> result = new ArrayList<Coinsurance>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT c FROM Coinsurance c, MotorPolicy m WHERE c.policyNo = m.policyNo AND c.invoiceNo IS NULL ");

			if (criteria.getAgent() != null) {
				queryString.append(" AND m.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND m.commenmanceDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND m.commenmanceDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND m.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND m.organization.id = :organizationId");
			}
			if (criteria.getSaleMan() != null) {
				queryString.append(" AND m.saleMan.id = :saleManId");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND m.branch.id = :branchId");
			}
			if (!criteria.getPolicyNo().isEmpty()) {
				queryString.append(" AND m.policyNo = :policyNo");
			}
			if (criteria.getCoinsuranceCompany() != null) {
				queryString.append(" AND c.coinsuranceCompany.id = :coinsuranceCompanyId");
			}

			queryString.append(" order by c.coinsuranceCompany.id");

			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getSaleMan() != null) {
				query.setParameter("saleManId", criteria.getSaleMan().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (!criteria.getPolicyNo().isEmpty()) {
				query.setParameter("policyNo", criteria.getPolicyNo());
			}
			if (criteria.getCoinsuranceCompany() != null) {
				query.setParameter("coinsuranceCompanyId", criteria.getCoinsuranceCompany().getId());
			}
			result = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed findMotorCoinsuranceByCriteria : ", pe);
		}
		return result;
	}
}
