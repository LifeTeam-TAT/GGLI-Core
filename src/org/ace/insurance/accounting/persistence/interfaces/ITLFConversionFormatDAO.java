/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.persistence.interfaces;

import org.ace.insurance.accounting.CoReStatus;
import org.ace.insurance.accounting.TLFConversionFormat;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.java.component.persistence.exception.DAOException;

public interface ITLFConversionFormatDAO {

	public TLFConversionFormat findTlfConversionFormat(UniqueKey uniqueKey, String cnttype, CoReStatus coReStatus) throws DAOException;

}
