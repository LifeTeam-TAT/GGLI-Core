/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.qualification.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.qualification.persistence.interfaces.IQualificationDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("QualificationDAO")
public class QualificationDAO extends BasicDAO implements IQualificationDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Qualification qualification) throws DAOException {
		try {
			em.persist(qualification);
			insertProcessLog(TableName.QUALIFICATION, qualification.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Qualification", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Qualification qualification) throws DAOException {
		try {
			em.merge(qualification);
			updateProcessLog(TableName.QUALIFICATION, qualification.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Qualification", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Qualification qualification) throws DAOException {
		try {
			qualification = em.merge(qualification);
			em.remove(qualification);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Qualification", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Qualification findById(String id) throws DAOException {
		Qualification result = null;
		try {
			result = em.find(Qualification.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Qualification", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Qualification> findAll() throws DAOException {
		List<Qualification> result = null;
		try {
			Query q = em.createNamedQuery("Qualification.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Qualification", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Qualification> findByCriteria(String criteria) throws DAOException {
		List<Qualification> result = null;
		try {
			// Query q = em.createNamedQuery("Qualification.findByCriteria");
			Query q = em.createQuery("Select t from Qualification t where t.name Like '" + criteria + "%'");
			// q.setParameter("criteriaValue", "%" + criteria + "%");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Qualification.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExist(Qualification qualification) throws DAOException {
		boolean exist = false;
		String qualificationName = qualification.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Qualification c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(qualification.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (qualification.getId() != null)
				query.setParameter("id", qualification.getId());
			query.setParameter("name", qualificationName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

}
