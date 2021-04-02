/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.user.service.interfaces;

import java.util.List;

import org.ace.insurance.common.RespPersonCriteria;
import org.ace.insurance.common.UserCriteria;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.USER001;
import org.ace.insurance.user.User;

public interface IUserService {
	public boolean authenticate(String username, String password);

	public User addNewUser(User user);

	public void changePassword(String username, String newPassword, String oldPassword);

	public void resetPassword(String user);

	public User updateUser(User user, String oldPassword);

	public User updateAuthority(User user);

	public void deleteUser(User user);

	public User findUser(String userCode);

	public List<User> findAllUser();

	public List<User> findUserByPermission(WorkflowTask permission, WorkFlowType workFlowType);

	public List<User> findRespPersonByPermission(WorkflowTask permission, WorkFlowType workFlowType);

	public List<User> findRespPersonByPermission(WorkflowTask permission, WorkFlowType workFlowType, RespPersonCriteria criteria);

	public List<User> findUserByCriteria(UserCriteria criteria, int max);

	public User findUserById(String id);

	public List<USER001> findUser(WorkflowTask workFlowTask, WorkFlowType workFlowType, String branchId);

	User findUserAndCreateLog(String userCode);
}
