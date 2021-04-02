package org.ace.insurance.payment.service;

import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.ReversePaymentTransactionLog;
import org.ace.insurance.payment.persistence.interfacs.IAgentCommissionDAO;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.persistence.interfacs.ITLFDAO;
import org.ace.insurance.payment.service.interfaces.IReversePaymentTransactionLogService;
import org.ace.insurance.payment.service.interfaces.IReverseTLFService;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "ReverseTLFService")
public class ReverseTLFService implements IReverseTLFService {

	@Autowired
	private IPaymentDAO paymentDAO;

	@Autowired
	private ITLFDAO tlfDAO;

	@Autowired
	private IAgentCommissionDAO agentCommissionDAO;

	@Autowired
	private IReversePaymentTransactionLogService logService;

	@Autowired
	private IUserProcessService userService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void reverseTLFAndPayment(String receiptNo) throws SystemException {
		try {
			paymentDAO.reversetPaymentByReceiptNo(receiptNo);
			tlfDAO.reverseBytlfNo(receiptNo);
			agentCommissionDAO.removeAgentcomissionByReceiptNo(receiptNo);
			User user = userService.getLoginUser();
			ReversePaymentTransactionLog log = new ReversePaymentTransactionLog();
			log.setReceiptNo(receiptNo);
			log.setReverseDate(new Date());
			log.setUserId(user.getId());
			logService.insertLog(log);
		} catch (DAOException e) {
			throw new SystemException("Fail to rever tlf and payment ", e.getErrorCode());
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPaymentByReceiptNo(TLFVoucherCriteria criteria) throws SystemException {
		try {
			return paymentDAO.findPaymentByReceiptNoAndComplete(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to find payment by receipt no");
		}
	}

}
