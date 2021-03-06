package org.ace.java.component.idgen.persistence;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.java.component.idgen.IDGen;
import org.ace.java.component.idgen.persistence.interfaces.IDGenDAOInf;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("IDGenDAO")
public class IDGenDAO extends BasicDAO implements IDGenDAOInf {

	@Resource(name = "IDConfigLoader")
	protected IDConfigLoader configLoader;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public IDGen getNextId(String generateItem, boolean isIgnoreBranch) throws DAOException {
		IDGen idGen = null;
		String branchCode = "";
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT g FROM IDGen g WHERE g.generateItem = :generateItem");
			if (!isIgnoreBranch) {
				query.append(" AND g.branch.id IN (SELECT b.id FROM Branch b Where b.branchCode = :branchCode)");
			} else {
				query.append(" AND g.branch IS NULL ");
			}
			Query selectQuery = em.createQuery(query.toString());
			selectQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);
			selectQuery.setHint("javax.persistence.lock.timeout", 30000);
			if (configLoader.isCentralizedSystem()) {
				branchCode = userProcessService.getLoginUser().getBranch().getBranchCode();
			} else {
				branchCode = configLoader.getBranchCode();
			}
			selectQuery.setParameter("generateItem", generateItem);
			if (!isIgnoreBranch) {
				selectQuery.setParameter("branchCode", branchCode);
			}

			idGen = (IDGen) selectQuery.getSingleResult();

			idGen.setMaxValue(idGen.getMaxValue() + 1);
			idGen = em.merge(idGen);
			em.flush();
		} catch (NoResultException e) {
			throw translate("There is no Result " + generateItem, e);
		} catch (PersistenceException pe) {
			throw translate("Failed to update ID Generation for " + generateItem, pe);
		}
		return idGen;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public IDGen getNextId(String generateItem, Branch branch, boolean isIgnoreBranch) throws DAOException {
		IDGen idGen = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT g FROM IDGen g WHERE g.generateItem = :generateItem");
			if (!isIgnoreBranch) {
				query.append(" AND g.branch.id IN (SELECT b.id FROM Branch b Where b.branchCode = :branchCode)");
			} else {
				query.append(" AND g.branch IS NULL ");
			}

			Query selectQuery = em.createQuery(query.toString());
			selectQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);
			selectQuery.setHint("javax.persistence.lock.timeout", 30000);
			selectQuery.setParameter("generateItem", generateItem);
			if (!isIgnoreBranch) {
				selectQuery.setParameter("branchCode", branch.getBranchCode());
			}
			idGen = (IDGen) selectQuery.getSingleResult();
			idGen.setMaxValue(idGen.getMaxValue() + 1);
			idGen = em.merge(idGen);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update ID Generation for " + generateItem, pe);
		}
		return idGen;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public IDGen getIDGen(String generateItem, boolean isIgnoreBranch) throws DAOException {
		IDGen idGen = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT g FROM IDGen g WHERE g.generateItem = :generateItem");
			if (!isIgnoreBranch) {
				query.append(" AND g.branch.id IN (SELECT b.id FROM Branch b Where b.branchCode = :branchCode)");
			} else {
				query.append(" AND g.branch IS NULL ");
			}

			Query selectQuery = em.createQuery(query.toString());
			selectQuery.setParameter("generateItem", generateItem);
			if (!isIgnoreBranch) {
				selectQuery.setParameter("branchCode", configLoader.getBranchCode());
			}
			idGen = (IDGen) selectQuery.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Max Value for " + generateItem, pe);
		}
		return idGen;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public IDGen getIDGenForAutoRenewal(String generateItem) throws DAOException {
		IDGen idGen = null;
		try {
			Query selectQuery = em.createQuery("SELECT g FROM IDGen g WHERE g.generateItem = :generateItem");
			selectQuery.setParameter("generateItem", generateItem);
			idGen = (IDGen) selectQuery.getSingleResult();
			idGen.setMaxValue(idGen.getMaxValue() + 1);
			idGen = em.merge(idGen);
			em.flush();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Max Value for " + generateItem, pe);
		}
		return idGen;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public IDGen updateIDGen(IDGen idGen) throws DAOException {
		try {
			idGen = em.merge(idGen);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update IDGen ", pe);
		}
		return idGen;
	}
}
