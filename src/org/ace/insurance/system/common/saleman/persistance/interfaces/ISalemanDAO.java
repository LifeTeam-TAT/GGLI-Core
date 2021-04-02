package org.ace.insurance.system.common.saleman.persistance.interfaces;

import java.util.List;

import org.ace.insurance.common.SaleManCriteria;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.saleman.history.SaleManHistory;
import org.ace.java.component.persistence.exception.DAOException;

public interface ISalemanDAO {
	public void insert(SaleMan saleMan) throws DAOException;

	public void insertsaleManHistory(SaleManHistory saleManhistory) throws DAOException;

	public void update(SaleMan saleMan) throws DAOException;

	public void delete(SaleMan saleMan) throws DAOException;

	public SaleMan findById(String id) throws DAOException;

	public List<SaleMan> findAll() throws DAOException;

	public List<SaleMan> findSaleManByCriteria(SaleManCriteria saleManCriteria) throws DAOException;

	public boolean checkExistingSaleMan(SaleMan saleMan) throws DAOException;
}
