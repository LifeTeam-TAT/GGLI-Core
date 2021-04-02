package org.ace.insurance.system.faq.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.faq.FAQ;
import org.ace.insurance.system.faq.persistence.interfaces.IFAQDAO;
import org.ace.insurance.system.productinformation.Language;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.ws.model.system.FAQ001;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("FAQDAO")
public class FAQDAO extends BasicDAO implements IFAQDAO {
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(FAQ faq) throws DAOException {
		try {
			em.persist(faq);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert FAQ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(FAQ faq) throws DAOException {
		try {
			faq = em.merge(faq);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update FAQ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(FAQ faq) throws DAOException {
		try {
			faq = em.merge(faq);
			em.remove(faq);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete FAQ", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public FAQ findById(String id) throws DAOException {
		FAQ result = null;
		try {
			result = em.find(FAQ.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find FAQ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ> findAll() throws DAOException {
		List<FAQ> result = null;
		try {
			Query q = em.createNamedQuery("FAQ.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of FAQ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ> findFAQByCodeNo(String codeNo) throws DAOException {
		List<FAQ> result = null;
		try {
			Query q = em.createQuery("SELECT faq FROM FAQ faq WHERE faq.codeNo = :codeNo");
			q.setParameter("codeNo", codeNo);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find fAQ by codeNo", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ> findFAQByLanguage(Language language) throws DAOException {
		List<FAQ> result = null;
		try {
			Query q = em.createQuery("select f from FAQ f where f.language = :language");
			q.setParameter("language", language);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find fAQ by language", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ001> findAll_FAQ001() throws DAOException {
		List<FAQ001> result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT NEW " + FAQ001.class.getName());
			buffer.append(" ( f.id, f.question, f.answer, f.codeNo, f.language, f.version, ");
			buffer.append(" f.recorder.createdUserId, f.recorder.createdDate, ");
			buffer.append(" f.recorder.updatedUserId, f.recorder.updatedDate ) FROM FAQ f ");
			Query query = em.createQuery(buffer.toString());
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of FAQ", pe);
		}
		return result;
	}
}
