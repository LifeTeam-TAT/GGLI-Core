package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.LifeProductType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.report.common.StudentLifeCriteria;
import org.ace.insurance.report.common.SummaryReportCriteria;
import org.ace.insurance.report.life.SnakeBitePolicyMonthlyReport;
import org.ace.insurance.report.life.persistence.interfaces.ILifePolicyMonthlyReportDAO;
import org.ace.insurance.report.life.report.LifeMonthlyReport;
import org.ace.insurance.report.studentLife.BCStudentLifeView;
import org.ace.insurance.report.studentLife.NewStudentLifeView;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifePolicyMonthlyReportDAO")
public class LifePolicyMonthlyReportDAO extends BasicDAO implements ILifePolicyMonthlyReportDAO {

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeMonthlyReport> findLifePolicyMonthlyReport(SummaryReportCriteria criteria) throws DAOException {
		List<LifeMonthlyReport> result = new ArrayList<LifeMonthlyReport>();
		StringBuffer query = new StringBuffer();

		try {
			query.append("SELECT distinct new org.ace.insurance.report.life.report.LifeMonthlyReport(l) FROM LifeMonthlyReportView l WHERE  l.policyNo IS NOT NULL ");

			if (criteria.getYear() != 0) {
				query.append(" AND l.activedPolicyStartDate  >= :startDate");
			}
			if (criteria.getYear() != 0) {
				query.append("  AND l.activedPolicyStartDate  <= :endDate");
			}
			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				query.append(" AND l.entitysId = :entityId");
			} else if (criteria.getBranch() != null) {

				query.append(" AND l.branchId = :branchId");
			}
			if (null != criteria.getSalepoint()) {
				query.append(" AND l.salePointId = :salePointId");
			}
			if (criteria.getLifeProductType() != null) {
				query.append(" AND l.productId = :productId");
			}

			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", getStartDate(criteria));
			q.setParameter("endDate", getEndDate(criteria));

			if (criteria.getLifeProductType().equals(LifeProductType.SNAKE_BITE)) {
				q.setParameter("productId", ProductIDConfig.getSnakeBikeId());
			} else if (criteria.getLifeProductType().equals(LifeProductType.GROUP_LIFE)) {
				q.setParameter("productId", ProductIDConfig.getGroupLifeId());
			} else if (criteria.getLifeProductType().equals(LifeProductType.PUBLIC_LIFE)) {
				q.setParameter("productId", ProductIDConfig.getPublicLifeId());
			} else if (criteria.getLifeProductType().equals(LifeProductType.SHORT_ENDOWMENT_LIFE)) {
				q.setParameter("productId", ProductIDConfig.getShortEndowLifeId());
			} else {
				q.setParameter("productId", ProductIDConfig.getSportManId());
			}
			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				q.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}

			if (null != criteria.getSalepoint()) {
				q.setParameter("salePointId", criteria.getSalepoint().getId());
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of FirePolicy by criteria.", pe);
		}
		RegNoSorter<LifeMonthlyReport> regNoSorter = new RegNoSorter<LifeMonthlyReport>(result);
		return regNoSorter.getSortedList();
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeMonthlyReport> findLifePolicyRenewalMonthlyReport(SummaryReportCriteria criteria) throws DAOException {
		List<LifeMonthlyReport> result = new ArrayList<LifeMonthlyReport>();
		StringBuffer query = new StringBuffer();
		try {

			query.append("SELECT new org.ace.insurance.report.life.report.LifeMonthlyReport(l) FROM LifeRenewalMonthlyReportView l WHERE l.policyNo IS NOT NULL ");

			if (criteria.getYear() != 0) {
				query.append(" AND l.paymentDate >= :startDate");
			}
			if (criteria.getYear() != 0) {
				query.append(" AND l.paymentDate <= :endDate");
			}
			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				query.append(" AND l.entitysId = :entityId");
			} else if (criteria.getBranch() != null) {
				query.append(" AND l.branchId = :branchId");
			}

			if (null != criteria.getSalepoint()) {
				query.append(" AND l.salePointId = :salePointId");
			}

			if (criteria.getLifeProductType() != null) {
				query.append(" AND l.productId = :productId");
			}

			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", getStartDate(criteria));
			q.setParameter("endDate", getEndDate(criteria));

			if (criteria.getLifeProductType().equals(LifeProductType.GROUP_LIFE)) {
				q.setParameter("productId", ProductIDConfig.getGroupLifeId());
			} else if (criteria.getLifeProductType().equals(LifeProductType.PUBLIC_LIFE)) {
				q.setParameter("productId", ProductIDConfig.getPublicLifeId());
			} else {
				q.setParameter("productId", ProductIDConfig.getShortEndowLifeId());
			}

			if (null != criteria.getEntity() && null == criteria.getBranch()) {
				q.setParameter("entityId", criteria.getEntity().getId());
			} else if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}
			if (null != criteria.getSalepoint()) {
				q.setParameter("salePointId", criteria.getSalepoint().getId());
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifePolicyMonthlyReport  by criteria.", pe);
		}
		RegNoSorter<LifeMonthlyReport> regNoSorter = new RegNoSorter<LifeMonthlyReport>(result);
		return regNoSorter.getSortedList();
	}

	public Date getStartDate(SummaryReportCriteria criteria) {
		Calendar cal = Calendar.getInstance();
		int year = criteria.getYear();
		int month = criteria.getMonth();
		cal.set(year, month, 1);
		Date startDate = cal.getTime();
		return Utils.resetStartDate(startDate);
	}

	public Date getEndDate(SummaryReportCriteria criteria) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, criteria.getMonth());
		cal.set(Calendar.YEAR, criteria.getYear());
		DateTime dateTime = new DateTime(cal.getTime());
		DateTime lastTime = dateTime.dayOfMonth().withMaximumValue();
		return Utils.resetEndDate(new Date(lastTime.getMillis()));
	}

	public List<SnakeBitePolicyMonthlyReport> findSnakeBitePolicyMonthlyReport(SummaryReportCriteria criteria) throws DAOException {

		List<SnakeBitePolicyMonthlyReport> result = new ArrayList<SnakeBitePolicyMonthlyReport>();
		StringBuffer query = new StringBuffer();

		try {

			query.append("SELECT DISTINCT l, p.receiptNo FROM SnakeBitePolicy l INNER JOIN Payment p ON l.referenceNo = p.receiptNo "
					+ "WHERE l.snakeBitePolicyNo IS NOT NULL AND l.product.id = :productId AND l.policyStartDate >= :startDate AND l.policyStartDate <= :endDate");
			if (criteria.getBranch() != null) {
				query.append(" AND l.branch.id = :branchId");
			}
			query.append(" ORDER BY l.snakeBitePolicyNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", getStartDate(criteria));
			q.setParameter("endDate", getEndDate(criteria));
			q.setParameter("productId", criteria.getProductId());

			if (criteria.getBranch() != null) {
				q.setParameter("branchId", criteria.getBranch().getId());
			}

			@SuppressWarnings("unchecked")
			List<Object[]> raws = q.getResultList();

			for (Object[] a : raws) {
				SnakeBitePolicy snakebitePolicy = (SnakeBitePolicy) a[0];
				String receiptNo = (String) a[1];
				result.add(new SnakeBitePolicyMonthlyReport(snakebitePolicy, criteria.getLifeProductType(), receiptNo));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find all of FirePolicy by criteria.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<NewStudentLifeView> findStudentLifePolicyMonthlyReportByCriteria(StudentLifeCriteria studentLifeCriteria) {
		List<NewStudentLifeView> result = new ArrayList<NewStudentLifeView>();
		try {
			StringBuffer query = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			query.append("select n from NewStudentLifeView n where n.policyNo is not null");
			if (studentLifeCriteria.getStartDate() != null) {
				query.append(" AND n.startDate>=:startDate");
				param.put("startDate", Utils.resetStartDate(studentLifeCriteria.getStartDate()));
			}
			if (studentLifeCriteria.getEndDate() != null) {
				query.append(" AND n.startDate<= :endDate");
				param.put("endDate", Utils.resetEndDate(studentLifeCriteria.getEndDate()));
			}
			if (studentLifeCriteria.getStartMonthType() > -1 || studentLifeCriteria.getEndMonthType() > -1) {
				if (studentLifeCriteria.getStartMonthType() > -1 && studentLifeCriteria.getEndMonthType() > -1) {
					query.append(" AND n.startDate >= :startMonthType");
					param.put("startMonthType", Utils.getStartDate(studentLifeCriteria.getYear(), studentLifeCriteria.getStartMonthType()));
					query.append(" AND n.startDate<=:endMonthType");
					param.put("endMonthType", Utils.getEndDate(studentLifeCriteria.getYear(), studentLifeCriteria.getEndMonthType()));
				} else {
					if (studentLifeCriteria.getStartMonthType() > -1) {
						query.append(" AND FUNC('MONTH', n.startDate) = :month");
						param.put("month", studentLifeCriteria.getStartMonthType() + 1);
					} else {
						query.append(" AND FUNC('MONTH', n.startDate)= :month");
						param.put("month", studentLifeCriteria.getEndMonthType() + 1);
					}
					query.append(" AND FUNC('YEAR', n.startDate) = :year");
					param.put("year", studentLifeCriteria.getYear());
				}
			}
			if (studentLifeCriteria.getEntity() != null && studentLifeCriteria.getBranch() == null) {
				query.append(" AND n.entityId like :entityId");
				param.put("entityId", "%" + studentLifeCriteria.getEntity().getId() + "%");
			} else if (studentLifeCriteria.getBranch() != null) {
				query.append(" AND n.branchId like :branchId");
				param.put("branchId", "%" + studentLifeCriteria.getBranch().getId() + "%");
			}
			if (studentLifeCriteria.getSalePoint() != null) {
				query.append(" AND n.salePointId like :salePointId");
				param.put("salePointId", "%" + studentLifeCriteria.getSalePoint().getId() + "%");
			}
			if (studentLifeCriteria.getPaymentType() != null) {
				query.append(" AND n.paymentTypeId like :paymentTypeId");
				param.put("paymentTypeId", "%" + studentLifeCriteria.getPaymentType() + "%");
			}
			if (studentLifeCriteria.getChannel() != null) {
				query.append(" AND n.channel like :channel");
				param.put("channel", "%" + studentLifeCriteria.getChannel() + "%");
			}
			Query q = em.createQuery(query.toString());
			for (String key : param.keySet()) {
				q.setParameter(key, param.get(key));
			}
			result = q.getResultList();

		} catch (PersistenceException pe) {
			throw translate("Failed to find StudentLife Policy Monthly Report By Criteria", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<BCStudentLifeView> findBCStudentLifePolicyMonthlyReportByCriteria(StudentLifeCriteria studentLifeCriteria) {
		List<BCStudentLifeView> result = new ArrayList<BCStudentLifeView>();
		try {
			StringBuffer query = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			query.append("select n from BCStudentLifeView n where n.policyNo is not null");
			if (studentLifeCriteria.getStartDate() != null) {
				query.append(" AND n.receive_Date >= :startDate");
				param.put("startDate", Utils.resetStartDate(studentLifeCriteria.getStartDate()));
			}
			if (studentLifeCriteria.getEndDate() != null) {
				query.append(" AND n.receive_Date <= :endDate");
				param.put("endDate", Utils.resetEndDate(studentLifeCriteria.getEndDate()));
			}
			if (studentLifeCriteria.getStartMonthType() > -1 || studentLifeCriteria.getEndMonthType() > -1) {
				if (studentLifeCriteria.getStartMonthType() > -1 && studentLifeCriteria.getEndMonthType() > -1) {
					query.append(" AND n.receive_Date >= :startMonthType");
					param.put("startMonthType", Utils.getStartDate(studentLifeCriteria.getYear(), studentLifeCriteria.getStartMonthType()));
					query.append(" AND n.receive_Date<=:endMonthType");
					param.put("endMonthType", Utils.getEndDate(studentLifeCriteria.getYear(), studentLifeCriteria.getEndMonthType()));
				} else {
					if (studentLifeCriteria.getStartMonthType() > -1) {
						query.append(" AND FUNC('MONTH', n.receive_Date) = :month");
						param.put("month", studentLifeCriteria.getStartMonthType() + 1);
					} else {
						query.append(" AND FUNC('MONTH', n.receive_Date)= :month");
						param.put("month", studentLifeCriteria.getEndMonthType() + 1);
					}
					query.append(" AND FUNC('YEAR', n.receive_Date) = :year");
					param.put("year", studentLifeCriteria.getYear());
				}
			}

			if (studentLifeCriteria.getSalePoint() != null) {
				query.append(" AND n.salePointId like :salePointId");
				param.put("salePointId", "%" + studentLifeCriteria.getSalePoint().getId() + "%");
			}
			if (studentLifeCriteria.getPaymentType() != null) {
				query.append(" AND n.paymentTypeId like :paymentTypeId");
				param.put("paymentTypeId", "%" + studentLifeCriteria.getPaymentType() + "%");
			}
			if (studentLifeCriteria.getChannel() != null) {
				query.append(" AND n.channel like :channel");
				param.put("channel", "%" + studentLifeCriteria.getChannel() + "%");
			}
			Query q = em.createQuery(query.toString());
			for (String key : param.keySet()) {
				q.setParameter(key, param.get(key));
			}
			result = q.getResultList();

		} catch (PersistenceException pe) {
			throw translate("Failed to find BC Student Life Policy Monthly Report By Criteria", pe);
		}
		return result;
	}

}
