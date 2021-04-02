package org.ace.insurance.system.common.securityQuestion.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.securityQuestion.SecurityQuestion;

public interface ISecurityQuestionService {

	List<SecurityQuestion> findAllSecurityQuestionList();

	void delete(String id);

	void insert(SecurityQuestion securityQuestion);

	void update(SecurityQuestion securityQuestion);

	List<SecurityQuestion> findByCriteria(String criteria);

}
