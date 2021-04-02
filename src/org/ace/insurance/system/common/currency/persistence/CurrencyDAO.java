/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.currency.persistence;

import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.Currency001;
import org.ace.insurance.system.common.currency.persistence.interfaces.ICurrencyDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("CurrencyDAO")
public class CurrencyDAO extends BasicDAO implements ICurrencyDAO {

	@Resource(name = "CSC_CUR_CONFIG")
	private Properties properties;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Currency currency) throws DAOException {
		try {
			if (!isAlreadyExist(currency)) {
				em.persist(currency);
				insertProcessLog(TableName.CUR, currency.getId());
				em.flush();
			} else {
				throw new SystemException(null, currency.getCurrencyCode() + " is already exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Currency", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Currency currency) throws DAOException {
		try {
			if (!isAlreadyExist(currency)) {
				em.merge(currency);
				updateProcessLog(TableName.CUR, currency.getId());
				em.flush();
			} else {
				throw new SystemException(null, currency.getCurrencyCode() + " is already exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Currency", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Currency currency) throws DAOException {
		try {
			currency = em.merge(currency);
			em.remove(currency);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Currency", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Currency findById(String id) throws DAOException {
		Currency result = null;
		try {
			Query q = em.createNamedQuery("Currency.findById");
			q.setParameter("id", id);
			result = (Currency) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Currency( = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Currency findCurrencyByCurrencyCode(String currencyCode) throws DAOException {
		Currency result = null;
		try {
			Query query = em.createNamedQuery("Currency.findByCurrencyCode");
			query.setParameter("currencyCode", currencyCode);
			result = (Currency) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Currency", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Currency> findAll() throws DAOException {
		List<Currency> result = null;
		try {
			Query q = em.createNamedQuery("Currency.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Currency", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Currency findHomeCurrency() throws DAOException {
		Currency result = null;
		try {
			Query q = em.createQuery("SELECT c FROM Currency c WHERE c.currencyCode = :currencyCode");
			q.setParameter("currencyCode", "KYT");
			result = (Currency) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find home currency.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Currency findCurrencyByCSCCurrencyCode(String cscCurCode) throws DAOException {
		Currency result = null;
		try {
			String currencyCode = properties.getProperty(cscCurCode);
			result = findCurrencyByCurrencyCode(currencyCode);
		} catch (PersistenceException pe) {
			throw translate("Failed to find Currency", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Currency001> findAllCurrency() throws DAOException {
		List<Currency001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW " + Currency001.class.getName() + " (c.id, c.symbol, c.currencyCode) FROM Currency c ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Currency", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean isAlreadyExist(Currency currency) throws DAOException {
		boolean exist = false;
		String currencyCode = currency.getCurrencyCode().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.currencyCode) > 0) THEN TRUE ELSE FALSE END FROM Currency c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.currencyCode,' ','')) = :code ");
			buffer.append(currency.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (currency.getId() != null)
				query.setParameter("id", currency.getId());
			query.setParameter("code", currencyCode.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
