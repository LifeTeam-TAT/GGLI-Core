/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.user.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.RespPersonCriteria;
import org.ace.insurance.common.UserCriteria;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.Authority;
import org.ace.insurance.user.USER001;
import org.ace.insurance.user.User;
import org.ace.insurance.user.persistence.interfaces.IUserDAO;
import org.ace.java.component.idgen.service.interfaces.IDConfigLoader;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("UserDAO")
public class UserDAO extends BasicDAO implements IUserDAO {

	@Resource(name = "IDConfigLoader")
	private IDConfigLoader configLoader;

	@Transactional(propagation = Propagation.REQUIRED)
	public User insert(User user) throws DAOException {
		try {
			em.persist(user);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert User(Username = " + user.getUsercode() + ")", pe);
		}
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User update(User user) throws DAOException {
		try {
			user = em.merge(user);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update User(Username = " + user.getUsercode() + ")", pe);
		}
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(User user) throws DAOException {
		try {
			user = em.merge(user);
			em.remove(user);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete User(Username = " + user.getUsercode() + ")", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findAll() {
		List<User> result = null;
		try {
			Query q = em.createNamedQuery("User.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of User.", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User find(String usercode) throws DAOException {
		User result = null;
		try {
			Query q = em.createNamedQuery("User.findByUsercode");
			q.setParameter("usercode", usercode);
			result = (User) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find User(Username = " + usercode + ")", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User findById(String id) throws DAOException {
		User result = null;
		try {
			Query q = em.createNamedQuery("User.findById");
			q.setParameter("id", id);
			result = (User) q.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find User(Username = " + id + ")", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<USER001> findUser(WorkflowTask workflowTask, WorkFlowType workFlowType, String branchId) throws DAOException {
		List<USER001> result = null;
		try {
			
			StringBuffer buffer = new StringBuffer();
			StringBuffer accessbuffer = new StringBuffer();
			buffer.append("SELECT NEW " + USER001.class.getName() + "(u.id, u.usercode, u.name, u.disabled, u.authority , u.branch.branchCode) ");
			buffer.append("FROM User u LEFT JOIN u.authorityList a ");
			buffer.append("WHERE a.workFlowType = :workFlowType AND (u.branch.id = :branchId) ");
			buffer.append("AND :permission MEMBER OF a.permissionList ");
			//AND  u.accessAllBranch = true
			accessbuffer.append(" UNION ALL ");
			accessbuffer.append("SELECT u.id, u.usercode, u.name, u.disabled, u.authority , u.branch.branchCode " );
			accessbuffer.append("FROM User u LEFT JOIN u.authorityList a ");
			accessbuffer.append("WHERE a.workFlowType = :workFlowType AND (u.branch.id <> :branchId AND u.accessAllBranch = true ) ");
			accessbuffer.append("AND :permission MEMBER OF a.permissionList ");
			buffer.append(accessbuffer.toString());
			//
			Query query = em.createQuery(buffer.toString());
			query.setParameter("workFlowType", workFlowType);
			query.setParameter("permission", workflowTask);
			query.setParameter("branchId", branchId);
			result = query.getResultList();
			em.flush();
			//TODO FIXME PSH old code (change for access all branch)
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("SELECT NEW " + USER001.class.getName() + "(u.id, u.usercode, u.name, u.disabled, u.authority , u.branch.branchCode) ");
//			buffer.append("FROM User u LEFT JOIN u.authorityList a ");
//			buffer.append("WHERE a.workFlowType = :workFlowType AND u.branch.id = :branchId ");
//			buffer.append("AND :permission MEMBER OF a.permissionList ");
//			Query query = em.createQuery(buffer.toString());
//			query.setParameter("workFlowType", workFlowType);
//			query.setParameter("permission", workflowTask);
//			query.setParameter("branchId", branchId);
//			result = query.getResultList();
//			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find user by permission", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findRespPersonByPermission(WorkflowTask permission, WorkFlowType workFlowType) throws DAOException {
		Map<String, User> userMap = new HashMap<String, User>();
		try {
			String queryString = "SELECT a FROM Authority a WHERE a.workFlowType = :workFlowType AND a.user.branch.id = :branchId";
			Query q = em.createQuery(queryString);
			q.setParameter("workFlowType", workFlowType);
			q.setParameter("branchId", configLoader.getBranchId());
			List<Authority> authorityList = q.getResultList();
			if (authorityList != null && !authorityList.isEmpty()) {
				for (Authority auth : authorityList) {
					if (auth.getPermissionList() != null && auth.getPermissionList().contains(permission)) {
						userMap.put(auth.getUser().getUsercode(), auth.getUser());
					}
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find User by Role", pe);
		}
		return new ArrayList<User>(userMap.values());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findRespPersonByPermission(WorkflowTask permission, WorkFlowType workFlowType, RespPersonCriteria criteria) throws DAOException {
		Map<String, User> userMap = new HashMap<String, User>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT a FROM Authority a WHERE a.workFlowType = :workFlowType ");
			switch (criteria.getRespPersonCriteria()) {
				case BRANCH: {
					query.append("AND a.user.branch.branchCode = :branchCode ");
					break;
				}
				case NAME: {
					query.append("AND a.user.name like :name AND a.user.branch.id = :branchId ");
					break;
				}
				case USERCODE: {
					query.append("AND a.user.usercode like :userCode AND a.user.branch.id = :branchId ");
					break;
				}
				default: {
					query.append("AND a.user.branch.id = :branchId ");
					break;
				}
			}
			Query q = em.createQuery(query.toString());
			q.setParameter("workFlowType", workFlowType);
			switch (criteria.getRespPersonCriteria()) {
				case BRANCH: {
					q.setParameter("branchCode", criteria.getCriteriaValue());
					break;
				}
				case NAME: {
					q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
					q.setParameter("branchId", configLoader.getBranchId());
					break;
				}
				case USERCODE: {
					q.setParameter("userCode", "%" + criteria.getCriteriaValue() + "%");
					q.setParameter("branchId", configLoader.getBranchId());
					break;
				}
				default: {
					q.setParameter("branchId", configLoader.getBranchId());
					break;
				}
			}
			List<Authority> authorityList = q.getResultList();
			if (authorityList != null && !authorityList.isEmpty()) {
				for (Authority auth : authorityList) {
					if (auth.getPermissionList() != null && auth.getPermissionList().contains(permission)) {
						userMap.put(auth.getUser().getUsercode(), auth.getUser());
					}
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find User by Role", pe);
		}
		return new ArrayList<User>(userMap.values());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void changePassword(String usercode, String newPassword) throws DAOException {
		try {
			Query q = em.createNamedQuery("User.changePassword");
			q.setParameter("usercode", usercode);
			q.setParameter("newPassword", newPassword);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to change Password (Username = " + usercode + ")", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void resetPassword(String usercode, String defaultPassword) throws DAOException {
		try {
			Query q = em.createNamedQuery("User.resetPassword");
			q.setParameter("usercode", usercode);
			q.setParameter("defaultPassowrd", defaultPassword);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to reset Password (Username = " + usercode + ")", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findUserByCriteria(UserCriteria criteria, int max) throws DAOException {
		List<User> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT u FROM User u ");
			if (criteria.getUserCriteria() != null) {
				switch (criteria.getUserCriteria()) {
					case USER_CODE: {
						query.append("WHERE u.usercode like :usercode");
						break;
					}
					case NAME: {
						query.append("WHERE u.name like :name");
						break;
					}
					case SALEPOINT: {
						query.append("WHERE u.salePoint.name like :salePointName");
						break;
					}
				}
			}
			query.append(" Order By u.id DESC");
			Query q = em.createQuery(query.toString());
			q.setMaxResults(max);
			if (criteria.getUserCriteria() != null) {
				switch (criteria.getUserCriteria()) {
					case USER_CODE: {
						q.setParameter("usercode", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
					case NAME: {
						q.setParameter("name", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
					case SALEPOINT: {
						q.setParameter("salePointName", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
				}
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find User", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findByPermission(WorkflowTask permission, WorkFlowType workFlowType) throws DAOException {
		List<User> result = null;
		try {
			StringBuffer queryBuffer = new StringBuffer();
			queryBuffer.append("SELECT u FROM User u JOIN u.authorityList a WHERE a.workFlowType = :workFlowType ");
			queryBuffer.append(" AND  :permission MEMBER OF a.permissionList");
			Query query = em.createQuery(queryBuffer.toString());
			query.setParameter("workFlowType", workFlowType);
			query.setParameter("permission", permission);
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find User by Role", pe);
		}

		return result;
	}

}
