/***************************************************************************************
 * @author <<Myo Thiha Kyaw>>
 * @Date 2014-06-18
 * @Version 1.0
 * @Purpose <<For Travel Insurance>>
 * 
 *    
 ***************************************************************************************/

package org.ace.insurance.system.common.express.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ExpressCriteria;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.express.Express001;
import org.ace.insurance.system.common.express.persistence.interfaces.IExpressDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ExpressDAO")
public class ExpressDAO extends BasicDAO implements IExpressDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Express express) throws DAOException {
		try {
			if (!isAlreadyExist(express)) {
				em.persist(express);
				insertProcessLog(TableName.EXPRESS, express.getId());
			} else {
				throw new SystemException(null, express.getName() + "  is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Express", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Express express) throws DAOException {
		try {
			if (!isAlreadyExist(express)) {
				em.merge(express);
				updateProcessLog(TableName.EXPRESS, express.getId());
				em.flush();
			} else {
				throw new SystemException(null, express.getName() + "  is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Express", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Express express) throws DAOException {
		try {
			express = em.merge(express);
			em.remove(express);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Express", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Express findById(String id) throws DAOException {
		Express result = null;
		try {
			result = em.find(Express.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Express", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Express> findAll() throws DAOException {
		List<Express> result = null;
		try {
			Query q = em.createNamedQuery("Express.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Express", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Express> findByCriteria(ExpressCriteria criteria, int max) throws DAOException {
		List<Express> result = null;
		try {

			StringBuffer query = new StringBuffer();
			query.append("SELECT e FROM Express e ");

			if (criteria.getExpressCriteria() != null) {
				switch (criteria.getExpressCriteria()) {
					case NAME: {
						query.append("WHERE e.name like :name");
						break;
					}
					case EXPRESS_CODE: {
						query.append("WHERE e.codeNo like :codeNo");
						break;
					}
					case REGISTRATION_NO: {
						query.append("WHERE e.regNo like :regNo");
						break;
					}
				}
			}
			query.append(" Order By e.name DESC");
			Query q = em.createQuery(query.toString());
			q.setMaxResults(max);
			if (criteria.getExpressCriteria() != null) {
				switch (criteria.getExpressCriteria()) {
					case NAME: {
						q.setParameter("name", criteria.getCriteriaValue() + "%");
						break;
					}
					case EXPRESS_CODE: {
						q.setParameter("codeNo", criteria.getCriteriaValue());
						break;
					}
					case REGISTRATION_NO: {
						q.setParameter("regNo", criteria.getCriteriaValue());
						break;
					}
				}
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Express", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Express001> findByCriteria001(ExpressCriteria criteria, int max) throws DAOException {
		List<Express001> result = null;
		try {
			StringBuffer query = new StringBuffer("SELECT NEW " + Express001.class.getName());
			query.append(" ( e.id, e.name, e.codeNo, e.regNo) FROM Express e ");
			if (criteria.getExpressCriteria() != null) {
				switch (criteria.getExpressCriteria()) {
					case NAME: {
						query.append("WHERE e.name like :name");
						break;
					}
					case EXPRESS_CODE: {
						query.append("WHERE e.codeNo like :codeNo");
						break;
					}
					case REGISTRATION_NO: {
						query.append("WHERE e.regNo like :regNo");
						break;
					}
				}
			}
			query.append(" Order By e.name DESC");
			Query q = em.createQuery(query.toString());
			q.setMaxResults(max);
			if (criteria.getExpressCriteria() != null) {
				switch (criteria.getExpressCriteria()) {
					case NAME: {
						q.setParameter("name", criteria.getCriteriaValue() + "%");
						break;
					}
					case EXPRESS_CODE: {
						q.setParameter("codeNo", criteria.getCriteriaValue());
						break;
					}
					case REGISTRATION_NO: {
						q.setParameter("regNo", criteria.getCriteriaValue());
						break;
					}
				}
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Express", pe);
		}

		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExist(Express express) throws DAOException {
		boolean exist = false;
		String expressName = express.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Express c ");
			buffer.append(" WHERE c.name = :name ");
			buffer.append(express.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (express.getId() != null)
				query.setParameter("id", express.getId());
			query.setParameter("name", express.getName());
			exist = (Boolean) query.getSingleResult();

			if (exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.id) > 0) THEN TRUE ELSE FALSE END FROM Express c");
				buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
				buffer.append(" AND LOWER(c.address.permanentAddress)= :address ");
				buffer.append("	AND c.address.township.id = :townshipId");
				buffer.append(express.getId() != null ? " AND c.id != :id" : "");
				query = em.createQuery(buffer.toString());
				if (express.getId() != null)
					query.setParameter("id", express.getId());
				query.setParameter("name", expressName.toLowerCase());
				query.setParameter("townshipId", express.getAddress().getTownship().getId());
				query.setParameter("address", express.getAddress().getPermanentAddress().toLowerCase());
				exist = (Boolean) query.getSingleResult();
			}
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
