package org.ace.insurance.system.common.occurrence.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.occurrence.Occurrence;
import org.ace.insurance.system.common.occurrence.persistence.interfaces.IOccurrenceDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("OccurrenceDAO")
public class OccurrenceDAO extends BasicDAO implements IOccurrenceDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Occurrence occurrence) throws DAOException {
		try {
			if (!isAlreadyExist(occurrence)) {
				em.persist(occurrence);
				insertProcessLog(TableName.OCCURRENCE, occurrence.getId());
			} else {
				throw new SystemException(null, occurrence.getFromCity() + "-" + occurrence.getToCity() + " is already Exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Occurrence", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Occurrence occurrence) throws DAOException {
		try {
			if (!isAlreadyExist(occurrence)) {
				em.merge(occurrence);
				updateProcessLog(TableName.OCCURRENCE, occurrence.getId());
				em.flush();
			} else {
				throw new SystemException(null, occurrence.getFromCity() + "-" + occurrence.getToCity() + " is already Exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Occurrence", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Occurrence occurrence) throws DAOException {
		try {
			occurrence = em.merge(occurrence);
			em.remove(occurrence);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Occurrence", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Occurrence findById(String id) throws DAOException {
		Occurrence result = null;
		try {
			result = em.find(Occurrence.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Occurrence", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Occurrence> findAll() throws DAOException {
		List<Occurrence> result = null;
		try {
			Query q = em.createNamedQuery("Occurrence.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Occurrence", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Occurrence> findByCriteria(String criteria) throws DAOException {
		List<Occurrence> result = null;
		try {
			// Query q = em.createNamedQuery("Township.findByCriteria");
			Query q = em.createQuery("Select t from Occurrence t where t.description Like '" + criteria + "%'");
			q.setMaxResults(50);
			// q.setParameter("criteriaValue", "%" + criteria + "%");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Occurrence.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Occurrence findByFromCityToCity(Occurrence occurrence) throws DAOException {
		Occurrence result = null;
		try {
			// Query q =
			// em.createQuery("Select o from Occurrence o where o.fromCity.id =
			// fromCityId AND o.toCity.id = toCityId");
			// q.setParameter("fromCityId", occurrence.getFromCity().getId());
			// q.setParameter("toCityId", occurrence.getToCity().getId());

			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT o FROM Occurrence o where o.id is not null ");
			if (occurrence.getFromCity() != null) {
				queryString.append(" AND o.fromCity.id = :fromCity");
			}
			if (occurrence.getToCity() != null) {
				queryString.append(" AND o.toCity.id = :toCity");
			}
			Query query = em.createQuery(queryString.toString());
			if (occurrence.getFromCity() != null) {
				query.setParameter("fromCity", occurrence.getFromCity().getId());
			}
			if (occurrence.getToCity() != null) {
				query.setParameter("toCity", occurrence.getToCity().getId());
			}

			result = (Occurrence) query.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return result;
		} catch (PersistenceException pe) {
			throw translate("Failed to find by FromCityToCity of Occurrence.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExist(Occurrence occurrence) throws DAOException {
		boolean exist = false;
		String occurrenceFromCityName = occurrence.getFromCity().getName().replaceAll("\\s+", "");
		String occurrenceToCityName = occurrence.getToCity().getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.id) > 0 ) THEN TRUE ELSE FALSE END FROM Occurrence c ");
			buffer.append(" WHERE c.toCity.name = :toCityName  AND  c.fromCity.name = :formCityName ");
			buffer.append(occurrence.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (occurrence.getId() != null)
				query.setParameter("id", occurrence.getId());
			query.setParameter("toCityName", occurrenceFromCityName.toLowerCase());
			query.setParameter("formCityName", occurrenceToCityName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
