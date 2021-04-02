/***************************************************************************************
 * @author <<Thet Naing Soe>>
 * @Date 2018-02-02
 * @Version 1.0
 * @Purpose <<To push notification to all clients>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.notification.Notification;
import org.ace.insurance.system.notification.NotificationCriteria;
import org.ace.insurance.system.notification.service.interfaces.INotificationService;
import org.ace.insurance.web.common.PeriodType;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.interfaces.IDataRepository;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.ace.ws.fcm.FcmServer;
import org.primefaces.PrimeFaces;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

@ManagedBean(name = "ManagePushNotificationActionBean")
@ViewScoped
public class ManagePushNotificationActionBean extends BaseBean {

	@ManagedProperty(value = "#{DataRepository}")
	private IDataRepository<Notification> notiRespository;

	public void setNotiRespository(IDataRepository<Notification> notiRespository) {
		this.notiRespository = notiRespository;
	}

	@ManagedProperty(value = "#{NotificationService}")
	private INotificationService notificationService;

	public void setNotificationService(INotificationService notificationService) {
		this.notificationService = notificationService;
	}

	private int period;
	private int ttl;
	private PeriodType periodType;
	private Notification notification;
	private NotificationCriteria criteria;
	private List<Notification> notificationList;

	@PostConstruct
	public void init() {
		createNewInstance();
		search();
	}

	public void search() {
		try {
			notificationList = notificationService.findAllNotificationByCriteria(criteria);
			if (notificationList.size() == 0) {
				addInfoMessage("There is no Data!");
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void createNewInstance() {
		criteria = new NotificationCriteria();
		criteria.setPush(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 14);
		criteria.setEndDate(cal.getTime());
		search();
	}

	public void preparePushNotification(Notification notification) {
		this.notification = notification;
		ttl = 0;
		period = 1;
		periodType = PeriodType.WEEK;
		PrimeFaces.current().executeScript("PF('confirmPushNotiDialog').show();");
	}

	public void push() throws JSONException {
		try {
			if (validateTTL()) {
				// For PushNotification
				JSONObject noti = new JSONObject();
				noti.put("title", notification.getTitle().replaceAll("\\<.*?\\>", ""));
				noti.put("body", notification.getBody().replaceAll("\\<.*?\\>", ""));
				// For Custom Data
				JSONObject customData = new JSONObject();
				customData.put("id", notification.getId());
				customData.put("sound", "Default");
				FcmServer.send_FCM_Notification(noti, customData, ttl);
				if (!notification.isFinishedPush()) {
					notification.setFinishedPush(true);
					notiRespository.update(notification);
					search();
				}
				addInfoMessage(notification.getTitle() + " is successfully pushed.");
				PrimeFaces.current().executeScript("PF('confirmPushNotiDialog').hide();");
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	private boolean validateTTL() {
		ttl = 0;
		switch (periodType) {
			case WEEK:
				if (period > 4) {
					addErrorMessage("confirmPushNotiDialogForm:period", MessageId.VALIDATE_TTL_PERIOD, "4 Weeks");
					return false;
				} else
					ttl = period * (60 * 60 * 24 * 7);
				break;
			case DAY:
				if (period > 7) {
					addErrorMessage("confirmPushNotiDialogForm:period", MessageId.VALIDATE_TTL_PERIOD, "7 Days");
					return false;
				} else
					ttl = period * (60 * 60 * 24);
				break;
			case HOUR:
				if (period > 24) {
					addErrorMessage("confirmPushNotiDialogForm:period", MessageId.VALIDATE_TTL_PERIOD, "24 Hours");
					return false;
				} else
					ttl = period * (60 * 60);
				break;
			case MINUTE:
				if (period > 60) {
					addErrorMessage("confirmPushNotiDialogForm:period", MessageId.VALIDATE_TTL_PERIOD, "60 Minutes");
					return false;
				} else
					ttl = period * (60);
				break;
			default:
				break;
		}
		return true;
	}

	public PeriodType[] getNotiPeriodTypes() {
		return new PeriodType[] { PeriodType.WEEK, PeriodType.DAY, PeriodType.HOUR, PeriodType.MINUTE };
	}

	/* Getters and Setters */

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public List<Notification> getNotificationList() {
		return notificationList;
	}

	public NotificationCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(NotificationCriteria criteria) {
		this.criteria = criteria;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

}
