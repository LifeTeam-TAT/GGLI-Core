/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.accounting.persistence.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.accounting.InterfaceFile;
import org.ace.insurance.accounting.UniqueKey;
import org.ace.java.component.persistence.exception.DAOException;

public interface IInterfaceFileDAO {

	public void update(InterfaceFile interfaceFile) throws DAOException;

	public void delete(InterfaceFile interfaceFile) throws DAOException;

	public InterfaceFile findById(String id) throws DAOException;

	public long findCountByDate(Date actionDate, boolean onlyConvertable) throws DAOException;

	public List<InterfaceFile> findByCriteria(UniqueKey key, Date importedDate, boolean onlyConvertable) throws DAOException;

	public long findCountByCriteria(UniqueKey uniqueKey) throws DAOException;

	public int importInterfaceFiles(List<String> dataStringList, int dataRows, Date actionDate, String uploadedFileName) throws DAOException;

	public void moveOldFiles(Date pastOneMonthDate, boolean onlyConverted) throws DAOException, Exception;

	public List<String> findConvertableFileCount() throws DAOException;

	int findConvertableGroupCount() throws DAOException;

	public InterfaceFile findNextConvertibleFile(Date actionDate) throws DAOException;

	public long findCoReCountByCriteria(UniqueKey uniqueKey) throws DAOException;

	public String findPolicyProductCode(String rldgacct, Integer tranno) throws DAOException;

}
