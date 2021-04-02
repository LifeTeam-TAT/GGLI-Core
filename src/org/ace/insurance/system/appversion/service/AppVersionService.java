package org.ace.insurance.system.appversion.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.appversion.AppVersion;
import org.ace.insurance.system.appversion.MobileType;
import org.ace.insurance.system.appversion.persistence.interfaces.IAppVersionDAO;
import org.ace.insurance.system.appversion.service.interfaces.IAppVersionService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "AppVersionService")
public class AppVersionService extends BaseService implements IAppVersionService {
	@Resource(name = "AppVersionDAO")
	private IAppVersionDAO appVersionDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AppVersion> findAppVersionByTypeAndVersion(MobileType type, double version) throws SystemException {
		List<AppVersion> result = null;
		try {
			result = appVersionDAO.findByCriteria(type, version);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all AppVersion by Type And Version", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isExitInstance(MobileType type, double version) throws SystemException {
		try {
			List<AppVersion> result = appVersionDAO.findByCriteria(type, version);
			if (result.size() > 0)
				return true;
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to check isExitInstance for AppVersion", e);
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AppVersion findLatestApp(MobileType type) throws SystemException {
		AppVersion appVersion = null;
		try {
			appVersion = appVersionDAO.findLatestAppVersion(type);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find latest AppVersions.", e);
		}
		return appVersion;
	}

}
