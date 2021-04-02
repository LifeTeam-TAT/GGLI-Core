package org.ace.insurance.system.common.auditLog.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.auditLog.AuditLog;
import org.ace.insurance.system.common.auditLog.persistence.interfaces.IAuditLogDAO;
import org.ace.insurance.system.common.auditLog.service.interfaces.IAuditLogService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "AuditLogService")
public class AuditLogService extends BaseService implements IAuditLogService {
	
	@Resource(name = "AuditLogDAO")
	private IAuditLogDAO auditLogDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public AuditLog addNewAuditLog(AuditLog auditLog) {
		try {
			auditLog.setPrefix(getPrefix(AuditLog.class));
			auditLogDAO.insert(auditLog);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new Audit Log", e);
		}
		
		return auditLog;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAuditLog(AuditLog auditLog) {
		try {
			auditLogDAO.update(auditLog);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update a Audit Log", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAuditLog(AuditLog auditLog) {
		try {
			auditLogDAO.delete(auditLog);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete a Audit Log", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AuditLog findAuditLogById(String id) {
		AuditLog result = null;
		try {
			result = auditLogDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a Audit Log (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AuditLog> findAllAuditLog() {
		List<AuditLog> result = null;
		try {
			result = auditLogDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Audit Log)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistAuditLog(AuditLog auditLog) {
		try {
			return auditLogDAO.isAlreadyExistAuditLog(auditLog);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find  Audit Log By excel template id)", e);
		}
	}

}
