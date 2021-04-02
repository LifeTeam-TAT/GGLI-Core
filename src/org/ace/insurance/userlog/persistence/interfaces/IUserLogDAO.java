package org.ace.insurance.userlog.persistence.interfaces;

import org.ace.insurance.userlog.UserLog;
import org.ace.java.component.persistence.exception.DAOException;

public interface IUserLogDAO {

	void insertUserLost(UserLog userlog) throws DAOException;

}