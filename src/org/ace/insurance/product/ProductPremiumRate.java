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
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.PRODUCT_PREMIUMRATE)
@TableGenerator(name = "PRODUCTPREMIUMRATE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PRODUCTPREMIUMRATE_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "ProductPremiumRate.findAll", query = "SELECT p FROM ProductPremiumRate p "),
		@NamedQuery(name = "ProductPremiumRate.findById", query = "SELECT p FROM ProductPremiumRate p WHERE p.id = :id"),
		@NamedQuery(name = "ProductPremiumRate.findByProductId", query = "SELECT p FROM ProductPremiumRate p WHERE p.product.id = :productId"),
		@NamedQuery(name = "ProductPremiumRate.deleteById", query = "DELETE FROM ProductPremiumRate p WHERE p.id = :id") })
@Access(value = AccessType.FIELD)
public class ProductPremiumRate implements Serializable, Comparable<ProductPremiumRate> {
	private static final long serialVersionUID = 1L;
	@Transient
	private String id;
	@Transient
	private String prefix;
	private double premiumRate;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "productPremiumRate", orphanRemoval = true)
	private List<ProductPremiumRateKeyFactor> premiumRateKeyFactor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;
	
	@Enumerated(value = EnumType.STRING)
	private SumInsuredType sumInsuredType;

	@Version
	private int version;

	public ProductPremiumRate() {
	}

	public ProductPremiumRate(double premiumRate, Product product) {
		this.premiumRate = premiumRate;
		this.product = product;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PRODUCTPREMIUMRATE_GEN")
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

	public double getPremiumRate() {
		return this.premiumRate;
	}

	public void setPremiumRate(double premiumRate) {
		this.premiumRate = premiumRate;
	}

	public List<ProductPremiumRateKeyFactor> getPremiumRateKeyFactor() {
		return this.premiumRateKeyFactor;
	}

	public void setPremiumRateKeyFactor(List<ProductPremiumRateKeyFactor> premiumRateKeyFactor) {
		this.premiumRateKeyFactor = premiumRateKeyFactor;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	

	public SumInsuredType getSumInsuredType() {
		return sumInsuredType;
	}

	public void setSumInsuredType(SumInsuredType sumInsuredType) {
		this.sumInsuredType = sumInsuredType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		long temp;
		temp = Double.doubleToLongBits(premiumRate);
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
		ProductPremiumRate other = (ProductPremiumRate) obj;
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
		if (Double.doubleToLongBits(premiumRate) != Double.doubleToLongBits(other.premiumRate))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public int compareTo(ProductPremiumRate productPremiumRate) {
		if (this.premiumRate > productPremiumRate.getPremiumRate()) {
			return 1;
		} else {
			return -1;
		}
	}
}