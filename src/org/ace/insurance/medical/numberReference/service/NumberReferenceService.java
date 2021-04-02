package org.ace.insurance.medical.numberReference.service;

import javax.annotation.Resource;

import org.ace.insurance.medical.numberReference.persistence.interfaces.INumberReferenceDAO;
import org.ace.insurance.medical.numberReference.service.interfaces.INumberReferenceService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "NumberReferenceService")
public class NumberReferenceService extends BaseService implements INumberReferenceService {

	@Resource(name = "NumberReferenceDAO")
	private INumberReferenceDAO numberReferenceDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String findNewNumberReferenceByOldNumber(String oldNumber) throws SystemException {
		try {
			return numberReferenceDAO.findNewNumberReferenceByOldNumber(oldNumber);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find new number", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String findNewNumberReferenceByNewNumber(String newNumber) throws SystemException {
		try {
			return numberReferenceDAO.findNewNumberReferenceByNewNumber(newNumber);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find new number", e);
		}
	}

}
