package org.ace.insurance.system.common.bancaLIC.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaLIC.persistence.interfaces.IBancaLICDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("BancaLICDAO")
public class BancaLICDAO extends BasicDAO implements IBancaLICDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(BancaLIC bancaLIC) throws DAOException {
		try {
			if (!checkExistingBancaLIC(bancaLIC)) {
				em.persist(bancaLIC);
			} else {
				throw new SystemException(null, bancaLIC.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert bancaLIC (bancaLICname = " + bancaLIC.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(BancaLIC bancaLIC) throws DAOException {
		try {
			bancaLIC = em.merge(bancaLIC);
			em.remove(bancaLIC);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete bancaLIC User(bancaLIC name = " + bancaLIC.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(BancaLIC bancaLIC) {
		try {
			em.merge(bancaLIC);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update bancaLIC User(bancaLICname = " + bancaLIC.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaLIC> findAllBancaLIC() throws DAOException {
		List<BancaLIC> result = null;
		try {
			Query q = em.createQuery("select b from BancaLIC b ");
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all bancaLIC ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<BancaLIC> findAllBancaLICActive() throws DAOException {
		List<BancaLIC> result = null;
		try {
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			Query q = em.createQuery("select b from BancaLIC b WHERE b.licenseNo IS NOT NULL AND b.licenseExpireDate >= :today AND "
					+ "b.status = org.ace.insurance.common.utils.BancaStatus.ACTIVE");
			q.setParameter("today", today, TemporalType.DATE);
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all bancaLIC ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingBancaLIC(BancaLIC bancaLIC) throws DAOException {
		boolean exist = false;
		String bancaLICName = bancaLIC.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.name) > 0) THEN TRUE ELSE FALSE END FROM BancaLIC b ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
			buffer.append(bancaLIC.getId() != null ? "AND b.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (bancaLIC.getId() != null)
				query.setParameter("id", bancaLIC.getId());
			query.setParameter("name", bancaLICName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(b.id) > 0) THEN TRUE ELSE FALSE END FROM BancaLIC b");
				buffer.append(" WHERE b.id != :id");
				buffer.append(" AND LOWER(FUNCTION('REPLACE',b.name,' ','')) = :name ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", bancaLIC.getId());
				query.setParameter("name", bancaLIC.getName());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BancaLIC findById(String BancaLICId) throws DAOException {
		BancaLIC result = null;
		try {
			result = em.find(BancaLIC.class, BancaLICId);
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find bancaLIC", pe);
		}
		return result;
	}

}
