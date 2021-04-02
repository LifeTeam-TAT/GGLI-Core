package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.filter.cirteria.CRIA001;
import org.ace.insurance.filter.saleman.SAMN001;
import org.ace.insurance.filter.saleman.interfaces.ISALEMAN_Filter;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.saleman.service.interfaces.ISaleManService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "SalemanDialogActionBean")
@ViewScoped
public class SalemanDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SaleManService}")
	private ISaleManService saleManService;

	public void setSaleManService(ISaleManService saleManService) {
		this.saleManService = saleManService;
	}

	@ManagedProperty(value = "#{SALEMAN_Filter}")
	protected ISALEMAN_Filter filter;
	private CRIA001 saleManCriteria;
	private String criteriaValue;
	private List<SAMN001> salemanList;

	@PostConstruct
	public void init() {
		criteriaValue = "";
		salemanList = filter.find(30);
	}

	public void search() {
		salemanList = filter.find(saleManCriteria, criteriaValue);
	}

	public List<SAMN001> getSalemanList() {
		return salemanList;
	}

	public void selectSaleman(SAMN001 samn001) {
		SaleMan saleman = saleManService.findSaleManById(samn001.getId());
		PrimeFaces.current().dialog().closeDynamic(saleman);
	}

	public CRIA001[] getCriteriaItems() {
		return CRIA001.values();
	}

	public CRIA001 getSaleManCriteria() {
		return saleManCriteria;
	}

	public void setSaleManCriteria(CRIA001 saleManCriteria) {
		this.saleManCriteria = saleManCriteria;
	}

	public String getCriteriaValue() {
		return criteriaValue;
	}

	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	public void setFilter(ISALEMAN_Filter filter) {
		this.filter = filter;
	}
}
