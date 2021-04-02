/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.branch;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.township.Township;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.BRANCH)
@TableGenerator(name = "BRANCH_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "BRANCH_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "Branch.findAll", query = "SELECT b FROM Branch b ORDER BY b.name ASC"),
		@NamedQuery(name = "Branch.findByCode", query = "SELECT b FROM Branch b WHERE b.branchCode = :branchCode"),
		@NamedQuery(name = "Branch.findById", query = "SELECT b FROM Branch b WHERE b.id = :id") })
@Access(value = AccessType.FIELD)
public class Branch implements Serializable {
	private static final long serialVersionUID = 1680499663032866031L;
	@Transient
	private String id;
	@Transient
	private String prefix;
	private String name;
	private String branchCode;
	private String Address;
	@Convert(disableConversion = true)
	private String email;
	private String phone;
	private double longitude;
	private double latitude;
	private String url;
	private boolean isCoInsuAccess;
	private boolean status;

	private String description;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOWNSHIPID", referencedColumnName = "ID")
	private Township township;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ENTITYSID", referencedColumnName = "ID")
	private Entitys entity;

	private String payableACName;

	private String receivableACName;

	@Version
	private int version;

	public Branch() {
	}

	public Branch(Branch branch) {
		this.id = branch.getId();
		this.name = branch.getName();
		this.Address = branch.getAddress();
		this.branchCode = branch.getBranchCode();
		this.description = branch.getDescription();
		this.township = branch.getTownship();
		this.isCoInsuAccess = false;
		this.email = branch.getEmail();
		this.phone = branch.getPhone();
		this.latitude = branch.getLatitude();
		this.longitude = branch.getLongitude();
		this.url = branch.getUrl();
		this.receivableACName = branch.getReceivableACName();
		this.payableACName = branch.getPayableACName();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "BRANCH_GEN")
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public Township getTownship() {
		return township;
	}

	public void setTownship(Township township) {
		this.township = township;
	}

	public boolean isCoInsuAccess() {
		return isCoInsuAccess;
	}

	public void setCoInsuAccess(boolean isCoInsuAccess) {
		this.isCoInsuAccess = isCoInsuAccess;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
	}

	public String getPayableACName() {
		return payableACName;
	}

	public void setPayableACName(String payableACName) {
		this.payableACName = payableACName;
	}

	public String getReceivableACName() {
		return receivableACName;
	}

	public void setReceivableACName(String receivableACName) {
		this.receivableACName = receivableACName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Address == null) ? 0 : Address.hashCode());
		result = prime * result + ((branchCode == null) ? 0 : branchCode.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isCoInsuAccess ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((payableACName == null) ? 0 : payableACName.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((receivableACName == null) ? 0 : receivableACName.hashCode());
		result = prime * result + (status ? 1231 : 1237);
		result = prime * result + ((township == null) ? 0 : township.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Branch other = (Branch) obj;
		if (Address == null) {
			if (other.Address != null)
				return false;
		} else if (!Address.equals(other.Address))
			return false;
		if (branchCode == null) {
			if (other.branchCode != null)
				return false;
		} else if (!branchCode.equals(other.branchCode))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isCoInsuAccess != other.isCoInsuAccess)
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (payableACName == null) {
			if (other.payableACName != null)
				return false;
		} else if (!payableACName.equals(other.payableACName))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (receivableACName == null) {
			if (other.receivableACName != null)
				return false;
		} else if (!receivableACName.equals(other.receivableACName))
			return false;
		if (status != other.status)
			return false;
		if (township == null) {
			if (other.township != null)
				return false;
		} else if (!township.equals(other.township))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}