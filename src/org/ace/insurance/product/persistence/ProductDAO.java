/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.product.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.product.KFRefValue;
import org.ace.insurance.product.KeyFactorConfig;
import org.ace.insurance.product.PROGRP001;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.product.ProductPremiumRate;
import org.ace.insurance.product.persistence.interfaces.IProductDAO;
import org.ace.insurance.product.service.interfaces.IKeyFactorConfigLoader;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.ws.cargo.model.productAddOnLink.ProductAddOnLinkDTO;
import org.ace.ws.cargo.model.productKeyfactorLink.ProductKeyfactorLinkDTO;
import org.ace.ws.cargo.model.productPaymentTypeLink.ProductPaymentTypeLinkDTO;
import org.ace.ws.cargo.model.productPremiumRateKeyfactor.ProductPremiumRateKeyfactorDTO;
import org.ace.ws.cargo.model.productPremiumRateLink.ProductPremiumRateLinkDTO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ProductDAO")
public class ProductDAO extends BasicDAO implements IProductDAO {
	@Resource(name = "KeyFactorConfigLoader")
	private IKeyFactorConfigLoader keyFactorConfigLoader;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Product product) throws DAOException {
		try {
			if (!isAlreadyExistProduct(product)) {
				em.persist(product);
				insertProcessLog(TableName.PRODUCT, product.getId());
				em.flush();
			} else {
				throw new SystemException(null, product.getName() + " is already exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Product", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Product product) throws DAOException {
		try {
			if (!isAlreadyExistProduct(product)) {
				em.merge(product);
				updateProcessLog(TableName.PRODUCT, product.getId());
				em.flush();
			} else {
				throw new SystemException(null, product.getName() + " is already exist");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update Product", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Product product) throws DAOException {
		try {
			product = em.merge(product);
			em.remove(product);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Product", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(ProductGroup productGroup) throws DAOException {
		try {
			if (!isAlreadyExistProductGroup(productGroup)) {
				em.persist(productGroup);
				insertProcessLog(TableName.PRODUCTGROUP, productGroup.getId());
				em.flush();
			} else {
				throw new SystemException(null, productGroup.getName() + " is already exist.");
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Product", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(ProductGroup productGroup) throws DAOException {
		try {
			if (!isAlreadyExistProductGroup(productGroup)) {
				em.merge(productGroup);
				updateProcessLog(TableName.PRODUCTGROUP, productGroup.getId());
				em.flush();
			} else {
				throw new SystemException(null, productGroup.getName() + " is already exist.");
			}

		} catch (PersistenceException pe) {
			throw translate("Failed to update ProductGroup", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ProductGroup productGroup) throws DAOException {
		try {
			productGroup = em.merge(productGroup);
			em.remove(productGroup);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update ProductGroup", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(ProductPremiumRate productPremiumRate) throws DAOException {
		try {
			em.persist(productPremiumRate);
			insertProcessLog(TableName.PRODUCT_PREMIUMRATE, productPremiumRate.getId());
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert ProductPremiumRate", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Product findById(String id) throws DAOException {
		Product result = null;
		try {
			result = em.find(Product.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Product", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findAll() throws DAOException {
		List<Product> result = null;
		try {
			Query q = em.createNamedQuery("Product.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findAllByNotEmptyProductPremiumRate() throws DAOException {
		List<Product> result = null;
		try {
			Query q = em.createNamedQuery("Product.findAllByNotEmptyProductPremiumRate");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product by not empty ProductPremiumRate", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findAllByEmptyProductPremiumRate() throws DAOException {
		List<Product> result = null;
		try {
			Query q = em.createNamedQuery("Product.findAllByEmptyProductPremiumRate");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product by empty ProductPremiumRate", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findByInsuranceType(InsuranceType insuranceType) throws DAOException {
		List<Product> result = null;
		try {
			Query q = em.createNamedQuery("Product.findByInsuranceType");
			q.setParameter("insuranceType", insuranceType);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product by InsuranceType ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Product> findProductByCurrencyType(InsuranceType insuranceType, Currency currency) throws DAOException {
		List<Product> result = null;
		try {
			Query q = em.createNamedQuery("Product.findProductByCurrencyType");
			q.setParameter("insuranceType", insuranceType);
			q.setParameter("currency", currency);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product by InsuranceType ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<KFRefValue> findReferenceValue(String entityName, InsuranceType insuranceType) throws DAOException {
		List<KFRefValue> result = null;
		try {
			KeyFactorConfig kfConfig = keyFactorConfigLoader.getKeyFactorConfig(entityName);
			if (kfConfig != null) {
				String stQuery = kfConfig.getQuery(insuranceType);
				Query q = em.createQuery(stQuery);
				if (insuranceType != null) {
					q.setParameter("insuranceType", insuranceType);
				}
				result = q.getResultList();
				em.flush();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product by InsuranceType ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductGroup> findAllProductGroup() throws DAOException {
		List<ProductGroup> result = null;
		try {
			Query q = em.createNamedQuery("ProductGroup.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ProductGroup", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductGroup> findProductGroupByProductGroupType(ProductGroupType productGroupType) throws DAOException {
		List<ProductGroup> result = null;
		try {
			Query q = em.createNamedQuery("ProductGroup.findByGroupType");
			q.setParameter("groupType", productGroupType);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ProductGroup", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductPaymentTypeLinkDTO> findAllProductPaymentTypeLinkList() throws DAOException {
		List<ProductPaymentTypeLinkDTO> result = new ArrayList<ProductPaymentTypeLinkDTO>();
		try {
			Query q = em.createNativeQuery("SELECT * FROM PRODUCT_PAYMENTTYPE_LINK");

			List<Object[]> views = q.getResultList();
			String productId;
			String paymentTypeId;

			for (Object[] obj : views) {
				productId = (String) obj[0];
				paymentTypeId = (String) obj[1];

				result.add(new ProductPaymentTypeLinkDTO(productId, paymentTypeId));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ProductPaymentTypeLinkDTO", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductAddOnLinkDTO> findAllProductAddOnLinkList() throws DAOException {
		List<ProductAddOnLinkDTO> result = new ArrayList<ProductAddOnLinkDTO>();
		try {
			Query q = em.createNativeQuery("SELECT * FROM PRODUCT_ADDON_LINK");

			List<Object[]> views = q.getResultList();
			String productId;
			String addOnId;

			for (Object[] obj : views) {
				productId = (String) obj[0];
				addOnId = (String) obj[1];

				result.add(new ProductAddOnLinkDTO(productId, addOnId));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ProductAddOnLinkDTO", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductKeyfactorLinkDTO> findAllProductKeyfactorLinkList() throws DAOException {
		List<ProductKeyfactorLinkDTO> result = new ArrayList<ProductKeyfactorLinkDTO>();
		try {
			Query q = em.createNativeQuery("SELECT * FROM PRODUCT_KEYFACTOR_LINK");

			List<Object[]> views = q.getResultList();
			String productId;
			String keyfactorId;

			for (Object[] obj : views) {
				productId = (String) obj[0];
				keyfactorId = (String) obj[1];

				result.add(new ProductKeyfactorLinkDTO(productId, keyfactorId));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ProductKeyfactorLinkDTO", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductPremiumRateLinkDTO> findAllProductPremiumRateLinkList() throws DAOException {
		List<ProductPremiumRateLinkDTO> result = new ArrayList<ProductPremiumRateLinkDTO>();
		try {
			Query q = em.createNativeQuery("SELECT id,premiumRate,productId,version FROM PRODUCT_PREMIUMRATE_LINK");

			List<Object[]> views = q.getResultList();
			String id;
			double pr;
			double premiumRate;
			String productId;
			int version;

			for (Object[] obj : views) {
				id = (String) obj[0];
				premiumRate = (double) obj[1];
				productId = (String) obj[2];
				version = (Integer) obj[3];

				result.add(new ProductPremiumRateLinkDTO(id, premiumRate, productId, version));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ProductPremiumRateLinkDTO", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProductPremiumRateKeyfactorDTO> findAllProductPremiumRateKeyfactorList() throws DAOException {
		List<ProductPremiumRateKeyfactorDTO> result = new ArrayList<ProductPremiumRateKeyfactorDTO>();
		try {
			Query q = em.createNativeQuery("SELECT * FROM PRODUCT_RATEKEYFACTOR");

			List<Object[]> views = q.getResultList();
			String id;
			String productPremiumRateId;
			String keyfactorId;

			double fromValue;
			double toValue;
			String value;
			String referenceName;
			int version;

			for (Object[] obj : views) {
				id = (String) obj[0];
				productPremiumRateId = (String) obj[1];
				keyfactorId = (String) obj[2];

				fromValue = (obj[3] == null) ? 0.0 : (double) obj[3];
				toValue = (obj[4] == null) ? 0.0 : (double) obj[4];
				value = (String) obj[5];
				referenceName = (String) obj[6];
				version = (Integer) obj[7];

				result.add(new ProductPremiumRateKeyfactorDTO(id, productPremiumRateId, keyfactorId, fromValue, toValue, value, referenceName, version));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ProductPremiumRateKeyfactorDTO", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Product findShortTermProductById(String shortEndowLifeId) {
		Product result = null;
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("Select p from Product p where p.id=:productId");
			Query q = em.createQuery(builder.toString());
			q.setParameter("productId", shortEndowLifeId);
			result = (Product) q.getSingleResult();
		} catch (PersistenceException pe) {
			throw translate("Failed to find ShortTermProduct ById", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public KFRefValue findKFRefValueById(String entityName, String id) throws DAOException {
		KFRefValue result = null;
		try {
			KeyFactorConfig kfConfig = keyFactorConfigLoader.getKeyFactorConfig(entityName);
			String stQuery = kfConfig.getQuery(id);
			Query q = em.createQuery(stQuery);
			result = (KFRefValue) q.getSingleResult();
			em.flush();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find KFRefValue by id. ( ID : " + id + " , Entity Name : " + entityName + ")", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PROGRP001> findProductByInsuranceType(InsuranceType insuranceType) throws DAOException {
		List<PROGRP001> result = null;
		try {
			Query q = em.createNamedQuery("Product.findByInsuranceType");
			q.setParameter("insuranceType", insuranceType);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product by InsuranceType ", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PROGRP001> findProduct() throws DAOException {
		List<PROGRP001> result = null;
		try {
			Query q = em.createNamedQuery("Product.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Product", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<PROGRP001> findAllByGroupType(ProductGroupType groupType) throws DAOException {
		List<PROGRP001> result = null;
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT NEW " + PROGRP001.class.getName() + "(p.id, p.name, p.groupType) FROM ProductGroup p ");
			if (groupType != null)
				builder.append(" WHERE p.groupType = :groupType ");
			Query query = em.createQuery(builder.toString());
			if (groupType != null)
				query.setParameter("groupType", groupType);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all by group type ", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProductGroup findProductGroupById(String id) throws DAOException {
		ProductGroup result = null;
		try {
			result = em.find(ProductGroup.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Product Group", pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistProduct(Product product) {
		boolean exist = false;
		String productName = product.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Product c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(product.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (product.getId() != null)
				query.setParameter("id", product.getId());
			query.setParameter("name", productName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isAlreadyExistProductGroup(ProductGroup productGroup) {
		boolean exist = false;
		String productGroupName = productGroup.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;
			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM ProductGroup c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(productGroup.getId() != null ? " AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (productGroup.getId() != null)
				query.setParameter("id", productGroup.getId());
			query.setParameter("name", productGroupName.toLowerCase());
			exist = (Boolean) query.getSingleResult();
			em.flush();

			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}
}
