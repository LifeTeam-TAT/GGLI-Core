package org.ace.insurance.report.coinsurance.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.coinsurance.Coinsurance;
import org.ace.insurance.common.CoinsuranceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryDTO;
import org.ace.insurance.report.coinsurance.persistence.interfaces.ICoinsuranceEnquiryDAO;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("CoinsuranceDAO1")
public class CoinsuranceEnquiryDAO extends BasicDAO implements ICoinsuranceEnquiryDAO {

	@Override
	public List<CoinsuranceEnquiryDTO> findCoinsurances(CoinsuranceEnquiryCriteria coiCriteria) throws DAOException {
		List<CoinsuranceEnquiryDTO> result = new ArrayList<CoinsuranceEnquiryDTO>();
		List<Coinsurance> ciList = new ArrayList<Coinsurance>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("Select ci From Coinsurance ci WHERE ci.policyNo IS NOT NULL ");
			if (coiCriteria.getPolicyNo() != null) {
				query.append(" AND ci.policyNo like :policyNo");
			}

			if (coiCriteria.getStartDate() != null) {
				coiCriteria.setStartDate(Utils.resetStartDate(coiCriteria.getStartDate()));
				query.append(" AND ci.startDate >= :startDate");
			}

			if (coiCriteria.getEndDate() != null) {
				coiCriteria.setEndDate(Utils.resetEndDate(coiCriteria.getEndDate()));
				query.append(" AND ci.startDate <= :endDate");
			}

			if (coiCriteria.getInsuranceType() != null) {
				query.append(" AND ci.insuranceType = :insuranceType");
			}

			if (coiCriteria.getCoinsuranceType() != null) {
				query.append(" AND ci.coinsuranceType = :coinsuranceType");

			}

			Query q = em.createQuery(query.toString());

			if (coiCriteria.getPolicyNo() != null) {
				q.setParameter("policyNo", "%" + coiCriteria.getPolicyNo() + "%");
			}

			if (coiCriteria.getStartDate() != null) {
				q.setParameter("startDate", coiCriteria.getStartDate());
			}

			if (coiCriteria.getEndDate() != null) {
				q.setParameter("endDate", coiCriteria.getEndDate());
			}

			if (coiCriteria.getInsuranceType() != null) {
				q.setParameter("insuranceType", coiCriteria.getInsuranceType());
			}

			if (coiCriteria.getCoinsuranceType() != null) {
				q.setParameter("coinsuranceType", coiCriteria.getCoinsuranceType());

			}
			ciList = q.getResultList();
			em.flush();

			for (Coinsurance ci : ciList) {
				CoinsuranceEnquiryDTO coinsuranceEnquiryDTO = new CoinsuranceEnquiryDTO(ci.getId(), ci.getPolicyNo(), ci.getCustomerName(), ci.getAgentName(), ci.getStartDate(),
						ci.getInsuranceType(), ci.getCoinsuranceType(), ci.getSumInsuranced(), ci.getPremium(), ci.getCoinsuranceCompany());

				if (coiCriteria.getCoinsuranceType().equals(CoinsuranceType.IN)) {
					coinsuranceEnquiryDTO.setCustomerName(ci.getCustomerName());
				}
				result.add(coinsuranceEnquiryDTO);
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of CoinsurancePolicy by criteria.", pe);
		}
		return result;
	}

	private String CustomerNameForFire(String policyNo) {
		String result = "";
		String query = "Select f.customer, f.organization, f.bankBranch, f.ownerName From FirePolicy f Where f.policyNo = :policyNo";
		Query q = em.createQuery(query);
		q.setParameter("policyNo", policyNo);
		try {
			Object[] arr = (Object[]) q.getSingleResult();
			Customer customer = (Customer) arr[0];
			Organization organization = (Organization) arr[1];
			BankBranch bank = (BankBranch) arr[2];
			String ownerName = (String) arr[3];
			if (customer != null) {
				result = customer.getFullName();
			} else if (organization != null) {
				result = organization.getName();
			} else {
				result = bank.getName() + " ( " + ownerName + " )";
			}

		} catch (NoResultException e) {
			result = "";
		}

		return result;
	}

	private String CustomerNameForMotor(String policyNo) {
		String result = "";
		String query = "Select m.customer From MotorPolicy m Where m.policyNo = :policyNo";
		Query q = em.createQuery(query);
		q.setParameter("policyNo", policyNo);

		try {
			Customer customer = (Customer) q.getSingleResult();
			result = customer.getFullName();
		} catch (NoResultException e) {
			result = "";
		}
		return result;
	}

}
