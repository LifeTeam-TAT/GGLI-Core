package org.ace.insurance.web.dialog;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.MedicalPolicyCriteriaItems;
import org.ace.insurance.medical.claim.MedicalPolicyCriteria;
import org.ace.insurance.medical.policy.MPL002;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;

@ManagedBean(name = "MedicalPolicyNoDialogActionBean")
@ViewScoped
public class MedicalPolicyNoDialogActionBean extends BaseBean {

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	private MedicalPolicyCriteria medicalPolicyCriteria;
	private LazyDataModel<MPL002> lazyModel;
	private List<MPL002> medicalPolicyList;

	@PostConstruct
	public void init() {
		resetCriteria();
		// String result = getProductByReferenceType(product);
		medicalPolicyList = medicalPolicyService.findMedicalPolicyForClaimByProduct(null);
		lazyModel = new LazyDataModelUtil<MPL002>(medicalPolicyList);
	}

	public void searchPolicyCriteria() {
		medicalPolicyList = medicalPolicyService.findMedicalPolicyForClaimByCriteria(medicalPolicyCriteria);
		lazyModel = new LazyDataModelUtil<MPL002>(medicalPolicyList);
	}

	public void resetPolicyCriteria() {
		medicalPolicyCriteria.setCriteriaValue(null);
		medicalPolicyCriteria.setMedicalPolicyCriteriaItems(null);
		medicalPolicyList = medicalPolicyService.findMedicalPolicyForClaimByCriteria(medicalPolicyCriteria);
		lazyModel = new LazyDataModelUtil<MPL002>(medicalPolicyList);
	}

	public MedicalPolicyCriteriaItems[] getMedicalPolicyCriteriaList() {
		return MedicalPolicyCriteriaItems.values();
	}

	public void resetCriteria() {
		medicalPolicyCriteria = new MedicalPolicyCriteria();
	}

	public void selectMedicalPolicyNo(MPL002 mpl002) {
		MedicalPolicy policy = medicalPolicyService.findMedicalPolicyById(mpl002.getId());
		PrimeFaces.current().dialog().closeDynamic(policy);
	}

	public MedicalPolicyCriteria getMedicalPolicyCriteria() {
		return medicalPolicyCriteria;
	}

	public LazyDataModel<MPL002> getLazyModel() {
		return lazyModel;
	}

}
