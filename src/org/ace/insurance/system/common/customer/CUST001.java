package org.ace.insurance.system.common.customer;

import java.util.Date;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.Name;

public class CUST001 implements ISorter {
	private static final long serialVersionUID = 1L;

	private String id;
	private String idNo;
	private String fullIdNo;
	private IdConditionType idConditionType;
	private Gender gender;
	private String initialId;
	private String fullName;
	private String phone;
	private String mobilePh;
	private String email;
	private String accountNo;
	private Date dateOfBirth;
	private String fatherName;

	public CUST001() {
	}

	public CUST001(String id, String initialId, Name name, String mobile, String email, String phone, String accountNo, Date dateOfBirth, Gender gender, String fatherName,
			String fullIdNo) {
		this.id = id;
		this.fullIdNo = fullIdNo;
		this.gender = gender;
		this.initialId = initialId;
		this.fullName = initialId + " " + name.getFullName();
		this.phone = phone;
		this.mobilePh = mobile;
		this.email = email;
		this.accountNo = accountNo;
		this.dateOfBirth = dateOfBirth;
		this.fatherName = fatherName;
	}

	public CUST001(Customer customer) {
		this.id = customer.getId();
		this.idNo = customer.getIdNo();
		this.fullIdNo = customer.getFullIdNo();
		this.gender = customer.getGender();
		this.initialId = customer.getInitialId();
		this.fullName = customer.getFullName();
		this.phone = customer.getContentInfo().getPhone();
		this.mobilePh = customer.getContentInfo().getMobile();
		this.email = customer.getContentInfo().getEmail();
		this.accountNo = customer.getBankAccountNo();
		this.dateOfBirth = customer.getDateOfBirth();
		this.fatherName = customer.getFatherName();
	}

	public String getId() {
		return id;
	}

	public String getIdNo() {
		return idNo;
	}

	public String getFullIdNo() {
		return fullIdNo;
	}

	public IdConditionType getIdConditionType() {
		return idConditionType;
	}

	public Gender getGender() {
		return gender;
	}

	public String getInitialId() {
		return initialId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPhone() {
		return phone;
	}

	public String getMobilePh() {
		return mobilePh;
	}

	public String getEmail() {
		return email;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public String getFatherName() {
		return fatherName;
	}

	@Override
	public String getRegistrationNo() {
		return id;
	}

}
