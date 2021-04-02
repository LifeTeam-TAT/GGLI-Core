/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.product.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.product.KFRefValue;
import org.ace.insurance.product.PROGRP001;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.ws.cargo.model.productAddOnLink.ProductAddOnLinkDTO;
import org.ace.ws.cargo.model.productKeyfactorLink.ProductKeyfactorLinkDTO;
import org.ace.ws.cargo.model.productPaymentTypeLink.ProductPaymentTypeLinkDTO;
import org.ace.ws.cargo.model.productPremiumRateKeyfactor.ProductPremiumRateKeyfactorDTO;
import org.ace.ws.cargo.model.productPremiumRateLink.ProductPremiumRateLinkDTO;

public interface IProductDAO {
	public void insert(Product product) throws DAOException;

	public void update(Product product) throws DAOException;

	public void delete(Product product) throws DAOException;

	public void insert(ProductGroup productGroup) throws DAOException;

	public void update(ProductGroup productGroup) throws DAOException;

	public void delete(ProductGroup productGroup) throws DAOException;

	public Product findById(String id) throws DAOException;

	public List<Product> findByInsuranceType(InsuranceType insuranceType) throws DAOException;

	public List<Product> findProductByCurrencyType(InsuranceType insuranceType, Currency currency) throws DAOException;

	public List<Product> findAll() throws DAOException;

	public List<PROGRP001> findProductByInsuranceType(InsuranceType insuranceType) throws DAOException;

	public List<PROGRP001> findProduct() throws DAOException;

	public List<KFRefValue> findReferenceValue(String entityName, InsuranceType insuranceType) throws DAOException;

	public List<ProductGroup> findProductGroupByProductGroupType(ProductGroupType productGroupType) throws DAOException;

	public List<ProductGroup> findAllProductGroup() throws DAOException;

	public List<ProductPaymentTypeLinkDTO> findAllProductPaymentTypeLinkList() throws DAOException;

	public List<ProductAddOnLinkDTO> findAllProductAddOnLinkList() throws DAOException;

	public List<ProductKeyfactorLinkDTO> findAllProductKeyfactorLinkList() throws DAOException;

	public List<ProductPremiumRateLinkDTO> findAllProductPremiumRateLinkList() throws DAOException;

	public List<ProductPremiumRateKeyfactorDTO> findAllProductPremiumRateKeyfactorList() throws DAOException;

	public Product findShortTermProductById(String shortEndowLifeId);

	public KFRefValue findKFRefValueById(String entityName, String id) throws DAOException;

	public List<PROGRP001> findAllByGroupType(ProductGroupType groupType) throws DAOException;

	public ProductGroup findProductGroupById(String id) throws DAOException;

	boolean isAlreadyExistProduct(Product product) throws DAOException;;

	boolean isAlreadyExistProductGroup(ProductGroup product) throws DAOException;;
}
