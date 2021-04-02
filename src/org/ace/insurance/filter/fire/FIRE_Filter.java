package org.ace.insurance.filter.fire;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.filter.cirteria.CRIA002;
import org.ace.insurance.filter.fire.interfaces.IFIRE_Filter;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component("FIRE_Filter")
public class FIRE_Filter extends BasicDAO implements IFIRE_Filter {

	private Logger logger = Logger.getLogger(getClass());

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FPLC001> find(CRIA002 criteria) throws DAOException {
		List<Object[]> objectList = new ArrayList<Object[]>();
		Map<String, FPLC001> resultMap = new HashMap<String, FPLC001>();
		String id;
		String policyNo;
		String proposalNo;
		String saleMan;
		String agent;
		String customer;
		String branch;
		double premium;
		double sumInsured;
		PaymentType paymenttype;
		Date submittedDate;
		try {
			logger.debug("find() method has been started.");
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT DISTINCT f.id, f.policyNo, f.fireProposal.proposalNo, f.saleMan, f.agent, f.customer, f.organization, "
					+ "f.branch.name, fpp.premium, fpp.sumInsured, f.paymentType, f.commenmanceDate "
					+ "FROM FirePolicy f INNER Join f.policyBuildingInfoList pb INNER JOIN pb.firePolicyProductInfoList fpp WHERE f.policyNo IS NOT NULL");
			if (!isEmpty(criteria.getAgentId())) {
				queryString.append(" AND f.agent.id = '" + criteria.getAgentId() + "'");
			}
			if (!isEmpty(criteria.getStartDate())) {
				queryString.append(" AND f.commenmanceDate >= '" + criteria.getSQLStartDate() + "'");
			}
			if (!isEmpty(criteria.getEndDate())) {
				queryString.append(" AND f.commenmanceDate <= '" + criteria.getSQLEndDate() + "'");
			}
			if (!isEmpty(criteria.getCustomerId())) {
				queryString.append(" AND f.customer.id = '" + criteria.getCustomerId() + "'");
			}
			if (!isEmpty(criteria.getOrganizationId())) {
				queryString.append(" AND f.organization.id = '" + criteria.getOrganizationId() + "'");
			}
			if (!isEmpty(criteria.getSaleManId())) {
				queryString.append(" AND f.saleMan.id = '" + criteria.getSaleManId() + "'");
			}
			if (!isEmpty(criteria.getBranchId())) {
				queryString.append(" AND f.branch.id = '" + criteria.getBranchId() + "'");
			}
			if (!isEmpty(criteria.getProductId())) {
				queryString.append(" AND fpp.product.id = '" + criteria.getProductId() + "'");
			}
			if (!isEmpty(criteria.getReferenceNo())) {
				queryString.append(" AND f.policyNo = '" + criteria.getReferenceNo() + "'");
			}
			/* Executed query */
			Query query = em.createQuery(queryString.toString());

			objectList = query.getResultList();
			for (Object[] b : objectList) {
				id = (String) b[0];
				policyNo = (String) b[1];
				proposalNo = (String) b[2];
				if (b[3] == null) {
					saleMan = "";
				} else {
					SaleMan s = (SaleMan) b[3];
					saleMan = s.getFullName();
				}
				if (b[4] == null) {
					agent = "";
				} else {
					Agent a = (Agent) b[4];
					agent = a.getFullName();
				}
				if (b[6] == null) {
					Customer c = (Customer) b[5];
					customer = c.getFullName();
				} else {
					Organization org = (Organization) b[6];
					customer = org.getName();
				}
				branch = (String) b[7];
				premium = (Double) b[8];
				sumInsured = (Double) b[9];
				paymenttype = (PaymentType) b[10];
				submittedDate = (Date) b[11];
				if (resultMap.containsKey(policyNo)) {
					premium += resultMap.get(policyNo).getPremium();
					sumInsured += resultMap.get(policyNo).getSumInsured();
					resultMap.put(policyNo, new FPLC001(id, policyNo, proposalNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, submittedDate));
				} else {
					resultMap.put(policyNo, new FPLC001(id, policyNo, proposalNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, submittedDate));
				}
			}
			em.flush();

			logger.debug("find() method has been successfully finished.");
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifePolicy by LPLC001 : ", pe);
		}
		RegNoSorter<FPLC001> regNoSorter = new RegNoSorter<FPLC001>(new ArrayList<FPLC001>(resultMap.values()));
		return regNoSorter.getSortedList();
	}
}
