package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.LifeDailyIncomeReport;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.insurance.report.life.persistence.interfaces.ILifeDailyIncomeReportDAO;
import org.ace.insurance.report.life.view.LifeDailyIncomeReportView;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("LifeDailyIncomeReportDAO")
public class LifeDailyIncomeReportDAO extends BasicDAO implements ILifeDailyIncomeReportDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<LifeDailyIncomeReport> find(LifeDailyIncomeReportCriteria lifeDailyCriteria, List<String> productIdList) throws DAOException {
		List<LifeDailyIncomeReport> result = new ArrayList<LifeDailyIncomeReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT m FROM LifeDailyIncomeReportView m WHERE m.policyNo IS NOT NULL and m.paymentDate IS NOT NULL ");
			query.append(" AND m.productId IN :productIdList");
			if (lifeDailyCriteria.getStartDate() != null) {
				lifeDailyCriteria.setStartDate(Utils.resetStartDate(lifeDailyCriteria.getStartDate()));
				query.append(" AND m.paymentDate >= :startDate");
			}
			if (lifeDailyCriteria.getEndDate() != null) {
				lifeDailyCriteria.setEndDate(Utils.resetEndDate(lifeDailyCriteria.getEndDate()));
				query.append(" AND m.paymentDate <= :endDate");
			}
			if (null != lifeDailyCriteria.getEntity() && null == lifeDailyCriteria.getBranch()) {
				query.append(" AND m.entityId = :entityId");
			} else if (lifeDailyCriteria.getBranch() != null) {
				query.append(" AND m.branchId = :branchId");
			}

			if (null != lifeDailyCriteria.getSalePoint()) {
				query.append(" AND m.salePointId =:salePointId");
			}
			query.append(" ORDER BY  m.branchName, m.policyNo ");
			Query q = em.createQuery(query.toString());
			q.setParameter("productIdList", productIdList);
			if (lifeDailyCriteria.getStartDate() != null) {
				lifeDailyCriteria.setStartDate(Utils.resetStartDate(lifeDailyCriteria.getStartDate()));
				q.setParameter("startDate", lifeDailyCriteria.getStartDate());
			}
			if (lifeDailyCriteria.getEndDate() != null) {
				lifeDailyCriteria.setEndDate(Utils.resetStartDate(lifeDailyCriteria.getEndDate()));
				q.setParameter("endDate", lifeDailyCriteria.getEndDate());
			}
			if (null != lifeDailyCriteria.getEntity() && null == lifeDailyCriteria.getBranch()) {
				q.setParameter("entityId", lifeDailyCriteria.getEntity().getId());
			} else if (lifeDailyCriteria.getBranch() != null) {
				q.setParameter("branchId", lifeDailyCriteria.getBranch().getId());

			}

			if (null != lifeDailyCriteria.getSalePoint()) {
				q.setParameter("salePointId", lifeDailyCriteria.getSalePoint().getId());
			}

			List<LifeDailyIncomeReportView> views = q.getResultList();
			for (LifeDailyIncomeReportView view : views) {
				result.add(new LifeDailyIncomeReport(view));
			}

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifeDailyIncomeReport by criteria.", pe);
		}

		return result;
	}
}
