package org.ace.insurance.web.manage.renewal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.autoRenewal.service.interfaces.IAutoRenewalService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "BatchSchedulerActionBean")
public class BatchSchedulerActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String type;
	private String day;
	private Date time;
	private Date date;
	private String resultCorn;
	private boolean dayFlag;
	private boolean dateFlag;
	private String message;

	@PostConstruct
	public void init() {
		newData();
		message = readPropertiesFile();
	}

	public void newData() {
		type = "";
		day = "";
		resultCorn = "";
		dayFlag = false;
		dateFlag = false;
		date = new Date();
		date.setMonth(1);
		date.setDate(15);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.AM_PM, Calendar.AM);
		calendar.set(Calendar.HOUR, 3);
		calendar.set(Calendar.MINUTE, 30);
		time = calendar.getTime();
	}

	private String readPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;
		String message = "";
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			input = classLoader.getResourceAsStream("scheduler.properties");
			// load a properties file
			prop.load(input);
			message = prop.getProperty("message");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return message;
	}

	private String writePropertyFile() {
		Properties prop = new Properties();
		OutputStream output = null;
		String hour;
		String minute;
		String dateOfMonth;

		hour = String.valueOf(time.getHours());
		if (time.getMinutes() == 0) {
			minute = "00";
		} else
			minute = String.valueOf(time.getMinutes());
		dateOfMonth = String.valueOf(date.getDate());
		try {
			if ("Monthly".equalsIgnoreCase(type)) {
				resultCorn = "0 " + minute + " " + hour + " " + dateOfMonth + " * ?";
			} else if ("Weekly".equalsIgnoreCase(type)) {
				resultCorn = "0 " + minute + " " + hour + " ? * " + org.apache.commons.lang3.StringUtils.substring(day, 0, 3).toUpperCase();
			} else {
				resultCorn = "0 " + minute + " " + hour + " * * ?";
			}
			message = "Scheduler will run at " + hour + ":" + minute;
			if ("monthly".equalsIgnoreCase(type)) {
				message = message + " on the " + dateOfMonth + " day of every month.";
			} else if ("Weekly".equalsIgnoreCase(type)) {
				message = message + " of every " + day + ".";
			} else {
				message = message + " of every day.";
			}

			output = new FileOutputStream(getServletContext().getRealPath("/WEB-INF/classes/scheduler.properties"));
			prop.setProperty("cron", resultCorn);
			prop.setProperty("message", message);
			prop.store(output, null);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return message;
	}

	public void submit() {
		writePropertyFile();
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
		newData();
	}

	public void valueChangEvent(SelectEvent event) {
		if ("Daily".equalsIgnoreCase(type)) {
			dayFlag = false;
			dateFlag = false;
		} else if ("Weekly".equalsIgnoreCase(type)) {
			dayFlag = true;
			dateFlag = false;
		} else {
			dayFlag = false;
			dateFlag = true;
		}

	}

	public List<String> scheduleTypes() {
		return Arrays.asList("Daily", "Weekly", "Monthly");
	}

	public List<String> scheduleDays() {
		return Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getResultCorn() {
		return resultCorn;
	}

	public void setResultCorn(String resultCorn) {
		this.resultCorn = resultCorn;
	}

	public boolean isDayFlag() {
		return dayFlag;
	}

	public void setDayFlag(boolean dayFlag) {
		this.dayFlag = dayFlag;
	}

	public boolean isDateFlag() {
		return dateFlag;
	}

	public void setDateFlag(boolean dateFlag) {
		this.dateFlag = dateFlag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@ManagedProperty(value = "#{AutoRenewalService}")
	private IAutoRenewalService arService;

	public void setArService(IAutoRenewalService arService) {
		this.arService = arService;
	}

}
