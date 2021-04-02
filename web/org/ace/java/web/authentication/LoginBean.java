/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.java.web.authentication;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.userlog.UserLog;
import org.ace.insurance.userlog.service.interfaces.IUserLogService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@ManagedBean(name = "LoginBean")
@RequestScoped
public class LoginBean extends BaseBean {
	private Logger logger = Logger.getLogger(LoginBean.class);

	@Autowired
	private PasswordEncoder encoder;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{UserProcessService}")
	private IUserProcessService userProcessService;

	public void setUserProcessService(IUserProcessService userProcessService) {
		this.userProcessService = userProcessService;
	}

	@ManagedProperty(value = "#{UserLogService}")
	private IUserLogService userLogService;

	public void setUserLogService(IUserLogService userLogService) {
		this.userLogService = userLogService;
	}

	private String username;
	private String password;
	private boolean isDisabled;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public String authenticate() {
		logger.debug("Login, Username : " + username);
		boolean authenticate = userService.authenticate(username, password);
		if (authenticate) {
			logger.debug("Login is successfull (Username : " + username + ")");
			User user = userService.findUserAndCreateLog(username);
			putParam(Constants.LOGIN_USER, user);
			userProcessService.registerUser(user);
			return "dashboard";
		} else {
			addInfoMessage(null, MessageId.LOGIN_FAILED);
			logger.debug("Login is failed (Username : " + username + ")");
			return null;
		}
	}

	public String logout() {
		User user = userProcessService.getLoginUser();
		if (null != user) {
			UserLog userLog = new UserLog();
			userLog.setUserId(user.getId());
			userLog.setLogOutDate(new Date());
			userLogService.insertUserLog(userLog);
		}
		HttpSession session = (HttpSession) getFacesContext().getExternalContext().getSession(false);
		session.invalidate();
		if (getPrimeTheme().equalsIgnoreCase("home")) {
			return "pslogin";
		} else {
			return "login";
		}
	}

	public void checkPermission(ComponentSystemEvent event) {
		FacesContext context = getFacesContext();
		ExternalContext extContext = context.getExternalContext();
		String messageId = (String) extContext.getSessionMap().remove(Constants.MESSAGE_ID);
		if (messageId != null) {
			addInfoMessage(null, messageId);
		}
		if (isExistParam(Constants.VIEW_EXPIRED)) {
			if ((boolean) getParam(Constants.VIEW_EXPIRED)) {
				addInfoMessage("Your session has expired. Please login again.");
				removeParam(Constants.VIEW_EXPIRED);
			}
		}
	}
}
