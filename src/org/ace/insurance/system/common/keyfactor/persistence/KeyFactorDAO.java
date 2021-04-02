/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.keyfactor.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.keyfactor.persistence.interfaces.IKeyFactorDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("KeyFactorDAO")
public class KeyFactorDAO extends BasicDAO implements IKeyFactorDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(KeyFactor keyFactor) throws DAOException {
		try {
			if (!isAlreadyExistKeyFactor(keyFactor)) {
				em.persist(keyFactor);
				insertProcessLog(TableName.KEYFACTOR, keyFactor.getId());
				em.flush();
			} else {
				throw new SystemException(null, keyFactor.getValue() + "is already exist ");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert KeyFactor", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(KeyFactor keyFactor) throws DAOException {
		try {
			if (!isAlreadyExistKeyFactor(keyFactor)) {
				em.merge(keyFactor);
				updateProcessLog(TableName.KEYFACTOR, keyFactor.getId());
				em.flush();
			} else {
				throw new SystemException(null, keyFactor.getValue() + "is already exist ");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update KeyFactor", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(KeyFactor keyFactor) throws DAOException {
		try {
			keyFactor = em.merge(keyFactor);
			em.remove(keyFactor);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update KeyFactor", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public KeyFactor findById(String id) throws DAOException {
		KeyFactor result = null;
		try {
			result = em.find(KeyFactor.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find KeyFactor", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<KeyFactor> findAll() throws DAOException {
		List<KeyFactor> result = null;
		try {
			Query q = em.createNamedQuery("KeyFactor.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of KeyFactor", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistKeyFactor(KeyFactor keyFactor) throws DAOException {
		boolean exist = false;
		String keyFactorName = keyFactor.getValue().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.value) > 0) THEN TRUE ELSE FALSE END FROM KeyFactor c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.value,' ','')) = :value ");
			buffer.append(" AND c.keyFactorType = :keyFactorType ");
			buffer.append(keyFactor.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (keyFactor.getId() != null)
				query.setParameter("id", keyFactor.getId());
			query.setParameter("value", keyFactorName.toLowerCase());
			query.setParameter("keyFactorType", keyFactor.getKeyFactorType());
			exist = (Boolean) query.getSingleResult();
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<KeyFactor> findKeyFactorForSimpleLife() throws DAOException {
		List<KeyFactor> result = null;
		try {
			Query query = null;
			String value = "SIMPLE";
			StringBuffer buffer = new StringBuffer();
			buffer = new StringBuffer("SELECT c FROM KeyFactor c ");
			buffer.append(" WHERE c.value LIKE '%Death%' ");
			query = em.createQuery(buffer.toString());
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of KeyFactor", pe);
		}
		return result;
	}

}
