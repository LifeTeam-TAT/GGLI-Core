package org.ace.insurance.life.bancassurance.policy.service;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.ace.insurance.life.bancassurance.policy.BancaassurancePolicy;
import org.ace.insurance.life.bancassurance.policy.persistence.interfaces.IBancaassurancePolicyDAO;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "BancaassurancePolicyService")
public class BancaassurancePolicyService implements IBancaassurancePolicyService {

	@Resource(name = "BancaassurancePolicyDAO")
	private IBancaassurancePolicyDAO bancaassurancePolicyDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void insert(BancaassurancePolicy bancaassurancePolicy) {
		try {
			bancaassurancePolicyDAO.insert(bancaassurancePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new bancaassuranceProposal", e);
		} // TODO Auto-generated method stub
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(BancaassurancePolicy bancaassurancePolicy) {
		try {
			bancaassurancePolicyDAO.delete(bancaassurancePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new bancaassuranceProposal", e);
		} // TODO Auto-generated method stub// TODO Auto-generated method stub

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(BancaassurancePolicy bancaassurancePolicy) {
		try {
			bancaassurancePolicyDAO.update(bancaassurancePolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new bancaassuranceProposal", e);
		} // TODO Auto-generated method stub
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByLifepolicyId(String lifePolicyId) {
		BancaassurancePolicy result = null;
		try {
			result = bancaassurancePolicyDAO.findBancaassurancePolicyByLifepolicyId(lifePolicyId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassurancePolicy", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByMedicalpolicyId(String medicalPolicyId) {
		BancaassurancePolicy result = null;
		try {
			result = bancaassurancePolicyDAO.findBancaassurancePolicyByMedicalpolicyId(medicalPolicyId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassurancePolicy", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByPersonTravelpolicyId(String personTravelPolicyId) {
		BancaassurancePolicy result = null;
		try {
			result = bancaassurancePolicyDAO.findBancaassurancePolicyByPersonTravelpolicyId(personTravelPolicyId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassurancePolicy", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BancaassurancePolicy findBancaassurancePolicyByTravelproposalId(String TravelProposalId) {
		BancaassurancePolicy result = null;
		try {
			result = bancaassurancePolicyDAO.findBancaassurancePolicyByTravelproposalId(TravelProposalId);
		} catch (NoResultException e) {
			return null;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a BancaassurancePolicy", e);
		}
		return result;
	}
}
