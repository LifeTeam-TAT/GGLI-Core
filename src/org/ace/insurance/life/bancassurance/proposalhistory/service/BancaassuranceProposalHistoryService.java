package org.ace.insurance.life.bancassurance.proposalhistory.service;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposalhistory.BancaassuranceProposalHistory;
import org.ace.insurance.life.bancassurance.proposalhistory.persistence.interfaces.IBancaassuranceProposalHistoryDAO;
import org.ace.insurance.life.bancassurance.proposalhistory.service.interfaces.IBancaassuranceProposalHistoryService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "BancaassuranceProposalHistoryService")
public class BancaassuranceProposalHistoryService extends BaseService implements IBancaassuranceProposalHistoryService {

	@Resource(name = "BancaassuranceProposalHistoryDAO")
	private IBancaassuranceProposalHistoryDAO bancaassuranceProposalHistoryDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewBancaassuranceProposalHistory(BancaassuranceProposal bancaassuranceProposal) {
		try {
			BancaassuranceProposalHistory BancaassuranceProposalHistory = new BancaassuranceProposalHistory(bancaassuranceProposal);
			BancaassuranceProposalHistory.setReferenceId(bancaassuranceProposal.getId());
			bancaassuranceProposalHistoryDAO.insert(BancaassuranceProposalHistory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new BancaassuranceProposalHistory", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposalHistory updateBancaassuranceProposalHistory(BancaassuranceProposalHistory bancaassuranceProposalHistory) {
		try {
			bancaassuranceProposalHistoryDAO.update(bancaassuranceProposalHistory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a BancaassuranceProposalHistory", e);
		}
		return bancaassuranceProposalHistory;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteBancaassuranceProposalHistory(BancaassuranceProposalHistory BancaassuranceProposalHistory) {
		try {
			bancaassuranceProposalHistoryDAO.delete(BancaassuranceProposalHistory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a BancaassuranceProposalHistory", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposalHistory findBancaassuranceProposalByLifeproposalId(String lifeProposalId) {
		BancaassuranceProposalHistory result = null;
		try {
			result = bancaassuranceProposalHistoryDAO.findBancaassuranceProposalByLifeproposalId(lifeProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassuranceProposalHistory", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposalHistory findBancaassuranceProposalByMedicalproposalId(String medicalProposalId) {
		BancaassuranceProposalHistory result = null;
		try {
			result = bancaassuranceProposalHistoryDAO.findBancaassuranceProposalByMedicalproposalId(medicalProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassuranceProposalHistory", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposalHistory findBancaassuranceProposalByPersonTravelproposalId(String personTravelProposalId) {
		BancaassuranceProposalHistory result = null;
		try {
			result = bancaassuranceProposalHistoryDAO.findBancaassuranceProposalByPersonTravelproposalId(personTravelProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassuranceProposalHistory", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposalHistory findBancaassuranceProposalByTravelproposalId(String TravelProposalId) {
		BancaassuranceProposalHistory result = null;
		try {
			result = bancaassuranceProposalHistoryDAO.findBancaassuranceProposalByTravelproposalId(TravelProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassuranceProposalHistory", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposalHistory addBancaassuranceProposalHistory(BancaassuranceProposalHistory bancaHistory) throws DAOException {
		try {
			bancaassuranceProposalHistoryDAO.insert(bancaHistory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new BancaassuranceProposalHistory", e);
		}
		return bancaHistory;
	}

}
