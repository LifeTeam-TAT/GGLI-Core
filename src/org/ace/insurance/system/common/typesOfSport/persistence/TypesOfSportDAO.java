/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.typesOfSport.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.typesOfSport.TSP001;
import org.ace.insurance.system.common.typesOfSport.TypesOfSport;
import org.ace.insurance.system.common.typesOfSport.persistence.interfaces.ITypesOfSportDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("TypesOfSportDAO")
public class TypesOfSportDAO extends BasicDAO implements ITypesOfSportDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(TypesOfSport typesOfSport) throws DAOException {
		try {
			if (!isAlreadyExistTypesOfSport(typesOfSport)) {
				em.persist(typesOfSport);
				insertProcessLog(TableName.TYPESOFSPORT, typesOfSport.getId());
				em.flush();
			} else {
				throw new SystemException(null, typesOfSport.getName() + "is Already Exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert TypesOfSport", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(TypesOfSport typesOfSport) throws DAOException {
		try {
			if (!isAlreadyExistTypesOfSport(typesOfSport)) {
				em.merge(typesOfSport);
				updateProcessLog(TableName.TYPESOFSPORT, typesOfSport.getId());
				em.flush();
			} else {
				throw new SystemException(null, typesOfSport.getName() + "is Already Exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update TypesOfSport", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(TypesOfSport typesOfSport) throws DAOException {
		try {
			typesOfSport = em.merge(typesOfSport);
			em.remove(typesOfSport);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update TypesOfSport", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TypesOfSport findById(String id) throws DAOException {
		TypesOfSport result = null;
		try {
			result = em.find(TypesOfSport.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find TypesOfSport", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TypesOfSport> findAll() throws DAOException {
		List<TypesOfSport> result = null;
		try {
			Query q = em.createNamedQuery("TypesOfSport.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of TypesOfSport", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TypesOfSport> findByCriteria(String criteria, int max) throws DAOException {
		List<TypesOfSport> result = null;
		try {
			// Query q = em.createNamedQuery("Township.findByCriteria");
			Query q = em.createQuery("Select t from TypesOfSport t where t.name Like '" + criteria + "%'");
			// q.setParameter("criteriaValue", "%" + criteria + "%");
			q.setMaxResults(max);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of TypesOfSport.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TSP001> findTypesOfSport() throws DAOException {
		List<TSP001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW " + TSP001.class.getName() + " (t.id, t.name, t.description) FROM TypesOfSport t ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of TypesOfSport", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistTypesOfSport(TypesOfSport typesOfSport) throws DAOException {
		boolean exist = false;
		String typesOfSportName = typesOfSport.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM TypesOfSport c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(typesOfSport.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (typesOfSport.getId() != null)
				query.setParameter("id", typesOfSport.getId());
			query.setParameter("name", typesOfSportName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

}
