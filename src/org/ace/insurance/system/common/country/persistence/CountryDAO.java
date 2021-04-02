/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.country.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.country.COUNTRY001;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.country.persistence.interfaces.ICountryDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("CountryDAO")
public class CountryDAO extends BasicDAO implements ICountryDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Country country) throws DAOException {
		try {
			if (!isAlreadyExistCountry(country)) {
				em.persist(country);
				insertProcessLog(TableName.COUNTRY, country.getId());
				em.flush();
			} else {
				throw new SystemException(null, country.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Country", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Country country) throws DAOException {
		try {
			if (!isAlreadyExistCountry(country)) {
				em.merge(country);
				updateProcessLog(TableName.COUNTRY, country.getId());
				em.flush();
			} else {
				throw new SystemException(null, country.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Country", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Country country) throws DAOException {
		try {
			country = em.merge(country);
			em.remove(country);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Country", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Country findById(String id) throws DAOException {
		Country result = null;
		try {
			Query q = em.createNamedQuery("Country.findById");
			q.setParameter("id", id);
			result = (Country) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Country( Id = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findNameById(String id) throws DAOException {
		String name = null;
		try {
			Query query = em.createQuery("SELECT c.name FROM Country c where c.id = :id");
			query.setParameter("id", id);
			name = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Township", pe);
		}
		return name;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<COUNTRY001> findAllCountry() throws DAOException {
		List<COUNTRY001> result = null;
		try {
			TypedQuery<COUNTRY001> query = em.createQuery("SELECT NEW " + COUNTRY001.class.getName() + " (c.id, c.name, c.description) FROM Country c ", COUNTRY001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Country", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistCountry(Country country) throws DAOException {
		boolean exist = false;
		String countryName = country.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Country c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(country.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (country.getId() != null)
				query.setParameter("id", country.getId());
			query.setParameter("name", countryName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}
}
