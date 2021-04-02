package org.ace.insurance.life.bancassurance.proposal.service;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.persistence.interfaces.IBancaassuranceProposalDAO;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.bancassurance.proposalhistory.BancaassuranceProposalHistory;
import org.ace.insurance.life.bancassurance.proposalhistory.persistence.interfaces.IBancaassuranceProposalHistoryDAO;
import org.ace.insurance.life.bancassurance.proposalhistory.service.interfaces.IBancaassuranceProposalHistoryService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "BancaassuranceProposalService")
public class BancaassuranceProposalService implements IBancaassuraceProposalService {

	@Resource(name = "BancaassuranceProposalDAO")
	private IBancaassuranceProposalDAO bancaassuranceProposalDAO;

	@Resource(name = "BancaassuranceProposalHistoryDAO")
	private IBancaassuranceProposalHistoryDAO bancaassuranceProposalHistoryDAO;

	@Autowired
	private IBancaassuranceProposalHistoryService bancaassuranceProposalhistoryService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void insert(BancaassuranceProposal bancaassuranceProposal) {
		try {
			bancaassuranceProposalDAO.insert(bancaassuranceProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new bancaassuranceProposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(BancaassuranceProposal bancaassuranceProposal) {
		try {
			bancaassuranceProposalDAO.delete(bancaassuranceProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete a bancaassuranceProposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(BancaassuranceProposal bancaassuranceProposal) {
		try {
			bancaassuranceProposalDAO.update(bancaassuranceProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update a bancaassuranceProposal", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByLifeproposalId(String lifeProposalId) {
		BancaassuranceProposal result = null;
		try {
			result = bancaassuranceProposalDAO.findBancaassuranceProposalByLifeproposalId(lifeProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a bancaassuranceProposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByMedicalproposalId(String medicalProposalId) {
		BancaassuranceProposal result = null;
		try {
			result = bancaassuranceProposalDAO.findBancaassuranceProposalByMedicalproposalId(medicalProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a bancaassuranceProposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassuranceProposal findBancaassuranceProposalByPersonTravelproposalId(String personTravelProposalId) {
		BancaassuranceProposal result = null;
		try {
			result = bancaassuranceProposalDAO.findBancaassuranceProposalByPersonTravelproposalId(personTravelProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a bancaassuranceProposal", e);
		}
		return result;
	}

	@Override
	public BancaassuranceProposal findBancaassuranceProposalByTravelproposalId(String TravelProposalId) {
		BancaassuranceProposal result = null;
		try {
			result = bancaassuranceProposalDAO.findBancaassuranceProposalByTravelproposalId(TravelProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a bancaassuranceProposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposal CreateHistoryAndRemoveBancaassuranceProposalByLifeproposalId(String lifeProposalId) throws SystemException {
		BancaassuranceProposal result = null;
		try {
			result = bancaassuranceProposalDAO.findBancaassuranceProposalByLifeproposalId(lifeProposalId);
			BancaassuranceProposalHistory bancahistory = new BancaassuranceProposalHistory(result);
			bancahistory.setReferenceId(result.getId());
			bancaassuranceProposalHistoryDAO.insert(bancahistory);
			delete(result);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a bancaassuranceProposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposal CreateHistoryAndRemoveBancaassuranceProposalByPersonTravelproposalId(String personTravelProposalId) throws SystemException {
		BancaassuranceProposal result = null;
		try {
			result = bancaassuranceProposalDAO.findBancaassuranceProposalByPersonTravelProposalId(personTravelProposalId);
			BancaassuranceProposalHistory bancahistory = new BancaassuranceProposalHistory(result);
			bancaassuranceProposalhistoryService.addBancaassuranceProposalHistory(bancahistory);
			delete(result);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a bancaassuranceProposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaassuranceProposal CreateHistoryAndRemoveBancaassuranceProposalByMedicalproposalId(String medicalProposalId) throws SystemException {

		BancaassuranceProposal result = null;
		try {
			result = bancaassuranceProposalDAO.findBancaassuranceProposalByMedicalproposalId(medicalProposalId);
			BancaassuranceProposalHistory bancahistory = new BancaassuranceProposalHistory(result);
			bancahistory.setReferenceId(result.getId());
			bancaassuranceProposalHistoryDAO.insert(bancahistory);
			delete(result);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a bancaassuranceProposal", e);
		}
		return result;

	}
}
