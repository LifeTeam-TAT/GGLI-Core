package org.ace.insurance.system.productinformation.subcategory.persistence;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.insurance.system.productinformation.subcategory.persistence.interfaces.ISubCategoryDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("SubCategoryDAO")
public class SubCategoryDAO extends BasicDAO implements ISubCategoryDAO{

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(SubCategory subCategory) throws DAOException {
		try {
			em.persist(subCategory);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert SubCategory", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SubCategory update(SubCategory subCategory) throws DAOException {
		try {
			subCategory = em.merge(subCategory);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SubCategory", pe);
		}
		return subCategory;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(SubCategory subCategory) throws DAOException {
		try {
			subCategory = em.merge(subCategory);
			em.remove(subCategory);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update SubCategory", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public SubCategory findById(String id) throws DAOException {
		SubCategory result = null;
		try {
			result = em.find(SubCategory.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find SubCategory", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SubCategory> findAll() throws DAOException {
		List<SubCategory> result = null;
		try {
			Query q = em.createNamedQuery("SubCategory.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of SubCategory", pe);
		}
		return result;
	}

}
