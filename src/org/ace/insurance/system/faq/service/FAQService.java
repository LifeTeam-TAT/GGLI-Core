package org.ace.insurance.system.faq.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.system.faq.FAQ;
import org.ace.insurance.system.faq.persistence.interfaces.IFAQDAO;
import org.ace.insurance.system.faq.service.interfaces.IFAQService;
import org.ace.insurance.system.productinformation.Language;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.ace.ws.model.system.FAQ001;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "FAQService")
public class FAQService extends BaseService implements IFAQService {
	@Resource(name = "FAQDAO")
	private IFAQDAO fAQDAO;

	public void setfAQDAO(IFAQDAO fAQDAO) {
		this.fAQDAO = fAQDAO;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewFAQ(FAQ faq) {
		try {
			fAQDAO.insert(faq);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new FAQ", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateFAQ(FAQ faq) {
		try {
			fAQDAO.update(faq);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a FAQ", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteFAQ(FAQ faq) {
		try {
			fAQDAO.delete(faq);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a FAQ", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ> findAllFAQ() {
		List<FAQ> result = null;
		try {
			result = fAQDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of FAQ)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public FAQ findFAQById(String id) {
		FAQ result = null;
		try {
			result = fAQDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a FAQ (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ> findFAQByCodeNo(String codeNo) {
		List<FAQ> result = null;
		try {
			result = fAQDAO.findFAQByCodeNo(codeNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find FAQ by codeNo)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ> findFAQByLanguage(Language language) {
		List<FAQ> result = null;
		try {
			result = fAQDAO.findFAQByLanguage(language);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find FAQ by language)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewFAQGroup(List<FAQ> faqList) {
		try {
			String faqCodeNo = customIDGenerator.getNextId(SystemConstants.FAQ_CODE_NO, "", false);
			for (FAQ faq : faqList) {
				faq.setCodeNo(faqCodeNo);
				addNewFAQ(faq);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new FAQGroup", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<FAQ001> findAll_FAQ001() {
		List<FAQ001> result = null;
		try {
			result = fAQDAO.findAll_FAQ001();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of SurveyQuestion)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String generateFAQCode() {
		String faqCodeNo = customIDGenerator.getNextId(SystemConstants.FAQ_CODE_NO, "", false);
		return faqCodeNo;
	}
}
