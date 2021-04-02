package org.ace.insurance.system.notification.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.notification.Notification;
import org.ace.insurance.system.notification.NotificationCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface INotificationDAO {
	public List<Notification> findByCriteria(NotificationCriteria criteria) throws DAOException;
}
