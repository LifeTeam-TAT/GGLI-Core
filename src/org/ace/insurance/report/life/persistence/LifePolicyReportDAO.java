package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.LifePolicyReport;
import org.ace.insurance.report.life.LifePolicyReportCriteria;
import org.ace.insurance.report.life.ShortTermEndowmentLifeReportCriteria;
import org.ace.insurance.report.life.persistence.interfaces.ILifePolicyReportDAO;
import org.ace.insurance.report.life.view.LifePolicyView;
import org.ace.insurance.report.personalAccident.PersonalAccidentPolicyReport;
import org.ace.insurance.report.personalAccident.PersonalAccidentPolicyView;
import org.ace.insurance.report.shortEndowLife.NewShortTermEndowmentLifePolicyReport;
import org.ace.insurance.report.shortEndowLife.NewShortTermEndowmentLifePolicyView;
import org.ace.insurance.report.shortEndowLife.ShortEndowLifePolicyReport;
import org.ace.insurance.report.shortEndowLife.ShortEndowLifePolicyView;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifePolicyReportDAO")
public class LifePolicyReportDAO extends BasicDAO implements ILifePolicyReportDAO {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicyReport> findLifePolicyReport(LifePolicyReportCriteria lifePolicyReportCriteria, List<String> productIdList) throws DAOException {
		List<LifePolicyReport> result = new ArrayList<LifePolicyReport>();
		List<LifePolicyView> viewList = new ArrayList<LifePolicyView>();
		try {
			StringBuffer query = new StringBuffer();

			query.append("SELECT l FROM LifePolicyView l WHERE l.id is not null ");
			query.append(" AND l.productId IN :productIdList");
			if (lifePolicyReportCriteria.getAgent() != null) {
				query.append(" AND l.agentId = :agentId");
			}

			if (lifePolicyReportCriteria.getCustomer() != null) {
				query.append(" AND l.customerId = :customerId");
			}
			if (lifePolicyReportCriteria.getBranch() != null) {
				query.append(" AND l.branchId = :branchId");
			}
			if (lifePolicyReportCriteria.getStartDate() != null) {
				lifePolicyReportCriteria.setStartDate(Utils.resetStartDate(lifePolicyReportCriteria.getStartDate()));
				query.append(" AND l.commenmanceDate >= :startDate");
			}
			if (lifePolicyReportCriteria.getEndDate() != null) {
				lifePolicyReportCriteria.setEndDate(Utils.resetEndDate(lifePolicyReportCriteria.getEndDate()));
				query.append(" AND l.commenmanceDate <= :endDate");
			}
			if (lifePolicyReportCriteria.getProposaltype() != null) {
				query.append(" AND l.status = :status");
			}

			if (null != lifePolicyReportCriteria.getSalePoint()) {
				query.append(" AND l.salePointId =:salePointId");
			}

			query.append(" order by l.branchName, l.policyNo");

			Query q = em.createQuery(query.toString());
			q.setParameter("productIdList", productIdList);
			if (lifePolicyReportCriteria.getAgent() != null) {
				q.setParameter("agentId", lifePolicyReportCriteria.getAgent().getId());
			}
			if (lifePolicyReportCriteria.getCustomer() != null) {
				q.setParameter("customerId", lifePolicyReportCriteria.getCustomer().getId());
			}
			if (lifePolicyReportCriteria.getBranch() != null) {
				q.setParameter("branchId", lifePolicyReportCriteria.getBranch().getId());
			}
			if (lifePolicyReportCriteria.getStartDate() != null) {
				q.setParameter("startDate", lifePolicyReportCriteria.getStartDate());
			}
			if (lifePolicyReportCriteria.getEndDate() != null) {
				q.setParameter("endDate", lifePolicyReportCriteria.getEndDate());
			}
			if (lifePolicyReportCriteria.getProposaltype() != null) {
				q.setParameter("status", lifePolicyReportCriteria.getProposaltype().getLabel());
			}
			if (null != lifePolicyReportCriteria.getSalePoint()) {
				q.setParameter("salePointId", lifePolicyReportCriteria.getSalePoint().getId());
			}

			viewList = q.getResultList();
			em.flush();
			if (viewList != null) {
				for (LifePolicyView view : viewList) {
					result.add(new LifePolicyReport(view));
				}
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifePolicyReport by criteria.", pe);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonalAccidentPolicyReport> findPersonalAccidentPolicyReport(LifePolicyReportCriteria lifePolicyReportCriteria) throws DAOException {
		List<PersonalAccidentPolicyReport> result = new ArrayList<PersonalAccidentPolicyReport>();
		List<PersonalAccidentPolicyView> viewList = null;
		try {
			StringBuffer query = new StringBuffer();

			query.append("SELECT p FROM PersonalAccidentPolicyView p WHERE p.policyId is not null ");

			if (lifePolicyReportCriteria.getAgent() != null) {
				query.append(" AND p.agentId = :agentId");
			}

			if (lifePolicyReportCriteria.getCustomer() != null) {
				query.append(" AND p.customerId = :customerId");
			}
			if (lifePolicyReportCriteria.getEntity() != null && lifePolicyReportCriteria.getBranch() == null) {
				query.append(" AND p.entityId = :entityId");
			} else if (lifePolicyReportCriteria.getBranch() != null) {
				query.append(" AND p.branchId = :branchId");
			}
			if (lifePolicyReportCriteria.getSalePoint() != null) {
				query.append(" AND p.salePointId = :salePointId");
			}
			if (lifePolicyReportCriteria.getOrganization() != null) {
				query.append(" AND p.organizationId =:organizationId");
			}
			if (lifePolicyReportCriteria.getStartDate() != null) {
				lifePolicyReportCriteria.setStartDate(Utils.resetStartDate(lifePolicyReportCriteria.getStartDate()));
				query.append(" AND p.commenmanceDate >= :startDate");
			}
			if (lifePolicyReportCriteria.getEndDate() != null) {
				lifePolicyReportCriteria.setEndDate(Utils.resetEndDate(lifePolicyReportCriteria.getEndDate()));
				query.append(" AND p.commenmanceDate <= :endDate");
			}
			if (lifePolicyReportCriteria.getProduct() != null) {
				query.append(" AND p.productId =:productId");
			}
			query.append(" order by p.branchId, p.policyNo");

			Query q = em.createQuery(query.toString());

			if (lifePolicyReportCriteria.getAgent() != null) {
				q.setParameter("agentId", lifePolicyReportCriteria.getAgent().getId());
			}
			if (lifePolicyReportCriteria.getCustomer() != null) {
				q.setParameter("customerId", lifePolicyReportCriteria.getCustomer().getId());
			}
			if (lifePolicyReportCriteria.getEntity() != null && lifePolicyReportCriteria.getBranch() == null) {
				q.setParameter("entityId", lifePolicyReportCriteria.getEntity().getId());
			} else if (lifePolicyReportCriteria.getBranch() != null) {
				q.setParameter("branchId", lifePolicyReportCriteria.getBranch().getId());
			}
			if (lifePolicyReportCriteria.getSalePoint() != null) {
				q.setParameter("salePointId", lifePolicyReportCriteria.getSalePoint().getId());
			}
			if (lifePolicyReportCriteria.getOrganization() != null) {
				q.setParameter("organizationId", lifePolicyReportCriteria.getOrganization().getId());
			}
			if (lifePolicyReportCriteria.getStartDate() != null) {
				q.setParameter("startDate", lifePolicyReportCriteria.getStartDate());
			}
			if (lifePolicyReportCriteria.getEndDate() != null) {
				q.setParameter("endDate", lifePolicyReportCriteria.getEndDate());
			}
			if (lifePolicyReportCriteria.getProduct() != null) {
				q.setParameter("productId", lifePolicyReportCriteria.getProduct().getId());
			}
			viewList = q.getResultList();
			em.flush();
			if (viewList != null) {
				for (PersonalAccidentPolicyView view : viewList) {
					result.add(new PersonalAccidentPolicyReport(view));
				}
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of PersonalAccidentPolicyReport by criteria.", pe);
		}

		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ShortEndowLifePolicyReport> findShortEndowLifePolicyReport(LifePolicyReportCriteria lifePolicyReportCriteria) throws DAOException {
		List<ShortEndowLifePolicyReport> result = new ArrayList<ShortEndowLifePolicyReport>();
		List<ShortEndowLifePolicyView> viewList = null;
		try {
			StringBuffer query = new StringBuffer();

			query.append("SELECT p FROM ShortEndowLifePolicyView p WHERE p.policyNo is not null ");
			if (lifePolicyReportCriteria.getAgent() != null) {
				query.append(" AND p.agentId = :agentId");
			}
			if (lifePolicyReportCriteria.getCustomer() != null) {
				query.append(" AND p.customerId = :customerId");
			}
			if (null != lifePolicyReportCriteria.getEntity() && null == lifePolicyReportCriteria.getBranch()) {
				query.append(" AND p.entitysId = :entityId");
			} else if (lifePolicyReportCriteria.getBranch() != null) {
				query.append(" AND p.branchId = :branchId");
			}

			if (null != lifePolicyReportCriteria.getSalePoint()) {
				query.append(" AND p.salePointId = :salePointId");
			}
			if (lifePolicyReportCriteria.getSalePoint() != null) {
				query.append(" AND p.salePointId = :salePointId");
			}
			if (lifePolicyReportCriteria.getOrganization() != null) {
				query.append(" AND p.organizationId =:organizationId");
			}
			if (lifePolicyReportCriteria.getStartDate() != null) {
				lifePolicyReportCriteria.setStartDate(Utils.resetStartDate(lifePolicyReportCriteria.getStartDate()));
				query.append(" AND p.commenmanceDate >= :startDate");
			}
			if (lifePolicyReportCriteria.getEndDate() != null) {
				lifePolicyReportCriteria.setEndDate(Utils.resetEndDate(lifePolicyReportCriteria.getEndDate()));
				query.append(" AND p.commenmanceDate <= :endDate");
			}
			query.append(" order by p.branchId, p.policyNo");

			Query q = em.createQuery(query.toString());

			if (lifePolicyReportCriteria.getAgent() != null) {
				q.setParameter("agentId", lifePolicyReportCriteria.getAgent().getId());
			}
			if (lifePolicyReportCriteria.getCustomer() != null) {
				q.setParameter("customerId", lifePolicyReportCriteria.getCustomer().getId());
			}
			if (null != lifePolicyReportCriteria.getEntity() && null == lifePolicyReportCriteria.getBranch()) {
				q.setParameter("entityId", lifePolicyReportCriteria.getEntity().getId());
			}
			if (lifePolicyReportCriteria.getBranch() != null) {
				q.setParameter("branchId", lifePolicyReportCriteria.getBranch().getId());
			}
			if (null != lifePolicyReportCriteria.getSalePoint()) {
				q.setParameter("salePointId", lifePolicyReportCriteria.getSalePoint().getId());
			}
			if (lifePolicyReportCriteria.getSalePoint() != null) {
				q.setParameter("salePointId", lifePolicyReportCriteria.getSalePoint().getId());
			}
			if (lifePolicyReportCriteria.getOrganization() != null) {
				q.setParameter("organizationId", lifePolicyReportCriteria.getOrganization().getId());
			}
			if (lifePolicyReportCriteria.getStartDate() != null) {
				q.setParameter("startDate", lifePolicyReportCriteria.getStartDate());
			}
			if (lifePolicyReportCriteria.getEndDate() != null) {
				q.setParameter("endDate", lifePolicyReportCriteria.getEndDate());
			}
			viewList = q.getResultList();
			em.flush();
			if (viewList != null) {
				for (ShortEndowLifePolicyView view : viewList) {
					result.add(new ShortEndowLifePolicyReport(view));
				}
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ShortEndowLifePolicyReport by criteria.", pe);
		}
		RegNoSorter<ShortEndowLifePolicyReport> regNoSorter = new RegNoSorter<ShortEndowLifePolicyReport>(result);
		return regNoSorter.getSortedList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<NewShortTermEndowmentLifePolicyReport> findNewShortTermEndowmentLifePolicyReport(ShortTermEndowmentLifeReportCriteria criteria) throws DAOException {
		List<NewShortTermEndowmentLifePolicyReport> result = new ArrayList<NewShortTermEndowmentLifePolicyReport>();
		List<NewShortTermEndowmentLifePolicyView> viewList = null;
		try {
			StringBuffer query = new StringBuffer();

			query.append("SELECT p FROM NewShortTermEndowmentLifePolicyView p WHERE p.policyId is not null ");
			if (criteria.getAgentId() != null && !criteria.getAgentId().isEmpty()) {
				query.append(" AND p.agentId = :agentId");
			}

			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				query.append(" AND p.entitysId = :entityId");
			} else if (criteria.getBranch() != null) {
				query.append(" AND p.branchId = :branchId");
			}

			if (null != criteria.getSalePoint()) {
				query.append(" AND p.salePointId = :salePointId");
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.append(" AND p.policyStartDate >= :startDate ");
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.append(" AND p.policyStartDate <= :endDate");
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				query.append(" AND p.policyNo = :policyNo");
			}
			if (criteria.getInsurePersonName() != null && !criteria.getInsurePersonName().isEmpty()) {
				query.append(" AND UPPER(p.insuredPersonName) LIKE CONCAT('%',UPPER(:insuredPersonName),'%') ");
			}
			query.append(" order by p.branchId, p.policyNo");

			Query q = em.createQuery(query.toString());

			if (criteria.getAgentId() != null && !criteria.getAgentId().isEmpty()) {
				q.setParameter("agentId", criteria.getAgentId());
			}

			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				q.setParameter("entityId", criteria.getEntity().getId());
			}
			if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			if (null != criteria.getSalePoint()) {
				q.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				q.setParameter("policyNo", criteria.getPolicyNo());
			}
			if (criteria.getInsurePersonName() != null && !criteria.getInsurePersonName().isEmpty()) {
				q.setParameter("insuredPersonName", criteria.getInsurePersonName());
			}

			viewList = q.getResultList();
			em.flush();
			if (viewList != null) {
				for (NewShortTermEndowmentLifePolicyView view : viewList) {
					result.add(new NewShortTermEndowmentLifePolicyReport(view));
				}
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ShortEndowLifePolicyReport by criteria.", pe);
		}
//		RegNoSorter<NewShortTermEndowmentLifePolicyReport> regNoSorter = new RegNoSorter<NewShortTermEndowmentLifePolicyReport>(result);
		return result;
	}
}
