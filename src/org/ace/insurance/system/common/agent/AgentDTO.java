package org.ace.insurance.system.common.agent;

public class AgentDTO {

	private String fullName;

	public AgentDTO() {
	}

	public AgentDTO(String fullName) {
		super();
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
