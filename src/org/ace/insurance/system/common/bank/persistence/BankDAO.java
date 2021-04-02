/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.bank.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.TableName;
import org.ace.insurance.filter.bankCustomer.BANKCUSTOMER001;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.insurance.system.common.bank.persistence.interfaces.IBankDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BankDAO")
public class BankDAO extends BasicDAO implements IBankDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Bank bank) throws DAOException {
		try {
			em.persist(bank);
			insertProcessLog(TableName.BANK, bank.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Bank", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Bank bank) throws DAOException {
		try {
			em.merge(bank);
			updateProcessLog(TableName.BANK, bank.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Bank", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Bank bank) throws DAOException {
		try {
			bank = em.merge(bank);
			em.remove(bank);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Bank", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Bank findById(String id) throws DAOException {
		Bank result = null;
		try {
			result = em.find(Bank.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Bank", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BANKCUSTOMER001> findAll() throws DAOException {
		List<BANKCUSTOMER001> result = null;
		try {
			Query q = em.createNamedQuery("Bank.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Bank", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Bank> findAllBank() throws DAOException {
		List<Bank> result = null;
		try {
			TypedQuery<Bank> q = em.createNamedQuery("Bank.findAll", Bank.class);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Bank", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Coa> findCAOByAType() throws DAOException {
		List<Coa> result = null;

		try {
			Query query = em.createQuery("SELECT a FROM Coa a WHERE a.acType='A' AND substring(a.acode,4,3)<>'000' ");
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find CAO By AType", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Bank> findACodeNotNull() throws DAOException {
		List<Bank> result = null;
		try {
			Query query = em.createQuery("SELECT b FROM Bank b WHERE b.acode IS NOT NULL");
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All Banks which ACode is Not Null", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Bank findByCSCBankCode(String cscBankCode) throws DAOException {
		Bank result = null;
		try {
			Query query = em.createQuery("SELECT b FROM Bank b WHERE b.cscBankCode=:cscBankCode");
			query.setParameter("cscBankCode", cscBankCode);
			result = (Bank) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find bank by cscbankcode", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Bank findByACODE(String acode) throws DAOException {
		Bank result = null;
		try {
			Query query = em.createQuery("SELECT b FROM Bank b WHERE b.acode=:acode");
			query.setParameter("acode", acode);
			result = (Bank) query.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find bank by acode", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BANKCUSTOMER001> findAllInterBranchBank() throws DAOException {
		List<BANKCUSTOMER001> result = null;
		try {
			Query query = em.createQuery("SELECT b FROM Bank b WHERE  b.acode  Like  'ash%' ");
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All Of InterBranchBank", pe);
		}
		return result;
	}

	@Override
	public Bank findBybranchId(String branchId) throws DAOException {
		Bank result = null;
		try {
			Query query = em.createQuery("SELECT b FROM Bank b WHERE b.branch.id=:branchId");
			query.setParameter("branchId", branchId);
			result = (Bank) query.getSingleResult();
		} catch (PersistenceException pe) {
			throw translate("Failed to find bank by acode", pe);
		}
		return result;
	}
}
