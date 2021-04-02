package org.ace.insurance.web.manage.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.Authority;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.model.DualListModel;

@ViewScoped
@ManagedBean(name = "AuthorityConfigActionBean")
public class AuthorityConfigActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private User configUser;
	private Map<WorkFlowType, AuthorityDTO> authorityMap;
	private WorkFlowType workFlowType;
	private Map<String, WorkflowTask> permissionMap;
	private DualListModel<String> permissionDualListModel;

	private void initializeInjection() {
		configUser = (User) getParam("configUser");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		authorityMap = new HashMap<WorkFlowType, AuthorityDTO>();
		permissionMap = new HashMap<String, WorkflowTask>();
		configUser = userService.findUserById(configUser.getId());
		loadSourcePermission();
		List<Authority> authorityList = configUser.getAuthorityList();
		if (authorityList != null && !authorityList.isEmpty()) {
			for (Authority auth : authorityList) {
				AuthorityDTO authorityDTO = new AuthorityDTO(auth.getWorkFlowType(), new HashSet<WorkflowTask>(auth.getPermissionList()));
				authorityMap.put(auth.getWorkFlowType(), authorityDTO);
			}
		}
	}

	private void loadSourcePermission() {
		List<String> source = new ArrayList<String>();
		for (WorkflowTask p : WorkflowTask.values()) {
			permissionMap.put(p.getLabel(), p);
			source.add(p.getLabel());
		}
		permissionDualListModel = new DualListModel<String>(source, new ArrayList<String>());
	}

	public User getUser() {
		return configUser;
	}

	public void setUser(User configUser) {
		this.configUser = configUser;
	}

	public DualListModel<String> getPermissionDualListModel() {
		return permissionDualListModel;
	}

	public void setPermissionDualListModel(DualListModel<String> permissionDualListModel) {
		this.permissionDualListModel = permissionDualListModel;
	}

	public WorkFlowType getWorkFlowType() {
		return workFlowType;
	}

	public void setWorkFlowType(WorkFlowType workFlowType) {
		this.workFlowType = workFlowType;
	}

	public WorkFlowType[] getWorkFlowTypes() {
		return WorkFlowType.values();
	}

	public List<AuthorityDTO> getAuthorityDTOList() {
		if (authorityMap == null) {
			return new ArrayList<AuthorityDTO>();
		}
		return new ArrayList<AuthorityDTO>(authorityMap.values());
	}

	public void addAuthority() {
		Set<WorkflowTask> target = new HashSet<WorkflowTask>();
		for (String p : permissionDualListModel.getTarget()) {
			target.add(permissionMap.get(p));
		}
		authorityMap.put(workFlowType, new AuthorityDTO(workFlowType, target));
		loadSourcePermission();
	}

	public void prepareEditAuthority(AuthorityDTO authorityDTO) {
		loadSourcePermission();
		this.workFlowType = authorityDTO.getWorkFlowType();
		List<String> target = new ArrayList<String>();
		for (WorkflowTask p : authorityDTO.getPermissionList()) {
			target.add(p.getLabel());
		}
		permissionDualListModel.getSource().removeAll(target);
		permissionDualListModel.setTarget(target);
	}

	public void deleteAuthority(AuthorityDTO authorityDTO) {
		authorityMap.remove(authorityDTO.getWorkFlowType());
	}

	public String applyAuthority() {
		String result = null;
		try {
			List<Authority> authorityList = new ArrayList<Authority>();
			for (AuthorityDTO dto : authorityMap.values()) {
				authorityList.add(new Authority(configUser, dto.getWorkFlowType(), new ArrayList<WorkflowTask>(dto.getPermissionList())));
			}
			configUser.setAuthorityList(authorityList);
			userService.updateAuthority(configUser);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, configUser.getUsercode());
			result = "manageUser";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}
}
