/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.bankBranch.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.bankBranch.BBRANCH001;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.persistence.interfaces.IBankBranchDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BankBranchDAO")
public class BankBranchDAO extends BasicDAO implements IBankBranchDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BankBranch bankBranch) throws DAOException {
		try {
			em.persist(bankBranch);
			insertProcessLog(TableName.BANKBRANCH, bankBranch.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert BankBranch", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BankBranch bankBranch) throws DAOException {
		try {
			em.merge(bankBranch);
			updateProcessLog(TableName.BANKBRANCH, bankBranch.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update BankBranch", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BankBranch bankBranch) throws DAOException {
		try {
			bankBranch = em.merge(bankBranch);
			em.remove(bankBranch);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update BankBranch", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public BankBranch findById(String id) throws DAOException {
		BankBranch result = null;
		try {
			Query q = em.createNamedQuery("BankBranch.findById");
			q.setParameter("id", id);
			result = (BankBranch) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BankBranch(BankBranch = " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BBRANCH001> findAll() throws DAOException {
		List<BBRANCH001> result = null;
		try {
			Query q = em.createQuery("SELECT NEW " + BBRANCH001.class.getName() + " (b.id, b.branchCode, b.name, b.bank, b.township, b.address) FROM BankBranch b ");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of BankBranch", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BankBranch> findAllBankBranch() throws DAOException {
		List<BankBranch> result = null;
		try {
			Query q = em.createNamedQuery("BankBranch.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of BankBranch", pe);
		}
		return result;
	}
}
