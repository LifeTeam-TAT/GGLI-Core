package org.ace.insurance.system.common.securityQuestion.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.securityQuestion.SecurityQuestion;
import org.ace.insurance.system.common.securityQuestion.persistence.interfaces.ISecurityQuestionDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("SecurityQuestionDAO")
public class SecurityQuestionDAO extends BasicDAO implements ISecurityQuestionDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(SecurityQuestion securityQuestion) {
		try {
			em.merge(securityQuestion);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to Update SecurityQuestion", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(SecurityQuestion securityQuestion) {
		try {
			em.persist(securityQuestion);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to Insert SecurityQuestion", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(String id) {
		try {
			StringBuilder query = new StringBuilder();
			query.append("Delete from SecurityQuestion where id=:id");
			Query q = em.createQuery(query.toString());
			q.setParameter("id", id);
			q.executeUpdate();
		} catch (PersistenceException pe) {
			throw translate("Failed to Delete SecurityQuestion", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SecurityQuestion> findAllSecurityQuestionList() {
		List<SecurityQuestion> securityQuestionList;
		try {
			Query q = em.createNamedQuery("SecurityQuestion.findAll");
			securityQuestionList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to Find All SecurityQuestion", pe);
		}
		return securityQuestionList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SecurityQuestion> findByCriteria(String criteria) {
		List<SecurityQuestion> securityQuestionList;
		try {
			Query q = em.createQuery("Select q from SecurityQuestion q where q.securityQuestion Like '" + criteria + "%'");
			securityQuestionList = q.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Failed to Find All SecurityQuestion", pe);
		}
		return securityQuestionList;
	}

}
