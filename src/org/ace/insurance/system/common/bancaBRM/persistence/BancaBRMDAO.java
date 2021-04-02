package org.ace.insurance.system.common.bancaBRM.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.system.common.bancaBRM.BRM001;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBRM.persistence.interfaces.IBancaBRMDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaBRMDAO")
public class BancaBRMDAO extends BasicDAO implements IBancaBRMDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaBRM bancaBRM) throws DAOException {
		try {
			if (!checkExistingBancaBRM(bancaBRM)) {
				em.persist(bancaBRM);
			} else {
				throw new SystemException(null, bancaBRM.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert bancaBRM (bancaBRMname = " + bancaBRM.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaBRM bancaBRM) throws DAOException {
		try {
			bancaBRM = em.merge(bancaBRM);
			em.remove(bancaBRM);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete bancaBRM User(bancaBRM name = " + bancaBRM.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaBRM bancaBRM) {
		try {
			em.merge(bancaBRM);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update bancaBRM User(bancaBRMname = " + bancaBRM.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaBRM> findAllBancaBRM() throws DAOException {
		List<BancaBRM> result = null;
		try {
			Query q = em.createQuery("select b from BancaBRM b ");
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all bancaBRM ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaBRM(BancaBRM bancaBRM) throws DAOException {
		boolean exist = false;
		String bancaBRMName = bancaBRM.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM BancaBRM b ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
			buffer.append(bancaBRM.getId() != null ? "AND b.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (bancaBRM.getId() != null)
				query.setParameter("id", bancaBRM.getId());
			query.setParameter("name", bancaBRMName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM BancaBRM b");
				buffer.append(" WHERE b.id != :id");
				buffer.append(" AND LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", bancaBRM.getId());
				query.setParameter("name", bancaBRM.getName());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaBRM findById(String BancaBRMId) throws DAOException {
		BancaBRM result = null;
		try {
			result = em.find(BancaBRM.class, BancaBRMId);
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find bancaBRM", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BRM001> findByBranchId(String branchId) throws DAOException {
		List<BRM001> result = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT NEW " + BRM001.class.getName());
			buffer.append("(s.id, s.name, s.remark) from BancaBRM s inner join s.brmBranchInfoList info where info.bancaBranch.id =:branchId");
			TypedQuery<BRM001> query = em.createQuery(buffer.toString(), BRM001.class);
			query.setParameter("branchId", branchId);
			result = query.getResultList();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find bancaBRM", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<BRM001> findAll_BRM001() throws DAOException {
		List<BRM001> result = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT NEW " + BRM001.class.getName());
			buffer.append("(b.id, b.name, b.remark) FROM  BancaBRM b ORDER BY b.name ASC");
			TypedQuery<BRM001> query = em.createQuery(buffer.toString(), BRM001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all BRM001.", pe);
		}
		return result;
	}

}
