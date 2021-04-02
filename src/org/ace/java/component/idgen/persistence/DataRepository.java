package org.ace.java.component.idgen.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.persistence.interfaces.IDataRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("DataRepository")
public class DataRepository<T> extends BasicDAO implements IDataRepository<T> {

	/**
	 * Basic Insert Operation using EM
	 * 
	 * @param object
	 *            -> object to insert
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Object object) throws DAOException {
		try {
			em.persist(object);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert " + object.getClass().getName(), pe);
		}
	}

	/**
	 * Basic Update Operation using EM
	 * 
	 * @param param
	 *            -> Object to update
	 * @return updated object
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public T update(T param) throws DAOException {
		try {
			param = em.merge(param);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update " + param.getClass().getName(), pe);
		}
		return param;
	}

	/**
	 * Basic Delete Operation using EM
	 * 
	 * @param object
	 *            -> Object to delete
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Object object) throws DAOException {
		try {
			object = em.merge(object);
			em.remove(object);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete " + object.getClass().getName(), pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteById(Class<T> paramClass, String id) throws DAOException {
		try {
			T object = findById(paramClass, id);
			delete(object);
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to delete " + paramClass.getName(), pe);
		}
	}

	/**
	 * Basic Find Operation using EM
	 * 
	 * @param paramClass
	 *            -> class name
	 * @param paramObject
	 *            -> primary key value of enity object
	 * @return result value
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public T findById(Class<T> paramClass, Object paramObject) throws DAOException {
		T result = null;
		try {
			result = em.find(paramClass, paramObject);
			em.flush();
		} catch (NoResultException nre) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find By id " + paramObject.getClass().getName(), pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findByParentId(Class<T> paramClass, String parentId) throws DAOException {
		List<T> result = null;
		try {
			Query query = em.createNamedQuery(paramClass.getSimpleName() + ".findByParentId");
			query.setParameter("parentId", parentId);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find By Parent id " + parentId, pe);
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkCodeNo(Class<T> paramClass, String codeNo) throws DAOException {
		boolean valid;
		try {
			Query query = em.createNamedQuery(paramClass.getSimpleName() + ".checkCode");
			query.setParameter("codeNo", codeNo);
			List<String> code = query.getResultList();
			if (code.size() > 0) {
				valid = false;
			} else {
				valid = true;
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to check code No " + codeNo + " is valid or not.", pe);
		}
		return valid;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findAll(Class<T> paramClass) throws DAOException {
		List<T> result = null;
		try {
			Query q = em.createNamedQuery(paramClass.getSimpleName() + ".findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of " + paramClass.getSimpleName() + "List.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findLimitedRange(Class<T> paramClass, int start, int limit) throws DAOException {
		List<T> result = null;
		try {
			Query q = em.createNamedQuery(paramClass.getSimpleName() + ".findAll");
			q.setFirstResult(start);
			q.setMaxResults(limit);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of " + paramClass.getSimpleName() + "List.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Object findByCode(Class<T> paramClass, String code) throws DAOException {
		Object result = null;
		try {
			Query query = em.createNamedQuery(paramClass.getSimpleName() + ".findByCode");
			query.setParameter("code", code);
			result = query.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find by code.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int findDuplicateByMonth(Class<T> paramClass, int year, int month, String companyID) throws DAOException {
		int result;
		try {
			Query q = em.createNamedQuery(paramClass.getSimpleName() + ".findByMonth");
			q.setParameter("importedYear", year);
			q.setParameter("importedMonth", month);
			q.setParameter("companyID", companyID);
			result = Integer.parseInt(q.getSingleResult().toString());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Motor by Month", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteDuplicatesByMonth(Class<T> paramClass, int year, int month, String companyID) throws DAOException {
		try {
			Query q = em.createNamedQuery(paramClass.getSimpleName() + ".deleteDuplicateByMonth");
			q.setParameter("importedYear", year);
			q.setParameter("importedMonth", month);
			q.setParameter("companyID", companyID);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Motor by Month", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findByType(Class<T> paramClass, Enum<?> type) throws DAOException {
		List<T> result = null;
		try {
			Query q = em.createNamedQuery(paramClass.getSimpleName() + ".findByType");
			q.setParameter("type", type);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of " + paramClass.getSimpleName() + " by " + type.toString(), pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<T> findAllParamDTO(Class<T> paramClass) throws DAOException {
		List<T> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + paramClass.getName());
			buffer.append("(c.id, c.name, c.description, c.version) FROM " + paramClass.getSimpleName() + " c ");
			Query q = em.createQuery(buffer.toString());
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of " + paramClass.getSimpleName() + "List.", pe);
		}
		return result;
	}
}
