package org.ace.insurance.mobile.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ace.insurance.mobile.enums.ResponseStatus;
import org.ace.insurance.mobile.securityAnswer.SQA001;

public class MU001 {
	private String id;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String email;
	private String newPassword;
	private String password;
	private String activatedCode;
	// private boolean isActived;
	private boolean isActivate;
	private Date activatedDate;
	private List<SQA001> sqa001List;
	private int version;
	private ResponseStatus responseStatus;

	public MU001() {

	}

	public MU001(MobileUser mobileUser) {
		this.id = mobileUser.getId();
		this.firstName = mobileUser.getFirstName();
		this.lastName = mobileUser.getLastName();
		this.mobileNumber = mobileUser.getMobileNumber();
		this.email = mobileUser.getEmail();
		this.password = mobileUser.getPassword();
		this.activatedCode = mobileUser.getActivatedCode();
		this.isActivate = mobileUser.isActivate();
		this.activatedDate = mobileUser.getActivatedDate();
		this.version = mobileUser.getVersion();
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
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 *            the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the activatedCode
	 */
	public String getActivatedCode() {
		return activatedCode;
	}

	/**
	 * @param activatedCode
	 *            the activatedCode to set
	 */
	public void setActivatedCode(String activatedCode) {
		this.activatedCode = activatedCode;
	}

	/**
	 * @return the isActivate
	 */
	public boolean isActivate() {
		return isActivate;
	}

	/**
	 * @param isActivate
	 *            the isActivate to set
	 */
	public void setActivate(boolean isActivate) {
		this.isActivate = isActivate;
	}

	/**
	 * @return the activatedDate
	 */
	public Date getActivatedDate() {
		return activatedDate;
	}

	/**
	 * @param activatedDate
	 *            the activatedDate to set
	 */
	public void setActivatedDate(Date activatedDate) {
		this.activatedDate = activatedDate;
	}

	/**
	 * @return the sqa001List
	 */
	public List<SQA001> getSqa001List() {
		if (sqa001List == null)
			sqa001List = new ArrayList<SQA001>();
		return sqa001List;
	}

	/**
	 * @param sqa001List
	 *            the sqa001List to set
	 */
	public void setSqa001List(List<SQA001> sqa001List) {
		this.sqa001List = sqa001List;
	}

	public void addSQA001(SQA001 sqa001) {
		getSqa001List().add(sqa001);
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getName() {
		String result = "";
		if (firstName != null && !firstName.isEmpty()) {
			result += firstName.trim();
		}
		if (lastName != null && !lastName.isEmpty())
			result += " " + lastName.trim();
		return result;
	}

}
