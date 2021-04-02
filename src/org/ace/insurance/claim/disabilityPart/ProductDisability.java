package org.ace.insurance.claim.disabilityPart;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.product.Product;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PRODUCTDISABILITY)
@TableGenerator(name = "PRODUCTDISABILITY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PRODUCTDISABILITY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "ProductDisability.findAll", query = "SELECT p FROM ProductDisability p"),
		@NamedQuery(name = "ProductDisability.findDisabilityPartByProductId", query = "SELECT dp FROM ProductDisability p LEFT JOIN p.disabilityRateList r LEFT JOIN r.disabilityPart dp WHERE p.product.id = :productId"),
		@NamedQuery(name = "ProductDisability.findDisabilityRateByProductId", query = "SELECT r FROM ProductDisability p LEFT JOIN p.disabilityRateList r WHERE p.product.id = :productId") })

@EntityListeners(IDInterceptor.class)
public class ProductDisability implements Serializable {

	private static final long serialVersionUID = 1452762688141244175L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PRODUCTDISABILITY_GEN")
	private String id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PRODUCTDISABILITYID", referencedColumnName = "ID")
	private List<ProductDisabilityRate> disabilityRateList;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public ProductDisability() {
		super();
	}

	public ProductDisability(String id, Product product, List<ProductDisabilityRate> disabilityRateList, CommonCreateAndUpateMarks commonCreateAndUpateMarks, int version) {
		super();
		this.id = id;
		this.product = product;
		this.disabilityRateList = disabilityRateList;
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<ProductDisabilityRate> getDisabilityRateList() {
		return disabilityRateList;
	}

	public void setDisabilityRateList(List<ProductDisabilityRate> disabilityRateList) {
		this.disabilityRateList = disabilityRateList;
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
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
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
		ProductDisability other = (ProductDisability) obj;
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
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
