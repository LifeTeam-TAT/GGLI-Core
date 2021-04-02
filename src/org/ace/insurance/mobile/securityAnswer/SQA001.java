package org.ace.insurance.mobile.securityAnswer;

public class SQA001 {

	private String id;
	private String securityQuestionId;
	private String securityAnswer;

	public SQA001() {

	}

	public SQA001(SecurityAnswer securityAnswer) {
		this.id = securityAnswer.getId();
		this.securityQuestionId = securityAnswer.getSecurityQuestion().getId();
		this.securityAnswer = securityAnswer.getSecurityAnswer();
	}

	public SQA001(String securityQuestionId, String securityAnswer) {
		this.securityQuestionId = securityQuestionId;
		this.securityAnswer = securityAnswer;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the securityQuestionId
	 */
	public String getSecurityQuestionId() {
		return securityQuestionId;
	}

	/**
	 * @param securityQuestionId
	 *            the securityQuestionId to set
	 */
	public void setSecurityQuestionId(String securityQuestionId) {
		this.securityQuestionId = securityQuestionId;
	}

	/**
	 * @return the securityAnswer
	 */
	public String getSecurityAnswer() {
		return securityAnswer;
	}

	/**
	 * @param securityAnswer
	 *            the securityAnswer to set
	 */
	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

}
