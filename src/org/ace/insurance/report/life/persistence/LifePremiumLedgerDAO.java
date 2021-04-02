package org.ace.insurance.report.life.persistence;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.life.persistence.interfaces.ILifePremiumLedgerDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("LifePremiumLedgerDAO")
public class LifePremiumLedgerDAO extends BasicDAO implements ILifePremiumLedgerDAO {

	@Override
	public List<Payment> findCompletePaymentByReferenceNo(String referenceNo) throws DAOException {
		List<Payment> paymentList = null;
		try {
			StringBuffer stBuffer = new StringBuffer();
			stBuffer.append("SELECT p FROM Payment p WHERE  p.referenceNo = :referenceNo AND p.complete = true AND p.reverse = false  ORDER BY p.paymentDate");
			Query q = em.createQuery(stBuffer.toString());
			q.setParameter("referenceNo", referenceNo);
			paymentList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find CompletePayments by policy Id.", pe);
		}
		return paymentList;
	}

	@Override
	public Date getLifeSurveyDate(String proposalId) throws DAOException {
		Date surveyDate;
		try {

			Query query = em.createQuery("SELECT l.date FROM LifeSurvey l WHERE l.lifeProposal.id = :id");
			query.setParameter("id", proposalId);
			surveyDate = (Date) query.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeSurveyDate by proposal Id.", pe);
		}
		return surveyDate;
	}

}
