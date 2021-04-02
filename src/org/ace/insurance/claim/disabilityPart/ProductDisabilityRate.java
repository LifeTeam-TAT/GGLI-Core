package org.ace.insurance.claim.disabilityPart;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PRODUCT_DISABILITYRATE)
@TableGenerator(name = "PRODUCT_DISABILITYRATE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PRODUCT_DISABILITYRATE_GEN", allocationSize = 10)
@NamedQuery(name = "ProductDisabilityRate.findRateById", query = "SELECT pdr.percentage FROM ProductDisabilityRate pdr WHERE pdr.id = :id")
@EntityListeners(IDInterceptor.class)
public class ProductDisabilityRate implements Serializable {

	private static final long serialVersionUID = 6584141230464902966L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PRODUCT_DISABILITYRATE_GEN")
	private String id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DISABILITYPARTID", referencedColumnName = "ID")
	private DisabilityPart disabilityPart;

	private double percentage;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public ProductDisabilityRate() {
		super();
	}

	public ProductDisabilityRate(String id, DisabilityPart disabilityPart, double percentage, CommonCreateAndUpateMarks commonCreateAndUpateMarks, int version) {
		super();
		this.id = id;
		this.disabilityPart = disabilityPart;
		this.percentage = percentage;
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DisabilityPart getDisabilityPart() {
		return disabilityPart;
	}

	public void setDisabilityPart(DisabilityPart disabilityPart) {
		this.disabilityPart = disabilityPart;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
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
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((disabilityPart == null) ? 0 : disabilityPart.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(percentage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		ProductDisabilityRate other = (ProductDisabilityRate) obj;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (disabilityPart == null) {
			if (other.disabilityPart != null)
				return false;
		} else if (!disabilityPart.equals(other.disabilityPart))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(percentage) != Double.doubleToLongBits(other.percentage))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
