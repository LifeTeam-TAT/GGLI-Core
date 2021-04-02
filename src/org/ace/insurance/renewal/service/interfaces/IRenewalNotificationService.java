package org.ace.insurance.renewal.service.interfaces;

import java.util.List;

import org.ace.insurance.renewal.RenewalNotification;
import org.ace.insurance.renewal.RenewalNotificationCriteria;

public interface IRenewalNotificationService {
	public List<RenewalNotification> findMotorPolicies(RenewalNotificationCriteria criteria);

	public List<RenewalNotification> findFirePolicies(RenewalNotificationCriteria criteria);

	public List<RenewalNotification> findGroupLifePolicies(String productId, RenewalNotificationCriteria criteria);
}
