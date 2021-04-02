package org.ace.insurance.report.studentLife;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_BC_STUDENT_LIFE)
@ReadOnly
public class BCStudentLifeView {
	@Id
	@Column(name = "POLICYNO")
	private String policyNo;

	@Column(name = "POLICY_HOLDER_NAME")
	private String policyHolderName;

	@Column(name = "INSURED_PERSON_NAME")
	private String insuredPersonName;

	@Column(name = "POLICY_HOLDER_DOB")
	private Date policyHolderDOB;

	@Column(name = "INSURED_PERSON_DOB")
	private Date insuredPersonDOB;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "SUMINSURED")
	private double suminsured;

	@Column(name = "POLICY_TERM")
	private int policyTerm;

	@Column(name = "PREMIUM_TERM")
	private int premiumTerm;

	@Column(name = "INSTALLMENT_PREMIUM")
	private double premium;

	@Column(name = "PAYMENTTYPEID")
	private String paymentTypeId;

	@Column(name = "MODE_OF_PREMIUM")
	private String paymentTypeName;

	@Column(name = "AGENT_COMMISSION")
	private double agentCommission;

	@Column(name = "RECEIVEDDATE")
	private Date receive_Date;
	@Column(name = "CHANNEL")
	private String channel;

	///
	@Column(name = "SALEPOINT_ID")
	private String salePointId;

	@Column(name = "SALEPOINT_NAME")
	private String salePointName;

	@Column(name = "BRANCH_ID")
	private String branchId;

	@Column(name = "BRANCH_NAME")
	private String branchName;

	@Column(name = "AGENT_NAME")
	private String agentName;

	@Column(name = "AGENT_REGISTRATION_NO")
	private String regrestionNo;

	@Column(name = "ENTITYID")
	private String entityId;

	public String getEntityId() {
		return entityId;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getPolicyHolderName() {
		return policyHolderName;
	}

	public void setPolicyHolderName(String policyHolderName) {
		this.policyHolderName = policyHolderName;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public void setInsuredPersonName(String insuredPersonName) {
		this.insuredPersonName = insuredPersonName;
	}

	public Date getPolicyHolderDOB() {
		return policyHolderDOB;
	}

	public void setPolicyHolderDOB(Date policyHolderDOB) {
		this.policyHolderDOB = policyHolderDOB;
	}

	public Date getInsuredPersonDOB() {
		return insuredPersonDOB;
	}

	public void setInsuredPersonDOB(Date insuredPersonDOB) {
		this.insuredPersonDOB = insuredPersonDOB;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getSuminsured() {
		return suminsured;
	}

	public void setSuminsured(double suminsured) {
		this.suminsured = suminsured;
	}

	public int getPolicyTerm() {
		return policyTerm;
	}

	public void setPolicyTerm(int policyTerm) {
		this.policyTerm = policyTerm;
	}

	public int getPremiumTerm() {
		return premiumTerm;
	}

	public void setPremiumTerm(int premiumTerm) {
		this.premiumTerm = premiumTerm;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public double getAgentCommission() {
		return agentCommission;
	}

	public void setAgentCommission(double agentCommission) {
		this.agentCommission = agentCommission;
	}

	public Date getReceive_Date() {
		return receive_Date;
	}

	public void setReceive_Date(Date receive_Date) {
		this.receive_Date = receive_Date;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getRegrestionNo() {
		return regrestionNo;
	}

	public void setRegrestionNo(String regrestionNo) {
		this.regrestionNo = regrestionNo;
	}

	public String getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

}
