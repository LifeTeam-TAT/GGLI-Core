package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.LifePolicyCriteriaItems;
import org.ace.insurance.life.claim.LifePolicyCriteria;
import org.ace.insurance.life.claim.LifePolicySearch;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;

@ManagedBean(name = "LifePolicyNoDialogActionBean")
@ViewScoped
public class LifePolicyNoDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private LifePolicyCriteria lifePolicyCriteria;

	private LazyDataModel<LifePolicySearch> lazyModel;

	private List<LifePolicySearch> policySearchList;

	@PostConstruct
	public void init() {
		resetCriteria();
		if (isExistParam("Actived")) {
			policySearchList = lifePolicyService.findActiveLifePolicy(lifePolicyCriteria);
		} else if (isExistParam("PRODUCTID")) {
			String product = (String) getParam("PRODUCTID");
			policySearchList = lifePolicyService.findLifePolicyForClaimByProduct(product);
		} else {
			policySearchList = lifePolicyService.findLifePolicyForClaimByCriteria(lifePolicyCriteria);
		}

		lazyModel = new LazyDataModelUtil<>(policySearchList);
	}

	public void searchPolicyCriteria() {
		String formId = "policyNoForm";
		if ((lifePolicyCriteria.getLifePolicyCriteriaItems() == null) || (lifePolicyCriteria.getLifePolicyCriteriaItems().toString().equals("Select Criteria"))) {
			addErrorMessage(formId + ":selectLifePolicyCriteria", MessageId.PLEASE_SELECT_POLICY_CRITERIA);
		} else {
			if (isExistParam("Actived")) {
				policySearchList = lifePolicyService.findActiveLifePolicy(lifePolicyCriteria);
			} else {
				policySearchList = lifePolicyService.findLifePolicyForClaimByCriteria(lifePolicyCriteria);
			}
			lazyModel = new LazyDataModelUtil<>(policySearchList);
		}
	}

	public void resetPolicyCriteria() {
		lifePolicyCriteria.setCriteriaValue(null);
		lifePolicyCriteria.setLifePolicyCriteriaItems(null);
		policySearchList = lifePolicyService.findLifePolicyForClaimByCriteria(null);
	}

	public LifePolicyCriteriaItems[] getLifePolicyCriteriaList() {
		return LifePolicyCriteriaItems.values();
	}

	public void resetCriteria() {
		lifePolicyCriteria = new LifePolicyCriteria();
	}

	public void selectLifePolicyNo(LifePolicySearch lifePolicy) {
		putParam("lifePolicy", lifePolicy);
		PrimeFaces.current().dialog().closeDynamic(lifePolicy);
	}

	public LifePolicyCriteria getLifePolicyCriteria() {
		return lifePolicyCriteria;
	}

	public LazyDataModel<LifePolicySearch> getLazyModel() {
		return lazyModel;
	}
}
