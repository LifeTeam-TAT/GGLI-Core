package org.ace.insurance.medical.proposal.persistence;

import java.math.BigDecimal;
/***************************************************************************************
 * @author Kyaw Myat Htut
 * @Date 2014-6-31
 * @Version 1.0
 * @Purpose This class serves as the DAO to manipulate the <code>Process</code>
 *          object.
 * 
 ***************************************************************************************/
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.Utils;
import org.ace.insurance.medical.proposal.MP001;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalAttachment;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAttachment;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("MedicalProposalDAO")
public class MedicalProposalDAO extends BasicDAO implements IMedicalProposalDAO {
	private Logger logger = Logger.getLogger(this.getClass());

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsPerWithReasonAndApprove(String rejectReason, boolean approved, String id) throws DAOException {
		try {
			Query query = em
					.createQuery("UPDATE MedicalProposalInsuredPerson s SET s.rejectReason =:rejectReason , s.approved =:approved, s.finishedApproved=true WHERE s.id =:id");
			query.setParameter("rejectReason", rejectReason);
			query.setParameter("approved", approved);
			query.setParameter("id", id);
			query.executeUpdate();

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update InsPerWithReasonAndApprove", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #insert(org.ace.insurance.medical.proposal.MedicalProposal)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal insert(MedicalProposal medicalProposal) throws DAOException {
		try {
			em.persist(medicalProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert MedicalProposal", pe);
		}
		return medicalProposal;
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #update(org.ace.insurance.medical.proposal.MedicalProposal)
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(MedicalProposal medicalProposal) throws DAOException {
		try {
			em.merge(medicalProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MedicalProposal", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #delete(org.ace.insurance.medical.proposal.MedicalProposal)
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(MedicalProposal medicalProposal) throws DAOException {
		try {
			medicalProposal = em.merge(medicalProposal);
			em.remove(medicalProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete MedicalProposal", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #findById(String)
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal findById(String id) throws DAOException {
		MedicalProposal result = null;
		try {
			result = em.find(MedicalProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalProposal", pe);
		}
		return result;
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposal> findAll() throws DAOException {
		List<MedicalProposal> result = null;
		try {
			Query q = em.createNamedQuery("MedicalProposal.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of MedicalProposal", pe);
		}
		return result;
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("MedicalProposal.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}

	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertSurvey(MedicalSurvey medicalSurvey) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM MedicalSurvey l WHERE l.medicalProposal.id = :medicalProposalId");
			delQuery.setParameter("medicalProposalId", medicalSurvey.getMedicalProposal().getId());
			delQuery.executeUpdate();
			em.persist(medicalSurvey);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Survey", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuPersonMedicalStatus(MedicalProposalInsuredPerson proposalInsuredPerson) throws DAOException {
		try {

			String queryString = "UPDATE MedicalProposalInsuredPerson p SET p.clsOfHealth = :clsOfHealth WHERE p.id = :insuPersonId";
			Query query = em.createQuery(queryString);
			query.setParameter("insuPersonId", proposalInsuredPerson.getId());
			query.executeUpdate();

		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addAttachment(MedicalProposal medicalProposal) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM MedicalProposalAttachment l WHERE l.medicalProposal.id = :medicalProposalId");
			delQuery.setParameter("medicalProposalId", medicalProposal.getId());
			delQuery.executeUpdate();
			for (MedicalProposalAttachment att : medicalProposal.getAttachmentList()) {
				em.persist(att);
			}
			for (MedicalProposalInsuredPerson pin : medicalProposal.getMedicalProposalInsuredPersonList()) {
				Query query = em.createQuery("DELETE FROM MedicalProposalInsuredPersonAttachment l WHERE l.proposalInsuredPerson.id = :proposalInsuredPersonId");
				query.setParameter("proposalInsuredPersonId", pin.getId());
				query.executeUpdate();
				for (MedicalProposalInsuredPersonAttachment att : pin.getAttachmentList()) {
					em.persist(att);
				}
			}

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Attachment", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuredPersonApprovalInfo(MedicalProposalInsuredPerson proposalInsuredPerson) throws DAOException {
		try {
			em.merge(proposalInsuredPerson);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MP001> findByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {
		List<Object[]> objArr = null;
		List<MP001> result = new ArrayList<>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT m.id, m.proposalNo, ");
			buffer.append(" CONCAT(TRIM(COALESCE(s.initialId, '')),' ', TRIM(COALESCE(s.name.firstName, '')), ' ',");
			buffer.append(" TRIM(COALESCE(s.name.middleName, '')), ' ', TRIM(COALESCE(s.name.lastName, ''))), ");
			buffer.append(" CONCAT(TRIM(COALESCE(a.initialId, '')),' ', TRIM(COALESCE(a.name.firstName, '')), ' ',");
			buffer.append(" TRIM(COALESCE(a.name.middleName, '')), ' ', TRIM(COALESCE(a.name.lastName, ''))), ");
			buffer.append(" CONCAT(TRIM(COALESCE(r.initialId, '')),' ', TRIM(COALESCE(r.name.firstName, '')), ' ',");
			buffer.append(" TRIM(COALESCE(r.name.middleName, '')), ' ', TRIM(COALESCE(r.name.lastName, ''))), ");
			buffer.append(" CONCAT(TRIM(COALESCE(c.initialId, '')),' ', TRIM(COALESCE(c.name.firstName, '')), ' ',");
			buffer.append(" TRIM(COALESCE(c.name.middleName, '')), ' ', TRIM(COALESCE(c.name.lastName, ''))), ");
			buffer.append(" b.name, SUM(mp.unit + mp.basicPlusUnit), o.name, ");
			buffer.append(" SUM(COALESCE(mp.proposedPremium, 0) + COALESCE(mp.basicPlusPremium, 0 ) + COALESCE(mp.proposedAddOnPremium, 0 )), ");
			buffer.append(" m.submittedDate");
			buffer.append(" FROM MedicalProposal m JOIN m.medicalProposalInsuredPersonList mp ");
			buffer.append(" LEFT OUTER JOIN m.saleMan s LEFT OUTER JOIN m.agent a LEFT OUTER JOIN m.referral r ");
			buffer.append(" LEFT OUTER JOIN m.customer c LEFT OUTER JOIN m.branch b");
			buffer.append(" LEFT OUTER JOIN m.organization o ");
			buffer.append(" WHERE m.proposalNo IS NOT NULL");
			if (criteria.getAgent() != null) {
				buffer.append(" AND m.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				buffer.append(" AND m.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				buffer.append(" AND m.submittedDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				buffer.append(" AND m.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				buffer.append(" AND m.organization.id = :orgnizationId");
			}
			if (criteria.getSaleMan() != null) {
				buffer.append(" AND m.saleMan.id = :saleManId");
			}
			if (criteria.getReferral() != null) {
				buffer.append(" AND m.referral.id = :referralId");
			}
			if (criteria.getBranch() != null) {
				buffer.append(" AND m.branch.id = :branchId");
			}
			if (criteria.getProduct() != null) {
				buffer.append(" AND mp.product.id = :productId");
			}
			if (null != criteria.getProductIdList() && criteria.getProductIdList().size() > 0) {
				buffer.append(" AND mp.product.id IN :productList");
			}
			if (!criteria.getNumber().isEmpty()) {
				buffer.append(" AND m.proposalNo= :proposalNo");
			}
			if (criteria.getProposalType() != null) {
				buffer.append(" AND m.proposalType = :proposalType");
			}

			buffer.append(" GROUP BY m.id, m.proposalNo,");
			buffer.append(" s.initialId, s.name, a.initialId, a.name,  ");
			buffer.append(" r.initialId, r.name, c.initialId, c.name, ");
			buffer.append(" b.name, o.name, m.submittedDate");

			/* Executed query */
			Query query = em.createQuery(buffer.toString());
			if (criteria.getProposalType() != null) {
				query.setParameter("proposalType", criteria.getProposalType());
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
				query.setParameter("orgnizationId", criteria.getOrganization().getId());
			}
			if (criteria.getSaleMan() != null) {
				query.setParameter("saleManId", criteria.getSaleMan().getId());
			}
			if (criteria.getReferral() != null) {
				query.setParameter("referralId", criteria.getReferral().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			}
			if (null != criteria.getProductIdList() && criteria.getProductIdList().size() > 0) {
				query.setParameter("productList", criteria.getProductIdList());
			}
			if (!criteria.getNumber().isEmpty()) {
				query.setParameter("proposalNo", criteria.getNumber());
			}

			objArr = query.getResultList();
			String id = null;
			String proposalNo = null;
			String salePerson = null;
			String customer = null;
			String organization = null;
			String branch = null;
			Long unit = null;
			BigDecimal totalPremium = null;
			Date submittedDate = null;
			for (Object[] arr : objArr) {
				id = (String) arr[0];
				proposalNo = (String) arr[1];
				salePerson = (String) arr[2] + (String) arr[3] + (String) arr[4];
				customer = (String) arr[5];
				branch = (String) arr[6];
				unit = (Long) arr[7];
				organization = (String) arr[8];
				totalPremium = (BigDecimal) arr[9];
				submittedDate = (Date) arr[10];
				if (organization != null) {
					customer = organization;
				}
				result.add(new MP001(id, proposalNo, salePerson, customer, branch, unit, totalPremium.doubleValue(), submittedDate, "", ""));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by EnquiryCriteria : ", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalSurvey findSurveyByProposalId(String proposalId) throws DAOException {
		MedicalSurvey result = null;
		try {
			logger.debug("findSurveyByProposalId() method has been started.");
			Query query = em.createQuery("SELECT l FROM MedicalSurvey l WHERE l.medicalProposal.id = :id");
			query.setParameter("id", proposalId);
			result = (MedicalSurvey) query.getSingleResult();
			em.flush();
			logger.debug("findSurveyByProposalId() method has been started.");
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Proposal Survey : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SurveyQuestionAnswer> findSurveyQuestionAnswerByProposalId(String proposalId) throws DAOException {
		List<SurveyQuestionAnswer> surveyQuestionAnswerList = null;
		try {
			Query q = em.createQuery("SELECT p.surveyQuestionAnswerList FROM MedicalProposal m  JOIN m.medicalProposalInsuredPersonList p  where m.id =:proposalId ");
			q.setParameter("proposalId", proposalId);
			surveyQuestionAnswerList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Survey Question Answer : ", pe);
		}
		return surveyQuestionAnswerList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMedicalProposalCompleteStatus(String proposalId) throws DAOException {
		try {
			Query query = em.createNamedQuery("MedicalProposal.updateCompleteStatus");
			query.setParameter("id", proposalId);
			query.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Medical Proposal Complete Status", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposal> findMedicalProposalByGroupMicroHealthId(String id) throws DAOException {
		try {
			TypedQuery<MedicalProposal> query = em.createNamedQuery("MedicalProposal.findByGroupMicroHealthId", MedicalProposal.class);
			query.setParameter("id", id);
			return query.getResultList();
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Failed to Find Medical Proposal ", pe);
		}
	}
}
