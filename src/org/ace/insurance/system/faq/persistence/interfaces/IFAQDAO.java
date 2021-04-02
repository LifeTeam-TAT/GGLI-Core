package org.ace.insurance.system.faq.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.faq.FAQ;
import org.ace.insurance.system.productinformation.Language;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.ws.model.system.FAQ001;

public interface IFAQDAO {
	public void insert(FAQ faq) throws DAOException;

	public void update(FAQ faq) throws DAOException;

	public void delete(FAQ faq) throws DAOException;

	public FAQ findById(String id) throws DAOException;

	public List<FAQ> findAll() throws DAOException;

	public List<FAQ> findFAQByCodeNo(String codeNo) throws DAOException;

	public List<FAQ> findFAQByLanguage(Language language) throws DAOException;

	public List<FAQ001> findAll_FAQ001() throws DAOException;
}
