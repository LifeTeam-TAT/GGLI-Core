package org.ace.insurance.report.life.persistence.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.Payment;
import org.ace.java.component.persistence.exception.DAOException;

public interface ILifePremiumLedgerDAO {

	public List<Payment> findCompletePaymentByReferenceNo(String referenceNo) throws DAOException;

	public Date getLifeSurveyDate(String proposalId) throws DAOException;

}
