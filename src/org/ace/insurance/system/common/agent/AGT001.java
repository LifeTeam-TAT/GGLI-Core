package org.ace.insurance.system.common.agent;

import java.io.Serializable;

import org.ace.insurance.common.Name;

public class AGT001 implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String codeNo;
	private String liscenseNo;
	private String fullIdNo;
	private String initialId;
	private String fullName;
	private String fullAddress;

	public AGT001() {
	}

	public AGT001(String id, String codeNo, String fullAddress, String liscenseNo, String initialId, Name name, String fullIdNo) {
		this.id = id;
		this.codeNo = codeNo;
		this.liscenseNo = liscenseNo;
		this.fullAddress = fullAddress;
		this.fullIdNo = fullIdNo;
		this.initialId = initialId;
		this.fullName = initialId + " " + name.getFullName();
	}

	public AGT001(Agent agent) {
		this.id = agent.getId();
		this.fullIdNo = agent.getIdNo();
		this.initialId = agent.getInitialId();
		this.fullName = agent.getFullName();
		this.codeNo = agent.getCodeNo();
		this.fullAddress = agent.getFullAddress();
		this.liscenseNo = agent.getLiscenseNo();
	}

	public String getId() {
		return id;
	}

	public String getCodeNo() {
		return codeNo;
	}

	public String getLiscenseNo() {
		return liscenseNo;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public String getInitialId() {
		return initialId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

}
