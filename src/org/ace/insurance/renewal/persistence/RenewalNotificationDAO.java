package org.ace.insurance.renewal.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.Utils;
import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.renewal.RenewalNotification;
import org.ace.insurance.renewal.RenewalNotificationCriteria;
import org.ace.insurance.renewal.persistence.interfaces.IRenewalNotificationDAO;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.user.User;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("RenewalNotificationDAO")
public class RenewalNotificationDAO extends BasicDAO implements IRenewalNotificationDAO {

	@Resource(name = "UserProcessService")
	private IUserProcessService userProcessService;

	@Resource(name = "IDConfigLoader")
	private IDConfigLoader idConfigLoader;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RenewalNotification> findMotorPolicies(RenewalNotificationCriteria criteria) throws DAOException {
		Map<String, RenewalNotification> result = new HashMap<String, RenewalNotification>();
		User loginUser = userProcessService.getLoginUser();
		boolean accessAllBranch = loginUser.isAccessAllBranch();
		String branchId = idConfigLoader.getBranchId();
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT m.id, m.policyNo, m.customer, m.organization, m.activedPolicyEndDate, pv.registrationNo "
					+ " FROM MotorPolicy m INNER JOIN m.policyVehicleList pv WHERE m.policyNo IS NOT NULL ");
			if (criteria.getStartDate() != null) {
				queryString.append(" AND m.activedPolicyEndDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND m.activedPolicyEndDate <= :endDate");
			}
			if (!accessAllBranch) {
				queryString.append(" AND m.branch.id = :branchId");
			}

			Query query = em.createQuery(queryString.toString());
			if (criteria.getStartDate() != null) {
				query.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}
			if (criteria.getEndDate() != null) {
				query.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}
			if (!accessAllBranch) {
				query.setParameter("branchId", branchId);
			}

			List<Object[]> raws = query.getResultList();
			String id;
			String policyNo;
			Customer customer = null;
			Organization organization = null;
			String name = "";
			Date endDate;
			String regisNo;
			for (Object[] a : raws) {
				int days = 0;
				id = (String) a[0];
				policyNo = (String) a[1];
				customer = (Customer) a[2];
				organization = (Organization) a[3];
				if (customer != null) {
					name = customer.getFullName();
				} else if (organization != null) {
					name = organization.getName();
				}
//				name = (customer == null) ? organization.getName() : 
				endDate = (Date) a[4];
				regisNo = (String) a[5];
				days = Utils.daysBetween(endDate, new Date(), false, true);
				if (result.containsKey(id)) {
					RenewalNotification notification = result.get(id);
					notification.setMultipleVehicle(true);
					notification.setRegisNo(null);
					result.put(id, notification);
				} else {
					result.put(id, new RenewalNotification(id, policyNo, name, regisNo, endDate, days * (-1), false));
				}
			}

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifeProposal by criteria.", pe);
		}
		RegNoSorter<RenewalNotification> regNoSorter = new RegNoSorter<RenewalNotification>(new ArrayList<RenewalNotification>(result.values()));
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RenewalNotification> findFirePolicies(RenewalNotificationCriteria criteria) throws DAOException {
		Map<String, RenewalNotification> result = new HashMap<String, RenewalNotification>();
		User loginUser = userProcessService.getLoginUser();
		boolean accessAllBranch = loginUser.isAccessAllBranch();
		String branchId = idConfigLoader.getBranchId();
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT f.id, f.policyNo, f.customer, f.organization, f.bankCustomer, f.ownerName, f.activedPolicyEndDate, pb.buildingName , f.propertyLocation "
					+ " FROM FirePolicy f INNER JOIN f.policyBuildingInfoList pb WHERE f.policyNo IS NOT NULL");
			if (criteria.getStartDate() != null) {
				queryString.append(" AND f.activedPolicyEndDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND f.activedPolicyEndDate <= :endDate");
			}
			if (!accessAllBranch) {
				queryString.append(" AND f.branch.id = :branchId");
			}
			Query query = em.createQuery(queryString.toString());
			if (criteria.getStartDate() != null) {
				query.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}
			if (criteria.getEndDate() != null) {
				query.setParameter("endDate", Utils.resetEndDate(criteria.getEndDate()));
			}
			if (!accessAllBranch) {
				query.setParameter("branchId", branchId);
			}

			List<Object[]> raws = query.getResultList();
			String id;
			String policyNo;
			String name;
			Date endDate;
			String buildingName;
			String buildingAddress;
			for (Object[] a : raws) {
				int days = 0;
				id = (String) a[0];
				policyNo = (String) a[1];
				if (a[2] == null) {
					if (a[4] == null) {
						name = ((Organization) a[3]).getName();
					} else {
						name = ((Bank) a[4]).getName() + " ( " + a[5] + " )";
					}
				} else if (a[3] == null) {
					if (a[4] == null) {
						name = ((Customer) a[2]).getFullName();
					} else {
						name = ((Bank) a[4]).getName() + " ( " + a[5] + " )";
					}
				} else {
					if (a[2] == null) {
						name = ((Organization) a[3]).getName();
					} else {
						name = ((Customer) a[2]).getFullName();
					}
				}
				endDate = (Date) a[6];
				buildingName = (String) a[7];
				days = Utils.daysBetween(endDate, new Date(), false, true);
				buildingAddress = (String) a[8];
				if (result.containsKey(id)) {
					RenewalNotification notification = result.get(id);
					notification.getBuildingNameList().add(buildingName);
					result.put(id, notification);
				} else {
					result.put(id, new RenewalNotification(id, policyNo, name, buildingName, buildingAddress, endDate, days * (-1)));
				}
			}

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifeProposal by criteria.", pe);
		}
		RegNoSorter<RenewalNotification> regNoSorter = new RegNoSorter<RenewalNotification>(new ArrayList<RenewalNotification>(result.values()));
		return regNoSorter.getSortedList();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RenewalNotification> findGroupLifePolicies(String productId, RenewalNotificationCriteria criteria) throws DAOException {
		Map<String, RenewalNotification> result = new HashMap<String, RenewalNotification>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT l.id, l.policyNo, l.customer, l.organization, l.activedPolicyEndDate, pv.idNo FROM LifePolicy l "
					+ " INNER JOIN l.policyInsuredPersonList pv WHERE l.policyNo IS NOT NULL AND pv.product.id = :productId ");
			if (criteria.getStartDate() != null) {
				query.append(" AND l.activedPolicyEndDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				query.append(" AND l.activedPolicyEndDate <= :endDate");
			}
			Query q = em.createQuery(query.toString());
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", Utils.resetStartDate(criteria.getStartDate()));
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", Utils.resetStartDate(criteria.getEndDate()));
			}

			q.setParameter("productId", productId);
			List<Object[]> raws = q.getResultList();
			String id;
			String policyNo;
			Customer customer = null;
			Organization organization = null;
			String customerName;
			Date endDate;
			String idNo;

			for (Object[] a : raws) {
				int days = 0;
				id = (String) a[0];
				policyNo = (String) a[1];
				customer = (Customer) a[2];
				organization = (Organization) a[3];
				customerName = (customer == null) ? organization.getName() : customer.getFullName();
				endDate = (Date) a[4];
				idNo = (String) a[5];
				days = Utils.daysBetween(endDate, new Date(), false, true);
				result.put(policyNo, new RenewalNotification(id, policyNo, customerName, idNo, endDate, days * (-1), false));
			}
			em.flush();
		} catch (IllegalArgumentException ie) {
			ie.printStackTrace();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifeProposal by criteria.", pe);
		}

		RegNoSorter<RenewalNotification> regNoSorter = new RegNoSorter<RenewalNotification>(new ArrayList<RenewalNotification>(result.values()));
		return regNoSorter.getSortedList();
	}

		static public void main(String arg[]) {
		EntityManager em = Persistence.createEntityManagerFactory("JPA").createEntityManager();
		em.getTransaction().begin();
		Date startDate = Utils.createDate(2017, 1, 1);
		Date endDate = Utils.createDate(2017, 8, 1);
		StringBuffer query = new StringBuffer();
		query.append("SELECT f.id, f.policyNo," + " f.activedPolicyEndDate, fb.buildingName FROM FirePolicy f "
				+ " INNER JOIN f.policyBuildingInfoList fb WHERE f.policyNo IS NOT NULL AND f.activedPolicyEndDate >= :startDate" + " AND f.activedPolicyEndDate <= :endDate");
		Query q = em.createQuery(query.toString());
		q.setParameter("startDate", startDate);
		q.setParameter("endDate", endDate);
		List<RenewalNotification> result = new ArrayList<RenewalNotification>();
		List<Object[]> raws = q.getResultList();
		System.out.println(raws.size());
		em.getTransaction().commit();
	}
}
