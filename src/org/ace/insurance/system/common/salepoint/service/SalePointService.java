package org.ace.insurance.system.common.salepoint.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.persistence.interfaces.ISalePointDAO;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "SalePointService")
public class SalePointService extends BaseService implements ISalePointService {

	@Resource(name = "SalePointDAO")
	private ISalePointDAO salePointDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewSalePoint(SalePoint salePoint) {
		try {
			salePointDAO.insert(salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new SalePoint", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSalePoint(SalePoint salePoint) {
		try {
			salePointDAO.update(salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a SalePoint", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSalePoint(SalePoint salePoint) {
		try {
			salePointDAO.delete(salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a SalePoint", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SalePoint> findAllSalePoint() {
		List<SalePoint> result = null;
		try {
			result = salePointDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SalePoint)", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SalePoint> findAllSalePointByStatus() {
		List<SalePoint> result = null;
		try {
			result = salePointDAO.findAllByStatus();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SalePoint)", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public SalePoint findSalePointById(String id) {
		SalePoint result = null;
		try {
			result = salePointDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a SalePoint (ID : " + id + ")", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public SalePoint findSalePointByCode(String branchCode) {
		SalePoint result = null;
		try {
			result = salePointDAO.findByCode(branchCode);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a SalePoint (branchCode : " + branchCode + ")", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExist(SalePoint salePoint) {
		try {
			return salePointDAO.isAlreadyExist(salePoint);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a SalePoint ");
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SalePoint> findAllSalePointByBranch(String branchId) {
		try {
			return salePointDAO.findAllSalePointByBrach(branchId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a SalePoint ");
		}

	}

}