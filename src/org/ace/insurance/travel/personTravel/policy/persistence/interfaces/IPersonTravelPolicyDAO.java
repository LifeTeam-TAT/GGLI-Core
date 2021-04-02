package org.ace.insurance.travel.personTravel.policy.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.travel.personTravel.PersonTravelMonthlyIncomeReport;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.web.manage.report.travel.personTravel.criteria.PersonTravelCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IPersonTravelPolicyDAO {

	public void insert(PersonTravelPolicy personTravelPolicy) throws DAOException;

	public void update(PersonTravelPolicy personTravelPolicy) throws DAOException;

	public PersonTravelPolicy findPolicyById(String id) throws DAOException;

	public PersonTravelPolicy findPolicyByProposalId(String proposalId) throws DAOException;

	public List<Payment> findByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) throws DAOException;

	public List<PersonTravelPolicy> findPaymentOrderByReceiptNo(String receiptNo, String bpmsReceiptNo) throws DAOException;

	public List<PersonTravelMonthlyIncomeReport> findPersonTravelMonthlyIncome(PersonTravelCriteria criteria) throws DAOException;

	public String findPolicyIdByProposalId(String proposalId) throws DAOException;

	void deletePersonTravelPolicy(PersonTravelPolicy policy) throws DAOException;
}
