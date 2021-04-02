/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.company.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.company.Company;
import org.ace.insurance.system.common.company.Company001;
import org.ace.insurance.system.common.company.persistence.interfaces.ICompanyDAO;
import org.ace.insurance.system.common.company.service.interfaces.ICompanyService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "CompanyService")
public class CompanyService extends BaseService implements ICompanyService {

	@Resource(name = "CompanyDAO")
	private ICompanyDAO companyDAO;

	public void addNewCompany(Company company) {
		try {
			company.setPrefix(getPrefix(Company.class));
			companyDAO.insert(company);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Company", e);
		}
	}

	public void updateCompany(Company company) {
		try {
			companyDAO.update(company);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Company", e);
		}
	}

	public void deleteCompany(Company company) {
		try {
			companyDAO.delete(company);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a Company", e);
		}
	}

	public List<Company> findAllCompany() {
		List<Company> result = null;
		try {
			result = companyDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Company)", e);
		}
		return result;
	}

	public Company findCompanyById(String id) {
		Company result = null;
		try {
			result = companyDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Company (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Company> findByCriteria(String criteria) {
		List<Company> result = null;
		try {
			result = companyDAO.findByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Company by criteria " + criteria, e);
		}
		return result;
	}

	public List<Company001> findAll() {
		List<Company001> result = null;
		try {
			result = companyDAO.findAllCompany();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Company)", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistCompany(Company company) {
		return companyDAO.isAlreadyExistCompany(company);
	}

}