/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.role.service.interfaces;

import java.util.List;

import org.ace.insurance.common.ResourceItemCriteria;
import org.ace.insurance.role.ItemType;
import org.ace.insurance.role.ResourceItem;
import org.ace.insurance.role.Role;

public interface IRoleService {
	public Role addNewRole(Role role);

	public Role updateRole(Role role);

	public void deleteRole(Role role);

	public Role findRole(String id);

	public List<Role> findAllRole();

	public List<ResourceItem> findAllResourceItem();

	public List<ResourceItem> findNoPermissionResourceItem();

	public List<ResourceItem> findAllResourceItemByType(ItemType itemType);

	public List<Role> findByCriteria(String criteria);

	public List<ResourceItem> findResourceItemByCriteria(ResourceItemCriteria criteria);
}
