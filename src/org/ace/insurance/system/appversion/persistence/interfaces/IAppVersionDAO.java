package org.ace.insurance.system.appversion.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.appversion.AppVersion;
import org.ace.insurance.system.appversion.MobileType;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAppVersionDAO {
	public List<AppVersion> findByCriteria(MobileType type, double version) throws DAOException;

	public AppVersion findLatestAppVersion(MobileType type) throws DAOException;
}
