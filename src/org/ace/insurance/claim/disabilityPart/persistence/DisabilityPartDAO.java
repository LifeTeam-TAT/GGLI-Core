package org.ace.insurance.claim.disabilityPart.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.persistence.interfaces.IDisabilityPartDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("DisabilityPartDAO")
public class DisabilityPartDAO extends BasicDAO implements IDisabilityPartDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(DisabilityPart disabilityPart) throws DAOException {
		try {
			if (!isAlreadyExist(disabilityPart)) {
				em.persist(disabilityPart);
				em.flush();
			} else {
				throw new SystemException(null, disabilityPart.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Disability Part", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(DisabilityPart disabilityPart) throws DAOException {
		try {
			if (!isAlreadyExist(disabilityPart)) {
				em.merge(disabilityPart);
				em.flush();
			} else {
				throw new SystemException(null, disabilityPart.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Disability Part", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(DisabilityPart disabilityPart) throws DAOException {
		try {
			disabilityPart = em.merge(disabilityPart);
			em.remove(disabilityPart);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Disability Part", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public DisabilityPart findById(String id) throws DAOException {
		DisabilityPart result = null;
		try {
			result = em.find(DisabilityPart.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Disability Part", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<DisabilityPart> findAll() throws DAOException {
		List<DisabilityPart> result = null;
		try {
			Query q = em.createNamedQuery("DisabilityPart.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Disability Part", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExist(DisabilityPart disabilityPart) throws DAOException {
		boolean exist = false;
		String disabilityPartName = disabilityPart.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM DisabilityPart c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(disabilityPart.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (disabilityPart.getId() != null)
				query.setParameter("id", disabilityPart.getId());
			query.setParameter("name", disabilityPartName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

}
