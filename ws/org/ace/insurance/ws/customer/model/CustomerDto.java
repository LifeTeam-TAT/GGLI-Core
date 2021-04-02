package org.ace.insurance.ws.customer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Customer")
public class CustomerDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "id", required = false)
	private String id;

	@XmlElement(name = "prefix", required = false)
	private String prefix;

	@XmlElement(name = "initialId", required = true)
	private String initialId;

	@XmlElement(name = "fatherName", required = false)
	private String fatherName;

	@XmlElement(name = "idNo", required = false)
	private String idNo;

	@XmlElement(name = "dateOfBirth", required = true)
	private Date dateOfBirth;

	@XmlElement(name = "labourNo", required = false)
	private String labourNo;

	@XmlElement(name = "birthMark", required = false)
	private String birthMark;

	@XmlElement(name = "salary", required = false)
	private String salary;

	@XmlElement(name = "closedPolicy", required = false)
	private int closedPolicy;

	@XmlElement(name = "activePolicy", required = false)
	private int activePolicy;

	@XmlElement(name = "bankAccountNo", required = false)
	private String bankAccountNo;

	@XmlElement(name = "gender", required = true)
	private Gender gender;

	@XmlElement(name = "idType", required = true)
	private IdType idType;

	@XmlElement(name = "maritalStatus", required = false)
	private MaritalStatus maritalStatus;

	@XmlElement(name = "officeAddress", required = false)
	private OfficeAddressDto officeAddress;

	@XmlElement(name = "permanentAddress", required = true)
	private PermanentAddressDto permanentAddress;

	@XmlElement(name = "residentAddress", required = true)
	private ResidentAddressDto residentAddress;

	@XmlElement(name = "contentInfo", required = false)
	private ContentInfoDto contentInfo;

	@XmlElement(name = "name", required = true)
	private NameDto name;

	@XmlElement(name = "familyInfo", required = false)
	private List<FamilyInfoDto> familyInfo;

	@XmlElement(name = "branchId", required = true)
	private String branchId;

	@XmlElement(name = "qualificationId", required = false)
	private String qualificationId;

	@XmlElement(name = "bankBranchId", required = false)
	private String bankBranchId;

	@XmlElement(name = "religionId", required = false)
	private String religionId;

	@XmlElement(name = "occupationId", required = false)
	private String occupationId;

	@XmlElement(name = "industryId", required = false)
	private String industryId;

	@XmlElement(name = "countryId", required = true)
	private String countryId;

	public CustomerDto() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getInitialId() {
		return initialId;
	}

	public void setInitialId(String initialId) {
		this.initialId = initialId;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getBirthMark() {
		return birthMark;
	}

	public String getSalary() {
		return salary;
	}

	public void setBirthMark(String birthMark) {
		this.birthMark = birthMark;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public int getClosedPolicy() {
		return closedPolicy;
	}

	public void setClosedPolicy(int closedPolicy) {
		this.closedPolicy = closedPolicy;
	}

	public int getActivePolicy() {
		return activePolicy;
	}

	public void setActivePolicy(int activePolicy) {
		this.activePolicy = activePolicy;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getLabourNo() {
		return labourNo;
	}

	public void setLabourNo(String labourNo) {
		this.labourNo = labourNo;
	}

	public OfficeAddressDto getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(OfficeAddressDto officeAddress) {
		this.officeAddress = officeAddress;
	}

	public PermanentAddressDto getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(PermanentAddressDto permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public ResidentAddressDto getResidentAddress() {
		return residentAddress;
	}

	public void setResidentAddress(ResidentAddressDto residentAddress) {
		this.residentAddress = residentAddress;
	}

	public ContentInfoDto getContentInfo() {
		return contentInfo;
	}

	public void setContentInfo(ContentInfoDto contentInfo) {
		this.contentInfo = contentInfo;
	}

	public NameDto getName() {
		return name;
	}

	public void setName(NameDto name) {
		this.name = name;
	}

	public List<FamilyInfoDto> getFamilyInfo() {
		if (familyInfo == null)
			familyInfo = new ArrayList<FamilyInfoDto>();
		return familyInfo;
	}

	public void setFamilyInfo(List<FamilyInfoDto> familyInfo) {
		this.familyInfo = familyInfo;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getQualificationId() {
		return qualificationId;
	}

	public void setQualificationId(String qualificationId) {
		this.qualificationId = qualificationId;
	}

	public String getBankBranchId() {
		return bankBranchId;
	}

	public void setBankBranchId(String bankBranchId) {
		this.bankBranchId = bankBranchId;
	}

	public String getReligionId() {
		return religionId;
	}

	public void setReligionId(String religionId) {
		this.religionId = religionId;
	}

	public String getOccupationId() {
		return occupationId;
	}

	public void setOccupationId(String occupationId) {
		this.occupationId = occupationId;
	}

	public String getIndustryId() {
		return industryId;
	}

	public void setIndustryId(String industryId) {
		this.industryId = industryId;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}
}
