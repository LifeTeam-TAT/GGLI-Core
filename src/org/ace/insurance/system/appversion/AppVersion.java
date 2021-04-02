package org.ace.insurance.system.appversion;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.APPVERSION)
@TableGenerator(name = "APPVERSION_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "APPVERSION_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "AppVersion.findAll", query = "SELECT v FROM AppVersion v ORDER BY v.appVersion DESC"),
		@NamedQuery(name = "AppVersion.findById", query = "SELECT v FROM AppVersion v WHERE v.id = :id ORDER BY v.appVersion DESC"),
		@NamedQuery(name = "AppVersion.findByTypeAndVersion", query = "SELECT v FROM AppVersion v WHERE v.type = :type AND v.appVersion = :appVersion ORDER BY v.appVersion DESC"),
		@NamedQuery(name = "AppVersion.findLatestVersionByType", query = "SELECT v FROM AppVersion v WHERE v.type = :type AND v.appVersion = :appVersion ORDER BY v.appVersion DESC") })
@EntityListeners(IDInterceptor.class)
public class AppVersion implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "APPVERSION_GEN")
	private String id;

	private double appVersion;

	@Enumerated(EnumType.STRING)
	private MobileType type;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(double appVersion) {
		this.appVersion = appVersion;
	}

	public MobileType getType() {
		return type;
	}

	public void setType(MobileType type) {
		this.type = type;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
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
		long temp;
		temp = Double.doubleToLongBits(appVersion);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AppVersion other = (AppVersion) obj;
		if (Double.doubleToLongBits(appVersion) != Double.doubleToLongBits(other.appVersion))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type != other.type)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
