/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.addon.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.addon.persistence.interfaces.IAddOnDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("AddOnDAO")
public class AddOnDAO extends BasicDAO implements IAddOnDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(AddOn addOn) throws DAOException {
		try {
			if (!isAlreadyExistAddOn(addOn)) {
				em.persist(addOn);
				insertProcessLog(TableName.ADDON, addOn.getId());
				em.flush();
			} else {
				throw new SystemException(null, addOn.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert AddOn", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AddOn addOn) throws DAOException {
		try {
			if (!isAlreadyExistAddOn(addOn)) {
				em.merge(addOn);
				updateProcessLog(TableName.ADDON, addOn.getId());
				em.flush();
			} else {
				throw new SystemException(null, addOn.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update AddOn", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(AddOn addOn) throws DAOException {
		try {
			addOn = em.merge(addOn);
			em.remove(addOn);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update AddOn", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AddOn findById(String id) throws DAOException {
		AddOn result = null;
		try {
			result = em.find(AddOn.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find AddOn", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AddOn> findAll() throws DAOException {
		List<AddOn> result = null;
		try {
			Query q = em.createNamedQuery("AddOn.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AddOn", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AddOn> findByCriteria(String criteria) throws DAOException {
		List<AddOn> result = null;
		try {
			Query q = em.createQuery("Select a from AddOn a where a.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of AddOn.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistAddOn(AddOn addOn) throws DAOException {
		boolean exist = false;
		String addOnName = addOn.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM AddOn c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(" AND c.addOnType = :addOnType ");
			buffer.append(addOn.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (addOn.getId() != null)
				query.setParameter("id", addOn.getId());
			query.setParameter("name", addOnName.toLowerCase());
			query.setParameter("addOnType", addOn.getAddOnType());
			exist = (Boolean) query.getSingleResult();
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

}
