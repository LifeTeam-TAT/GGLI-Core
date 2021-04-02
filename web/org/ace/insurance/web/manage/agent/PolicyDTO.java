package org.ace.insurance.web.manage.agent;

import java.util.Date;

import org.ace.insurance.common.IdType;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.web.common.PeriodType;

public class PolicyDTO {
	private String policyNo;
	private PolicyReferenceType policyReferenceType;
	private String customerName;
	private IdType idType;
	private String idNo;
	private Double premium;
	private Double sumInsured;
	private Date commissionStartDate;
	private String paymentType;
	private int period;
	private PeriodType periodType;
	private Double commission;

	public PolicyDTO(MedicalPolicy medicalPolicy, Date commissionStartDate, Double commission, PolicyReferenceType policyReferenceType) {
		this.policyNo = medicalPolicy.getPolicyNo();
		this.policyReferenceType = policyReferenceType;
		this.customerName = medicalPolicy.getCustomerName();
		this.idNo = medicalPolicy.getCustomer() == null ? "" : medicalPolicy.getCustomer().getFullIdNo();
		this.idType = medicalPolicy.getCustomer() == null ? null : medicalPolicy.getCustomer().getIdType();
		this.premium = medicalPolicy.getTotalPremium();
		this.sumInsured = medicalPolicy.getTotalSumInsured();
		this.commissionStartDate = commissionStartDate;
		this.paymentType = medicalPolicy.getPaymentType().getName();
		this.period = medicalPolicy.getPolicyInsuredPersonList().get(0).getPeriodYears();
		this.periodType = PeriodType.YEAR;
		this.commission = commission;
	}

	public PolicyDTO(LifePolicy lPolicy, Date commissionStartDate, Double commission, PolicyReferenceType referenceType) {
		this.policyNo = lPolicy.getPolicyNo();
		this.policyReferenceType = referenceType;
		this.customerName = lPolicy.getCustomerName();
		this.idNo = lPolicy.getCustomer() == null ? "" : lPolicy.getCustomer().getIdNo();
		this.idType = lPolicy.getCustomer() == null ? null : lPolicy.getCustomer().getIdType();
		this.premium = lPolicy.getTotalBasicTermPremium() + lPolicy.getTotalAddOnTermPremium();
		this.sumInsured = lPolicy.getTotalSumInsured();
		this.commissionStartDate = commissionStartDate;
		this.paymentType = lPolicy.getPaymentType().getName();
		int lPeriod = lPolicy.getPolicyInsuredPersonList().get(0).getPeriodMonth();
		if (lPeriod >= 12) {
			this.period = lPeriod / 12;
		} else {
			this.period = lPeriod;
		}
		if (lPeriod >= 12) {
			this.periodType = PeriodType.YEAR;
		} else {
			this.periodType = PeriodType.MONTH;
		}
		this.commission = commission;
	}

	public PolicyDTO(PersonTravelPolicy personTravelPolicy, Date commissionStartDate, Double commission) {
		this.policyNo = personTravelPolicy.getPolicyNo();
		this.policyReferenceType = PolicyReferenceType.PERSON_TRAVEL_POLICY;
		this.customerName = personTravelPolicy.getCustomerName();
		this.idNo = personTravelPolicy.getCustomer() == null ? "" : personTravelPolicy.getCustomer().getFullIdNo();
		this.idType = personTravelPolicy.getCustomer() == null ? null : personTravelPolicy.getCustomer().getIdType();
		this.premium = personTravelPolicy.getTotalPremium();
		this.sumInsured = personTravelPolicy.getTotalSumInsured();
		this.commissionStartDate = commissionStartDate;
		this.paymentType = personTravelPolicy.getPaymentType().getName();
		this.commission = commission;
	}
	
	public PolicyDTO(GroupFarmerProposal groupFarmerProposal, Date commissionStartDate, Double commission) {
		this.policyNo = groupFarmerProposal.getProposalNo();
		this.policyReferenceType = PolicyReferenceType.GROUP_FARMER_PROPOSAL;
		this.customerName = groupFarmerProposal.getOrganization().getName();
		this.idNo = groupFarmerProposal.getOrganization() == null ? "" : groupFarmerProposal.getOrganization().getRegNo();
		this.idType = null;
		this.premium = groupFarmerProposal.getPremium();
		this.sumInsured = groupFarmerProposal.getTotalSI();
		this.commissionStartDate = commissionStartDate;
		this.paymentType = groupFarmerProposal.getPaymentType().getName();
		this.commission = commission;
		this.period=1;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public PolicyReferenceType getPolicyReferenceType() {
		return policyReferenceType;
	}

	public void setPolicyReferenceType(PolicyReferenceType policyReferenceType) {
		this.policyReferenceType = policyReferenceType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public Double getPremium() {
		return premium;
	}

	public void setPremium(Double premium) {
		this.premium = premium;
	}

	public Double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(Double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public Date getCommissionStartDate() {
		return commissionStartDate;
	}

	public void setCommissionStartDate(Date commissionStartDate) {
		this.commissionStartDate = commissionStartDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}
}
