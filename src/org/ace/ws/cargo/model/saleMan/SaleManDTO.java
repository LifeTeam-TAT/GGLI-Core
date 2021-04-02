package org.ace.ws.cargo.model.saleMan;

import java.io.Serializable;

/**
 * @author HlaingWinTunn
 *
 */
public class SaleManDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String initialId;
	private String codeNo;
	private String licenseNo;
	private String idNo;
	private String dateOfBirth;
	private String idType;
	private String name;
	private String mobile;
	private String fax;
	private String email;
	private String address;
	private String branch;
	private int version;
	private String fullName;

	public SaleManDTO() {

	}

	public SaleManDTO(String id, String initialId, String codeNo, String licenseNo, String idNo, String dateOfBirth, String idType, String name, String mobile, String fax,
			String email, String address, String branch, int version) {
		super();
		this.id = id;
		this.initialId = initialId;
		this.codeNo = codeNo;
		this.licenseNo = licenseNo;
		this.idNo = idNo;
		this.dateOfBirth = dateOfBirth;
		this.idType = idType;
		this.name = name;
		this.mobile = mobile;
		this.fax = fax;
		this.email = email;
		this.address = address;
		this.branch = branch;
		this.version = version;
	}

	public SaleManDTO(String fullName) {
		super();
		this.fullName = fullName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
	}

	public String getCodeNo() {
		return codeNo;
	}

	public void setCodeNo(String codeNo) {
		this.codeNo = codeNo;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
