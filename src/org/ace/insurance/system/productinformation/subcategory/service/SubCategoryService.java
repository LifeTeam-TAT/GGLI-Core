package org.ace.insurance.system.productinformation.subcategory.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.insurance.system.productinformation.subcategory.persistence.interfaces.ISubCategoryDAO;
import org.ace.insurance.system.productinformation.subcategory.service.interfaces.ISubCategoryService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "SubCategoryService")
public class SubCategoryService extends BaseService implements ISubCategoryService {

	@Resource(name = "SubCategoryDAO")
	private ISubCategoryDAO subCategoryDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewSubCategory(SubCategory subCategory) {
		try {
			subCategoryDAO.insert(subCategory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new SubCategory", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateSubCategory(SubCategory subCategory) {
		try {
			subCategoryDAO.update(subCategory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update SubCategory", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteSubCategory(SubCategory subCategory) {
		try {
			subCategoryDAO.delete(subCategory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a SubCategory", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public SubCategory findSubCategoryById(String id) {
		SubCategory result = null;
		try {
			result = subCategoryDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a SubCategory (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<SubCategory> findAllSubCategory() {
		List<SubCategory> result = null;
		try {
			result = subCategoryDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SubCategory)", e);
		}
		return result;
	}

}
