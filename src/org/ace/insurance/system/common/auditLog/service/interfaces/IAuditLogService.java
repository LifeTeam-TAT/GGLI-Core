package org.ace.insurance.system.common.auditLog.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.auditLog.AuditLog;

public interface IAuditLogService {
	
	public AuditLog addNewAuditLog(AuditLog auditLog);

	public void updateAuditLog(AuditLog auditLog);

	public void deleteAuditLog(AuditLog auditLog);

	public AuditLog findAuditLogById(String id);

	public List<AuditLog> findAllAuditLog();

	public boolean isAlreadyExistAuditLog(AuditLog auditLog);

}
