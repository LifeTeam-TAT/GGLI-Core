package org.ace.insurance.life.migrate.persistence;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.life.migrate.ShortEndowmentExtraValue;
import org.ace.insurance.life.migrate.persistence.interfaces.IShortEndowmentExtraValueDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ShortEndowmentExtraValueDAO")
public class ShortEndowmentExtraValueDAO extends BasicDAO implements IShortEndowmentExtraValueDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(ShortEndowmentExtraValue shortEndowmentExtraValue) {
		try {
			em.persist(shortEndowmentExtraValue);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert ShortEndowmentExtraValue", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateShortTermPolicyNo(String id, String policyNo) {
		try {
			StringBuilder query = new StringBuilder();
			query.append("Update ShortEndowmentExtraValue s set s.shortTermPolicyNo=:policyNo where s.referenceNo=:id");
			Query q = em.createQuery(query.toString());
			q.setParameter("policyNo", policyNo);
			q.setParameter("id", id);
			q.executeUpdate();
		} catch (PersistenceException pe) {
			throw translate("Failed to update ShortTermPolicyNo", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double getExtraAmountByReferencenNo(String referenceNo) {
		double result = 0.0;
		try {
			Query q = em.createQuery("SELECT e.extraAmount FROM ShortEndowmentExtraValue e WHERE e.referenceNo = :referenceNo");
			q.setParameter("referenceNo", referenceNo);
			result = (double) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update ShortTermPolicyNo", pe);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ShortEndowmentExtraValue getShortEndowmentExtraValueByPolicyNo(String shortTermPolicyNo) {
		ShortEndowmentExtraValue result = null;
		try {
			Query q = em.createQuery("SELECT se FROM ShortEndowmentExtraValue se WHERE se.shortTermPolicyNo = :shortTermPolicyNo");
			q.setParameter("shortTermPolicyNo", shortTermPolicyNo);
			result = (ShortEndowmentExtraValue) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find ShortEndowmentExtraValue", pe);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateShortEndowmentExtraValue(ShortEndowmentExtraValue extraValue) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE ShortEndowmentExtraValue se SET se.isPaid = :isPaid WHERE se.shortTermPolicyNo = :shortTermPolicyNo ");
			q.setParameter("isPaid", extraValue.isPaid());
			q.setParameter("shortTermPolicyNo", extraValue.getShortTermPolicyNo());
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update ShortEndowmentExtraValue", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ShortEndowmentExtraValue findShortEndowmentExtraValue(String referenceNo) throws DAOException {
		ShortEndowmentExtraValue result = null;
		try {
			Query q = em.createQuery("SELECT se FROM  ShortEndowmentExtraValue se WHERE se.referenceNo = :referenceNo");
			q.setParameter("referenceNo", referenceNo);
			result = (ShortEndowmentExtraValue) q.getSingleResult();
			em.flush();
		} catch (NoResultException nre) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to update ShortEndowmentExtraValue", pe);
		}
		return result;
	}
}
