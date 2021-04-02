package org.ace.insurance.claim.disabilityPart.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.ProductDisability;
import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;
import org.ace.insurance.claim.disabilityPart.persistence.interfaces.IProductDisabilityDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ProductDisabilityDAO")
public class ProductDisabilityDAO extends BasicDAO implements IProductDisabilityDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(ProductDisability productDisability) throws DAOException {
		try {
			em.persist(productDisability);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Product Disability", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProductDisability update(ProductDisability productDisability) throws DAOException {
		try {
			em.merge(productDisability);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Product Disability", pe);
		}
		return productDisability;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ProductDisability productDisability) throws DAOException {
		try {
			productDisability = em.merge(productDisability);
			em.remove(productDisability);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Product Disability", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(ProductDisabilityRate productDisabilityRate) throws DAOException {
		try {
			em.persist(productDisabilityRate);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Product Disability Rate", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ProductDisabilityRate productDisabilityRate) throws DAOException {
		try {
			productDisabilityRate = em.merge(productDisabilityRate);
			em.remove(productDisabilityRate);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Product Disability Rate", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductDisability> findAll() throws DAOException {
		List<ProductDisability> result = null;
		try {
			Query q = em.createNamedQuery("ProductDisability.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product Disability", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<DisabilityPart> findAllDisabilityPartByProduct(String productId) throws DAOException {
		List<DisabilityPart> result = null;
		try {
			Query q = em.createNamedQuery("ProductDisability.findDisabilityPartByProductId");
			q.setParameter("productId", productId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product Disability", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductDisabilityRate> findAllDisabilityRateByProduct(String productId) throws DAOException {
		List<ProductDisabilityRate> result = null;
		try {
			Query q = em.createNamedQuery("ProductDisability.findDisabilityRateByProductId");
			q.setParameter("productId", productId);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Disability Rate", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findDisabilityRateById(String id) throws DAOException {
		double rate = 0.0;
		try {
			Query query = em.createNamedQuery("ProductDisabilityRate.findRateById");
			query.setParameter("id", id);
			rate = (double) query.getSingleResult();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Disability Rate", pe);
		}
		return rate;
	}
}
