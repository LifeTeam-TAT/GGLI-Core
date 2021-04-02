package org.ace.insurance.system.common.bancaBRM.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaBRM.BRM001;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaBRMService {

	public void insert(BancaBRM bancaBRM);

	public void delete(BancaBRM bancaBRM);

	public void update(BancaBRM bancaBRM);

	public BancaBRM findByBancaBRMId(String bancaBRMId);

	public List<BancaBRM> findAllBancaBRM();

	public List<BRM001> findAll_BRM001();

	public boolean checkExistingBancaBRM(BancaBRM bancaBRM);

	public List<BRM001> findByBranchId(String branchId) throws DAOException;

}
