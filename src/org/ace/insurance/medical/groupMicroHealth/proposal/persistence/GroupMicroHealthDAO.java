package org.ace.insurance.medical.groupMicroHealth.proposal.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.Utils;
import org.ace.insurance.medical.groupMicroHealth.proposal.GroupMicroHealth;
import org.ace.insurance.medical.groupMicroHealth.proposal.persistence.interfaces.IGroupMicroHealthDAO;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.enquires.medical.groupMicroHealth.GroupMicroHealthCriteria;
import org.ace.insurance.web.manage.medical.groupMicroHealth.policy.GroupMicroHealthDTO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("GroupMicroHealthDAO")
public class GroupMicroHealthDAO extends BasicDAO implements IGroupMicroHealthDAO {
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public GroupMicroHealth create(GroupMicroHealth groupMicroHealth) throws DAOException {
		try {
			em.persist(groupMicroHealth);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Fail To Insert Group Micro Health", e);
		}
		return groupMicroHealth;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(GroupMicroHealth groupMicroHealth) throws DAOException {
		try {
			em.merge(groupMicroHealth);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Fail To Update Group Micro Health", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(GroupMicroHealth groupMicroHealth) throws DAOException {
		try {
			groupMicroHealth = em.merge(groupMicroHealth);
			em.remove(groupMicroHealth);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Fail To delete Group Micro Health", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupMicroHealth> findAll() throws DAOException {
		try {
			TypedQuery<GroupMicroHealth> query = em.createNamedQuery("GroupMicroHealth.findAll", GroupMicroHealth.class);
			return query.getResultList();
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException e) {
			throw translate("Fail To FindAll Group Micro Health", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public GroupMicroHealth findById(String id) throws DAOException {
		GroupMicroHealth groupMicroHealth = null;
		try {
			groupMicroHealth = em.find(GroupMicroHealth.class, id);
		} catch (PersistenceException e) {
			throw translate("Fail To FindAll Group Micro Health", e);
		}
		return groupMicroHealth;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupMicroHealthDTO> findAllPaymentCompleteDTO(GroupMicroHealthCriteria criteria) throws DAOException {
		List<GroupMicroHealthDTO> result = null;
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT NEW " + GroupMicroHealthDTO.class.getName());
			queryString.append(
					" (g.proposalNo,g.id,g.startDate,g.numberOfInsuredPerson,g.numberOfUnit,g.totalPremium,g.agent,g.saleMan,g.referral,g.salePoint,g.paymentComplete) from GroupMicroHealth g WHERE g.paymentComplete = 'true' and g.processComplete ='0' ");

			if (!criteria.getProposalNo().isEmpty()) {
				queryString.append(" AND g.proposalNo= :proposalNo");
			}

			if (criteria.getStartDate() != null) {
				queryString.append(" AND g.startDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND g.startDate <= :endDate");
			}

			TypedQuery<GroupMicroHealthDTO> query = em.createQuery(queryString.toString(), GroupMicroHealthDTO.class);

			if (!criteria.getProposalNo().isEmpty()) {
				query.setParameter("proposalNo", criteria.getProposalNo());
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			result = query.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException e) {
			throw translate("Fail To FindAll Group Micro Health", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupMicroHealthDTO> findALLProcessCompleteDTO(EnquiryCriteria criteria) throws DAOException {
		List<GroupMicroHealthDTO> result = null;
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT NEW " + GroupMicroHealthDTO.class.getName());
			queryString.append(" (g) from GroupMicroHealth g WHERE g.proposalNo is not null");

			if (!criteria.getNumber().isEmpty()) {
				queryString.append(" AND g.proposalNo >= :proposalNo");
			}

			if (null != criteria.getStartDate()) {
				queryString.append(" AND g.startDate >= :startDate");
			}
			if (null != criteria.getEndDate()) {
				queryString.append(" AND g.startDate <= :endDate");
			}
			if (null != criteria.getAgent()) {
				queryString.append(" AND g.agent.id = :agentId");
			}
			if (null != criteria.getSaleMan()) {
				queryString.append(" AND g.saleMan.id = :saleManId");
			}
			if (null != criteria.getReferral()) {
				queryString.append(" AND g.referral.id = :referralId");
			}
			if (null != criteria.getBranch()) {
				queryString.append(" AND g.branch.id = :branchId");
			}
			if (null != criteria.getSalePoint()) {
				queryString.append(" AND g.salePoint.id = :salePointId");
			}
			Query query = em.createQuery(queryString.toString());

			if (!criteria.getNumber().isEmpty()) {
				query.setParameter("proposalNo", criteria.getNumber());
			}
			if (null != criteria.getStartDate()) {
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (null != criteria.getEndDate()) {
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (null != criteria.getAgent()) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (null != criteria.getSaleMan()) {
				query.setParameter("saleManId", criteria.getSaleMan().getId());
			}
			if (null != criteria.getReferral()) {
				query.setParameter("referralId", criteria.getReferral().getId());
			}
			if (null != criteria.getBranch()) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (null != criteria.getSalePoint()) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}

			result = query.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Fail To FindAll Group Micro Health", pe);
		}
		return result;
	}
}
