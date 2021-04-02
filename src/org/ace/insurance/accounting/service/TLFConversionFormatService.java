/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.service;

import javax.annotation.Resource;

import org.ace.insurance.accounting.CoReStatus;
import org.ace.insurance.accounting.TLFConversionFormat;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.insurance.accounting.persistence.interfaces.ITLFConversionFormatDAO;
import org.ace.insurance.accounting.service.interfaces.ITLFConversionFormatService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.ace.java.web.common.MessageId;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "TLFConversionFormatService")
public class TLFConversionFormatService extends BaseService implements ITLFConversionFormatService {
	private Logger logger = Logger.getLogger(this.getClass());

	@Resource(name = "TLFConversionFormatDAO")
	private ITLFConversionFormatDAO tlfConversionFormatDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public TLFConversionFormat findTlfConversionFormat(UniqueKey uniqueKey, String cnttype, CoReStatus coReStatus) throws SystemException {
		TLFConversionFormat result;
		try {
			result = tlfConversionFormatDAO.findTlfConversionFormat(uniqueKey, cnttype, coReStatus);
		} catch (DAOException e) {
			logger.info(MessageId.CSC_IMPORT_EXCEPTION + " : " + e.getClass().getName() + " Exception in findTlfConversionFormat method ");
			throw new SystemException(e.getErrorCode(), e.getMessage(), e);
		}
		return result;
	}

}