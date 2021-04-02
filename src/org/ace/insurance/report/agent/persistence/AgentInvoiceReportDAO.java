package org.ace.insurance.report.agent.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.report.agent.AgentInvoiceCriteria;
import org.ace.insurance.report.agent.persistence.interfaces.IAgentInvoiceReportDAO;
import org.ace.insurance.web.manage.agent.payment.AgentCommissionReportDTO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("AgentInvoiceReportDAO")
public class AgentInvoiceReportDAO extends BasicDAO implements IAgentInvoiceReportDAO {

	@Resource(name = "PaymentDAO")
	protected IPaymentDAO paymentDAO;

	public List<AgentCommission> find(AgentInvoiceCriteria criteria) throws DAOException {
		List<AgentCommission> resultList = new ArrayList<AgentCommission>();
		try {
			String policy = "";
			String policyHistory = "";
			StringBuffer criteriaString = new StringBuffer();
			switch (criteria.getInsuranceType()) {
				case SINGLE_PREMIUM_CREDIT_LIFE:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
				case STUDENT_LIFE:
				case FARMER:
				case PUBLIC_TERM_LIFE:
				case LIFE:
				case PERSONAL_ACCIDENT:
				case SHORT_ENDOWMENT_LIFE:
					policy = " LifePolicy p ";
					policyHistory = " LifePolicyHistory p ";
					break;
				case MEDICAL:
				case HEALTH:
				case MICRO_HEALTH:
				case CRITICAL_ILLNESS:
					policy = " MedicalPolicy p ";
					break;
				case GROUP_FARMER:
					policy = " GroupFarmerProposal p ";
					break;
				case PERSON_TRAVEL:
					policy = " PersonTravelPolicy p ";
					break;
				default:
					break;
			}

			if (criteria.getInvoiceNo() != null && !criteria.getInvoiceNo().isEmpty()) {
				criteriaString.append(" AND ac.invoiceNo = :invoiceNo ");
			}

			if (criteria.getBranch() != null) {
				criteriaString.append(" AND p.branch.id = :branchId ");
			}

			if (criteria.getAgent() != null) {
				criteriaString.append(" AND ac.agent.id = :agentId ");
			}

			if (criteria.getInsuranceType().equals(InsuranceType.LIFE) || criteria.getInsuranceType().equals(InsuranceType.MEDICAL)
					|| InsuranceType.HEALTH.equals(criteria.getInsuranceType()) || InsuranceType.CRITICAL_ILLNESS.equals(criteria.getInsuranceType())
					|| criteria.getInsuranceType().equals(InsuranceType.SHORT_ENDOWMENT_LIFE) || criteria.getInsuranceType().equals(InsuranceType.STUDENT_LIFE)
					|| criteria.getInsuranceType().equals(InsuranceType.PUBLIC_TERM_LIFE) || criteria.getInsuranceType().equals(InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE)
					|| criteria.getInsuranceType().equals(InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE) || criteria.getInsuranceType().equals(InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE)) {
				criteriaString.append(" AND (ac.referenceType = :referenceType OR ac.referenceType = :billType) ");
			} else {
				criteriaString.append(" AND ac.referenceType = :referenceType ");
			}

			StringBuffer queryString = new StringBuffer();
			queryString.append(" SELECT DISTINCT(ac) ");
			queryString.append(" FROM AgentCommission ac, " + policy);
			queryString.append(" WHERE ac.referenceNo = p.id AND ac.invoiceNo IS NOT NULL " + criteriaString.toString());
			if (criteria.getInsuranceType().equals(InsuranceType.LIFE) || criteria.getInsuranceType().equals(InsuranceType.SHORT_ENDOWMENT_LIFE)) {
				queryString.append(" UNION ");
				queryString.append(" SELECT DISTINCT(ac) ");
				queryString.append(" FROM AgentCommission ac, " + policyHistory);
				queryString.append(" WHERE ac.referenceNo = p.policyReferenceNo AND ac.invoiceNo IS NOT NULL " + criteriaString.toString());
			}

			Query query = em.createQuery(queryString.toString());
			if (criteria.getInvoiceNo() != null && !criteria.getInvoiceNo().isEmpty()) {
				query.setParameter("invoiceNo", criteria.getInvoiceNo());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}

			switch (criteria.getInsuranceType()) {

				case FARMER:
					query.setParameter("referenceType", PolicyReferenceType.FARMER_POLICY);
					break;
				case PUBLIC_TERM_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY);
					query.setParameter("billType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY_BILL_COLLECTION);
					break;
				case LIFE:
					query.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
					query.setParameter("billType", PolicyReferenceType.LIFE_BILL_COLLECTION);
					break;
				case MEDICAL:
					query.setParameter("referenceType", PolicyReferenceType.MEDICAL_POLICY);
					query.setParameter("billType", PolicyReferenceType.MEDICAL_BILL_COLLECTION);
					break;
				case HEALTH:
					query.setParameter("referenceType", PolicyReferenceType.HEALTH_POLICY);
					query.setParameter("billType", PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
					break;
				case MICRO_HEALTH:
					query.setParameter("referenceType", PolicyReferenceType.MICRO_HEALTH_POLICY);
					break;
				case CRITICAL_ILLNESS:
					query.setParameter("referenceType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY);
					query.setParameter("billType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
					break;
				case SHORT_ENDOWMENT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY);
					query.setParameter("billType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;
				case PERSONAL_ACCIDENT:
					query.setParameter("referenceType", PolicyReferenceType.PA_POLICY);
					break;
				case GROUP_FARMER:
					query.setParameter("referenceType", PolicyReferenceType.GROUP_FARMER_PROPOSAL);
					break;
				case PERSON_TRAVEL:
					query.setParameter("referenceType", PolicyReferenceType.PERSON_TRAVEL_POLICY);
					break;
				case STUDENT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.STUDENT_LIFE_POLICY);
					query.setParameter("billType", PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					query.setParameter("billType", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					query.setParameter("billType", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY);
					query.setParameter("billType", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;
				default:
					break;
			}
			resultList = query.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Agent Invoice Report by criteria.", pe);
		}
		return resultList;
	}

	public List<AgentCommissionReportDTO> getAgentCommissionReportDTOForLife(List<AgentCommission> agentCommissionList) throws DAOException {
		List<AgentCommissionReportDTO> dtoList = new ArrayList<AgentCommissionReportDTO>();

		for (AgentCommission agentCommission : agentCommissionList) {
			StringBuffer query = new StringBuffer();
			query.append("SELECT l FROM LifePolicy l WHERE l.id = :policyId");

			Query q = em.createQuery(query.toString());
			q.setParameter("policyId", agentCommission.getReferenceNo());

			LifePolicy policy = (LifePolicy) q.getSingleResult();

			// Search Payment with receiptNo of agent commission
			List<Payment> paymentList = paymentDAO.findPaymentByReceiptNo(agentCommission.getReceiptNo());
			Payment payment = paymentList.get(0);

			// determine within one year or not
			double commissionPercentage = 0.0;
			double premium = 0.0;
			double renewalPremium = 0.0;
			// within one year payment
			if (agentCommission.getEntryType() == AgentCommissionEntryType.UNDERWRITING) {
				commissionPercentage = agentCommission.getPercentage();
				premium = agentCommission.getPremium();
				renewalPremium = 0.0;
			} else if (agentCommission.getEntryType() == AgentCommissionEntryType.RENEWAL || agentCommission.getEntryType() == AgentCommissionEntryType.RENEWAL_FIRST) {
				commissionPercentage = agentCommission.getPercentage();
				premium = 0.0;
				renewalPremium = policy.getPremium();
			}

			AgentCommissionReportDTO dto = new AgentCommissionReportDTO(payment.getReceiptNo(), payment.getPaymentDate(), policy.getPolicyNo(), agentCommission.getCommission(),
					agentCommission.getWithHoldingTax(), commissionPercentage, premium, renewalPremium, policy.getTotalSumInsured(), policy.getCustomerName());
			dtoList.add(dto);
		}
		return dtoList;
	}
}
