package org.ace.insurance.system.appversion.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.appversion.AppVersion;
import org.ace.insurance.system.appversion.MobileType;
import org.ace.insurance.system.appversion.persistence.interfaces.IAppVersionDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("AppVersionDAO")
public class AppVersionDAO extends BasicDAO implements IAppVersionDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AppVersion> findByCriteria(MobileType type, double version) throws DAOException {
		List<AppVersion> result = null;
		try {
			Query q = em.createNamedQuery("AppVersion.findByTypeAndVersion");
			q.setParameter("type", type);
			q.setParameter("appVersion", version);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find AppVersion by Type and Version", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AppVersion findLatestAppVersion(MobileType type) throws DAOException {
		AppVersion result = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT a FROM AppVersion a WHERE a.appVersion = (SELECT MAX(v.appVersion) FROM AppVersion v WHERE v.type = :type) AND a.type = :types");
			Query q = em.createQuery(buffer.toString());
			q.setParameter("type", type);
			q.setParameter("types", type);
			result = (AppVersion) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			result = new AppVersion();
		} catch (PersistenceException pe) {
			throw translate("Failed to find latest AppVersion by Type", pe);
		}
		return result;
	}
}
