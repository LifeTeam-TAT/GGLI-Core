package org.ace.insurance.system.common.bancaMethod.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.persistence.interfaces.IBancaMethodDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaMethodDAO")
public class BancaMethodDAO extends BasicDAO implements IBancaMethodDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaMethod banca) throws DAOException {
		try {
			if (!checkExistingBanca(banca)) {
				em.persist(banca);
			} else {
				throw new SystemException(null, banca.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert channel (channelname = " + banca.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaMethod banca) throws DAOException {
		try {
			banca = em.merge(banca);
			em.remove(banca);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete channel User(channel name = " + banca.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaMethod banca) throws DAOException {
		try {
			em.merge(banca);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update channel User(banamethodname = " + banca.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaMethod findById(String BancaId) throws DAOException {
		BancaMethod result = null;
		try {
			result = em.find(BancaMethod.class, BancaId);
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find bancamethod", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaMethod> findAllBanca() {
		List<BancaMethod> result = null;
		try {
			Query q = em.createQuery("select b from BancaMethod b ");
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all bancamethod ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBanca(BancaMethod banca) throws DAOException {
		boolean exist = false;
		String bancaName = banca.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM BancaMethod b ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
			buffer.append(banca.getId() != null ? "AND b.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (banca.getId() != null)
				query.setParameter("id", banca.getId());
			query.setParameter("name", bancaName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM BancaMethod b");
				buffer.append(" WHERE b.id != :id");
				buffer.append(" AND LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", banca.getId());
				query.setParameter("name", banca.getName());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

}
