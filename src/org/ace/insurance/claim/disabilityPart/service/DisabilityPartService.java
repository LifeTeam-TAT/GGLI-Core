package org.ace.insurance.claim.disabilityPart.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.persistence.interfaces.IDisabilityPartDAO;
import org.ace.insurance.claim.disabilityPart.service.interfaces.IDisabilityPartService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "DisabilityPartService")
public class DisabilityPartService extends BaseService implements IDisabilityPartService {

	@Resource(name = "DisabilityPartDAO")
	private IDisabilityPartDAO disabilityPartDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addNewDisabilityPart(DisabilityPart disabilityPart) {
		try {
			disabilityPartDAO.insert(disabilityPart);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add a new DisabilityPart", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateDisabilityPart(DisabilityPart disabilityPart) {
		try {
			disabilityPartDAO.update(disabilityPart);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update a Disability Part", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteDisabilityPart(DisabilityPart disabilityPart) {
		try {
			disabilityPartDAO.delete(disabilityPart);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete a Disability Part", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<DisabilityPart> findAllDisabilityPart() {
		List<DisabilityPart> result = null;
		try {
			result = disabilityPartDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of Disability Part)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public DisabilityPart findDisabilityPartById(String id) {
		DisabilityPart result = null;
		try {
			result = disabilityPartDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a DisabilityPart (ID : " + id + ")", e);
		}
		return result;
	}
}
