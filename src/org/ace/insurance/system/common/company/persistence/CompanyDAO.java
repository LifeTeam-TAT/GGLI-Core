/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.company.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.company.Company;
import org.ace.insurance.system.common.company.Company001;
import org.ace.insurance.system.common.company.persistence.interfaces.ICompanyDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("CompanyDAO")
public class CompanyDAO extends BasicDAO implements ICompanyDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Company company) throws DAOException {
		try {
			em.persist(company);
			insertProcessLog(TableName.COMPANY, company.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Company", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Company company) throws DAOException {
		try {
			em.merge(company);
			updateProcessLog(TableName.COMPANY, company.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Company", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Company company) throws DAOException {
		try {
			company = em.merge(company);
			em.remove(company);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Company", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Company findById(String id) throws DAOException {
		Company result = null;
		try {
			result = em.find(Company.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Company", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Company> findAll() throws DAOException {
		List<Company> result = null;
		try {
			Query q = em.createNamedQuery("Company.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Company", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Company> findByCriteria(String criteria) throws DAOException {
		List<Company> result = null;
		try {
			Query q = em.createQuery("Select c from Company c where c.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Company.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Company001> findAllCompany() throws DAOException {
		List<Company001> result = null;
		try {
			Query q = em
					.createQuery("SELECT NEW " + Company001.class.getName() + "(c.id, c.name, c.description,c.address.township.name,c.address.permanentAddress) FROM Company c ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Company", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExistCompany(Company company) throws DAOException {
		boolean exist = false;
		String companyName = company.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Company c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(company.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (company.getId() != null)
				query.setParameter("id", company.getId());
			query.setParameter("name", companyName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.id) > 0) THEN TRUE ELSE FALSE END FROM Company c");
				buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
				buffer.append(" AND LOWER(c.address.permanentAddress)= :address ");
				buffer.append("	AND c.address.township.id = :townshipId");
				buffer.append(company.getId() != null ? " AND c.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (company.getId() != null)
					query.setParameter("id", company.getId());
				query.setParameter("name", companyName.toLowerCase());
				query.setParameter("townshipId", company.getAddress().getTownship().getId());
				query.setParameter("address", company.getAddress().getPermanentAddress().toLowerCase());
				exist = (Boolean) query.getSingleResult();
			}
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
