package org.ace.insurance.web;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "MenuActionBean")
public class MenuActionBean extends BaseBean {

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	public void sendInsureType(String insuranceType) {
		putParam("InsuranceType", InsuranceType.valueOf(insuranceType));
	}

	public void outjectEndowmentProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getPublicLifeID());
		putParam("product", product);
	}

	public void outjectShortTermEndowmentProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getShortTermEndowmentID());
		putParam("product", product);
	}

	public void outjectSinglePremiumEndowmentLifeProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getSinglePremiumEndowmentLifeID());
		putParam("product", product);
	}

	public void outjectSinglePremiumCreditLifeProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getSinglePremiumCreditLifeID());
		putParam("product", product);
	}

	public void outjectShortTermSinglePremiumCreditLifeProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getShortTermSinglePremiumCreditLifeID());
		putParam("product", product);
	}

	public void outjectGroupLifeProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getGroupLifeID());
		putParam("product", product);
	}

	public void outjectSnakeBiteProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getSnakeBiteID());
		putParam("product", product);
	}

	public void outjectSportManProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getSportmanID());
		putParam("product", product);
	}

	public void outjectPersonalAccidentProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getPersonalAccidentID());
		putParam("product", product);
	}

	public void outjectFarmerProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getFarmerId());
		putParam("product", product);
	}

	public void outjectPublicTermLifeProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getPublicTermLifeId());
		putParam("product", product);
	}

	public void outjectSimpleLifeProduct() {
		Product product = productService.findProductById(KeyFactorChecker.getSimpleLifeId());
		putParam("product", product);
	}

	public String getStudentLifeUI() {
		Product product = productService.findProductById(KeyFactorChecker.getStudentLifeID());
		putParam("PRODUCT", product);
		putParam("isNew", true);
		return "addNewStudentLifeProposal";
	}

	public String getMicroHealthUI() {
		putParam("HEALTHTYPE", HealthType.MICROHEALTH);
		return "medicalProposal";
	}

	public String getCriticalIllnessUI() {
		putParam("HEALTHTYPE", HealthType.CRITICALILLNESS);
		return "medicalProposal";
	}

	public String getHealth() {
		putParam("HEALTHTYPE", HealthType.HEALTH);
		return "medicalProposal";
	}

	public String getGroupMicroHealthUI() {
		return "groupMicroHealthProposal";
	}

	public String getDetailsGroupMicroHealthUI() {
		return "detailsGroupMicroHealth";
	}
}
