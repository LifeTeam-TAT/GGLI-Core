/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.relationship.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.relationship.RELATION001;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.persistence.interfaces.IRelationShipDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("RelationShipDAO")
public class RelationShipDAO extends BasicDAO implements IRelationShipDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(RelationShip relationShip) throws DAOException {
		try {
			em.persist(relationShip);
			insertProcessLog(TableName.RELATIONSHIP, relationShip.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert RelationShip", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(RelationShip relationShip) throws DAOException {
		try {
			em.merge(relationShip);
			updateProcessLog(TableName.RELATIONSHIP, relationShip.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update RelationShip", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(RelationShip relationShip) throws DAOException {
		try {
			relationShip = em.merge(relationShip);
			em.remove(relationShip);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update RelationShip", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public RelationShip findById(String id) throws DAOException {
		RelationShip result = null;
		try {
			Query q = em.createNamedQuery("RelationShip.findById");
			q.setParameter("id", id);
			result = (RelationShip) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find RelationShip(RelationShip = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RelationShip> findAll() throws DAOException {
		List<RelationShip> result = null;
		try {
			Query q = em.createNamedQuery("RelationShip.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of RelationShip", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RelationShip> findByCriteria(String criteria) throws DAOException {
		List<RelationShip> result = null;
		try {
			// Query q = em.createNamedQuery("RelationShip.findByCriteria");
			Query q = em.createQuery("Select t from RelationShip t where t.name Like '" + criteria + "%'");
			// q.setParameter("criteriaValue", "%" + criteria + "%");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of RelationShip.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RELATION001> findRelationship() throws DAOException {
		List<RELATION001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW " + RELATION001.class.getName() + " (r.id, r.name, r.description) FROM RelationShip r ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of RelationShip", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistRelationShip(RelationShip relationShip) throws DAOException {
		boolean exist = false;
		String relationShipName = relationShip.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM RelationShip c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(relationShip.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (relationShip.getId() != null)
				query.setParameter("id", relationShip.getId());
			query.setParameter("name", relationShipName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

}
