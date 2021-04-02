package org.ace.insurance.system.notification.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.notification.Notification;
import org.ace.insurance.system.notification.NotificationCriteria;
import org.ace.insurance.system.notification.persistence.interfaces.INotificationDAO;
import org.ace.insurance.system.notification.service.interfaces.INotificationService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "NotificationService")
public class NotificationService extends BaseService implements INotificationService {
	@Resource(name = "NotificationDAO")
	private INotificationDAO notificationDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Notification> findAllNotificationByCriteria(NotificationCriteria criteria) throws SystemException {
		List<Notification> result = null;
		try {
			result = notificationDAO.findByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all Notification by NotificationCriteria", e);
		}
		return result;
	}

}
