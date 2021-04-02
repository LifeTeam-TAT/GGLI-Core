package org.ace.insurance.report.agent.view;

import java.io.Serializable;
import java.util.Date;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.interfaces.IDataModel;

public class StaffAgentCommissionInfo implements Serializable, IDataModel {
	private static final long serialVersionUID = 1L;
	private String id;
	private String policyNo;
	private String cashReceiptNo;
	private String agentName;
	private String agentCode;
	private String mobile;
	private String address;
	private double totalComission;
	//private String licenseNo;
	private Date startDate;
	private Date endDate;
	private String typeOfProduct;
	private PolicyReferenceType referenceType;
	private String sanctionNo;
	private Date sanctionDate;
	private String currencyCode;
	private String currencySymbol;
	private String bpmsReceiptNo;
	private String branchName;

	public StaffAgentCommissionInfo() {

	}

	public StaffAgentCommissionInfo(String id, String policyNo, String cashReceiptNo, String agentName, String agentCode, String mobile, String address, double totalComission,
			 Date startDate, Date endDate, String typeOfProduct, PolicyReferenceType referenceType, String sanctionNo) {
		this.id = id;
		this.policyNo = policyNo;
		this.cashReceiptNo = cashReceiptNo;
		this.agentName = agentName;
		this.agentCode = agentCode;
		this.mobile = mobile;
		this.address = address;
		this.totalComission = totalComission;
		//this.licenseNo = licenseNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.typeOfProduct = typeOfProduct;
		this.referenceType = referenceType;
		this.sanctionNo = sanctionNo;
	}

	public StaffAgentCommissionInfo(String id, String policyNo, String cashReceiptNo, String agentName, String agentCode, String mobile, String address, double totalComission,
			 Date startDate, Date endDate, String typeOfProduct, PolicyReferenceType referenceType, String sanctionNo, Date sanctionDate, String currencyCode) {
		this.id = id;
		this.policyNo = policyNo;
		this.cashReceiptNo = cashReceiptNo;
		this.agentName = agentName;
		this.agentCode = agentCode;
		this.mobile = mobile;
		this.address = address;
		this.totalComission = totalComission;
		//this.licenseNo = licenseNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.typeOfProduct = typeOfProduct;
		this.referenceType = referenceType;
		this.sanctionNo = sanctionNo;
		this.sanctionDate = sanctionDate;
		this.currencyCode = currencyCode;
	}

	public StaffAgentCommissionInfo(String id, String policyNo, String cashReceiptNo, String bpmsReceiptNo, String agentName, String agentCode, String mobile, String address,
			double totalComission,Date startDate, Date endDate, String typeOfProduct, PolicyReferenceType referenceType, String sanctionNo, String currencyCode,
			String currencySymbol, String branchName) {
		this.id = id;
		this.policyNo = policyNo;
		this.cashReceiptNo = cashReceiptNo;
		this.agentName = agentName;
		this.agentCode = agentCode;
		this.mobile = mobile;
		this.address = address;
		this.totalComission = totalComission;
		//this.licenseNo = licenseNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.typeOfProduct = typeOfProduct;
		this.referenceType = referenceType;
		this.sanctionNo = sanctionNo;
		this.currencyCode = currencyCode;
		this.currencySymbol = currencySymbol;
		this.bpmsReceiptNo = bpmsReceiptNo;
		this.branchName = branchName;
	}

	public StaffAgentCommissionInfo(String id, String policyNo, String cashReceiptNo, String agentName, String agentCode, String mobile, String address, double totalComission,
			 Date startDate, Date endDate, String typeOfProduct, PolicyReferenceType referenceType, String sanctionNo, Date sanctionDate, String currencyCode,
			String currencySymbol, String bpmsReceiptNo) {
		super();
		this.id = id;
		this.policyNo = policyNo;
		this.cashReceiptNo = cashReceiptNo;
		this.agentName = agentName;
		this.agentCode = agentCode;
		this.mobile = mobile;
		this.address = address;
		this.totalComission = totalComission;
		//this.licenseNo = licenseNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.typeOfProduct = typeOfProduct;
		this.referenceType = referenceType;
		this.sanctionNo = sanctionNo;
		this.sanctionDate = sanctionDate;
		this.currencyCode = currencyCode;
		this.currencySymbol = currencySymbol;
		this.bpmsReceiptNo = bpmsReceiptNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public String getMobile() {
		return mobile;
	}

	public String getAddress() {
		return address;
	}

	public double getTotalComission() {
		return totalComission;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setTotalComission(double totalComission) {
		this.totalComission = totalComission;
	}

	/*
	 * public String getLicenseNo() { return licenseNo; }
	 * 
	 * public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
	 */

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTypeOfProduct() {
		return typeOfProduct;
	}

	public void setTypeOfProduct(String typeOfProduct) {
		this.typeOfProduct = typeOfProduct;
	}

	public PolicyReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(PolicyReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCashReceiptNo() {
		return cashReceiptNo;
	}

	public void setCashReceiptNo(String cashReceiptNo) {
		this.cashReceiptNo = cashReceiptNo;
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

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getBpmsReceiptNo() {
		return bpmsReceiptNo;
	}

	public void setBpmsReceiptNo(String bpmsReceiptNo) {
		this.bpmsReceiptNo = bpmsReceiptNo;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((agentCode == null) ? 0 : agentCode.hashCode());
		result = prime * result + ((agentName == null) ? 0 : agentName.hashCode());
		result = prime * result + ((bpmsReceiptNo == null) ? 0 : bpmsReceiptNo.hashCode());
		result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
		result = prime * result + ((cashReceiptNo == null) ? 0 : cashReceiptNo.hashCode());
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((currencySymbol == null) ? 0 : currencySymbol.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
	//	result = prime * result + ((licenseNo == null) ? 0 : licenseNo.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((referenceType == null) ? 0 : referenceType.hashCode());
		result = prime * result + ((sanctionDate == null) ? 0 : sanctionDate.hashCode());
		result = prime * result + ((sanctionNo == null) ? 0 : sanctionNo.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(totalComission);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((typeOfProduct == null) ? 0 : typeOfProduct.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaffAgentCommissionInfo other = (StaffAgentCommissionInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (agentCode == null) {
			if (other.agentCode != null)
				return false;
		} else if (!agentCode.equals(other.agentCode))
			return false;
		if (agentName == null) {
			if (other.agentName != null)
				return false;
		} else if (!agentName.equals(other.agentName))
			return false;
		if (bpmsReceiptNo == null) {
			if (other.bpmsReceiptNo != null)
				return false;
		} else if (!bpmsReceiptNo.equals(other.bpmsReceiptNo))
			return false;
		if (branchName == null) {
			if (other.branchName != null)
				return false;
		} else if (!branchName.equals(other.branchName))
			return false;
		if (cashReceiptNo == null) {
			if (other.cashReceiptNo != null)
				return false;
		} else if (!cashReceiptNo.equals(other.cashReceiptNo))
			return false;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		if (currencySymbol == null) {
			if (other.currencySymbol != null)
				return false;
		} else if (!currencySymbol.equals(other.currencySymbol))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		/*
		 * if (licenseNo == null) { if (other.licenseNo != null) return false; } else if
		 * (!licenseNo.equals(other.licenseNo)) return false;
		 */
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		} else if (!mobile.equals(other.mobile))
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (referenceType != other.referenceType)
			return false;
		if (sanctionDate == null) {
			if (other.sanctionDate != null)
				return false;
		} else if (!sanctionDate.equals(other.sanctionDate))
			return false;
		if (sanctionNo == null) {
			if (other.sanctionNo != null)
				return false;
		} else if (!sanctionNo.equals(other.sanctionNo))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (Double.doubleToLongBits(totalComission) != Double.doubleToLongBits(other.totalComission))
			return false;
		if (typeOfProduct == null) {
			if (other.typeOfProduct != null)
				return false;
		} else if (!typeOfProduct.equals(other.typeOfProduct))
			return false;
		return true;
	}

}
