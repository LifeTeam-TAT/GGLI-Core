package org.ace.insurance.mobile.user.service;

import javax.annotation.Resource;

import org.ace.insurance.mobile.user.MobileUser;
import org.ace.insurance.mobile.user.persistence.interfaces.IMobileUserDAO;
import org.ace.insurance.mobile.user.service.interfaces.IMobileUserService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "MobileUserServices")
public class MobileUserService extends BaseService implements IMobileUserService {

	@Resource(name = "MobileUserDAOTravel")
	private IMobileUserDAO mobileUserDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MobileUser findMobileUserById(String id) {
		MobileUser result = null;
		try {
			result = mobileUserDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a MobileUser (ID : " + id + ")", e);
		}
		return result;
	}
}
