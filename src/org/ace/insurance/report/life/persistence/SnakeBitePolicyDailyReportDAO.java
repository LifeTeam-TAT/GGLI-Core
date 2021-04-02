package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.life.snakebite.view.SnakeBiteDailyIncomeReport;
import org.ace.insurance.report.life.persistence.interfaces.ISnakeBitePolicyDailyReportDAO;
import org.ace.insurance.report.life.service.SnakeBitePolicyDailyReportCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class serves as the DAO to manipulate the
 * <code>SnakeBite Policy Daily Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/11/29
 * 
 * @Updated PPA
 * @date 2016/06/14
 */

@Repository("SnakeBitePolicyDailyReportDAO")
public class SnakeBitePolicyDailyReportDAO extends BasicDAO implements ISnakeBitePolicyDailyReportDAO {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SnakeBiteDailyIncomeReport> findSnakeBitePolicyDailyReport(SnakeBitePolicyDailyReportCriteria criteria) {
		List<SnakeBiteDailyIncomeReport> result = new ArrayList<SnakeBiteDailyIncomeReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT s FROM SnakeBiteDailyIncomeReport s WHERE s.policyId IS NOT NULL ");

			if (criteria.getStartDate() != null) {
				query.append(" AND s.paymentDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				query.append(" AND s.paymentDate <= :endDate");
			}

			if (criteria.getEntity() != null && criteria.getBranch() == null) {
				query.append(" AND s.entityId=:entityId");
			} else if (criteria.getBranch() != null) {
				query.append(" AND s.branchId=:branchId");
			}
			if (criteria.getSalePoint() != null) {
				query.append(" AND s.salePointId=:salePointId");
			}
			Query q = em.createQuery(query.toString());

			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
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
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SnakeBiteDailyIncomeReport by criteria.", pe);
		}
		return result;
	}
}
