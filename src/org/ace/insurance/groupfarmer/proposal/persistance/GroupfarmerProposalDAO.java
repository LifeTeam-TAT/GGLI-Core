package org.ace.insurance.groupfarmer.proposal.persistance;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalDTO;
import org.ace.insurance.groupfarmer.proposal.persistance.interfaces.IGroupfarmerProposalDAO;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("GroupfarmerProposalDAO")
public class GroupfarmerProposalDAO extends BasicDAO implements IGroupfarmerProposalDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public GroupFarmerProposal insert(GroupFarmerProposal groupFarmerProposal) throws DAOException {
		try {
			em.persist(groupFarmerProposal);
			insertProcessLog(TableName.GROUPFARMERPROPOSAL, groupFarmerProposal.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert GroupFarmerProposal", pe);
		}
		return groupFarmerProposal;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(GroupFarmerProposal groupFarmerProposal) throws DAOException {
		try {
			em.merge(groupFarmerProposal);
			insertProcessLog(TableName.GROUPFARMERPROPOSAL, groupFarmerProposal.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update GroupFarmerProposal", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(GroupFarmerProposal groupFarmerProposal) throws DAOException {
		try {
			groupFarmerProposal = em.merge(groupFarmerProposal);
			em.remove(groupFarmerProposal);
			insertProcessLog(TableName.GROUPFARMERPROPOSAL, groupFarmerProposal.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete GroupFarmerProposal", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public GroupFarmerProposal findById(String id) throws DAOException {
		GroupFarmerProposal result = null;
		try {
			result = em.find(GroupFarmerProposal.class, id);
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find GroupFarmerProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupFarmerProposalDTO> findAllGroupFarmerProposalByisPaymentComplete(EnquiryCriteria criteria) throws DAOException {
		List<GroupFarmerProposalDTO> result = null;
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT NEW " + GroupFarmerProposalDTO.class.getName());
			queryString.append(" (p) FROM GroupFarmerProposal p where p.isPaymentComplete = 1 and p.isProcessComplete = 0");
			if (!criteria.getNumber().isEmpty()) {
				queryString.append(" AND p.proposalNo= :proposalNo");
			}

			if (criteria.getStartDate() != null) {
				queryString.append(" AND p.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND p.submittedDate <= :endDate");
			}
			Query q = em.createQuery(queryString.toString());
			if (!criteria.getNumber().isEmpty()) {
				q.setParameter("proposalNo", criteria.getNumber());
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				q.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				q.setParameter("endDate", criteria.getEndDate());
			}
			result = q.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find All GroupFarmerProposal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupFarmerProposalDTO> findGroupFarmerProposalByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {

		List<GroupFarmerProposalDTO> resultList = null;
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT NEW " + GroupFarmerProposalDTO.class.getName());
			queryString.append("(p) FROM GroupFarmerProposal p WHERE p.proposalNo is not null ");
			if (criteria.getStartDate() != null) {
				queryString.append(" AND p.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND p.submittedDate <= :endDate");
			}
			if (!criteria.getNumber().isEmpty()) {
				queryString.append(" AND p.proposalNo= :proposalNo");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND p.branch.id = :branchId");
			}
			queryString.append(" ORDER BY p.id ASC ");
			/* Executed query */
			Query query = em.createQuery(queryString.toString());

			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (!criteria.getNumber().isEmpty()) {
				query.setParameter("proposalNo", criteria.getNumber());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}

			resultList = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All GroupFarmer proposal", pe);
		}
		return resultList;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupFarmerProposal> finGroupFarmerProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo) throws DAOException {
		List<GroupFarmerProposal> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT g FROM GroupFarmerProposal g, Payment p, TLF t WHERE g.id = p.referenceNo ");
			query.append("AND g.id = t.referenceNo AND p.isPO = TRUE AND t.clearing = TRUE AND t.paid = FALSE ");
			if (!StringUtils.isBlank(receiptNo)) {
				query.append("AND t.tlfNo = :receiptNo ");
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				query.append("AND t.bpmsReceiptNo = :bpmsReceiptNo ");
			}
			Query q = em.createQuery(query.toString());
			if (!StringUtils.isBlank(receiptNo)) {
				q.setParameter("receiptNo", receiptNo);
			}
			if (!StringUtils.isBlank(bpmsReceiptNo)) {
				q.setParameter("bpmsReceiptNo", bpmsReceiptNo);
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelPolicy PaymentOrder", pe);
		}
		return result;
	}
}
