package org.ace.insurance.report.medical;

import java.util.Date;

import org.ace.insurance.report.medical.view.HealthDailyReportView;

public class HealthDailyReportDTO {

	private Date paymentDate;
	private String proposalNo;
	private String policyNo;
	private String insuredName;
	private String beneficiaryName;
	private int basicUnit;
	private int additionalUnit;
	private int option1Unit;
	private int option2Unit;
	private double premium;
	private String premiumMode;
	private Date startDate;
	private Date endDate;
	private String agentName;
	private String branch;
	private String salePointName;
	private String salePointCode;
	private String branchName;

	public HealthDailyReportDTO() {

	}

	public HealthDailyReportDTO(HealthDailyReportView view) {
		this.paymentDate = view.getPaymentDate();
		this.proposalNo = view.getProposalNo();
		this.policyNo = view.getPolicyNo();
		this.insuredName = view.getInsuredName();
		this.beneficiaryName = view.getBeneficiaryName();
		this.basicUnit = view.getBasicUnit();
		this.additionalUnit = view.getAdditionalUnit();
		this.option1Unit = view.getOption1Unit();
		this.option2Unit = view.getOption2Unit();
		this.premium = view.getPremium();
		this.premiumMode = view.getPremiumMode();
		this.startDate = view.getStartDate();
		this.endDate = view.getEndDate();
		this.agentName = view.getAgentName();
		this.branch = view.getBranch();
		this.salePointName = view.getSalePointName();
		this.salePointCode = view.getSalePointCode();
		this.branchName = view.getBranch();
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public int getBasicUnit() {
		return basicUnit;
	}

	public int getAdditionalUnit() {
		return additionalUnit;
	}

	public int getOption1Unit() {
		return option1Unit;
	}

	public int getOption2Unit() {
		return option2Unit;
	}

	public double getPremium() {
		return premium;
	}

	public String getPremiumMode() {
		return premiumMode;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getBranch() {
		return branch;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getSalePointCode() {
		return salePointCode;
	}

	public void setSalePointCode(String salePointCode) {
		this.salePointCode = salePointCode;
	}

}
