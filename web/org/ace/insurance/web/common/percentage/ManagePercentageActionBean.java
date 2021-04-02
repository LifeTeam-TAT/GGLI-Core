package org.ace.insurance.web.common.percentage;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.service.interfaces.IPercentageService;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManagePercentageActionBean")
public class ManagePercentageActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{PercentageService}")
	private IPercentageService percentageService;

	public IPercentageService getPercentageService() {
		return percentageService;
	}

	public void setPercentageService(IPercentageService percentageService) {
		this.percentageService = percentageService;
	}

	@ManagedProperty(value = "#{RelationShipTypeService}")
	private IRelationShipTypeService relationshipService;

	private Percentage percentage;
	private Product product;
	private List<Percentage> percentageInfoList;
	private List<Product> productsList;
	private List<Percentage> filterpercentageInfoList;
	private InsuranceType insuranceType;
	private List<RelationShipType> relationshiplist;
	private boolean isSportMan;
	private boolean isPersonalAccident;
	private boolean isFarmer;
	private boolean isSnakeBite;
	private boolean isGroupLife;
	private boolean isEndownmentLife;
	private boolean isPublicTermLife;
	private boolean isMonthBase;
	private boolean isShortTermEndowment;
	private boolean createNew;

	private Percentage oldPercentage;

	@PostConstruct
	public void init() {
		createNewPercentageInfo();
		loadPercentageInfo();

	}

	public void loadPercentageInfo() {
		productsList = productService.findAllProduct();
		relationshiplist = relationshipService.findAllRelationShipType();
		percentageInfoList = percentageService.findAllPercentage();

	}

	public void createNewPercentageInfo() {
		createNew = true;
		percentage = new Percentage();
	}

	public void prepareUpdatePercentage(Percentage percentage) {
		createNew = false;
		this.percentage = percentage;
	}

	public void addNewPercentage() {
		try {
			if (isOldData(percentage)) {
				this.oldPercentage.setPercent(percentage.getPercent());
				percentageService.update(oldPercentage);
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, percentage.getPercent());
			} else {
				percentageService.insert(percentage);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, percentage.getPercent());
			}
			createNewPercentageInfo();
			loadPercentageInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadPercentageInfo();
	}

	private boolean isOldData(Percentage percentage) {
		boolean result = false;

		for (Percentage old : percentageInfoList) {
			if (old.getProduct().equals(percentage.getProduct()) && old.getRelationshiptype().equals(percentage.getRelationshiptype())) {
				result = true;
				this.oldPercentage = old;
			}
		}
		return result;
	}

	public void updatePercentage() {
		try {
			percentageService.update(percentage);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, percentage.getPercent());
			createNewPercentageInfo();
			loadPercentageInfo();

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadPercentageInfo();
	}

	public void deletePercentage(Percentage percentage) {
		try {
			percentageService.delete(percentage);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, percentage.getPercent());
			createNewPercentageInfo();
			loadPercentageInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadPercentageInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
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

	public void changeProduct(AjaxBehaviorEvent e) {
		loadRenderValues();
	}

	private void loadRenderValues() {
		if (null != product) {
			isFarmer = KeyFactorChecker.isFarmer(product);
			isSportMan = KeyFactorChecker.isSportMan(product);
			isPersonalAccident = KeyFactorChecker.isPersonalAccident(product);
			isShortTermEndowment = KeyFactorChecker.isShortTermEndowment(product.getId());
			isSnakeBite = KeyFactorChecker.isSnakeBite(product);
			isGroupLife = KeyFactorChecker.isGroupLife(product);
			isEndownmentLife = KeyFactorChecker.isPublicLife(product);
			isPublicTermLife = KeyFactorChecker.isPublicTermLife(product);
			
		}
		isMonthBase = isPersonalAccident;
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public void setProductsList(List<Product> productsList) {
		this.productsList = productsList;
	}

	public List<RelationShipType> getRelationshiplist() {
		return relationshiplist;
	}

	public void setRelationshiplist(List<RelationShipType> relationshiplist) {
		this.relationshiplist = relationshiplist;
	}

	public Percentage getPercentage() {
		return percentage;
	}

	public void setPercentage(Percentage percentage) {
		this.percentage = percentage;
	}

	public IRelationShipTypeService getRelationshipService() {
		return relationshipService;
	}

	public void setRelationshipService(IRelationShipTypeService relationshipService) {
		this.relationshipService = relationshipService;
	}

	public boolean isSportMan() {
		return isSportMan;
	}

	public void setSportMan(boolean isSportMan) {
		this.isSportMan = isSportMan;
	}

	public boolean isPersonalAccident() {
		return isPersonalAccident;
	}

	public void setPersonalAccident(boolean isPersonalAccident) {
		this.isPersonalAccident = isPersonalAccident;
	}

	public boolean isFarmer() {
		return isFarmer;
	}

	public void setFarmer(boolean isFarmer) {
		this.isFarmer = isFarmer;
	}

	public boolean isSnakeBite() {
		return isSnakeBite;
	}

	public void setSnakeBite(boolean isSnakeBite) {
		this.isSnakeBite = isSnakeBite;
	}

	public boolean isGroupLife() {
		return isGroupLife;
	}

	public void setGroupLife(boolean isGroupLife) {
		this.isGroupLife = isGroupLife;
	}

	public boolean isEndownmentLife() {
		return isEndownmentLife;
	}

	public void setEndownmentLife(boolean isEndownmentLife) {
		this.isEndownmentLife = isEndownmentLife;
	}

	public boolean isPublicTermLife() {
		return isPublicTermLife;
	}

	public void setPublicTermLife(boolean isPublicTermLife) {
		this.isPublicTermLife = isPublicTermLife;
	}

	public boolean isMonthBase() {
		return isMonthBase;
	}

	public void setMonthBase(boolean isMonthBase) {
		this.isMonthBase = isMonthBase;
	}

	public boolean isShortTermEndowment() {
		return isShortTermEndowment;
	}

	public void setShortTermEndowment(boolean isShortTermEndowment) {
		this.isShortTermEndowment = isShortTermEndowment;
	}

	public IProductService getProductService() {
		return productService;
	}

	public List<Percentage> getPercentageInfoList() {
		return percentageInfoList;
	}

	public void setPercentageInfoList(List<Percentage> percentageInfoList) {
		this.percentageInfoList = percentageInfoList;
	}

	public List<Percentage> getFilterpercentageInfoList() {
		return filterpercentageInfoList;
	}

	public void setFilterpercentageInfoList(List<Percentage> filterpercentageInfoList) {
		this.filterpercentageInfoList = filterpercentageInfoList;
	}

}
