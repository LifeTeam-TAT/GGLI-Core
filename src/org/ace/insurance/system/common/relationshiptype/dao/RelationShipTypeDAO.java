package org.ace.insurance.system.common.relationshiptype.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.entitys.persistence.interfaces.IEntityDAO;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.dao.interfaces.IRelationShipTypeDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("RelationShipTypeDAO")
public class RelationShipTypeDAO extends BasicDAO implements IRelationShipTypeDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(RelationShipType relationShipType) {
		try {
			if (!checkExistingrelationShipType(relationShipType)) {
				em.persist(relationShipType);
			} else {
				throw new SystemException(null, relationShipType.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert entitys (relationShipType = " + relationShipType.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(RelationShipType relationShipType) {
		try {
			relationShipType = em.merge(relationShipType);
			em.remove(relationShipType);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete entitys User(relationShipType = " + relationShipType.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(RelationShipType relationShipType) {
		try {
			em.merge(relationShipType);
		} catch (PersistenceException e) {
			throw translate("Failed to update entitys User(relationShipType = " + relationShipType.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<RelationShipType> findAllRelationShipType() {
		List<RelationShipType> result = null;
		try {
			Query q = em.createQuery("select e from RelationShipType e ");
			result = q.getResultList();
		} catch (NoResultException pe) {
			return null;

		} catch (PersistenceException e) {
			throw translate("Failed to  find allss RelationShipType ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingrelationShipType(RelationShipType relationShipType) throws DAOException {
		boolean exist = false;
		String relationShipTypeName = relationShipType.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Entitys c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(relationShipType.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (relationShipType.getId() != null)
				query.setParameter("id", relationShipType.getId());
			query.setParameter("name", relationShipTypeName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

}
