package org.ace.insurance.system.common.bancaMethod.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.persistence.interfaces.IBancaMethodDAO;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("BancaMethodService")
public class BancaMethodService implements IBancaMethodService {

	@Resource(name = "BancaMethodDAO")
	private IBancaMethodDAO bancaDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaMethod banca) {
		try {
			bancaDAO.insert(banca);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a banca", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaMethod banca) {
		try {
			bancaDAO.delete(banca);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a banca", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaMethod banca) {
		try {
			bancaDAO.update(banca);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a banca", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaMethod findByBancaId(String bancaId) {
		BancaMethod result = null;
		try {
			result = bancaDAO.findById(bancaId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find banca", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaMethod> findAllBanca() {
		List<BancaMethod> result = null;
		try {
			result = bancaDAO.findAllBanca();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a bancamethod", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBanca(BancaMethod banca) {
		return bancaDAO.checkExistingBanca(banca);
	}

}
