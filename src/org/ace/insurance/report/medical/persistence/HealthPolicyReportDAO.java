package org.ace.insurance.report.medical.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.medical.HealthPolicyReportDTO;
import org.ace.insurance.report.medical.persistence.interfaces.IHealthPolicyReportDAO;
import org.ace.insurance.web.manage.report.medical.HealthPolicyReportCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("HealthPolicyReportDAO")
public class HealthPolicyReportDAO extends BasicDAO implements IHealthPolicyReportDAO {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<HealthPolicyReportDTO> findHealthPolicyReportDTO(HealthPolicyReportCriteria criteria) throws DAOException {
		List<HealthPolicyReportDTO> resultList = null;
		try {

			StringBuffer query = new StringBuffer();
			query.append("SELECT new org.ace.insurance.report.medical.HealthPolicyReportDTO(h) from HealthPolicyReportView h WHERE h.id IS NOT NULL");

			if (criteria.getStartDate() != null) {
				query.append(" AND h.commencementDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				query.append(" AND h.commencementDate <= :endDate");
			}
			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				query.append(" AND h.entitysId = :entitysId");
			} else if (criteria.getBranch() != null) {
				query.append(" AND h.branchId = :branchId");
			}
			if (criteria.getAgent() != null) {
				query.append(" AND h.agentId = :agentId");
			}
			if (criteria.getSalePoint() != null) {
				query.append(" AND h.salePointId = :salePointId");
			}
			if (criteria.getCustomer() != null) {
				query.append(" AND h.customerId = :customerId");
			}

			Query q = em.createQuery(query.toString());

			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}

			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				q.setParameter("entitysId", criteria.getEntity().getId());
			}

			else if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}

			if (criteria.getSalePoint() != null) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			if (criteria.getAgent() != null) {
				q.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getCustomer() != null) {
				q.setParameter("customerId", criteria.getCustomer().getId());
			}

			resultList = q.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Failed to find HealthPolicyReportDTO", pe);
		}
		return resultList;
	}
}
