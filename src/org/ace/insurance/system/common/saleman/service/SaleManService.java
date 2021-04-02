package org.ace.insurance.system.common.saleman.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.SaleManCriteria;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.saleman.history.SaleManHistory;
import org.ace.insurance.system.common.saleman.persistance.interfaces.ISalemanDAO;
import org.ace.insurance.system.common.saleman.service.interfaces.ISaleManService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "SaleManService")
public class SaleManService extends BaseService implements ISaleManService {

	@Resource(name = "SalemanDAO")
	private ISalemanDAO salemanDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewSaleMan(SaleMan saleMan) {
		try {
			saleMan.setPrefix(getPrefix(SaleMan.class));
			saleMan.setPrefix(getPrefix(SaleMan.class));
			salemanDAO.insert(saleMan);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new SaleMan", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSaleMan(SaleMan saleMan) {

		try {
			salemanDAO.update(saleMan);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a SaleMan", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteSaleMan(SaleMan saleMan) {
		try {
			salemanDAO.delete(saleMan);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a saleMan", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SaleMan findSaleManById(String id) {
		SaleMan result = null;
		try {
			result = salemanDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a SaleMan (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SaleMan> findAllSaleMan() {
		List<SaleMan> result = null;
		try {
			result = salemanDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SaleMan)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SaleMan> findSaleManByCriteria(SaleManCriteria saleManCriteria) {
		List<SaleMan> result = null;
		try {
			result = salemanDAO.findSaleManByCriteria(saleManCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all SaleMan by criteria)", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingSaleMan(SaleMan saleMan) {
		try {
			return salemanDAO.checkExistingSaleMan(saleMan);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a saleMan By Name", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewSaleManHistory(SaleManHistory saleManhistory) {
		try {

			salemanDAO.insertsaleManHistory(saleManhistory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new SaleMan", e);
		}

	}

}
