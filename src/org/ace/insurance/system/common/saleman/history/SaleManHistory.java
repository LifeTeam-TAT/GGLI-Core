package org.ace.insurance.system.common.saleman.history;

import java.io.Serializable;
import java.util.Date;
import java.util.StringTokenizer;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.ViberContent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.SALEMANHISTORY)
@TableGenerator(name = "SALEMANHISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "SALEMANHISTORY_GEN", allocationSize = 10)
@Access(value = AccessType.FIELD)
public class SaleManHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Transient
	private String id;
	@Transient
	private String prefix;
	private String initialId;
	private String codeNo;
	private String licenseNo;
	private String idNo;
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@Enumerated(EnumType.STRING)
	private IdType idType;

	@Embedded
	private Name name;

	@AttributeOverrides({ @AttributeOverride(name = "phone", column = @Column(name = "VoicePhoneNo")), @AttributeOverride(name = "mobile", column = @Column(name = "SMSPhoneNo")) })
	@Embedded
	private ContentInfo contentInfo;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "viberPhone", column = @Column(name = "ViberPhoneNo")) })
	private ViberContent viberContent;

	@Embedded
	@AttributeOverride(name = "permanentAddress", column = @Column(name = "ADDRESS"))
	@AssociationOverride(name = "township", joinColumns = @JoinColumn(name = "TOWNSHIPID"))
	private PermanentAddress address;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@Version
	private int version;

	@Transient
	private String townshipCode;

	@Transient
	private String provinceCode;

	@Transient
	private String idConditionType;

	public SaleManHistory() {

	}

	public SaleManHistory(SaleMan saleMan) {
		this.id = saleMan.getId();
		this.address = saleMan.getAddress();
		this.branch = saleMan.getBranch();
		this.codeNo = saleMan.getCodeNo();
		this.contentInfo = saleMan.getContentInfo();
		this.dateOfBirth = saleMan.getDateOfBirth();
		this.idConditionType = saleMan.getIdConditionType();
		this.idNo = saleMan.getIdNo();
		this.idType = saleMan.getIdType();
		this.initialId = saleMan.getInitialId();
		this.licenseNo = saleMan.getLicenseNo();
		this.name = saleMan.getName();
		this.version = saleMan.getVersion();
		this.provinceCode = saleMan.getProvinceCode();
		this.townshipCode = saleMan.getTownshipCode();
		this.prefix = saleMan.getPrefix();

	}

	public SaleManHistory(SaleManHistory saleManhistory) {
		this.id = saleManhistory.getId();
		this.address = saleManhistory.getAddress();
		this.branch = saleManhistory.getBranch();
		this.codeNo = saleManhistory.getCodeNo();
		this.contentInfo = saleManhistory.getContentInfo();
		this.dateOfBirth = saleManhistory.getDateOfBirth();
		this.idConditionType = saleManhistory.getIdConditionType();
		this.idNo = saleManhistory.getIdNo();
		this.idType = saleManhistory.getIdType();
		this.initialId = saleManhistory.getInitialId();
		this.licenseNo = saleManhistory.getLicenseNo();
		this.name = saleManhistory.getName();
		this.version = saleManhistory.getVersion();
		this.provinceCode = saleManhistory.getProvinceCode();
		this.townshipCode = saleManhistory.getTownshipCode();
		this.prefix = saleManhistory.getPrefix();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "SALEMANHISTORY_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
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

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getFullName() {
		return name.getFullName();
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

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public ContentInfo getContentInfo() {
		return contentInfo;
	}

	public void setContentInfo(ContentInfo contentInfo) {
		this.contentInfo = contentInfo;
	}

	public ViberContent getViberContent() {
		if (viberContent == null) {
			this.viberContent = new ViberContent();
		}
		return this.viberContent;
	}

	public void setViberContent(ViberContent viberContent) {
		this.viberContent = viberContent;
	}

	public PermanentAddress getAddress() {
		return address;
	}

	public void setAddress(PermanentAddress address) {
		this.address = address;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void loadFullIdNo() {
		if (idType.equals(IdType.NRCNO) && (idNo != null || !idNo.isEmpty()) && idNo.matches("[0-9]{1,2}/[aA-aZ]*\\([aA-zZ]{1}\\)[0-9]*")) {
			StringTokenizer token = new StringTokenizer(idNo, "/()");
			provinceCode = token.nextToken();
			townshipCode = token.nextToken();
			idConditionType = token.nextToken();
			idNo = token.nextToken();
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO) || idType.equals(IdType.STILL_APPLYING)) {
			idNo = idNo == null || id.isEmpty() ? "STILL_APPLYING" : idNo;
		}
	}

	public void setFullIdNO(String fullIdNo) {
		if (idType.equals(IdType.NRCNO)) {
			this.idNo = provinceCode + "/" + townshipCode + "(" + idConditionType + ")" + fullIdNo;
		} else if (idType.equals(IdType.FRCNO) || idType.equals(IdType.PASSPORTNO)) {
			this.idNo = fullIdNo;
		}
	}

	public String getTownshipCode() {
		return townshipCode;
	}

	public void setTownshipCode(String townshipCode) {
		this.townshipCode = townshipCode;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getIdConditionType() {
		return idConditionType;
	}

	public void setIdConditionType(String idConditionType) {
		this.idConditionType = idConditionType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((codeNo == null) ? 0 : codeNo.hashCode());
		result = prime * result + ((contentInfo == null) ? 0 : contentInfo.hashCode());
		result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idConditionType == null) ? 0 : idConditionType.hashCode());
		result = prime * result + ((idNo == null) ? 0 : idNo.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
		result = prime * result + ((initialId == null) ? 0 : initialId.hashCode());
		result = prime * result + ((licenseNo == null) ? 0 : licenseNo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((provinceCode == null) ? 0 : provinceCode.hashCode());
		result = prime * result + ((townshipCode == null) ? 0 : townshipCode.hashCode());
		result = prime * result + version;
		result = prime * result + ((viberContent == null) ? 0 : viberContent.hashCode());
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
		SaleManHistory other = (SaleManHistory) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (codeNo == null) {
			if (other.codeNo != null)
				return false;
		} else if (!codeNo.equals(other.codeNo))
			return false;
		if (contentInfo == null) {
			if (other.contentInfo != null)
				return false;
		} else if (!contentInfo.equals(other.contentInfo))
			return false;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idConditionType == null) {
			if (other.idConditionType != null)
				return false;
		} else if (!idConditionType.equals(other.idConditionType))
			return false;
		if (idNo == null) {
			if (other.idNo != null)
				return false;
		} else if (!idNo.equals(other.idNo))
			return false;
		if (idType != other.idType)
			return false;
		if (initialId == null) {
			if (other.initialId != null)
				return false;
		} else if (!initialId.equals(other.initialId))
			return false;
		if (licenseNo == null) {
			if (other.licenseNo != null)
				return false;
		} else if (!licenseNo.equals(other.licenseNo))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (provinceCode == null) {
			if (other.provinceCode != null)
				return false;
		} else if (!provinceCode.equals(other.provinceCode))
			return false;
		if (townshipCode == null) {
			if (other.townshipCode != null)
				return false;
		} else if (!townshipCode.equals(other.townshipCode))
			return false;
		if (version != other.version)
			return false;
		if (viberContent == null) {
			if (other.viberContent != null)
				return false;
		} else if (!viberContent.equals(other.viberContent))
			return false;
		return true;
	}

}
