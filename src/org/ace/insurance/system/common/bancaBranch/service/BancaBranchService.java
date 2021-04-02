package org.ace.insurance.system.common.bancaBranch.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaBranch.persistence.interfaces.IBancaBranchDAO;
import org.ace.insurance.system.common.bancaBranch.service.interfaces.IBancaBranchService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("BancaBranchService")
public class BancaBranchService implements IBancaBranchService {

	@Resource(name = "BancaBranchDAO")
	private IBancaBranchDAO bancaBranchDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaBranch bancaBranch) {
		try {
			bancaBranchDAO.insert(bancaBranch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a bancaBranch", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaBranch bancaBranch) {
		try {
			bancaBranchDAO.delete(bancaBranch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a bancaBranch", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaBranch bancaBranch) {
		try {
			bancaBranchDAO.update(bancaBranch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a bancaBranch", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaBranch> findAllBancaBranch() {
		List<BancaBranch> result = null;
		try {
			result = bancaBranchDAO.findAllBancaBranch();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a bancaBranch", e);
		}
		return result;
	}

	@Override
	public BancaBranch findByBancaBranchId(String bancaBranchId) {
		BancaBranch result = null;
		try {
			result = bancaBranchDAO.findById(bancaBranchId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find bancaBranch", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaBranch(BancaBranch bancaBranch) {

		return bancaBranchDAO.checkExistingBancaBranch(bancaBranch);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaBranch> findAll() throws SystemException {
		try {
			return bancaBranchDAO.findAllBancaBranch();
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of BancaBranch", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaBranch> findAllBancaBranchByChannel(String channelId) {
		try {
			return bancaBranchDAO.findAllBancaBranchByChannel(channelId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a BancaBranch ");
		}

	}

}
