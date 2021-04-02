package org.ace.insurance.travel.personTravel.proposal.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Name;
import org.ace.insurance.common.Utils;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.persistence.interfaces.IPersonTravelProposalDAO;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository(value = "PersonTravelProposalDAO")
public class PersonTravelProposalDAO extends BasicDAO implements IPersonTravelProposalDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(PersonTravelProposal personTravelProposal) throws DAOException {
		try {
			em.persist(personTravelProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert PersonTravelInfo", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(PersonTravelProposal personTravelProposal) throws DAOException {
		try {
			em.merge(personTravelProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update PersonTravelInfo", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(PersonTravelProposal personTravelProposal) throws DAOException {
		try {
			personTravelProposal = em.merge(personTravelProposal);
			em.remove(personTravelProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete PersonTravelProposal", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelProposal> findAll() throws DAOException {
		List<PersonTravelProposal> resultList = null;
		try {
			Query q = em.createNamedQuery("PersonTravelProposal.findAll");
			resultList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All PersonTravelProposal", pe);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelProposal findById(String id) throws DAOException {
		PersonTravelProposal result = null;
		try {
			result = em.find(PersonTravelProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelProposal By Id", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStatus(String status, String id) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PTPL001> findByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {
		List<PTPL001> results = new ArrayList<PTPL001>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT p.id,p.proposalNo,s.name, a.name, r.name,c.name, o.name,"
					+ "p.branch.name,p.personTravelInfo.premium,p.personTravelInfo.totalUnit,p.personTravelInfo.noOfPassenger,p.paymentType.name,p.submittedDate");
			buffer.append(" FROM PersonTravelProposal p ");
			buffer.append(" LEFT OUTER JOIN p.saleMan s ");
			buffer.append(" LEFT OUTER JOIN p.agent a ");
			buffer.append(" LEFT OUTER JOIN p.referral r ");
			buffer.append(" LEFT OUTER JOIN p.customer c ");
			buffer.append(" LEFT OUTER JOIN p.organization o ");
			buffer.append(" WHERE p.proposalNo IS NOT NULL");

			if (criteria.getStartDate() != null) {
				buffer.append(" AND p.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				buffer.append(" AND p.submittedDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				buffer.append(" AND p.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				buffer.append(" AND p.organization.id = :organizationId");
			}
			if (criteria.getSaleMan() != null) {
				buffer.append(" AND p.saleMan.id = :saleManId");
			}
			if (criteria.getBranch() != null) {
				buffer.append(" AND p.branch.id = :branchId");
			}
			if (criteria.getProduct() != null) {
				buffer.append(" AND p.product.id = :productId");
			}
			if (!criteria.getNumber().isEmpty()) {
				buffer.append(" AND p.proposalNo = :proposalNo");
			}
			Query query = em.createQuery(buffer.toString());
			/* Executed query */
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getSaleMan() != null) {
				query.setParameter("saleManId", criteria.getSaleMan().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			}
			if (!criteria.getNumber().isEmpty()) {
				query.setParameter("proposalNo", criteria.getNumber());
			}
			@SuppressWarnings("unchecked")
			List<Object[]> objectList = query.getResultList();
			String id;
			String proposalNo;
			String salePersonName;
			String customerName;
			String branch;
			Double premium;
			int totalUnit;
			int noOfPassenger;
			String paymentType;
			Date submittedDate;
			for (Object[] objArr : objectList) {
				id = (String) objArr[0];
				proposalNo = (String) objArr[1];
				if (objArr[3] == null) {
					SaleMan s = new SaleMan();
					s.setName((Name) objArr[2]);
					salePersonName = s.getFullName();
				} else {
					Agent a = new Agent();
					a.setName((Name) objArr[3]);

					salePersonName = a.getFullName();
				}
				if (objArr[5] == null) {
					Customer c = new Customer();
					c.setName((Name) objArr[4]);
					customerName = c.getFullName();
				} else {
					// organization
					customerName = ((Name) objArr[5]).getFirstName();
				}
				branch = (String) objArr[7];
				premium = (Double) objArr[8];
				totalUnit = (int) objArr[9];
				noOfPassenger = (int) objArr[10];
				paymentType = (String) objArr[11];
				submittedDate = (Date) objArr[12];
				results.add(new PTPL001(id, proposalNo, salePersonName, customerName, branch, premium, totalUnit, noOfPassenger, paymentType, submittedDate));
			}
			// results = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelProposal By Criteria", pe);
		}
		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("PersonTravelProposal.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}
	}
}
