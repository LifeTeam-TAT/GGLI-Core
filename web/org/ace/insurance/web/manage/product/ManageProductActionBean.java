/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.product;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.product.PremiumRateType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageProductActionBean")
public class ManageProductActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private Product product;
	private List<Product> productList;
	private InsuranceType insuranceType;

	@PostConstruct
	public void init() {
		if (insuranceType == null) {
			productList = productService.findAllProduct();
		} else {
			productList = productService.findProductByInsuranceType(insuranceType);
		}
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public InsuranceType[] getInsuranceTypes() {
		return InsuranceType.values();
	}

	public void changeInsuranceType(AjaxBehaviorEvent e) {
		if (insuranceType != null) {
			productList = productService.findProductByInsuranceType(insuranceType);
		} else {
			productList = productService.findAllProduct();
		}
	}

	public List<Product> getProductList() {
		return productList;
	}

	public String prepareForAddNewProduct() {
		this.product = new Product();
		this.product.setAutoCalculate(true);
		this.product.setPremiumRateType(PremiumRateType.USER_DEFINED_RATE);
		return "addNewProduct";
	}

	public String prepareForUpdateProduct(Product product) {
		putParam("productParam", product);
		return "updateProduct";
	}

	public String prepareForPremiumRateConfig(Product product) {
		putParam("productParam", product);
		return "manageProductPrmRateConfig";
	}

	public void deleteProduct() {
		try {
			productService.deleteProduct(product);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, product.getName());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}
}
