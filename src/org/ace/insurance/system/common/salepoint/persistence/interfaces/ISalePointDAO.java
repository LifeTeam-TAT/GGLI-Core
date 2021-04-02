package org.ace.insurance.system.common.salepoint.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.java.component.persistence.exception.DAOException;

public interface ISalePointDAO {

	void insert(SalePoint salePoint) throws DAOException;

	void update(SalePoint salePoint) throws DAOException;

	void delete(SalePoint salePoint) throws DAOException;

	SalePoint findById(String id) throws DAOException;

	SalePoint findByCode(String code) throws DAOException;

	List<SalePoint> findAll() throws DAOException;

	List<SalePoint> findAllByStatus() throws DAOException;

	List<SalePoint> findAllSalePointByBrach(String branchId) throws DAOException;

	boolean isAlreadyExist(SalePoint salePoint) throws DAOException;

}