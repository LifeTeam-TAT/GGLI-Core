package org.ace.insurance.renewal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.interfaces.IDataModel;

public class RenewalNotification implements ISorter, IDataModel {
	private static final long serialVersionUID = 1L;

	private String policyNo;
	private String policyId;
	private String customerName;
	private String regisNo;
	private Date endDate;
	private int days;
	private boolean isMultipleVehicle;
	private List<String> buildingNameList;
	private String buildingAddress;

	public RenewalNotification() {

	}

	public RenewalNotification(String policyId, String policyNo, String customerName, String regisNo, Date endDate, int days, boolean isMultipleVehicle) {
		this.policyId = policyId;
		this.policyNo = policyNo;
		this.customerName = customerName;
		this.regisNo = regisNo;
		this.endDate = endDate;
		this.days = days;
		this.isMultipleVehicle = isMultipleVehicle;
	}

	public RenewalNotification(String policyId, String policyNo, String customerName, String buildingName, String buildingAddress, Date endDate, int days) {
		this.policyId = policyId;
		this.policyNo = policyNo;
		this.customerName = customerName;
		this.endDate = endDate;
		this.days = days;
		this.buildingAddress = buildingAddress;
		getBuildingNameList().add(buildingName);

	}

	public RenewalNotification(List<RenewalNotification> renewalList) {
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getRegisNo() {
		return regisNo;
	}

	public void setRegisNo(String regisNo) {
		this.regisNo = regisNo;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public boolean isMultipleVehicle() {
		return isMultipleVehicle;
	}

	public void setMultipleVehicle(boolean isMultipleVehicle) {
		this.isMultipleVehicle = isMultipleVehicle;
	}

	public List<String> getBuildingNameList() {
		if (buildingNameList == null) {
			buildingNameList = new ArrayList<String>();
		}
		return buildingNameList;
	}

	public void setBuildingNameList(List<String> buildingNameList) {
		this.buildingNameList = buildingNameList;
	}

	public void setBuildingAddress(String buildingAddress) {
		this.buildingAddress = buildingAddress;
	}

	public void addBuildingName(String buildingName) {
		getBuildingNameList().add(buildingName);
	}

	public String getBuildingAddress() {
		return buildingAddress;
	}

	public String getBuildingName() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < getBuildingNameList().size(); i++) {
			buffer.append(getBuildingNameList().get(i));
			if (getBuildingNameList().size() > (i + 1)) {
				buffer.append(", ");
			}
		}
		return buffer.toString();
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

	@Override
	public String getId() {
		return policyNo;
	}

}
