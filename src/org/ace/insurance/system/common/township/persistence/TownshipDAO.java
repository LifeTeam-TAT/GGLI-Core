/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.township.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.province.Province;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.Township001;
import org.ace.insurance.system.common.township.persistence.interfaces.ITownshipDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("TownshipDAO")
public class TownshipDAO extends BasicDAO implements ITownshipDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Township township) throws DAOException {
		try {
			if (!isAlreadyExistTownship(township)) {
				em.persist(township);
				insertProcessLog(TableName.TOWNSHIP, township.getId());
				em.flush();
			} else {
				throw new SystemException(null, township.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Township", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Township township) throws DAOException {
		try {
			if (!isAlreadyExistTownship(township)) {
				em.merge(township);
				updateProcessLog(TableName.TOWNSHIP, township.getId());
				em.flush();
			} else {
				throw new SystemException(null, township.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Township", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Township township) throws DAOException {
		try {
			township = em.merge(township);
			em.remove(township);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Township", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Township findById(String id) throws DAOException {
		Township result = null;
		try {
			result = em.find(Township.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Township", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Township> findByProvince(Province province) throws DAOException {
		List<Township> result = null;
		try {
			Query query = em.createNamedQuery("Township.findByProvince");
			query.setParameter("provinceId", province.getId());
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Township", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Township> findAll() throws DAOException {
		List<Township> result = null;
		try {
			Query q = em.createNamedQuery("Township.findAll");
			result = q.getResultList();
			q.setMaxResults(50);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Township", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Township> findByCriteria(String criteria) throws DAOException {
		List<Township> result = null;
		try {
			Query q = em.createQuery("Select t from Township t where t.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Township.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findNameById(String id) throws DAOException {
		String name = null;
		try {
			Query query = em.createQuery("SELECT t.name FROM Township t where t.id = :id");
			query.setParameter("id", id);
			name = (String) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Township", pe);
		}
		return name;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Township001> findAllTownship() throws DAOException {
		List<Township001> result = null;
		try {
			StringBuffer buffer = new StringBuffer("SELECT NEW " + Township001.class.getName());
			buffer.append("(t.id, t.name, t.province.name) FROM Township t ORDER BY t.province.name ASC");
			TypedQuery<Township001> query = em.createQuery(buffer.toString(), Township001.class);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all Township001.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Township001> findByCriteria001(String criteria) throws DAOException {
		List<Township001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW " + Township001.class.getName() + " (t.id, t.name, t.description) FROM Township t where t.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Township.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistTownship(Township township) throws DAOException {
		boolean exist = false;
		String townshipName = township.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Township c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(township.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (township.getId() != null)
				query.setParameter("id", township.getId());
			query.setParameter("name", townshipName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

}
