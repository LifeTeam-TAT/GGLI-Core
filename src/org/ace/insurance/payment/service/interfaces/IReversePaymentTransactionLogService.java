package org.ace.insurance.payment.service.interfaces;

import org.ace.insurance.payment.ReversePaymentTransactionLog;
import org.ace.java.component.SystemException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface IReversePaymentTransactionLogService {

	void insertLog(ReversePaymentTransactionLog log) throws SystemException;

}