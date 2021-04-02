package org.ace.insurance.web.manage.system;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.configuration.SettingKeys;
import org.ace.insurance.system.configuration.interfaces.ISettingVariableService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "ManageFireClaimSettingsActionBean")
public class ManageFireClaimSettingsActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{SettingVariableService}")
	private ISettingVariableService settingVariableService;

	public void setSettingVariableService(ISettingVariableService settingVariableService) {
		this.settingVariableService = settingVariableService;
	}

	private int daysToInform;
	private int daysToClaim;
	private static final String daysToInformKey = SettingKeys.Fire.NO_OF_VALID_DAYS_TO_INFORM;
	private static final String daysToClaimKey = SettingKeys.Fire.NO_OF_VALID_DAYS_TO_CLAIM;

	@PostConstruct
	public void init() {
		System.out.println("Init Fire Claim Setting.....");
		daysToInform = settingVariableService.getSettingVariable(daysToInformKey, 15);
		daysToClaim = settingVariableService.getSettingVariable(daysToClaimKey, 365);
	}

	public int getDaysToInform() {
		return daysToInform;
	}

	public void setDaysToInform(int daysToInform) {
		this.daysToInform = daysToInform;
	}

	public int getDaysToClaim() {
		return daysToClaim;
	}

	public void setDaysToClaim(int daysToClaim) {
		this.daysToClaim = daysToClaim;
	}

	public void updateSettings() {
		try {
			settingVariableService.setSettingVariable(daysToInformKey, daysToInform);
			settingVariableService.setSettingVariable(daysToClaimKey, daysToClaim);
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "The setting values have been saved.", ""));
			// ret = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}
}
