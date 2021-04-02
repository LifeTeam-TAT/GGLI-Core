package org.ace.insurance.web.manage.claim.disabilityPart;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.claim.disabilityPart.DisabilityPart;
import org.ace.insurance.claim.disabilityPart.ProductDisability;
import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;
import org.ace.insurance.claim.disabilityPart.service.interfaces.IProductDisabilityService;
import org.ace.insurance.product.Product;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageDisabilityRateActionBean")
public class ManageDisabilityRateActionBean extends BaseBean {

	@ManagedProperty(value = "#{ProductDisabilityService}")
	private IProductDisabilityService productDisabilityService;

	public void setProductDisabilityService(IProductDisabilityService productDisabilityService) {
		this.productDisabilityService = productDisabilityService;
	}

	private ProductDisability productDisability;
	private ProductDisabilityRate disabilityRate;
	private List<ProductDisability> productDisabilityList;
	private List<ProductDisabilityRate> disabilityRateList;
	private boolean createNew;

	@PostConstruct
	public void init() {
		createNewProductDisability();
		loadProductDisability();
	}

	public void createNewProductDisability() {
		productDisability = new ProductDisability();
		disabilityRate = new ProductDisabilityRate();
		disabilityRateList = new ArrayList<ProductDisabilityRate>();
		createNew = true;

	}

	private void loadProductDisability() {
		productDisabilityList = productDisabilityService.findAllDisabilityPart();
	}

	private boolean validate() {
		boolean result = true;
		for (ProductDisabilityRate rate : disabilityRateList) {
			if (rate.getPercentage() <= 0.0) {
				result = false;
				break;
			}
		}
		return result;

	}

	public void addNewProductDisability() {
		try {
			if (validate()) {
				productDisability.setDisabilityRateList(disabilityRateList);
				productDisabilityService.addNewProductDisability(productDisability);
				productDisabilityList.add(productDisability);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, productDisability.getProduct().getName());
				createNewProductDisability();
			} else {
				addErrorMessage(null, MessageId.ADD_PERCENTAGE, productDisability.getProduct().getName());
			}
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void deleteDisabilityRate(ProductDisabilityRate rate) {

		productDisability.getDisabilityRateList().remove(rate);
	}

	public void updateProductDisability() {
		try {
			if (validate()) {
				productDisability.setDisabilityRateList(disabilityRateList);
				productDisability = productDisabilityService.updateProductDisability(productDisability);
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, productDisability.getProduct().getName());
				createNewProductDisability();
				loadProductDisability();
			} else {
				addErrorMessage("disabilityRateEntryForm:disabilityPercentageGroup", MessageId.ADD_PERCENTAGE, productDisability.getProduct().getName());
			}
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void prepareEditProductDisability(ProductDisability disability) {
		this.productDisability = productDisabilityService.findProductDisbilityById(disability.getId());
		if (null != productDisability) {
			for (ProductDisabilityRate disabilityrate : disability.getDisabilityRateList()) {
				this.disabilityRateList.add(disabilityrate);
			}
		}
		createNew = false;
	}

	public void deleteProductDisability(ProductDisability disability) {
		try {
			productDisabilityService.deleteProductDisability(disability);
			productDisabilityList.remove(disability);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, disability.getProduct().getName());
			createNewProductDisability();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void returnProduct(SelectEvent event) {
		Product product = (Product) event.getObject();
		productDisability.setProduct(product);
	}

	public void returnDisabilityPart(SelectEvent event) {
		List<DisabilityPart> disabilityPartList = new ArrayList<DisabilityPart>();
		for (ProductDisabilityRate rate : disabilityRateList) {
			disabilityPartList.add(rate.getDisabilityPart());
		}
		List<DisabilityPart> selectedDisabilityPartList = (List<DisabilityPart>) event.getObject();
		for (DisabilityPart part : selectedDisabilityPartList) {
			if (!disabilityPartList.contains(part)) {
				disabilityRate = new ProductDisabilityRate();
				disabilityRate.setDisabilityPart(part);
				disabilityRateList.add(disabilityRate);
			}
		}
		productDisability.setDisabilityRateList(disabilityRateList);
	}

	public ProductDisability getProductDisability() {
		return productDisability;
	}

	public List<ProductDisability> getProductDisabilityList() {
		return productDisabilityList;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public ProductDisabilityRate getDisabilityRate() {
		return disabilityRate;
	}

	public List<ProductDisabilityRate> getDisabilityRateList() {
		return disabilityRateList;
	}

	public void selectDisabilityPart() {
		List<DisabilityPart> disabilityPartList = new ArrayList<DisabilityPart>();
		for (ProductDisabilityRate rate : disabilityRateList) {
			disabilityPartList.add(rate.getDisabilityPart());
		}
		selectDisabilityPart(disabilityPartList);
	}
}