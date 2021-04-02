/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.deno.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.deno.Deno;
import org.ace.insurance.system.common.deno.persistence.interfaces.IDenoDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("DenoDAO")
public class DenoDAO extends BasicDAO implements IDenoDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Deno deno) throws DAOException {
		try {
			em.persist(deno);
			insertProcessLog(TableName.DENO, deno.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Deno", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Deno deno) throws DAOException {
		try {
			em.merge(deno);
			updateProcessLog(TableName.DENO, deno.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Deno", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Deno deno) throws DAOException {
		try {
			deno = em.merge(deno);
			em.remove(deno);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Deno", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Deno findById(String id) throws DAOException {
		Deno result = null;
		try {
			result = em.find(Deno.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Deno", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Deno> findAll() throws DAOException {
		List<Deno> result = null;
		try {
			Query q = em.createNamedQuery("Deno.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Deno", pe);
		}
		return result;
	}
}
