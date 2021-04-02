package org.ace.insurance.life.claim.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeClaimSurvey;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.persistence.interfaces.ILifeClaimProposalDAO;
import org.ace.insurance.system.common.paymenttype.persistence.interfaces.IPaymentTypeDAO;
import org.ace.insurance.web.manage.life.claim.LifeDisabilityPaymentCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifeClaimProposalDAO")
public class LifeClaimProposalDAO extends BasicDAO implements ILifeClaimProposalDAO {

	@Resource(name = "PaymentTypeDAO")
	private IPaymentTypeDAO paymentTypeDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(LifeClaimProposal claimProposal) throws DAOException {
		try {
			em.persist(claimProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeClaimProposal", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(LifeClaimProposal claimProposal) throws DAOException {
		try {
			em.merge(claimProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeClaimProposal", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(LifeClaimProposal claimProposal) throws DAOException {
		try {
			claimProposal = em.merge(claimProposal);
			em.remove(claimProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeClaimProposal", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeClaimProposal findById(String id) throws DAOException {
		LifeClaimProposal result = null;
		try {
			result = em.find(LifeClaimProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeClaimProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(LifeClaimSurvey lifeClaimSurvey) throws DAOException {
		try {
			em.persist(lifeClaimSurvey);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeClaimSurvey", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<DisabilityLifeClaim> findDisabilityLifeClaimByLifeClaimProposalNo(LifeDisabilityPaymentCriteria criteria) throws DAOException {
		List<DisabilityLifeClaim> result = new ArrayList<DisabilityLifeClaim>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT c FROM LifeClaimProposal p JOIN p.claimList c WHERE p.claimProposalNo = :claimProposalNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("claimProposalNo", criteria.getClaimProposalNo());
			List<LifePolicyClaim> results = q.getResultList();
			em.flush();

			for (LifePolicyClaim claim : results) {
				if (claim instanceof DisabilityLifeClaim) {
					DisabilityLifeClaim disabilityClaim = (DisabilityLifeClaim) claim;
					result.add(disabilityClaim);
				}
			}

		} catch (PersistenceException pe) {
			throw translate("Failed to find DisabilityLifeClaim", pe);
		}
		return result;
	}

	public void update(DisabilityLifeClaim disabilityLifeClaim) throws DAOException {
		try {
			em.merge(disabilityLifeClaim);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update DisabilityLifeClaim", pe);
		}
	}

}
