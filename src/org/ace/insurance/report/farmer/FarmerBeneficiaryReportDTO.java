package org.ace.insurance.report.farmer;

public class FarmerBeneficiaryReportDTO {
	private String name;
	private String fullIdNo;
	private String address;
	
	public FarmerBeneficiaryReportDTO(String name, String fullIdNo, String address) {
		this.name = name;
		this.fullIdNo = fullIdNo;
		this.address = address;
	}
	
	public String getName() {
		return name;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public String getAddress() {
		return address;
	}
	
}
