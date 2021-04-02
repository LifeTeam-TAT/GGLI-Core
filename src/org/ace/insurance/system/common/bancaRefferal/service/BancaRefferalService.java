package org.ace.insurance.system.common.bancaRefferal.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.bancaRefferal.persistence.interfaces.IBancaRefferalDAO;
import org.ace.insurance.system.common.bancaRefferal.service.interfaces.IBancaRefferalService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("BancaRefferalService")
public class BancaRefferalService implements IBancaRefferalService {

	@Resource(name = "BancaRefferalDAO")
	private IBancaRefferalDAO bancarefferalDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaRefferal bancarefferal) {
		try {
			bancarefferalDAO.insert(bancarefferal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a bancarefferal", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaRefferal bancarefferal) {
		try {
			bancarefferalDAO.delete(bancarefferal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a bancarefferal", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaRefferal bancarefferal) {
		try {
			bancarefferalDAO.update(bancarefferal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a bancarefferal", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaRefferal findByBancaId(String bancarefferalId) {
		BancaRefferal result = null;
		try {
			result = bancarefferalDAO.findById(bancarefferalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find bancarefferalId", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancaRefferal() {
		List<BancaRefferal> result = null;
		try {
			result = bancarefferalDAO.findAllBancarefferal();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BancaRefferal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancaRefferalByAgentLicenseNo() {
		List<BancaRefferal> result = null;
		try {
			result = bancarefferalDAO.findAllBancaRefferalByAgentLicenseNo();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BancaRefferal", e);
		}
		return result;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancaRefferalActive(String branchId) {
		List<BancaRefferal> result = null;
		try {
			result = bancarefferalDAO.findAllBancarefferalActive(branchId);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BancaRefferal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancaRefferalByOTC(String branchId) throws SystemException {
		List<BancaRefferal> result = null;
		try {
			result = bancarefferalDAO.findAllBancarefferalByOTC(branchId);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BancaRefferalByOTC", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaRefferal(BancaRefferal bancarefferal) {
		return bancarefferalDAO.checkExistingBancaRefferal(bancarefferal);
	}
}
