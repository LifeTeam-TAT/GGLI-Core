package org.ace.insurance.web.manage.numberReference;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.numberReference.service.interfaces.INumberReferenceService;
import org.ace.java.web.common.BaseBean;

@ManagedBean(name = "NumberReferenceActionBean")
@ViewScoped
public class NumberReferenceActionBean extends BaseBean {

	@ManagedProperty(value = "#{NumberReferenceService}")
	private INumberReferenceService numberReferenceService;

	public void setNumberReferenceService(INumberReferenceService numberReferenceService) {
		this.numberReferenceService = numberReferenceService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private String oldNumber;
	private String newNumber;
	private LifePolicy lifePolicy;

	public void search() {
		if (null != oldNumber && !oldNumber.isEmpty()) {
			lifePolicy = null;
			this.newNumber = numberReferenceService.findNewNumberReferenceByOldNumber(oldNumber);
			if (null != newNumber && !newNumber.isEmpty()) {
				lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(newNumber);
			}
		} else if (null != newNumber || !newNumber.isEmpty()) {
			lifePolicy = null;
			this.oldNumber = numberReferenceService.findNewNumberReferenceByNewNumber(newNumber);
			if (null != oldNumber && !oldNumber.isEmpty()) {
				lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(oldNumber);
			}
		}

	}

	public void resetOldNumber() {
		this.oldNumber = null;
	}

	public void resetNewNumber() {
		this.newNumber = null;
	}

	public void reset() {
		this.oldNumber = null;
		this.newNumber = null;
		lifePolicy = null;
	}

	public String getOldNumber() {
		return oldNumber;
	}

	public void setOldNumber(String oldNumber) {
		this.oldNumber = oldNumber;
	}

	public String getNewNumber() {
		return newNumber;
	}

	public void setNewNumber(String newNumber) {
		this.newNumber = newNumber;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

}
