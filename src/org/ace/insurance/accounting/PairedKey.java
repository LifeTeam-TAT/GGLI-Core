package org.ace.insurance.accounting;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;

@Entity
@Table(name = TableName.TLF_CONVERSION_FORMAT_PAIRED_KEY)
public class PairedKey implements Serializable {
	private static final long serialVersionUID = 5942752389043036489L;

	@Id
	private String id;

	private String batchTranCode;
	private String sacscode;
	private String sacstype;
	@Column(name = "IS_SAME_POLICY_NO")
	private boolean isSamePolicyNo;

	@Column(name = "CALCULATION_METHOD")
	@Enumerated(EnumType.STRING)
	private CalculationMethod calculationMethod;

	private Boolean optional;

	public PairedKey() {
	}

	public String getBatchTranCode() {
		return batchTranCode;
	}

	public void setBatchTranCode(String batchTranCode) {
		this.batchTranCode = batchTranCode;
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

	public boolean isSamePolicyNo() {
		return isSamePolicyNo;
	}

	public void setSamePolicyNo(boolean isSamePolicyNo) {
		this.isSamePolicyNo = isSamePolicyNo;
	}

	public CalculationMethod getCalculationMethod() {
		return calculationMethod;
	}

	public void setCalculationMethod(CalculationMethod calculationMethod) {
		this.calculationMethod = calculationMethod;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchTranCode == null) ? 0 : batchTranCode.hashCode());
		result = prime * result + ((calculationMethod == null) ? 0 : calculationMethod.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isSamePolicyNo ? 1231 : 1237);
		result = prime * result + ((optional == null) ? 0 : optional.hashCode());
		result = prime * result + ((sacscode == null) ? 0 : sacscode.hashCode());
		result = prime * result + ((sacstype == null) ? 0 : sacstype.hashCode());
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
		PairedKey other = (PairedKey) obj;
		if (batchTranCode == null) {
			if (other.batchTranCode != null)
				return false;
		} else if (!batchTranCode.equals(other.batchTranCode))
			return false;
		if (calculationMethod != other.calculationMethod)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isSamePolicyNo != other.isSamePolicyNo)
			return false;
		if (optional == null) {
			if (other.optional != null)
				return false;
		} else if (!optional.equals(other.optional))
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
		return true;
	}

}
