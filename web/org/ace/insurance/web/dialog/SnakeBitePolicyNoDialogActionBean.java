package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.SnakeBitePolicyNoCriteria;
import org.ace.insurance.life.snakebite.SnakeBitePolicyNoCriteriaItems;
import org.ace.insurance.life.snakebite.SnakeBitePolicySearch;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;

@ManagedBean(name = "SnakeBitePolicyNoDialogActionBean")
@ViewScoped
public class SnakeBitePolicyNoDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SnakeBitePolicyService}")
	private ISnakeBitePolicyService snakeBitePolicyService;

	public void setSnakeBitePolicyService(ISnakeBitePolicyService snakeBitePolicyService) {
		this.snakeBitePolicyService = snakeBitePolicyService;
	}

	private SnakeBitePolicy criteria;
	private SnakeBitePolicyNoCriteria snakeBitePolicyNoCriteria;
	private LazyDataModel<SnakeBitePolicySearch> lazyModel;
	private List<SnakeBitePolicySearch> policySearchList;

	@PostConstruct
	public void init() {
		resetSnakeBitePolicyNoCriteria();
		searchSnakeBitePolicyNoCriteria();
	}

	public void searchSnakeBitePolicyNoCriteria() {
		policySearchList = snakeBitePolicyService.findSnakeBitePolicyNoByCriteria(snakeBitePolicyNoCriteria);
		lazyModel = new LazyDataModelUtil(policySearchList);
	}

	public SnakeBitePolicyNoCriteriaItems[] getSnakeBitePolicyNoCriteriaList() {
		return SnakeBitePolicyNoCriteriaItems.values();
	}

	public void resetSnakeBitePolicyNoCriteria() {
		snakeBitePolicyNoCriteria = new SnakeBitePolicyNoCriteria();
		snakeBitePolicyNoCriteria.setCriteriaValue(null);
		snakeBitePolicyNoCriteria.setSnakeBitePolicyNoCriteriaItems(null);
		policySearchList = snakeBitePolicyService.findSnakeBitePolicyNoByCriteria(null);
		lazyModel = new LazyDataModelUtil(policySearchList);
	}

	public void selectSnakeBitePolicyNo(SnakeBitePolicySearch snakeBitePolicyNo) {
		putParam("snakeBitePolicyNo", snakeBitePolicyNo);
		PrimeFaces.current().dialog().closeDynamic(snakeBitePolicyNo);
	}

	public SnakeBitePolicyNoCriteria getSnakeBitePolicyNoCriteria() {
		return snakeBitePolicyNoCriteria;
	}

	public SnakeBitePolicy getCriteria() {
		return criteria;
	}

	public void setCriteria(SnakeBitePolicy criteria) {
		this.criteria = criteria;
	}

	public LazyDataModel<SnakeBitePolicySearch> getLazyModel() {
		return lazyModel;
	}
}
