package org.ace.insurance.system.notification.service.interfaces;

import java.util.List;

import org.ace.insurance.system.notification.Notification;
import org.ace.insurance.system.notification.NotificationCriteria;
import org.ace.java.component.SystemException;

public interface INotificationService {
	public List<Notification> findAllNotificationByCriteria(NotificationCriteria criteria) throws SystemException;
}
