package org.ace.insurance.system.common.bancaBranch.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaBranch.persistence.interfaces.IBancaBranchDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaBranchDAO")
public class BancaBranchDAO extends BasicDAO implements IBancaBranchDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaBranch bancaBranch) throws DAOException {
		try {
			if (!checkExistingBancaBranch(bancaBranch)) {
				em.persist(bancaBranch);
			} else {
				throw new SystemException(null, bancaBranch.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert bancaBranch (bancaBranchname = " + bancaBranch.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaBranch bancaBranch) throws DAOException {
		try {
			bancaBranch = em.merge(bancaBranch);
			em.remove(bancaBranch);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete bancaBranch User(bancaBranch name = " + bancaBranch.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaBranch bancaBranch) {
		try {
			em.merge(bancaBranch);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update bancaBranch User(bancaBranchname = " + bancaBranch.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaBranch> findAllBancaBranch() throws DAOException {
		List<BancaBranch> result = null;
		try {
			Query q = em.createQuery("select c from BancaBranch c ");
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all bancaBranch ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaBranch(BancaBranch bancaBranch) throws DAOException {
		boolean exist = false;
		String bancaBranchName = bancaBranch.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM BancaBranch b ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
			buffer.append(bancaBranch.getId() != null ? "AND b.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (bancaBranch.getId() != null)
				query.setParameter("id", bancaBranch.getId());
			query.setParameter("name", bancaBranchName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM BancaBranch b");
				buffer.append(" WHERE b.id != :id");
				buffer.append(" AND LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", bancaBranch.getId());
				query.setParameter("name", bancaBranch.getName());
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
	public BancaBranch findById(String BancaBranchId) throws DAOException {
		BancaBranch result = null;
		try {
			result = em.find(BancaBranch.class, BancaBranchId);
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find bancaBranch", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaBranch> findAllBancaBranchByChannel(String channelId) throws DAOException {
		List<BancaBranch> result = null;
		try {
			Query q = em.createQuery("SELECT  b  FROM  BancaBranch b   where b.channel.id = :channelId order by b.name asc");
			q.setParameter("channelId", channelId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of BancaBranch", pe);
		}
		return result;
	}
}
