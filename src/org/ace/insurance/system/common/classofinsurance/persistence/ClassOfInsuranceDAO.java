package org.ace.insurance.system.common.classofinsurance.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.classofinsurance.ClassOfInsurance;
import org.ace.insurance.system.common.classofinsurance.persistence.interfaces.IClassOfInsuranceDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ClassOfInsuranceDAO")
public class ClassOfInsuranceDAO extends BasicDAO implements IClassOfInsuranceDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(ClassOfInsurance classofinsurance) throws DAOException {
		try {
			if (!isAlreadyExistClassOfInsurance(classofinsurance)) {
				em.persist(classofinsurance);
				insertProcessLog(TableName.CLASSOFINSURANCE, classofinsurance.getId());
				em.flush();
			} else {
				throw new SystemException(null, classofinsurance.getName() + "is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert ClassOfInsurance", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(ClassOfInsurance classofinsurance) throws DAOException {
		try {
			if (!isAlreadyExistClassOfInsurance(classofinsurance)) {
				em.merge(classofinsurance);
				updateProcessLog(TableName.CLASSOFINSURANCE, classofinsurance.getId());
				em.flush();
			} else {
				throw new SystemException(null, classofinsurance.getName() + "is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update ClassOfInsurance", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ClassOfInsurance classofinsurance) throws DAOException {
		try {
			classofinsurance = em.merge(classofinsurance);
			em.remove(classofinsurance);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete ClassOfInsurance", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ClassOfInsurance findById(String id) throws DAOException {
		ClassOfInsurance result = null;
		try {
			result = em.find(ClassOfInsurance.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find ClassOfInsurance", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClassOfInsurance> findAll() throws DAOException {
		List<ClassOfInsurance> result = null;
		try {
			Query q = em.createNamedQuery("ClassOfInsurance.findAll");
			result = q.getResultList();
			q.setMaxResults(50);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ClassOfInsurance", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ClassOfInsurance> findByCriteria(String criteria) throws DAOException {
		List<ClassOfInsurance> result = null;
		try {
			// Query q = em.createNamedQuery("Township.findByCriteria");
			Query q = em.createQuery("Select c from ClassOfInsurance c where c.name Like '" + criteria + "%'");
			// q.setParameter("criteriaValue", "%" + criteria + "%");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of ClassOfInsurance.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistClassOfInsurance(ClassOfInsurance classofinsurance) throws DAOException {
		boolean exist = false;
		String classofinsuranceName = classofinsurance.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM ClassOfInsurance c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(classofinsurance.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (classofinsurance.getId() != null)
				query.setParameter("id", classofinsurance.getId());
			query.setParameter("name", classofinsuranceName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

}
