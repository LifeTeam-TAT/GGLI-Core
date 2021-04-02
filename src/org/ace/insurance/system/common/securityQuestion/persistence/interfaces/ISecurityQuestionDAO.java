package org.ace.insurance.system.common.securityQuestion.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.securityQuestion.SecurityQuestion;

public interface ISecurityQuestionDAO {

	void update(SecurityQuestion securityQuestion);

	void insert(SecurityQuestion securityQuestion);

	void delete(String id);

	List<SecurityQuestion> findAllSecurityQuestionList();

	List<SecurityQuestion> findByCriteria(String criteria);

}
