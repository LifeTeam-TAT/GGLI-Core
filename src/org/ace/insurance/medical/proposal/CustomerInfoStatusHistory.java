package org.ace.insurance.medical.proposal;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.CustomerStatus;
import org.ace.insurance.common.TableName;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.CUSTOMERINFOSTATUS_HISTORY)
@TableGenerator(name = "CUSTOMERSTATUS_HISTORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "CUSTOMERSTATUS_HISTORY_GEN", allocationSize = 10)
@Access(value = AccessType.FIELD)
@EntityListeners(IDInterceptor.class)
public class CustomerInfoStatusHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "CUSTOMERSTATUS_HISTORY_GEN")
	private String id;

	@Enumerated(EnumType.STRING)
	private CustomerStatus statusName;

	@Version
	private int version;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	public CustomerInfoStatusHistory() {

	}

	public CustomerInfoStatusHistory(CustomerInfoStatus customerinfostatus) {
		// this.id = customerinfostatus.getId();
		this.statusName = customerinfostatus.getStatusName();
		this.commonCreateAndUpateMarks = customerinfostatus.getCommonCreateAndUpateMarks();
	}

	public CustomerInfoStatusHistory(String id, CustomerStatus statusName, int version) {
		this.id = id;
		this.statusName = statusName;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public CustomerStatus getStatusName() {
		return statusName;
	}

	public void setStatusName(CustomerStatus statusName) {
		this.statusName = statusName;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((statusName == null) ? 0 : statusName.hashCode());
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
		CustomerInfoStatusHistory other = (CustomerInfoStatusHistory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (statusName != other.statusName)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
