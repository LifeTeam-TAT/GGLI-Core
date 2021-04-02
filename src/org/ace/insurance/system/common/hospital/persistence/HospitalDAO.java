/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.hospital.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.hospital.Hospital001;
import org.ace.insurance.system.common.hospital.persistence.interfaces.IHospitalDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("HospitalDAO")
public class HospitalDAO extends BasicDAO implements IHospitalDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Hospital hospital) throws DAOException {
		try {
			if (!isAlreadyExistHospital(hospital)) {
				em.persist(hospital);
				insertProcessLog(TableName.COUNTRY, hospital.getId());
				em.flush();
			} else {
				throw new SystemException(null, hospital.getName() + " is already Exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Hospital", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Hospital hospital) throws DAOException {
		try {
			if (!isAlreadyExistHospital(hospital)) {
				em.merge(hospital);
				updateProcessLog(TableName.COUNTRY, hospital.getId());
				em.flush();
			} else {
				throw new SystemException(null, hospital.getName() + " is already Exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Hospital", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Hospital hospital) throws DAOException {
		try {
			hospital = em.merge(hospital);
			em.remove(hospital);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Hospital", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Hospital findById(String id) throws DAOException {
		Hospital result = null;
		try {
			result = em.find(Hospital.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Hospital", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Hospital> findAll() throws DAOException {
		List<Hospital> result = null;
		try {
			Query q = em.createNamedQuery("Hospital.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Hospital", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Hospital> findByCriteria(String criteria) throws DAOException {
		List<Hospital> result = null;
		try {
			Query q = em.createQuery("Select t from Hospital t where t.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Hospital.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Hospital001> findAllHospital() throws DAOException {
		List<Hospital001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW " + Hospital001.class.getName()
					+ " ( h.id, h.name, h.contentInfo.phone, h.address.permanentAddress, h.address.township.name, h.description) FROM Hospital h ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Hospital", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Hospital001> findByCriteria001(String criteria) throws DAOException {
		List<Hospital001> result = null;
		try {
			Query q = em.createQuery("Select New " + Hospital001.class.getName()
					+ " (h.id,h.name,h.contentInfo.phone,h.address.permanentAddress, h.address.township.name, h.description) from Hospital h where h.name like '" + criteria
					+ "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Hospital.", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExistHospital(Hospital hospital) throws DAOException {
		boolean exist = false;
		String hospitalName = hospital.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM Hospital b ");
			buffer.append(" WHERE LOWER(b.name) = :code ");
			buffer.append(hospital.getId() != null ? " AND b.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (hospital.getId() != null)
				query.setParameter("id", hospital.getId());
			query.setParameter("code", hospital.getName());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM Hospital b");
				buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				buffer.append(" AND LOWER(b.address.permanentAddress)= :address ");
				buffer.append("	AND b.address.township.id = :townshipId");
				buffer.append(hospital.getId() != null ? " AND b.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (hospital.getId() != null)
					query.setParameter("id", hospital.getId());
				query.setParameter("name", hospitalName.toLowerCase());
				query.setParameter("townshipId", hospital.getAddress().getTownship().getId());
				query.setParameter("address", hospital.getAddress().getPermanentAddress().toLowerCase());
				exist = (Boolean) query.getSingleResult();
			}
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
