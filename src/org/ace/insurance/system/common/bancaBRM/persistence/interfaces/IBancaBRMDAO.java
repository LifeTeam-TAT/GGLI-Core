package org.ace.insurance.system.common.bancaBRM.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaBRM.BRM001;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaBRMDAO {

	public void insert(BancaBRM bancaBRM) throws DAOException;

	public void delete(BancaBRM bancaBRM) throws DAOException;

	public void update(BancaBRM bancaBRM) throws DAOException;

	public BancaBRM findById(String BancaBRMlId) throws DAOException;

	public List<BancaBRM> findAllBancaBRM();

	public List<BRM001> findAll_BRM001() throws DAOException;

	public boolean checkExistingBancaBRM(BancaBRM bancaBRM) throws DAOException;

	public List<BRM001> findByBranchId(String branchId) throws DAOException;

}
