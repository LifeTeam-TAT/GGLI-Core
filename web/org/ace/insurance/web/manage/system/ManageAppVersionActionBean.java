/***************************************************************************************
 * @author <<Thet Naing Soe>>
 * @Date 2018-02-01
 * @Version 1.0
 * @Purpose <<To announce system informations to all clients>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.appversion.AppVersion;
import org.ace.insurance.system.appversion.MobileType;
import org.ace.insurance.system.appversion.service.interfaces.IAppVersionService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.interfaces.IDataRepository;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageAppVersionActionBean")
public class ManageAppVersionActionBean extends BaseBean {

	@ManagedProperty(value = "#{DataRepository}")
	private IDataRepository<AppVersion> appVersionRespository;

	public void setAppVersionRespository(IDataRepository<AppVersion> appVersionRespository) {
		this.appVersionRespository = appVersionRespository;
	}

	@ManagedProperty(value = "#{AppVersionService}")
	private IAppVersionService appVersionService;

	public void setAppVersionService(IAppVersionService appVersionService) {
		this.appVersionService = appVersionService;
	}

	private boolean createNew;
	private AppVersion appVersion;
	private List<AppVersion> appVersionList;

	@PostConstruct
	public void init() {
		createNewInstance();
		loadNotifications();
	}

	public void loadNotifications() {
		try {
			appVersionList = appVersionRespository.findAll(AppVersion.class);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void createNewInstance() {
		createNew = true;
		appVersion = new AppVersion();
	}

	public void prepareUpdate(AppVersion appVersion) {
		this.appVersion = appVersion;
		createNew = false;
	}

	public void addNewAppVersion() {
		try {
			if (appVersionService.isExitInstance(appVersion.getType(), appVersion.getAppVersion())) {
				addErrorMessage("This version is already exit for " + appVersion.getType().getLabel() + ".<br/>" + "Version : " + appVersion.getAppVersion());
			} else {
				appVersionRespository.insert(appVersion);
				appVersionList.add(appVersion);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, "Version " + appVersion.getAppVersion());
				createNewInstance();
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateAppVersion() {
		try {
			appVersionRespository.update(appVersion);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, "Version " + appVersion.getAppVersion());
			loadNotifications();
			createNewInstance();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteAppVersion(AppVersion appVersion) {
		try {
			appVersionRespository.delete(appVersion);
			appVersionList.remove(appVersion);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, "Version " + appVersion.getAppVersion());
			createNewInstance();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public EnumSet<MobileType> getMobileTypes() {
		return EnumSet.allOf(MobileType.class);
	}

	public AppVersion getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(AppVersion appVersion) {
		this.appVersion = appVersion;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<AppVersion> getAppVersionList() {
		return appVersionList;
	}

}
