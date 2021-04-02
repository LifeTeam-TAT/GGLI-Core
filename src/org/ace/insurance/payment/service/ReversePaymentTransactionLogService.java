package org.ace.insurance.payment.service;

import org.ace.insurance.payment.ReversePaymentTransactionLog;
import org.ace.insurance.payment.persistence.interfacs.IReversePaymentTransactionLogDAO;
import org.ace.insurance.payment.service.interfaces.IReversePaymentTransactionLogService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ReversePaymentTransactionLogService")
public class ReversePaymentTransactionLogService extends BaseService implements IReversePaymentTransactionLogService{

	@Autowired
	private IReversePaymentTransactionLogDAO logDAO;
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertLog(ReversePaymentTransactionLog log) throws SystemException{
		try {
			logDAO.insertLog(log);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(),"fail to insert log");
		}
	}
}
