package org.ace.insurance.system.common.ggiorganization;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.java.component.FormatID;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.GGIORGANIZATION)
@TableGenerator(name = "GGIORGANIZATION_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "GGIORGANIZATION_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
@Access(value = AccessType.FIELD)
public class GGIOrganization implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Transient
	private String id;
	private String name;
	
	@Transient
	private String prefix;
	@Column(name = "CODE_NO")
	private String codeNo;
	@Column(name = "REG_NO")
	private String regNo;
	@Column(name = "OWNER_NAME")
	private String OwnerName;
	private int activePolicy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date activedDate;
	private String description;
	
	@Embedded
	@AttributeOverride(name = "permanentAddress", column = @Column(name = "ADDRESS"))
	@AssociationOverride(name = "township", joinColumns = @JoinColumn(name = "TOWNSHIP_ID"))
	private PermanentAddress address;

	@Embedded
	private ContentInfo contentInfo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	private int version;

	public GGIOrganization() {
		address = new PermanentAddress();
		contentInfo = new ContentInfo();
		commonCreateAndUpateMarks = new CommonCreateAndUpateMarks();
	}

	public int getVersion() {
		return version;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GGIORGANIZATION_GEN")
	@Access(AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCodeNo() {
		return codeNo;
	}

	public void setCodeNo(String codeNo) {
		this.codeNo = codeNo;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getOwnerName() {
		return OwnerName;
	}

	public void setOwnerName(String ownerName) {
		OwnerName = ownerName;
	}

	public int getActivePolicy() {
		return activePolicy;
	}

	public void setActivePolicy(int activePolicy) {
		this.activePolicy = activePolicy;
	}

	public Date getActivedDate() {
		return activedDate;
	}

	public void setActivedDate(Date activedDate) {
		this.activedDate = activedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PermanentAddress getAddress() {
		return address;
	}

	public void setAddress(PermanentAddress address) {
		this.address = address;
	}

	public ContentInfo getContentInfo() {
		return contentInfo;
	}

	public void setContentInfo(ContentInfo contentInfo) {
		this.contentInfo = contentInfo;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((OwnerName == null) ? 0 : OwnerName.hashCode());
		result = prime * result + activePolicy;
		result = prime * result + ((activedDate == null) ? 0 : activedDate.hashCode());
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((codeNo == null) ? 0 : codeNo.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((contentInfo == null) ? 0 : contentInfo.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((regNo == null) ? 0 : regNo.hashCode());
		result = prime * result + version;
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
		GGIOrganization other = (GGIOrganization) obj;
		if (OwnerName == null) {
			if (other.OwnerName != null)
				return false;
		} else if (!OwnerName.equals(other.OwnerName))
			return false;
		if (activePolicy != other.activePolicy)
			return false;
		if (activedDate == null) {
			if (other.activedDate != null)
				return false;
		} else if (!activedDate.equals(other.activedDate))
			return false;
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
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (contentInfo == null) {
			if (other.contentInfo != null)
				return false;
		} else if (!contentInfo.equals(other.contentInfo))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (regNo == null) {
			if (other.regNo != null)
				return false;
		} else if (!regNo.equals(other.regNo))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
}
