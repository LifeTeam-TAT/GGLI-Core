/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.USERROLE)
@TableGenerator(name = "ROLE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "ROLE_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "Role.findAll", query = "SELECT m FROM Role m ORDER BY m.name ASC"),
		@NamedQuery(name = "Role.findById", query = "SELECT m FROM Role m WHERE m.id = :id") })
@Access(value = AccessType.FIELD)
public class Role implements Serializable {

	@Transient
	private String id;
	@Transient
	private String prefix;
	@Convert(disableConversion = true)
	private String name;

	@Version
	private int version;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ROLE_RESOURCE_ITEMS", joinColumns = { @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "RESITEM_ID", referencedColumnName = "ID") })
	private List<ResourceItem> resourceItemList;

	public Role() {
	}

	public Role(String id, String name) {
		super();
		this.id = id;
		this.name = name;
		resourceItemList = new ArrayList<ResourceItem>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ROLE_GEN")
	@Access(value = AccessType.PROPERTY)
	@Convert(disableConversion = true)
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

	public List<ResourceItem> getResourceItemList() {
		return resourceItemList;
	}

	public void setResourceItemList(List<ResourceItem> resourceItemList) {
		this.resourceItemList = resourceItemList;
	}

	public void addResourceItemList(List<ResourceItem> itemList) {
		if (resourceItemList == null) {
			resourceItemList = new ArrayList<ResourceItem>();
		}
		Set<ResourceItem> set = new HashSet<ResourceItem>(resourceItemList);
		set.addAll(itemList);
		resourceItemList = new ArrayList<ResourceItem>(set);
	}

	public void addResourceItem(ResourceItem item) {
		if (resourceItemList == null) {
			resourceItemList = new ArrayList<ResourceItem>();
		}
		Set<ResourceItem> set = new HashSet<ResourceItem>(resourceItemList);
		set.add(item);
		resourceItemList = new ArrayList<ResourceItem>(set);
	}

	public void removeResourceItem(ResourceItem item) {
		if (resourceItemList == null) {
			resourceItemList = new ArrayList<ResourceItem>();
		}
		resourceItemList.remove(item);
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
		Role other = (Role) obj;
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
		if (version != other.version)
			return false;
		return true;
	}

}
