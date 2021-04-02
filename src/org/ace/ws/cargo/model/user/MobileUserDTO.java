/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2015-10-09
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.ws.cargo.model.user;

import java.io.Serializable;

public class MobileUserDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String userCode;
	private String userType;
	private String password;
	private String phone;
	private String email;
	private String address;
	private boolean accessSync;
	private boolean accountDisable;
	private boolean changePassword;
	private int version;

	public MobileUserDTO() {
	}

	public MobileUserDTO(String id, String name, String userCode, String userType, String password, String phone, String email, String address, boolean accessSync,
			boolean accountDisable, boolean changePassword, int version) {
		super();
		this.id = id;
		this.name = name;
		this.userCode = userCode;
		this.userType = userType;
		this.password = password;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.accessSync = accessSync;
		this.accountDisable = accountDisable;
		this.changePassword = changePassword;
		this.version = version;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isAccessSync() {
		return accessSync;
	}

	public void setAccessSync(boolean accessSync) {
		this.accessSync = accessSync;
	}

	public boolean isAccountDisable() {
		return accountDisable;
	}

	public void setAccountDisable(boolean accountDisable) {
		this.accountDisable = accountDisable;
	}

}