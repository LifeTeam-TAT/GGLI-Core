/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.product;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.PRODUCT_RATEKEYFACTOR)
@TableGenerator(name = "PRODUCTPREMIUMRATEKEYFACTOR_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PRODUCTPREMIUMRATEKEYFACTOR_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "ProductPremiumRateKeyFactor.findAll", query = "SELECT p FROM ProductPremiumRateKeyFactor p "),
		@NamedQuery(name = "ProductPremiumRateKeyFactor.findById", query = "SELECT p FROM ProductPremiumRateKeyFactor p WHERE p.id = :id"),
		@NamedQuery(name = "ProductPremiumRateKeyFactor.deleteById", query = "DELETE FROM ProductPremiumRateKeyFactor p WHERE p.id = :id") })
@Access(value = AccessType.FIELD)
public class ProductPremiumRateKeyFactor implements Serializable {
	private static final long serialVersionUID = 1L;
	@Transient
	private String id;
	@Transient
	private String prefix;

	@Column(name = "VALUE")
	private String value;

	@Column(name = "REFERENCENAME")
	private String referenceName;

	@Column(name = "FROMVALUE")
	private double from;

	@Column(name = "TOVALUE")
	private double to;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KEYFACTORID", referencedColumnName = "ID")
	private KeyFactor keyFactor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTPREMIUMRATEID", referencedColumnName = "ID")
	private ProductPremiumRate productPremiumRate;

	@Version
	private int version;

	public ProductPremiumRateKeyFactor() {
	}

	public ProductPremiumRateKeyFactor(double from, double to, String value, String referenceName, KeyFactor keyFactor, ProductPremiumRate productPremiumRate) {
		this.from = from;
		this.to = to;
		this.value = value;
		this.referenceName = referenceName;
		this.keyFactor = keyFactor;
		this.productPremiumRate = productPremiumRate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PRODUCTPREMIUMRATEKEYFACTOR_GEN")
	@Access(value = AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public double getFrom() {
		return this.from;
	}

	public void setFrom(double from) {
		this.from = from;
	}

	public double getTo() {
		return this.to;
	}

	public void setTo(double to) {
		this.to = to;
	}

	public KeyFactor getKeyFactor() {
		return this.keyFactor;
	}

	public void setKeyFactor(KeyFactor keyFactor) {
		this.keyFactor = keyFactor;
	}

	public ProductPremiumRate getProductPremiumRate() {
		return this.productPremiumRate;
	}

	public void setProductPremiumRate(ProductPremiumRate productPremiumRate) {
		this.productPremiumRate = productPremiumRate;
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
		temp = Double.doubleToLongBits(from);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((referenceName == null) ? 0 : referenceName.hashCode());
		temp = Double.doubleToLongBits(to);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ProductPremiumRateKeyFactor other = (ProductPremiumRateKeyFactor) obj;
		if (Double.doubleToLongBits(from) != Double.doubleToLongBits(other.from))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (referenceName == null) {
			if (other.referenceName != null)
				return false;
		} else if (!referenceName.equals(other.referenceName))
			return false;
		if (Double.doubleToLongBits(to) != Double.doubleToLongBits(other.to))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}