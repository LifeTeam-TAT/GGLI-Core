package org.ace.insurance.life.migrate.service;

import javax.annotation.Resource;

import org.ace.insurance.life.migrate.ShortEndowmentExtraValue;
import org.ace.insurance.life.migrate.persistence.interfaces.IShortEndowmentExtraValueDAO;
import org.ace.insurance.life.migrate.service.interfaces.IShortEndowmentExtraValueService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "ShortEndowmentExtraValueService")
public class ShortEndowmentExtraValueService extends BaseService implements IShortEndowmentExtraValueService {

	@Resource(name = "ShortEndowmentExtraValueDAO")
	private IShortEndowmentExtraValueDAO shortEndowmentExtraValueDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewShortEndowmentExtraValue(ShortEndowmentExtraValue shortEndowmentExtraValue) {
		try {
			shortEndowmentExtraValue.setPrefix(getPrefix(ShortEndowmentExtraValue.class));
			shortEndowmentExtraValueDAO.insert(shortEndowmentExtraValue);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add New ShortEndowmentExtraValue", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findExtraAmount(String id) {
		double result = 0;
		try {
			result = shortEndowmentExtraValueDAO.getExtraAmountByReferencenNo(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find ExtraAmount " + e);
		}
		return result;
	}

	public ShortEndowmentExtraValue findShortEndowmentExtraValueByPolicyNo(String shortTermPolicyNo) {
		ShortEndowmentExtraValue result = null;
		try {
			result = shortEndowmentExtraValueDAO.getShortEndowmentExtraValueByPolicyNo(shortTermPolicyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find ShortEndowmentExtraValue " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateShortEndowmentExtraValue(ShortEndowmentExtraValue extraValue) {
		try {
			shortEndowmentExtraValueDAO.updateShortEndowmentExtraValue(extraValue);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update ShortEndowmentExtraValue.", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ShortEndowmentExtraValue findShortEndowmentExtraValue(String referenceNo) {
		ShortEndowmentExtraValue result = null;
		try {
			shortEndowmentExtraValueDAO.findShortEndowmentExtraValue(referenceNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update ShortEndowmentExtraValue.", e);
		}
		return result;
	}

}
