/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.role.persistence;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ResourceItemCriteria;
import org.ace.insurance.role.ItemType;
import org.ace.insurance.role.ResourceItem;
import org.ace.insurance.role.Role;
import org.ace.insurance.role.persistence.interfaces.IRoleDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("RoleDAO")
public class RoleDAO extends BasicDAO implements IRoleDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public Role insert(Role role) throws DAOException {
		try {
			em.persist(role);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Role(Rolename = " + role.getName() + ")", pe);
		}
		return role;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Role update(Role role) throws DAOException {
		try {
			role = em.merge(role);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Role(Rolename = " + role.getName() + ")", pe);
		}
		return role;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Role role) throws DAOException {
		try {
			role = em.merge(role);
			em.remove(role);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete Role(Rolename = " + role.getName() + ")", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Role> findAll() {
		List<Role> result = null;
		try {
			Query q = em.createNamedQuery("Role.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Role.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Role find(String id) throws DAOException {
		Role result = null;
		try {
			result = em.find(Role.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Role(ID = " + id + ")", pe);
		}
		return result;
	}

	@Override
	public List<ResourceItem> findAllResourceItem() throws DAOException {
		List<ResourceItem> result = null;
		try {
			Query q = em.createNamedQuery("ResourceItem.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Role.", pe);
		}
		return result;
	}

	@Override
	public List<ResourceItem> findNoPermissionResourceItem() throws DAOException {
		List<ResourceItem> result = null;
		try {
			Query q = em.createNamedQuery("ResourceItem.findNoPermissionItems");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Role.", pe);
		}
		return result;
	}

	@Override
	public List<ResourceItem> findAllResourceItemByType(ItemType itemType) throws DAOException {
		List<ResourceItem> result = null;
		try {
			Query q = em.createNamedQuery("ResourceItem.findByType");
			q.setParameter("itemType", itemType);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Role.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Role> findByCriteria(String criteria) throws DAOException {
		List<Role> result = null;
		try {
			Query q = em.createQuery("Select r from Role r where r.name Like '" + criteria + "%'");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find by criteria of Role.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceItem> findResourceItemByCriteria(ResourceItemCriteria criteria) throws DAOException {
		List<ResourceItem> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT r FROM ResourceItem r ");
			if (criteria.getResourceItemCriteria() != null) {
				switch (criteria.getResourceItemCriteria()) {
					case NAME: {
						query.append("WHERE r.name like :name");
						break;
					}
					case PAGE_URL: {
						query.append("WHERE r.url like :url");
						break;
					}
				}
			}
			query.append(" Order By r.itemType DESC");
			Query q = em.createQuery(query.toString());
			if (criteria.getResourceItemCriteria() != null) {

				switch (criteria.getResourceItemCriteria()) {
					case NAME: {
						q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
					case PAGE_URL: {
						q.setParameter("url", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
				}
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find ResourceItem", pe);
		}

		return result;
	}
}
