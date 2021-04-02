package org.ace.insurance.system.appversion.service.interfaces;

import java.util.List;

import org.ace.insurance.system.appversion.AppVersion;
import org.ace.insurance.system.appversion.MobileType;
import org.ace.java.component.SystemException;

public interface IAppVersionService {
	public List<AppVersion> findAppVersionByTypeAndVersion(MobileType type, double version) throws SystemException;

	public AppVersion findLatestApp(MobileType type) throws SystemException;

	public boolean isExitInstance(MobileType type, double version) throws SystemException;
}
