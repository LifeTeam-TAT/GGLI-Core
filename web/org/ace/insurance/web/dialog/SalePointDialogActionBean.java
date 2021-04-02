package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "SalePointDialogActionBean")
@ViewScoped
public class SalePointDialogActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SalePointService}")
	private ISalePointService salePointService;

	public void setSalePointService(ISalePointService salePointService) {
		this.salePointService = salePointService;
	}

	private List<SalePoint> salePointList;

	@PostConstruct
	public void init() {
		if (isExistParam("BRANCHID")) {
			String branchId = (String) getParam("BRANCHID");
			salePointList = salePointService.findAllSalePointByBranch(branchId);
		} else {
			salePointList = salePointService.findAllSalePointByStatus();
		}
	}

	public List<SalePoint> getSalePointList() {
		return salePointList;
	}

	public void setSalePointList(List<SalePoint> salePointList) {
		this.salePointList = salePointList;
	}

	public void selectSalePoint(SalePoint salePoint) {
		PrimeFaces.current().dialog().closeDynamic(salePoint);
	}

}
