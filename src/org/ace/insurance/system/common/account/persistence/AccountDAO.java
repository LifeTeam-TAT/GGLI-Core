package org.ace.insurance.system.common.account.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.system.common.account.CurrencyChartOfAccount;
import org.ace.insurance.system.common.account.persistence.interfaces.IAccountDAO;
import org.ace.insurance.system.common.bank.Coa;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("AccountDAO")
public class AccountDAO extends BasicDAO implements IAccountDAO {

	@Override
	public List<Coa> findAllCoa() throws DAOException {
		try {
			TypedQuery<Coa> query = em.createQuery("SELECT c FROM Coa c", Coa.class);
			return query.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Fail to find COA", pe);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void createCcoa(CurrencyChartOfAccount ccoa) throws DAOException {
		try {
			em.persist(ccoa);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("fail to create ccoa", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int findSetupValueByVariable(String variable) throws DAOException {
		int result = 0;
		try {
			Query q = em.createNativeQuery("SELECT s.value FROM Setup s WHERE s.variable=?1");
			q.setParameter("1", variable);
			result = Integer.parseInt((String) q.getSingleResult());
			em.flush();
		} catch (NoResultException e) {
			return 0;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of User", pe);
		}
		return result;
	}

}
