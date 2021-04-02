package org.ace.insurance.system.common.classofinsurance;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.java.component.FormatID;

@Entity
@Table(name=TableName.CLASSOFINSURANCE)
@TableGenerator(name = "CLASSOFINSURANCE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "CLASSOFINSURANCE_GEN", allocationSize = 10)
@NamedQueries(value = {
		@NamedQuery(name = "ClassOfInsurance.findAll", query = "SELECT c FROM ClassOfInsurance  c ORDER BY c.name ASC"),
//		@NamedQuery(name = "Classofinsurance.findByProvince", query = "SELECT t FROM Township t WHERE t.province.id = :provinceId"),
		@NamedQuery(name = "ClassOfInsurance.findById", query = "SELECT c FROM ClassOfInsurance c WHERE c.id = :id") })
@Access(value = AccessType.FIELD)
public class ClassOfInsurance implements Serializable {
	private static final long serialVersionUID = 1L;
	@Transient
	private String id;
	@Transient
	private String prefix;
	private String name;
	private String description;
	@Version
	private int version;
	
	

	public ClassOfInsurance(){
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "CLASSOFINSURANCE_GEN")
	@Access(value = AccessType.PROPERTY)
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
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
		ClassOfInsurance other = (ClassOfInsurance) obj;
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
		return true;
	}
	

}
