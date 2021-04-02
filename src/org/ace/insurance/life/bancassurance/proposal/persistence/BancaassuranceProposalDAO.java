package org.ace.insurance.life.bancassurance.proposal.persistence;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.persistence.interfaces.IBancaassuranceProposalDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaassuranceProposalDAO")
public class BancaassuranceProposalDAO extends BasicDAO implements IBancaassuranceProposalDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void insert(BancaassuranceProposal bancaassuranceProposal) {
		try {
			em.persist(bancaassuranceProposal);
		} catch (PersistenceException pe) {
			throw translate("Failed to insert BancaassuranceProposal", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(BancaassuranceProposal bancaassuranceProposal) {
		try {
			bancaassuranceProposal = em.merge(bancaassuranceProposal);
			em.remove(bancaassuranceProposal);
		} catch (PersistenceException pe) {
			throw translate("Failed to delete BancaassuranceProposal", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(BancaassuranceProposal bancaassuranceProposal) {
		// TODO Auto-generated method stub
		try {
			bancaassuranceProposal = em.merge(bancaassuranceProposal);
		} catch (PersistenceException pe) {
			throw translate("Failed to update BancaassuranceProposal", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposal findById(String id) throws DAOException {
		BancaassuranceProposal result = null;
		try {
			TypedQuery<BancaassuranceProposal> q = em.createQuery("SELECT B FROM BancaassuranceProposal B WHERE B.id = :id", BancaassuranceProposal.class);
			q.setParameter("id", id);
			result = q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal(BancaassuranceProposal = " + id + ")", pe);
		}
		return result;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByLifeproposalId(String lifeProposalId) {
		BancaassuranceProposal result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposal b WHERE b.lifeProposal.id= :lifeProposalId");
			q.setParameter("lifeProposalId", lifeProposalId);
			result = (BancaassuranceProposal) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByPersonTravelProposalId(String personTravelId) {
		BancaassuranceProposal result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposal b WHERE b.personTravelProposal.id= :personTravelId");
			q.setParameter("personTravelId", personTravelId);
			result = (BancaassuranceProposal) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByMedicalproposalId(String medicalProposalId) {
		BancaassuranceProposal result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposal b WHERE b.medicalProposal.id= :medicalProposalId");
			q.setParameter("medicalProposalId", medicalProposalId);
			result = (BancaassuranceProposal) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByPersonTravelproposalId(String personTravelProposalId) {
		BancaassuranceProposal result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposal b WHERE b.personTravelProposal.id= :personTravelProposalId");
			q.setParameter("personTravelProposalId", personTravelProposalId);
			result = (BancaassuranceProposal) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByTravelproposalId(String travelProposalId) {
		BancaassuranceProposal result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposal b WHERE b.travelProposal.id= :travelProposalId");
			q.setParameter("travelProposalId", travelProposalId);
			result = (BancaassuranceProposal) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal", pe);
		}
		return result;
	}
}
