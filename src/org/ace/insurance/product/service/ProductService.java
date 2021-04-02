/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.product.service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.insurance.product.KFRefValue;
import org.ace.insurance.product.PROGRP001;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.product.ProductPremiumRate;
import org.ace.insurance.product.persistence.interfaces.IProductDAO;
import org.ace.insurance.product.persistence.interfaces.IProductPremiumRateDAO;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.keyfactor.service.interfaces.IKeyFactorService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.ace.ws.cargo.model.productAddOnLink.ProductAddOnLinkDTO;
import org.ace.ws.cargo.model.productKeyfactorLink.ProductKeyfactorLinkDTO;
import org.ace.ws.cargo.model.productPaymentTypeLink.ProductPaymentTypeLinkDTO;
import org.ace.ws.cargo.model.productPremiumRateKeyfactor.ProductPremiumRateKeyfactorDTO;
import org.ace.ws.cargo.model.productPremiumRateLink.ProductPremiumRateLinkDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "ProductService")
public class ProductService extends BaseService implements IProductService {

	@Resource(name = "ProductDAO")
	private IProductDAO productDAO;

	@Resource(name = "ProductPremiumRateDAO")
	private IProductPremiumRateDAO productPremiumRateDAO;

	@Resource(name = "KeyFactorService")
	private IKeyFactorService keyFactorService;

	@Resource(name = "FIRE_ID_CONFIG")
	private Properties properties;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewProduct(Product product) {
		// boolean addSumInsuredKey = true;
		try {
			/*
			 * for(KeyFactor kf : motorProduct.getKeyFactorList()) {
			 * if(kf.getId().equals(KeyFactor.SUM_INSU_ID)) { addSumInsuredKey =
			 * false;break; } } if(addSumInsuredKey) { KeyFactor kf =
			 * keyFactorService.findKeyFactorById(KeyFactor.SUM_INSU_ID);
			 * motorProduct.getKeyFactorList().add(kf); }
			 */
			product.setPrefix(getPrefix(Product.class));
			productDAO.insert(product);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Product", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewProductGroup(ProductGroup productGroup) {
		try {
			productGroup.setPrefix(getPrefix(ProductGroup.class));
			productDAO.insert(productGroup);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Product", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProduct(Product product) {
		try {
			productDAO.update(product);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Product", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProductGroup(ProductGroup productGroup) {
		try {
			productDAO.update(productGroup);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a updateProductGroup", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteProduct(Product product) {
		try {
			productDAO.delete(product);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a Product", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteProductGroup(ProductGroup productGroup) {
		try {
			productDAO.delete(productGroup);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a ProductGroup", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProductPremiumRate addNewProductPremiumRate(ProductPremiumRate productPremiumRate) {
		ProductPremiumRate result = null;
		try {
			productPremiumRate.setPrefix(getPrefix(ProductPremiumRate.class));
			result = productPremiumRateDAO.insert(productPremiumRate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new ProductPremiumRate", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProductPremiumRate(ProductPremiumRate productPremiumRate) {
		try {
			productPremiumRateDAO.update(productPremiumRate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a ProductPremiumRate", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteProductPremiumRate(ProductPremiumRate productPremiumRate) {
		try {
			productPremiumRateDAO.delete(productPremiumRate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a ProductPremiumRate", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findAllProduct() {
		List<Product> result = null;
		try {
			result = productDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Product findProductById(String id) {
		Product result = null;
		try {
			result = productDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Product (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProductPremiumRate findProductPremiumRateById(String id) {
		ProductPremiumRate result = null;
		try {
			result = productPremiumRateDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a ProductPremiumRate (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductPremiumRate> findProductPremiumRateByProduct(String productId) {
		List<ProductPremiumRate> result = null;
		try {
			result = productPremiumRateDAO.findByProduct(productId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a ProductPremiumRate by Product (ID : " + productId + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Double findProductPremiumRate(Map<KeyFactor, String> keyfatorValueMap, Product product, SumInsuredType sumInsuredType) {
		Double result;
		try {
			result = productPremiumRateDAO.findProductPremiumRate(keyfatorValueMap, product, sumInsuredType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a ProductPremiumRate", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Double findThirdPartyPremiumRate(Map<KeyFactor, String> keyfatorValueMap, Product product) {
		Double result = 0.0;
		try {
			result = productPremiumRateDAO.findThirdPartyPremiumRate(keyfatorValueMap, product);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a ProductPremiumRate", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findProductByInsuranceType(InsuranceType insuranceType) throws SystemException {
		List<Product> result = null;
		try {
			result = productDAO.findByInsuranceType(insuranceType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product by InsuranceType)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findProductByCurrencyType(InsuranceType insuranceType, Currency currency) throws SystemException {
		List<Product> result = null;
		try {
			result = productDAO.findProductByCurrencyType(insuranceType, currency);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product by InsuranceType)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<KFRefValue> findReferenceValue(String entityName, InsuranceType insuranceType) throws SystemException {
		List<KFRefValue> result = null;
		try {
			result = productDAO.findReferenceValue(entityName, insuranceType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of KFRefValue)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductGroup> findAllProductGroup() {
		List<ProductGroup> result = null;
		try {
			result = productDAO.findAllProductGroup();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductGroup> findProductGroupByProductGroupType(ProductGroupType productGroupType) throws SystemException {
		List<ProductGroup> result = null;
		try {
			result = productDAO.findProductGroupByProductGroupType(productGroupType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<PROGRP001> findAllByGroupType(ProductGroupType groupType) throws SystemException {
		List<PROGRP001> result = null;
		try {
			result = productDAO.findAllByGroupType(groupType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public ProductGroup findProductGroupById(String id) throws SystemException {
		ProductGroup result = null;
		try {
			result = productDAO.findProductGroupById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Product Group (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductPaymentTypeLinkDTO> findAllProductPaymentTypeLinkList() throws SystemException {
		List<ProductPaymentTypeLinkDTO> result = null;
		try {
			result = productDAO.findAllProductPaymentTypeLinkList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of ProductPaymentTypeLinkDTO)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductAddOnLinkDTO> findAllProductAddOnLinkList() throws SystemException {
		List<ProductAddOnLinkDTO> result = null;
		try {
			result = productDAO.findAllProductAddOnLinkList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of ProductAddOnLinkDTO)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductKeyfactorLinkDTO> findAllProductKeyfactorLinkDTOList() throws SystemException {
		List<ProductKeyfactorLinkDTO> result = null;
		try {
			result = productDAO.findAllProductKeyfactorLinkList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of ProductKeyfactorLinkDTO)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductPremiumRateLinkDTO> findAllProductPremiumRateLinkList() throws SystemException {
		List<ProductPremiumRateLinkDTO> result = null;
		try {
			result = productDAO.findAllProductPremiumRateLinkList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of ProductPremiumRateLinkDTO)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductPremiumRateKeyfactorDTO> findAllProductPremiumRateKeyfactorList() throws SystemException {
		List<ProductPremiumRateKeyfactorDTO> result = null;
		try {
			result = productDAO.findAllProductPremiumRateKeyfactorList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of ProductPremiumRateKeyfactorDTO)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Product findShortTermProductById(String shortEndowLifeId) {
		Product result = null;
		try {
			result = productDAO.findShortTermProductById(shortEndowLifeId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find ShortTermProduct By Id)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public KFRefValue findKFRefValueById(String entityName, String id) throws SystemException {
		KFRefValue result = null;
		try {
			result = productDAO.findKFRefValueById(entityName, id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find KFRefValue by id. ( ID : " + id + " , Entity Name : " + entityName + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PROGRP001> findByInsuranceType(InsuranceType insuranceType) throws SystemException {
		List<PROGRP001> result = null;
		try {
			result = productDAO.findProductByInsuranceType(insuranceType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product by InsuranceType)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PROGRP001> findProduct() throws SystemException {
		List<PROGRP001> result = null;
		try {
			result = productDAO.findProduct();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of product)", e);
		}
		return result;
	}

}