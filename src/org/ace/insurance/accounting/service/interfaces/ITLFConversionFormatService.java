/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.service.interfaces;

import org.ace.insurance.accounting.CoReStatus;
import org.ace.insurance.accounting.TLFConversionFormat;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.java.component.SystemException;

public interface ITLFConversionFormatService {

	public TLFConversionFormat findTlfConversionFormat(UniqueKey uniqueKey, String cnttype, CoReStatus coReStatus) throws SystemException;
}
