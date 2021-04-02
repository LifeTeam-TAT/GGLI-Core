package org.ace.insurance.system.notification;

import java.util.Date;

public class NotificationCriteria {
	private String id;

	private String title;

	private boolean isPush;

	private boolean finishedPush;

	private Date startDate;

	private Date endDate;

	private Date createdDate;

	private int maxResult;

	private boolean isApi;

	public NotificationCriteria() {
		super();
	}

	public NotificationCriteria(boolean isPush, Date endDate, Date createdDate, boolean isApi, boolean finishedPush) {
		super();
		this.isPush = isPush;
		this.endDate = endDate;
		this.createdDate = createdDate;
		this.isApi = isApi;
		this.finishedPush = finishedPush;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isPush() {
		return isPush;
	}

	public void setPush(boolean isPush) {
		this.isPush = isPush;
	}

	public boolean isFinishedPush() {
		return finishedPush;
	}

	public void setFinishedPush(boolean finishedPush) {
		this.finishedPush = finishedPush;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isApi() {
		return isApi;
	}

	public void setApi(boolean isApi) {
		this.isApi = isApi;
	}

}
