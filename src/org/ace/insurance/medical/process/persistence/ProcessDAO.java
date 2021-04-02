package org.ace.insurance.medical.process.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.medical.process.Process;
import org.ace.insurance.medical.process.Process001;
import org.ace.insurance.medical.process.persistence.interfaces.IProcessDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/***************************************************************************************
 * @author HS
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose This class serves as the DAO to manipulate the <code>Process</code>
 *          object.
 * 
 ***************************************************************************************/

@Repository("ProcessDAO")
public class ProcessDAO extends BasicDAO implements IProcessDAO {

	/**
	 * @see org.ace.insurance.medical.process.persistence.interfaces.IProcessDAO
	 *      #insert(org.ace.insurance.medical.process.Process)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Process process) throws DAOException {
		try {
			if (!isAlreadyExistProcess(process)) {
				em.persist(process);
				em.flush();
			} else {
				throw new SystemException(null, process.getName() + " is already exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Process", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.process.persistence.interfaces.IProcessDAO
	 *      #update(org.ace.insurance.medical.process.Process)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Process process) throws DAOException {
		try {
			if (!isAlreadyExistProcess(process)) {
				em.merge(process);
				em.flush();
			} else {
				throw new SystemException(null, process.getName() + " is already exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Process", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.process.persistence.interfaces.IProcessDAO
	 *      #delete(org.ace.insurance.medical.process.Process)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Process process) throws DAOException {
		try {
			process = em.merge(process);
			em.remove(process);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Process", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.process.persistence.interfaces.IProcessDAO
	 *      #findAll()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Process> findAll() throws DAOException {
		List<Process> result = null;
		try {
			Query q = em.createNamedQuery("Process.findAll");
			Query qq = em.createNamedQuery("Process.findByName");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Process", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String findByName(String name) throws DAOException {
		String result = "";
		try {
			Query qq = em.createNamedQuery("Process.findByName");
			qq.setParameter("processName", name);
			result = (String) qq.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Process", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Process001> findAllProcess() throws DAOException {
		List<Process001> result = null;
		try {
			/*
			 * Query q = em.createNamedQuery("Process.findAll"); Query qq =
			 * em.createNamedQuery("Process.findByName");
			 */
			Query q = em.createQuery("SELECT NEW " + Process001.class.getName() + " (p.id, p.name, p.description ) FROM Process p ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Process", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Process findById(String id) throws DAOException {
		Process result = null;
		try {
			result = em.find(Process.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find process", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExistProcess(Process process) throws DAOException {
		boolean exist = false;
		String processName = process.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Process c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name");
			buffer.append(process.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (process.getId() != null)
				query.setParameter("id", process.getId());
			query.setParameter("name", processName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find process", pe);
		}
	}
}
