package org.ace.insurance.system.common.auditLog.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.auditLog.AuditLog;
import org.ace.insurance.system.common.auditLog.persistence.interfaces.IAuditLogDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("AuditLogDAO")
public class AuditLogDAO extends BasicDAO implements IAuditLogDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(AuditLog auditLog) throws DAOException {
		try {
			em.persist(auditLog);
			insertProcessLog(TableName.AUDITLOG, auditLog.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Audit Log", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AuditLog auditLog) throws DAOException {
		try {
			em.merge(auditLog);
			updateProcessLog(TableName.AUDITLOG, auditLog.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Audit Log", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(AuditLog auditLog) throws DAOException {
		try {
			auditLog = em.merge(auditLog);
			em.remove(auditLog);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Audit Log", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AuditLog findById(String id) throws DAOException {
		AuditLog result = null;
		try {
			Query q = em.createNamedQuery("AuditLog.findById");
			q.setParameter("id", id);
			result = (AuditLog) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Audit Log(AuditLog = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AuditLog> findAll() throws DAOException {
		List<AuditLog> result = null;
		try {
			Query q = em.createNamedQuery("AuditLog.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Audit Log", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistAuditLog(AuditLog auditLog) throws DAOException {
		boolean exist = false;
		String excelFileId = auditLog.getExcelFileId().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(a.excelFileId) > 0) THEN TRUE ELSE FALSE END FROM AuditLog a ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',a.excelFileId,' ','')) = :excelFileId ");
			buffer.append(auditLog.getId() != null ? "AND a.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (auditLog.getId() != null)
				query.setParameter("id", auditLog.getId());
			query.setParameter("excelFileId", excelFileId.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing excel template id ", pe);
		}
	}

}
