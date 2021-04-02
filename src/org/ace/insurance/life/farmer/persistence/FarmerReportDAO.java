package org.ace.insurance.life.farmer.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.FarmerPolicyType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.farmer.persistence.interfaces.IFarmerReportDAO;
import org.ace.insurance.report.common.MonthlyReportCriteria;
import org.ace.insurance.report.farmer.FarmerDailyReport;
import org.ace.insurance.report.farmer.FarmerMonthlyReport;
import org.ace.insurance.report.farmer.FarmerSummaryReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("FarmerReportDAO")
public class FarmerReportDAO extends BasicDAO implements IFarmerReportDAO {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FarmerDailyReport> findDailyReport(LifeDailyIncomeReportCriteria criteria) throws DAOException {
		List<FarmerDailyReport> result = null;
		try {
			StringBuffer buffer = new StringBuffer();

			buffer.append("SELECT NEW " + FarmerDailyReport.class.getName() + "(f) FROM FarmerDailyReportView f WHERE f.policyNo IS NOT NULL");

			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				buffer.append(" AND f.activedPolicyStartDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				buffer.append(" AND f.activedPolicyStartDate <= :endDate");
			}
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				buffer.append(" AND f.entityId=:entityId");
			} else if (criteria.getBranch() != null) {
				buffer.append(" AND f.branchId=:branchId");
			}
			if (criteria.getSalePoint() != null) {
				buffer.append(" AND f.salePointId=:salePointId");
			}

			if (criteria.getFarmerType() != null) {
				if (FarmerPolicyType.FARMER.equals(criteria.getFarmerType())) {
					buffer.append(" AND f.groupFarmerProposalNo  IS  NULL ");
				} else {
					buffer.append(" AND f.groupFarmerProposalNo IS NOT NULL ");
				}
			}

			Query q = em.createQuery(buffer.toString());

			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				q.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}

			result = q.getResultList();
			em.flush();
			RegNoSorter<FarmerDailyReport> sortedResultList = new RegNoSorter<>(result);
			result = sortedResultList.getSortedList();
		} catch (PersistenceException pe) {
			throw translate("Failed to find farmer daily report.", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FarmerMonthlyReport> findMonthlyReport(MonthlyReportCriteria criteria) throws DAOException {
		List<FarmerMonthlyReport> resultList = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + FarmerMonthlyReport.class.getName() + "(f) FROM FarmerMonthlyView f"
					+ " WHERE f.policyNo IS NOT NULL AND f.activedPolicyStartDate >= :startDate AND f.activedPolicyStartDate <= :endDate");

			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				buffer.append(" AND f.entityId=:entityId");
			} else if (criteria.getBranch() != null) {
				buffer.append(" AND f.branchId=:branchId");
			}

			if (criteria.getSalePoint() != null) {
				buffer.append(" AND f.salePointId=:salePointId");
			}

			Query query = em.createQuery(buffer.toString());
			query.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
			query.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));

			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				query.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}

			resultList = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find farmer monthly report.", pe);
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FarmerSummaryReport> findSummaryReport(LifeDailyIncomeReportCriteria criteria) throws DAOException {
		List<FarmerSummaryReport> result = null;
		try {
			StringBuffer buffer = new StringBuffer();

			buffer.append("SELECT NEW " + FarmerSummaryReport.class.getName() + "(f) FROM FarmerSummaryReportView f WHERE f.date >= :startDate" + " AND f.date <= :endDate ");

			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				buffer.append(" AND f.entityId=:entityId");
			} else if (criteria.getBranch() != null) {
				buffer.append(" AND f.branchId=:branchId");
			}
			if (criteria.getSalePoint() != null) {
				buffer.append(" AND f.salePointId=:salePointId");
			}
			Query q = em.createQuery(buffer.toString());
			q.setParameter("startDate", criteria.getStartDate());

			criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
			q.setParameter("endDate", criteria.getEndDate());

			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				q.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find farmer summary report.", pe);
		}
		return result;
	}
}
