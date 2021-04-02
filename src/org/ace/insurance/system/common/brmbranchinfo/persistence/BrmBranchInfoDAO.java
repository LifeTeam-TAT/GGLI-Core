package org.ace.insurance.system.common.brmbranchinfo.persistence;

import javax.persistence.PersistenceException;

import org.ace.insurance.system.common.brmbranchinfo.BrmBranchInfo;
import org.ace.insurance.system.common.brmbranchinfo.persistence.interfaces.IBrmBranchInfoDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BrmBranchInfoDAO")
public class BrmBranchInfoDAO extends BasicDAO implements IBrmBranchInfoDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BrmBranchInfo brmBranchInfo) throws DAOException {
		try {

			em.persist(brmBranchInfo);

		} catch (PersistenceException e) {
			throw translate("Failed to insert brmBranchInfo", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BrmBranchInfo brmBranchInfo) throws DAOException {
		try {
			brmBranchInfo = em.merge(brmBranchInfo);
			em.remove(brmBranchInfo);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete brmBranchInfo", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BrmBranchInfo brmBranchInfo) throws DAOException {
		try {
			em.merge(brmBranchInfo);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update brmBranchInfo", e);
		}
	}

}
