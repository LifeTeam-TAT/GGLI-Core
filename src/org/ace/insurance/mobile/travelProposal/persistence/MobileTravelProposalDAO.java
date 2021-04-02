package org.ace.insurance.mobile.travelProposal.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.mobile.travelProposal.MTP002;
import org.ace.insurance.mobile.travelProposal.MobileTravelProposal;
import org.ace.insurance.mobile.travelProposal.TravelProposalCriteria;
import org.ace.insurance.mobile.travelProposal.persistence.interfaces.IMobileTravelProposalDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("MobileTravelProposalDAO")
public class MobileTravelProposalDAO extends BasicDAO implements IMobileTravelProposalDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public MobileTravelProposal update(MobileTravelProposal mobileTravelProposal) throws DAOException {
		try {
			mobileTravelProposal = em.merge(mobileTravelProposal);
			updateProcessLog(TableName.PAYMENT, mobileTravelProposal.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MobileTravelProposal", pe);
		}
		return mobileTravelProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MTP002> findByCriteria(TravelProposalCriteria tProposalCriteria) throws DAOException {
		List<MTP002> result = new ArrayList<MTP002>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT u.id,");
			buffer.append(" CONCAT(TRIM(u.firstName), ' ',TRIM(u.lastName)),");
			buffer.append(" t.policyNo,SUM(p.premium),t.paymentDate,t.id");
			buffer.append(" FROM MobileTravelProposal t LEFT JOIN t.insuredPersonList p JOIN MobileUser u ON u.id = t.userId");
			buffer.append(" WHERE t.paymentDate IS NOT NULL AND t.isConvert IS NULL");

			if (tProposalCriteria.getStartDate() != null) {
				buffer.append(" AND t.paymentDate >= :startDate");
			}
			if (tProposalCriteria.getEndDate() != null) {
				buffer.append(" AND t.paymentDate <= :endDate");
			}
			buffer.append(" GROUP BY u.id,u.firstName,u.lastName,t.policyNo,t.paymentDate,t.id");

			Query query = em.createQuery(buffer.toString());

			if (tProposalCriteria.getStartDate() != null) {
				query.setParameter("startDate", DateUtils.resetStartDate(tProposalCriteria.getStartDate()));
			}
			if (tProposalCriteria.getEndDate() != null) {
				query.setParameter("endDate", DateUtils.resetEndDate(tProposalCriteria.getEndDate()));
			}

			List<Object[]> objectList = query.getResultList();

			String mobileUserId = null;
			String mobileUserName = null;
			String policyNo = null;
			double premium;
			Date paymentDate;
			String travelProposalId;
			for (Object[] b : objectList) {
				mobileUserId = (String) b[0];
				mobileUserName = (String) b[1];
				policyNo = (String) b[2];
				premium = (double) b[3];
				paymentDate = (Date) b[4];
				travelProposalId = (String) b[5];
				result.add(new MTP002(mobileUserId, mobileUserName, policyNo, premium, paymentDate, travelProposalId));
			}
			em.flush();

		} catch (PersistenceException e) {
			throw translate("Failed to find Mobile Travel Proposal By Criteria", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public MobileTravelProposal findById(String id) throws DAOException {
		MobileTravelProposal result = null;
		try {
			Query q = em.createNamedQuery("MobileTravelProposal.findById");
			q.setParameter("id", id);
			result = (MobileTravelProposal) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find MobileTravelProposal by mobileUser", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MobileTravelProposal findByPolicyNo(String policyNo) throws DAOException {
		MobileTravelProposal result = null;
		try {
			Query q = em.createNamedQuery("MobileTravelProposal.findByPolicyNo");
			q.setParameter("policyNo", policyNo);
			result = (MobileTravelProposal) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find MobileTravelProposal By ProposalNo" + policyNo, pe);
		}
		return result;
	}

	// buffer.append("SELECT NEW
	// m.id,m.productId,m.policyNo,m.paymentDate,m.paymentType,m.userId,");
	// buffer.append(" p.departureDate,p.arrivalDate,p.route,p.unit,p.premium");
	// buffer.append(" FROM MobileTravelProposal m LEFT JOIN l.insuredPersonList
	// p");
	// buffer.append(" WHERE m.paymentDate IS NOT NULL AND m.isConvert IS NOT
	// NULL");

}
