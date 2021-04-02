package org.ace.insurance.report.coinsurance.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.coinsurance.CoinsuranceCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceReport;
import org.ace.insurance.report.coinsurance.CoinsuranceSummary;
import org.ace.insurance.report.coinsurance.CoinsuranceSummaryReport;
import org.ace.insurance.report.coinsurance.persistence.interfaces.ICoinsuranceReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("CoinsuranceReportDAO")
public class CoinsuranceReportDAO extends BasicDAO implements ICoinsuranceReportDAO {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<CoinsuranceReport> findCoinsurances(CoinsuranceCriteria coinsuranceCriteria) throws DAOException {
		List<CoinsuranceReport> result;
		try {
			StringBuffer query = new StringBuffer();

			query.append("SELECT c FROM CoinsuranceReport c WHERE c.id IS NOT NULL");
			if (coinsuranceCriteria.getCoinsuranceCompanyId() != null) {
				query.append(" AND c.coinsuranceCompanyId = :companyId");
			}
			if (coinsuranceCriteria.getCoinsuranceType() != null) {
				query.append(" AND c.coinsuranceType = :coinsuranceType");
			}
			if (coinsuranceCriteria.getInsuranceType() != null) {
				query.append(" AND c.insuranceType = :insuranceType");
			}
			if (coinsuranceCriteria.getPolicyNo() != null) {
				query.append(" AND c.policyNo = :policyNo");
			}
			if (coinsuranceCriteria.getStartDate() != null) {
				coinsuranceCriteria.setStartDate(Utils.resetStartDate(coinsuranceCriteria.getStartDate()));
				query.append(" AND c.startDate >= :startDate");
			}
			if (coinsuranceCriteria.getEndDate() != null) {
				coinsuranceCriteria.setEndDate(Utils.resetEndDate(coinsuranceCriteria.getEndDate()));
				query.append(" AND c.startDate <= :endDate");
			}

			Query q = em.createQuery(query.toString());
			if (coinsuranceCriteria.getCoinsuranceCompanyId() != null) {
				q.setParameter("companyId", coinsuranceCriteria.getCoinsuranceCompanyId());
			}
			if (coinsuranceCriteria.getCoinsuranceType() != null) {
				q.setParameter("coinsuranceType", coinsuranceCriteria.getCoinsuranceType().getLabel());
			}
			if (coinsuranceCriteria.getInsuranceType() != null) {
				q.setParameter("insuranceType", coinsuranceCriteria.getInsuranceType().getLabel());
			}
			if (coinsuranceCriteria.getPolicyNo() != null) {
				q.setParameter("policyNo", coinsuranceCriteria.getPolicyNo());
			}
			if (coinsuranceCriteria.getStartDate() != null) {
				q.setParameter("startDate", coinsuranceCriteria.getStartDate());
			}
			if (coinsuranceCriteria.getEndDate() != null) {
				q.setParameter("endDate", coinsuranceCriteria.getEndDate());
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Coinsurance Report by criteria.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<CoinsuranceSummaryReport> findCoinsuranceSummary(CoinsuranceCriteria coinsuranceCriteria) throws DAOException {
		List<CoinsuranceSummary> summaryList = new ArrayList<CoinsuranceSummary>();
		Map<String, CoinsuranceSummaryReport> resultMap = new TreeMap<String, CoinsuranceSummaryReport>();
		List<CoinsuranceSummaryReport> result = new ArrayList<CoinsuranceSummaryReport>();
		CoinsuranceSummaryReport report = new CoinsuranceSummaryReport();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT c FROM CoinsuranceSummary c WHERE c.id IS NOT NULL ");

			if (coinsuranceCriteria.getStartDate() != null) {
				query.append(" AND c.startDate >= :startDate");
			}
			if (coinsuranceCriteria.getEndDate() != null) {
				query.append(" AND c.startDate <= :endDate");
			}
			query.append(" ORDER BY c.name");
			Query q = em.createQuery(query.toString());
			if (coinsuranceCriteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(coinsuranceCriteria.getStartDate()));
			}
			if (coinsuranceCriteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(coinsuranceCriteria.getEndDate()));
			}
			summaryList = q.getResultList();

			String name;
			String coinsuType;
			double receivedSumInsured;
			double sumInsured;
			for (CoinsuranceSummary summary : summaryList) {
				name = summary.name;
				coinsuType = summary.coinsuType;
				receivedSumInsured = summary.receivedSumInsured;
				sumInsured = summary.sumInsured;
				if (resultMap.containsKey(name)) {
					report = resultMap.get(name);
					if (CoinsuranceType.IN.getLabel().equals(coinsuType)) {
						report.setInAmount(receivedSumInsured);
						report.setDifference(receivedSumInsured - report.getOutAmount());
					} else if (CoinsuranceType.OUT.getLabel().equals(coinsuType)) {
						report.setOutAmount(sumInsured);
						report.setDifference(report.getInAmount() - sumInsured);
					}
				} else {
					report = new CoinsuranceSummaryReport(name, receivedSumInsured, sumInsured);
				}
				resultMap.put(name, report);
			}
			result = new ArrayList<CoinsuranceSummaryReport>(resultMap.values());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CoinsurancePolicy by criteria.", pe);
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> findAllPolicyNo() throws DAOException {
		List<String> policies = null;
		StringBuffer query = new StringBuffer();
		query.append("SELECT c.policyNo FROM Coinsurance c where c.policyNo is not null");
		try {
			Query q = em.createQuery(query.toString());
			policies = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to find all Coinsured Policy No.", e);
		}
		return policies;
	}

}
