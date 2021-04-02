/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.company.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.company.Company;
import org.ace.insurance.system.common.company.Company001;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICompanyDAO {
	public void insert(Company Company) throws DAOException;

	public void update(Company Company) throws DAOException;

	public void delete(Company Company) throws DAOException;

	public Company findById(String id) throws DAOException;

	public List<Company> findAll() throws DAOException;

	public List<Company001> findAllCompany() throws DAOException;

	public List<Company> findByCriteria(String criteria) throws DAOException;

	public boolean isAlreadyExistCompany(Company company) throws DAOException;

}
