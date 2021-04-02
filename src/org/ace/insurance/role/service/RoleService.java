/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.role.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.ResourceItemCriteria;
import org.ace.insurance.role.ItemType;
import org.ace.insurance.role.ResourceItem;
import org.ace.insurance.role.Role;
import org.ace.insurance.role.persistence.interfaces.IRoleDAO;
import org.ace.insurance.role.service.interfaces.IRoleService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("RoleService")
public class RoleService extends BaseService implements IRoleService {

	@Resource(name = "RoleDAO")
	private IRoleDAO roleDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public Role addNewRole(Role role) {
		try {
			role.setPrefix(getPrefix(Role.class));
			role = roleDAO.insert(role);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a Role", e);
		}
		return role;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Role updateRole(Role role) {
		try {
			role = roleDAO.update(role);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update role", e);
		}
		return role;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteRole(Role role) {
		try {
			roleDAO.delete(role);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete role", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Role> findAllRole() {
		List<Role> result = null;
		try {
			result = roleDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to fine all Role", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceItem> findAllResourceItem() {
		List<ResourceItem> result = null;
		try {
			result = roleDAO.findAllResourceItem();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to fine all ResourceItem", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceItem> findNoPermissionResourceItem() {
		List<ResourceItem> result = null;
		try {
			result = roleDAO.findNoPermissionResourceItem();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to fine all ResourceItem", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceItem> findAllResourceItemByType(ItemType itemType) {
		List<ResourceItem> result = null;
		try {
			result = roleDAO.findAllResourceItemByType(itemType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to fine all ResourceItem", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Role findRole(String id) {
		Role role = roleDAO.find(id);
		return role;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Role> findByCriteria(String criteria) {
		List<Role> result = null;
		try {
			result = roleDAO.findByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Role by criteria " + criteria, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceItem> findResourceItemByCriteria(ResourceItemCriteria criteria) {
		List<ResourceItem> result = null;
		try {
			result = roleDAO.findResourceItemByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a ResourceItem (criteriaValue : " + criteria.getCriteriaValue() + ")", e);
		}
		return result;
	}
}
