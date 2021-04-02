/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.medical.policy.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.MedicalPolicyCriteriaItems;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.NotificationCriteria;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.BusinessUtils;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.claim.MedicalPolicyCriteria;
import org.ace.insurance.medical.claim.service.interfaces.IMedicalClaimProposalService;
import org.ace.insurance.medical.policy.MED002;
import org.ace.insurance.medical.policy.MPL001;
import org.ace.insurance.medical.policy.MPL002;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonGuardian;
import org.ace.insurance.medical.policy.persistence.interfaces.IMedicalPolicyDAO;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.manage.enquires.PolicyEnquiryCriteria;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionDTO;
import org.ace.insurance.web.manage.life.billcollection.PolicyNotificationDTO;
import org.ace.insurance.web.manage.medical.proposal.PolicyGuardianDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.GuardianFactory;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("MedicalPolicyDAO")
public class MedicalPolicyDAO extends BasicDAO implements IMedicalPolicyDAO {
	private Logger logger = Logger.getLogger(this.getClass());

	@Resource(name = "MedicalClaimProposalService")
	private IMedicalClaimProposalService medicalClaimProposalService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(MedicalPolicy medicalPolicy) throws DAOException {
		try {
			em.persist(medicalPolicy);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert MedicalPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(MedicalPolicy medicalPolicy) throws DAOException {
		try {
			medicalPolicy = em.merge(medicalPolicy);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MedicalPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(MedicalPolicy medicalPolicy) throws DAOException {
		try {
			medicalPolicy = em.merge(medicalPolicy);
			em.remove(medicalPolicy);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete MedicalPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> findMedPolicyInsuredPersonById(String id) throws DAOException {
		List<String> result = new ArrayList<String>();
		try {
			Query query = em.createQuery("SELECT m.unit FROM MedicalPolicyInsuredPerson m WHERE m.id = :id AND m.endDate >=:newDate ");
			query.setParameter("id", id);
			query.setParameter("newDate", new Date());
			result = query.getResultList();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalPolicy findById(String id) throws DAOException {
		MedicalPolicy result = null;
		try {
			result = em.find(MedicalPolicy.class, id);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalPolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalPolicy> findAll() throws DAOException {
		List<MedicalPolicy> result = null;
		try {
			Query q = em.createNamedQuery("MedicalPolicy.findAll");
			result = q.getResultList();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of MedicalPolicy", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBeneficiaryClaimStatusByBeneficiaryNo(String beneficiaryNo, ClaimStatus status) throws DAOException {
		try {
			Query q = em.createNamedQuery("MedicalPolicyInsuredPersonBeneficiaries.updateClaimStatus");
			q.setParameter("beneficiaryNo", beneficiaryNo);
			q.setParameter("claimStatus", status);
			// updateProcessLog(TableName.MEDICALPOLICYINSUREDPERSONBENEFICIARIES,
			// id);
			q.executeUpdate();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Claim Status By Id : ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyInsuBeneByBeneficiaryNo(MedicalPolicyInsuredPersonBeneficiaries medicalPolicyInsuredPersonBeneficiaries) throws DAOException {
		try {
			em.merge(medicalPolicyInsuredPersonBeneficiaries);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MedicalPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalPolicy findByProposalId(String proposalId) throws DAOException {
		MedicalPolicy result = null;
		try {
			Query q = em.createNamedQuery("MedicalPolicy.findByProposalId");
			q.setParameter("medicalProposalId", proposalId);
			result = (MedicalPolicy) q.getSingleResult();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of MedicalPolicy by ProposalID : " + proposalId, pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalPolicy findMedicalPolicyByPolicyNo(String policyNo) throws DAOException {
		MedicalPolicy result = null;
		try {
			Query q = em.createNamedQuery("MedicalPolicy.findByPolicyNo");
			q.setParameter("policyNo", policyNo);
			result = (MedicalPolicy) q.getSingleResult();
			em.flush();
			System.gc();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalPolicy", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MED002> findMedicalPolicyForPolicyEnquiry(PolicyEnquiryCriteria criteria) throws DAOException {
		List<MED002> result = new ArrayList<>();
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT NEW " + MED002.class.getName());
			queryString.append("(m.id,m.policyNo,m.medicalProposal.proposalNo,");
			queryString.append("a.initialId,a.name,s.initialId,s.name,");
			queryString.append("c.initialId,c.name,o.name,b.name,");
			queryString.append("m.activedPolicyStartDate,m.activedPolicyEndDate)");
			queryString.append(" FROM MedicalPolicy m ");
			queryString.append(" JOIN m.policyInsuredPersonList mp");
			queryString.append(" LEFT OUTER JOIN m.agent a ");
			queryString.append(" LEFT OUTER JOIN m.saleMan s ");
			queryString.append(" LEFT OUTER JOIN m.referral r ");
			queryString.append(" LEFT OUTER JOIN m.branch b ");
			queryString.append(" LEFT OUTER JOIN m.customer c ");
			queryString.append(" LEFT OUTER JOIN m.organization o  ");
			queryString.append(" WHERE m.policyNo IS NOT NULL ");
			if (criteria.getStartDate() != null) {
				queryString.append(" AND m.commenmanceDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND m.commenmanceDate <= :endDate");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND b.id = :branchId");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND c.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND o.id = :organizationId");
			}
			if (criteria.getAgent() != null) {
				queryString.append(" AND a.id = :agentId");
			}
			if (criteria.getSaleMan() != null) {
				queryString.append(" AND s.id = :saleManId");
			}
			if (criteria.getReferral() != null) {
				queryString.append(" AND r.id = :referralId");
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				queryString.append(" AND m.policyNo LIKE :policyNo ");
			}
			if (null != criteria.getProductIdList() && criteria.getProductIdList().size() > 0) {
				queryString.append(" AND mp.product.id in :productIdList");
			}

			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			if (criteria.getStartDate() != null) {
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getSaleMan() != null) {
				query.setParameter("saleManId", criteria.getSaleMan().getId());
			}
			if (criteria.getReferral() != null) {
				query.setParameter("referralId", criteria.getReferral().getId());
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				query.setParameter("policyNo", "%" + criteria.getPolicyNo() + "%");
			}

			if (null != criteria.getProductIdList() && criteria.getProductIdList().size() > 0) {
				query.setParameter("productIdList", criteria.getProductIdList());
			}
			result = (List<MED002>) query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalClaimProposal by EnquiryCriteria : ", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<MPL002> findMedicalPolicyForClaimByCriteria(MedicalPolicyCriteria criteria) throws DAOException {
		List<MPL002> medicalPolicyList = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT NEW " + MPL002.class.getName() + "(l.id, l.policyNo, c.initialId, c.name, o.name, l.branch.name)");
			buffer.append(" FROM MedicalPolicy l LEFT OUTER JOIN l.customer c LEFT OUTER JOIN l.organization o WHERE l.policyNo IS NOT NULL ");
			if (criteria.getCriteriaValue() != null) {
				if (MedicalPolicyCriteriaItems.CUSTOMER_NAME.equals(criteria.getMedicalPolicyCriteriaItems()))
					buffer.append(" AND CONCAT(TRIM(c.initialId),' ' ,TRIM(c.name.firstName),' ', TRIM(CONCAT(TRIM(c.name.middleName), ' ')), TRIM(c.name.lastName)) LIKE :param ");

				if (criteria.getMedicalPolicyCriteriaItems() == MedicalPolicyCriteriaItems.POLICY_NO)
					buffer.append(" AND l.policyNo LIKE :param");

				if (criteria.getMedicalPolicyCriteriaItems() == MedicalPolicyCriteriaItems.ORGANIZATION_NAME)
					buffer.append(" AND l.organization.name LIKE :param");
			}

			Query query = em.createQuery(buffer.toString());
			if (criteria.getCriteriaValue() != null) {
				if (criteria.getMedicalPolicyCriteriaItems() == MedicalPolicyCriteriaItems.CUSTOMER_NAME)
					query.setParameter("param", "%" + criteria.getCriteriaValue() + "%");

				if (criteria.getMedicalPolicyCriteriaItems() == MedicalPolicyCriteriaItems.POLICY_NO)
					query.setParameter("param", "%" + criteria.getCriteriaValue() + "%");

				if (criteria.getMedicalPolicyCriteriaItems() == MedicalPolicyCriteriaItems.ORGANIZATION_NAME)
					query.setParameter("param", "%" + criteria.getCriteriaValue() + "%");
			}

			query.setMaxResults(30);
			medicalPolicyList = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve MedicalPolicy for Claim By Criteria", pe);
		}

		return medicalPolicyList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MPL002> findMedicalPolicyForClaimByProduct(String productId) throws DAOException {
		List<MPL002> medicalPolicyList = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT NEW " + MPL002.class.getName() + "(l.id, l.policyNo, c.initialId, c.name, o.name, l.branch.name)");
			buffer.append(
					" FROM MedicalPolicy l LEFT OUTER JOIN l.customer c LEFT OUTER JOIN l.organization o LEFT OUTER JOIN l.policyInsuredPersonList pi  WHERE l.policyNo IS NOT NULL ");
			// buffer.append(" AND pi.product.id = :productId");
			Query query = em.createQuery(buffer.toString());

			// query.setParameter("productId", productId);

			query.setMaxResults(30);
			medicalPolicyList = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve MedicalPolicy for Claim By Product", pe);
		}

		return medicalPolicyList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyGuardian(PolicyGuardianDTO policyGuardianDTO) throws DAOException {
		try {
			MedicalPolicyInsuredPersonGuardian medicalPolicyInsuredPersonGuardian = GuardianFactory.getPolicyGuardian(policyGuardianDTO);
			em.merge(medicalPolicyInsuredPersonGuardian);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MedicalPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyInsuredPerson(MedicalPolicyInsuredPerson medicalPolicyInsuredPerson) throws DAOException {
		try {
			em.merge(medicalPolicyInsuredPerson);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MedicalPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalPolicy> findByCustomer(Customer customer) throws DAOException {
		List<MedicalPolicy> result = null;
		try {
			Query q = em.createNamedQuery("MedicalPolicy.findByCustomer");
			q.setParameter("customerId", customer.getId());
			result = q.getResultList();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of MedicalPolicy by ProposalID : ", pe);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBillCollection(MedicalPolicy medicalPolicy) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE MedicalPolicy p SET p.lastPaymentTerm = :lastPaymentTerm, p.activedPolicyEndDate = :activedPolicyEndDate WHERE p.id = :id ");
			q.setParameter("lastPaymentTerm", medicalPolicy.getLastPaymentTerm());
			q.setParameter("activedPolicyEndDate", medicalPolicy.getActivedPolicyEndDate());
			q.setParameter("id", medicalPolicy.getId());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Bill Collection after medical policy", pe);
		}
	}

	/* used in billCollection(billCollection payment) */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BillCollectionDTO> findBillCollectionByCriteria(PolicyCriteria criteria) throws DAOException {
		List<BillCollectionDTO> result = new ArrayList<BillCollectionDTO>();
		try {
			String policyId = null;
			String policyNo = null;
			PaymentType paymentType = null;
			Date endDate = null;
			int lastPaymentTerm = 0;
			String initialId = null;
			Name name = null;
			String idNo = null;
			String orgName = null;
			String orgRegNo = null;
			double basicTermPremium, addOnTermPremium, discountPercent, ncbPremium;
			PolicyReferenceType referenceType;
			int periodMonth = 0;
			StringBuffer query = new StringBuffer();
			// TODO FIXME update query with p's addon premium after adding in
			// entity
			query.append(" SELECT DISTINCT l.id, l.policyNo, l.paymentType, l.activedPolicyEndDate, ");

			query.append(" l.lastPaymentTerm, c.initialId,c.name, c.fullIdNo, o.name,o.regNo, ");

			query.append(" SUM(i.premium) , SUM(i.basicPlusPremium) , ");
			query.append(" SUM(i.addOnPremium) ,");
			query.append(" SUM(i.totalNcbPremium), p.referenceType , MAX(i.periodMonth) ");
			query.append(" FROM MedicalPolicy  l LEFT JOIN l.customer c  LEFT JOIN l.organization o ");
			query.append(" LEFT JOIN l.policyInsuredPersonList i, Payment p");
			query.append(" WHERE p.referenceNo = l.id AND p.toTerm = l.lastPaymentTerm AND p.complete = '1' and p.reverse = '0' AND i.endDate > l.activedPolicyEndDate ");

			if (criteria.getReferenceType() != null) {
				query.append(" AND i.product.id IN :productId ");
			}

			if (criteria.getPolicyCriteria() != null) {

				switch (criteria.getPolicyCriteria()) {
					case POLICYNO:
						query.append(" AND l.policyNo LIKE :policyNo ");
						break;
					case CUSTOMERNAME:
						query.append(
								" AND CONCAT(c.name.firstName, ' ', c.name.middleName, ' ', c.name.lastName) LIKE :name OR CONCAT(c.name.firstName, ' ', c.name.middleName, c.name.lastName) like :name ");
						break;
					case ORGANIZATIONNAME:
						query.append(" AND o.name LIKE :name ");
						break;
					case BANKCUSTOMER:
						query.append(" AND l.bank.name LIKE :name ");
						break;
				}
			}
			if (criteria.getSalePointId() != null && !criteria.getSalePointId().isEmpty()) {
				query.append(" AND l.salePoint.id = :salePointId ");
			}
			if (criteria.getFromDate() != null) {
				query.append(" AND l.activedPolicyEndDate >= :fromDate ");
			}

			if (criteria.getToDate() != null) {
				query.append(" AND l.activedPolicyEndDate <= :toDate ");
			}

			query.append(" AND l.id NOT IN ( SELECT p.referenceNo FROM Payment p WHERE p.complete = '0' and p.reverse = '0' )");
			query.append(" GROUP BY l.id, l.policyNo, l.paymentType, l.activedPolicyEndDate, l.lastPaymentTerm, c.initialId, c.name, c.fullIdNo, o.name, o.regNo, ");
			query.append(" p.referenceType");
			query.append(" ORDER BY l.policyNo ASC ");
			Query q = em.createQuery(query.toString());

			if (criteria.getReferenceType() != null) {
				q.setParameter("productId", ProductIDConfig.getIdByReferenceType(criteria.getReferenceType()));
			}

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

			if (criteria.getSalePointId() != null && !criteria.getSalePointId().isEmpty()) {
				q.setParameter("salePointId", criteria.getSalePointId());
			}

			if (criteria.getFromDate() != null) {
				q.setParameter("fromDate", Utils.resetStartDate(criteria.getFromDate()));
			}

			if (criteria.getToDate() != null) {
				q.setParameter("toDate", Utils.resetEndDate(criteria.getToDate()));
			}

			// TODO FIXME TOK ncb premium deduction on bill collection?
			List<Object[]> objectList = q.getResultList();

			for (Object[] arr : objectList) {
				String fullName;
				policyId = (String) arr[0];
				policyNo = (String) arr[1];
				paymentType = (PaymentType) arr[2];
				endDate = (Date) arr[3];
				lastPaymentTerm = (Integer) arr[4];
				lastPaymentTerm = lastPaymentTerm == 0 ? 1 : lastPaymentTerm;
				initialId = (String) arr[5];
				name = (Name) arr[6];
				idNo = (String) arr[7];
				orgName = (String) arr[8];
				orgRegNo = (String) arr[9];
				if (idNo == null) {
					idNo = orgRegNo;
				}
				if (initialId != null) {
					fullName = initialId + " " + name.getFullName();
				} else if (name != null) {
					fullName = name.getFullName();
				} else {
					fullName = orgName;
				}
				double basicPremium = (arr[10] == null ? 0 : (double) arr[10]) + (arr[11] == null ? 0 : (double) arr[11]);

				basicTermPremium = BusinessUtils.getTermPremium(basicPremium, paymentType.getMonth());
				addOnTermPremium = BusinessUtils.getTermPremium(arr[12] == null ? 0 : (double) arr[12], paymentType.getMonth());
				ncbPremium = arr[12] == null ? 0 : (double) arr[13];
				referenceType = (PolicyReferenceType) arr[14];
				periodMonth = (int) arr[15];
				int totalPaymentTime = 0;
				if (paymentType.getMonth() != 0) {
					totalPaymentTime = periodMonth / paymentType.getMonth();
				} else {
					totalPaymentTime = 1;
				}

				if (lastPaymentTerm < totalPaymentTime) {
					BillCollectionDTO bill = new BillCollectionDTO(policyId, policyNo, fullName, idNo, endDate, endDate, paymentType, 1, lastPaymentTerm, basicTermPremium,
							addOnTermPremium, 0, referenceType, ncbPremium, 0, 0, 0, 0);
					result.add(bill);
				}
				// TODO FIXME , so many zeroes :)
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MPL001> findPolicyByCriteria(MedicalPolicyCriteria criteria) throws DAOException {
		List<MPL001> policyList = new ArrayList<MPL001>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT p.id, p.policyNo, SUM(person.unit),");
			buffer.append(" CONCAT(TRIM(COALESCE(a.initialId, '')),' ', TRIM(COALESCE(a.name.firstName, '')), ' ',");
			buffer.append(" TRIM(COALESCE(a.name.middleName, '')), ' ', TRIM(COALESCE(a.name.lastName, ''))), ");
			buffer.append(" CONCAT(TRIM(COALESCE(s.initialId, '')),' ', TRIM(COALESCE(s.name.firstName, '')), ' ',");
			buffer.append(" TRIM(COALESCE(s.name.middleName, '')), ' ', TRIM(COALESCE(s.name.lastName, ''))), ");
			buffer.append(" CONCAT(TRIM(COALESCE(c.initialId, '')),' ', TRIM(COALESCE(c.name.firstName, '')), ' ',");
			buffer.append(" TRIM(COALESCE(c.name.middleName, '')), ' ', TRIM(COALESCE(c.name.lastName, ''))), ");
			buffer.append(" o.name, p.branch.name, p.paymentType.name, p.activedPolicyEndDate");
			buffer.append(" FROM MedicalPolicy p LEFT OUTER JOIN p.agent a LEFT OUTER JOIN p.customer c");
			buffer.append(" LEFT OUTER JOIN p.organization o LEFT OUTER JOIN p.saleMan s");
			buffer.append(" JOIN p.policyInsuredPersonList person");
			buffer.append(" WHERE p.policyNo IS NOT NULL");
			if (criteria.getCriteriaValue() != null) {
				switch (criteria.getMedicalPolicyCriteriaItems()) {
					case CUSTOMER_NAME:
						buffer.append(" AND CONCAT(TRIM(COALESCE(c.initialId, '')),' ', TRIM(COALESCE(c.name.firstName, '')), ' ',");
						buffer.append(" TRIM(COALESCE(c.name.middleName, '')), ' ', TRIM(COALESCE(c.name.lastName, ''))) LIKE :param");
						break;
					case POLICY_NO:
						buffer.append(" AND p.policyNo LIKE :param");
						break;
					case ORGANIZATION_NAME:
						buffer.append(" AND p.organization.name LIKE :param");
						break;
				}
			}
			buffer.append(" GROUP BY p.id, p.policyNo, a.initialId, a.name.firstName, a.name.middleName, a.name.lastName,");
			buffer.append(" s.initialId, s.name.firstName, s.name.middleName, s.name.lastName,");
			buffer.append(" c.initialId, c.name.firstName, c.name.middleName, c.name.lastName,");
			buffer.append(" o.name, p.branch.name, p.paymentType.name, p.activedPolicyEndDate");
			Query query = em.createQuery(buffer.toString());
			if (criteria.getCriteriaValue() != null) {
				query.setParameter("param", "%" + criteria.getCriteriaValue() + "%");
			}
			List<Object[]> objArr = query.getResultList();
			for (Object[] arr : objArr) {
				policyList.add(new MPL001((String) arr[0], (String) arr[1], (Long) arr[2], (String) arr[3], (String) arr[4], (String) arr[5], (String) arr[6], (String) arr[7],
						(String) arr[8], (Date) arr[9]));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve MedicalPolicy for Criteria", pe);
		}
		return policyList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyNotificationDTO> findPolicyNotificationByCriteria(NotificationCriteria criteria) throws DAOException {
		List<PolicyNotificationDTO> result = new ArrayList<PolicyNotificationDTO>();
		try {
			StringBuffer queryStr = new StringBuffer("SELECT p.policyNo");
			queryStr.append(", CONCAT(TRIM(COALESCE(c.initialId, '')),' ', TRIM(COALESCE(c.name.firstName, '')), ' ',");
			queryStr.append(" TRIM(COALESCE(c.name.middleName, '')), ' ', TRIM(COALESCE(c.name.lastName, '')))");
			queryStr.append(", COALESCE(c.fullIdNo,''),pt.name,p.lastPaymentTerm,COALESCE(i.premium,0)+COALESCE(i.basicPlusPremium,0)+COALESCE(SUM(a.premium),0)");
			queryStr.append(", p.activedPolicyStartDate,p.activedPolicyEndDate,p.salePoint.name");
			queryStr.append(" FROM MedicalPolicy p INNER JOIN p.policyInsuredPersonList i LEFT JOIN i.customer c");
			queryStr.append(" LEFT JOIN i.policyInsuredPersonAddOnList a JOIN p.paymentType pt WHERE pt.id != :lumpsum AND i.product.id IN :productIdList");

			if (criteria.getSalePoint() != null) {
				queryStr.append(" AND p.salePoint.id = :salePointId");
			}

			if (criteria.getReportType().equalsIgnoreCase("Monthly") || criteria.getStartDate() != null) {
				queryStr.append(" AND p.activedPolicyEndDate >= :startDate");
			}
			if (criteria.getReportType().equalsIgnoreCase("Monthly") || criteria.getEndDate() != null) {
				queryStr.append(" AND p.activedPolicyEndDate <= :endDate");
			}

			queryStr.append(" GROUP BY p.policyNo,c.initialId,c.name.firstName,c.name.middleName,");
			queryStr.append(" c.name.lastName,c.fullIdNo,pt.name,p.lastPaymentTerm,i.premium,i.basicPlusPremium,p.activedPolicyStartDate,p.activedPolicyEndDate,p.salePoint.name");

			Query q = em.createQuery(queryStr.toString());
			q.setParameter("lumpsum", KeyFactorChecker.getLumpsumId());

			if (ReferenceType.MEDICAL_POLICY.equals(criteria.getReferenceType())) {
				q.setParameter("productIdList", ProductIDConfig.getIdByReferenceType(criteria.getReferenceType()));
			}
			if (ReferenceType.CRITICAL_ILLNESS_POLICY.equals(criteria.getReferenceType())) {
				q.setParameter("productIdList", ProductIDConfig.getIdByReferenceType(criteria.getReferenceType()));
			}
			if (ReferenceType.HEALTH_POLICY.equals(criteria.getReferenceType())) {
				q.setParameter("productIdList", ProductIDConfig.getIdByReferenceType(criteria.getReferenceType()));
			}

			if (criteria.getReportType().equalsIgnoreCase("Monthly")) {
				q.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
				q.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));
				if (criteria.getSalePoint() != null) {
					q.setParameter("salePointId", criteria.getSalePoint().getId());
				}
			} else {
				if (criteria.getStartDate() != null) {
					criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
					q.setParameter("startDate", criteria.getStartDate());
				}
				if (criteria.getEndDate() != null) {
					criteria.setEndDate(Utils.resetStartDate(criteria.getEndDate()));
					q.setParameter("endDate", criteria.getEndDate());
				}
				if (criteria.getSalePoint() != null) {
					q.setParameter("salePointId", criteria.getSalePoint().getId());
				}
			}
			List<Object[]> objArr = q.getResultList();
			for (Object[] arr : objArr) {
				int lastPaymentTerm = (int) arr[4];
				result.add(new PolicyNotificationDTO((String) arr[0], (String) arr[1], (String) arr[2], (String) arr[3], lastPaymentTerm != 0 ? lastPaymentTerm + 1 : 2,
						((BigDecimal) arr[5]).doubleValue(), (Date) arr[6], (Date) arr[7], (String) arr[8]));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PolicyNotificationDTO by criteria", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalPolicy> findPaymentOrderByReceiptNo(String receiptNo, String bpmsReceiptNo, PolicyReferenceType policyReferenceType) throws DAOException {
		List<MedicalPolicy> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT m FROM MedicalPolicy m, Payment p, TLF t WHERE m.id = p.referenceNo ");
			query.append("AND m.id = t.referenceNo AND p.isPO = TRUE AND t.clearing = TRUE AND t.paid = FALSE ");

			if (!StringUtils.isBlank(receiptNo)) {
				query.append("AND t.tlfNo = :receiptNo ");
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				query.append("AND t.bpmsReceiptNo = :bpmsReceiptNo ");
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
