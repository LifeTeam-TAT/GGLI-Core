package org.ace.insurance.aspects.versionref;

import org.ace.insurance.versionref.VersionRef;
import org.ace.insurance.versionref.service.interfaces.IVersionRefService;
import org.ace.insurance.ws.common.model.VersionRefDomainName;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionReferenceUpdateAspect {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private IVersionRefService versionRefService;
			
	protected void processVerIncrementAround(ProceedingJoinPoint joinPoint, Object entity) throws Throwable {		
		String entityName = entity.getClass().getSimpleName();
		Signature signature = joinPoint.getSignature();
		String targetMetName = signature.toShortString();
		//check operation type
		UpdateOperationType op = checkUpdateType(signature.getName(), entityName);			
		
		logger.debug( "Executing.." + targetMetName );
		try {
			joinPoint.proceed();
			//get entity id and insert record with incremented version number
			String entityId = (String)PropertyUtils.getProperty(entity, "id");
			//group name as domain 'customer'
			String group = VersionRefDomainName.Customer.class.getSimpleName();
			increaseVersion(group, entityId, entityName, op);
		} catch ( Throwable t ) {
			logger.error( t.getMessage() + ": " + targetMetName);
			throw t;
		}
		logger.debug("Successfully executed " + targetMetName);
	}
	
	//--------------PRIVATE METHODS------------------------
	private void increaseVersion(String group, String entityId, String entityName, UpdateOperationType updateType) {		
		//get max version number of group and entity name
		int maxVersionNo = versionRefService.findMaximumVersionNo(group, entityName);
		// version ref instance creation
		VersionRef versionRef = new VersionRef(entityId, entityName, maxVersionNo + 1, updateType.getLabel(), group);
		//create new record with increasing version number
		versionRefService.addNewVersionRef(versionRef);
	}
	
	private UpdateOperationType checkUpdateType(String invokedMethod, String entityName) {
		UpdateOperationType ret = null;
		if (invokedMethod.equals("addNew" + entityName)) {
			ret = UpdateOperationType.ADDNEW;
		} else if (invokedMethod.equals("update" + entityName)) {
			ret = UpdateOperationType.UPDATE;
		} else if (invokedMethod.equals("delete" + entityName)) {
			ret = UpdateOperationType.DELETE;
		}
		return ret;
	}
}
