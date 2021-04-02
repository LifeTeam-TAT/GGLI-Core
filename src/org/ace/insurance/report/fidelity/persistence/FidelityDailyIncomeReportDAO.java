package org.ace.insurance.report.fidelity.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.fidelity.FidelityDailyIncomeReport;
import org.ace.insurance.report.fidelity.FidelityDailyIncomeReportCriteria;
import org.ace.insurance.report.fidelity.persistence.interfaces.IFidelityDailyIncomeReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("FidelityDailyIncomeReportDAO")
public class FidelityDailyIncomeReportDAO extends BasicDAO implements IFidelityDailyIncomeReportDAO {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FidelityDailyIncomeReport> find(FidelityDailyIncomeReportCriteria criteria) throws DAOException {
		List<FidelityDailyIncomeReport> result = new ArrayList<FidelityDailyIncomeReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append(" SELECT f FROM FidelityDailyIncomeReport f WHERE f.policyNo IS NOT NULL AND f.receiptNo IS NOT NULL ");

			if (criteria.getStartDate() != null) {
				query.append(" AND f.receiptDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				query.append(" AND f.receiptDate <= :endDate");
			}
			if (criteria.getBranch() != null) {
				query.append(" AND f.branchId = :branchId");
			}
			Query q = em.createQuery(query.toString());
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}
			if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			result = q.getResultList();
			em.flush();
			return result;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CustomerInformationReport by criteria.", pe);
		}
	}

}
