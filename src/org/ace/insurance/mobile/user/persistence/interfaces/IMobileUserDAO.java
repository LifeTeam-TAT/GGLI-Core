package org.ace.insurance.mobile.user.persistence.interfaces;

import org.ace.insurance.mobile.user.MobileUser;
import org.ace.java.component.persistence.exception.DAOException;

public interface IMobileUserDAO {

	public MobileUser findById(String id) throws DAOException;

}
