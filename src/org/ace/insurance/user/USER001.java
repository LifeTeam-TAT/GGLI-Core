package org.ace.insurance.user;

import java.io.Serializable;

public class USER001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String usercode;
	private String name;
	private boolean disabled;
	private double authority;
	private String branchcode;
	
	public USER001(String id, String usercode, String name, boolean disabled, double authority, String branchcode) {
		super();
		this.id = id;
		this.usercode = usercode;
		this.name = name;
		this.disabled = disabled;
		this.authority = authority;
		this.branchcode = branchcode;
	}
	
	public USER001(User user) {
		this.id = user.getId();
		this.usercode = user.getUsercode();
		this.name = user.getName();
		this.disabled = user.isDisabled();
		this.authority = user.getAuthority();
		this.branchcode = user.getBranch().getBranchCode();
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public double getAuthority() {
		return authority;
	}

	public void setAuthority(double authority) {
		this.authority = authority;
	}

	public String getBranchcode() {
		return branchcode;
	}

	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}
}
