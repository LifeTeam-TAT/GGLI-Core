package org.ace.insurance.payment.persistence.interfacs;

import org.ace.insurance.payment.ReversePaymentTransactionLog;
import org.ace.java.component.persistence.exception.DAOException;

public interface IReversePaymentTransactionLogDAO {

	void insertLog(ReversePaymentTransactionLog log)throws DAOException;

}