package org.ace.insurance.report.agent.persistence;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.agent.AgentMonthlyLifeSaleReport;
import org.ace.insurance.report.agent.persistence.interfaces.IAgentMonthlyLifeSaleReportDAO;
import org.ace.insurance.report.common.AgentMonthlyLifeSaleCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**************************************************************************
 * @Date 12/05/2016.
 * @author Pyae Phyo Aung.
 * @Rev v1.0.
 * @CopyRight ACEPLUS SOLUTIONS CO., Ltd.
 *************************************************************************/

@Repository("AgentMonthlyLifeSaleReportDAO")
public class AgentMonthlyLifeSaleReportDAO extends BasicDAO implements IAgentMonthlyLifeSaleReportDAO {

	/**
	 * Find AgentMonthlyLifeSaleReport from database with given required
	 * criteria fields.
	 * 
	 * @param AgentMonthlyLifeSaleCriteria
	 *            [Required fields to filter for Report]
	 * 
	 * @return Map<String,AgentMonthlyLifeSaleReport>[AgentMonthlyLifeSaleReport
	 *         Map with agent codeNo key].
	 * 
	 * @throws DAOException
	 *             [DataAccessObject Exception ]
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, AgentMonthlyLifeSaleReport> findMonthlySale(AgentMonthlyLifeSaleCriteria criteria) throws DAOException {
		Map<String, AgentMonthlyLifeSaleReport> result = new TreeMap<String, AgentMonthlyLifeSaleReport>();
		List<Object[]> rawList;
		AgentMonthlyLifeSaleReport report;
		try {

			StringBuffer query = new StringBuffer();
			String id;
			String agentName;
			String agentCodeNo;
			int endowmentPolicy = 0;
			double endowmentPremium = 0.0;
			int groupPolicy = 0;
			double groupPremium = 0.0;
			int healthPolicy = 0;
			double healthPremium = 0.0;
			int totalPolicy = 0;
			double totalPremium = 0.0;
			String branchName = "";
			String salePointName = "";

			query.append("SELECT a.agentId,a.agentName,a.codeNo,a.endowmentLife,a.groupLife,a.totalPremium,a.branchName,a.salePointName,a.entityId ");
			query.append(" FROM AgentSaleComparisonReportView a  WHERE a.agentId IS NOT NULL ");
			query.append(" AND  (a.referenceType='LIFE_POLICY' OR a.referenceType='MEDICAL_POLICY')");
			query.append(" AND  a.activedPolicyStartDate >= :startDate");
			query.append(" AND a.activedPolicyStartDate < :endDate");
			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				query.append(" AND a.entityId = :entityId");
			} else if (null != criteria.getBranch()) {

				query.append(" AND a.branchId = :branchId");
			}
			if (criteria.getSalePoint() != null) {
				query.append(" AND a.salePointId= :salePointId");
			}
			if (criteria.getAgent() != null) {
				query.append(" AND a.agentId= :agentId");
			}
			Query q = em.createQuery(query.toString());

			q.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
			q.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));

			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				q.setParameter("entityId", criteria.getEntity().getId());
			} else if (null != criteria.getBranch()) {

				q.setParameter("branchId", criteria.getBranch().getId());
			}

			if (criteria.getSalePoint() != null) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}

			if (criteria.getAgent() != null) {
				q.setParameter("agentId", criteria.getAgent().getId());
			}
			rawList = q.getResultList();
			for (Object[] r : rawList) {
				id = (String) r[0];
				agentName = (String) r[1];
				agentCodeNo = (String) r[2];
				endowmentPolicy = (Integer) r[3];
				groupPolicy = (Integer) r[4];
				salePointName = (String) r[7];
				if (endowmentPolicy != 0) {
					endowmentPremium = (Double) r[5];
					groupPremium = 0.0;
					healthPremium = 0.0;
				} else if (groupPolicy != 0) {
					groupPremium = (Double) r[5];
					endowmentPremium = 0.0;
					healthPremium = 0.0;
				} else if (healthPolicy != 0) {
					healthPremium = (Double) r[5];
					endowmentPremium = 0.0;
					groupPremium = 0.0;
				} else if (branchName != null) {
					branchName = (String) r[6];
				} else if (salePointName != null) {
					salePointName = (String) r[7];
				}
				totalPolicy = endowmentPolicy + groupPolicy + healthPolicy;
				totalPremium = endowmentPremium + groupPremium + healthPremium;
				if (result.containsKey(agentCodeNo)) {
					report = result.get(agentCodeNo);
					report.setEndowmentPolicy(report.getEndowmentPolicy() + endowmentPolicy);
					report.setEndowmentPremium(report.getEndowmentPremium() + endowmentPremium);
					report.setGroupPolicy(report.getGroupPolicy() + groupPolicy);
					report.setGroupPremium(report.getGroupPremium() + groupPremium);
					report.setHealthPolicy(report.getHealthPolicy() + healthPolicy);
					report.setHealthPremium(report.getHealthPremium() + healthPremium);
					report.setTotalPolicy(report.getTotalPolicy() + totalPolicy);
					report.setTotalPremium(report.getTotalPremium() + totalPremium);
					report.setBranchName(report.getBranchName() + branchName);
					report.setSalePointName(report.getSalePointName() + salePointName);
				} else {
					report = new AgentMonthlyLifeSaleReport(id, agentName, agentCodeNo, endowmentPolicy, endowmentPremium, groupPolicy, groupPremium, healthPolicy, healthPremium,
							totalPolicy, totalPremium, branchName, salePointName);

				}
				result.put(agentCodeNo, report);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Agent Monthly Sale Report for Life Assessment by criteria.", pe);
		}
		return result;
	}
}
