package org.ace.insurance.report.agent.persistence;

import java.util.Date;
import java.util.List;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.agent.AgentComissionInfo;
import org.ace.insurance.system.common.agent.Agent;

public class AgentSanctionDTO implements ISorter {
	private String id;
	private String sanctionNo;
	private Date sanctionDate;
	private PolicyReferenceType referenceType;
	private double commision;
	private Agent agent;
	private String licenseNo;

	// private String receiptNo;

	public AgentSanctionDTO() {
	}

	public AgentSanctionDTO(String id, String sanctionNo, Date sanctionDate, PolicyReferenceType referenceType, double commision, String licenseNo) {
		this.id = id;
		this.sanctionNo = sanctionNo;
		this.sanctionDate = sanctionDate;
		this.referenceType = referenceType;
		this.commision = commision;
		this.licenseNo = licenseNo;
	}

	public AgentSanctionDTO(String sanctionNo, Date sanctionDate, PolicyReferenceType referenceType, double commision, Agent agent) {
		this.sanctionNo = sanctionNo;
		this.sanctionDate = sanctionDate;
		this.referenceType = referenceType;
		this.commision = commision;
		this.agent = agent;
	}

	public AgentSanctionDTO(List<AgentComissionInfo> agentComInfos) {
		for (AgentComissionInfo agc : agentComInfos) {
			commision += agc.getTotalComission();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSanctionNo() {
		return sanctionNo;
	}

	public void setSanctionNo(String sanctionNo) {
		this.sanctionNo = sanctionNo;
	}

	public Date getSanctionDate() {
		return sanctionDate;
	}

	public void setSanctionDate(Date sanctionDate) {
		this.sanctionDate = sanctionDate;
	}

	public PolicyReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(PolicyReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public double getCommision() {
		return Utils.getTwoDecimalPoint(commision);
	}

	public void setCommision(double commision) {
		this.commision = commision;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	@Override
	public String getRegistrationNo() {
		return sanctionNo;
	}
}
