package org.ace.insurance.system.productfaq.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.system.faq.FAQ;
import org.ace.insurance.system.productfaq.ProductFAQ;
import org.ace.insurance.system.productfaq.persistence.interfaces.IProductFAQDAO;
import org.ace.insurance.system.productfaq.service.interfaces.IProductFAQService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "ProductFAQService")
public class ProductFAQService extends BaseService implements IProductFAQService {

	@Resource(name = "ProductFAQDAO")
	private IProductFAQDAO productFAQDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ProductFAQ findById(String id) {
		ProductFAQ result = null;
		try {
			result = productFAQDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Fail to find ProductFAQ by Id : " + id, e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductFAQ> findAllProductFAQ() {
		List<ProductFAQ> result = null;
		try {
			result = productFAQDAO.findAllProductFAQ();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find All ProductFAQ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertProductFAQ(ProductFAQ productFAQ) {
		try {
			setFAQCodeNo(productFAQ);
			productFAQDAO.insert(productFAQ);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to insert ProductFAQ", e);
		}
	}

	private void setFAQCodeNo(ProductFAQ productFAQ) {
		for (FAQ faq : productFAQ.getFaqList()) {
			if (faq.getCodeNo() == null) {
				String faqCodeNo = customIDGenerator.getNextId(SystemConstants.FAQ_CODE_NO, "", false);
				faq.setCodeNo(faqCodeNo);
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ProductFAQ updateProductFAQ(ProductFAQ productFAQ) {
		try {
			setFAQCodeNo(productFAQ);
			productFAQ = productFAQDAO.update(productFAQ);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to insert ProductFAQ", e);
		}
		return productFAQ;

	}
}
