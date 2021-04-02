package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.persistence.interfaces.ISnakeBitePolicyMonthlyReportDAO;
import org.ace.insurance.report.life.service.SnakeBitePolicyMonthlyReportCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.springframework.stereotype.Repository;

/**
 * This class serves as the DAO to manipulate the
 * <code>SnakeBite Policy Monthly Report</code> object.
 * 
 * @author NNH
 * @since 1.0.0
 * @date 2013/Nov/29
 */

@Repository("SnakeBitePolicyMonthlyReportDAO")
public class SnakeBitePolicyMonthlyReportDAO extends BasicDAO implements ISnakeBitePolicyMonthlyReportDAO {

	@Override
	public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReport(SnakeBitePolicyMonthlyReportCriteria criteria) {
		List<SnakeBitePolicyMonthlyReport> results = new ArrayList<SnakeBitePolicyMonthlyReport>();
		List<Object[]> rawList = new ArrayList<Object[]>();

		StringBuffer query = new StringBuffer();
		query.append("Select s,p From SnakeBitePolicy s, Payment p  where s.id is not null and p.receiptNo = s.referenceNo and s.complete = TRUE");

		if (criteria.getStartDate() != null) {
			criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
			query.append(" AND s.policyStartDate >= :startDate");
		}
		if (criteria.getEndDate() != null) {
			criteria.setEndDate(Utils.resetStartDate(criteria.getEndDate()));
			query.append(" AND s.policyStartDate <= :endDate");
		}

		Query q = em.createQuery(query.toString());

		if (criteria.getStartDate() != null) {
			q.setParameter("startDate", criteria.getStartDate());
		}
		if (criteria.getEndDate() != null) {
			q.setParameter("endDate", criteria.getEndDate());
		}

		rawList = q.getResultList();

		for (Object[] b : rawList) {
			results.add(new SnakeBitePolicyMonthlyReport((SnakeBitePolicy) b[0], (Payment) b[1]));
		}
		return results;
	}

	// public static void main(String[] args) {
	// EntityManager em =
	// Persistence.createEntityManagerFactory("JPA").createEntityManager();
	// List<String> list = new ArrayList<String>();
	// StringBuffer query = new StringBuffer();
	// System.out.println("******"+ 12%5);
	//
	// }
}
