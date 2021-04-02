/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.city.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.city.CITY001;
import org.ace.insurance.system.common.city.City;
import org.ace.insurance.system.common.city.persistence.interfaces.ICityDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("CityDAO")
public class CityDAO extends BasicDAO implements ICityDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(City city) throws DAOException {
		try {
			if (!isAlreadyExist(city)) {
				em.persist(city);
				insertProcessLog(TableName.CITY, city.getId());
				em.flush();
			} else {
				throw new SystemException(null, city.getName() + "is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert City", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(City city) throws DAOException {
		try {
			if (!isAlreadyExist(city)) {
				em.merge(city);
				updateProcessLog(TableName.CITY, city.getId());
				em.flush();
			} else {
				throw new SystemException(null, city.getName() + "is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update City", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(City city) throws DAOException {
		try {
			city = em.merge(city);
			em.remove(city);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update City", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public City findById(String id) throws DAOException {
		City result = null;
		try {
			Query q = em.createNamedQuery("City.findById");
			q.setParameter("id", id);
			result = (City) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find City(City = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<City> findAll() throws DAOException {
		List<City> result = null;
		try {
			Query q = em.createNamedQuery("City.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of City", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<City> findByCriteria(String criteria) throws DAOException {
		List<City> result = null;
		try {
			// Query q = em.createNamedQuery("Township.findByCriteria");
			Query q = em.createQuery("Select t from City t where t.name Like '" + criteria + "%'");
			// q.setParameter("criteriaValue", "%" + criteria + "%");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of City.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public City findByName(String name) throws DAOException {
		List<City> result = null;
		try {
			Query q = em.createQuery("Select c from City c where c.name = :name ");
			q.setParameter("name", name);
			result = q.getResultList();
			if (result.size() > 0) {
				return result.get(0);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by name of City.", pe);
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findNameById(String id) throws DAOException {
		String name = null;
		try {
			Query query = em.createQuery("SELECT c.name FROM City c where c.id = :id");
			query.setParameter("id", id);
			name = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Township", pe);
		}
		return name;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<CITY001> findCity() throws DAOException {
		List<CITY001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW " + CITY001.class.getName() + " (c.id, c.name, c.description, c.province) FROM City c ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of City", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<CITY001> findCityByCriteria(String criteria) throws DAOException {
		List<CITY001> result = null;
		try {
			Query q = em.createQuery("Select New " + CITY001.class.getName() + " (c.id,c.name,c.description,c.province) from City c where c.name like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of City.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExist(City city) throws DAOException {
		boolean exist = false;
		String cityName = city.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM City c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(city.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (city.getId() != null)
				query.setParameter("id", city.getId());
			query.setParameter("name", cityName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
