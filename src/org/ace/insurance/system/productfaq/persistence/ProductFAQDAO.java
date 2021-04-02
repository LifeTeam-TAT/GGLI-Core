package org.ace.insurance.system.productfaq.persistence;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.productfaq.ProductFAQ;
import org.ace.insurance.system.productfaq.persistence.interfaces.IProductFAQDAO;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.BasicDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository(value = "ProductFAQDAO")
public class ProductFAQDAO extends BasicDAO implements IProductFAQDAO {
	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(ProductFAQ productFAQ) {
		try {
			em.persist(productFAQ);
			// insertProcessLog(TableName.PRODUCTFAQ, productFAQ.getId());
			em.flush();
		} catch (PersistenceException pe) {
			translate("insert productFAQ ", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ProductFAQ update(ProductFAQ productFAQ) {
		try {
			productFAQ = em.merge(productFAQ);
			// updateProcessLog(TableName.PRODUCTFAQ, productFAQ.getId());
			em.flush();
		} catch (PersistenceException pe) {
			translate("update productFAQ ", pe);
		}
		return productFAQ;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ProductFAQ findById(String id) {
		ProductFAQ result = null;
		try {
			Query q = em.createNamedQuery("ProductFAQ.findById");
			q.setParameter("id", id);
			result = (ProductFAQ) q.getSingleResult();
			em.flush();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException pe) {
			translate("fail to find ProductFAQ by Id : " + id, pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductFAQ> findAllProductFAQ() {
		List<ProductFAQ> productFAQList = null;
		try {
			Query q = em.createNamedQuery("ProductFAQ.findAll");
			productFAQList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			translate("Fail to find all ProductFAQ ", pe);
		}
		return productFAQList;
	}

}
