package org.ace.insurance.userlog.persistence;

import javax.persistence.PersistenceException;

import org.ace.insurance.userlog.UserLog;
import org.ace.insurance.userlog.persistence.interfaces.IUserLogDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("UserLogDAO")
public class UserLogDAO extends BasicDAO implements IUserLogDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertUserLost(UserLog userlog) throws DAOException {
		try {
			em.persist(userlog);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Fail to insert user log", pe);
		}
	}

}
