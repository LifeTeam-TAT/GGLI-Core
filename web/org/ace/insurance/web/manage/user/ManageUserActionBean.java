/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.UserCriteriaItems;
import org.ace.insurance.role.Role;
import org.ace.insurance.role.service.interfaces.IRoleService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

@ViewScoped
@ManagedBean(name = "ManageUserActionBean")
public class ManageUserActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{RoleService}")
	private IRoleService roleService;

	public void setRoleService(IRoleService roleService) {
		this.roleService = roleService;
	}

	private User user;
	private User tempUser;
	private List<User> userList;
	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private boolean createNew;
	private Map<String, Role> roleMap;
	private Map<String, Role> roleMapS;
	private Map<String, Role> roleMapT;
	private DualListModel<String> dualListModel;
	private List<String> source;
	private List<String> target;
	private List<SelectItem> userCriteriaItemList;
	private String oldpassword;

	@PostConstruct
	public void init() {
		source = new ArrayList<String>();
		target = new ArrayList<String>();
		roleMap = new HashMap<String, Role>();
		List<Role> roleList = roleService.findAllRole();
		for (Role role : roleList) {
			String name = role.getName();
			roleMap.put(name, role);
			source.add(name);
		}

		userCriteriaItemList = new ArrayList<SelectItem>();
		for (UserCriteriaItems criteriaItem : UserCriteriaItems.values()) {
			userCriteriaItemList.add(new SelectItem(criteriaItem.getLabel(), criteriaItem.getLabel()));
		}
		dualListModel = new DualListModel<String>(source, target);
		createNewUser();
		loadUser();

	}

	private void loadUser() {
		userList = userService.findAllUser();
	}

	public void selectSalePoint() {

		selectSalePointByBranch(user.getBranch() == null ? "" : user.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		user.setSalePoint(salePoint);
	}

	public List<SelectItem> getUserCriteriaItemList() {
		return userCriteriaItemList;
	}

	public void setUserCriteriaItemList(List<SelectItem> userCriteriaItemList) {
		this.userCriteriaItemList = userCriteriaItemList;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<User> getUserList() {
		return userList;
	}

	public User getUser() {
		return user;
	}

	public Map<String, Role> getRoleMap() {
		return roleMap;
	}

	public void setRoleMap(Map<String, Role> roleMap) {
		this.roleMap = roleMap;
	}

	public DualListModel<String> getDualListModel() {
		return dualListModel;
	}

	public void setDualListModel(DualListModel<String> dualListModel) {
		this.dualListModel = dualListModel;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public Map<String, Role> getRoleMapS() {
		return roleMapS;
	}

	public void setRoleMapS(Map<String, Role> roleMapS) {
		this.roleMapS = roleMapS;
	}

	public Map<String, Role> getRoleMapT() {
		return roleMapT;
	}

	public void setRoleMapT(Map<String, Role> roleMapT) {
		this.roleMapT = roleMapT;
	}

	public List<String> getSource() {
		return source;
	}

	public void setSource(List<String> source) {
		this.source = source;
	}

	public List<String> getTarget() {
		return target;
	}

	public void setTarget(List<String> target) {
		this.target = target;
	}

	public void createNewUser() {
		dualListModel = new DualListModel<String>(source, target);
		createNew = true;
		user = new User();
	}

	public void prepareUpdateUser(User user) {
		createNew = false;
		tempUser = userService.findUser(user.getUsercode());
		List<Role> selectedroles = tempUser.getRoleList();
		List<String> source = new ArrayList<String>();
		List<String> target = new ArrayList<String>();
		oldpassword = tempUser.getPassword();

		roleMapT = new HashMap<String, Role>();
		for (Role role : selectedroles) {
			String name = role.getName();
			roleMapT.put(name, role);
			target.add(name);

		}
		List<Role> roleList = roleService.findAllRole();

		roleMapS = new HashMap<String, Role>();
		for (Role role1 : roleList) {
			if (!selectedroles.contains(role1)) {
				String name = role1.getName();
				roleMapS.put(name, role1);
				source.add(name);
			}
		}

		dualListModel = new DualListModel<String>(source, target);
		// PasswordCodecHandler c = new PasswordCodecHandler();
		// user.setPassword(c.decode(tempUser.getPassword()));
		this.user = user;

	}

	public void addNewUser() {
		try {
			if (validate()) {
				List<Role> roleList = new ArrayList<Role>();
				List<String> targetList = dualListModel.getTarget();
				for (String target : targetList) {
					roleList.add(roleMap.get(target));
				}
				user.setRoleList(roleList);
				userService.addNewUser(user);
				userList.add(user);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, user.getUsercode());
				createNewUser();
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateUser() {
		try {
			if (validate()) {

				List<Role> selectedRolesList = new ArrayList<Role>();
				List<String> targetList = dualListModel.getTarget();
				for (String targetItem : targetList) {
					selectedRolesList.add(roleMap.get(targetItem));
				}
				user.setRoleList(selectedRolesList);
				userService.updateUser(user, oldpassword);
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, user.getUsercode());
				createNewUser();
				loadUser();
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean validate() {
		boolean result = true;
		User u = userService.findUser(user.getUsercode());
		if (u != null) {
			if (u.getUsercode().equals(user.getUsercode()) && !u.getId().equals(user.getId())) {
				FacesContext.getCurrentInstance().addMessage("userEntryForm:usercode", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "UserCode is already added!"));
				result = false;
			}
		}
		return result;
	}

	public String deleteUser(User user) {
		try {
			userService.deleteUser(user);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, user.getUsercode());
			userList.remove(user);
			createNewUser();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public String prepareEditUserAuthority(User configUser) {
		putParam("configUser", configUser);
		return "authorityConfig";
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		user.setBranch(branch);
	}

	public void removeUser() {
		user.setBranch(null);
		user.setSalePoint(null);
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

}
