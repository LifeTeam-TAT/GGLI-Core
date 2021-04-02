/***************************************************************************************
 * @author <<Thet Naing Soe>>
 * @Date 2018-02-01
 * @Version 1.0
 * @Purpose <<To announce system informations to all clients>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.notification.Notification;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.interfaces.IDataRepository;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;;

@ViewScoped
@ManagedBean(name = "ManageNotificationActionBean")
public class ManageNotificationActionBean extends BaseBean {

	@ManagedProperty(value = "#{DataRepository}")
	private IDataRepository<Notification> notiRespository;

	public void setNotiRespository(IDataRepository<Notification> notiRespository) {
		this.notiRespository = notiRespository;
	}

	private String title = "";
	private boolean isBold;
	private boolean isItalic;
	private boolean isUnderline;
	private boolean createNew;
	private Notification notification;
	private List<Notification> notificationList;

	@PostConstruct
	public void init() {
		createNewInstance();
		loadNotifications();
		createNew = true;
	}

	public void loadNotifications() {
		try {
			notificationList = notiRespository.findAll(Notification.class);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void createNewInstance() {
		// createNew = true;
		notification = new Notification();
		this.title = "";
		isBold = false;
		isUnderline = false;
		isItalic = false;
	}

	public void prepareUpdateNotification(Notification notification) {
		this.notification = notification;
		createNew = false;
		this.title = notification.getTitle();
		String notiTitle = "";
		if (!notification.getTitle().isEmpty()) {
			notiTitle = notification.getTitle();
			isBold = false;
			isUnderline = false;
			isItalic = false;
			if (notiTitle.contains("<b>")) {
				isBold = true;
				notiTitle = notiTitle.replace("<b>", "");
				notiTitle = notiTitle.replace("</b>", "");
			}
			if (notiTitle.contains("<u>")) {
				isUnderline = true;
				notiTitle = notiTitle.replace("<u>", "");
				notiTitle = notiTitle.replace("</u>", "");
			}
			if (notiTitle.contains("<i>")) {
				isItalic = true;
				notiTitle = notiTitle.replace("<i>", "");
				notiTitle = notiTitle.replace("</i>", "");
			}
		}
		this.notification.setTitle(notiTitle);
		// this.notification.setBody(notification.getBody());
	}

	public void addNewNotification() {
		try {
			if (this.title != null && !this.title.isEmpty())
				notification.setTitle(this.title);
			notiRespository.insert(notification);
			notificationList.add(notification);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, notification.getTitle());
			createNewInstance();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateNotification() {
		try {
			if (this.title != null && !this.title.isEmpty())
				notification.setTitle(this.title);
			notiRespository.update(notification);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, notification.getTitle());
			loadNotifications();
			createNewInstance();
			createNew = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public String deleteNotification() {
		try {
			Notification noti = notiRespository.findById(Notification.class, notification.getId());
			String title = noti.getTitle();
			notiRespository.delete(noti);
			notificationList.remove(noti);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, title);
			createNewInstance();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public void checkBUI() {
		this.title = notification.getTitle();
		if (this.title != null && !this.title.isEmpty()) {
			if (isBold)
				this.title = "<b>" + title + "</b>";
			else {
				this.title = title.replace("<b>", "");
				this.title = title.replace("</b>", "");
			}
			if (isItalic)
				this.title = "<i>" + title + "</i>";
			else {
				this.title = title.replace("<i>", "");
				this.title = title.replace("</i>", "");
			}
			if (isUnderline)
				this.title = "<u>" + title + "</u>";
			else {
				this.title = title.replace("<u>", "");
				this.title = title.replace("</u>", "");
			}
			if (!isBold && !isItalic && !isUnderline) {
				notification.setTitle(this.title);
				this.title = "";
			}

			PrimeFaces.current().ajax().update("notificationEntryForm:titleGroup");
		}
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<Notification> getNotificationList() {
		return notificationList;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}

	public boolean isItalic() {
		return isItalic;
	}

	public void setItalic(boolean isItalic) {
		this.isItalic = isItalic;
	}

	public boolean isUnderline() {
		return isUnderline;
	}

	public void setUnderline(boolean isUnderline) {
		this.isUnderline = isUnderline;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
