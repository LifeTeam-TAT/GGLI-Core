package org.ace.insurance.web.manage.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.ResourceItemCriteria;
import org.ace.insurance.common.ResourceItemCriteriaItems;
import org.ace.insurance.role.ItemType;
import org.ace.insurance.role.ResourceItem;
import org.ace.insurance.role.Role;
import org.ace.insurance.role.service.interfaces.IRoleService;
import org.ace.insurance.web.common.LazyDataModelUtil;
import org.ace.insurance.web.datamodel.ResourceItemDataModel;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.model.LazyDataModel;

@ViewScoped
@ManagedBean(name = "ManageRoleActionBean")
public class ManageRoleActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean createNew;
	private Role role;
	@ManagedProperty(value = "#{RoleService}")
	private IRoleService roleService;

	public void setRoleService(IRoleService roleService) {
		this.roleService = roleService;
	}

	private List<Role> roleList;
	private LazyDataModel<Role> lazyModel;
	private ItemType itemType;
	private List<ResourceItem> resourceItemList;
	private List<ResourceItem> selectedResourceItemList;
	private Map<String, ResourceItem> resourceItemMap;
	private String criteria;
	private String selectedCriteria;
	private List<SelectItem> resourceItemCriteriaItemList;
	private ResourceItemCriteria resourceItemCriteria;

	@PostConstruct
	public void init() {
		resourceItemList = roleService.findAllResourceItem();
		roleList = roleService.findAllRole();
		lazyModel = new LazyDataModelUtil(roleList);
		resourceItemCriteria = new ResourceItemCriteria();
		resourceItemCriteriaItemList = new ArrayList<SelectItem>();
		for (ResourceItemCriteriaItems criteriaItem : ResourceItemCriteriaItems.values()) {
			resourceItemCriteriaItemList.add(new SelectItem(criteriaItem.getLabel(), criteriaItem.getLabel()));
		}
		createNewRole();
	}

	public void createNewRole() {
		role = new Role();
		createNew = true;
		criteria = null;
		selectedResourceItemList = new ArrayList<ResourceItem>();
		resourceItemMap = new HashMap<String, ResourceItem>();
	}

	public void addNewRole() {
		try {
			roleService.addNewRole(role);
			loadRoleDataModel();
			addInfoMessage(null, MessageId.INSERT_SUCCESS, role.getName());
			createNewRole();
			init();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void prepareUpdateRole(Role role) {
		createNew = false;
		this.role = role;
	}

	public void updateRole() {
		try {
			roleService.updateRole(role);
			// refreshRoleList();
			loadRoleDataModel();
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, role.getName());
			createNewRole();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	private void refreshRoleList() {
		for (Role r : roleList) {
			if (r.getId().equals(role.getId())) {
				roleList.remove(r);
				break;
			}
		}
		roleList.add(role);
	}

	public String deleteRole() {
		try {
			roleService.deleteRole(role);
			roleList.remove(role);
			loadRoleDataModel();
			addInfoMessage(null, MessageId.DELETE_SUCCESS, role.getName());
			createNewRole();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public void addResourceItem() {
		if (role.getResourceItemList() != null && !role.getResourceItemList().isEmpty()) {
			for (ResourceItem resourceItem : role.getResourceItemList()) {
				String name = resourceItem.getName();
				resourceItemMap.put(name, resourceItem);
			}
			for (ResourceItem resourceItem : selectedResourceItemList) {
				String name = resourceItem.getName();
				resourceItemMap.put(name, resourceItem);
			}
			List<ResourceItem> resourceItemList = new ArrayList<ResourceItem>(resourceItemMap.values());
			role.setResourceItemList(resourceItemList);
		} else {
			this.role.setResourceItemList(selectedResourceItemList);
		}

	}

	public void removeAllResourceItems() {
		role.getResourceItemList().clear();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public ItemType[] getResourceItemTypes() {
		return ItemType.values();
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	private void loadRoleDataModel() {
		lazyModel = new LazyDataModelUtil(roleList);
	}

	public LazyDataModel<Role> getLazyModel() {
		return lazyModel;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ResourceItemDataModel getResourceItemDataModel() {
		return new ResourceItemDataModel(resourceItemList);
	}

	public List<ResourceItem> getSelectedResourceItemList() {
		return selectedResourceItemList;
	}

	public void setSelectedResourceItemList(List<ResourceItem> selectedResourceItemList) {
		this.selectedResourceItemList = selectedResourceItemList;
	}

	public List<ResourceItem> getResourceItemList() {
		return resourceItemList;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public void searchRole() {
		roleList = roleService.findByCriteria(criteria);
		loadRoleDataModel();
	}

	public String getSelectedCriteria() {
		return selectedCriteria;
	}

	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}

	public List<SelectItem> getResourceItemCriteriaItemList() {
		return resourceItemCriteriaItemList;
	}

	public void setResourceItemCriteriaItemList(List<SelectItem> resourceItemCriteriaItemList) {
		this.resourceItemCriteriaItemList = resourceItemCriteriaItemList;
	}

	public ResourceItemCriteria getResourceItemCriteria() {
		return resourceItemCriteria;
	}

	public void setResourceItemCriteria(ResourceItemCriteria resourceItemCriteria) {
		this.resourceItemCriteria = resourceItemCriteria;
	}

	public void searchResourceItem() {
		resourceItemCriteria.setResourceItemCriteria(null);
		for (ResourceItemCriteriaItems criteriaItem : ResourceItemCriteriaItems.values()) {
			if (criteriaItem.toString().equals(selectedCriteria)) {
				resourceItemCriteria.setResourceItemCriteria(criteriaItem);
			}
		}
		resourceItemList = roleService.findResourceItemByCriteria(resourceItemCriteria);
	}

}
