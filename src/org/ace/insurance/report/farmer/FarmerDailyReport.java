package org.ace.insurance.report.farmer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.ace.insurance.common.ISorter;

public class FarmerDailyReport implements ISorter {
	private static final long serialVersionUID = 288430480510991981L;

	private String id;
	private String policyNo;
	private String groupFarmerProposalNo;
	private String insuredPersonName;
	private String fullIdNo;
	private String address;
	private List<FarmerBeneficiaryReportDTO> beneficiaryList = new ArrayList<FarmerBeneficiaryReportDTO>();
	private Date activedPolicyStartDate;
	private double sumInsured;
	private double premium;
	private String agentName;
	private String remark;
	private String proposalNo;
	private String salepoint;
	private String branch;

	// For Beneficiary Data
	private StringTokenizer st1;
	private StringTokenizer st2;
	private List<String> nameList = new ArrayList<String>();
	private List<String> fullIdNoList = new ArrayList<String>();
	private List<String> addressList = new ArrayList<String>();
	private String[] dataArray = new String[3];
	private final String PERSON_DELIMITER = "#";
	private final String DATA_DELIMITER = "$^";

	public FarmerDailyReport() {

	}

	public FarmerDailyReport(FarmerDailyReportView view) {
		id = view.getId();
		policyNo = view.getPolicyNo();
		groupFarmerProposalNo = view.getGroupFarmerProposalNo();
		proposalNo = view.getProposalNo();
		insuredPersonName = view.getInsuredPersonName();
		fullIdNo = view.getFullIdNo();
		address = view.getAddress();

		/* Set Beneficiary Data */
		if (null != view.getBeneficiaryInfo()) {
			setBeneficiaryData(view.getBeneficiaryInfo());
		}

		activedPolicyStartDate = view.getActivedPolicyStartDate();
		sumInsured = view.getSumInsured();
		premium = view.getPremium();
		agentName = view.getAgentName();
		remark = view.getRemark();
		salepoint = view.getSalePointName();
		branch = view.getBranchName();
	}

	private void setBeneficiaryData(String beneficiaryInfo) {
		st1 = new StringTokenizer(beneficiaryInfo, PERSON_DELIMITER);
		while (st1.hasMoreTokens()) {
			st2 = new StringTokenizer(st1.nextToken(), DATA_DELIMITER);
			int i = 0;
			while (st2.hasMoreTokens()) {
				dataArray[i] = st2.nextToken();
				i++;
			}
			beneficiaryList.add(new FarmerBeneficiaryReportDTO(dataArray[0], dataArray[1], dataArray[2]));
			nameList.add(dataArray[0]);
			fullIdNoList.add(dataArray[1]);
			addressList.add(dataArray[2]);
		}
	}

	public String getId() {
		return id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public String getAddress() {
		return address;
	}

	public List<FarmerBeneficiaryReportDTO> getBeneficiaryList() {
		return beneficiaryList;
	}

	public List<String> getNameList() {
		return nameList;
	}

	public List<String> getFullIdNoList() {
		return fullIdNoList;
	}

	public List<String> getAddressList() {
		return addressList;
	}

	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getRemark() {
		return remark;
	}

	public String getGroupFarmerProposalNo() {
		return groupFarmerProposalNo;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

	public String getSalepoint() {
		return salepoint;
	}

	public void setSalepoint(String salepoint) {
		this.salepoint = salepoint;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

}
