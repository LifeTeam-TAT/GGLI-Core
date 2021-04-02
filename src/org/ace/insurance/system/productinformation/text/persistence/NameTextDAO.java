package org.ace.insurance.system.productinformation.text.persistence;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.productinformation.text.NameText;
import org.ace.insurance.system.productinformation.text.persistence.interfaces.INameTextDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository(value = "NameTextDAO")
public class NameTextDAO extends BasicDAO implements INameTextDAO{

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(NameText nameText) throws DAOException {
		try {
			em.persist(nameText);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert NameText", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public NameText update(NameText nameText) throws DAOException {
		try {
			nameText = em.merge(nameText);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update NameText", pe);
		}
		return nameText;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(NameText nameText) throws DAOException {
		try {
			nameText = em.merge(nameText);
			em.remove(nameText);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update NameText", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public NameText findById(String id) throws DAOException {
		NameText result = null;
		try {
			result = em.find(NameText.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find NameText", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<NameText> findAll() throws DAOException {
		List<NameText> result = null;
		try {
			Query q = em.createNamedQuery("NameText.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of NameText", pe);
		}
		return result;
	}

}
