package org.ace.insurance.payment.persistence;

import javax.persistence.PersistenceException;

import org.ace.insurance.payment.ReversePaymentTransactionLog;
import org.ace.insurance.payment.persistence.interfacs.IReversePaymentTransactionLogDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ReversePaymentTransactionLogDAO")
public class ReversePaymentTransactionLogDAO extends BasicDAO implements IReversePaymentTransactionLogDAO {
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertLog(ReversePaymentTransactionLog log) throws DAOException{
		try {
			em.persist(log);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Fail to insert log", pe);
		}
	}
}
