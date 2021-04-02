package org.ace.insurance.filter.motor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.filter.cirteria.CRIA002;
import org.ace.insurance.filter.motor.interfaces.IMOTOR_Filter;
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

@Component("MOTOR_Filter")
public class MOTOR_Filter extends BasicDAO implements IMOTOR_Filter {
	private Logger logger = Logger.getLogger(getClass());

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MPLC001> find(CRIA002 criteria) throws DAOException {
		List<Object[]> objectList = new ArrayList<Object[]>();
		Map<String, MPLC001> resultMap = new HashMap<String, MPLC001>();
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
		Date commenmanceDate;
		try {
			logger.debug("find() method has been started.");
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT DISTINCT m.id, m.policyNo, m.saleMan, m.agent, m.customer, m.organization, "
					+ "m.branch.name, pi.premium, pi.sumInsured, m.paymentType, m.commenmanceDate, m.motorProposal.proposalNo "
					+ "FROM MotorPolicy m INNER JOIN m.policyVehicleList pi WHERE m.policyNo IS NOT NULL ");
			if (!isEmpty(criteria.getAgent())) {
				queryString.append(" AND m.agent.id = '" + criteria.getAgentId() + "'");
			}
			if (!isEmpty(criteria.getStartDate())) {
				queryString.append(" AND m.commenmanceDate >= '" + criteria.getSQLStartDate() + "'");
			}
			if (!isEmpty(criteria.getEndDate())) {
				queryString.append(" AND m.commenmanceDate <= '" + criteria.getSQLEndDate() + "'");
			}
			if (!isEmpty(criteria.getCustomerId())) {
				queryString.append(" AND m.customer.id = '" + criteria.getCustomerId() + "'");
			}
			if (!isEmpty(criteria.getOrganizationId())) {
				queryString.append(" AND m.organization.id = '" + criteria.getOrganizationId() + "'");
			}
			if (!isEmpty(criteria.getSaleManId())) {
				queryString.append(" AND m.saleMan.id = '" + criteria.getSaleManId() + "'");
			}
			if (!isEmpty(criteria.getBranchId())) {
				queryString.append(" AND m.branch.id = '" + criteria.getBranchId() + "'");
			}
			if (!isEmpty(criteria.getProductId())) {
				queryString.append(" AND pi.product.id = '" + criteria.getProductId() + "'");
			}
			if (!isEmpty(criteria.getTypeOfBodyId())) {
				queryString.append(" AND pi.typeOfBody.id = '" + criteria.getTypeOfBodyId() + "'");
			}
			if (!isEmpty(criteria.getReferenceNo())) {
				queryString.append(" AND m.policyNo= '" + criteria.getReferenceNo() + "'");
			}

			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			objectList = query.getResultList();

			System.out.println("Object List Size : " + objectList.size());

			for (Object[] b : objectList) {
				id = (String) b[0];
				policyNo = (String) b[1];
				if (b[2] == null) {
					saleMan = "";
				} else {
					SaleMan s = (SaleMan) b[2];
					saleMan = s.getFullName();
				}
				if (b[3] == null) {
					agent = "";
				} else {
					Agent a = (Agent) b[3];
					agent = a.getFullName();
				}
				if (b[5] == null) {
					Customer c = (Customer) b[4];
					customer = c.getFullName();
				} else {
					Organization org = (Organization) b[5];
					customer = org.getName();
				}
				branch = (String) b[6];
				premium = (Double) b[7];
				sumInsured = (Double) b[8];
				paymenttype = (PaymentType) b[9];
				commenmanceDate = (Date) b[10];
				proposalNo = (String) b[11];
				if (resultMap.containsKey(policyNo)) {
					premium += resultMap.get(policyNo).getPremium();
					sumInsured += resultMap.get(policyNo).getSumInsured();
					resultMap.put(policyNo, new MPLC001(id, policyNo, proposalNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, commenmanceDate));
				} else {
					resultMap.put(policyNo, new MPLC001(id, policyNo, proposalNo, saleMan, agent, customer, branch, premium, sumInsured, paymenttype, commenmanceDate));
				}
			}
			em.flush();

			logger.debug("find() method has been successfully finished.");
		} catch (PersistenceException pe) {
			throw translate("Failed to find MotorPolicy by MPLC001 : ", pe);
		}
		RegNoSorter<MPLC001> regNoSorter = new RegNoSorter<MPLC001>(new ArrayList<MPLC001>(resultMap.values()));
		return regNoSorter.getSortedList();
	}
}
