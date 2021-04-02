package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.product.PROGRP001;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "ProductGroupDialogActionBean")
@ViewScoped
public class ProductGroupDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private List<PROGRP001> productGroupList;

	@PostConstruct
	public void init() {
		ProductGroupType productGroupType = (ProductGroupType) getParam("PRODUCTGROUPTYPE");
		productGroupList = productService.findAllByGroupType(productGroupType);

	}

	public List<PROGRP001> getProductGroupList() {
		return productGroupList;
	}

	public void selectProductGroup(PROGRP001 progrp001) {
		ProductGroup productGroup = productService.findProductGroupById(progrp001.getId());
		PrimeFaces.current().dialog().closeDynamic(productGroup);
	}
}
