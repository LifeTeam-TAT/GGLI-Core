package org.ace.insurance.system.common.bancaLIC.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaLIC.persistence.interfaces.IBancaLICDAO;
import org.ace.insurance.system.common.bancaLIC.service.intefaces.IBancaLICService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("BancaLICService")
public class BancaLICService implements IBancaLICService {

	@Resource(name = "BancaLICDAO")
	private IBancaLICDAO bancaLICDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaLIC bancaLIC) {
		try {
			bancaLICDAO.insert(bancaLIC);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a BancaLIC", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaLIC bancaLIC) {
		try {
			bancaLICDAO.delete(bancaLIC);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a bancaLIC", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaLIC bancaLIC) {
		try {
			bancaLICDAO.update(bancaLIC);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a bancaLIC", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaLIC> findAllBancaLIC() {
		List<BancaLIC> result = null;
		try {
			result = bancaLICDAO.findAllBancaLIC();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a bancaLIC", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaLIC> findAllBancaLICActive() {
		List<BancaLIC> result = null;
		try {
			result = bancaLICDAO.findAllBancaLICActive();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a bancaLIC", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaLIC findByBancaLICId(String bancaLICId) {
		BancaLIC result = null;
		try {
			result = bancaLICDAO.findById(bancaLICId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find bancaLIC", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaLIC(BancaLIC bancaLIC) {

		return bancaLICDAO.checkExistingBancaLIC(bancaLIC);
	}

}
