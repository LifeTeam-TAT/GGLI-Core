package org.ace.insurance.system.common.auditLog.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.auditLog.AuditLog;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAuditLogDAO {
	
	public void insert(AuditLog auditLog) throws DAOException;

	public void update(AuditLog auditLog) throws DAOException;

	public void delete(AuditLog auditLog) throws DAOException;

	public AuditLog findById(String id) throws DAOException;

	public List<AuditLog> findAll() throws DAOException;

	public boolean isAlreadyExistAuditLog(AuditLog auditLog) throws DAOException;

}
