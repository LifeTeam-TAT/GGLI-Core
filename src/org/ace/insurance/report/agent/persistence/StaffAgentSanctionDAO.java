package org.ace.insurance.report.agent.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.report.agent.AgentSanctionReport;
import org.ace.insurance.report.agent.StaffSanctionCriteria;
import org.ace.insurance.report.agent.persistence.interfaces.IStaffAgentSactionDAO;
import org.ace.insurance.report.agent.view.StaffAgentCommissionInfo;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("StaffAgentSactionDAO")
public class StaffAgentSanctionDAO extends BasicDAO implements IStaffAgentSactionDAO {

	@Resource(name = "IDConfigLoader")
	protected IDConfigLoader configLoader;

	@Resource(name = "UserProcessService")
	protected IUserProcessService userProcessService;

	@Resource(name = "PaymentDAO")
	protected IPaymentDAO paymentDAO;

	List<StaffAgentCommission> resultCommission = new ArrayList<StaffAgentCommission>();
	AgentSanctionReport result2;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<StaffAgentCommissionInfo> findStaffAgents(StaffSanctionCriteria criteria) throws DAOException {

		List<StaffAgentCommissionInfo> result = new ArrayList<StaffAgentCommissionInfo>();
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

			if (criteria.getStaff() != null) {
				criteriaString.append(" AND a.staff.id = :staffId");
				historyCriteriaString.append(" AND a1.staff.id = :staffId");
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
				policyQuery.append(" SELECT ag.initialId, ag.name, ag.staff_no,  ag.contentInfo.mobile,");
				policyQuery.append(" ag.residentAddress.residentAddress, t.name, t.province.name, t.province.country.name,");
				policyQuery.append(" a.commission, a.referenceType, a.id, a.receiptNo,a.bpmsReceiptNo,m.proposalNo,a.sanctionNo,m.branch.name");
			} else {
				policyQuery.append(" SELECT ag.initialId, ag.name, ag.staff_no,  ag.contentInfo.mobile,");
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
					policyQuery.append(" FROM StaffAgentCommission a JOIN a.staff ag LEFT OUTER JOIN ag.residentAddress.township t, LifePolicy m WHERE a.referenceNo = m.id");
					unionQuery.append(" UNION ALL ");
					unionQuery.append(" SELECT ag1.initialId, ag1.name, ag1.staff_no, ag1.contentInfo.mobile,");
					unionQuery.append(" ag1.residentAddress.residentAddress, t1.name, t1.province.name, t1.province.country.name,");
					unionQuery.append(" a1.commission, a1.referenceType, a1.id, a1.receiptNo,a1.bpmsReceiptNo, m1.policyNo, a1.sanctionNo,m1.branch.name");
					unionQuery.append(" FROM StaffAgentCommission a1 JOIN a1.staff ag1 LEFT OUTER JOIN ag1.residentAddress.township t1, ");
					unionQuery.append(" LifePolicyHistory m1 WHERE a1.referenceNo = m1.policyReferenceNo ");
					unionQuery.append(" AND a1.status = FALSE ");
					unionQuery.append(historyCriteriaString.toString());
					break;
				case PERSONAL_ACCIDENT:
				case FARMER:
					policyQuery.append(" FROM StaffAgentCommission a JOIN a.staff ag LEFT OUTER JOIN ag.residentAddress.township t, LifePolicy m WHERE a.referenceNo = m.id  ");
					break;
				case PUBLIC_TERM_LIFE:
					policyQuery.append(" FROM StaffAgentCommission a JOIN a.staff ag LEFT OUTER JOIN ag.residentAddress.township t, LifePolicy m WHERE a.referenceNo = m.id  ");
					break;
				case MEDICAL:
				case HEALTH:
				case MICRO_HEALTH:
				case CRITICAL_ILLNESS:
					policyQuery.append(" FROM StaffAgentCommission a JOIN a.staff ag LEFT OUTER JOIN ag.residentAddress.township t, MedicalPolicy m WHERE a.referenceNo = m.id ");
					break;
				case GROUP_MICRO_HEALTH:
					policyQuery
							.append(" FROM StaffAgentCommission a JOIN a.staff ag LEFT OUTER JOIN ag.residentAddress.township t, GroupMicroHealth m WHERE a.referenceNo = m.id ");
					break;
				case PERSON_TRAVEL:
					policyQuery
							.append(" FROM StaffAgentCommission a JOIN a.staff ag LEFT OUTER JOIN ag.residentAddress.township t, PersonTravelPolicy m WHERE a.referenceNo = m.id ");
					break;
				case GROUP_FARMER:
					policyQuery.append(
							" FROM StaffAgentCommission a JOIN a.staff ag LEFT OUTER JOIN ag.residentAddress.township t, GroupFarmerProposal m WHERE a.referenceNo = m.id ");
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

			if (criteria.getStaff() != null) {
				query.setParameter("staffId", criteria.getStaff().getId());
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
			query.setParameter("currency", criteria.getCurrency().getId());

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
			String mobile = null;
			String address = null;
			double commission = 0.0;
			PolicyReferenceType ref = null;
			String StaffAgentCommissionId = null;
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
				// liscenseNo = (String) items[3];
				mobile = (String) items[3];
				address = (String) items[4];
				address = address + ((String) items[5]) + ((String) items[6]) + ((String) items[7]);
				commission = (Double) items[8];
				ref = (PolicyReferenceType) items[9];
				StaffAgentCommissionId = (String) items[10];
				receiptNo = (String) items[11];
				bpmsReceiptNo = (String) items[12];
				policyNo = (String) items[13];
				sanctionNo = (String) items[14];
				branchName = (String) items[15];
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

				result.add(new StaffAgentCommissionInfo(StaffAgentCommissionId, policyNo, receiptNo, bpmsReceiptNo, agentName, codeNo, mobile, address, commission,
						criteria.getStartDate(), criteria.getEndDate(), typeOfProduct, ref, sanctionNo, currencyCode, currencySymbol, branchName));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Agent", pe);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAgentCommissionStaus(String agentCommissionId, String sanctionNo, Date sanctionDate, double withHoldingTax, double homeWithHoldingTax) throws DAOException {
		try {
			Query q = em.createQuery(
					"UPDATE StaffAgentCommission a SET a.status = :status , a.sanctionDate = :sanctionDate , a.sanctionNo = :sanctionNo, a.withHoldingTax = :withHoldingTax, a.homeWithHoldingTax = :homeWithHoldingTax where a.id = :id");
			q.setParameter("id", agentCommissionId);
			q.setParameter("status", true);
			q.setParameter("sanctionDate", sanctionDate);
			q.setParameter("sanctionNo", sanctionNo);
			q.setParameter("withHoldingTax", withHoldingTax);
			q.setParameter("homeWithHoldingTax", homeWithHoldingTax);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update StaffAgentCommission", pe);
		}

	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public StaffAgentCommission findAgentCommissionById(String id) {
		StaffAgentCommission agentCommission = null;
		try {
			agentCommission = em.find(StaffAgentCommission.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find AgentCommission By Id : " + id, pe);
		}
		return agentCommission;
	}


}
