package org.ace.insurance.life.policy.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.coinsurance.OutCoinsuranceCriteria;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.LifePolicyCriteriaItems;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.NotificationCriteria;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyCriteriaItems;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.LifeEndowmentPolicySearch;
import org.ace.insurance.life.claim.ClaimStatus;
import org.ace.insurance.life.claim.LifePolicyCriteria;
import org.ace.insurance.life.claim.LifePolicySearch;
import org.ace.insurance.life.policy.EndorsementLifePolicyPrint;
import org.ace.insurance.life.policy.LPC001;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.persistence.interfaces.ILifePolicyDAO;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.persistence.interfaces.ILifePolicyHistoryDAO;
import org.ace.insurance.life.proposal.LPL002;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionDTO;
import org.ace.insurance.web.manage.life.billcollection.PolicyNotificationDTO;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifePolicyDAO")
public class LifePolicyDAO extends BasicDAO implements ILifePolicyDAO {

	@Resource(name = "LifePolicyHistoryDAO")
	private ILifePolicyHistoryDAO lifePolicyHistoryDAO;

	@Resource(name = "UserProcessService")
	private IUserProcessService userProcessService;

	@Resource(name = "IDConfigLoader")
	private IDConfigLoader idConfigLoader;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(LifePolicy lifePolicy) throws DAOException {
		try {
			em.persist(lifePolicy);
			insertProcessLog(TableName.LIFEPOLICY, lifePolicy.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to insert LifePolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(LifePolicy lifePolicy) throws DAOException {
		try {
			/*
			 * for (PolicyInsuredPerson insuredPerson :
			 * lifePolicy.getPolicyInsuredPersonList()) {
			 * em.merge(insuredPerson); }
			 */
			em.merge(lifePolicy);
			updateProcessLog(TableName.LIFEPOLICY, lifePolicy.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to update LifePolicy", pe);
		}

	}

	/*
	 * @Transactional(propagation = Propagation.REQUIRED) public void
	 * updateActivePolicyEndDate(Date activePolicyEndDate, String policyId)
	 * throws DAOException { try{ logger.debug(
	 * "update active policy end date method has been started."); String query =
	 * "Update LifePolicy Set activedPolicyEndDate = :endDate Where id = :policyId"
	 * ; Query q = em.createQuery(query); q.setParameter("endDate",
	 * activePolicyEndDate); q.setParameter("policyId", policyId);
	 * q.executeUpdate(); updateProcessLog(TableName.LIFEPOLICY, policyId);
	 * em.flush(); logger.debug(
	 * "update active policy end date method has been successfully completed.");
	 * }catch(PersistenceException pe){ logger.error(
	 * "update active policy end date method has been failed.", pe); throw
	 * translate("failed to update LifePolicy", pe); }
	 * 
	 * }
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(LifePolicy lifePolicy) throws DAOException {
		try {
			lifePolicy = em.merge(lifePolicy);
			em.remove(lifePolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifePolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicy findById(String id) throws DAOException {
		LifePolicy result = null;
		try {
			result = em.find(LifePolicy.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findByProposalId(String proposalId) throws DAOException {
		List<LifePolicy> result = null;
		LifePolicyHistory history = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findByProposalId");
			q.setParameter("lifeProposalId", proposalId);
			result = q.getResultList();
			em.flush();

			/**
			 * If it's Policy have been finished Endorse and Renewal Case,
			 * Cann't find Policy Table. So find in History Table.
			 */
			if (result.isEmpty()) {
				history = lifePolicyHistoryDAO.findByProposalId(proposalId);
				if (history != null) {
					result.add(new LifePolicy(history));
				}
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifePolicy by ProposalID : " + proposalId, pe);
		}
		return result;
	}

	@Override
	public List<LifePolicy> findByPolicyId(String policyId) throws DAOException {
		List<LifePolicy> result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findByPolicyId");
			q.setParameter("lifePolicyId", policyId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifePolicy by Policy ID : " + policyId, pe);
		}
		return result;
	}

	@Override
	public List<LifePolicy> findByReceiptNo(String receiptNo) throws DAOException {
		List<LifePolicy> result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findByReceiptNo");
			q.setParameter("receiptNo", receiptNo);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifePolicy by ReceiptNo : " + receiptNo, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findAll() throws DAOException {
		List<LifePolicy> result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifePolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findAllActiveLifePolicy() throws DAOException {
		List<LifePolicy> result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findAllActiveLifePolicy");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ActiveLifePolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findByDate(Date startDate, Date endDate) throws DAOException {
		List<LifePolicy> result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findByDate");
			q.setParameter("startDate", startDate);
			q.setParameter("endDate", endDate);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy by Date: ", pe);
		}
		return result;
	}

	@Override
	public void increasePrintCount(String id) throws DAOException {
		try {
			Query q = em.createNamedQuery("LifePolicy.increasePrintCount");
			q.setParameter("id", id);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to increase print count : ", pe);
		}
	}

	@Override
	public void updateCommenmanceDate(Date date, String id) throws DAOException {
		try {
			Query q = em.createNamedQuery("LifePolicy.updateCommenmanceDate");
			q.setParameter("commenceDate", date);
			q.setParameter("id", id);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update commenmance date : ", pe);
		}

	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LPL002> findByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {
		List<LPL002> result = new ArrayList<LPL002>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT NEW " + LPL002.class.getName());
			queryString.append("(l.id, l.policyNo, l.lifeProposal.proposalNo, CONCAT(TRIM(s.name.firstName),' ',TRIM(s.name.middleName),' ',TRIM(s.name.lastName))");
			queryString.append(", CONCAT(a.initialId,' ',TRIM(a.name.firstName),' ',TRIM(a.name.middleName),' ',TRIM(a.name.lastName)), ");
			queryString.append(
					"case when  o.name is null then CONCAT(c.initialId,' ',TRIM(c.name.firstName),' ',TRIM(c.name.middleName),' ',TRIM(c.name.lastName)) else o.name end, ");
			queryString.append(
					"l.branch.name, sum(COALESCE(pi.premium,0.0)), sum(COALESCE(pi.sumInsured,0.0)), l.paymentType, l.commenmanceDate, sum(COALESCE(pi.basicTermPremium,0.0)), COALESCE(pi.periodMonth,0)) ");
			queryString.append("FROM LifePolicy l INNER JOIN l.policyInsuredPersonList pi LEFT OUTER JOIN l.customer c LEFT OUTER JOIN l.organization o ");
			queryString.append("LEFT OUTER JOIN l.saleMan s LEFT OUTER JOIN l.agent a WHERE l.policyNo IS NOT NULL");
			if (criteria.getInsuranceType() != null) {
				queryString.append(" AND pi.product.insuranceType = :insuranceType");
			}
			if (criteria.getAgent() != null) {
				queryString.append(" AND l.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND cast(l.commenmanceDate as date) >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND cast(l.commenmanceDate as date) <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND l.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND l.organization.id = :organizationId");
			}
			if (criteria.getSaleMan() != null) {
				queryString.append(" AND l.saleMan.id = :saleManId");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND l.branch.id = :branchId");
			}
			if (criteria.getProduct() != null) {
				queryString.append(" AND pi.product.id = :productId");
			}
			if (!criteria.getNumber().isEmpty()) {
				queryString.append(" AND l.policyNo = :policyNo");
			}
			queryString.append(
					" group by l.id, l.policyNo, l.lifeProposal.proposalNo, s.name,a.initialId,a.name,c.initialId,c.name, o.name,l.branch.name,l.paymentType, l.commenmanceDate, pi.periodMonth ");
			Query query = em.createQuery(queryString.toString());
			if (criteria.getInsuranceType() != null) {
				query.setParameter("insuranceType", criteria.getInsuranceType());
			}
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
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			}
			if (!criteria.getNumber().isEmpty()) {
				query.setParameter("policyNo", criteria.getNumber());
			}
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy by EnquiryCriteria : ", pe);
		}
		return new ArrayList<LPL002>(result);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePaymentDate(String lifePolicyId, Date paymentDate, Date paymentValidDate) throws DAOException {
		try {
			Query q = em.createNamedQuery("LifePolicy.updatePaymentDate");
			q.setParameter("id", lifePolicyId);
			q.setParameter("paymentDate", paymentDate);
			q.setParameter("paymentValidDate", paymentValidDate);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update payment date : ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findByCustomer(Customer customer) throws DAOException {
		List<LifePolicy> result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findByCustomer");
			q.setParameter("customerId", customer.getId());
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy by Date: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateEndorsementStatus(boolean status, String policylId) throws DAOException {
		try {
			Query q = em.createNamedQuery("LifePolicy.updateEndorsementStatus");
			q.setParameter("isEndorsementApplied", status);
			q.setParameter("id", policylId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update endorsement status", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PolicyInsuredPerson findInsuredPersonByCodeNo(String codeNo) throws DAOException {
		PolicyInsuredPerson result = null;
		try {
			Query q = em.createQuery("SELECT i FROM PolicyInsuredPerson i WHERE i.insPersonCodeNo = :codeNo ORDER BY i.endDate desc");
			q.setParameter("codeNo", codeNo);
			q.setMaxResults(1);
			result = (PolicyInsuredPerson) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of find Insured Person By CodeNo : " + codeNo, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuredPersonStatusByCodeNo(String codeNo, EndorsementStatus status) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE PolicyInsuredPerson i SET i.endorsementStatus = :status WHERE i.insPersonCodeNo = :codeNo");
			q.setParameter("codeNo", codeNo);
			q.setParameter("status", status);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Insured Person Status By CodeNo : ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyStatusById(String id, PolicyStatus status) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE LifePolicy p SET p.policyStatus = :status WHERE p.id = :id");
			q.setParameter("id", id);
			q.setParameter("status", status);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Policy Status By Id : ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyAttachment(LifePolicy lifePolicy) throws DAOException {
		try {
			update(lifePolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to update MotorPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicy findLifePolicyByPolicyNo(String policyNo) throws DAOException {
		LifePolicy result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findByPolicyNo");
			q.setParameter("policyNo", policyNo);
			result = (LifePolicy) q.getSingleResult();
			em.flush();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBeneficiaryClaimStatusById(String id, ClaimStatus status) throws DAOException {
		try {
			Query q = em.createNamedQuery("PolicyInsuredPersonBeneficiaries.updateClaimStatus");
			q.setParameter("id", id);
			q.setParameter("claimStatus", status);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Claim Status By Id : ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuredPersonClaimStatusById(String id, ClaimStatus status) throws DAOException {
		try {
			Query q = em.createNamedQuery("PolicyInsuredPerson.updateClaimStatus");
			q.setParameter("id", id);
			q.setParameter("claimStatus", status);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Claim Status By Id : ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findLifeCoPolicyByCriteria(OutCoinsuranceCriteria criteria) throws DAOException {
		List<LifePolicy> result = new ArrayList<LifePolicy>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT DISTINCT l FROM LifePolicy l, Coinsurance c WHERE l.policyNo = c.policyNo AND c.invoiceNo IS NULL");

			if (criteria.getAgent() != null) {
				queryString.append(" AND l.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND l.commenmanceDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND l.commenmanceDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND l.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND l.organization.id = :organizationId");
			}
			if (criteria.getSaleMan() != null) {
				queryString.append(" AND l.saleMan.id = :saleManId");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND l.branch.id = :branchId");
			}

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
			result = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Coinsurance LifePolicy by Criteria : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicySearch> findLifePolicyForClaimByCriteria(LifePolicyCriteria criteria) throws DAOException {
		List<LifePolicySearch> result = null;

		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT NEW " + LifePolicySearch.class.getName());
			query.append("( l.policyNo, c.initialId, c.name, o.name, l.branch.name ) From LifePolicy l ");
			query.append(" LEFT OUTER JOIN l.customer c LEFT OUTER JOIN l.organization o ");
			query.append(" WHERE l.policyNo IS NOT NULL");
			if (criteria != null && criteria.getCriteriaValue() != null) {
				if (criteria.getLifePolicyCriteriaItems() == LifePolicyCriteriaItems.CUSTOMER_NAME) {
					query.append(" AND CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, ' ', l.customer.name.lastName) like :criteriaValue OR "
							+ "CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName,  l.customer.name.lastName) like :criteriaValue");
				}
				if (criteria.getLifePolicyCriteriaItems() == LifePolicyCriteriaItems.POLICY_NO) {
					query.append(" AND l.policyNo like :criteriaValue");
				}
				if (criteria.getLifePolicyCriteriaItems() == LifePolicyCriteriaItems.ORGANIZATION_NAME) {
					query.append(" AND l.organization.name like :criteriaValue");
				}
			}

			Query q = em.createQuery(query.toString());
			if (criteria != null && criteria.getCriteriaValue() != null) {
				q.setParameter("criteriaValue", "%" + criteria.getCriteriaValue() + "%");
			}

			q.setMaxResults(30);
			result = q.getResultList();

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve LifePolicy for Claim By Criteria", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicySearch> findActiveLifePolicy(LifePolicyCriteria criteria) throws DAOException {
		List<LifePolicySearch> result = null;

		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT NEW " + LifePolicySearch.class.getName());
			query.append("( l.policyNo, c.initialId, c.name, o.name, l.branch.name ) From LifePolicy l ");
			query.append(" LEFT OUTER JOIN l.customer c LEFT OUTER JOIN l.organization o ");
			query.append(
					" WHERE l.policyNo IS NOT NULL AND (l.policyStatus = org.ace.insurance.common.PolicyStatus.UPDATE  OR l.policyStatus = org.ace.insurance.common.PolicyStatus.LOAN OR l.policyStatus = org.ace.insurance.common.PolicyStatus.INFORCE OR l.policyStatus  is NULL )");
			if (criteria != null && criteria.getCriteriaValue() != null) {
				if (criteria.getLifePolicyCriteriaItems() == LifePolicyCriteriaItems.CUSTOMER_NAME) {
					query.append(" AND CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, ' ', l.customer.name.lastName) like :criteriaValue OR "
							+ "CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName,  l.customer.name.lastName) like :criteriaValue");
				}
				if (criteria.getLifePolicyCriteriaItems() == LifePolicyCriteriaItems.POLICY_NO) {
					query.append(" AND l.policyNo like :criteriaValue");
				}
				if (criteria.getLifePolicyCriteriaItems() == LifePolicyCriteriaItems.ORGANIZATION_NAME) {
					query.append(" AND l.organization.name like :criteriaValue");
				}
			}

			Query q = em.createQuery(query.toString());
			if (criteria != null && criteria.getCriteriaValue() != null) {
				q.setParameter("criteriaValue", "%" + criteria.getCriteriaValue() + "%");
			}
			q.setMaxResults(10);
			result = q.getResultList();

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve LifePolicy for Claim By Criteria", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LPC001> findRenewalGroupLifePolicyByPolicyCriteria(PolicyCriteria criteria, int max) throws DAOException {
		List<Object[]> objectList = new ArrayList<Object[]>();
		Map<String, LPC001> resultMap = new HashMap<String, LPC001>();
		User loginUser = userProcessService.getLoginUser();
		boolean accessAllBranch = loginUser.isAccessAllBranch();
		String branchId = idConfigLoader.getBranchId();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT l.id, l.policyNo, s.initialId, s.name, a.initialId, a.name, c.initialId, c.name, o.name, b.name, pi.premium, pi.sumInsured, ");
			query.append(" t.name FROM LifePolicy l INNER JOIN l.policyInsuredPersonList pi LEFT OUTER JOIN pi.product p ");
			query.append(" LEFT OUTER JOIN l.saleMan s LEFT OUTER JOIN l.agent a LEFT OUTER JOIN l.customer c LEFT OUTER JOIN ");
			query.append(" l.organization o LEFT OUTER JOIN l.branch b LEFT OUTER JOIN l.paymentType t ");
			query.append(" WHERE p.id = :productId AND ");
			query.append(" (l.isEndorsementApplied = FALSE OR l.isEndorsementApplied IS NULL) ");
			if (!accessAllBranch) {
				query.append(" AND b.id = :branchId");
			}

			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case POLICYNO: {
						query.append(" AND l.policyNo like :value");
						break;
					}
					case CUSTOMERNAME: {
						query.append(
								" AND FUNCTION( 'REPLACE', CONCAT(CONCAT(c.initialId, ''), CONCAT(c.name.firstName,''), CONCAT(c.name.middleName, ''), CONCAT(c.name.lastName , '') ), ' ', '')");
						query.append(" LIKE :value ");
						break;
					}
					case ORGANIZATIONNAME: {
						query.append(" AND o.name like :value ");
						break;
					}
				}
			}

			Query q = em.createQuery(query.toString());
			if (!accessAllBranch)
				q.setParameter("branchId", branchId);

			q.setParameter("productId", KeyFactorChecker.getGroupLifeID());
			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case ORGANIZATIONNAME:
					case POLICYNO: {
						q.setParameter("value", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
					case CUSTOMERNAME: {
						q.setParameter("value", "%" + StringUtils.replace(criteria.getCriteriaValue(), " ", "") + "%");
						break;
					}
				}
			}
			objectList = q.getResultList();
			em.flush();
			String id;
			String policyNo;
			String saleManInitialId;
			Name saleManName;
			String saleMan = null;
			String agentInitialId;
			Name agentName;
			String agent = null;
			String customerInitialId;
			Name customerName;
			String customer;
			String organization;
			String branch;
			double premium;
			double sumInsured;
			String paymenttype;

			for (Object[] b : objectList) {
				id = (String) b[0];
				policyNo = (String) b[1];
				saleManInitialId = (String) b[2];
				saleManName = (Name) b[3];
				agentInitialId = (String) b[4];
				agentName = (Name) b[5];
				customerInitialId = (String) b[6];
				customerName = (Name) b[7];
				organization = (String) b[8];
				branch = (String) b[9];
				premium = (Double) b[10];
				sumInsured = (Double) b[11];
				paymenttype = (String) b[12];
				if (saleManName != null) {
					saleMan = saleManInitialId + saleManName.getFullName();
				}
				if (agentName != null) {
					agent = agentInitialId + agentName.getFullName();
				}
				if (customerName != null) {
					customer = customerInitialId + customerName.getFullName();
				} else {
					customer = organization;
				}

				if (resultMap.containsKey(policyNo)) {
					premium += resultMap.get(policyNo).getPremium();
					sumInsured += resultMap.get(policyNo).getSumInsured();
					resultMap.put(policyNo, new LPC001(id, policyNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, true));
				} else {
					resultMap.put(policyNo, new LPC001(id, policyNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, false));
				}
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}

		RegNoSorter<LPC001> sorter = new RegNoSorter<LPC001>(new ArrayList<LPC001>(resultMap.values()));
		return sorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findByPageCriteria(PolicyCriteria criteria) throws DAOException {
		List<LifePolicy> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT l FROM LifePolicy l INNER JOIN l.policyInsuredPersonList p, Payment pay "
					+ " WHERE p.endDate > l.activedPolicyEndDate AND pay.referenceNo = l.id AND pay.referenceType IN :referenceTypeList AND pay.complete = 1");
			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case POLICYNO:
						query.append(" AND l.policyNo LIKE :policyNo ");
						break;
					case CUSTOMERNAME:
						query.append(
								" AND CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, ' ', l.customer.name.lastName) LIKE :name OR CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, l.customer.name.lastName) like :name ");
						break;
					case ORGANIZATIONNAME:
						query.append(" AND l.organization.name LIKE :name ");
						break;

					default:
						break;
				}
			}

			if (criteria.getFromDate() != null) {
				query.append(" AND l.activedPolicyEndDate >= :fromDate ");
			}

			if (criteria.getToDate() != null) {
				query.append(" AND l.activedPolicyEndDate <= :toDate ");
			}

			query.append(" AND pay.referenceNo NOT IN ( SELECT p.referenceNo FROM Payment p WHERE p.complete = '0' )");
			query.append(" ORDER BY l.policyNo ASC ");
			Query q = em.createQuery(query.toString());
			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case POLICYNO:
						q.setParameter("policyNo", "%" + criteria.getCriteriaValue() + "%");
						break;
					case ORGANIZATIONNAME:
						q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
						break;
					case CUSTOMERNAME:
						q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
						break;
				}
			}

			if (criteria.getFromDate() != null) {
				q.setParameter("fromDate", Utils.resetStartDate(criteria.getFromDate()));
			}

			if (criteria.getToDate() != null) {
				q.setParameter("toDate", Utils.resetEndDate(criteria.getToDate()));
			}

			q.setParameter("referenceTypeList", Arrays.asList(PolicyReferenceType.LIFE_BILL_COLLECTION, PolicyReferenceType.LIFE_POLICY));
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BillCollectionDTO> findBillCollectionByCriteria(PolicyCriteria criteria) throws DAOException {
		List<BillCollectionDTO> result = new ArrayList<BillCollectionDTO>();
		try {
			StringBuffer query = new StringBuffer();

			query.append("SELECT l.id, l.policyNo, l.paymentType, l.activedPolicyStartDate, l.activedPolicyEndDate, ");
			query.append(" l.lastPaymentTerm, p.basicTermPremium , p.addOnTermPremium , ");
			query.append(" p.initialId, p.name, p.idNo, se.extraAmount, p.periodMonth");
			query.append(" FROM LifePolicy l LEFT JOIN l.policyInsuredPersonList p");
			query.append(" LEFT JOIN ShortEndowmentExtraValue se ON se.shortTermPolicyNo=l.policyNo AND se.isPaid = '0'");
			query.append(" LEFT JOIN LifePolicyHistory lph ON lph.policyNo = l.policyNo");
			query.append(" WHERE l.paymentEndDate > l.activedPolicyEndDate");

			if (criteria.getSalePointId() != null && !criteria.getSalePointId().isEmpty()) {
				query.append(" AND l.salePoint.id = :salePointId ");
			}
			if (criteria.getReferenceType() != null) {
				query.append(" AND p.product.id IN :productId ");
			}

			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case POLICYNO:
						query.append(" AND l.policyNo LIKE :criteriaValue ");
						break;
					case CUSTOMERNAME:
						query.append(
								" AND ( CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, ' ', l.customer.name.lastName) LIKE :criteriaValue OR CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, l.customer.name.lastName) like :criteriaValue )");
						break;
					case ORGANIZATIONNAME:
						query.append(" AND l.organization.name LIKE :criteriaValue ");
						break;
					case BANKCUSTOMER:
						query.append(" AND l.bank.name LIKE :criteriaValue ");
						break;
				}
			}
			if (criteria.getFromDate() != null) {
				query.append(" AND l.activedPolicyEndDate >= :fromDate ");
			}
			if (criteria.getToDate() != null) {
				query.append(" AND l.activedPolicyEndDate <= :toDate ");
			}

			query.append(" AND (l.id NOT IN ( SELECT p.referenceNo FROM Payment p WHERE p.complete = '0' and p.reverse = '0' )");
			query.append(" OR lph.policyReferenceNo  NOT IN ( SELECT p.referenceNo FROM Payment p WHERE p.complete = '0' and p.reverse = '0' ))");
			query.append(" AND (l.policyStatus IS NULL or l.policyStatus NOT IN :policyStatusList) AND (l.isMigrated IS Null or l.isMigrated = '0')");
			query.append(" ORDER BY l.policyNo ASC ");
			Query q = em.createQuery(query.toString());

			if (criteria.getReferenceType() != null) {
				q.setParameter("productId", ProductIDConfig.getIdByReferenceType(criteria.getReferenceType()));
			}

			if (criteria.getPolicyCriteria() != null) {
				q.setParameter("criteriaValue", "%" + criteria.getCriteriaValue() + "%");
			}
			if (criteria.getFromDate() != null) {
				q.setParameter("fromDate", Utils.resetStartDate(criteria.getFromDate()));
			}
			if (criteria.getToDate() != null) {
				q.setParameter("toDate", Utils.resetEndDate(criteria.getToDate()));
			}

			if (criteria.getSalePointId() != null && !criteria.getSalePointId().isEmpty()) {
				q.setParameter("salePointId", criteria.getSalePointId());
			}

			q.setParameter("policyStatusList", Arrays.asList(PolicyStatus.TERMINATE, PolicyStatus.PAIDUP, PolicyStatus.SURRENDER));
			List<Object[]> objectList = q.getResultList();
			String policyId = null;
			String policyNo = null;
			PaymentType paymentType = null;
			Date startDate = null;
			Date endDate = null;
			int lastPaymentTerm = 0;
			Double basicTermPremium = null;
			Double addOnTermPremium = null;
			String initialId = null;
			Name name = null;
			String idNo = null;
			int periodOfMonth = 0;
			Double extraAmount = null;
			for (Object[] arr : objectList) {
				policyId = (String) arr[0];
				policyNo = (String) arr[1];
				paymentType = (PaymentType) arr[2];
				startDate = (Date) arr[3];
				endDate = (Date) arr[4];
				lastPaymentTerm = (Integer) arr[5];
				lastPaymentTerm = lastPaymentTerm == 0 ? 1 : lastPaymentTerm;
				basicTermPremium = (Double) arr[6];
				addOnTermPremium = (Double) arr[7];
				basicTermPremium = basicTermPremium == null ? 0 : basicTermPremium;
				addOnTermPremium = addOnTermPremium == null ? 0 : addOnTermPremium;
				initialId = (String) arr[8];
				name = (Name) arr[9];
				idNo = (String) arr[10];
				extraAmount = (Double) arr[11];
				periodOfMonth = (int) arr[12];
				extraAmount = extraAmount == null ? 0 : extraAmount;
				int totalPaymentTime = 0;
				if (paymentType.getMonth() != 0) {
					totalPaymentTime = periodOfMonth / paymentType.getMonth();
				} else {
					totalPaymentTime = 1;
				}
				if (lastPaymentTerm < totalPaymentTime) {
					result.add(new BillCollectionDTO(policyId, policyNo, initialId + name.getFullName(), idNo, endDate, endDate, paymentType, 1, lastPaymentTerm,
							basicTermPremium.doubleValue() + addOnTermPremium.doubleValue(), 0, 0, null, 0, 0, 0, 0, 0, extraAmount));
				}

				em.flush();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BillCollectionDTO> findPaidUpPolicyByCriteria(PolicyCriteria criteria) throws DAOException {
		List<BillCollectionDTO> result = new ArrayList<BillCollectionDTO>();
		try {
			String policyId = null;
			String policyNo = null;
			PaymentType paymentType = null;
			Date startDate = null;
			Date endDate = null;
			int lastPaymentTerm = 0;
			Double basicTermPremium = null;
			Double addOnTermPremium = null;
			Double serviceCharges = null;
			Double paidUpAmount = null;
			Double realPaidUpAmount = null;
			Double receivedPremium = null;
			Date paidUpDate = null;
			String initialId = null;
			Name name = null;
			String idNo = null;
			StringBuffer query = new StringBuffer();
			query.append("SELECT Distinct l.id, l.policyNo, l.paymentType, l.activedPolicyStartDate, l.activedPolicyEndDate, ");
			query.append(" l.lastPaymentTerm,   p.basicTermPremium, p.addOnTermPremium , ");
			query.append(" p.initialId, p.name, p.idNo , pro.paidUpAmount , pro.realPaidUpAmount ,pro.receivedPremium , pro.serviceCharges , pro.submittedDate ");
			query.append(" FROM  LifePolicy l JOIN l.policyInsuredPersonList  p   LEFT  LifePaidUpProposal pro  ");
			query.append(" WHERE pro.complete = 0 AND pro.lifePolicy.id = l.id ");
			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case POLICYNO:
						query.append(" AND l.policyNo LIKE :policyNo ");
						break;
					case CUSTOMERNAME:
						query.append(
								" AND CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, ' ', l.customer.name.lastName) LIKE :name OR CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName, l.customer.name.lastName) like :name ");
						break;
					case ORGANIZATIONNAME:
						query.append(" AND l.organization.name LIKE :name ");
						break;
					case BANKCUSTOMER:
						query.append(" AND l.bank.name LIKE :name ");
						break;
				}
			}
			if (criteria.getFromDate() != null) {
				query.append(" AND pro.activedPolicyEndDate >= :fromDate ");
			}

			if (criteria.getToDate() != null) {
				query.append(" AND pro.activedPolicyEndDate <= :toDate ");
			}

			query.append(" ORDER BY l.policyNo ASC ");
			Query q = em.createQuery(query.toString());
			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case POLICYNO:
						q.setParameter("policyNo", "%" + criteria.getCriteriaValue() + "%");
						break;
					case ORGANIZATIONNAME:
						q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
						break;
					case CUSTOMERNAME:
						q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
						break;
					case BANKCUSTOMER:
						q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
						break;
				}
			}

			if (criteria.getFromDate() != null) {
				q.setParameter("fromDate", Utils.resetStartDate(criteria.getFromDate()));
			}

			if (criteria.getToDate() != null) {
				q.setParameter("toDate", Utils.resetEndDate(criteria.getToDate()));
			}

			List<Object[]> objectList = q.getResultList();

			for (Object[] arr : objectList) {
				policyId = (String) arr[0];
				policyNo = (String) arr[1];
				paymentType = (PaymentType) arr[2];
				startDate = (Date) arr[3];
				endDate = (Date) arr[4];
				lastPaymentTerm = (Integer) arr[5];
				lastPaymentTerm = lastPaymentTerm == 0 ? 2 : lastPaymentTerm + 1;
				basicTermPremium = (Double) arr[6];
				addOnTermPremium = (Double) arr[7];
				basicTermPremium = basicTermPremium == null ? 0 : basicTermPremium;
				addOnTermPremium = addOnTermPremium == null ? 0 : addOnTermPremium;
				initialId = (String) arr[8];
				name = (Name) arr[9];
				idNo = (String) arr[10];
				paidUpAmount = (Double) arr[11];
				realPaidUpAmount = (Double) arr[12];
				receivedPremium = (Double) arr[13];
				serviceCharges = (Double) arr[14];
				paidUpDate = (Date) arr[15];
				result.add(new BillCollectionDTO(policyId, policyNo, initialId + name.getFullName(), idNo, startDate, endDate, paymentType, 1, lastPaymentTerm,
						basicTermPremium.doubleValue() + addOnTermPremium.doubleValue(), 0, 0, serviceCharges, paidUpAmount, realPaidUpAmount, receivedPremium, paidUpDate));

			}

			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBillCollection(LifePolicy lifePolicy) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE LifePolicy p SET p.lastPaymentTerm = :lastPaymentTerm, p.activedPolicyEndDate = :activedPolicyEndDate WHERE p.id = :id ");
			q.setParameter("lastPaymentTerm", lifePolicy.getLastPaymentTerm());
			q.setParameter("activedPolicyEndDate", lifePolicy.getActivedPolicyEndDate());
			q.setParameter("id", lifePolicy.getId());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Bill Collection after life policy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyNotificationDTO> findNotificationLifePolicy(NotificationCriteria criteria) throws DAOException {
		Map<String, PolicyNotificationDTO> map = new HashMap<String, PolicyNotificationDTO>();
		List<Object> objectList = new ArrayList<Object>();
		List<PolicyNotificationDTO> result = new ArrayList<PolicyNotificationDTO>();
		try {
			String policyNo;
			String insuredPersonName;
			String idNo;
			String paymentType;
			int paymentTerm;
			double basicTermPremium;
			Date activedPolicyStartDate;
			Date activedPolicyEndDate;
			int periodMonth = 0;
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT l.policyNo, pi.initialId, pi.name, pi.idNo, l.paymentType.name, l.lastPaymentTerm, SUM(pi.basicTermPremium), ");
			buffer.append("l.activedPolicyStartDate, l.activedPolicyEndDate, pi.periodMonth,l.salePoint.name FROM LifePolicy l INNER JOIN l.policyInsuredPersonList pi ");
			buffer.append("WHERE l.policyNo IS NOT NULL AND l.activedPolicyEndDate != pi.endDate AND pi.product.id = :productId ");
			if (criteria.getSalePoint() != null)
				buffer.append(" AND l.salePoint.id = :salePointId ");

			if (criteria.getReportType().equalsIgnoreCase("Monthly") || criteria.getStartDate() != null)
				buffer.append(" AND l.activedPolicyEndDate >= :startDate");

			if (criteria.getReportType().equalsIgnoreCase("Monthly") || criteria.getEndDate() != null)
				buffer.append(" AND l.activedPolicyEndDate <= :endDate");

			buffer.append(" GROUP BY l.policyNo, pi.initialId, pi.name, pi.idNo, l.paymentType.name, l.lastPaymentTerm, ");
			buffer.append("l.activedPolicyStartDate, l.activedPolicyEndDate, pi.periodMonth,l.salePoint.name");

			Query query = em.createQuery(buffer.toString());

			if (ReferenceType.LIFE_POLICY.equals(criteria.getReferenceType())) {
				query.setParameter("productId", KeyFactorChecker.getPublicLifeID());
			} else if (ReferenceType.SHORT_ENDOWMENT_LIFE_POLICY.equals(criteria.getReferenceType())) {
				query.setParameter("productId", KeyFactorChecker.getShortTermEndowmentID());
			} else if (ReferenceType.STUDENT_LIFE_POLICY.equals(criteria.getReferenceType())) {
				query.setParameter("productId", KeyFactorChecker.getStudentLifeID());
			} else if (ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY.equals(criteria.getReferenceType())) {
				query.setParameter("productId", KeyFactorChecker.getSinglePremiumCreditLifeID());
			} else if (ReferenceType.SINGLE_PREMIUM_ENDOWMNET_LIFE_POLICY.equals(criteria.getReferenceType())) {
				query.setParameter("productId", KeyFactorChecker.getSinglePremiumEndowmentLifeID());
			} else if (ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY.equals(criteria.getReferenceType())) {
				query.setParameter("productId", KeyFactorChecker.getShortTermSinglePremiumCreditLifeID());
			} 

			if (criteria.getReportType().equalsIgnoreCase("Monthly")) {
				query.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
				query.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));

			} else {
				if (criteria.getStartDate() != null) {
					criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
					query.setParameter("startDate", criteria.getStartDate());
				}
				if (criteria.getEndDate() != null) {
					criteria.setEndDate(Utils.resetStartDate(criteria.getEndDate()));
					query.setParameter("endDate", criteria.getEndDate());
				}
			}

			if (criteria.getSalePoint() != null) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}

			objectList = query.getResultList();

			for (Object object : objectList) {
				Object[] objArr = (Object[]) object;
				policyNo = (String) objArr[0];
				Name name = (Name) objArr[2];
				insuredPersonName = (String) objArr[1] + " " + name.getFirstName() + " " + name.getMiddleName() + " " + name.getLastName();
				idNo = (String) objArr[3];
				paymentType = (String) objArr[4];
				paymentTerm = ((Integer) objArr[5]) != 0 ? (Integer) objArr[5] + 1 : 2;
				basicTermPremium = (Double) objArr[6];
				activedPolicyStartDate = (Date) objArr[7];
				activedPolicyEndDate = (Date) objArr[8];
				periodMonth = (Integer) objArr[9];
				String salepointName = (String) objArr[10];
				map.put(policyNo, new PolicyNotificationDTO(policyNo, insuredPersonName, idNo, paymentType, paymentTerm, basicTermPremium, activedPolicyStartDate,
						activedPolicyEndDate, salepointName));
			}

			for (PolicyNotificationDTO dto : map.values()) {
				String surveyQuery = "SELECT s.refund FROM LifePolicySummary s WHERE s.policyNo = :policyNo";
				Query wf = em.createQuery(surveyQuery);
				wf.setParameter("policyNo", dto.getPolicyNo());
				try {
					double ref = (Double) wf.getSingleResult();
					dto.setRefund(ref);
				} catch (Exception e) {
					dto.setRefund(0.0);
				}
				double total = dto.getTermPremium() - dto.getRefund();
				dto.setTotalAmout(total);
				result.add(dto);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}
		RegNoSorter<PolicyNotificationDTO> regNoSorter = new RegNoSorter<PolicyNotificationDTO>(result);
		return regNoSorter.getSortedList();
	}

	public boolean isBetweenNotificationDays(Date startDate, Date endDate, Date policyDate) {
		if (policyDate.getTime() >= startDate.getTime() && policyDate.getTime() <= endDate.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(EndorsementLifePolicyPrint endorsementPolicyPrint) throws DAOException {
		try {
			em.persist(endorsementPolicyPrint);
			insertProcessLog(TableName.LIFEPOLICY_ENDORSEMENT_PRINT, endorsementPolicyPrint.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to insert FirePolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(EndorsementLifePolicyPrint endorsementPolicyPrint) throws DAOException {
		try {
			em.merge(endorsementPolicyPrint);
			updateProcessLog(TableName.LIFEPOLICY_ENDORSEMENT_PRINT, endorsementPolicyPrint.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to update FirePolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public EndorsementLifePolicyPrint findEndorsementLifePolicyPrintById(String id) throws DAOException {
		EndorsementLifePolicyPrint result = null;
		try {
			result = em.find(EndorsementLifePolicyPrint.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find EndorsementPolicyPrint", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<EndorsementLifePolicyPrint> findEndorsementPolicyPrintByLifePolicyNo(String lifePolicyNo) throws DAOException {
		List<EndorsementLifePolicyPrint> result = null;
		try {
			Query q = em.createQuery("SELECT  e FROM EndorsementLifePolicyPrint e WHERE e.lifePolicy.policyNo = :lifePolicyNo ");
			q.setParameter("lifePolicyNo", lifePolicyNo);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find EndorsementPolicyPrint by firePolicyNo", pe);
		}
		return result;
	}

	@Override
	public String findPolicyNoById(String policyId) throws DAOException {
		String result = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findPolicyNoById");
			q.setParameter("id", policyId);
			result = (String) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PolicyNo by PolicyId : " + policyId, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicy findByLifeProposalId(String lifeProposalId) throws DAOException {
		LifePolicy result = null;
		LifePolicyHistory history = null;
		try {
			Query q = em.createNamedQuery("LifePolicy.findByLifeProposalId");
			q.setParameter("lifeProposalId", lifeProposalId);
			result = (LifePolicy) q.getSingleResult();
			em.flush();
		} catch (NoResultException nre) {
			history = lifePolicyHistoryDAO.findByProposalId(lifeProposalId);
			if (history != null)
				return new LifePolicy(history);
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) throws DAOException {
		List<Payment> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT p FROM LifePolicy l, Payment p, TLF t WHERE (l.isEndorsementApplied = FALSE OR l.isEndorsementApplied IS NULL) ");
			if (criteria.getReceiptNoCriteria() != null) {
				switch (criteria.getReceiptNoCriteria()) {
					case RECEIPTNO: {
						query.append("AND p.receiptNo like :receiptNo AND l.id = p.referenceNo AND p.isPO = TRUE ");
						query.append("AND l.id = t.referenceNo AND t.clearing = TRUE AND t.paid = FALSE ");
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

	// used for migration
	// delete after migration
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyPersonCode(List<PolicyInsuredPerson> personList) throws DAOException {
		try {
			for (PolicyInsuredPerson person : personList) {
				Query query = em.createQuery("UPDATE PolicyInsuredPerson p SET p.insPersonCodeNo = :personCodeNo WHERE p.id = :id");
				query.setParameter("id", person.getId());
				query.setParameter("personCodeNo", person.getInsPersonCodeNo());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update insured person code no", pe);
		}
	}

	// used for migration
	// delete after migration
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyBeneficiaryCode(List<PolicyInsuredPersonBeneficiaries> beneficiaryList) throws DAOException {
		try {
			for (PolicyInsuredPersonBeneficiaries beneficiary : beneficiaryList) {
				Query query = em.createQuery("UPDATE PolicyInsuredPersonBeneficiaries p SET p.beneficiaryNo = :beneficiaryNo WHERE p.id = :id");
				query.setParameter("id", beneficiary.getId());
				query.setParameter("beneficiaryNo", beneficiary.getBeneficiaryNo());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update beneficiary code no", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<LPC001> findEndorsementByCriteria(PolicyCriteria criteria, int max) throws DAOException {
		List<Object[]> objectList = new ArrayList<Object[]>();
		Map<String, LPC001> resultMap = new HashMap<String, LPC001>();
		User loginUser = userProcessService.getLoginUser();
		boolean accessAllBranch = loginUser.isAccessAllBranch();
		String branchId = idConfigLoader.getBranchId();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT l.id, l.policyNo, s.initialId, s.name, a.initialId, a.name, c.initialId, c.name, o.name, b.name, pi.premium, pi.sumInsured, ");
			query.append(" t.name FROM LifePolicy l INNER JOIN l.policyInsuredPersonList pi LEFT OUTER JOIN pi.product p ");
			query.append(" LEFT OUTER JOIN l.saleMan s LEFT OUTER JOIN l.agent a LEFT OUTER JOIN l.customer c LEFT OUTER JOIN ");
			query.append(" l.organization o LEFT OUTER JOIN l.branch b LEFT OUTER JOIN l.paymentType t ");
			query.append(" WHERE p.id IN :productList AND ");

			query.append(" (l.isEndorsementApplied = FALSE OR l.isEndorsementApplied IS NULL) ");
			query.append(" AND l.policyNo IS NOT NULL");
			if (!accessAllBranch) {
				query.append(" AND b.id = :branchId");
			}

			if (criteria.getPolicyCriteria() != null) {
				switch (criteria.getPolicyCriteria()) {
					case POLICYNO: {
						query.append(" AND l.policyNo like :criteriaValue");
						break;
					}
					case CUSTOMERNAME: {
						query.append(
								" AND FUNCTION( 'REPLACE', CONCAT(CONCAT(c.initialId, ''), CONCAT(c.name.firstName,''), CONCAT(c.name.middleName, ''), CONCAT(c.name.lastName , '') ), ' ', '')");
						query.append(" LIKE :criteriaValue ");
						break;
					}
					case ORGANIZATIONNAME: {
						query.append(" AND o.name like :criteriaValue ");
						break;
					}
				}
			}

			Query q = em.createQuery(query.toString());
			if (!accessAllBranch)
				q.setParameter("branchId", branchId);
			q.setParameter("productList", Arrays.asList(ProductIDConfig.getGroupLifeId(), ProductIDConfig.getShortEndowLifeId()));

			if (criteria.getPolicyCriteria() != null) {
				if (criteria.getPolicyCriteria().equals(PolicyCriteriaItems.CUSTOMERNAME))
					q.setParameter("criteriaValue", "%" + StringUtils.replace(criteria.getCriteriaValue(), " ", "") + "%");
				else
					q.setParameter("criteriaValue", "%" + criteria.getCriteriaValue() + "%");

			}
			objectList = q.getResultList();
			em.flush();
			String id;
			String policyNo;
			String saleManInitialId;
			Name saleManName;
			String saleMan = null;
			String agentInitialId;
			Name agentName;
			String agent = null;
			String customerInitialId;
			Name customerName;
			String customer;
			String organization;
			String branch;
			double premium;
			double sumInsured;
			String paymenttype;

			for (Object[] b : objectList) {
				id = (String) b[0];
				policyNo = (String) b[1];
				saleManInitialId = (String) b[2];
				saleManName = (Name) b[3];
				agentInitialId = (String) b[4];
				agentName = (Name) b[5];
				customerInitialId = (String) b[6];
				customerName = (Name) b[7];
				organization = (String) b[8];
				branch = (String) b[9];
				premium = (Double) b[10];
				sumInsured = (Double) b[11];
				paymenttype = (String) b[12];
				if (saleManName != null) {
					saleMan = saleManInitialId + saleManName.getFullName();
				}
				if (agentName != null) {
					agent = agentInitialId + agentName.getFullName();
				}
				if (customerName != null) {
					customer = customerInitialId + customerName.getFullName();
				} else {
					customer = organization;
				}
				if (resultMap.containsKey(policyNo)) {
					premium += resultMap.get(policyNo).getPremium();
					sumInsured += resultMap.get(policyNo).getSumInsured();
					resultMap.put(policyNo, new LPC001(id, policyNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, true));
				} else {
					resultMap.put(policyNo, new LPC001(id, policyNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, false));
				}
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}

		RegNoSorter<LPC001> sorter = new RegNoSorter<LPC001>(new ArrayList<LPC001>(resultMap.values()));
		return sorter.getSortedList();
	}

	/******** Migrate Endowment Policy ************/
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeEndowmentPolicySearch> findPublicLifePolicyByCriteria(LifePolicyCriteria criteria, boolean isMigrate) {
		List<LifeEndowmentPolicySearch> lifeEndowmentPolicy = new ArrayList<>();
		try {
			StringBuffer query = new StringBuffer();
			String productId = ProductIDConfig.getPublicLifeId();
			query.append("Select New " + LifeEndowmentPolicySearch.class.getName());
			query.append("(l.policyNo,l.commenmanceDate,l.activedPolicyEndDate,l.paymentType.name,max(p.fromTerm),SUM(p.basicPremium)+SUM(p.addOnPremium))");
			query.append(" from LifePolicy l,Payment p,PolicyInsuredPerson pi");
			query.append(" where l.id=p.referenceNo AND l.id=pi.lifePolicy.id AND pi.product.id=:productId AND p.complete!='0'");
			/*
			 * if (isMigrate) {
			 * query.append(" AND (l.isMigrated IS NULL or l.isMigrated='0')");
			 * }
			 */
			if (criteria.getLifePolicyCriteriaItems() != null)
				if (LifePolicyCriteriaItems.POLICY_NO == criteria.lifePolicyCriteriaItems) {
					query.append(" AND l.policyNo like :criteriaValue");
				} else if (LifePolicyCriteriaItems.CUSTOMER_NAME == criteria.lifePolicyCriteriaItems) {
					query.append(" AND (CONCAT( l.customer.name.firstName, ' ', l.customer.name.middleName, ' ', l.customer.name.lastName) like :criteriaValue"
							+ " OR CONCAT(l.customer.name.firstName, ' ', l.customer.name.middleName,l.customer.name.lastName) like :criteriaValue)");

				} else if (LifePolicyCriteriaItems.ORGANIZATION_NAME == criteria.lifePolicyCriteriaItems) {
					query.append(" AND l.organization.name like :criteriaValue");
				}
			// This boolean is for Payment State
			query.append(" AND (l.policyStatus IS NULL or l.policyStatus != :policyStatus)");
			query.append(" group by l.policyNo,l.commenmanceDate,l.activedPolicyEndDate,l.paymentType.name");

			Query q = em.createQuery(query.toString());
			q.setMaxResults(30);
			q.setParameter("productId", productId);
			if (criteria.getLifePolicyCriteriaItems() != null)
				q.setParameter("criteriaValue", "%" + criteria.getCriteriaValue() + "%");
			q.setParameter("policyStatus", PolicyStatus.TERMINATE);
			lifeEndowmentPolicy = q.getResultList();
		} catch (NoResultException n) {
		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}
		return lifeEndowmentPolicy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findTotalPaidPremiumFromLifePolicyHistoryByPolicyNo(String policyNo) {
		double result = 0;
		try {
			StringBuffer query = new StringBuffer();
			query.append("Select COALESCE(SUM(p.basicPremium),0.0)+COALESCE(SUM(p.addOnPremium),0.0) from Payment p,LifePolicyHistory lh");
			query.append(" where lh.policyNo=:policyNo and lh.policyReferenceNo=p.referenceNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("policyNo", policyNo);
			result = (double) q.getSingleResult();
		} catch (NoResultException n) {
			result = 0;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Total Paid Premium From LifePolicyHistory By Policy No", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePublicLifePolicy(LPC001 oldLifePolicy) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("Update LifePolicy l set l.policyStatus=:status where l.id=:id");
			Query q = em.createQuery(builder.toString());
			q.setParameter("status", oldLifePolicy.getPolicyStatus());
			q.setParameter("id", oldLifePolicy.getId());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update PublicLife Policy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findPublicLifeTotalPaidPremium(String lifePolicyId) {
		double result = 0.0;
		try {
			StringBuilder query = new StringBuilder();
			query.append("Select (SUM(p.basicPremium)+SUM(p.addOnPremium))");
			query.append(" from Payment p where p.referenceNo=:lifePolicyId");
			Query q = em.createQuery(query.toString());
			q.setParameter("lifePolicyId", lifePolicyId);
			result = (double) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PublicLife TotalPaidPremium", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findPolicyIdByPolicyNo(String policyNo) {
		String result;
		try {
			StringBuilder query = new StringBuilder();
			query.append("Select l.id from LifePolicy l where l.policyNo like :policyNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("policyNo", "%" + policyNo + "%");
			result = (String) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PolicyId By PolicyNo", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LPC001 findLifePolicyNoAndIdById(String id) {
		LPC001 result = new LPC001();
		try {
			StringBuilder query = new StringBuilder();
			query.append("Select New " + LPC001.class.getName() + "(l.id,l.policyNo) from LifePolicy l where l.id=:id");
			Query q = em.createQuery(query.toString());
			q.setParameter("id", id);
			result = (LPC001) q.getSingleResult();
			em.flush();
		} catch (NoResultException p) {
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicyNo And Id By Proposal Id", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findLifePolicyIdByLifeProposalId(String id) {
		String result;
		try {
			StringBuilder query = new StringBuilder();
			query.append("Select l.id from LifePolicy l where l.lifeProposal.id=:id");
			Query q = em.createQuery(query.toString());
			q.setParameter("id", id);
			result = (String) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicyId By LifeProposalId", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findAllLifePolicyByGroupFarmerProposalID(String groupFarmerId) {
		List<LifePolicy> result = null;
		try {
			Query q = em.createQuery("Select l from LifePolicy l where  l.lifeProposal.id in (select p.id from LifeProposal p where p.groupFarmerProposal.id = :groupFarmerId )");
			q.setParameter("groupFarmerId", groupFarmerId);
			result = q.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy By GroupFarmerProposalID", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicySearch> findLifePolicyForClaimByProduct(String productId) {
		List<LifePolicySearch> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append(" SELECT NEW " + LifePolicySearch.class.getName());
			query.append("(l.policyNo, c.initialId, c.name, o.name, l.branch.name ) FROM LifePolicy l ");
			query.append(" LEFT OUTER JOIN l.customer c LEFT OUTER JOIN l.organization o LEFT OUTER JOIN  l.policyInsuredPersonList i  WHERE  l.policyNo IS NOT NULL ");
			if (productId != null) {
				query.append(" AND  i.product.id = :productId ");
			}
			Query q = em.createQuery(query.toString());
			if (productId != null) {
				q.setParameter("productId", productId);
			}
			q.setMaxResults(30);
			result = q.getResultList();

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve LifePolicy for Claim By Criteria", pe);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicy> findPaymentOrderByReceiptNo(String receiptNo, String bpmsReceiptNo, PolicyReferenceType policyReferenceType) throws DAOException {
		List<LifePolicy> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT m FROM LifePolicy m, Payment p, TLF t WHERE m.id = p.referenceNo ");
			query.append("AND m.id = t.referenceNo AND p.isPO = TRUE AND t.clearing = TRUE AND t.paid = FALSE ");
			if (!StringUtils.isBlank(receiptNo)) {
				query.append("AND t.tlfNo= :receiptNo ");
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				query.append("AND t.bpmsReceiptNo= :bpmsReceiptNo ");
			}
			if (policyReferenceType != null) {
				query.append("AND p.referenceType = :policyReferenceType ");
			}
			Query q = em.createQuery(query.toString());
			if (!StringUtils.isBlank(receiptNo)) {
				q.setParameter("receiptNo", receiptNo);
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				q.setParameter("bpmsReceiptNo", bpmsReceiptNo);
			}
			if (policyReferenceType != null) {
				q.setParameter("policyReferenceType", policyReferenceType);
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy PaymentOrder", pe);
		}
		return result;
	}

}