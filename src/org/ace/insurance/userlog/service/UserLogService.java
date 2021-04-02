package org.ace.insurance.userlog.service;

import org.ace.insurance.userlog.UserLog;
import org.ace.insurance.userlog.persistence.interfaces.IUserLogDAO;
import org.ace.insurance.userlog.service.interfaces.IUserLogService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("UserLogService")
public class UserLogService extends BaseService implements IUserLogService {

	@Autowired
	private IUserLogDAO userLogDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertUserLog(UserLog userLog) throws SystemException {
		try {
			userLogDAO.insertUserLost(userLog);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to insert User log", e);
		}
	}

}
