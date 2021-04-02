/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.occupation.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.occupation.OCCUPATION001;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.persistence.interfaces.IOccupationDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("OccupationDAO")
public class OccupationDAO extends BasicDAO implements IOccupationDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Occupation occupation) throws DAOException {
		try {
			em.persist(occupation);
			insertProcessLog(TableName.OCCUPATION, occupation.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Occupation", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Occupation occupation) throws DAOException {
		try {
			em.merge(occupation);
			updateProcessLog(TableName.OCCUPATION, occupation.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Occupation", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Occupation occupation) throws DAOException {
		try {
			occupation = em.merge(occupation);
			em.remove(occupation);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Occupation", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Occupation findById(String id) throws DAOException {
		Occupation result = null;
		try {
			Query q = em.createNamedQuery("Occupation.findById");
			q.setParameter("id", id);
			result = (Occupation) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Occupation(Occupation = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Occupation> findByInsuranceType(InsuranceType insuranceType) throws DAOException {
		List<Occupation> result = null;
		try {
			Query q = em.createNamedQuery("Occupation.findByInsuranceType");
			q.setParameter("insuranceType", insuranceType);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Occupation by Insurance Type", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Occupation> findAll() throws DAOException {
		List<Occupation> result = null;
		try {
			Query q = em.createNamedQuery("Occupation.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Occupation", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Occupation> findByCriteria(String criteria) throws DAOException {
		List<Occupation> result = null;
		try {
			// Query q = em.createNamedQuery("Occupation.findByCriteria");
			Query q = em.createQuery("Select t from Occupation t where t.name Like '" + criteria + "%'");
			// q.setParameter("criteriaValue", "%" + criteria + "%");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Occupation.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<OCCUPATION001> findOccupationByInsuranceType(InsuranceType insuranceType) throws DAOException {
		List<OCCUPATION001> result = null;
		try {
			Query q = em.createNamedQuery("Occupation.findByInsuranceType");
			q.setParameter("insuranceType", insuranceType);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Occupation by Insurance Type", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<OCCUPATION001> findOccupation() throws DAOException {
		List<OCCUPATION001> result = null;
		try {
			TypedQuery<OCCUPATION001> query = em.createQuery("SELECT NEW " + OCCUPATION001.class.getName() + " (o.id, o.name, o.description) FROM Occupation o ",
					OCCUPATION001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Occupation", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<OCCUPATION001> findOccupationByCriteria(String criteria) throws DAOException {
		List<OCCUPATION001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + OCCUPATION001.class.getName());
			buffer.append("(t.id,t.name,t.description)");
			buffer.append(" FROM Occupation t where t.name Like '" + criteria + "%'");
			Query q = em.createQuery(buffer.toString());
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Occupation.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistOccupation(Occupation occupation) throws DAOException {
		boolean exist = false;
		String occupationName = occupation.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Occupation c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(occupation.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (occupation.getId() != null)
				query.setParameter("id", occupation.getId());
			query.setParameter("name", occupationName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

}
