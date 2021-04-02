package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.LifePolicyCriteriaItems;
import org.ace.insurance.life.LifeEndowmentPolicySearch;
import org.ace.insurance.life.claim.LifePolicyCriteria;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "PublicLifePolicyDialogActionBean")
@ViewScoped
public class PublicLifePolicyDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private LifePolicyCriteria lifePolicyCriteria;
	private List<LifeEndowmentPolicySearch> lifeEndowSearchList;

	private String receiptNo;
	boolean isMigrate;

	@PostConstruct
	public void init() {
		lifePolicyCriteria = new LifePolicyCriteria();
		isMigrate = true;
		resetPolicyCriteria();
	}

	public void searchPolicyCriteria() {
		isMigrate = true;
		String formId = "policyNoForm";
		if ((lifePolicyCriteria.getLifePolicyCriteriaItems() == null) || (lifePolicyCriteria.getLifePolicyCriteriaItems().toString().equals("Select Criteria"))) {
			addErrorMessage(formId + ":selectLifePolicyCriteria", MessageId.PLEASE_SELECT_POLICY_CRITERIA);
		} else {
			lifeEndowSearchList = lifePolicyService.findPublicLifePolicyBycriteria(lifePolicyCriteria, isMigrate);
		}
	}

	public void resetPolicyCriteria() {
		lifePolicyCriteria.setCriteriaValue(null);
		lifePolicyCriteria.setLifePolicyCriteriaItems(null);
		lifeEndowSearchList = lifePolicyService.findPublicLifePolicyBycriteria(lifePolicyCriteria, isMigrate);
	}

	public LifePolicyCriteriaItems[] getLifePolicyCriteriaList() {
		return LifePolicyCriteriaItems.values();
	}

	public void selectLifePolicyNo(LifeEndowmentPolicySearch lifePolicy) {
		putParam("lifePolicy", lifePolicy);
		PrimeFaces.current().dialog().closeDynamic(lifePolicy);
		// PaymentDTO paymentDTO =
		// lifePolicyService.findNoPaidPaymentByReferenceNo(lifePolicy.getPolicyNo());
		// boolean needToBillCollect = false;
		// loop: for (PaymentDTO paymentDTO : paymentDTOList) {
		// if (!paymentDTO.isComplete()) {
		// needToBillCollect = true;
		// receiptNo = paymentDTO.getReceiptNo();
		// break loop;
		// }
		// }
		// if (needToBillCollect) {
		// PrimeFaces.current().executeScript("PF('PF('dlg2').show();");
		// } else {
		// putParam("lifePolicy", lifePolicy);
		// PrimeFaces.current().dialog().closeDynamic(lifePolicy);
		// }

	}

	public List<LifeEndowmentPolicySearch> getLifeEndowSearchList() {
		return lifeEndowSearchList;
	}

	public LifePolicyCriteria getLifeEndowmentCriteria() {
		return lifePolicyCriteria;
	}

	public void setLifeEndowmentCriteria(LifePolicyCriteria lifeEndowmentCriteria) {
		this.lifePolicyCriteria = lifeEndowmentCriteria;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public boolean isMigrate() {
		return isMigrate;
	}

}
