package org.ace.insurance.medical.claim.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.medical.claim.ClaimInitialReport;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.claim.MedicalClaimCriteria;
import org.ace.insurance.medical.claim.persistence.interfaces.IMedicalClaimInitialRepDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("MedicalClaimInitialRepDAO")
public class MedicalClaimInitialRepDAO extends BasicDAO implements IMedicalClaimInitialRepDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public ClaimInitialReport insert(ClaimInitialReport medicalClaimInitialReport) throws DAOException {
		try {
			em.persist(medicalClaimInitialReport);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("failed to insert medicalClaimInitialReport", pe);
		}
		return medicalClaimInitialReport;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClaimInitialReport> findAll() throws DAOException {
		List<ClaimInitialReport> result = null;
		try {
			Query q = em.createNamedQuery("ClaimInitialReport.findAll");
			result = q.getResultList();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CliamInitialReport", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClaimInitialReport> findAllActiveClaim() throws DAOException {
		List<ClaimInitialReport> result = null;
		try {
			Query q = em.createNamedQuery("ClaimInitialReport.findAllActiveClaim");
			result = q.getResultList();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CliamInitialReport", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClaimInitialReport> findActiveClaimByCriteria(MedicalClaimCriteria criteria) throws DAOException {
		List<ClaimInitialReport> result = null;
		try {
			StringBuffer buffer = new StringBuffer();

			buffer.append("SELECT r FROM ClaimInitialReport r WHERE r.id IS NOT NULL");
			if (criteria.getPolicyNo() != null) {
				buffer.append(" AND r.policyNo = :policyNo");
			}
			if (criteria.getClaimInitialReportNo() != null) {
				buffer.append(" AND r.claimReportNo LIKE :claimReportNo");
			}
			if (criteria.getReporterName() != null) {
				buffer.append(" AND r.claimInitialReporter.name = :reporter");
			}
			if (criteria.getStartDate() != null) {
				buffer.append(" AND r.reportDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				buffer.append(" AND r.reportDate <= :endDate");
			}
			buffer.append(
					" AND r.claimStatus IN (org.ace.insurance.medical.claim.ClaimStatus.CLAIM_APPLIED,org.ace.insurance.medical.claim.ClaimStatus.INITIAL_INFORM)  Order by r.claimReportNo");

			Query q = em.createQuery(buffer.toString());
			if (criteria.getPolicyNo() != null) {
				q.setParameter("policyNo", criteria.getPolicyNo());
			}
			if (criteria.getClaimInitialReportNo() != null) {
				q.setParameter("claimReportNo", "%" + criteria.getClaimInitialReportNo() + "%");
			}
			if (criteria.getReporterName() != null) {
				q.setParameter("reporter", criteria.getReporterName());
			}
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", criteria.getEndDate());
			}

			result = q.getResultList();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CliamInitialReport", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ClaimInitialReport findByPolicyInsuredPersonId(String id) throws DAOException {
		ClaimInitialReport result;
		try {
			result = em.find(ClaimInitialReport.class, id);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CliamInitialReport", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(ClaimInitialReport claimInitialReport) throws DAOException {
		try {
			em.merge(claimInitialReport);
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to update CliamInitialReport", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateByPolicyInsured(String insuredPersonId, ClaimStatus claimStatus) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE ClaimInitialReport p SET p.claimStatus =:claimStatus WHERE p.policyInsuredPerson.id =: insuredPersonId");
			q.setParameter("claimStatus", claimStatus.PAID);
			q.setParameter("insuredPersonId", insuredPersonId);
			q.getResultList();
			em.flush();
			System.gc();
		} catch (PersistenceException pe) {
			throw translate("Failed to update CliamInitialReport", pe);
		}
	}
}
