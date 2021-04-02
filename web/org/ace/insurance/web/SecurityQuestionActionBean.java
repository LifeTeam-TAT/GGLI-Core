package org.ace.insurance.web;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.securityQuestion.SecurityQuestion;
import org.ace.insurance.system.common.securityQuestion.service.interfaces.ISecurityQuestionService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ManagedBean(name = "SecurityQuestionActionBean")
@ViewScoped
public class SecurityQuestionActionBean extends BaseBean {

	@ManagedProperty(value = "#{SecurityQuestionService}")
	private ISecurityQuestionService securityQuestionService;

	public void setSecurityQuestionService(ISecurityQuestionService securityQuestionService) {
		this.securityQuestionService = securityQuestionService;
	}

	private List<SecurityQuestion> securityQuestionList;

	private SecurityQuestion securityQuestion;
	private boolean createNew;
	private String criteria;

	@PostConstruct
	public void init() {
		loadSecurityQuestion();
		createNewSecurityQuestion();
	}

	public void addNewSecurityQuestion() {
		try {
			securityQuestionService.insert(securityQuestion);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, securityQuestion.getSecurityQuestion());
			loadSecurityQuestion();
			createNewSecurityQuestion();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void updateSecurityQuestion() {
		try {
			securityQuestionService.update(securityQuestion);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, securityQuestion.getSecurityQuestion());
			loadSecurityQuestion();
			createNewSecurityQuestion();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void deleteSecurityQuestion() {
		try {
			securityQuestionService.delete(securityQuestion.getId());
			addInfoMessage(null, MessageId.DELETE_SUCCESS, securityQuestion.getId());
			loadSecurityQuestion();
			createNewSecurityQuestion();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void loadSecurityQuestion() {
		try {
			securityQuestionList = securityQuestionService.findAllSecurityQuestionList();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void createNewSecurityQuestion() {
		securityQuestion = new SecurityQuestion();
		createNew = true;
		criteria = null;
	}

	public void prepareSecurityQuestion(SecurityQuestion securityQuestion) {
		this.securityQuestion = securityQuestion;
		createNew = false;
	}

	public void searchSecurityQuestion() {
		securityQuestionList = securityQuestionService.findByCriteria(criteria);
	}

	public List<SecurityQuestion> getSecurityQuestionList() {
		return securityQuestionList;
	}

	public SecurityQuestion getSecurityQuestion() {
		return securityQuestion;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

}
