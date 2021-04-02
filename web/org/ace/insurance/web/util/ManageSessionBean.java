package org.ace.insurance.web.util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.ace.insurance.process.interfaces.IUserProcessService;
import org.ace.insurance.user.User;
import org.ace.java.web.common.Constants;

@ViewScoped
@ManagedBean(name = "ManageSessionBean")
public class ManageSessionBean {

	@ManagedProperty(value = "#{UserProcessService}")
	private IUserProcessService userProcessService;

	public void setUserProcessService(IUserProcessService userProcessService) {
		this.userProcessService = userProcessService;
	}

	public void clearSession() {
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.LOGIN_USER);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(Constants.LOGIN_USER, user);
		userProcessService.registerUser(user);
	}

}
