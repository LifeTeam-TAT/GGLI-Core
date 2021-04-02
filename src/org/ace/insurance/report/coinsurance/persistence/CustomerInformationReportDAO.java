package org.ace.insurance.report.coinsurance.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.report.coinsurance.persistence.interfaces.ICustomerInformationReportDAO;
import org.ace.insurance.report.customer.CustomerInformationCriteria;
import org.ace.insurance.report.customer.CustomerInformationReport;
import org.ace.insurance.report.customer.FamilyInfoDTO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("CustomerInformationReportDAO")
public class CustomerInformationReportDAO extends BasicDAO implements ICustomerInformationReportDAO {

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CustomerInformationReport> findAllCustomer(CustomerInformationCriteria criteria) throws DAOException {
		List<CustomerInformationReport> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT NEW " + CustomerInformationReport.class.getName() + " (c) ");
			query.append(" FROM CustomerInformationReportView c WHERE c.customerId is NOT NULL");

			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.append(" AND c.dob >= :startDate");
			}

			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetStartDate(criteria.getEndDate()));
				query.append(" AND c.dob <= :endDate");
			}

			if (criteria.getCustomer() != null) {
				query.append(" AND c.customerId = :customerId");
			}
			if (null != criteria.getEntity() && null == criteria.getCustomerBrach()) {
				query.append(" AND c.entitysId = :entityId");
				
			} else if (criteria.getCustomerBrach() != null) {
				query.append(" AND c.branchId = :branchId");
			}

			if (null != criteria.getCustomersalepoint()) {
				query.append(" AND c.salePointId = :salePointId");
			}
			
			if (criteria.getActivePolicy() > 0) {
				query.append(" AND c.activePolicy >= :activePolicy");
			}

			if (criteria.getEndActivePolicy() > 0) {
				query.append(" AND c.activePolicy <= :endActivePolicy");
			}

			Query q = em.createQuery(query.toString());

			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				q.setParameter("customerId", criteria.getCustomer());
			}
			if (null != criteria.getEntity() && null == criteria.getCustomerBrach()) {
				q.setParameter("entityId", criteria.getEntity().getId());
			}
			if (criteria.getCustomerBrach() != null) {
				q.setParameter("branchId", criteria.getCustomerBrach().getId());
			}
			if (null != criteria.getCustomersalepoint()) {
				q.setParameter("salePointId", criteria.getCustomersalepoint().getId());
			}
			if (criteria.getActivePolicy() > 0) {
				q.setParameter("activePolicy", criteria.getActivePolicy());
			}
			if (criteria.getEndActivePolicy() > 0) {
				q.setParameter("endActivePolicy", criteria.getEndActivePolicy());
			}

			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of General Agent Sale by criteria.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FamilyInfoDTO> findFamilyInfoByCustomerId(String id) throws DAOException {
		List<FamilyInfoDTO> result = new ArrayList<FamilyInfoDTO>();
		List<Object[]> objects = new ArrayList<Object[]>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT (RTRIM(LTRIM(F.INITIALID))+' '+ RTRIM(LTRIM(F.FIRSTNAME))+' '+RTRIM(LTRIM(F.MIDDLENAME))+' '+RTRIM(LTRIM(F.LASTNAME)))"
					+ ", F.IDNO, R.NAME, F.DATEOFBIRTH FROM CUSTOMER_FAMILY_LINK F LEFT JOIN RELATIONSHIP R ON R.ID = F.RELATIONSHIPID");
			if (id != null) {
				query.append(" WHERE F.CUSTOMERID = ?");
			}
			Query q = em.createNativeQuery(query.toString());
			if (id != null) {
				q.setParameter(1, id);
			}
			objects = q.getResultList();

			String name;
			String nrc;
			String relationship;
			Date dob;

			for (Object[] o : objects) {
				name = (String) o[0];
				name = name.replace("  ", " ");
				nrc = (String) o[1];
				relationship = (String) o[2];
				dob = (Date) o[3];
				result.add(new FamilyInfoDTO(name, nrc, relationship, dob));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to retrieve Family Info By Customer ID (Customer ID. : " + id + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<IPolicy> findAllPolicies(String customerId) throws DAOException {
		List<IPolicy> result = new ArrayList<IPolicy>();
		List<LifePolicy> lifePolicyList = null;
		try {
			Query lifeQuery = em.createNamedQuery("LifePolicy.findByCustomer");
			lifeQuery.setParameter("customerId", customerId);
			lifePolicyList = lifeQuery.getResultList();
			for (LifePolicy policy : lifePolicyList) {
				result.add(policy);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of MotorPolicy by ProposalID : ", pe);
		}
		return result;
	}
}
