package org.ace.insurance.system.productinformation.text.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.productinformation.text.NameText;
import org.ace.insurance.system.productinformation.text.persistence.interfaces.INameTextDAO;
import org.ace.insurance.system.productinformation.text.service.interfaces.INameTextService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "NameTextService")
public class NameTextService extends BaseService implements INameTextService {

	@Resource(name = "NameTextDAO")
	private INameTextDAO nameTextDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewNameText(NameText nameText) {
		try {
			nameTextDAO.insert(nameText);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new NameText", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateNameText(NameText nameText) {
		try {
			nameTextDAO.update(nameText);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update NameText", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteNameText(NameText nameText) {
		try {
			nameTextDAO.delete(nameText);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a NameText", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public NameText findNameTextById(String id) {
		NameText result = null;
		try {
			result = nameTextDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a NameText (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<NameText> findAllNameText() {
		List<NameText> result = null;
		try {
			result = nameTextDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of NameText)", e);
		}
		return result;
	}

}
