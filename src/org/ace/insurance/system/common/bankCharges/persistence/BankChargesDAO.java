package org.ace.insurance.system.common.bankCharges.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.bankCharges.BankCharges;
import org.ace.insurance.system.common.bankCharges.persistence.interfaces.IBankChargesDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BankChargesDAO")
public class BankChargesDAO extends BasicDAO implements IBankChargesDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BankCharges bankCharges) throws DAOException {
		try {
			if (!checkExistingBankCharges(bankCharges)) {
				em.persist(bankCharges);
			} else {
				throw new SystemException(null, bankCharges.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert bankCharges (bankChargesname = " + bankCharges.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BankCharges bankCharges) throws DAOException {
		try {
			bankCharges = em.merge(bankCharges);
			em.remove(bankCharges);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete bankCharges User(bankCharges name = " + bankCharges.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BankCharges bankCharges) throws DAOException {
		try {
			em.merge(bankCharges);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update bankCharges User(bankCharges name = " + bankCharges.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BankCharges findById(String BankChargesId) throws DAOException {
		BankCharges result = null;
		try {
			result = em.find(BankCharges.class, BankChargesId);
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find BankCharges", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BankCharges> findAllBankCharges() {
		List<BankCharges> result = null;
		try {
			Query q = em.createQuery("select b from BankCharges b where b.status=true");
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all BankCharges ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBankCharges(BankCharges bankCharges) throws DAOException {
		boolean exist = false;
		String bankChargesName = bankCharges.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer(
					"SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM BankCharges b ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
			buffer.append(bankCharges.getId() != null ? "AND b.id != :id" : "");
			buffer.append("AND b.status = :status ");
			query = em.createQuery(buffer.toString());
			if (bankCharges.getId() != null)
				query.setParameter("id", bankCharges.getId());
			query.setParameter("name", bankChargesName.toLowerCase());
			query.setParameter("status", bankCharges.isStatus());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer(
						"SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM BankCharges b");
				buffer.append(" WHERE b.id != :id");
				buffer.append(" AND LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", bankCharges.getId());
				query.setParameter("name", bankCharges.getName());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

	@Override
	public List<BankCharges> findAllbankChargesActive(String branchId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

}
