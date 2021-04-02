package org.ace.insurance.userlog.service.interfaces;

import org.ace.insurance.userlog.UserLog;
import org.ace.java.component.SystemException;

public interface IUserLogService {

	void insertUserLog(UserLog userLog) throws SystemException;

}