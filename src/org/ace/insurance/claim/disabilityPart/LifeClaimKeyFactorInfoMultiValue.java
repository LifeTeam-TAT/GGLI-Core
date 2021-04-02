package org.ace.insurance.claim.disabilityPart;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.ace.insurance.product.KFRefValue;

@Embeddable
public class LifeClaimKeyFactorInfoMultiValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value;
	private String description;

	public LifeClaimKeyFactorInfoMultiValue() {
	}

	public LifeClaimKeyFactorInfoMultiValue(KFRefValue kfRefValue) {
		this.value = kfRefValue.getId();
		this.description = kfRefValue.getName();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		LifeClaimKeyFactorInfoMultiValue other = (LifeClaimKeyFactorInfoMultiValue) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
