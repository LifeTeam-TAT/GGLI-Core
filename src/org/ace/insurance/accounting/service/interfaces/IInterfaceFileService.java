/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.service.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.accounting.InterfaceFile;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.java.component.SystemException;

public interface IInterfaceFileService {

	public void updateInterfaceFile(InterfaceFile InterfaceFile) throws SystemException;

	public void deleteInterfaceFile(InterfaceFile InterfaceFile) throws SystemException;

	public InterfaceFile findInterfaceFileById(String id) throws SystemException;

	public List<InterfaceFile> findByCriteria(UniqueKey uniqueKey, Date importedDate, boolean onlyConvertable) throws SystemException;

	public long findCountByDate(Date actionDate, boolean onlyConvertable) throws SystemException;

	public long findCountByCriteria(UniqueKey uniqueKey) throws SystemException;

	public int importInterfaceFiles(List<String> result, int dataRows, Date actionDate, String uploadedFileName) throws SystemException;

	public List<String> findConvertableFileCount() throws SystemException;

	public long convertFiles(Date actionDate) throws SystemException, Exception;

	public long findCoReCountByCriteria(UniqueKey uniqueKey) throws SystemException;

	public String findPolicyProductCode(String rldgacct, Integer tranno) throws SystemException;
}
