package org.ace.insurance.mobile.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.mobile.securityAnswer.SecurityAnswer;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.MOBILE_USER)
@TableGenerator(name = "MOBILEUSER_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "MOBILEUSER_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "MobileUser.findAll", query = "SELECT m FROM MobileUser m ORDER BY m.firstName ASC"),
		@NamedQuery(name = "MobileUser.findById", query = "SELECT m FROM MobileUser m WHERE m.id = :id") })
@Access(value = AccessType.FIELD)
public class MobileUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;
	@Transient
	private String prefix;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String email;
	private String password;
	private String activatedCode;
	// private boolean isActived;
	private boolean isActivate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date activatedDate;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "MOBILEUSERID", referencedColumnName = "ID")
	private List<SecurityAnswer> securityAnswerList;

	@Version
	private int version;

	public MobileUser() {
	}

	public MobileUser(MU001 mobileUserDTO) {
		this.id = mobileUserDTO.getId();
		this.firstName = mobileUserDTO.getFirstName();
		this.lastName = mobileUserDTO.getLastName();
		this.mobileNumber = mobileUserDTO.getMobileNumber();
		this.email = mobileUserDTO.getEmail();
		this.password = mobileUserDTO.getPassword();
		this.activatedCode = mobileUserDTO.getActivatedCode();
		this.isActivate = mobileUserDTO.isActivate();
		this.activatedDate = mobileUserDTO.getActivatedDate();
		this.version = mobileUserDTO.getVersion();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MOBILEUSER_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public List<SecurityAnswer> getSecurityAnswerList() {
		if (securityAnswerList == null) {
			securityAnswerList = new ArrayList<SecurityAnswer>();
		}
		return securityAnswerList;
	}

	public void setSecurityAnswerList(List<SecurityAnswer> securityAnswerList) {
		this.securityAnswerList = securityAnswerList;
	}

	public void addSecurityAnswer(SecurityAnswer securityAnswer) {
		getSecurityAnswerList().add(securityAnswer);
	}

	/**
	 * @return the activatedCode
	 */
	public String getActivatedCode() {
		return activatedCode;
	}

	/**
	 * @param activatedCode
	 *            the activatedCode to set
	 */
	public void setActivatedCode(String activatedCode) {
		this.activatedCode = activatedCode;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 *            the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the isActivate
	 */
	public boolean isActivate() {
		return isActivate;
	}

	/**
	 * @param isActivate
	 *            the isActivate to set
	 */
	public void setActivate(boolean isActivate) {
		this.isActivate = isActivate;
	}

	/**
	 * @return the activatedDate
	 */
	public Date getActivatedDate() {
		return activatedDate;
	}

	/**
	 * @param activatedDate
	 *            the activatedDate to set
	 */
	public void setActivatedDate(Date activatedDate) {
		this.activatedDate = activatedDate;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	/*************
	 * Generated Method
	 */
	public String getName() {
		String result = "";
		if (firstName != null && !firstName.isEmpty()) {
			result += firstName.trim();
		}
		if (lastName != null && !lastName.isEmpty())
			result += " " + lastName.trim();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activatedCode == null) ? 0 : activatedCode.hashCode());
		result = prime * result + ((activatedDate == null) ? 0 : activatedDate.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isActivate ? 1231 : 1237);
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((securityAnswerList == null) ? 0 : securityAnswerList.hashCode());
		result = prime * result + version;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MobileUser other = (MobileUser) obj;
		if (activatedCode == null) {
			if (other.activatedCode != null)
				return false;
		} else if (!activatedCode.equals(other.activatedCode))
			return false;
		if (activatedDate == null) {
			if (other.activatedDate != null)
				return false;
		} else if (!activatedDate.equals(other.activatedDate))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isActivate != other.isActivate)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (securityAnswerList == null) {
			if (other.securityAnswerList != null)
				return false;
		} else if (!securityAnswerList.equals(other.securityAnswerList))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
