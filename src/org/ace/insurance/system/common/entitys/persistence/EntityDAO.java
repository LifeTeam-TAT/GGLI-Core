package org.ace.insurance.system.common.entitys.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.entitys.persistence.interfaces.IEntityDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("EntityDAO")
public class EntityDAO extends BasicDAO implements IEntityDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Entitys entitys) {
		try {
			if (!checkExistingEntity(entitys)) {
				em.persist(entitys);
			} else {
				throw new SystemException(null, entitys.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert entitys (entityname = " + entitys.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Entitys entitys) {
		try {
			entitys = em.merge(entitys);
			em.remove(entitys);
		} catch (PersistenceException e) {
			throw translate("Failed to delete entitys User(entityname = " + entitys.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Entitys entitys) {
		try {
			em.merge(entitys);
		} catch (PersistenceException e) {
			throw translate("Failed to update entitys User(entityname = " + entitys.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Entitys> findAllEntitys() {
		List<Entitys> result = null;
		try {
			Query q = em.createQuery("select e from Entitys e ");
			result = q.getResultList();
		} catch (NoResultException pe) {
			return null;

		} catch (PersistenceException e) {
			throw translate("Failed to  find allss entitys ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingEntity(Entitys entitys) throws DAOException {
		boolean exist = false;
		String entitysName = entitys.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Entitys c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(entitys.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (entitys.getId() != null)
				query.setParameter("id", entitys.getId());
			query.setParameter("name", entitysName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

}
