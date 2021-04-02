package org.ace.ws.model.system;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.ace.insurance.system.productinformation.Language;

// FAQ
public class FAQ001 {
	private String id;
	private String question;
	private String answer;
	private String codeNo;
	private String language;
	private int version;
	private String createdUserId;
	private Date createdDate;
	private String updatedUserId;
	private Date updatedDate;

	public FAQ001() {
	}

	public FAQ001(String id, String question, String answer, String codeNo, Language language, int version, String createdUserId, Date createdDate, String updatedUserId,
			Date updatedDate) throws UnsupportedEncodingException {
		this.id = id;
		this.question = URLEncoder.encode(question == null ? "" : question, "UTF-8");
		this.answer = URLEncoder.encode(answer == null ? "" : answer, "UTF-8");
		this.codeNo = codeNo;
		this.language = language == null ? "" : language.getLabel();
		this.version = version;
		this.createdUserId = createdUserId;
		this.createdDate = createdDate;
		this.updatedUserId = updatedUserId;
		this.updatedDate = updatedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCodeNo() {
		return codeNo;
	}

	public void setCodeNo(String codeNo) {
		this.codeNo = codeNo;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(String updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}
