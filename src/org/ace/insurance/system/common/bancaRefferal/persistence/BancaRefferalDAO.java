package org.ace.insurance.system.common.bancaRefferal.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.bancaRefferal.persistence.interfaces.IBancaRefferalDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaRefferalDAO")
public class BancaRefferalDAO extends BasicDAO implements IBancaRefferalDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaRefferal bancarefferal) throws DAOException {
		try {
			if (!checkExistingBancaRefferal(bancarefferal)) {
				em.persist(bancarefferal);
			} else {
				throw new SystemException(null, bancarefferal.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert channel (bancarefferalname = " + bancarefferal.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaRefferal bancarefferal) throws DAOException {
		try {
			bancarefferal = em.merge(bancarefferal);
			em.remove(bancarefferal);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete bancarefferal User(bancarefferal name = " + bancarefferal.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaRefferal bancarefferal) throws DAOException {
		try {
			em.merge(bancarefferal);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update bancarefferal User(bancarefferal name = " + bancarefferal.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaRefferal findById(String BancarefferalId) throws DAOException {
		BancaRefferal result = null;
		try {
			result = em.find(BancaRefferal.class, BancarefferalId);
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Bancarefferal", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancarefferal() {
		List<BancaRefferal> result = null;
		try {
			Query q = em.createQuery("select b from BancaRefferal b");
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all BancaRefferal ", e);
		}
		return result;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancaRefferalByAgentLicenseNo() throws DAOException {
		List<BancaRefferal> result = null;
		try {
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			TypedQuery<BancaRefferal> query = em.createQuery(
					"select br from BancaRefferal br where br.status = org.ace.insurance.common.utils.BancaStatus.ACTIVE and br.licenseNo IS NOT NULL"
							+ " and br.licenseExpireDate >= :today",
					BancaRefferal.class);
			query.setParameter("today", today, TemporalType.DATE);
			result = query.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all BancaRefferalByOTC ", e);
		}
		return result;
	}

	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancarefferalActive(String branchId) throws DAOException {
		List<BancaRefferal> result = null;
		try {
			Query q = em.createQuery("select br from BancaRefferal br where br.bancaBranch.id =:branchId and br.status = org.ace.insurance.common.utils.BancaStatus.ACTIVE");
			q.setParameter("branchId", branchId);
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all BancaRefferal ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaRefferal> findAllBancarefferalByOTC(String branchId) throws DAOException {
		List<BancaRefferal> result = null;
		try {
			/*
			 * TypedQuery<BancaRefferal> q = em.
			 * createQuery("select b from BancaRefferal b WHERE b.licenseNo IS NOT NULL AND b.status"
			 * + "= org.ace.insurance.common.utils.BancaStatus.ACTIVE"
			 * ,BancaRefferal.class); result = q.getResultList();
			 */
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			TypedQuery<BancaRefferal> query = em.createQuery(
					"select br from BancaRefferal br where br.bancaBranch.id =:branchId and br.status = org.ace.insurance.common.utils.BancaStatus.ACTIVE and br.licenseNo IS NOT NULL"
							+ " and br.licenseExpireDate >= :today",
					BancaRefferal.class);
			query.setParameter("branchId", branchId);
			query.setParameter("today", today, TemporalType.DATE);
			result = query.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all BancaRefferalByOTC ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaRefferal(BancaRefferal bancarefferal) throws DAOException {
		boolean exist = false;
		String bancaName = bancarefferal.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM BancaRefferal b ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
			buffer.append(bancarefferal.getId() != null ? "AND b.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (bancarefferal.getId() != null)
				query.setParameter("id", bancarefferal.getId());
			query.setParameter("name", bancaName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM BancaRefferal b");
				buffer.append(" WHERE b.id != :id");
				buffer.append(" AND LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", bancarefferal.getId());
				query.setParameter("name", bancarefferal.getName());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
