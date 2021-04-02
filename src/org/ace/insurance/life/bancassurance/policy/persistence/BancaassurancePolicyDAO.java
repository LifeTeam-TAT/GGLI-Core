package org.ace.insurance.life.bancassurance.policy.persistence;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.persistence.interfaces.IBancaassurancePolicyDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaassurancePolicyDAO")
public class BancaassurancePolicyDAO extends BasicDAO implements IBancaassurancePolicyDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void insert(BancaassurancePolicy bancaassurancePolicy) {
		try {
			em.persist(bancaassurancePolicy);
		} catch (PersistenceException pe) {
			throw translate("Failed to insert BancaassuranceProposal", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(BancaassurancePolicy bancaassurancePolicy) {
		try {
			bancaassurancePolicy = em.merge(bancaassurancePolicy);
			em.remove(bancaassurancePolicy);
		} catch (PersistenceException pe) {
			throw translate("Failed to insert BancaassuranceProposal", pe);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(BancaassurancePolicy bancaassurancePolicy) {
		try {
			em.merge(bancaassurancePolicy);
		} catch (PersistenceException pe) {
			throw translate("Failed to insert BancaassuranceProposal", pe);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findById(String id) throws DAOException {
		BancaassurancePolicy result = null;
		try {
			result = em.find(BancaassurancePolicy.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByLifepolicyId(String lifePolicyId) {
		BancaassurancePolicy result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassurancePolicy b WHERE b.lifePolicy.id= :lifePolicyId");
			q.setParameter("lifePolicyId", lifePolicyId);
			result = (BancaassurancePolicy) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassurancePolicy", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByMedicalpolicyId(String medicalPolicyId) {
		BancaassurancePolicy result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassurancePolicy b WHERE b.medicalPolicy.id= :medicalPolicyId");
			q.setParameter("medicalPolicyId", medicalPolicyId);
			result = (BancaassurancePolicy) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassurancePolicy", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByPersonTravelpolicyId(String personTravelPolicyId) {
		BancaassurancePolicy result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassurancePolicy b WHERE b.personTravelPolicy.id= :personTravelPolicyId");
			q.setParameter("personTravelPolicyId", personTravelPolicyId);
			result = (BancaassurancePolicy) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassurancePolicy", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByTravelproposalId(String travelProposalId) {
		BancaassurancePolicy result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassurancePolicy b WHERE b.travelProposal.id= :travelProposalId");
			q.setParameter("travelProposalId", travelProposalId);
			result = (BancaassurancePolicy) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassurancePolicy", pe);
		}
		return result;
	}

}
