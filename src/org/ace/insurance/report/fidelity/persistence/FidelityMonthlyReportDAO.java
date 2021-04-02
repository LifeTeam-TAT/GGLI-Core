package org.ace.insurance.report.fidelity.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.fidelity.FidelityMonthlyReport;
import org.ace.insurance.report.fidelity.FidelityMonthlyReportCriteria;
import org.ace.insurance.report.fidelity.persistence.interfaces.IFidelityMonthlyReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class serves as the DAO to manipulate the
 * <code>Fidelity Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2014/Feb/4
 */

@Repository("FidelityMonthlyReportDAO")
public class FidelityMonthlyReportDAO extends BasicDAO implements IFidelityMonthlyReportDAO {
	private Logger logger = Logger.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FidelityMonthlyReport> find(FidelityMonthlyReportCriteria criteria) throws DAOException {
		List<FidelityMonthlyReport> resultList = new ArrayList<FidelityMonthlyReport>();
		try {
			logger.info("FidelityMonthlyReportDAO find() method has been started...... ");
			StringBuffer query = new StringBuffer();
			query.append("SELECT f FROM FidelityMonthlyReport f ");
			query.append(" WHERE f.paymentDate >= :startDate AND f.paymentDate <= :endDate");

			if (criteria.getBranch() != null) {
				query.append(" AND f.branchId = :branchId");
			}

			Query q = em.createQuery(query.toString());

			q.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
			q.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));

			if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}

			resultList = q.getResultList();
			em.flush();
			logger.info("FidelityMonthlyReportDAO find() method has been ended...... ");
			return resultList;
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of FidelityMonthlyReport by criteria.", pe);
		}

	}

}
