package org.ace.insurance.report.agent.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PolicyHistoryEntryType;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.Utils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.product.Product;
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.report.agent.AgentCommissionDetailCriteria;
import org.ace.insurance.report.agent.AgentSanctionCriteria;
import org.ace.insurance.report.agent.AgentSanctionReport;
import org.ace.insurance.report.agent.persistence.interfaces.IAgentSanctionDAO;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("AgentSanctionDAO")
public class AgentSanctionDAO extends BasicDAO implements IAgentSanctionDAO {

	@Resource(name = "IDConfigLoader")
	protected IDConfigLoader configLoader;

	@Resource(name = "UserProcessService")
	protected IUserProcessService userProcessService;

	@Resource(name = "PaymentDAO")
	protected IPaymentDAO paymentDAO;

	List<AgentCommission> resultCommission = new ArrayList<AgentCommission>();
	AgentSanctionReport result2;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentSanctionReport> findIndividual(AgentSanctionCriteria criteria, AgentComissionInfo agentComissionInfo, boolean isEnquiry) throws DAOException {
		List<AgentSanctionReport> result = new ArrayList<AgentSanctionReport>();
		switch (criteria.getInsuranceType()) {
			case SINGLE_PREMIUM_CREDIT_LIFE:
			case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
			case SINGLE_PREMIUM_ENDOWMENT_LIFE:
			case STUDENT_LIFE:
			case LIFE:
			case PERSONAL_ACCIDENT:
			case FARMER:
			case PUBLIC_TERM_LIFE:
			case SHORT_ENDOWMENT_LIFE:
				result.addAll(findForLifeIndividual(criteria, agentComissionInfo, isEnquiry));
				break;
			case MEDICAL:
				result.addAll(findForMedicalIndividual(criteria, agentComissionInfo, isEnquiry, PolicyReferenceType.MEDICAL_POLICY, PolicyReferenceType.MEDICAL_BILL_COLLECTION));
				break;
			case HEALTH:
				result.addAll(
						findForMedicalIndividual(criteria, agentComissionInfo, isEnquiry, PolicyReferenceType.HEALTH_POLICY, PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION));
				break;
			case MICRO_HEALTH:
				result.addAll(findForMedicalIndividual(criteria, agentComissionInfo, isEnquiry, PolicyReferenceType.MICRO_HEALTH_POLICY, null));
				break;
			case CRITICAL_ILLNESS:
				result.addAll(findForMedicalIndividual(criteria, agentComissionInfo, isEnquiry, PolicyReferenceType.CRITICAL_ILLNESS_POLICY,
						PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION));
				break;
			case GROUP_MICRO_HEALTH:
				result.addAll(findForGroupMicroHealth(criteria, agentComissionInfo, isEnquiry, PolicyReferenceType.GROUP_MICRO_HEALTH,
						PolicyReferenceType.GROUP_MICRO_HEALTH_BILL_COLLECTION));
				break;

			case PERSON_TRAVEL:
				result.addAll(findForPersonTravelIndividual(criteria, agentComissionInfo, isEnquiry));
				break;
			case GROUP_FARMER:
				result.addAll(findForGroupFarmerIndividual(criteria, agentComissionInfo, isEnquiry, PolicyReferenceType.GROUP_FARMER_PROPOSAL, null));
				break;
			default:
				result.addAll(findForLifeIndividual(criteria, agentComissionInfo, isEnquiry));
				break;
		}
		return result;
	}

	private List<AgentSanctionReport> findForGroupMicroHealth(AgentSanctionCriteria criteria, AgentComissionInfo agentComissionInfo, boolean isEnquiry,
			PolicyReferenceType groupMicroHealth, PolicyReferenceType groupMicroHealthBillCollection) {
		List<Object[]> agentComList = new ArrayList<Object[]>();
		List<AgentSanctionReport> result = new ArrayList<AgentSanctionReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT ac, a, m, p FROM AgentCommission ac, Agent a, GroupMicroHealth m, Payment p "
					+ " WHERE ac.agent.id = a.id AND ac.referenceNo = m.id AND ac.referenceNo = p.referenceNo AND ac.id = :agentCommissionId");
			query.append(" AND (ac.referenceType = :referenceTypePolicy ");

			if (groupMicroHealthBillCollection != null) {
				query.append(" OR ac.referenceType = :referenceTypeBillCollection) ");
			} else {
				query.append(" ) ");
			}

			if (!isEnquiry) {
				query.append(" AND ac.isPaid = FALSE");
			}
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}

			Query q = em.createQuery(query.toString());
			q.setParameter("agentCommissionId", agentComissionInfo.getId());
			q.setParameter("referenceTypePolicy", groupMicroHealth);
			if (groupMicroHealthBillCollection != null) {
				q.setParameter("referenceTypeBillCollection", groupMicroHealthBillCollection);
			}
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			agentComList = q.getResultList();
			for (Object[] items : agentComList) {
				AgentCommission agentComission = (AgentCommission) items[0];
				Agent agent = (Agent) items[1];
				GroupMicroHealth policy = (GroupMicroHealth) items[2];
				Payment payment = (Payment) items[3];
				result.add(new AgentSanctionReport(agent.getId(), payment.getReceiptNo(), " ", policy.getProposalNo(), policy.getTotalPremium(), policy.getTotalPremium(),
						payment.getReinstatementPremium(), agentComission.getPercentage(), agentComission.getCommission(), agent.getFullName(), agent.getCodeNo(),
						agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(), "Medical Insurance", agent.getContentInfo().getMobile(), agent.getFullAddress(),
						agentComission, agentComission.getSanctionNo()));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private List<AgentSanctionReport> findForLifeIndividual(AgentSanctionCriteria criteria, AgentComissionInfo a, boolean isEnquiry) throws DAOException {
		List<Object[]> agentComList = new ArrayList<Object[]>();
		List<AgentSanctionReport> result = new ArrayList<AgentSanctionReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT ac, a, m, p FROM AgentCommission ac, Agent a, LifePolicy m, Payment p "
					+ " WHERE ac.isPaid = FALSE AND ac.agent.id = a.id AND ac.referenceNo = m.id AND ac.receiptNo = p.receiptNo AND ac.id = :agentCommissionId");
			if (criteria.getInsuranceType() != null
					&& (InsuranceType.LIFE.equals(criteria.getInsuranceType()) || InsuranceType.SHORT_ENDOWMENT_LIFE.equals(criteria.getInsuranceType())
							|| InsuranceType.STUDENT_LIFE.equals(criteria.getInsuranceType()) || InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType())
							|| InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE.equals(criteria.getInsuranceType())
							|| InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType()))) {
				query.append(" AND (ac.referenceType = :referenceType OR ac.referenceType = :refBill)  ");
			} else {
				query.append(" AND ac.referenceType = :referenceType");
			}
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}
			// if(a.getId() != null){
			// query.append(" AND ac.agent.id = :agentId");
			// }

			Query q = em.createQuery(query.toString());
			q.setParameter("agentCommissionId", a.getId());
			// q.setParameter("sanctionNo", a.getSanctionNo());
			if (criteria.getInsuranceType() != null) {
				switch (criteria.getInsuranceType()) {
					case STUDENT_LIFE:
						q.setParameter("referenceType", PolicyReferenceType.STUDENT_LIFE_POLICY);
						q.setParameter("refBill", PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
						break;
					case PERSONAL_ACCIDENT:
						q.setParameter("referenceType", PolicyReferenceType.PA_POLICY);
						break;
					case FARMER:
						q.setParameter("referenceType", PolicyReferenceType.FARMER_POLICY);
						break;
					case SHORT_ENDOWMENT_LIFE:
						q.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY);
						q.setParameter("refBill", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
						break;
					case PUBLIC_TERM_LIFE:
						q.setParameter("referenceType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY);

						break;
					case GROUP_FARMER:
						q.setParameter("referenceType", PolicyReferenceType.GROUP_FARMER_PROPOSAL);
						break;
					case SINGLE_PREMIUM_CREDIT_LIFE:
						q.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
						q.setParameter("refBill", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
						break;
					case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
						q.setParameter("referenceType", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
						q.setParameter("refBill", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
						break;
					case SINGLE_PREMIUM_ENDOWMENT_LIFE:
						q.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY);
						q.setParameter("refBill", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION);
						break;
					default:
						q.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
						q.setParameter("refBill", PolicyReferenceType.LIFE_BILL_COLLECTION);
						break;
				}
			}
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}
			// if(a.getId() != null){
			// q.setParameter("agentId", a.getId());
			// }
			agentComList = q.getResultList();
			InsuranceType insuranceType = criteria.getInsuranceType();
			String insuranceTypeStr = insuranceType
					.equals(InsuranceType.PERSONAL_ACCIDENT)
							? "Personal Accident Insurance"
							: insuranceType.equals(InsuranceType.FARMER) ? "Farmer Insurance"
									: insuranceType.equals(InsuranceType.PUBLIC_TERM_LIFE) ? "Public Term Life Insurance"
											: insuranceType.equals(InsuranceType.SHORT_ENDOWMENT_LIFE) ? "Short Term Endowment Life"
													: insuranceType.equals(InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE) ? "Single Premium Credit Life"
															: insuranceType.equals(InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE) ? "Single Premium Endowment Life"
																	: insuranceType.equals(InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE)
																			? "Short Term Single Premium Credit Life"
																			: insuranceType.equals(InsuranceType.STUDENT_LIFE) ? "Student Life Insurance" : "Life Insurance";
			for (Object[] items : agentComList) {
				AgentCommission agentComission = (AgentCommission) items[0];
				Agent agent = (Agent) items[1];
				LifePolicy policy = (LifePolicy) items[2];
				Payment payment = (Payment) items[3];
				result.add(new AgentSanctionReport(agent.getId(), payment.getReceiptNo(), policy.getCustomerName(), policy.getPolicyNo(), policy.getTotalSumInsured(),
						policy.getTotalTermPremium(), payment.getReinstatementPremium(), agentComission.getPercentage(), agentComission.getCommission(), agent.getFullName(),
						agent.getCodeNo(), agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(), insuranceTypeStr, agent.getContentInfo().getMobile(),
						agent.getFullAddress(), agentComission, agentComission.getSanctionNo()));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private List<AgentSanctionReport> findForMedicalIndividual(AgentSanctionCriteria criteria, AgentComissionInfo a, boolean isEnquiry, PolicyReferenceType p1,
			PolicyReferenceType p2) throws DAOException {
		List<Object[]> agentComList = new ArrayList<Object[]>();
		List<AgentSanctionReport> result = new ArrayList<AgentSanctionReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT ac, a, m, p FROM AgentCommission ac, Agent a, MedicalPolicy m, Payment p "
					+ "  WHERE ac.agent.id = a.id AND ac.referenceNo = m.id AND ac.receiptNo = p.receiptNo AND ac.id = :agentCommissionId");
			query.append(" AND (ac.referenceType = :referenceTypePolicy ");

			if (p2 != null) {
				query.append(" OR ac.referenceType = :referenceTypeBillCollection) ");
			} else {
				query.append(" ) ");
			}

			if (!isEnquiry) {
				query.append(" AND ac.isPaid = FALSE");
			}
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}

			Query q = em.createQuery(query.toString());
			q.setParameter("agentCommissionId", a.getId());
			q.setParameter("referenceTypePolicy", p1);
			if (p2 != null) {
				q.setParameter("referenceTypeBillCollection", p2);
			}
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			agentComList = q.getResultList();
			for (Object[] items : agentComList) {
				AgentCommission agentComission = (AgentCommission) items[0];
				Agent agent = (Agent) items[1];
				MedicalPolicy policy = (MedicalPolicy) items[2];
				Payment payment = (Payment) items[3];
				result.add(new AgentSanctionReport(agent.getId(), payment.getReceiptNo(), policy.getCustomerName(), policy.getPolicyNo(), policy.getTotalSumInsured(),
						policy.getTotalTermPremium(), payment.getReinstatementPremium(), agentComission.getPercentage(), agentComission.getCommission(), agent.getFullName(),
						agent.getCodeNo(), agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(), "Medical Insurance", agent.getContentInfo().getMobile(),
						agent.getFullAddress(), agentComission, agentComission.getSanctionNo()));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;
	}

	// only for life agent sanction print
	@Transactional(propagation = Propagation.REQUIRED)
	public AgentSanctionReport findLifeIndividualForSanction(AgentSanctionCriteria criteria, AgentComissionInfo a) throws DAOException {
		AgentSanctionReport result = null;
		try {
			// Search AgentCommission, Agent with agent commission id
			AgentCommission agentCommission = null;
			Agent agent = null;
			Payment payment = null;
			LifePolicy policy = null;
			String typeOfBody = "";
			StringBuffer query = new StringBuffer();
			query.append(" SELECT ac, a FROM AgentCommission ac, Agent a ");
			query.append(" WHERE ac.isPaid = FALSE AND ac.agent.id = a.id AND ac.id = :agentCommissionId ");
			query.append(" AND (ac.referenceType = :referenceType OR ac.referenceType = :refBill) ");

			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}

			Query q = em.createQuery(query.toString());
			q.setParameter("agentCommissionId", a.getId());

			switch (criteria.getInsuranceType()) {
				case LIFE:
					q.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.LIFE_BILL_COLLECTION);
					typeOfBody = "Life Insurance";
					break;
				case MEDICAL:
					break;
				case HEALTH:
					break;
				case MICRO_HEALTH:
					break;
				case CRITICAL_ILLNESS:
					break;
				case SHORT_ENDOWMENT_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
					typeOfBody = "Short Term Endowment Life Insurance";
					break;
				case PUBLIC_TERM_LIFE:
					q.setParameter("referenceType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY);
					q.setParameter("refBill", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY_BILL_COLLECTION);
					typeOfBody = "Public Term  Life Insurance";
					break;
				default:
					break;
			}

			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			Object[] obj = (Object[]) q.getSingleResult();
			agentCommission = (AgentCommission) obj[0];
			agent = (Agent) obj[1];

			// Search LifePolicy with referenceNo of agent commission
			try {
				q = em.createQuery("SELECT l FROM LifePolicy l WHERE l.id = :policyId");
				q.setParameter("policyId", agentCommission.getReferenceNo());
				policy = (LifePolicy) q.getSingleResult();
			} catch (NoResultException e) {
				// search in history
				q = em.createQuery("SELECT l FROM LifePolicyHistory l WHERE l.policyReferenceNo = :policyId");
				q.setParameter("policyId", agentCommission.getReferenceNo());
				LifePolicyHistory history = (LifePolicyHistory) q.getSingleResult();
				policy = new LifePolicy(history);
			}

			// Search Payment with receiptNo of agent commission
			List<Payment> paymentList = paymentDAO.findPaymentByReceiptNo(agentCommission.getReceiptNo());
			payment = paymentList.get(0);

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
				renewalPremium = agentCommission.getPremium();
			}

			result = new AgentSanctionReport(agent.getId(), payment.getReceiptNo(), policy.getCustomerName(), policy.getPolicyNo(), policy.getTotalSumInsured(), premium,
					renewalPremium, commissionPercentage, agentCommission.getCommission(), agent.getFullName(), agent.getCodeNo(), agent.getLiscenseNo(), criteria.getStartDate(),
					criteria.getEndDate(), typeOfBody, agent.getContentInfo().getMobile(), agent.getFullAddress(), agentCommission, agentCommission.getSanctionNo());
			em.flush();

		} catch (NoResultException pe) {
			result = null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;
	}

	// only for life agent sanction print from enquiry
	@Transactional(propagation = Propagation.REQUIRED)
	public AgentSanctionReport findLifeIndividualForSanctionEnquiry(AgentSanctionCriteria criteria, AgentComissionInfo a) throws DAOException {
		AgentSanctionReport result = null;
		try {
			// Search AgentCommission
			AgentCommission agentCommission = null;
			LifePolicy policy = null;
			Agent agent = null;
			StringBuffer query = new StringBuffer();
			query.append("SELECT ac, a FROM AgentCommission ac, Agent a WHERE ac.agent.id = a.id AND ac.id = :agentCommissionId");

			Query q = em.createQuery(query.toString());
			q.setParameter("agentCommissionId", a.getId());

			try {
				Object[] agentCom = (Object[]) q.getSingleResult();
				agentCommission = (AgentCommission) agentCom[0];
				agent = (Agent) agentCom[1];
			} catch (NoResultException e) {
				// do nothing
			}

			String lifePolicyQuery = "SELECT l FROM LifePolicy l WHERE l.id = :policyId";
			q = em.createQuery(lifePolicyQuery);
			q.setParameter("policyId", agentCommission.getReferenceNo());

			try {
				policy = (LifePolicy) q.getSingleResult();
			} catch (NoResultException e) {
				// search in history
				lifePolicyQuery = "SELECT l FROM LifePolicyHistory l WHERE l.policyReferenceNo = :policyId";
				q = em.createQuery(lifePolicyQuery);
				q.setParameter("policyId", agentCommission.getReferenceNo());
				try {
					LifePolicyHistory history = (LifePolicyHistory) q.getSingleResult();
					policy = new LifePolicy(history);
				} catch (NoResultException ee) {
					// do nothing
				}
			}

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
				renewalPremium = agentCommission.getPremium();
			}

			result = new AgentSanctionReport(agent.getId(), agentCommission.getReceiptNo(), policy.getCustomerName(), policy.getPolicyNo(), policy.getTotalSumInsured(), premium,
					renewalPremium, commissionPercentage, agentCommission.getCommission(), agent.getFullName(), agent.getCodeNo(), agent.getLiscenseNo(), criteria.getStartDate(),
					criteria.getEndDate(), "Life Insurance", agent.getContentInfo().getMobile(), agent.getFullAddress(), agentCommission, agentCommission.getSanctionNo());
			em.flush();

		} catch (NoResultException pe) {
			result = null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AgentComissionInfo> findAgents(AgentSanctionCriteria criteria) throws DAOException {
		List<AgentComissionInfo> result = new ArrayList<AgentComissionInfo>();
		List<Object[]> agentComList = new ArrayList<Object[]>();
		String branchCode = null;
		StringBuffer policyQuery = new StringBuffer();
		try {
			if (criteria.getBranch() == null) {
				if (configLoader.isCentralizedSystem()) {
					branchCode = userProcessService.getLoginUser().getBranch().getBranchCode();
				} else {
					branchCode = configLoader.getBranchCode();
				}
			} else {
				branchCode = criteria.getBranch().getBranchCode();
			}
			/** Create UNION ALL String **/
			StringBuffer unionQuery = new StringBuffer();
			/** Create query string **/
			StringBuffer criteriaString = new StringBuffer();
			StringBuffer historyCriteriaString = new StringBuffer();
			if (criteria.getStartDate() != null) {
				criteriaString.append(" AND a.commissionStartDate >= :startDate");
				historyCriteriaString.append(" AND a1.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				criteriaString.append(" AND a.commissionStartDate <= :endDate ");
				historyCriteriaString.append(" AND a1.commissionStartDate <= :endDate ");
			}

			if (InsuranceType.LIFE.equals(criteria.getInsuranceType()) || InsuranceType.MEDICAL.equals(criteria.getInsuranceType())
					|| InsuranceType.SHORT_ENDOWMENT_LIFE.equals(criteria.getInsuranceType()) || InsuranceType.HEALTH.equals(criteria.getInsuranceType())
					|| InsuranceType.CRITICAL_ILLNESS.equals(criteria.getInsuranceType()) || InsuranceType.STUDENT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.PUBLIC_TERM_LIFE.equals(criteria.getInsuranceType()) || InsuranceType.SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SINGLE_PREMIUM_ENDOWMENT_LIFE.equals(criteria.getInsuranceType())
					|| InsuranceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE.equals(criteria.getInsuranceType())) {
				criteriaString.append(" AND (a.referenceType = :referenceType OR a.referenceType = :billReferenceType) AND a.CUR = :currency");
				historyCriteriaString.append(" AND (a1.referenceType = :referenceType OR a1.referenceType = :billReferenceType) AND a1.CUR = :currency");
			} else {
				criteriaString.append(" AND a.referenceType = :referenceType AND a.CUR = :currency");
				historyCriteriaString.append(" AND a1.referenceType = :referenceType AND a1.CUR = :currency");
			}

			if (criteria.getAgent() != null) {
				criteriaString.append(" AND a.agent.id = :agentId");
				historyCriteriaString.append(" AND a1.agent.id = :agentId");
			}

			if (criteria.getReceiptNo() != null && !criteria.getReceiptNo().isEmpty()) {
				criteriaString.append(" AND a.receiptNo = :receiptNo");
				historyCriteriaString.append(" AND a1.receiptNo = :receiptNo");
			}

			if (null != criteria.getBpmsReceiptNo() && !criteria.getBpmsReceiptNo().isEmpty()) {
				criteriaString.append(" AND a.bpmsReceiptNo = :bpmsReceiptNo");
				historyCriteriaString.append(" AND a1.bpmsReceiptNo = :bpmsReceiptNo");
			}
			if (InsuranceType.GROUP_FARMER.equals(criteria.getInsuranceType()) || InsuranceType.GROUP_MICRO_HEALTH.equals(criteria.getInsuranceType())) {
				if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
					criteriaString.append(" AND m.proposalNo = :policyNo");
					historyCriteriaString.append(" AND m1.proposalNo = :policyNo");
				}
			} else {
				if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
					criteriaString.append(" AND m.policyNo = :policyNo");
					historyCriteriaString.append(" AND m1.policyNo = :policyNo");
				}
			}

			if (InsuranceType.GROUP_FARMER.equals(criteria.getInsuranceType()) || InsuranceType.GROUP_MICRO_HEALTH.equals(criteria.getInsuranceType())) {
				policyQuery.append(" SELECT ag.initialId, ag.name, ag.codeNo, ag.liscenseNo, ag.contentInfo.mobile,");
				policyQuery.append(" ag.residentAddress.residentAddress, t.name, t.province.name, t.province.country.name,");
				policyQuery.append(" a.commission, a.referenceType, a.id, a.receiptNo,a.bpmsReceiptNo,m.proposalNo,a.sanctionNo,m.branch.name");
			} else {
				policyQuery.append(" SELECT ag.initialId, ag.name, ag.codeNo, ag.liscenseNo, ag.contentInfo.mobile,");
				policyQuery.append(" ag.residentAddress.residentAddress, t.name, t.province.name, t.province.country.name,");
				policyQuery.append(" a.commission, a.referenceType, a.id, a.receiptNo,a.bpmsReceiptNo, m.policyNo, a.sanctionNo,m.branch.name");
			}

			switch (criteria.getInsuranceType()) {
				case LIFE:
				case STUDENT_LIFE:
				case SINGLE_PREMIUM_CREDIT_LIFE:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
				case SHORT_ENDOWMENT_LIFE:
					policyQuery.append(" FROM AgentCommission a JOIN a.agent ag LEFT OUTER JOIN ag.residentAddress.township t, LifePolicy m WHERE a.referenceNo = m.id");
					unionQuery.append(" UNION ALL ");
					unionQuery.append(" SELECT ag1.initialId, ag1.name, ag1.codeNo, ag1.liscenseNo, ag1.contentInfo.mobile,");
					unionQuery.append(" ag1.residentAddress.residentAddress, t1.name, t1.province.name, t1.province.country.name,");
					unionQuery.append(" a1.commission, a1.referenceType, a1.id, a1.receiptNo,a1.bpmsReceiptNo, m1.policyNo, a1.sanctionNo,m1.branch.name");
					unionQuery.append(" FROM AgentCommission a1 JOIN a1.agent ag1 LEFT OUTER JOIN ag1.residentAddress.township t1, ");
					unionQuery.append(" LifePolicyHistory m1 WHERE a1.referenceNo = m1.policyReferenceNo ");
					unionQuery.append(" AND a1.status = FALSE ");
					unionQuery.append(historyCriteriaString.toString());
					break;
				case PERSONAL_ACCIDENT:
				case FARMER:
					policyQuery.append(" FROM AgentCommission a JOIN a.agent ag LEFT OUTER JOIN ag.residentAddress.township t, LifePolicy m WHERE a.referenceNo = m.id  ");
					break;
				case PUBLIC_TERM_LIFE:
					policyQuery.append(" FROM AgentCommission a JOIN a.agent ag LEFT OUTER JOIN ag.residentAddress.township t, LifePolicy m WHERE a.referenceNo = m.id  ");
					break;
				case MEDICAL:
				case HEALTH:
				case MICRO_HEALTH:
				case CRITICAL_ILLNESS:
					policyQuery.append(" FROM AgentCommission a JOIN a.agent ag LEFT OUTER JOIN ag.residentAddress.township t, MedicalPolicy m WHERE a.referenceNo = m.id ");
					break;
				case GROUP_MICRO_HEALTH:
					policyQuery.append(" FROM AgentCommission a JOIN a.agent ag LEFT OUTER JOIN ag.residentAddress.township t, GroupMicroHealth m WHERE a.referenceNo = m.id ");
					break;
				case PERSON_TRAVEL:
					policyQuery.append(" FROM AgentCommission a JOIN a.agent ag LEFT OUTER JOIN ag.residentAddress.township t, PersonTravelPolicy m WHERE a.referenceNo = m.id ");
					break;
				case GROUP_FARMER:
					policyQuery.append(" FROM AgentCommission a JOIN a.agent ag LEFT OUTER JOIN ag.residentAddress.township t, GroupFarmerProposal m WHERE a.referenceNo = m.id ");
					break;
				default:
					break;
			}

			policyQuery.append(" AND a.status = FALSE ");
			policyQuery.append(criteriaString.toString());
			policyQuery.append(unionQuery.toString());

			Query query = em.createQuery(policyQuery.toString());

			/** Set parameter data of query **/

			if (criteria.getStartDate() != null) {
				query.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				query.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getReceiptNo() != null && !criteria.getReceiptNo().isEmpty()) {
				query.setParameter("receiptNo", criteria.getReceiptNo());
			}
			if (null != criteria.getBpmsReceiptNo() && !criteria.getBpmsReceiptNo().isEmpty()) {
				query.setParameter("bpmsReceiptNo", criteria.getBpmsReceiptNo());
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				query.setParameter("policyNo", criteria.getPolicyNo());
			}
			query.setParameter("currency", criteria.getCurrency().getCurrencyCode());

			switch (criteria.getInsuranceType()) {
				case SHORT_ENDOWMENT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;
				case STUDENT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.STUDENT_LIFE_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION);
					break;
				case LIFE:
					query.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.LIFE_BILL_COLLECTION);
					break;
				case PERSONAL_ACCIDENT:
					query.setParameter("referenceType", PolicyReferenceType.PA_POLICY);
					break;
				case FARMER:
					query.setParameter("referenceType", PolicyReferenceType.FARMER_POLICY);
					break;
				case PUBLIC_TERM_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.PUBLIC_TERM_LIFE_POLICY_BILL_COLLECTION);

					break;
				case MEDICAL:
					query.setParameter("referenceType", PolicyReferenceType.MEDICAL_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.MEDICAL_BILL_COLLECTION);
					break;
				case HEALTH:
					query.setParameter("referenceType", PolicyReferenceType.HEALTH_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION);
					break;
				case MICRO_HEALTH:
					query.setParameter("referenceType", PolicyReferenceType.MICRO_HEALTH_POLICY);
					break;
				case GROUP_MICRO_HEALTH:
					query.setParameter("referenceType", PolicyReferenceType.GROUP_MICRO_HEALTH);
					break;
				case CRITICAL_ILLNESS:
					query.setParameter("referenceType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION);
					break;
				case PERSON_TRAVEL:
					query.setParameter("referenceType", PolicyReferenceType.PERSON_TRAVEL_POLICY);
					break;
				case GROUP_FARMER:
					query.setParameter("referenceType", PolicyReferenceType.GROUP_FARMER_PROPOSAL);
					break;
				case SINGLE_PREMIUM_CREDIT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				case SINGLE_PREMIUM_ENDOWMENT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_BILL_COLLECTION);
					break;
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE:
					query.setParameter("referenceType", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY);
					query.setParameter("billReferenceType", PolicyReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_BILL_COLLECTION);
					break;
				default:
					break;
			}

			agentComList = query.getResultList();

			String agentName = null;
			Name name = null;
			String codeNo = null;
			String liscenseNo = null;
			String mobile = null;
			String address = null;
			double commission = 0.0;
			PolicyReferenceType ref = null;
			String agentCommissionId = null;
			String policyNo = null;
			String receiptNo = null;
			String bpmsReceiptNo = null;
			String typeOfProduct = null;
			String sanctionNo = null;
			String currencyCode = null;
			String currencySymbol = null;
			String branchName = "";
			for (Object[] items : agentComList) {
				agentName = (String) items[0];
				name = (Name) items[1];
				agentName = agentName + " " + name.getFullName();
				codeNo = (String) items[2];
				liscenseNo = (String) items[3];
				mobile = (String) items[4];
				address = (String) items[5];
				address = address + ((String) items[6]) + ((String) items[7]) + ((String) items[8]);
				commission = (Double) items[9];
				ref = (PolicyReferenceType) items[10];
				agentCommissionId = (String) items[11];
				receiptNo = (String) items[12];
				bpmsReceiptNo = (String) items[13];
				policyNo = (String) items[14];
				sanctionNo = (String) items[15];
				branchName = (String) items[16];
				switch (criteria.getInsuranceType()) {
					case SHORT_ENDOWMENT_LIFE:
					case LIFE:
						typeOfProduct = "Life Insurance";
						break;
					case PERSONAL_ACCIDENT:
						typeOfProduct = "Personal Accident Insurance";
						break;
					case GROUP_FARMER:
					case FARMER:
						typeOfProduct = "Farmer Insurance";
						break;
					case PUBLIC_TERM_LIFE:
						typeOfProduct = "PublicTermLife Insurance";
						break;
					case MEDICAL:
						typeOfProduct = "Medical Insurance";
						break;

					case PERSON_TRAVEL:
						typeOfProduct = "Person Travel Insurance";
						break;
					default:
						break;
				}

				result.add(new AgentComissionInfo(agentCommissionId, policyNo, receiptNo, bpmsReceiptNo, agentName, codeNo, mobile, address, commission, liscenseNo,
						criteria.getStartDate(), criteria.getEndDate(), typeOfProduct, ref, sanctionNo, currencyCode, currencySymbol, branchName));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Agent", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSanction(AgentCommission agentCommission) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE AgentCommission a SET a.status = :status where a.id = :id");
			q.setParameter("id", agentCommission.getId());
			q.setParameter("status", true);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update AgentCommission", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAgentCommissionStaus(String agentCommissionId, String sanctionNo, Date sanctionDate, double withHoldingTax, double homeWithHoldingTax) throws DAOException {
		try {
			Query q = em.createQuery(
					"UPDATE AgentCommission a SET a.status = :status , a.sanctionDate = :sanctionDate , a.sanctionNo = :sanctionNo, a.withHoldingTax = :withHoldingTax, a.homeWithHoldingTax = :homeWithHoldingTax where a.id = :id");
			q.setParameter("id", agentCommissionId);
			q.setParameter("status", true);
			q.setParameter("sanctionDate", sanctionDate);
			q.setParameter("sanctionNo", sanctionNo);
			q.setParameter("withHoldingTax", withHoldingTax);
			q.setParameter("homeWithHoldingTax", homeWithHoldingTax);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update AgentCommission", pe);
		}

	}

	// newly added
	@Transactional(propagation = Propagation.REQUIRED)
	public AgentSanctionReport findIndividualAgentCommission(AgentCommissionDetailCriteria criteria, AgentCommission a) throws DAOException {
		// result2 = new ArrayList<AgentSanctionReport>();
		// AgentSanctionReport result2=null;
		if (criteria.getInsuranceType() == null) {
			findForLifeIndividualAgentCommission(criteria, a);

		} else if (criteria.getInsuranceType().equals(InsuranceType.LIFE)) {
			findForLifeIndividualAgentCommission(criteria, a);
		}
		return result2;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void findForLifeIndividualAgentCommission(AgentCommissionDetailCriteria criteria, AgentCommission a) throws DAOException {
		List<Object[]> agentComList = new ArrayList<Object[]>();
		try {
			StringBuffer query = new StringBuffer();
			query.append(
					"SELECT ac, a, m, p FROM AgentCommission ac, Agent a, LifePolicy m, Payment p where ac.agent.id = a.id AND ac.referenceNo = m.id AND ac.referenceNo = p.referenceNo AND ac.agent.id = :agentId AND ac.id= :commissionId");
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}
			if (criteria.getInsuranceType() != null) {
				query.append(" AND ac.referenceType = :referenceType");
			}
			Query q = em.createQuery(query.toString());
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetStartDate(criteria.getEndDate()));
			}

			q.setParameter("agentId", a.getAgent().getId());
			q.setParameter("commissionId", a.getId());
			if (criteria.getInsuranceType() != null) {
				q.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
			}

			agentComList = q.getResultList();

			for (Object[] items : agentComList) {
				AgentCommission agentComission = (AgentCommission) items[0];
				Agent agent = (Agent) items[1];
				LifePolicy policy = (LifePolicy) items[2];
				Payment payment = (Payment) items[3];
				result2 = (new AgentSanctionReport(agent.getId(), payment.getReceiptNo(), policy.getCustomerName(), policy.getPolicyNo(), policy.getTotalSumInsured(),
						policy.getTotalPremium(), payment.getReinstatementPremium(), policy.getInsuredPersonInfo().get(0).getProduct().getFirstCommission(),
						agentComission.getCommission(), agent.getFullName(), agent.getCodeNo(), agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(),
						"Life Insurance", agent.getContentInfo().getMobile(), agent.getFullAddress(), agentComission, agentComission.getSanctionNo()));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentCommission> findIndividualAgentComission(AgentSanctionCriteria criteria, AgentComissionInfo a) throws DAOException {
		resultCommission = new ArrayList<AgentCommission>();
		if (criteria.getInsuranceType() == null) {
			resultCommission = findForLifeIndividualAgentComission(criteria, a);

		} else if (criteria.getInsuranceType().equals(InsuranceType.LIFE)) {
			resultCommission = findForLifeIndividualAgentComission(criteria, a);
		}
		return resultCommission;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentCommission> findForLifeIndividualAgentComission(AgentSanctionCriteria criteria, AgentComissionInfo a) throws DAOException {
		List<AgentCommission> agentComList = new ArrayList<AgentCommission>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT ac FROM AgentCommission ac where ac.status = FALSE");
			if (criteria.getInsuranceType() != null) {
				query.append(" AND ac.referenceType = :referenceType");
			}
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}
			if (a.getId() != null) {
				query.append(" AND ac.agent.id = :agentId");
			}
			Query q = em.createQuery(query.toString());
			if (criteria.getInsuranceType() != null) {

				q.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);
			}

			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}
			if (a.getId() != null) {
				q.setParameter("agentId", a.getId());
			}
			agentComList = q.getResultList();

			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return agentComList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentSanctionDTO> findAllAgentCommissionInfoByEnquiry(AgentSanctionCriteria criteria) throws DAOException {
		Map<String, AgentSanctionDTO> resultMap = new HashMap<String, AgentSanctionDTO>();
		StringBuffer policyQuery = new StringBuffer();
		StringBuffer criteriaBuffer = new StringBuffer();

		try {
			String policy = "";
			String policyHistory = "";
			switch (criteria.getInsuranceType()) {

				case LIFE:
					policy = " LifePolicy p ";
					policyHistory = " LifePolicyHistory p ";
					break;
				case HEALTH:
				case MICRO_HEALTH:
				case CRITICAL_ILLNESS:
				case MEDICAL:
					policy = " MedicalPolicy p ";
					break;
				case GROUP_FARMER:
					policy = " GroupFarmerProposal p";
					break;
				case GROUP_MICRO_HEALTH:
					policy = " GroupMicroHealth p";
					break;
				case PERSON_TRAVEL:
					policy = " PersonTravelPolicy p";
					break;
				default:
					break;
			}

			if (criteria.getAgent() != null) {
				criteriaBuffer.append(" AND a.agent.id = :agentId ");
			}

			if (criteria.getBranch() != null) {
				criteriaBuffer.append(" AND p.branch.id = :branchId ");
			}

			if (!org.ace.insurance.web.common.Utils.isEmpty(criteria.getSanctionNo())) {
				criteriaBuffer.append(" AND a.sanctionNo = :sanctionNo ");
			}

			policyQuery.append("SELECT a.agent, a.commission , a.referenceType, a.sanctionNo, a.sanctionDate ");
			policyQuery.append(" FROM AgentCommission a, " + policy + " WHERE a.referenceNo = p.id AND a.sanctionNo IS NOT NULL ");
			policyQuery.append(criteriaBuffer.toString());
			if (criteria.getInsuranceType().equals(InsuranceType.LIFE) || criteria.getInsuranceType().equals(InsuranceType.SHORT_ENDOWMENT_LIFE)) {
				policyQuery.append(" UNION ALL ");
				policyQuery.append(" SELECT a.agent, a.commission, a.referenceType, a.sanctionNo, a.sanctionDate ");
				policyQuery.append(" FROM AgentCommission a, " + policyHistory + " WHERE a.referenceNo = p.policyReferenceNo AND a.sanctionNo IS NOT NULL ");
				policyQuery.append(criteriaBuffer.toString());
			}

			Query query = em.createQuery(policyQuery.toString());
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}

			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}

			if (!org.ace.insurance.web.common.Utils.isEmpty(criteria.getSanctionNo())) {
				query.setParameter("sanctionNo", criteria.getSanctionNo());
			}

			List<Object[]> objectArr = query.getResultList();
			em.flush();
			Agent agent = null;
			double commission = 0.0;
			PolicyReferenceType ref = null;
			String sanctionNo = null;
			Date sanctionDate = null;
			AgentSanctionDTO dto = null;
			for (Object[] obj : objectArr) {
				agent = (Agent) obj[0];
				commission = (Double) obj[1];
				ref = (PolicyReferenceType) obj[2];
				sanctionNo = (String) obj[3];
				sanctionDate = (Date) obj[4];
				if (!resultMap.containsKey(sanctionNo)) {
					resultMap.put(sanctionNo, new AgentSanctionDTO(sanctionNo, sanctionDate, ref, commission, agent));
				} else {
					dto = resultMap.get(sanctionNo);
					commission = commission + dto.getCommision();
					resultMap.put(sanctionNo, new AgentSanctionDTO(sanctionNo, sanctionDate, ref, commission, agent));
				}
			}

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Agent", pe);
		}
		return new ArrayList<AgentSanctionDTO>(resultMap.values());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentComissionInfo> findAgentCommissionInfoByLife(AgentSanctionCriteria criteria) throws DAOException {
		List<AgentComissionInfo> result = new ArrayList<AgentComissionInfo>();
		List<Object[]> agentComList = new ArrayList<Object[]>();
		String branchCode = null;
		StringBuffer policyQuery = new StringBuffer();
		try {
			if (criteria.getBranch() == null) {
				if (configLoader.isCentralizedSystem()) {
					branchCode = userProcessService.getLoginUser().getBranch().getBranchCode();
				} else {
					branchCode = configLoader.getBranchCode();
				}
			} else {
				branchCode = criteria.getBranch().getBranchCode();
			}
			// Search All Policy depends on login user branch
			// eg, If yangon branch user login in, then find all of the policy
			// that is proposed in yangon branch.
			// Do no consider agent branch.

			// reference of agent commission and payment to policy is missing
			// (because of endorsement and renewal process),
			// then find in policy history

			// (1) Find From Policy
			// Life
			policyQuery.append("SELECT a.agent, a.commission, a.referenceType, a.id, a.sanctionNo, a.sanctionDate, p.receiptNo, l.policyNo "
					+ "from AgentCommission a, Payment p, LifePolicy l " + "where a.referenceNo = p.referenceNo and a.referenceNo = l.id and l.branch.branchCode = :branchCode and "
					+ "a.status = TRUE ");
			policyQuery.append(" AND a.referenceType = :referenceType");
			policyQuery.append(" And a.sanctionNo is not null");

			if (criteria.getStartDate() != null) {
				policyQuery.append(" AND a.commissionStartDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				policyQuery.append(" AND a.commissionStartDate <= :endDate");
			}

			if (criteria.getAgent() != null) {
				policyQuery.append(" AND a.agent.id = :agentId");
			}

			Query policyQ = em.createQuery(policyQuery.toString());
			policyQ.setParameter("branchCode", branchCode);
			policyQ.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);

			if (criteria.getStartDate() != null) {
				policyQ.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				policyQ.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}
			if (criteria.getAgent() != null) {
				policyQ.setParameter("agentId", criteria.getAgent().getId());
			}

			agentComList = policyQ.getResultList();
			Agent agent = null;
			double commission = 0.0;
			PolicyReferenceType ref = null;
			String agentCommissionId = null;
			String sanctionNo = null;
			Date sanctionDate = null;
			String policyNo = null;
			String receiptNo = null;
			String typeOfProduct = null;
			for (Object[] items : agentComList) {
				agent = (Agent) items[0];
				commission = (Double) items[1];
				ref = (PolicyReferenceType) items[2];
				agentCommissionId = (String) items[3];
				sanctionNo = (String) items[4];
				sanctionDate = (Date) items[5];
				receiptNo = (String) items[6];
				policyNo = (String) items[7];
				typeOfProduct = "Life Insurance";

				result.add(new AgentComissionInfo(agentCommissionId, policyNo, receiptNo, agent.getFullName(), agent.getCodeNo(), agent.getContentInfo().getMobile(),
						agent.getFullAddress(), commission, agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(), typeOfProduct, ref, sanctionNo, sanctionDate,
						null));
			}

			// find from Policy History
			StringBuffer policyHistoryQuery = new StringBuffer();
			// Life
			policyHistoryQuery.append("SELECT a.agent, a.commission, a.referenceType, a.id, a.sanctionNo, a.sanctionDate, p.receiptNo, l.policyNo "
					+ "from AgentCommission a, Payment p, LifePolicyHistory l "
					+ "where a.referenceNo = p.referenceNo and a.referenceNo = l.policyReferenceNo and l.entryType <> :entryType and "
					+ "l.branch.branchCode = :branchCode and a.status = TRUE ");
			policyHistoryQuery.append(" AND a.referenceType = :referenceType");
			policyHistoryQuery.append(" And a.sanctionNo is not null");

			if (criteria.getStartDate() != null) {
				policyHistoryQuery.append(" AND a.commissionStartDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				policyHistoryQuery.append(" AND a.commissionStartDate <= :endDate ");
			}

			Query policyHistoryQ = em.createQuery(policyHistoryQuery.toString());
			policyHistoryQ.setParameter("branchCode", branchCode);
			policyHistoryQ.setParameter("entryType", PolicyHistoryEntryType.UNDERWRITING);
			policyHistoryQ.setParameter("referenceType", PolicyReferenceType.LIFE_POLICY);

			if (criteria.getStartDate() != null) {
				policyHistoryQ.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				policyHistoryQ.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			agentComList = policyHistoryQ.getResultList();
			if (!agentComList.isEmpty()) {
				for (Object[] items : agentComList) {
					agent = (Agent) items[0];
					commission = (Double) items[1];
					ref = (PolicyReferenceType) items[2];
					agentCommissionId = (String) items[3];
					sanctionNo = (String) items[4];
					sanctionDate = (Date) items[5];
					policyNo = (String) items[6];
					receiptNo = (String) items[7];
					typeOfProduct = "Life Insurance";
					result.add(new AgentComissionInfo(agentCommissionId, policyNo, receiptNo, agent.getFullName(), agent.getCodeNo(), agent.getContentInfo().getMobile(),
							agent.getFullAddress(), commission, agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(), typeOfProduct, ref, sanctionNo, sanctionDate,
							null));
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Agent", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AgentComissionInfo> findAgentCommissionInfoBySanction(AgentSanctionDTO agentSanctionDTO) {
		List<AgentComissionInfo> result = new ArrayList<AgentComissionInfo>();
		StringBuffer policyQuery = new StringBuffer();
		try {
			String policy = "";
			String policyHistory = "";
			String typeOfProduct = null;
			PolicyReferenceType referenceType = agentSanctionDTO.getReferenceType();
			switch (referenceType) {
				case SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_POLICY:
				case SINGLE_PREMIUM_ENDOWMENT_LIFE_POLICY:
				case STUDENT_LIFE_POLICY:
				case STUDENT_LIFE_POLICY_BILL_COLLECTION:
				case FARMER_POLICY:
				case PUBLIC_TERM_LIFE_POLICY:
				case LIFE_BILL_COLLECTION:
				case LIFE_POLICY:
				case SHORT_ENDOWMENT_LIFE_POLICY:
				case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				case PA_POLICY:
					policy = " LifePolicy p ";
					policyHistory = " LifePolicyHistory p ";
					typeOfProduct = " Life Insurance ";
					break;
				case HEALTH_POLICY_BILL_COLLECTION:
				case MEDICAL_BILL_COLLECTION:
				case MEDICAL_POLICY:
				case HEALTH_POLICY:
				case MICRO_HEALTH_POLICY:
				case CRITICAL_ILLNESS_POLICY:
				case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
					policy = " MedicalPolicy p ";
					typeOfProduct = " Medical Insurance ";
					break;
				case GROUP_FARMER_PROPOSAL:
					policy = " GroupFarmerProposal p ";
					typeOfProduct = " Life Insurance ";
					break;
				case GROUP_MICRO_HEALTH:
					policy = "GroupMicroHealth p ";
					typeOfProduct = "Medical Insurance";
					break;
				case PERSON_TRAVEL_POLICY:
					policy = " PersonTravelPolicy p ";
					typeOfProduct = "Travel Insurance";
					break;
				default:
					break;
			}
			if (PolicyReferenceType.GROUP_FARMER_PROPOSAL.equals(referenceType)) {
				policyQuery.append(" SELECT a.agent, a.commission, a.referenceType, a.id, a.sanctionNo, a.sanctionDate, a.receiptNo, p.proposalNo , a.CUR ");
			} else if (PolicyReferenceType.GROUP_MICRO_HEALTH.equals(referenceType)) {
				policyQuery.append(" SELECT a.agent, a.commission, a.referenceType, a.id, a.sanctionNo, a.sanctionDate, a.receiptNo, p.id , a.CUR ");
			} else {
				policyQuery.append(" SELECT a.agent, a.commission, a.referenceType, a.id, a.sanctionNo, a.sanctionDate, a.receiptNo, p.policyNo , a.CUR ");
			}
			policyQuery.append(" FROM AgentCommission a, " + policy);
			policyQuery.append(" WHERE a.referenceNo = p.id AND a.sanctionNo = :sanctionNo AND a.agent.id = :agendId ");
			if (referenceType.equals(PolicyReferenceType.LIFE_POLICY) || referenceType.equals(PolicyReferenceType.LIFE_BILL_COLLECTION)) {
				policyQuery.append(" UNION ALL ");
				policyQuery.append(" SELECT a.agent, a.commission, a.referenceType, a.id, a.sanctionNo, a.sanctionDate, a.receiptNo, p.policyNo, a.CUR ");
				policyQuery.append(" FROM AgentCommission a, " + policyHistory);
				policyQuery.append(" WHERE a.referenceNo = p.policyReferenceNo AND a.sanctionNo = :sanctionNo AND a.agent.id = :agendId ");

			}
			Query policyQ = em.createQuery(policyQuery.toString());
			policyQ.setParameter("sanctionNo", agentSanctionDTO.getSanctionNo());
			policyQ.setParameter("agendId", agentSanctionDTO.getAgent().getId());
			List<Object[]> objArrList = policyQ.getResultList();
			em.flush();
			Agent agent = null;
			double commission = 0.0;
			PolicyReferenceType ref = null;
			String agentCommissionId = null;
			String sanctionNo = null;
			Date sanctionDate = null;
			String policyNo = null;
			String receiptNo = null;
			String currencyCode = "";
			for (Object[] arr : objArrList) {
				agent = (Agent) arr[0];
				commission = (Double) arr[1];
				ref = (PolicyReferenceType) arr[2];
				agentCommissionId = (String) arr[3];
				sanctionNo = (String) arr[4];
				sanctionDate = (Date) arr[5];
				receiptNo = (String) arr[6];
				policyNo = (String) arr[7];
				currencyCode = (String) arr[8];
				Date startDate = Utils.minusDays(sanctionDate, 6);
				Date endDate = Utils.plusDays(sanctionDate, 1);
				result.add(new AgentComissionInfo(agentCommissionId, policyNo, receiptNo, agent.getFullName(), agent.getCodeNo(), agent.getContentInfo().getMobile(),
						agent.getFullAddress(), commission, agent.getLiscenseNo(), startDate, endDate, typeOfProduct, ref, sanctionNo, sanctionDate, currencyCode));

			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of findAgentCommissionInfoBySanction", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AgentCommission findAgentCommissionById(String id) {
		AgentCommission agentCommission = null;
		try {
			agentCommission = em.find(AgentCommission.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find AgentCommission By Id : " + id, pe);
		}
		return agentCommission;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private List<AgentSanctionReport> findForPersonTravelIndividual(AgentSanctionCriteria criteria, AgentComissionInfo agentComissionInfo, boolean isEnquiry) throws DAOException {
		List<Object[]> agentComList = new ArrayList<Object[]>();
		List<AgentSanctionReport> result = new ArrayList<AgentSanctionReport>();
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT ac, ac.agent, m, p FROM AgentCommission ac, PersonTravelPolicy m, Payment p "
					+ " WHERE ac.referenceNo = m.id AND ac.referenceNo = p.referenceNo AND ac.id = :agentCommissionId");
			queryString.append(" AND ac.referenceType = :referenceType");
			if (!isEnquiry) {
				queryString.append(" AND ac.isPaid = FALSE");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND ac.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				queryString.append(" AND ac.commissionStartDate <= :endDate");
			}

			Query query = em.createQuery(queryString.toString());
			query.setParameter("agentCommissionId", agentComissionInfo.getId());
			query.setParameter("referenceType", PolicyReferenceType.PERSON_TRAVEL_POLICY);
			if (criteria.getStartDate() != null) {
				query.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}
			if (criteria.getEndDate() != null) {
				query.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			agentComList = query.getResultList();
			for (Object[] items : agentComList) {
				AgentCommission agentComission = (AgentCommission) items[0];
				Agent agent = (Agent) items[1];
				PersonTravelPolicy policy = (PersonTravelPolicy) items[2];
				Payment payment = (Payment) items[3];
				String typeOfProduct = "";
				Product product = policy.getProduct();
				if (ProductIDConfig.isUnder100MileTravelInsurance(product)) {
					typeOfProduct = "Under 100 Miles Travelling Insurance";
				} else {
					typeOfProduct = "General Travelling Insurance";
				}
				result.add(new AgentSanctionReport(agent.getId(), payment.getReceiptNo(), policy.getCustomerName(), policy.getPolicyNo(), policy.getTotalSumInsured(),
						policy.getTotalPremium(), payment.getReinstatementPremium(), agentComission.getPercentage(), agentComission.getCommission(), agent.getFullName(),
						agent.getCodeNo(), agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(), typeOfProduct, agent.getContentInfo().getMobile(),
						agent.getFullAddress(), agentComission, agentComission.getSanctionNo()));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private List<AgentSanctionReport> findForGroupFarmerIndividual(AgentSanctionCriteria criteria, AgentComissionInfo a, boolean isEnquiry, PolicyReferenceType p1,
			PolicyReferenceType p2) throws DAOException {
		List<Object[]> agentComList = new ArrayList<Object[]>();
		List<AgentSanctionReport> result = new ArrayList<AgentSanctionReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT ac, a, m, p FROM AgentCommission ac, Agent a, GroupFarmerProposal m, Payment p "
					+ " WHERE ac.agent.id = a.id AND ac.referenceNo = m.id AND ac.referenceNo = p.referenceNo AND ac.id = :agentCommissionId");
			query.append(" AND (ac.referenceType = :referenceTypePolicy ");

			if (p2 != null) {
				query.append(" OR ac.referenceType = :referenceTypeBillCollection) ");
			} else {
				query.append(" ) ");
			}

			if (!isEnquiry) {
				query.append(" AND ac.isPaid = FALSE");
			}
			if (criteria.getStartDate() != null) {
				query.append(" AND ac.commissionStartDate >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				query.append(" AND ac.commissionStartDate <= :endDate");
			}

			Query q = em.createQuery(query.toString());
			q.setParameter("agentCommissionId", a.getId());
			q.setParameter("referenceTypePolicy", p1);
			if (p2 != null) {
				q.setParameter("referenceTypeBillCollection", p2);
			}
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}

			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			agentComList = q.getResultList();
			for (Object[] items : agentComList) {
				AgentCommission agentComission = (AgentCommission) items[0];
				Agent agent = (Agent) items[1];
				GroupFarmerProposal proposal = (GroupFarmerProposal) items[2];
				Payment payment = (Payment) items[3];
				result.add(new AgentSanctionReport(agent.getId(), payment.getReceiptNo(), proposal.getOrganization().getName(), proposal.getProposalNo(), proposal.getTotalSI(),
						proposal.getPremium(), payment.getReinstatementPremium(), agentComission.getPercentage(), agentComission.getCommission(), agent.getFullName(),
						agent.getCodeNo(), agent.getLiscenseNo(), criteria.getStartDate(), criteria.getEndDate(), "Farmer Insurance", agent.getContentInfo().getMobile(),
						agent.getFullAddress(), agentComission, agentComission.getSanctionNo()));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of AgentCommission by criteria.", pe);
		}
		return result;
	}
}
