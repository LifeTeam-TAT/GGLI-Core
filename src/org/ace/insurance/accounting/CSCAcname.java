package org.ace.insurance.accounting;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;
import org.ace.insurance.payment.enums.DoubleEntry;

@Entity
@Table(name = TableName.TLF_CONVERSION_FORMAT_ACNAME)
public class CSCAcname {
	@Id
	private String id;

	private String acname;

	@Enumerated(EnumType.STRING)
	private DoubleEntry status;

	@Enumerated(EnumType.STRING)
	private CalculationType calculationType;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "ACNAME_ID", referencedColumnName = "ID")
	private List<PairedKey> pairedKeys;

	@Column(name = "FORCE_TRAN")
	private boolean forceTran;

	@Enumerated(EnumType.STRING)
	private GLSign glSign;

	public CSCAcname() {
	}

	public CSCAcname(CSCAcname acname) {
		this.acname = acname.getAcname();
		this.status = acname.getStatus();
		this.calculationType = acname.getCalculationType();
		this.pairedKeys = acname.getPairedKeys();
		this.forceTran = acname.isForceTran();
		this.glSign = acname.getGlSign();
	}

	public CSCAcname(String id, String acname, DoubleEntry status, CalculationType calculationType, List<PairedKey> pairedKeys, boolean forceTran, GLSign glSign) {
		super();
		this.id = id;
		this.acname = acname;
		this.status = status;
		this.calculationType = calculationType;
		this.pairedKeys = pairedKeys;
		this.forceTran = forceTran;
		this.glSign = glSign;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAcname() {
		return acname;
	}

	public void setAcname(String acname) {
		this.acname = acname;
	}

	public DoubleEntry getStatus() {
		return status;
	}

	public void setStatus(DoubleEntry status) {
		this.status = status;
	}

	public CalculationType getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(CalculationType calculationType) {
		this.calculationType = calculationType;
	}

	public List<PairedKey> getPairedKeys() {
		return pairedKeys;
	}

	public void setPairedKeys(List<PairedKey> pairedKeys) {
		this.pairedKeys = pairedKeys;
	}

	public boolean isForceTran() {
		return forceTran;
	}

	public void setForceTran(boolean forceTran) {
		this.forceTran = forceTran;
	}

	public GLSign getGlSign() {
		return glSign;
	}

	public void setGlSign(GLSign glSign) {
		this.glSign = glSign;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acname == null) ? 0 : acname.hashCode());
		result = prime * result + ((calculationType == null) ? 0 : calculationType.hashCode());
		result = prime * result + (forceTran ? 1231 : 1237);
		result = prime * result + ((glSign == null) ? 0 : glSign.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((pairedKeys == null) ? 0 : pairedKeys.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		CSCAcname other = (CSCAcname) obj;
		if (acname == null) {
			if (other.acname != null)
				return false;
		} else if (!acname.equals(other.acname))
			return false;
		if (calculationType != other.calculationType)
			return false;
		if (forceTran != other.forceTran)
			return false;
		if (glSign != other.glSign)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (pairedKeys == null) {
			if (other.pairedKeys != null)
				return false;
		} else if (!pairedKeys.equals(other.pairedKeys))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

}
