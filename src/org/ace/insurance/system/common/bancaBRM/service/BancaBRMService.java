package org.ace.insurance.system.common.bancaBRM.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.bancaBRM.BRM001;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBRM.persistence.interfaces.IBancaBRMDAO;
import org.ace.insurance.system.common.bancaBRM.service.interfaces.IBancaBRMService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("BancaBRMService")
public class BancaBRMService implements IBancaBRMService {

	@Resource(name = "BancaBRMDAO")
	private IBancaBRMDAO bancaBRMDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaBRM bancaBRM) {
		try {
			bancaBRMDAO.insert(bancaBRM);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a bancaBRM", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaBRM bancaBRM) {
		try {
			bancaBRMDAO.delete(bancaBRM);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a bancaBRM", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaBRM bancaBRM) {
		try {
			bancaBRMDAO.update(bancaBRM);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a bancaBRM", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaBRM> findAllBancaBRM() {
		List<BancaBRM> result = null;
		try {
			result = bancaBRMDAO.findAllBancaBRM();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a bancaBRM", e);
		}
		return result;
	}

	@Override
	public BancaBRM findByBancaBRMId(String bancaBRMId) {
		BancaBRM result = null;
		try {
			result = bancaBRMDAO.findById(bancaBRMId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find bancaBRM", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaBRM(BancaBRM bancaBRM) {

		return bancaBRMDAO.checkExistingBancaBRM(bancaBRM);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRM001> findAll_BRM001() {
		List<BRM001> result = null;
		try {
			result = bancaBRMDAO.findAll_BRM001();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find bancaBRM", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRM001> findByBranchId(String branchId) throws DAOException {
		List<BRM001> result = null;
		try {
			result = bancaBRMDAO.findByBranchId(branchId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find bancaBRM", e);
		}
		return result;

	}
}
