package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.USER001;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "UserDialogActionBean")
@ViewScoped
public class UserDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private List<USER001> userList;
	private User loginUser;

	@PostConstruct
	public void init() {
		loginUser = (User) getParam(Constants.LOGIN_USER);
		WorkflowTask workFlowTask = (WorkflowTask) getParam("WORKFLOWTASK");
		WorkFlowType workFlowType = (WorkFlowType) getParam("WORKFLOWTYPE");
		String branchId = loginUser.getBranch().getId();
		userList = userService.findUser(workFlowTask, workFlowType, branchId);
	}

	public void selectUser(USER001 usr001) {
		User user = userService.findUserById(usr001.getId());
		PrimeFaces.current().dialog().closeDynamic(user);
	}

	public List<USER001> getUserList() {
		return userList;
	}
}
