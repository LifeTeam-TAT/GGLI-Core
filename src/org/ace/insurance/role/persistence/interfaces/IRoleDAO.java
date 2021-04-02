/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.role.persistence.interfaces;

import java.util.List;

import org.ace.insurance.common.ResourceItemCriteria;
import org.ace.insurance.role.ItemType;
import org.ace.insurance.role.ResourceItem;
import org.ace.insurance.role.Role;
import org.ace.java.component.persistence.exception.DAOException;

public interface IRoleDAO {
	public Role insert(Role role) throws DAOException;

	public Role update(Role role) throws DAOException;

	public void delete(Role role) throws DAOException;

	public Role find(String id) throws DAOException;

	public List<Role> findAll() throws DAOException;

	public List<ResourceItem> findAllResourceItem() throws DAOException;

	public List<ResourceItem> findNoPermissionResourceItem() throws DAOException;

	public List<ResourceItem> findAllResourceItemByType(ItemType itemType) throws DAOException;

	public List<Role> findByCriteria(String criteria) throws DAOException;

	public List<ResourceItem> findResourceItemByCriteria(ResourceItemCriteria criteria) throws DAOException;
}
