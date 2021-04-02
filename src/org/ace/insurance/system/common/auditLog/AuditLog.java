package org.ace.insurance.system.common.auditLog;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
import org.ace.insurance.system.common.organization.Organization;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.AUDITLOG)
@TableGenerator(name = "AUDITLOG_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "AUDITLOG_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "AuditLog.findAll", query = "SELECT a FROM AuditLog a ORDER BY a.excelFileId ASC"),
@NamedQuery(name = "AuditLog.findById", query = "SELECT a FROM AuditLog a WHERE a.id = :id") })
@Access(value = AccessType.FIELD)
public class AuditLog {
	
	@Transient
	private String id;
	
	@Transient
	private String prefix;
	
	private String excelFileId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;
	
	@Version
	private int version;
	
	public AuditLog() {
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "AUDITLOG_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, this.getPrefix(), 5);
		}
	}

	public String getExcelFileId() {
		return excelFileId;
	}

	public void setExcelFileId(String excelFileId) {
		this.excelFileId = excelFileId;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
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
	
	
}
