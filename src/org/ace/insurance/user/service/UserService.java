/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.user.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.RespPersonCriteria;
import org.ace.insurance.common.UserCriteria;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.system.common.branch.persistence.interfaces.IBranchDAO;
import org.ace.insurance.user.USER001;
import org.ace.insurance.user.User;
import org.ace.insurance.user.persistence.interfaces.IUserDAO;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.userlog.UserLog;
import org.ace.insurance.userlog.persistence.interfaces.IUserLogDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("UserService")
public class UserService extends BaseService implements IUserService {

	@Resource(name = "UserDAO")
	private IUserDAO userDAO;

	@Resource(name = "BranchDAO")
	private IBranchDAO branchDAO;

	// @Resource(name = "PasswordCodecHandler")
	// private PasswordCodecHandler codecHandler;

	@Autowired
	private IUserLogDAO userLogDAO;

	@Autowired
	private PasswordEncoder encoder;

	@Transactional(propagation = Propagation.REQUIRED)
	public User addNewUser(User user) {
		try {
			user.setPrefix(getPrefix(User.class));
			String hashedPassword = encoder.encode(user.getPassword());
			user.setPassword(hashedPassword);
			user = userDAO.insert(user);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a User", e);
		}
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User updateUser(User user, String oldPassword) {
		try {
			if (!oldPassword.equals(user.getPassword())) {
				String hashedPassword = encoder.encode(user.getPassword());
				user.setPassword(hashedPassword);
			}
			user = userDAO.update(user);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update user", e);
		}
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User updateAuthority(User user) {
		try {
			user = userDAO.update(user);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update user", e);
		}
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteUser(User user) {
		try {
			userDAO.delete(user);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete user", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findUserByCriteria(UserCriteria criteria, int max) {
		List<User> result = null;
		try {
			result = userDAO.findUserByCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a User (criteriaValue : " + criteria.getCriteriaValue() + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<USER001> findUser(WorkflowTask workflowTask, WorkFlowType workFlowType, String branchId) {
		List<USER001> result = null;
		try {
			result = userDAO.findUser(workflowTask, workFlowType, branchId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to user by permission" + workFlowType, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findRespPersonByPermission(WorkflowTask permission, WorkFlowType workFlowType) {
		List<User> result = null;
		try {
			result = userDAO.findRespPersonByPermission(permission, workFlowType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to user by permission : " + permission + " : " + workFlowType, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findRespPersonByPermission(WorkflowTask permission, WorkFlowType workFlowType, RespPersonCriteria criteria) {
		List<User> result = null;
		try {
			result = userDAO.findRespPersonByPermission(permission, workFlowType, criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to user by permission : " + permission + " : " + workFlowType, e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<User> findAllUser() {
		List<User> result = null;
		try {
			result = userDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all user", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public User findUser(String userCode) {
		User user = userDAO.find(userCode);
		return user;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public User findUserAndCreateLog(String userCode) {
		User user = userDAO.find(userCode);
		UserLog userLog = new UserLog();
		userLog.setUserId(user.getId());
		userLog.setLogInDate(new Date());
		userLogDAO.insertUserLost(userLog);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public User findUserById(String id) {
		User user = userDAO.findById(id);
		return user;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void changePassword(String usercode, String newPassword, String oldPassword) {
		try {
			User user = userDAO.find(usercode);
			if (user != null && encoder.matches(oldPassword, user.getPassword())) {
				String encryptedPassword = encoder.encode(newPassword);
				userDAO.changePassword(usercode, encryptedPassword);
			} else {
				throw new SystemException(ErrorCode.OLD_PASSWORD_DOES_NOT_MATCH, "Old password does not match.");
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to change passowrd", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void resetPassword(String usercode) {
		try {
			String encryptedPassword = encoder.encode(User.DEFAULT_PASSWORD);
			userDAO.resetPassword(usercode, encryptedPassword);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reset passowrd", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean authenticate(String usercode, String password) {
		try {
			User user = userDAO.find(usercode);
			boolean isDisable = user.isDisabled();
			if (user != null && isDisable != true) {
				return encoder.matches(password, user.getPassword());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to change passowrd", e);
		}
		return false;
	}

	@Override
	public List<User> findUserByPermission(WorkflowTask permission, WorkFlowType workFlowType) {
		List<User> result = null;
		try {
			result = userDAO.findByPermission(permission, workFlowType);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to user by permission : " + permission + " : " + workFlowType, e);
		}
		return result;
	}

}
