package org.ace.insurance.life.bancassurance.proposalhistory.persistence;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.life.bancassurance.proposalhistory.BancaassuranceProposalHistory;
import org.ace.insurance.life.bancassurance.proposalhistory.persistence.interfaces.IBancaassuranceProposalHistoryDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaassuranceProposalHistoryDAO")
public class BancaassuranceProposalHistoryDAO extends BasicDAO implements IBancaassuranceProposalHistoryDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposalHistory insert(BancaassuranceProposalHistory bancaassuranceProposalHistory) throws DAOException {
		try {
			em.persist(bancaassuranceProposalHistory);
			insertProcessLog(TableName.BANCAASSURANCEPROPOSALHISTORY, bancaassuranceProposalHistory.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert BancaassuranceProposalHistory", pe);
		}
		return bancaassuranceProposalHistory;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaassuranceProposalHistory bancaassuranceProposalHistory) throws DAOException {
		try {
			bancaassuranceProposalHistory = em.merge(bancaassuranceProposalHistory);
			em.remove(bancaassuranceProposalHistory);
		} catch (PersistenceException pe) {
			throw translate("Failed to delete BancaassuranceProposalHistory", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaassuranceProposalHistory bancaassuranceProposalHistory) throws DAOException {
		try {
			bancaassuranceProposalHistory = em.merge(bancaassuranceProposalHistory);
		} catch (PersistenceException pe) {
			throw translate("Failed to update BancaassuranceProposalHistory", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposalHistory findBancaassuranceProposalByLifeproposalId(String lifeProposalId) throws DAOException {
		BancaassuranceProposalHistory result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposalHistory b WHERE b.lifeProposal.id= :lifeProposalId");
			q.setParameter("lifeProposalId", lifeProposalId);
			result = (BancaassuranceProposalHistory) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposalHistory", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposalHistory findBancaassuranceProposalByMedicalproposalId(String medicalProposalId) throws DAOException {
		BancaassuranceProposalHistory result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposalHistory b WHERE b.medicalProposal.id= :medicalProposalId");
			q.setParameter("medicalProposalId", medicalProposalId);
			result = (BancaassuranceProposalHistory) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposalHistory", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposalHistory findBancaassuranceProposalByPersonTravelproposalId(String personTravelProposalId) throws DAOException {
		BancaassuranceProposalHistory result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposalHistory b WHERE b.personTravelProposal.id= :personTravelProposalId");
			q.setParameter("personTravelProposalId", personTravelProposalId);
			result = (BancaassuranceProposalHistory) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposalHistory findBancaassuranceProposalByTravelproposalId(String travelProposalId) throws DAOException {
		BancaassuranceProposalHistory result = null;
		try {
			Query q = em.createQuery("SELECT b FROM BancaassuranceProposalHistory b WHERE b.travelProposal.id= :travelProposalId");
			q.setParameter("travelProposalId", travelProposalId);
			result = (BancaassuranceProposalHistory) q.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BancaassuranceProposalHistory", pe);
		}
		return result;
	}

}
