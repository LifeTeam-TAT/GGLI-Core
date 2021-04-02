package org.ace.insurance.renewal.persistence.interfaces;

import java.util.List;

import org.ace.insurance.renewal.RenewalNotification;
import org.ace.insurance.renewal.RenewalNotificationCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface IRenewalNotificationDAO {

	public List<RenewalNotification> findMotorPolicies(RenewalNotificationCriteria criteria) throws DAOException;

	public List<RenewalNotification> findFirePolicies(RenewalNotificationCriteria criteria) throws DAOException;

	public List<RenewalNotification> findGroupLifePolicies(String productId, RenewalNotificationCriteria criteria) throws DAOException;
}
