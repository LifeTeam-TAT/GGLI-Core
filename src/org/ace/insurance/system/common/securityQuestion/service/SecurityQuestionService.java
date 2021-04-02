package org.ace.insurance.system.common.securityQuestion.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.securityQuestion.SecurityQuestion;
import org.ace.insurance.system.common.securityQuestion.persistence.interfaces.ISecurityQuestionDAO;
import org.ace.insurance.system.common.securityQuestion.service.interfaces.ISecurityQuestionService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("SecurityQuestionService")
public class SecurityQuestionService extends BaseService implements ISecurityQuestionService {

	@Resource(name = "SecurityQuestionDAO")
	private ISecurityQuestionDAO securityQuestionDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SecurityQuestion> findAllSecurityQuestionList() {
		List<SecurityQuestion> securityQuestionList;
		try {
			securityQuestionList = securityQuestionDAO.findAllSecurityQuestionList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find All SecurityQuestion", e);
		}
		return securityQuestionList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(String id) {
		try {
			securityQuestionDAO.delete(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete SecurityQuestion", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(SecurityQuestion securityQuestion) {
		try {
			securityQuestion.setPrefix(getPrefix(SecurityQuestion.class));
			securityQuestionDAO.insert(securityQuestion);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to insert SecurityQuestion", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SecurityQuestion securityQuestion) {
		try {
			securityQuestionDAO.update(securityQuestion);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update SecurityQuestion", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SecurityQuestion> findByCriteria(String criteria) {
		List<SecurityQuestion> securityQuestionList;
		try {
			securityQuestionList = securityQuestionDAO.findByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find By Criteria", e);
		}
		return securityQuestionList;
	}

}
