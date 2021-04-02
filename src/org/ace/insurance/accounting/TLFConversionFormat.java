package org.ace.insurance.accounting;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.TLF_CONVERSION_FORMAT)
@EntityListeners(IDInterceptor.class)
public class TLFConversionFormat implements Serializable {
	private static final long serialVersionUID = 4225315543314667238L;

	@Id
	private String id;

	private String batctrcde;
	private String sacscode;
	private String sacstype;
	private String cnttype;
	@Enumerated(EnumType.STRING)
	private CoReStatus coReStatus;

	private boolean isSkip;

	// @Enumerated(EnumType.STRING)
	// private ConversionType conversionType;

	@Enumerated(EnumType.STRING)
	private GLSign glSign;

	@Column(name = "IS_INTERBRANCH_CHECK")
	private boolean isInterbranchCheck;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "FORMAT_ID", referencedColumnName = "ID")
	private List<CSCAcname> acnames;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "FORMAT_ID", referencedColumnName = "ID")
	private List<PairedKey> pairedKeys;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public TLFConversionFormat() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatctrcde() {
		return batctrcde;
	}

	public void setBatctrcde(String batctrcde) {
		this.batctrcde = batctrcde;
	}

	public String getSacscode() {
		return sacscode;
	}

	public void setSacscode(String sacscode) {
		this.sacscode = sacscode;
	}

	public String getSacstype() {
		return sacstype;
	}

	public void setSacstype(String sacstype) {
		this.sacstype = sacstype;
	}

	public String getCnttype() {
		return cnttype;
	}

	public void setCnttype(String cnttype) {
		this.cnttype = cnttype;
	}

	public CoReStatus getCoReStatus() {
		return coReStatus;
	}

	public void setCoReStatus(CoReStatus coReStatus) {
		this.coReStatus = coReStatus;
	}

	public boolean isSkip() {
		return isSkip;
	}

	public void setSkip(boolean isSkip) {
		this.isSkip = isSkip;
	}

	// public ConversionType getConversionType() {
	// return conversionType;
	// }
	//
	// public void setConversionType(ConversionType conversionType) {
	// this.conversionType = conversionType;
	// }

	public GLSign getGlSign() {
		return glSign;
	}

	public void setGlSign(GLSign glSign) {
		this.glSign = glSign;
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

	public List<CSCAcname> getAcnames() {
		return acnames;
	}

	public void setAcnames(List<CSCAcname> acnames) {
		this.acnames = acnames;
	}

	public List<PairedKey> getPairedKeys() {
		return pairedKeys;
	}

	public void setPairedKeys(List<PairedKey> pairedKeys) {
		this.pairedKeys = pairedKeys;
	}

	public boolean isInterbranchCheck() {
		return isInterbranchCheck;
	}

	public void setInterbranchCheck(boolean isInterbranchCheck) {
		this.isInterbranchCheck = isInterbranchCheck;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acnames == null) ? 0 : acnames.hashCode());
		result = prime * result + ((batctrcde == null) ? 0 : batctrcde.hashCode());
		result = prime * result + ((cnttype == null) ? 0 : cnttype.hashCode());
		result = prime * result + ((coReStatus == null) ? 0 : coReStatus.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((glSign == null) ? 0 : glSign.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isInterbranchCheck ? 1231 : 1237);
		result = prime * result + (isSkip ? 1231 : 1237);
		result = prime * result + ((pairedKeys == null) ? 0 : pairedKeys.hashCode());
		result = prime * result + ((sacscode == null) ? 0 : sacscode.hashCode());
		result = prime * result + ((sacstype == null) ? 0 : sacstype.hashCode());
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
		TLFConversionFormat other = (TLFConversionFormat) obj;
		if (acnames == null) {
			if (other.acnames != null)
				return false;
		} else if (!acnames.equals(other.acnames))
			return false;
		if (batctrcde == null) {
			if (other.batctrcde != null)
				return false;
		} else if (!batctrcde.equals(other.batctrcde))
			return false;
		if (cnttype == null) {
			if (other.cnttype != null)
				return false;
		} else if (!cnttype.equals(other.cnttype))
			return false;
		if (coReStatus != other.coReStatus)
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (glSign != other.glSign)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isInterbranchCheck != other.isInterbranchCheck)
			return false;
		if (isSkip != other.isSkip)
			return false;
		if (pairedKeys == null) {
			if (other.pairedKeys != null)
				return false;
		} else if (!pairedKeys.equals(other.pairedKeys))
			return false;
		if (sacscode == null) {
			if (other.sacscode != null)
				return false;
		} else if (!sacscode.equals(other.sacscode))
			return false;
		if (sacstype == null) {
			if (other.sacstype != null)
				return false;
		} else if (!sacstype.equals(other.sacstype))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
