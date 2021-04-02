package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuredProductGroup;
import org.ace.insurance.system.common.coinsurancecompany.service.interfaces.ICoinsuranceCompanyService;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.jboss.resteasy.logging.Logger;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ManageCoinsuranceCompanyActionBean")
public class ManageCoinsuranceCompanyActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(ManageCoinsuranceCompanyActionBean.class);
	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{CoinsuranceCompanyService}")
	private ICoinsuranceCompanyService coinsuranceCompanyService;

	public void setCoinsuranceCompanyService(ICoinsuranceCompanyService coinsuranceCompanyService) {
		this.coinsuranceCompanyService = coinsuranceCompanyService;
	}

	private boolean createNew;
	private CoinsuranceCompany coinsuranceCompany;
	private List<CoinsuranceCompany> coinsuranceCompanies;
	private double enteredPercent;
	private double commission;
	private CoinsuredProductGroup updatedCoinsuredPGroup;
	private List<ProductGroup> productGroupList;
	private ProductGroup selectedProductGroup;
	private String criteria;

	@PostConstruct
	public void init() {
		createNewCompany();
		loadCompany();
		productGroupList = productService.findAllProductGroup();
	}

	private void loadCompany() {
		coinsuranceCompanies = coinsuranceCompanyService.findAll();
	}

	public List<ProductGroup> getProductGroupList() {
		return productGroupList;
	}

	public void createNewCompany() {
		coinsuranceCompany = new CoinsuranceCompany();
		createNew = true;
	}

	public void prepareUpdateCoinsuranceCompany(CoinsuranceCompany coinsuranceCompany) {
		createNew = false;
		this.coinsuranceCompany = coinsuranceCompany;
	}

	public void prepareUpdateCoinsuranceProductGroup(CoinsuredProductGroup coinsuredProductGroup) {
		this.updatedCoinsuredPGroup = coinsuredProductGroup;
		this.enteredPercent = coinsuredProductGroup.getPrecentage();
		this.commission = coinsuredProductGroup.getCommissionPercent();
		this.selectedProductGroup = coinsuredProductGroup.getProductGroup();
	}

	public void updateCoinsuredProductGroup() {
		ProductGroup selectedPG = validateProductGroupPercentAvailabilityForUpdate();
		if (selectedPG != null) {
			CoinsuredProductGroup coinsuredPGroup = new CoinsuredProductGroup();
			coinsuredPGroup.setPrecentage(enteredPercent);
			coinsuredPGroup.setCommissionPercent(commission);
			coinsuredPGroup.setProductGroup(selectedPG);
			coinsuredPGroup.setCoinsuranceCompany(coinsuranceCompany);
			int index = coinsuranceCompany.getCoinsuredProductGroups().indexOf(updatedCoinsuredPGroup);
			coinsuranceCompany.getCoinsuredProductGroups().set(index, coinsuredPGroup);
			enteredPercent = 0;
			commission = 0;
		}
	}

	public ProductGroup validateProductGroupPercentAvailabilityForUpdate() {
		boolean ret = false;
		ProductGroup selectedPG = null;
		PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance();
		try {
			if (coinsuranceCompany.getCoinsuredProductGroups() == null || coinsuranceCompany.getCoinsuredProductGroups().isEmpty()) {
				addWranningMessage("Please select product group.");
				requestContext.getCallbackParams().put("validName", false);
			} else if (!(enteredPercent > 0)) {
				addWranningMessage("The entered percent value must be greater than 0.");
				requestContext.getCallbackParams().put("validName", false);
			} else if (!(enteredPercent < 100)) {
				addWranningMessage("The entered percent value must not be greater than 100.");
				requestContext.getCallbackParams().put("validName", false);
			} else {
				for (ProductGroup productGroup : getProductGroupList()) {
					if (productGroup.getId().equals(selectedProductGroup.getId())) {
						selectedPG = productGroup;
					}
				}
				ret = coinsuranceCompanyService.isPercentageAvailable(selectedPG, enteredPercent, coinsuranceCompany);
				if (!ret) {
					selectedPG = null;
					addWranningMessage("The entered percent value is not available for this product group.");
					requestContext.getCallbackParams().put("validName", false);
				} else {
					requestContext.getCallbackParams().put("validName", false);
				}
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return selectedPG;
	}

	public CoinsuranceCompany getCoinsuranceCompany() {
		return coinsuranceCompany;
	}

	public void setCoinsuranceCompany(CoinsuranceCompany coinsuranceCompany) {
		this.coinsuranceCompany = coinsuranceCompany;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public double getEnteredPercent() {
		return enteredPercent;
	}

	public void setEnteredPercent(double enteredPercent) {
		logger.debug("Entered Percentage" + enteredPercent);
		this.enteredPercent = enteredPercent;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public CoinsuredProductGroup getUpdatedCoinsuredPGroup() {
		return updatedCoinsuredPGroup;
	}

	public void setUpdatedCoinsuredPGroup(CoinsuredProductGroup updatedCoinsuredPGroup) {
		this.updatedCoinsuredPGroup = updatedCoinsuredPGroup;
	}

	public void addNewCoinsuranceCompany() {
		try {

			if (coinsuranceCompany.getCoinsuredProductGroups() == null || coinsuranceCompany.getCoinsuredProductGroups().isEmpty()) {
				addInfoMessage(null, MessageId.PRODUCT_GROUP_REQUIRED);
			} else {
				coinsuranceCompanyService.add(coinsuranceCompany);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, coinsuranceCompany.getName());
				createNewCompany();
			}
			loadCompany();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateCoinsuranceCompany() {
		try {
			Township township = coinsuranceCompany.getAddress().getTownship();
			if (township == null) {
				addInfoMessage(null, MessageId.REQUIRED_TOWNSHIP);
			} else if (coinsuranceCompany.getCoinsuredProductGroups() == null || coinsuranceCompany.getCoinsuredProductGroups().isEmpty()) {
				addInfoMessage(null, MessageId.PRODUCT_GROUP_REQUIRED);
			} else {
				coinsuranceCompanyService.update(coinsuranceCompany);
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, coinsuranceCompany.getName());
				createNewCompany();
			}
			loadCompany();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteCoinsuranceCompany() {
		try {
			coinsuranceCompanyService.delete(coinsuranceCompany);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, coinsuranceCompany.getName());
			createNewCompany();
			loadCompany();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public ProductGroup validateProductGroupPercentAvailability() {
		boolean ret = false;
		ProductGroup selectedPG = null;
		PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance();
		double percentage = 0.0;
		try {

			if (commission < 0 || commission > 100) {
				addWranningMessage("The commission percent must between 0 and 100.");
				requestContext.getCallbackParams().put("validName", false);
			} else if (!(enteredPercent > 0)) {
				addWranningMessage("The entered percent value must be greater than 0.");
				requestContext.getCallbackParams().put("validName", false);
			} else if (!(enteredPercent < 100)) {
				addWranningMessage("The entered percent value must not be greater than 100.");
				requestContext.getCallbackParams().put("validName", false);
			} else {
				for (ProductGroup productGroup : getProductGroupList()) {
					if (productGroup.getId().equals(selectedProductGroup.getId())) {
						selectedPG = productGroup;
					}
				}
				if (coinsuranceCompany.getCoinsuredProductGroups() != null) {
					for (CoinsuredProductGroup coPGroup : coinsuranceCompany.getCoinsuredProductGroups()) {
						if (coPGroup.getProductGroup().getId().equals(selectedPG.getId())) {
							percentage = coPGroup.getPrecentage() + enteredPercent;
							break;
						}
					}
				}
				if (!(percentage > 100)) {
					if (percentage != 0) {
						ret = coinsuranceCompanyService.isPercentageAvailable(selectedPG, percentage, coinsuranceCompany);
					} else {
						ret = coinsuranceCompanyService.isPercentageAvailable(selectedPG, enteredPercent, coinsuranceCompany);
					}
					if (!ret) {
						selectedPG = null;
						requestContext.getCallbackParams().put("validName", false);
						addWranningMessage("The entered percent value is not available for this product group.");
					} else {
						requestContext.getCallbackParams().put("validName", false);
					}
				} else {
					selectedPG = null;
					addWranningMessage("The entered percent value must not be greater than 100.");
					requestContext.getCallbackParams().put("validName", false);
				}

			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return selectedPG;
	}

	public void addNewCoinsuredProductGroup(ActionEvent event) {
		try {

			logger.debug("Entered Percennt:::::::" + enteredPercent);
			Boolean flag = false;
			ProductGroup productGroup = validateProductGroupPercentAvailability();
			if (productGroup != null) {
				if (coinsuranceCompany.getCoinsuredProductGroups() != null) {
					for (CoinsuredProductGroup coPGroup : coinsuranceCompany.getCoinsuredProductGroups()) {
						if (coPGroup.getProductGroup().getId().equals(productGroup.getId())) {
							coPGroup.setProductGroup(productGroup);
							double percentage = coPGroup.getPrecentage() + enteredPercent;
							coPGroup.setPrecentage(percentage);
							flag = true;
							break;
						}
					}
				}
				if (!flag) {
					CoinsuredProductGroup coinsuredPGroup = new CoinsuredProductGroup();
					coinsuredPGroup.setPrecentage(enteredPercent);
					coinsuredPGroup.setCommissionPercent(commission);
					coinsuredPGroup.setProductGroup(productGroup);
					coinsuredPGroup.setCoinsuranceCompany(coinsuranceCompany);
					coinsuranceCompany.addConinsuranceProductGroup(coinsuredPGroup);
				}
			}
			selectedProductGroup = null;
			enteredPercent = 0;
			commission = 0;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public List<CoinsuranceCompany> getCoinsuranceCompanies() {
		return coinsuranceCompanies;
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		coinsuranceCompany.getAddress().setTownship(township);
	}

	public ProductGroup getSelectedProductGroup() {
		return selectedProductGroup;
	}

	public void setSelectedProductGroup(ProductGroup selectedProductGroup) {
		this.selectedProductGroup = selectedProductGroup;
	}

	public void searchCoinsuranceCompany() {
		coinsuranceCompanies = coinsuranceCompanyService.findByCriteria(criteria);
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public void setCoinsuranceCompanies(List<CoinsuranceCompany> coinsuranceCompanies) {
		this.coinsuranceCompanies = coinsuranceCompanies;
	}

	public void setProductGroupList(List<ProductGroup> productGroupList) {
		this.productGroupList = productGroupList;
	}
}
