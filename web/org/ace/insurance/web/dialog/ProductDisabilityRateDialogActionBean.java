package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.claim.disabilityPart.ProductDisabilityRate;
import org.ace.insurance.claim.disabilityPart.service.interfaces.IProductDisabilityService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "ProductDisabilityRateDialogActionBean")
@ViewScoped
public class ProductDisabilityRateDialogActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductDisabilityService}")
	private IProductDisabilityService productDisabilityService;

	public void setProductDisabilityService(IProductDisabilityService productDisabilityService) {
		this.productDisabilityService = productDisabilityService;
	}

	private List<ProductDisabilityRate> productDisabilityRateList;

	@PreDestroy
	public void removeParam() {
		removeParam("produtId");
	}

	@PostConstruct
	public void init() {
		String produtId = isExistParam("produtId") ? (String) getParam("produtId") : null;
		if (produtId != null) {
			productDisabilityRateList = productDisabilityService.findAllDisabilityRateByProduct(produtId);
		}
	}

	public void selectDisabiliytRate(ProductDisabilityRate rate) {
		PrimeFaces.current().dialog().closeDynamic(rate);
	}

	public List<ProductDisabilityRate> getProductDisabilityRateList() {
		return productDisabilityRateList;
	}

}
