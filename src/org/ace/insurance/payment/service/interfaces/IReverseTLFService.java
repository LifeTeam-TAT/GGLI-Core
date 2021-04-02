package org.ace.insurance.payment.service.interfaces;

import java.util.List;

import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.TLF.TLFVoucherCriteria;
import org.ace.java.component.SystemException;

public interface IReverseTLFService {

	void reverseTLFAndPayment(String receiptNo);

	List<Payment> findPaymentByReceiptNo(TLFVoucherCriteria criteria) throws SystemException;

}