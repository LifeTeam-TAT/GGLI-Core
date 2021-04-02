package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.filter.life.LFP001;
import org.ace.insurance.filter.life.LifeFilterCriteria;
import org.ace.insurance.filter.life.LifeFilterCriteria.LifeFilterItem;
import org.ace.insurance.filter.life.interfaces.ILIFE_Filter;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "LifePolicyDialogActionBean")
@ViewScoped
public class LifePolicyDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LIFE_Filter}")
	private ILIFE_Filter filter;

	public void setFilter(ILIFE_Filter filter) {
		this.filter = filter;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private List<LFP001> lifePolicyList;
	private LifeFilterCriteria criteria;
	private String productId;

	@PostConstruct
	public void init() {
		productId = (String) getParam("productId");
		resetCriteria();
	}

	@PreDestroy
	public void destroy() {
		removeParam("productId");
	}

	public void search() {
		lifePolicyList = filter.find(criteria);
	}

	public void resetCriteria() {
		criteria = new LifeFilterCriteria();
		criteria.setProductId(productId);
		lifePolicyList = filter.find(criteria);
	}

	public List<LFP001> getLifePolicyList() {
		return lifePolicyList;
	}

	public LifeFilterCriteria getCriteria() {
		return criteria;
	}

	public LifeFilterItem[] getItems() {
		return LifeFilterItem.values();
	}

	public void selectLifePolicy(LFP001 lfp001) {
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(lfp001.getId());
		PrimeFaces.current().dialog().closeDynamic(lifePolicy);
	}

}
