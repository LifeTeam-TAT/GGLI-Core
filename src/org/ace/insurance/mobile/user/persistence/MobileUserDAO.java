package org.ace.insurance.mobile.user.persistence;

import javax.persistence.PersistenceException;

import org.ace.insurance.mobile.user.MobileUser;
import org.ace.insurance.mobile.user.persistence.interfaces.IMobileUserDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("MobileUserDAOTravel")
public class MobileUserDAO extends BasicDAO implements IMobileUserDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public MobileUser findById(String id) throws DAOException {
		MobileUser result = null;
		try {
			result = em.find(MobileUser.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find MobileUser", pe);
		}
		return result;
	}

}
