package org.ace.insurance.system.common.bank;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = TableName.COA)
@TableGenerator(name = "COA_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "COA_GEN", allocationSize = 10)
@Access(value = AccessType.FIELD)
public class Coa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name = "ACCODE")
	private String acode;

	// @Column(name = "DCODE")
	// private String dcode;

	@Column(name = "ACNAME")
	private String acName;

	// @Column(name = "ACCODETYPE")
	private char acType;

	@Temporal(TemporalType.DATE)
	private Date pDate;

	@Column(name = "IBSBACODE")
	private String iBSBAcode;

	public Coa() {
	}

	public Coa(String acode, char acType) {
		this.acode = acode;
		this.acType = acType;
	}

	public String getAcode() {
		return this.acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

	// public String getDcode() {
	// return dcode;
	// }

	// public void setDcode(String dcode) {
	// this.dcode = dcode;
	// }

	public String getAcName() {
		return acName;
	}

	public void setAcName(String acName) {
		this.acName = acName;
	}

	public char getAcType() {
		return this.acType;
	}

	public void setAcType(char acType) {
		this.acType = acType;
	}

	public Date getpDate() {
		return pDate;
	}

	public void setpDate(Date pDate) {
		this.pDate = pDate;
	}

	public String getiBSBAcode() {
		return iBSBAcode;
	}

	public void setiBSBAcode(String iBSBAcode) {
		this.iBSBAcode = iBSBAcode;
	}

	@Override
	public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(this, object);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
