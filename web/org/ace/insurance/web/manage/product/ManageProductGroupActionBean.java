package org.ace.insurance.web.manage.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.product.NcbRate;
import org.ace.insurance.product.NcbYear;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageProductGroupActionBean")
public class ManageProductGroupActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean createNew;
	private boolean crateNewNcbRate;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private List<ProductGroup> productGroupList;
	private ProductGroup productGroup;
	private NcbRate ncbRate;

	@PostConstruct
	public void init() {
		createNewProductGroup();
		loadProductGroup();
	}

	public void loadProductGroup() {
		productGroupList = productService.findAllProductGroup();
	}

	public void createNewProductGroup() {
		createNew = true;
		crateNewNcbRate = true;
		productGroup = new ProductGroup();
		ncbRate = new NcbRate();
		productGroup.setGroupType(ProductGroupType.LIFE);
		productGroup.setNcbRates(new ArrayList<NcbRate>());
	}

	public void prepareUpdateProductGroup(ProductGroup productGroup) {
		createNew = false;
		this.productGroup = new ProductGroup(productGroup);
	}

	private boolean isEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean validProductGroup() {
		boolean valid = true;
		String formID = "pGroupEntryForm";
		// if(productGroup.getUnderSession13() <= 0) {
		// addErrorMessage(formID + ":undersession13",
		// UIInput.REQUIRED_MESSAGE_ID);
		// valid = false;
		// }
		// if(productGroup.getUnderSession25() <= 0) {
		// addErrorMessage(formID + ":undersession25",
		// UIInput.REQUIRED_MESSAGE_ID);
		// valid = false;
		// }
		if (productGroup.getMaxSumInsured() <= 0) {
			addErrorMessage(formID + ":maxSumInsured", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(productGroup.getName())) {
			addErrorMessage(formID + ":name", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		/*
		 * if (isEmpty(productGroup.getPolicyNoPrefix())) {
		 * addErrorMessage(formID + ":policyNoPrefix",
		 * UIInput.REQUIRED_MESSAGE_ID); valid = false; }
		 */
		if (isEmpty(productGroup.getAccountCode())) {
			addErrorMessage(formID + ":accountCode", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	public void addNewNcbRate() {
		productGroup.getNcbRates().add(ncbRate);
		ncbRate = new NcbRate();
	}

	public void prepareUpdateNcbRate(NcbRate ncbRate) {
		crateNewNcbRate = false;
		this.ncbRate = ncbRate;
	}

	public void updateNcbRate() {
		List<NcbRate> ncbRateList = productGroup.getNcbRates();
		for (NcbRate ncb : ncbRateList) {
			if (ncb.equals(ncbRate)) {
				ncbRateList.remove(ncb);
				break;
			}
		}
		productGroup.getNcbRates().add(ncbRate);
		ncbRate = new NcbRate();
		crateNewNcbRate = true;
	}

	public void deleteNcbRate(NcbRate ncbRate) {
		productGroup.getNcbRates().remove(ncbRate);
	}

	public void addNewProductGroup() {
		try {
			if (validProductGroup()) {
				productService.addNewProductGroup(productGroup);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, productGroup.getName());
				loadProductGroup();
				createNewProductGroup();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateProductGroup() {
		try {
			if (validProductGroup()) {
				productService.updateProductGroup(productGroup);
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, productGroup.getName());
				loadProductGroup();
				createNewProductGroup();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteProductGroup() {
		try {
			productService.deleteProductGroup(productGroup);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, productGroup.getName());
			loadProductGroup();
			createNewProductGroup();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public boolean isCrateNewNcbRate() {
		return crateNewNcbRate;
	}

	public void setCrateNewNcbRate(boolean crateNewNcbRate) {
		this.crateNewNcbRate = crateNewNcbRate;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public List<ProductGroup> getProductGroupList() {
		return productGroupList;
	}

	public ProductGroupType[] getProductGroupTypeSelectItemList() {
		return ProductGroupType.values();
	}

	public NcbYear[] getNcbYearSelectItemList() {
		return NcbYear.values();
	}

	public NcbRate getNcbRate() {
		return ncbRate;
	}

	public void setNcbRate(NcbRate ncbRate) {
		this.ncbRate = ncbRate;
	}
}
