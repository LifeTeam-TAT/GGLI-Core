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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.product.PremiumRateType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.addon.service.interfaces.IAddOnService;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.keyfactor.service.interfaces.IKeyFactorService;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.service.interfaces.IPaymentTypeService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.datamodel.AddOnDataModel;
import org.ace.insurance.web.datamodel.KeyFactorDataModel;
import org.ace.insurance.web.datamodel.PaymentTypeDataModel;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewProductActionBean")
public class AddNewProductActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private Product product;
	@ManagedProperty(value = "#{PaymentTypeService}")
	private IPaymentTypeService paymentTypeService;

	public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
		this.paymentTypeService = paymentTypeService;
	}

	@ManagedProperty(value = "#{AddOnService}")
	private IAddOnService addOnService;

	public void setAddOnService(IAddOnService addOnService) {
		this.addOnService = addOnService;
	}

	@ManagedProperty(value = "#{KeyFactorService}")
	private IKeyFactorService keyFactorService;

	public void setKeyFactorService(IKeyFactorService keyFactorService) {
		this.keyFactorService = keyFactorService;
	}
	

	private List<KeyFactor> keyFactorList;
	private List<SelectItem> insuranceTypeSelectItemlist;
	private InsuranceType insuranceType;
	private List<ProductGroup> productGroups;
	private List<PaymentType> paymentTypeList;
	private List<AddOn> addOnList;
//	private boolean isSimpleLife;

	@PostConstruct
	public void init() {
		productGroups = productService.findAllProductGroup();
		paymentTypeList = paymentTypeService.findAllPaymentType();
		addOnList = addOnService.findAllAddOn();
		keyFactorList = keyFactorService.findAllKeyFactor();
		product = new Product();
		//isSimpleLife = KeyFactorChecker.isSimpleLife(product);
		insuranceTypeSelectItemlist = new ArrayList<SelectItem>();
		InsuranceType inType[] = insuranceType.values();
		List<InsuranceType> insuranceTypes = Arrays.asList(inType);
		for (InsuranceType in : insuranceTypes) {
			insuranceTypeSelectItemlist.add(new SelectItem(in, in.getLabel()));
		}
	}

	public PremiumRateType[] getPremiumRateTypes() {
		return PremiumRateType.values();
	}

	public InsuranceType[] getInsuranceTypes() {
		return InsuranceType.values();
	}

	public IProductService getProductService() {
		return productService;
	}

	public List<ProductGroup> getProductGroups() {
		return productGroups;
	}

	public void setProductGroups(List<ProductGroup> productGroups) {
		this.productGroups = productGroups;
	}

	public void setPaymentTypeList(List<PaymentType> paymentTypeList) {
		this.paymentTypeList = paymentTypeList;
	}

	public void setAddOnList(List<AddOn> addOnList) {
		this.addOnList = addOnList;
	}

	public void setKeyFactorList(List<KeyFactor> keyFactorList) {
		this.keyFactorList = keyFactorList;
	}

	public void changeFixValue(AjaxBehaviorEvent e) {
		if (!product.getAutoCalculate()) {
			product.setKeyFactorList(new ArrayList<KeyFactor>());
		}
	}

	public void removePaymentTypeList(PaymentType paymentType) {
		product.getPaymentTypeList().remove(paymentType);
	}

	public void removeAddOnList(AddOn addOn) {
		product.getAddOnList().remove(addOn);
	}

	public void removeKeyFactorList(KeyFactor keyFactor) {
		product.getKeyFactorList().remove(keyFactor);
	}

	
//	public boolean isSimpleLife() {
//		return isSimpleLife;
//	}
//
//	public void setSimpleLife(boolean isSimpleLife) {
//		this.isSimpleLife = isSimpleLife;
//	}

	public String addNewProduct() {
		String result = null;
		try {
			String formId = "productEntryForm";
			if (product.getProductGroup() == null) {
				addErrorMessage(formId + ":pGroup", UIInput.REQUIRED_MESSAGE_ID);
			}
			productService.addNewProduct(product);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, product.getName());
			result = "manageProduct";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public PaymentTypeDataModel getPaymentTypeDataModel() {
		return new PaymentTypeDataModel(paymentTypeList);
	}

	public AddOnDataModel getAddOnDataModel() {
		return new AddOnDataModel(addOnList);
	}

	public KeyFactorDataModel getKeyFactorDataModel() {
		return new KeyFactorDataModel(keyFactorList);
	}

	public PaymentType[] getSelectedPaymentTypes() {
		return null;
	}

	public void setSelectedPaymentTypes(PaymentType[] paymentTypes) {
		List<PaymentType> paymentTypeList = product.getPaymentTypeList();
		if (paymentTypeList == null) {
			paymentTypeList = new ArrayList<PaymentType>();
		}
		Set<PaymentType> paymentTypeSet = new HashSet<PaymentType>(paymentTypeList);
		for (PaymentType var : paymentTypes) {
			paymentTypeSet.add(var);
		}
		product.setPaymentTypeList(new ArrayList<PaymentType>(paymentTypeSet));
	}

	public AddOn[] getSelectedAddOns() {
		return null;
	}

	public void setSelectedAddOns(AddOn[] addons) {
		List<AddOn> addOnList = product.getAddOnList();
		if (addOnList == null) {
			addOnList = new ArrayList<AddOn>();
		}
		Set<AddOn> addOnSet = new HashSet<AddOn>(addOnList);
		for (AddOn var : addons) {
			addOnSet.add(var);
		}
		product.setAddOnList(new ArrayList<AddOn>(addOnSet));
	}

	public KeyFactor[] getSelectedKeyFactors() {
		return null;
	}

	public void setSelectedKeyFactors(KeyFactor[] keyFactors) {
		List<KeyFactor> keyFactorList = product.getKeyFactorList();
		if (keyFactorList == null) {
			keyFactorList = new ArrayList<KeyFactor>();
		}
		Set<KeyFactor> keyFactorSet = new HashSet<KeyFactor>(keyFactorList);
		for (KeyFactor var : keyFactors) {
			keyFactorSet.add(var);
		}
		product.setKeyFactorList(new ArrayList<KeyFactor>(keyFactorSet));
	}

	public List<PaymentType> getPaymentTypeList() {
		return paymentTypeList;
	}

	public List<AddOn> getAddOnList() {
		return addOnList;
	}

	public List<KeyFactor> getKeyFactorList() {
		return keyFactorList;
	}

	public List<SelectItem> getInsuranceTypeSelectItemlist() {
		return insuranceTypeSelectItemlist;
	}

	public void setInsuranceTypeSelectItemlist(List<SelectItem> insuranceTypeSelectItemlist) {
		this.insuranceTypeSelectItemlist = insuranceTypeSelectItemlist;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public void returnProductGroup(SelectEvent event) {
		ProductGroup productGroup = (ProductGroup) event.getObject();
		product.setProductGroup(productGroup);
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void returnCurrency(SelectEvent event) {
		Currency currency = (Currency) event.getObject();
		product.setCurrency(currency);
	}
}
