package org.ace.insurance.medical.numberReference.persistence;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.ace.insurance.medical.numberReference.persistence.interfaces.INumberReferenceDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("NumberReferenceDAO")
public class NumberReferenceDAO extends BasicDAO implements INumberReferenceDAO {
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String findNewNumberReferenceByOldNumber(String oldNumber) throws DAOException {
		try {
			TypedQuery<String> query = em.createNamedQuery("NumberReference.findNewNumberByOldNumber", String.class);
			query.setParameter("oldNumber", oldNumber);
			return query.getSingleResult();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Fail to find NumberReference by oldNumber", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String findNewNumberReferenceByNewNumber(String newNumber) throws DAOException {
		try {
			TypedQuery<String> query = em.createNamedQuery("NumberReference.findNewNumberByNewNumber", String.class);
			query.setParameter("newNumber", newNumber);
			return query.getSingleResult();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Fail to find NumberReference by New Number", pe);
		}
	}
}
