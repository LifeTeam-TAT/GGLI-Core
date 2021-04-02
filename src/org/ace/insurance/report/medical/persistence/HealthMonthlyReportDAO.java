package org.ace.insurance.report.medical.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.common.MonthlyReportCriteria;
import org.ace.insurance.report.medical.HealthMonthlyReport;
import org.ace.insurance.report.medical.persistence.interfaces.IHealthMonthlyReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("HealthMonthlyReportDAO")
public class HealthMonthlyReportDAO extends BasicDAO implements IHealthMonthlyReportDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public List<HealthMonthlyReport> findHealthMonthlyReport(MonthlyReportCriteria criteria) throws DAOException {
		List<HealthMonthlyReport> resultList = new ArrayList<HealthMonthlyReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append(
					"SELECT NEW org.ace.insurance.report.medical.HealthMonthlyReport(m.id,m.activedPolicyStartDate,m.policyNo,m.branchId,m.salePointName,m.insuredPersonName,m.gender,m.dateOfBirth,m.age,m.fullIdNo,"
							+ "m.occupation,m.address,m.paymentType,m.premium,m.receiptNo,m.paymentDate,m.beneficiaryName,m.relationship,m.unit,m.basicPlusUnit,m.addOn1,m.addOn2,m.salePersonName,"
							+ "m.customerType,m.salePersonType,m.commission,m.productId,m.proposalNo) FROM MedicalMonthlyView m WHERE m.policyNo IS NOT NULL AND m.activedPolicyStartDate >= :startDate "
							+ "AND m.activedPolicyStartDate <= :endDate");

			if (criteria.getMedicalProductType() != null) {
				query.append(" AND m.productId IN :productId ");
			}
			
			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				query.append(" AND m.entitysId = :entityId");
			} else if (criteria.getBranch() != null) {
				query.append(" AND m.branchId = :branchId");
			}

			if (null != criteria.getSalePoint()) {
				query.append(" AND m.salePointId = :salePointId");
			}
			
			if (criteria.getSalePoint() != null) {
				query.append(" AND m.salePointId = :salePointId");
			}
			
			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
			q.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));

			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				q.setParameter("entityId", criteria.getEntity().getId());
			}
			if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			if (criteria.getMedicalProductType() != null) {
				q.setParameter("productId", ProductIDConfig.getIdByMedicalProudctType(criteria.getMedicalProductType()));
			}
			resultList = q.getResultList();

			RegNoSorter<HealthMonthlyReport> sortedResultList = new RegNoSorter<>(resultList);
			resultList = sortedResultList.getSortedList();

		} catch (PersistenceException pe) {
			throw translate("Failed to find healthMonthlyReportDTO", pe);
		}
		return resultList;
	}
}
