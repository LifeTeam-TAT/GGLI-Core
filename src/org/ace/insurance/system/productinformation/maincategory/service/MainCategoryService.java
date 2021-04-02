package org.ace.insurance.system.productinformation.maincategory.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.productinformation.maincategory.MainCategory;
import org.ace.insurance.system.productinformation.maincategory.persistence.interfaces.IMainCategoryDAO;
import org.ace.insurance.system.productinformation.maincategory.service.interfaces.IMainCategoryService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "MainCategoryService")
public class MainCategoryService extends BaseService implements IMainCategoryService {

	@Resource(name = "MainCategoryDAO")
	private IMainCategoryDAO mainCategoryDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MainCategory addNewMainCategory(MainCategory mainCategory) {
		try {
			mainCategoryDAO.insert(mainCategory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new MainCategory", e);
		}
		return mainCategory;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MainCategory updateMainCategory(MainCategory mainCategory) {
		MainCategory category = null;
		try {
			category = mainCategoryDAO.update(mainCategory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update MainCategory", e);
		}
		return category;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteMainCategory(MainCategory mainCategory) {
		try {
			mainCategoryDAO.delete(mainCategory);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a MainCategory", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MainCategory findMainCategoryById(String id) {
		MainCategory result = null;
		try {
			result = mainCategoryDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a MainCategory (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<MainCategory> findAllMainCategory() {
		List<MainCategory> result = null;
		try {
			result = mainCategoryDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of MainCategory)", e);
		}
		return result;
	}

}
