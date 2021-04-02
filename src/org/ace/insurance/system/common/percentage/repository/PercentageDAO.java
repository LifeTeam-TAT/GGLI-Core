package org.ace.insurance.system.common.percentage.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.dao.interfaces.IPercentageDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("PercentageDAO")
public class PercentageDAO extends BasicDAO implements IPercentageDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Percentage percentage) {
		em.persist(percentage);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Percentage percentage) {
		try {
			percentage = em.merge(percentage);
			em.remove(percentage);
		} catch (PersistenceException e) {
			throw translate("Failed to delete organization User(percentage = " + percentage.getPercent() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Percentage percentage) {
		try {
			em.merge(percentage);
		} catch (PersistenceException e) {
			throw translate("Failed to update organization User(Percentage = " + percentage.getPercent() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Percentage> findAllPercentage() {
		List<Percentage> result = null;
		try {
			Query q = em.createQuery("select e from Percentage e ");
			result = q.getResultList();
		} catch (NoResultException pe) {
			return null;

		} catch (PersistenceException e) {
			throw translate("Failed to  find allss Percentage ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Percentage findPercentageWithRelationShip(String typeId, String productId) {
		try {
			TypedQuery<Percentage> q = em.createNamedQuery("Percentage.findPercentWithRelationShip", Percentage.class);
			q.setParameter("typeId", typeId);
			q.setParameter("productId", productId);
			return q.getSingleResult();
		} 
		catch (NoResultException pe) {
			return null;
		}catch (PersistenceException e) {
			throw translate("Failed to  find allss Percentage ", e);
		}
	}

}
